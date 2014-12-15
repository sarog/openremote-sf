#include <stddef.h>

#include "codes.h"
#include "serialize.h"
#include "portManager.h"
#include "port.h"
#include "server.h"

void createServerTransaction(apr_pool_t *pool, serverTransaction_t **tx, portReceive_t receiveCb) {
	if (*tx == NULL) {
		*tx = apr_palloc(pool, sizeof(serverTransaction_t));
		(*tx)->request = NULL;
		(*tx)->response = NULL;
		(*tx)->status = WAITING_FOR_REQUEST;
		(*tx)->portReceiveCb = receiveCb;
		(*tx)->shutdown = FALSE;
	}
}

void clearServerTransaction(apr_pool_t *pool, serverTransaction_t **tx) {
	apr_pool_clear(pool);
	*tx = NULL;
}

int checkInputMessage(apr_socket_t *sock, char *code, messageTxType_t *txType) {
	CHECK(readHeader(sock, code))
	if (*code == ACK) {
		*txType = CLIENT_TX;
	} else {
		*txType = SERVER_TX;
	}
	return R_SUCCESS;
}

ackCode_t getAckCode(int err) {
	switch (err) {
	case R_SUCCESS:
		return ackOk;
		// TODO complete
	default:
		return ackErr;
	}
	return err;
}

int operateRequest(apr_socket_t *sock, serverTransaction_t *tx, apr_pool_t *txPool, char code) {
	CHECK(readBody(sock, &tx->request, txPool, code))

	switch (tx->request->code) {
	case PING:
		return createACK(txPool, &tx->response, ackOk);
		break;
	case SHUTDOWN:
		tx->shutdown = TRUE;
		return createACK(txPool, &tx->response, ackOk);
		break;
	case NOTIFY: {
		port_t *port;
		int r = getPort(tx->request->fields[0].stringVal, &port);
		if (r == R_SUCCESS) {
			r = portSend(txPool, port, tx->request->fields[1].stringVal, tx->request->fields[1].length);
		}
		return createACK(txPool, &tx->response, getAckCode(r));
		break;
	}
	case LOCK: {
		port_t *port;
		int r = getPort(tx->request->fields[0].stringVal, &port);
		if (r == R_SUCCESS) {
			r = lock(txPool, port, tx->request->fields[1].stringVal, tx->portReceiveCb);
		}
		return createACK(txPool, &tx->response, getAckCode(r));
	}
	case UNLOCK: {
		port_t *port;
		int r = getPort(tx->request->fields[0].stringVal, &port);
		if (r == R_SUCCESS) {
			r = unlock(txPool, port, tx->request->fields[1].stringVal);
		}
		return createACK(txPool, &tx->response, getAckCode(r));
	}
	case CREATE_PORT: {
		int r = createPort(tx->request->fields[0].stringVal, tx->request->fields[1].stringVal);
		return createACK(txPool, &tx->response, getAckCode(r));
	}
	case CONFIGURE: {
		port_t *port;
		int r = getPort(tx->request->fields[0].stringVal, &port);
		if (r == R_SUCCESS) {
			apr_int32_t i;
			for (i = 0; i < tx->request->fields[1].int32Val; ++i) {
				r = portConfigure(txPool, port, tx->request->fields[2 + (i * 2)].stringVal, tx->request->fields[3 + (i * 2)].stringVal);
				if (r != R_SUCCESS)
					break;
			}
		}
		return createACK(txPool, &tx->response, getAckCode(r));
	}
	}

	return R_SUCCESS;
}
