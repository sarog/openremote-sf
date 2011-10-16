#include <stdio.h>
#include "apr_hash.h"
#include "portManager.h"

static apr_pool_t *portPool;
static apr_hash_t *ports;

int init() {
	apr_pool_create(&portPool, NULL);
	ports = apr_hash_make(portPool);
	return R_SUCCESS;
}

int createPort(char *portId, char *portType) {
	port_t *p;
	port_t port;
	p = apr_hash_get(ports, portId, strlen(portId));
	if (p != NULL)
		return R_PORT_EXISTS;
	// TODO portId should be copied?
	port.portId = portId;
	port.portType = portType;
	apr_hash_set(ports, portId, strlen(portId), &port);
	return R_SUCCESS;
}

int getPort(char *portId, port_t **port) {
	port_t *p;
	p = apr_hash_get(ports, portId, strlen(portId));
	if (p == NULL)
		return R_NO_SUCH_PORT;
	*port = p;
	return R_SUCCESS;
}
