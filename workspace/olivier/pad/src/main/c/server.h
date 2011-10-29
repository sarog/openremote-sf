#ifndef _SERVER_H
#define _SERVER_H

#include "apr_network_io.h"
#include "serialize.h"
#include "osPort.h"  // TODO keep?

typedef struct _serverTransaction_t {
	enum {
		WAITING_FOR_REQUEST, WAITING_FOR_RESPONSE
	} status;
	message_t *request;
	message_t *response;
	portReceive_t portReceiveCb;
} serverTransaction_t;

void createServerTransaction(apr_pool_t *pool, serverTransaction_t **tx, portReceive_t receiveCb);
void clearServerTransaction(apr_pool_t *pool, serverTransaction_t **tx);
int checkInputMessage(apr_socket_t *sock, char *code, messageTxType_t *type);
int operateRequest(apr_socket_t *sock, serverTransaction_t *serverTx, apr_pool_t *pool, char code);

#endif
