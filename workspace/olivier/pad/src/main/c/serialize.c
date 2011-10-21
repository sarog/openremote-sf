#include <stdio.h>

#include "apr_pools.h"

#include "codes.h"
#include "serialize.h"

void printMessage(message_t *message) {
	int i;
	printf("message code %c", message->code);
	switch (message->code) {
	case PING:
		printf(" (PING)");
		break;
	case SHUTDOWN:
		printf(" (SHUTDOWN)");
		break;
	case ACK:
		printf(" (ACK), code=0x%X", message->fields[0].int32Val);
		break;
	case NOTIFY:
		printf(" (NOTIFY), portId='%s', content='%s'", message->fields[0].stringVal, message->fields[1].stringVal);
		break;
	case LOCK:
		printf(" (LOCK), portId='%s', sourceId='%s'", message->fields[0].stringVal, message->fields[1].stringVal);
		break;
	case UNLOCK:
		printf(" (UNLOCK), portId='%s', sourceId='%s'", message->fields[0].stringVal, message->fields[1].stringVal);
		break;
	case CREATE_PORT:
		printf(" (CREATE_PORT), portId='%s', portType='%s'", message->fields[0].stringVal, message->fields[1].stringVal);
		break;
	case CONFIGURE:
		printf(" (CONFIGURE), portId='%s'", message->fields[0].stringVal);
		for (i = 0; i < message->fields[1].int32Val; ++i) {
			printf("\n\t%s=%s", message->fields[2 + i * 2].stringVal, message->fields[3 + i * 2].stringVal);
		}
		break;
	default:
		printf(" unknown");
		break;
	}
	printf("\n");
}

int buf2Uint16(const char *buf, apr_uint16_t *res) {
	apr_off_t v;
	apr_status_t rv;
	char *end;
	APR_RETURN_IF(apr_strtoff(&v, buf, &end, 16), rv, R_INVALID_MESSAGE)
	if (*end != 0 || end - buf != 4)
		return R_INVALID_MESSAGE;
	*res = (apr_uint16_t) v;
	return R_SUCCESS;
}

int buf2Int32(const char *buf, apr_int32_t *res) {
	apr_off_t v;
	apr_status_t rv;
	char *end;
	APR_RETURN_IF(apr_strtoff(&v, buf, &end, 16), rv, R_INVALID_MESSAGE)
	if (*end != 0 || end - buf != 8)
		return R_INVALID_MESSAGE;
	*res = (apr_int32_t) v;
	return R_SUCCESS;
}

int receiveStringBuf(apr_socket_t *sock, char *buf, int len) {
	int l = len - 1;
	apr_status_t rv = apr_socket_recv(sock, buf, &l);
	if (rv == APR_EOF || l != len - 1) {
		return R_INVALID_MESSAGE;
	}
	buf[l] = 0; // Make sure buf can be treated as a string
	return R_SUCCESS;
}

int readInt32(apr_socket_t *sock, field_t *field) {
	char buf[9];
	int r;
	RETURN_IF(receiveStringBuf(sock, buf, 9), r);
	RETURN_IF(buf2Int32(buf, &field->int32Val), r)
	return R_SUCCESS;
}

int readFieldLength(apr_socket_t *sock, apr_uint16_t *fieldLength) {
	char buf[5];
	int r;
	RETURN_IF(receiveStringBuf(sock, buf, 5), r);
	RETURN_IF(buf2Uint16(buf, fieldLength), r);
	return R_SUCCESS;
}

int readString(apr_pool_t *pool, apr_socket_t *sock, field_t *field) {
	int r;
	apr_uint16_t fieldLength;
	RETURN_IF(readFieldLength(sock, &fieldLength), r)
	field->stringVal = apr_palloc(pool, fieldLength + 1);
	RETURN_IF(receiveStringBuf(sock, field->stringVal, fieldLength + 1), r)
	return R_SUCCESS;
}

int checkCode(char code) {
	if (strchr("PSANCLUO", code) == NULL)
		return R_INVALID_CODE;
	return R_SUCCESS;
}

int setCode(message_t *message, char code) {
	message->code = (code_t) code;
	return R_SUCCESS;
}

void createMessageFields(apr_pool_t *pool, message_t *message, int nbFields) {
	int i;
	message->nbFields = nbFields;
	message->fields = apr_palloc(pool, sizeof(field_t) * nbFields);
	for (i = 0; i < nbFields; ++i) {
		message->fields[i].encoding = HEX;
		message->fields[i].length = 0;
	}
}

int readHeader(apr_socket_t *sock, char *code) {
	char car;
	int len;

	// Read version
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
	}APR_RETURN_IF(checkCode(car), rv, rv);
	*code = car;
	return R_SUCCESS;
}

int readBody(apr_socket_t *sock, message_t **message, apr_pool_t *pool, char code) {
	int r; // Return value
	field_t f1, f2;
	int i;

	// Allocate message
	*message = apr_palloc(pool, sizeof(message_t));

	// Set code
	(*message)->code = code;

	// Read message fields
	switch (code) {
	case PING:
	case SHUTDOWN:
		// Nothing more expected
		break;
	case ACK:
		createMessageFields(pool, *message, 1);
		RETURN_IF(readInt32(sock, &(*message)->fields[0]), r)
		break;
	case NOTIFY:
	case LOCK:
	case UNLOCK:
	case CREATE_PORT:
		createMessageFields(pool, *message, 2);
		RETURN_IF(readString(pool, sock, &(*message)->fields[0]), r)
		RETURN_IF(readString(pool, sock, &(*message)->fields[1]), r)
		break;
	case CONFIGURE:
		RETURN_IF(readString(pool, sock, &f1), r)
		RETURN_IF(readString(pool, sock, &f2), r)
		createMessageFields(pool, *message, f2.int32Val + 2);
		(*message)->fields[0].length = f1.length;
		(*message)->fields[0].stringVal = f1.stringVal;
		(*message)->fields[1].length = f2.length;
		(*message)->fields[1].int32Val = f2.int32Val;
		for (i = 0; i < f2.int32Val; ++i) {
			RETURN_IF(readString(pool, sock, &(*message)->fields[2 + (i * 2)]), r)RETURN_IF(
					readString(pool, sock, &(*message)->fields[3 + (i * 2)]), r)
		}
		break;
	default:
		return R_INVALID_MESSAGE;
	}

	printMessage(*message);

	return R_SUCCESS;
}

int writeMessage(apr_socket_t *sock, message_t *message) {
	const char *buf = "OK";
	printf("writing [%s]\n", buf);

	int len = strlen(buf);
	apr_socket_send(sock, buf, &len);

	return 0;
}
