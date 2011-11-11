#ifndef _LINUXSERIALPORT_H
#define _LINUXSERIALPORT_H

#include <termios.h>
#include "apr_pools.h"
#include "osPort.h"

struct _portContext_t {
	char *portId;
	int fd;
	struct termios oldtio;
	struct termios newtio;
	pthread_t readThread;
	portReceive_t portReceiveCb;
};

int physicalLock(apr_pool_t *pool, char *portId, portContext_t **portContext, portReceive_t portReceiveCb);
int physicalUnlock(apr_pool_t *pool, char *portId, portContext_t **portContext);
int physicalSend(portContext_t *portContext, char *data, int len);

#endif
