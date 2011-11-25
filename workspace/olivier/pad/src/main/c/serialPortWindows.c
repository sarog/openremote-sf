#include "codes.h"
#include "serialPortWindows.h"

int physicalLock(apr_pool_t *pool, char *portId, portContext_t **portContext, apr_hash_t *cfg, portReceive_t portReceiveCb) {
	return R_SUCCESS;
}

physicalLock_t physicalLockCb = physicalLock;

int physicalUnlock(apr_pool_t *pool, char *portId, portContext_t **portContext) {
	return R_SUCCESS;
}

physicalUnlock_t physicalUnlockCb = physicalUnlock;

int physicalSend(portContext_t *portContext, char *data, int len) {
	return R_SUCCESS;
}

physicalSend_t physicalSendCb = physicalSend;
