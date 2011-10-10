#include <stdio.h>

#include "apr_pools.h"
#include "serialize.h"

#define RETURN(call, r)              r = call; if(r != R_SUCCESS) return r;
#define APR_RETURN(call, rv, ret)    rv = call; if(rv != APR_SUCCESS) return ret;

void printMessage(message_t *message) {
	printf("message code %c", message->code);
	switch (message->code) {
	case ACK:
		printf(", code=0x%X", message->fields[0].int32Val);
		break;
	default:
		printf("TODO");
		break;
	}
	printf("\n");
}

int buf2Int32(const char *buf, apr_int32_t *res) {
	apr_off_t v;
	apr_status_t rv;
	char *end;
	APR_RETURN(apr_strtoff(&v, buf, &end, 16), rv, R_INVALID_MESSAGE)
	if (*end != 0)
		return R_INVALID_MESSAGE;
	*res = (apr_int32_t) v;
	return R_SUCCESS;
}

int readInt32(apr_socket_t *sock, apr_int32_t *res) {
	char buf[9];
	int len = 8;
	apr_status_t rv = apr_socket_recv(sock, buf, &len);
	if (rv == APR_EOF || len == 0) {
		return R_INVALID_MESSAGE;
	}

	buf[8] = 0; // Make sure buf can be treated as a string
	int r;
	RETURN(buf2Int32(buf, res), r)
	return R_SUCCESS;
}

int setCode(message_t *message, char code) {
	if (strchr("PSGA", code) == NULL)
		return R_INVALID_CODE;
	message->code = (code_t) code;
	return R_SUCCESS;
}

void createField(apr_pool_t *pool, field_t **field) {
	*field = apr_palloc(pool, sizeof(field_t));
	(*field)->encoding = HEX;
	(*field)->length = 0;
}

int readRequest(apr_socket_t *sock, message_t **message, apr_pool_t *pool) {
	// Allocate message
	*message = apr_palloc(pool, sizeof(message_t));

	// Read version
	char car;
	int len = 1;
	apr_status_t rv = apr_socket_recv(sock, &car, &len);
	if (rv == APR_EOF || len == 0) {
		return R_INVALID_MESSAGE;
	}
	if (car != 'a')
		return R_INVALID_VERSION;

	// Read message code
	rv = apr_socket_recv(sock, &car, &len);
	if (rv == APR_EOF || len == 0) {
		return R_INVALID_MESSAGE;
	}APR_RETURN(setCode(*message, car), rv, rv);

	apr_int32_t c;
	int r;
	field_t *f;
	switch (car) {
	case ACK:
		RETURN(readInt32(sock, &c), r)
		createField(pool, &f);
		f->int32Val = c;
		(*message)->nbFields = 1;
		(*message)->fields = f;
		break;
	case PING:
	case SHUTTING_DOWN:
	case SHUTDOWN:
		// Nothing more expected
		break;
	}

	printMessage(*message);

	return R_SUCCESS;
}

int writeResponse(apr_socket_t *sock, message_t *message) {
	const char *buf = "OK";
	printf("writing [%s]\n", buf);

	int len = strlen(buf);
	apr_socket_send(sock, buf, &len);

	return 0;
}
