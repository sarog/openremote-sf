#include "codes.h"
#include "serialize.h"
#include "port.h"
#include "server.h"

int checkInputMessage(apr_socket_t *sock, char *code, messageTxType_t *type) {
	CHECK(readHeader(sock, code))
	if (*code == ACK)
		*type = CLIENT;
	else
		*type = SERVER;
	return R_SUCCESS;
}

int operateRequest(apr_socket_t *sock, transaction_t *tx, apr_pool_t *txPool, char code) {

	CHECK(readBody(sock, &tx->request, txPool, code))

	switch (tx->request->code) {
	case PING:
		return createACK(txPool, &tx->response, ACK_OK);
		break;
	case SHUTDOWN:
		break;
	case NOTIFY: {
		port_t *port;
		CHECK(getPort(tx->request->fields[0].stringVal, &port))
		int r = portSend(txPool, port, tx->request->fields[1].stringVal, tx->request->fields[1].length);
		return createACK(txPool, &tx->response, r); //TODO define a return code
		break;
	}
	case LOCK: {
		port_t *port;
		CHECK(getPort(tx->request->fields[0].stringVal, &port))
		int r = lock(txPool, port, tx->request->fields[1].stringVal, tx->portReceiveCb);
		return createACK(txPool, &tx->response, r); //TODO define a return code
	}
	case UNLOCK: {
		port_t *port;
		CHECK(getPort(tx->request->fields[0].stringVal, &port))
		int r = unlock(txPool, port, tx->request->fields[1].stringVal);
		return createACK(txPool, &tx->response, r); //TODO define a return code
	}
	case CREATE_PORT:
		return createPort(tx->request->fields[0].stringVal, tx->request->fields[1].stringVal);
		break;
	case CONFIGURE: {
		port_t *port;
		CHECK(getPort(tx->request->fields[0].stringVal, &port))
		// TODO configure
		break;
	}
	}

	return R_SUCCESS;
}

int writeMessage(apr_socket_t *sock, message_t *message) {
	CHECK(writeHeader(sock, message));
	switch (message->code) {
	case ACK:
		CHECK(writeInt32(sock, &message->fields[0]))
		break;
	case NOTIFY:
		break;
	}

	return R_SUCCESS;
}
