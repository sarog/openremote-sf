#ifndef _SERIALIZE_H
#define _SERIALIZE_H

#include "apr_network_io.h"

#include "dispatch.h"

int readMessage(apr_socket_t *sock, message_t **message, apr_pool_t *pool);
int writeResponse(apr_socket_t *sock, message_t *message);

#endif
