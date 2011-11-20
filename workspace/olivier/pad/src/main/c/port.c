#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "codes.h"
#include "port.h"

int logicalLock(port_t *port, char *source) {
	if (port->lockSource != NULL && strcmp(port->lockSource, source) != 0)
		return R_WRONG_LOCK_STATUS;
	if (port->lockSource == NULL) {
		port->lockSource = malloc(strlen(source) + 1);
		strcpy(port->lockSource, source);
	}
	return R_SUCCESS;
}

int logicalUnlock(port_t *port, char *source) {
	if (port->lockSource == NULL || strcmp(port->lockSource, source) != 0)
		return R_WRONG_LOCK_STATUS;
	free(port->lockSource);
	port->lockSource = NULL;
	return R_SUCCESS;
}

int lock(apr_pool_t *pool, port_t *port, char *source, portReceive_t portReceiveCb) {
	CHECK(logicalLock(port, source))

	// Prepare runtime
	int r = port->lockCb(port->portPool, port->portId, &port->context, port->cfg, portReceiveCb);
	if (r != R_SUCCESS) {
		return unlock(pool, port, source);
	}
	return r;
}

int unlock(apr_pool_t *pool, port_t *port, char *source) {
	port->unlockCb(port->portPool, port->portId, &port->context);
	apr_pool_destroy(port->portPool);
	int r = logicalUnlock(port, source);
	return r;
}

int unencode(apr_pool_t *pool, char **buf, char *data, int len) {
	int i;
	if (len % 2 != 0)
		return R_INVALID_MESSAGE;
	*buf = apr_palloc(pool, len / 2);
	char tmp[3];
	tmp[2] = 0;
	char *end;
	for (i = 0; i < len; i += 2) {
		tmp[0] = data[i];
		tmp[1] = data[i + 1];
		APR_CHECK(apr_strtoff(&(*buf)[i / 2], tmp, &end, 16), R_INVALID_MESSAGE);
		if (*end != 0 || end - tmp != 2)
			return R_INVALID_MESSAGE;
	}
	return R_SUCCESS;
}

int portSend(apr_pool_t *pool, port_t *port, char *data, int len) {
	char *buf;

	// Check if port is locked
	if (port->lockSource == NULL)
		return R_UNLOCKED;

	// Unencode data
	CHECK(unencode(pool, &buf, data, len));

	// Send data
	return port->portSendCb(port->context, buf, len / 2);
}

int portConfigure(apr_pool_t *pool, port_t *port, char *cfgStr, char *cfgVal) {
	apr_hash_set(port->cfg, cfgStr, APR_HASH_KEY_STRING, cfgVal);
	return R_SUCCESS;
}
