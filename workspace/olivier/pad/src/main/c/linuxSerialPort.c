#include "codes.h"
#include "linuxSerialPort.h"

int linuxSerialLock(apr_pool_t *pool, char *portId, void **runtimeData) {
	// TODO Open serial port
	return R_SUCCESS;
}

int linuxSerialUnlock(apr_pool_t *pool, char *portId, void **runtimeData) {
	return R_SUCCESS;
}
