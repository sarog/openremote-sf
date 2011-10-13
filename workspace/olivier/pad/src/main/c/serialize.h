#ifndef _SERIALIZE_H
#define _SERIALIZE_H

#include "apr_network_io.h"

#define R_SUCCESS 0
#define R_INVALID_CODE -1
#define R_INVALID_MESSAGE -2
#define R_INVALID_VERSION -3

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
	PING = 'P', SHUTDOWN = 'S', SHUTTING_DOWN = 'G', ACK = 'A', NOTIFY = 'N'
} code_t;

typedef struct _message_t {
	code_t code;
	int nbFields;
	field_t *fields;
} message_t;

int readRequest(apr_socket_t *sock, message_t **message, apr_pool_t *pool);
int writeResponse(apr_socket_t *sock, message_t *message);

#endif
