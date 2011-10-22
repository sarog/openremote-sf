#ifndef _LINUXSERIALPORT_H
#define _LINUXSERIALPORT_H

#include "apr_pools.h"

int linuxSerialLock(apr_pool_t *pool, char *portId, void **runtimeData);
int linuxSerialUnlock(apr_pool_t *pool, char *portId, void **runtimeData);

#endif
