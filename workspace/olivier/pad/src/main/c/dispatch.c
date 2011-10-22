#include "codes.h"
#include "serialize.h"
#include "port.h"
#include "dispatch.h"

int checkInputMessage(apr_socket_t *sock, char *code, messageTxType_t *type) {
	CHECK(readHeader(sock, code))
	if (*code == ACK)
		*type = CLIENT;
	else
		*type = SERVER;
	return R_SUCCESS;
}

int operateRequest(apr_socket_t *sock, transaction_t *tx, apr_pool_t *txPool, char code) {
	port_t *port;

	CHECK(readBody(sock, &tx->request, txPool, code))

	switch (tx->request->code) {
	case PING:
		return createACK(txPool, &tx->response, ACK_OK);
		break;
	case SHUTDOWN:
		break;
	case NOTIFY:
		break;
	case LOCK:
		CHECK(getPort(tx->request->fields[0].stringVal, &port))
		return lock(port, tx->request->fields[1].stringVal);
		break;
	case UNLOCK:
		CHECK(getPort(tx->request->fields[0].stringVal, &port))
		return unlock(port, tx->request->fields[1].stringVal);
		break;
	case CREATE_PORT:
		return createPort(tx->request->fields[0].stringVal, tx->request->fields[1].stringVal);
		break;
	case CONFIGURE:
		CHECK(getPort(tx->request->fields[0].stringVal, &port))
		// TODO configure
		break;
	}

	return R_SUCCESS;
}

int writeMessage(apr_socket_t *sock, message_t *message) {
	CHECK(writeHeader(sock, message));
	switch (message->code) {
	case ACK:
		CHECK(writeInt32(sock, &message->fields[0]))
		break;
	}

	return R_SUCCESS;
}
