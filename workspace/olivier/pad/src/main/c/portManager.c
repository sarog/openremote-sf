#include <stdio.h>

#define APR_DECLARE_STATIC
#include "apr_hash.h"

#include "portManager.h"

extern physicalLock_t physicalLockCb;
extern physicalUnlock_t physicalUnlockCb;
extern physicalSend_t physicalSendCb;
static apr_pool_t *portsPool;
static apr_hash_t *ports;

int initPortManager() {
	apr_pool_create(&portsPool, NULL);
	ports = apr_hash_make(portsPool);
	return R_SUCCESS;
}

int createPort(char *portId, char *portType) {
	port_t *p;

	// Check if port already exists
	p = apr_hash_get(ports, portId, APR_HASH_KEY_STRING);
	if (p != NULL)
		return R_SUCCESS;

	// Otherwise create it
	p = apr_palloc(portsPool, sizeof(port_t));
	apr_pool_create(&p->portPool, portsPool);
	p->portId = apr_palloc(p->portPool, strlen(portId) + 1);
	strcpy(p->portId, portId);
	p->portType = apr_palloc(p->portPool, strlen(portType) + 1);
	strcpy(p->portType, portType);
	p->lockSource = NULL;
	p->cfg = apr_hash_make(p->portPool);
	p->lockCb = physicalLockCb;
	p->unlockCb = physicalUnlockCb;
	p->portSendCb = physicalSendCb;

	// Add newly created port to port list
	apr_hash_set(ports, p->portId, APR_HASH_KEY_STRING, p);

	return R_SUCCESS;
}

int getPort(char *portId, port_t **port) {
	port_t *p;
	p = apr_hash_get(ports, portId, APR_HASH_KEY_STRING);
	if (p == NULL)
		return R_NO_SUCH_PORT;
	*port = p;
	return R_SUCCESS;
}
