#include <stdio.h>

#include "apr_pools.h"

#include "codes.h"
#include "serialize.h"

#define PROTOCOL_VERSION 'a'

int buf2Uint16(const char *buf, apr_uint16_t *res) {
	apr_off_t v;
	char *end;
	APR_CHECK(apr_strtoff(&v, buf, &end, 16), R_INVALID_MESSAGE)
	if (*end != 0 || end - buf != 4)
		return R_INVALID_MESSAGE;
	*res = (apr_uint16_t) v;
	return R_SUCCESS;
}

int buf2Int32(const char *buf, apr_int32_t *res) {
	apr_off_t v;
	char *end;
	APR_CHECK(apr_strtoff(&v, buf, &end, 16), R_INVALID_MESSAGE)
	if (*end != 0 || end - buf != 8)
		return R_INVALID_MESSAGE;
	*res = (apr_int32_t) v;
	return R_SUCCESS;
}

int int322Buf(char *buf, apr_int32_t val) {
	apr_snprintf(buf, 9, "%08X", val);
	return R_SUCCESS;
}

int createMessage(char code, message_t **message, apr_pool_t *pool) {
	*message = apr_palloc(pool, sizeof(message_t));
	(*message)->code = code;
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

int createACK(apr_pool_t *pool, message_t **message, apr_int32_t code) {
	CHECK(createMessage(ACK, message, pool))
	createMessageFields(pool, *message, 1);
	(*message)->fields[0].int32Val = code;
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
	CHECK(receiveStringBuf(sock, buf, 9))CHECK(buf2Int32(buf, &field->int32Val))
	return R_SUCCESS;
}

int writeInt32(apr_socket_t *sock, field_t *field) {
	char buf[8];
	int len = 8;
	CHECK(int322Buf(buf, field->int32Val))
	apr_socket_send(sock, buf, &len);
	return R_SUCCESS;
}

int readFieldLength(apr_socket_t *sock, apr_uint16_t *fieldLength) {
	char buf[5];
	CHECK(receiveStringBuf(sock, buf, 5));
	CHECK(buf2Uint16(buf, fieldLength));
	return R_SUCCESS;
}

int readString(apr_pool_t *pool, apr_socket_t *sock, field_t *field) {
	apr_uint16_t fieldLength;
	CHECK(readFieldLength(sock, &fieldLength))
	field->stringVal = apr_palloc(pool, fieldLength + 1);
	CHECK(receiveStringBuf(sock, field->stringVal, fieldLength + 1))
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

int readHeader(apr_socket_t *sock, char *code) {
	char car;
	int len;

	// Read version
	len = 1;
	apr_status_t rv = apr_socket_recv(sock, &car, &len);
	if (rv == APR_EOF || len == 0) {
		return R_INVALID_MESSAGE;
	}
	if (car != PROTOCOL_VERSION)
		return R_INVALID_VERSION;

	// Read message code
	len = 1;
	rv = apr_socket_recv(sock, &car, &len);
	if (rv == APR_EOF || len == 0) {
		return R_INVALID_MESSAGE;
	}

	// Check code is valid
	APR_CHECK(checkCode(car), rv)
	*code = car;
	return R_SUCCESS;
}

int readBody(apr_socket_t *sock, message_t **message, apr_pool_t *pool, char code) {
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
		CHECK(readInt32(sock, &(*message)->fields[0]))
		break;
	case NOTIFY:
	case LOCK:
	case UNLOCK:
	case CREATE_PORT:
		createMessageFields(pool, *message, 2);
		CHECK(readString(pool, sock, &(*message)->fields[0]))
		CHECK(readString(pool, sock, &(*message)->fields[1]))
		break;
	case CONFIGURE: {
		field_t f1, f2;
		int i;
		CHECK(readString(pool, sock, &f1))CHECK(readString(pool, sock, &f2))
		createMessageFields(pool, *message, f2.int32Val + 2);
		(*message)->fields[0].length = f1.length;
		(*message)->fields[0].stringVal = f1.stringVal;
		(*message)->fields[1].length = f2.length;
		(*message)->fields[1].int32Val = f2.int32Val;
		for (i = 0; i < f2.int32Val; ++i) {
			CHECK(readString(pool, sock, &(*message)->fields[2 + (i * 2)]))
			CHECK(readString(pool, sock, &(*message)->fields[3 + (i * 2)]))
		}
	}
		break;
	default:
		return R_INVALID_MESSAGE;
	}

	printMessage(*message);

	return R_SUCCESS;
}

int writeHeader(apr_socket_t *sock, message_t *message) {
	char buf[2] = { 'a', 0 };
	int len = 2;
	buf[1] = message->code;
	apr_socket_send(sock, buf, &len);
	return R_SUCCESS;
}
