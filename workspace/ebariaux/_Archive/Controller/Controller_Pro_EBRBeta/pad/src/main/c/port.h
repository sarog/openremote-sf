#ifndef _PORT_H
#define _PORT_H

#define APR_DECLARE_STATIC
#include "apr_pools.h"

#include "osPort.h"

typedef struct _port_t {
	apr_pool_t *portPool;
	char *portId;
	char *portType;
	apr_hash_t *cfg;
	portContext_t *context;
	char *lockSource;
	physicalLock_t lockCb;
	physicalUnlock_t unlockCb;
	physicalSend_t portSendCb;
} port_t;

int lock(apr_pool_t *pool, port_t *port, char *source, portReceive_t portReceiveCb);
int unlock(apr_pool_t *pool, port_t *port, char *source);
int portSend(apr_pool_t *pool, port_t *port, char *data, int len);
int portConfigure(apr_pool_t *pool, port_t *port, char *cfgStr, char *cfgVal);

#endif
