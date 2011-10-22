#ifndef _PORT_H
#define _PORT_H

#include "apr_pools.h"

typedef int (*physicalLock_t)(apr_pool_t *pool, char *portId, void **runtimeData);
typedef int (*physicalUnlock_t)(apr_pool_t *pool, char *portId, void **runtimeData);

typedef struct _port_t {
	char *portId;
	char *portType;
	void *configuration;
	void *runtimeData;
	char *lockSource;
	physicalLock_t lockCb;
	physicalUnlock_t unlockCb;
} port_t;

int lock(apr_pool_t *pool, port_t *port, char *source);
int unlock(apr_pool_t *pool, port_t *port, char *source);

#endif
