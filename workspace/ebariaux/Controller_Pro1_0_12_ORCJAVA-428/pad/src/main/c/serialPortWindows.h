#ifndef _SERIALPORTWINDOWS_H
#define _SERIALPORTWINDOWS_H

#include <Windows.h>

#define APR_DECLARE_STATIC
#include "apr_pools.h"

#include "osPort.h"

struct _portContext_t {
	char *portId;
	HANDLE hComm;
	DCB params;
	portReceive_t portReceiveCb;
};

typedef struct _trsUnit_t {
	char *cfgStr;
	DWORD cfgVal;
} trsUnit;

typedef struct _trsTbl_t {
  char *key;
  apr_uint32_t nbValues;
  trsUnit values[];
} trsTbl_t;

#endif
