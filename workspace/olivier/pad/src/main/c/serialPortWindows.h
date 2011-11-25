#ifndef _SERIALPORTWINDOWS_H
#define _SERIALPORTWINDOWS_H

#define APR_DECLARE_STATIC
#include "apr_pools.h"

#include "osPort.h"

struct _portContext_t {
	char *portId;
	portReceive_t portReceiveCb;
};


#endif
