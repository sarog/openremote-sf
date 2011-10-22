#include <stdio.h>
#include "apr_hash.h"
#include "portManager.h"
#include "linuxSerialPort.h"

static apr_pool_t *portPool;
static apr_hash_t *ports;

int initPortManager() {
	apr_pool_create(&portPool, NULL);
	ports = apr_hash_make(portPool);
	return R_SUCCESS;
}

int createPort(char *portId, char *portType) {
	port_t *p;

	// Check if port already exists
	p = apr_hash_get(ports, portId, strlen(portId));
	if (p != NULL)
		return R_PORT_EXISTS;

	// Otherwise create it
	p = apr_palloc(portPool, sizeof(port_t));
	p->portId = apr_palloc(portPool, strlen(portId) + 1);
	strcpy(p->portId, portId);
	p->portType = apr_palloc(portPool, strlen(portType) + 1);
	strcpy(p->portType, portType);
	p->lockSource = NULL;
	p->configuration = NULL;
	apr_hash_set(ports, portId, strlen(portId), p);
	p->lockCb = linuxSerialLock;
	p->unlockCb = linuxSerialUnlock;
	printf("port '%s' created\n", portId);
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
