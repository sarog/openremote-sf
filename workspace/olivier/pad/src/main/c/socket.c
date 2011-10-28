/**
 *
 */
#ifdef HAVE_CONFIG_H
#include <config.h>
#endif

#include <stdio.h>
#include <assert.h>
#include <sys/socket.h>  // To please Eclipse
#include "apr_general.h"
#include "apr_file_io.h"
#include "apr_strings.h"
#include "apr_network_io.h"
#include "apr_poll.h"

#include "codes.h"
#include "server.h"
#include "client.h"

// Default listen port number
#define DEF_LISTEN_PORT		7876

// Default socket backlog number. SOMAXCONN is a system default value.
#define DEF_SOCKET_BACKLOG	SOMAXCONN

// TODO
#define DEF_POLLSET_NUM		32

// Default socket timeout
#define DEF_POLL_TIMEOUT	(APR_USEC_PER_SEC * 30)

// Default buffer size
typedef struct _serviceContext_t serviceContext_t;

// Network event callback function type
typedef int (*socket_callback_t)(serviceContext_t *context, apr_pollset_t *pollset, apr_socket_t *sock);

// Service context
struct _serviceContext_t {
	enum {
		RECV_MESSAGE, SEND_MESSAGE,
	} status;
	socket_callback_t cbFunc;
	apr_pool_t *socketPool;
	apr_pool_t *serverTxPool;
	apr_pool_t *clientTxPool;
	transaction_t *clientTx;
	transaction_t *serverTx;
};

static serviceContext_t *context;
static apr_socket_t *acceptedSocket;

static apr_socket_t* createListenSocket(apr_pool_t *listenPool);
static int doAccept(apr_pollset_t *pollset, apr_socket_t *lsock, apr_pool_t *socketPool);
int receiveMessage(serviceContext_t *context, apr_pollset_t *pollset, apr_socket_t *sock);
static int sendResponse(serviceContext_t *context, apr_pollset_t *pollset, apr_socket_t *sock);
int receive(char *portId, char *buf, int len);

/**
 *
 */
int runServer() {
	apr_status_t rv;
	apr_pool_t *listenPool, *socketPool;
	apr_socket_t *lsock;/* listening socket */
	apr_pollset_t *pollset;
	apr_int32_t num;
	const apr_pollfd_t *descriptors;

	apr_pool_create(&listenPool, NULL);
	apr_pool_create(&socketPool, NULL);

	lsock = createListenSocket(listenPool);

	apr_pollset_create(&pollset, DEF_POLLSET_NUM, listenPool, 0);
	{
		// Monitor with pollset the listen socket can read without blocking
		apr_pollfd_t pfd = { listenPool, APR_POLL_SOCKET, APR_POLLIN, 0, { NULL }, NULL };
		pfd.desc.s = lsock;
		apr_pollset_add(pollset, &pfd);
	}

	while (1) {
		rv = apr_pollset_poll(pollset, DEF_POLL_TIMEOUT, &num, &descriptors);
		if (rv == APR_SUCCESS) {
			int i;
			assert(num > 0);
			/* scan the active sockets */
			for (i = 0; i < num; i++) {
				if (descriptors[i].desc.s == lsock) {
					/* the listen socket is readable. that indicates we accepted a new connection */
					doAccept(pollset, lsock, socketPool);
				} else {
					serviceContext_t *context = descriptors[i].client_data;
					socket_callback_t cbFunc = context->cbFunc;
					cbFunc(context, pollset, descriptors[i].desc.s);
				}
			}
		}
	}

	apr_pool_destroy(socketPool);
	apr_pool_destroy(listenPool);
	return R_SUCCESS;
}

static apr_socket_t* createListenSocket(apr_pool_t *listenPool) {
	apr_status_t rv;
	apr_socket_t *s;
	apr_sockaddr_t *sa;

	rv = apr_sockaddr_info_get(&sa, NULL, APR_INET, DEF_LISTEN_PORT, 0, listenPool);
	if (rv != APR_SUCCESS) {
		goto error;
	}

	rv = apr_socket_create(&s, sa->family, SOCK_STREAM, APR_PROTO_TCP, listenPool);
	if (rv != APR_SUCCESS) {
		goto error;
	}

	/* non-blocking socket */
	apr_socket_opt_set(s, APR_SO_NONBLOCK, 1);
	apr_socket_timeout_set(s, 0);
	apr_socket_opt_set(s, APR_SO_REUSEADDR, 1);/* this is useful for a server(socket listening) process */

	rv = apr_socket_bind(s, sa);
	if (rv != APR_SUCCESS) {
		goto error;
	}
	rv = apr_socket_listen(s, DEF_SOCKET_BACKLOG);
	if (rv != APR_SUCCESS) {
		goto error;
	}

	return s;

	error: return NULL;
}

