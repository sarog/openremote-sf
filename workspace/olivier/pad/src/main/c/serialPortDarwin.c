#include <stdio.h>
#include <stddef.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>
#include <sys/ioctl.h>
#include <signal.h>

#include "apr_hash.h"

#include "codes.h"
#include "serialPortDarwin.h"

#define RCV_BUF_SIZE 256

static const trsTbl_t speedTrsTbl = { "speed", 5, { { "9600", B9600 }, {
		"19200", B19200 }, { "38400", B38400 }, { "57600", B57600 }, { "115200",
		B115200 } } };
static const trsTbl_t nbBitsTrsTbl = { "nbBits", 2,
		{ { "7", CS7 }, { "8", CS8 } } };
#define PARITY_NO 1
#define PARITY_EVEN 2
#define PARITY_ODD 3
static const trsTbl_t parityTrsTbl = { "parity", 3, { { "no", PARITY_NO }, {
		"even", PARITY_EVEN }, { "odd", PARITY_ODD } } };

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

tcflag_t lsGetCfg(apr_hash_t *cfg, const trsTbl_t *tbl, tcflag_t dft) {
	char *s = apr_hash_get(cfg, tbl->key, APR_HASH_KEY_STRING);
	tcflag_t out = dft;
	if (s != NULL) {
		int i;
		for (i = 0; i < tbl->nbValues; ++i) {
			if (strcmp(tbl->values[i].cfgStr, s) == 0) {
				out = tbl->values[i].cfgVal;
				break;
			}
		}
	}
	return out;
}

int lsConfigure(portContext_t *portContext, apr_hash_t *cfg) {
	// Save current port settings
	tcgetattr(portContext->fd, &portContext->oldtio);

	// Prepare new settings
	tcflag_t parity = lsGetCfg(cfg, &parityTrsTbl, PARITY_EVEN);
	portContext->newtio.c_cflag = lsGetCfg(cfg, &nbBitsTrsTbl, CS8) | CLOCAL
			| lsGetCfg(cfg, &speedTrsTbl, B19200);
	if (parity != PARITY_NO) {
		portContext->newtio.c_cflag |= PARENB;
	}
	if (parity == PARITY_ODD) {
		portContext->newtio.c_cflag |= PARODD;
	}
	if (parity != PARITY_ODD) {
		portContext->newtio.c_iflag = INPCK;
	}
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

int physicalLock(apr_pool_t *pool, char *portId, portContext_t **portContext,
		apr_hash_t *cfg, portReceive_t portReceiveCb) {
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

	CHECK(lsConfigure(*portContext, cfg));
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
