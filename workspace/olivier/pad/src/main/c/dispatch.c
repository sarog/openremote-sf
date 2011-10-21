#include "codes.h"
#include "serialize.h"
#include "port.h"
#include "dispatch.h"

int checkInputMessage(apr_socket_t *sock, char *code, messageTxType_t *type) {
	int r;

	RETURN_IF(readHeader(sock, code), r)
	if (*code == ACK)
		*type = CLIENT;
	else
		*type = SERVER;
	return R_SUCCESS;
}

int dispatchInputMessage(apr_socket_t *sock, message_t **message, apr_pool_t *pool, char code) {
	int r;
	port_t *port;

	RETURN_IF(readBody(sock, message, pool, code), r)

	switch ((*message)->code) {
	case PING:
		break;
	case SHUTDOWN:
		break;
	case ACK:
		break;
	case NOTIFY:
		break;
	case LOCK:
		RETURN_IF(getPort((*message)->fields[0].stringVal, &port), r)
		return lock(port, (*message)->fields[1].stringVal);
		break;
	case UNLOCK:
		RETURN_IF(getPort((*message)->fields[0].stringVal, &port), r)
		return unlock(port, (*message)->fields[1].stringVal);
		break;
	case CREATE_PORT:
		return createPort((*message)->fields[0].stringVal, (*message)->fields[1].stringVal);
		break;
	case CONFIGURE:
		RETURN_IF(getPort((*message)->fields[0].stringVal, &port), r)
		// TODO configure
		break;
	}

	return R_SUCCESS;
}
