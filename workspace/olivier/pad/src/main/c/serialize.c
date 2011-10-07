#include <stdio.h>

#include "apr_pools.h"
#include "serialize.h"

void printMessage(message_t *message) {
	printf("message code %c\n", message->code);
}

int setCode(message_t *message, char code) {
	if(strchr("PSGA", code) == NULL)
		return R_INVALID_CODE;
	message->code = (code_t) code;
	return R_SUCCESS;
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
	}
	rv = setCode(*message, car);
	if(rv != R_SUCCESS) return rv;

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
