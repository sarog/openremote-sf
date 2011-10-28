#ifndef _SERVER_H
#define _SERVER_H

#include "apr_network_io.h"
#include "serialize.h"
#include "osPort.h"  // TODO keep?

typedef struct _transaction_t {
	enum {
		WAITING_FOR_REQUEST, WAITING_FOR_RESPONSE
	} status;
	message_t *request;
	message_t *response;
	portReceive_t portReceiveCb;
} transaction_t;

int checkInputMessage(apr_socket_t *sock, char *code, messageTxType_t *type);
int operateRequest(apr_socket_t *sock, transaction_t *transaction, apr_pool_t *pool, char code);
int writeMessage(apr_socket_t *sock, message_t *message);

#endif
