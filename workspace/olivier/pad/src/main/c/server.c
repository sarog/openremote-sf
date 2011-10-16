/**
 *
 */
#ifdef HAVE_CONFIG_H
#include <config.h>
#endif

#include <stdio.h>
#include <assert.h>

#include "apr_general.h"
#include "apr_file_io.h"
#include "apr_strings.h"
#include "apr_network_io.h"
#include "apr_poll.h"

#include "codes.h"
#include "dispatch.h"

/* default listen port number */
#define DEF_LISTEN_PORT		7876

/* default socket backlog number. SOMAXCONN is a system default value */
#define DEF_SOCKET_BACKLOG	SOMAXCONN

#define DEF_POLLSET_NUM		32

/* default socket timeout */
#define DEF_POLL_TIMEOUT	(APR_USEC_PER_SEC * 30)

/* default buffer size */
typedef struct _serviceContext_t serviceContext_t;

/**
 * network event callback function type
 */
typedef int (*socket_callback_t)(serviceContext_t *context, apr_pollset_t *pollset, apr_socket_t *sock);

/**
 * network server context 
 */
struct _serviceContext_t {
	enum {
		SERV_RECV_REQUEST, SERV_SEND_RESPONSE,
	} status;

	socket_callback_t cbFunc;
	apr_pool_t *socketPool;
	apr_pool_t *transactionPool;

	message_t *request;
	message_t *response;
};

static apr_socket_t* createListenSocket(apr_pool_t *mp);
static int doAccept(apr_pollset_t *pollset, apr_socket_t *lsock, apr_pool_t *pool);

static int receiveRequestCallback(serviceContext_t *context, apr_pollset_t *pollset, apr_socket_t *sock);
static int sendResponseCallback(serviceContext_t *context, apr_pollset_t *pollset, apr_socket_t *sock);

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
	assert(lsock);

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

	return 0;
}

static apr_socket_t* createListenSocket(apr_pool_t *mp) {
	apr_status_t rv;
	apr_socket_t *s;
	apr_sockaddr_t *sa;

	rv = apr_sockaddr_info_get(&sa, NULL, APR_INET, DEF_LISTEN_PORT, 0, mp);
	if (rv != APR_SUCCESS) {
		goto error;
	}

	rv = apr_socket_create(&s, sa->family, SOCK_STREAM, APR_PROTO_TCP, mp);
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

static int doAccept(apr_pollset_t *pollset, apr_socket_t *lsock, apr_pool_t *pool) {
	apr_socket_t *ns;/* accepted socket */
	apr_status_t rv;

	rv = apr_socket_accept(&ns, lsock, pool);
	if (rv == APR_SUCCESS) {
		// Create service context
		serviceContext_t *context = apr_palloc(pool, sizeof(serviceContext_t));
		apr_pollfd_t descriptor = { pool, APR_POLL_SOCKET, APR_POLLIN, 0, { NULL }, context };
		descriptor.desc.s = ns;
		context->status = SERV_RECV_REQUEST;
		context->cbFunc = receiveRequestCallback;
		context->socketPool = pool;
		apr_pool_create(&context->transactionPool, NULL);

		/* non-blocking socket. We can't expect that @ns inherits non-blocking mode from @lsock */
		apr_socket_opt_set(ns, APR_SO_NONBLOCK, 1);
		apr_socket_timeout_set(ns, 0);

		/* monitor accepted socket */
		apr_pollset_add(pollset, &descriptor);
	}
	return TRUE;
}

void clearTransaction(serviceContext_t *context) {
	apr_pool_clear(context->transactionPool);
}

void clearSocket(serviceContext_t *context) {
	apr_pool_clear(context->socketPool);
}

static int receiveRequestCallback(serviceContext_t *context, apr_pollset_t *pollset, apr_socket_t *sock) {
	int r = dispatchInputMessage(sock, &context->request, context->transactionPool);
	if (r != R_SUCCESS) {
		apr_socket_close(sock);
		clearTransaction(context);
		clearSocket(context);
		return r;
	}

	// Change context status from write to read
	apr_pollfd_t descriptor = { context->socketPool, APR_POLL_SOCKET, APR_POLLIN, 0, { NULL }, context };
	descriptor.desc.s = sock;
	apr_pollset_remove(pollset, &descriptor);
	descriptor.reqevents = APR_POLLOUT;
	apr_pollset_add(pollset, &descriptor);
	context->status = SERV_SEND_RESPONSE;
	context->cbFunc = sendResponseCallback;
	return R_SUCCESS;
}

/**
 * Send a response to the client.
 */
static int sendResponseCallback(serviceContext_t *context, apr_pollset_t *pollset, apr_socket_t *sock) {
	writeResponse(sock, context->response);

	// Clear all memory related to request and response
	clearTransaction(context);

	// Change context status from write to read
	apr_pollfd_t descriptor = { context->socketPool, APR_POLL_SOCKET, APR_POLLOUT, 0, { NULL }, context };
	descriptor.desc.s = sock;
	apr_pollset_remove(pollset, &descriptor);
	descriptor.reqevents = APR_POLLIN;
	apr_pollset_add(pollset, &descriptor);
	context->status = SERV_RECV_REQUEST;
	context->cbFunc = receiveRequestCallback;
	return R_SUCCESS;
}
