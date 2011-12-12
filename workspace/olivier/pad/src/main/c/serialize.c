#include <stdio.h>

#define APR_DECLARE_STATIC
#include "apr_pools.h"
#include "apr_strings.h"

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

int int162Buf(char *buf, apr_int16_t val) {
	apr_snprintf(buf, 5, "%04X", val);
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

int fillStringField(apr_pool_t *pool, field_t *field, char *buf, int len) {
	field->length = len;
	field->stringVal = apr_palloc(pool, len);
	memcpy(field->stringVal, buf, len);
	return R_SUCCESS;
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

int createACK(apr_pool_t *pool, message_t **message, ackCode_t code) {
	CHECK(createMessage(ACK, message, pool))
	createMessageFields(pool, *message, 1);
	(*message)->fields[0].int32Val = code;
	return R_SUCCESS;
}

int createNotify(apr_pool_t *pool, message_t **message, char *portId, char *buf, int len) {
	CHECK(createMessage(NOTIFY, message, pool))
	createMessageFields(pool, *message, 2);
	CHECK( fillStringField(pool, &(*message)->fields[0], portId, strlen(portId)));
	CHECK(fillStringField(pool, &(*message)->fields[1], buf, len));
	return R_SUCCESS;
}

int receiveStringBuf(apr_socket_t *sock, char *buf, int len) {
	apr_size_t l = len - 1;
	apr_status_t rv = apr_socket_recv(sock, buf, &l);
	if (rv == APR_EOF || l != len - 1) {
		return R_INVALID_MESSAGE;
	}
	buf[l] = 0; // Make sure buf can be treated as a string
	return R_SUCCESS;
}

int readInt32(apr_socket_t *sock, field_t *field) {
	char buf[9];
	CHECK(receiveStringBuf(sock, buf, 9));
	CHECK(buf2Int32(buf, &field->int32Val));
	return R_SUCCESS;
}

int writeInt32(apr_socket_t *sock, field_t *field) {
	char buf[9];
	apr_size_t len = 8;
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

int writeFieldLength(apr_socket_t *sock, apr_uint16_t fieldLength) {
	char buf[4];
	apr_size_t len = 4;
	CHECK(int162Buf(buf, fieldLength))
	apr_socket_send(sock, buf, &len);
	// TODO check len and return value
	return R_SUCCESS;
}

int readString(apr_pool_t *pool, apr_socket_t *sock, field_t *field) {
	CHECK(readFieldLength(sock, &field->length))
	field->stringVal = apr_palloc(pool, field->length + 1);
	CHECK(receiveStringBuf(sock, field->stringVal, field->length + 1))
	return R_SUCCESS;
}

int writeString(apr_socket_t *sock, field_t *field) {
	apr_size_t len = field->length;
	CHECK(writeFieldLength(sock, len));
	apr_socket_send(sock, field->stringVal, &len);
	// TODO check len and return value
	return R_SUCCESS;
}

int writeOctetString(apr_socket_t *sock, field_t *field) {
	int i;
	apr_size_t len = 2 * field->length;
	char tmp[1024];
	writeFieldLength(sock, len);
	for (i = 0; i < field->length; ++i) {
		apr_snprintf(&tmp[2 * i], 3, "%02X", field->stringVal[i] & 0xFF);
	}
	apr_socket_send(sock, tmp, &len);
	// TODO check len and return value
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
	apr_size_t len;
	apr_status_t rv;

	// Read version
	len = 1;
	rv = apr_socket_recv(sock, &car, &len);
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
		field_t portNameField, nbParamsField;
		apr_int32_t i;
		CHECK(readString(pool, sock, &portNameField));
		CHECK(readInt32(sock, &nbParamsField));
		createMessageFields(pool, *message, nbParamsField.int32Val * 2 + 2);
		(*message)->fields[0].length = portNameField.length;
		(*message)->fields[0].stringVal = portNameField.stringVal;
		(*message)->fields[1].length = nbParamsField.length;
		(*message)->fields[1].int32Val = nbParamsField.int32Val;
		for (i = 0; i < nbParamsField.int32Val; ++i) {
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
	apr_size_t len = 2;
	buf[1] = message->code;
	apr_socket_send(sock, buf, &len);
	return R_SUCCESS;
}
