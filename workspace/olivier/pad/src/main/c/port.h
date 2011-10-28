#ifndef _PORT_H
#define _PORT_H

#include "apr_pools.h"
#include "osPort.h"

typedef struct _port_t {
	char *portId;
	char *portType;
	portConfiguration_t *configuration;
	apr_pool_t *runtimePool;
	portContext_t *context;
	char *lockSource;
	physicalLock_t lockCb;
	physicalUnlock_t unlockCb;
	physicalSend_t portSendCb;
} port_t;

int lock(apr_pool_t *pool, port_t *port, char *source, portReceive_t portReceiveCb);
int unlock(apr_pool_t *pool, port_t *port, char *source);
int portSend(apr_pool_t *pool, port_t *port, char *data, int len);

#endif
