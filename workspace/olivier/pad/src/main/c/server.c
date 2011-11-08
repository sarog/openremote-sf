#include <stddef.h>
#include "codes.h"
#include "serialize.h"
#include "port.h"
#include "server.h"

void createServerTransaction(apr_pool_t *pool, serverTransaction_t **tx, portReceive_t receiveCb) {
	if (*tx == NULL) {
		*tx = apr_palloc(pool, sizeof(serverTransaction_t));
		(*tx)->request = NULL;
		(*tx)->response = NULL;
		(*tx)->status = WAITING_FOR_REQUEST;
		(*tx)->portReceiveCb = receiveCb;
	}
}

void clearServerTransaction(apr_pool_t *pool, serverTransaction_t **tx) {
	apr_pool_clear(pool);
	*tx = NULL;
}

int checkInputMessage(apr_socket_t *sock, char *code, messageTxType_t *type) {
	CHECK(readHeader(sock, code))
	if (*code == ACK)
		*type = CLIENT;
	else
		*type = SERVER;
	return R_SUCCESS;
}

int operateRequest(apr_socket_t *sock, serverTransaction_t *tx, apr_pool_t *txPool, char code) {

	CHECK(readBody(sock, &tx->request, txPool, code))

	switch (tx->request->code) {
	case PING:
		return createACK(txPool, &tx->response, ACK_OK);
		break;
	case SHUTDOWN:
		break;
	case NOTIFY: {
		port_t *port;
		int r = getPort(tx->request->fields[0].stringVal, &port);
		if (r == R_SUCCESS) {
			r = portSend(txPool, port, tx->request->fields[1].stringVal, tx->request->fields[1].length);
		}
		return createACK(txPool, &tx->response, r); //TODO define a return code
		break;
	}
	case LOCK: {
		port_t *port;
		int r = getPort(tx->request->fields[0].stringVal, &port);
		if (r == R_SUCCESS) {
			r = lock(txPool, port, tx->request->fields[1].stringVal, tx->portReceiveCb);
		}
		return createACK(txPool, &tx->response, r); //TODO define a return code
	}
	case UNLOCK: {
		port_t *port;
		int r = getPort(tx->request->fields[0].stringVal, &port);
		if (r == R_SUCCESS) {
			r = unlock(txPool, port, tx->request->fields[1].stringVal);
		}
		return createACK(txPool, &tx->response, r); //TODO define a return code
	}
	case CREATE_PORT: {
		printf("creating port %s, %s\n", tx->request->fields[0].stringVal, tx->request->fields[1].stringVal);
		int r = createPort(tx->request->fields[0].stringVal, tx->request->fields[1].stringVal);
		printf("port created, %d\n", r);
		return createACK(txPool, &tx->response, r); //TODO define a return code
	}
	case CONFIGURE: {
		port_t *port;
		CHECK(getPort(tx->request->fields[0].stringVal, &port))
		// TODO configure
		return createACK(txPool, &tx->response, 0);
	}
	}

	return R_SUCCESS;
}
