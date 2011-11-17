#ifndef _SERIALIZE_H
#define _SERIALIZE_H

#include "apr_network_io.h"

typedef enum _messageTxType_t {
	CLIENT_TX, SERVER_TX
} messageTxType_t;

typedef struct _field_t {
	enum {
		HEX = 'H'
	} encoding;
	apr_uint16_t length;
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

int readHeader(apr_socket_t *sock, char *code);
int readBody(apr_socket_t *sock, message_t **message, apr_pool_t *pool, char code);
int createACK(apr_pool_t *pool, message_t **message, ackCode_t code);
int createNotify(apr_pool_t *pool, message_t **message, char *portId, char *buf, int len);

#endif
