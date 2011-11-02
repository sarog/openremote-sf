#ifndef _OSPORT_H
#define _OSPORT_H

typedef struct _portConfiguration_t portConfiguration_t;
typedef struct _portContext_t portContext_t;

typedef int (*portReceive_t)(char *portId, char *buf, int len);
typedef int (*physicalLock_t)(apr_pool_t *pool, char *portId, portContext_t **portR, portReceive_t portReceiveCb);
typedef int (*physicalUnlock_t)(apr_pool_t *pool, char *portId, portContext_t **portR);
typedef int (*physicalSend_t)(portContext_t *runtimeData, char *data, int len);

#endif /* _OSPORT_H_ */
