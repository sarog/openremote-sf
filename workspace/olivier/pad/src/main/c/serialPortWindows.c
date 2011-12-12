#include "codes.h"
#include "serialPortWindows.h"

#define RCV_BUF_SIZE 256

static const trsTbl_t speedTrsTbl = { "speed", 5, { { "9600", CBR_9600 }, {
		"19200", CBR_19200 }, { "38400", CBR_38400 }, { "57600", CBR_57600 }, { "115200",
		CBR_115200 } } };
static const trsTbl_t nbBitsTrsTbl = { "nbBits", 2,
		{ { "7", 7 }, { "8", 8 } } };
static const trsTbl_t parityTrsTbl = { "parity", 3,
		{ { "no", NOPARITY }, { "even", EVENPARITY }, { "odd", ODDPARITY } } };


void lsRead(void *data) {
	char buf[RCV_BUF_SIZE];
	portContext_t *portContext = (portContext_t *) data;
	BOOL res = TRUE;
	DWORD len;

	printf("started read thread\n");
	while (res) {
		res = ReadFile(portContext->hComm, buf, RCV_BUF_SIZE, &len, 0);
		if (res) {
			int r = portContext->portReceiveCb(portContext->portId, buf, len);
		}
	}
	printf("ended read thread\n");
	_endthread();
}

DWORD lsGetCfg(apr_hash_t *cfg, const trsTbl_t *tbl, DWORD dft) {
	char *s = (char *) apr_hash_get(cfg, tbl->key, APR_HASH_KEY_STRING);
	DWORD out = dft;
	if (s != NULL) {
		apr_uint32_t i;
		for (i = 0; i < tbl->nbValues; ++i) {
			if (strcmp(tbl->values[i].cfgStr, s) == 0) {
				out = tbl->values[i].cfgVal;
				break;
			}
		}
	}
	return out;
}

int lsConfigure(portContext_t *portContext, apr_hash_t *cfg) {
	if(!GetCommState(portContext->hComm, &portContext->params)) {
		return R_CONFIGURE_ERROR;
	}
	portContext->params.BaudRate = lsGetCfg(cfg, &speedTrsTbl, CBR_19200);
	portContext->params.ByteSize = (BYTE) lsGetCfg(cfg, &nbBitsTrsTbl, 8);
	portContext->params.StopBits = ONESTOPBIT;
	portContext->params.Parity = (BYTE) lsGetCfg(cfg, &parityTrsTbl, EVENPARITY);
	portContext->params.fInX = FALSE;
	portContext->params.fOutX = FALSE;
	portContext->params.fOutxCtsFlow = FALSE;
	portContext->params.fOutxDsrFlow = FALSE;
	portContext->params.fRtsControl = RTS_CONTROL_DISABLE;
	portContext->params.fDtrControl = DTR_CONTROL_DISABLE;
	portContext->params.fParity = FALSE;
	if(!SetCommState(portContext->hComm, &portContext->params)) {
		return R_CONFIGURE_ERROR;
	}
	return R_SUCCESS;
}

int physicalLock(apr_pool_t *pool, char *portId, portContext_t **portContext, apr_hash_t *cfg, portReceive_t portReceiveCb) {
	// Allocate serial port context
	*portContext = apr_palloc(pool, sizeof(portContext_t));
	(*portContext)->portReceiveCb = portReceiveCb;
	(*portContext)->portId = apr_palloc(pool, strlen(portId) + 1);
	strcpy((*portContext)->portId, portId);

	(*portContext)->hComm = CreateFile((*portContext)->portId,  
                    GENERIC_READ | GENERIC_WRITE, 
                    0, 
                    0, 
                    OPEN_EXISTING,
                    FILE_ATTRIBUTE_NORMAL,
                    0);
	if ((*portContext)->hComm == INVALID_HANDLE_VALUE) {
		return R_PORT_ERROR;
	}

	CHECK(lsConfigure(*portContext, cfg));
	// Start read thread
	_beginthread(lsRead, 0, *portContext);

	return R_SUCCESS;
}

physicalLock_t physicalLockCb = physicalLock;

int physicalUnlock(apr_pool_t *pool, char *portId, portContext_t **portContext) {
	CloseHandle((*portContext)->hComm);

	return R_SUCCESS;
}

physicalUnlock_t physicalUnlockCb = physicalUnlock;

int physicalSend(portContext_t *portContext, char *data, int len) {
	DWORD r;
	if(!WriteFile(portContext->hComm, data, len, &r, NULL)) {
		return R_SEND_ERROR;
	}
	return R_SUCCESS;
}

physicalSend_t physicalSendCb = physicalSend;
