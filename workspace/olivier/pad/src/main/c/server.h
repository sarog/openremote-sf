#ifndef _SERVER_H
#define _SERVER_H

#include "apr_network_io.h"
#include "transaction.h"

int checkInputMessage(apr_socket_t *sock, char *code, messageTxType_t *type);
int operateRequest(apr_socket_t *sock, transaction_t *transaction, apr_pool_t *pool, char code);

#endif
