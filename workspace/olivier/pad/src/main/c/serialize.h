#ifndef _SERIALIZE_H
#define _SERIALIZE_H

#include "apr_network_io.h"

int readRequest(apr_socket_t *sock);
int writeResponse(apr_socket_t *sock);

#endif
