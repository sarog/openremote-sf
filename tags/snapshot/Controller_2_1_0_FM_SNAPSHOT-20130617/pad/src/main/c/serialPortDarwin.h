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

typedef struct _trsUnit_t {
	char *cfgStr;
	tcflag_t cfgVal;
} trsUnit;

typedef struct _trsTbl_t {
  char *key;
  apr_uint32_t nbValues;
  trsUnit values[];
} trsTbl_t;

#endif
