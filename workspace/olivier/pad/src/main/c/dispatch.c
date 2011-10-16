#include "codes.h"
#include "serialize.h"
#include "port.h"
#include "dispatch.h"

int dispatchInputMessage(apr_socket_t *sock, message_t **message, apr_pool_t *pool) {
	int r;
	port_t *port;

	RETURN_IF(readMessage(sock, message, pool), r)

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
		return lock(port);
		break;
	case UNLOCK:
		RETURN_IF(getPort((*message)->fields[0].stringVal, &port), r)
		return unlock(port);
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
