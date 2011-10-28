#ifndef _CLIENT_H
#define _CLIENT_H

#include "apr_network_io.h"
#include "transaction.h"

int operatePortData(apr_socket_t *sock, transaction_t *transaction, apr_pool_t *pool, char *portId, char *buf, int len);

#endif
