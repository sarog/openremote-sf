#ifndef _SERIALIZE_H
#define _SERIALIZE_H

#include "apr_network_io.h"

#include "dispatch.h"

int readHeader(apr_socket_t *sock, char *code);
int readBody(apr_socket_t *sock, message_t **message, apr_pool_t *pool, char code);
int createACK(apr_pool_t *pool, message_t **message, apr_int32_t code);

#endif
