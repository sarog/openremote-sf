#include <stdio.h>
#include <stddef.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>
#include <linux/serial.h>
#include <sys/ioctl.h>
#include <asm-generic/ioctls.h>  // To please Eclipse
#include <signal.h>
#include "codes.h"
#include "linuxSerialPort.h"

#define RCV_BUF_SIZE 256

void lsReceiveSignal(int sig) {
	printf("Signal %d!\n", sig);
}

void *lsRead(void *data) {
	char buf[RCV_BUF_SIZE];
	portContext_t *portContext = (portContext_t *) data;

	sigset_t signalMask;
	sigemptyset(&signalMask);
	sigaddset(&signalMask, SIGALRM);
	int ret = pthread_sigmask(SIG_UNBLOCK, &signalMask, NULL);
	struct sigaction act;
	act.sa_handler = lsReceiveSignal;
	act.sa_flags = 0;
	sigaction(SIGALRM, &act, NULL);
	// init cancellation state for the thread
//  pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL);      /* enabled */
//  pthread_setcanceltype(PTHREAD_CANCEL_ASYNCHRONOUS, NULL); /* cancel immediately */

// start read loop and exit on EOF or error
	int res = 1;

	// notify the tread started
//  pthread_mutex_lock(&start_sync_mutex);
//  pthread_cond_signal(&start_sync_cond);
//  pthread_mutex_unlock(&start_sync_mutex);

	while (res > 0) {
		res = read(portContext->fd, buf, RCV_BUF_SIZE); /* read RCV_BUF_SIZE bytes at most   */
		if (res > 0) {
			int r = portContext->portReceiveCb(portContext->portId, buf, res);
		}
	}

	pthread_exit(R_SUCCESS);
	return NULL;
}

int lsConfigure(portContext_t *portContext) {
	struct serial_struct serinfo;

	// Force low latency on serial ports
	if (ioctl(portContext->fd, TIOCGSERIAL, &serinfo) < 0) {
		return R_CONFIGURE_ERROR;
	}
	serinfo.flags |= ASYNC_LOW_LATENCY;
	if (ioctl(portContext->fd, TIOCSSERIAL, &serinfo) < 0) {
		return R_CONFIGURE_ERROR;
	}

	// Save current port settings
	tcgetattr(portContext->fd, &portContext->oldtio);

	// Prepare new settings
	portContext->newtio.c_cflag = CS8 | // control modes : 8bits,
			CLOCAL | // ignore modem status lines, enable
			CREAD | PARENB | // receiver, enable parity
			B19200;
	portContext->newtio.c_iflag = INPCK | IGNPAR; // input modes : parity check & ignore parity-error bytes
	portContext->newtio.c_lflag = 0 & ~(ICANON | ECHO | ECHOE | ISIG); // Raw mode
	portContext->newtio.c_oflag = 0; // output modes
	portContext->newtio.c_cc[VMIN] = 1;
	portContext->newtio.c_cc[VTIME] = 0; // read timeout = 0 * 0.1s
	tcsetattr(portContext->fd, TCSANOW, &portContext->newtio);

	return R_SUCCESS;
}

int lsCreateReadThread(apr_pool_t *pool, portContext_t *portContext) {
	// start the read thread
//	pthread_mutex_lock(&start_sync_mutex);
	sigset_t signalMask;
	sigemptyset(&signalMask);
	sigaddset(&signalMask, SIGALRM);
	int ret = pthread_sigmask(SIG_BLOCK, &signalMask, NULL);
	pthread_create(&portContext->readThread, NULL, lsRead, portContext);

	// wait for the read thread to start.
//	pthread_cond_wait(&start_sync_cond, &start_sync_mutex);
//	pthread_mutex_unlock(&start_sync_mutex);

//	pthread_cond_destroy(&start_sync_cond);
//	pthread_mutex_destroy(&start_sync_mutex);
	return R_SUCCESS;
}

int lsInterruptReadThread(portContext_t *portContext) {
	apr_status_t r;
	pthread_kill(portContext->readThread, SIGALRM);
	pthread_join(portContext->readThread, NULL);
	return R_SUCCESS;
}

int lsUnconfigure(portContext_t *portContext) {
	// Restore old port settings
	tcsetattr(portContext->fd, TCSAFLUSH, &portContext->oldtio);
	return R_SUCCESS;
}

int physicalLock(apr_pool_t *pool, char *portId, portContext_t **portContext, portReceive_t portReceiveCb) {
	// Allocate serial port data memory
	*portContext = apr_palloc(pool, sizeof(portContext_t));
	(*portContext)->portReceiveCb = portReceiveCb;
	(*portContext)->portId = apr_palloc(pool, strlen(portId) + 1);
	strcpy((*portContext)->portId, portId);

	// Open serial port
	(*portContext)->fd = open(portId, O_RDWR | O_NOCTTY);
	if ((*portContext)->fd < 0) {
		return R_PORT_ERROR;
	}

	CHECK(lsConfigure(*portContext));
	CHECK(lsCreateReadThread(pool, *portContext));

	return R_SUCCESS;
}

physicalLock_t physicalLockCb = physicalLock;

int physicalUnlock(apr_pool_t *pool, char *portId, portContext_t **portContext) {
	// Close serial port
	//fcntl((*portContext)->fd, F_SETFL, FNDELAY);
	close((*portContext)->fd);

	// Interrupt read thread
	lsInterruptReadThread(*portContext);

	// Restore port configuration
	lsUnconfigure(*portContext);
	return R_SUCCESS;
}

physicalUnlock_t physicalUnlockCb = physicalUnlock;

int physicalSend(portContext_t *portContext, char *data, int len) {
	int r = write(portContext->fd, data, len); // TODO write in a loop
	if (r == -1)
		return R_SEND_ERROR;
	return R_SUCCESS;
}

physicalSend_t physicalSendCb = physicalSend;
