#ifndef _LINUXSERIALPORT_H
#define _LINUXSERIALPORT_H

#include <termios.h>
#include "apr_pools.h"
#include "apr_thread_proc.h"
#include "osPort.h"

typedef struct _portContext_t {
	char *portId;
	int fd;
	struct termios oldtio;
	struct termios newtio;
	apr_thread_t *readThread;
	portReceive_t portReceiveCb;
} portContext_t;

int physicalLock(apr_pool_t *pool, char *portId, portContext_t **portContext, portReceive_t portReceiveCb);
int physicalUnlock(apr_pool_t *pool, char *portId, portContext_t **portContext);
int physicalSend(portContext_t *portContext, char *data, int len);

#endif
