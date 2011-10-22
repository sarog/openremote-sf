#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "codes.h"
#include "port.h"

int logicalLock(port_t *port, char *source) {
	if (port->lockSource != NULL)
		return R_WRONG_LOCK_STATUS;
	port->lockSource = malloc(strlen(source) + 1);
	strcpy(port->lockSource, source);
	printf("port '%s' locked by '%s'\n", port->portId, port->lockSource);
	return R_SUCCESS;
}

int logicalUnlock(port_t *port, char *source) {
	if (port->lockSource == NULL || strcmp(port->lockSource, source) != 0)
		return R_WRONG_LOCK_STATUS;
	free(port->lockSource);
	port->lockSource = NULL;
	printf("port '%s' unlocked by '%s' \n", port->portId, port->lockSource);
	return R_SUCCESS;
}

int lock(apr_pool_t *pool, port_t *port, char *source) {
	CHECK(logicalLock(port, source))
	int r = port->lockCb(pool, port->portId, &port->runtimeData);
	if (r != R_SUCCESS) {
		return logicalUnlock(port, source);
	}
	return r;
}

int unlock(apr_pool_t *pool, port_t *port, char *source) {
	int r = logicalUnlock(port, source);
	return r;
}
