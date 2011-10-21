#ifndef _DISPATCH_H
#define _DISPATCH_H

#include "apr_network_io.h"

typedef enum _messageTxType_t {
	CLIENT, SERVER
} messageTxType_t;

typedef struct _field_t {
	enum {
		HEX = 'H'
	} encoding;
	int length;
	union {
		apr_int32_t int32Val;
		char *stringVal;
	};
} field_t;

typedef enum _code_t {
	PING = 'P',
	SHUTDOWN = 'S',
	SHUTTING_DOWN = 'G',
	ACK = 'A',
	NOTIFY = 'N',
	CONFIGURE = 'C',
	LOCK = 'L',
	UNLOCK = 'U',
	PORT_CREATED = 'D',
	PORT_REMOVED = 'V',
	CREATE_PORT = 'O'
} code_t;

typedef struct _message_t {
	code_t code;
	int nbFields;
	field_t *fields;
} message_t;

int readMsg(apr_socket_t *sock, message_t **message, apr_pool_t *pool);
int dispatchInputMessage(apr_socket_t *sock, message_t **message, apr_pool_t *pool, char code);

#endif
