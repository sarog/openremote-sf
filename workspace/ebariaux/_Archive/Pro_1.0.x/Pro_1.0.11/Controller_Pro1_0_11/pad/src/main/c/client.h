#ifndef _CLIENT_H
#define _CLIENT_H

#define APR_DECLARE_STATIC
#include "apr_thread_cond.h"

#include "serialize.h"

typedef struct _clientTransaction_t {
	message_t *request;
	message_t *response;
} clientTransaction_t;

int createClientTransaction(apr_pool_t *pool, clientTransaction_t **tx);
void clearClientTransaction(apr_pool_t *pool, clientTransaction_t **tx);
int operatePortData(clientTransaction_t *clientTx, apr_pool_t *pool, char *portId, char *buf, int len);
int operateResponse(apr_socket_t *sock, clientTransaction_t *clientTx, apr_pool_t *pool, char code);

#endif