static int doAccept(apr_pollset_t *pollset, apr_socket_t *lsock, apr_pool_t *socketPool) {
	apr_status_t rv;

	rv = apr_socket_accept(&acceptedSocket, lsock, socketPool);
	if (rv == APR_SUCCESS) {
		// Create service context
		context = apr_palloc(socketPool, sizeof(serviceContext_t));
		apr_pollfd_t descriptor = { socketPool, APR_POLL_SOCKET, APR_POLLIN, 0, { NULL }, context };
		descriptor.desc.s = acceptedSocket;
		context->status = RECV_MESSAGE;
		context->cbFunc = receiveMessage;
		context->socketPool = socketPool;

		// Create new transaction pools
		apr_pool_create(&context->serverTxPool, context->socketPool);
		apr_pool_create(&context->clientTxPool, context->socketPool);
		context->serverTx = NULL;
		context->clientTx = NULL;

		/* non-blocking socket. We can't expect that @ns inherits non-blocking mode from @lsock */
		apr_socket_opt_set(acceptedSocket, APR_SO_NONBLOCK, 1);
		apr_socket_timeout_set(acceptedSocket, 0);

		/* monitor accepted socket */
		apr_pollset_add(pollset, &descriptor);
	}
	return TRUE;
}

void createTransaction(apr_pool_t *pool, transaction_t **transaction) {
	if (*transaction == NULL) {
		*transaction = apr_palloc(pool, sizeof(transaction_t));
		(*transaction)->request = NULL;
		(*transaction)->response = NULL;
		(*transaction)->status = WAITING_FOR_REQUEST;
		(*transaction)->portReceiveCb = receive;
	}
}

void clearTransaction(apr_pool_t *pool, transaction_t **transaction) {
	apr_pool_clear(pool);
	*transaction = NULL;
}

void closeSocket(apr_socket_t *sock, serviceContext_t *context) {
	apr_socket_close(sock);
	apr_pool_clear(context->socketPool);
	context = NULL;
	acceptedSocket = NULL;
}

static int receiveRequest(serviceContext_t *context, apr_pollset_t *pollset, apr_socket_t *sock, char code) {
	// Create a new server transaction
	// TODO what happens if a transaction was already created?
	createTransaction(context->serverTxPool, &context->serverTx);

	int r = operateRequest(sock, context->serverTx, context->serverTxPool, code);
	if (r != R_SUCCESS) {
		closeSocket(sock, context);
		return r;
	}

	// Change context status from receive to send
	apr_pollfd_t descriptor = { context->socketPool, APR_POLL_SOCKET, APR_POLLIN, 0, { NULL }, context };
	descriptor.desc.s = sock;
	apr_pollset_remove(pollset, &descriptor);
	descriptor.reqevents = APR_POLLOUT;
	apr_pollset_add(pollset, &descriptor);
	context->status = SEND_MESSAGE;
	context->cbFunc = sendResponse;
	return R_SUCCESS;
}

static int receiveResponse(serviceContext_t *context, apr_pollset_t *pollset, apr_socket_t *sock, char code) {
	// TODO
	return R_SUCCESS;
}

int receiveMessage(serviceContext_t *context, apr_pollset_t *pollset, apr_socket_t *sock) {
	char code;
	messageTxType_t type;

	// Read message header
	int r = checkInputMessage(sock, &code, &type);
	if (r != R_SUCCESS) {
		closeSocket(sock, context);
		return r;
	}

	// Handle rest of message according to its transaction type
	if (type == SERVER) {
		return receiveRequest(context, pollset, sock, code);
	} else {
		return receiveResponse(context, pollset, sock, code);
	}
}

/**
 * Send a response to the client.
 */
static int sendResponse(serviceContext_t *context, apr_pollset_t *pollset, apr_socket_t *sock) {
	writeMessage(sock, context->serverTx->response);

	// Clear all memory related to request and response
	clearTransaction(context->serverTxPool, &context->serverTx);

	// Change context status from write to read
	apr_pollfd_t descriptor = { context->socketPool, APR_POLL_SOCKET, APR_POLLOUT, 0, { NULL }, context };
	descriptor.desc.s = sock;
	apr_pollset_remove(pollset, &descriptor);
	descriptor.reqevents = APR_POLLIN;
	apr_pollset_add(pollset, &descriptor);
	context->status = RECV_MESSAGE;
	context->cbFunc = receiveMessage;
	return R_SUCCESS;
}

int receive(char *portId, char *buf, int len) {
	if (context->clientTx != NULL)
		return R_TX_RUNNING;
	createTransaction(context->clientTxPool, &context->clientTx);

	return operatePortData(acceptedSocket, context->clientTx, context->clientTxPool, portId, buf, len);
}
