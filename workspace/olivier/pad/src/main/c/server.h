#ifndef _SERVER_H
#define _SERVER_H

#include "apr_network_io.h"

typedef int (*readCallback_t)(apr_socket_t *sock);
typedef int (*writeCallback_t)(apr_socket_t *sock);

int runServer(readCallback_t readCb, writeCallback_t writeCb);

#endif
