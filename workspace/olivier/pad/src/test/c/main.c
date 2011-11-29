#include <stdio.h>

#define APR_DECLARE_STATIC
#include "apr_general.h"

#include "codes.h"
#include "test.h"
#include "serialize.h"
#include "portManager.h"

void testBuf2Int32() {
	apr_int32_t v;
	int r = buf2Int32("00000001", &v);
	ASSERT_INTS_EQUAL(r, R_SUCCESS);
	ASSERT_INTS_EQUAL(v, 1);
	r = buf2Int32("FFFFFFFF", &v);
	ASSERT_INTS_EQUAL(r, R_SUCCESS);
	ASSERT_INTS_EQUAL(v, -1);
	r = buf2Int32("GFFFFFFF", &v);
	ASSERT_INTS_EQUAL(r, R_INVALID_MESSAGE);
	r = buf2Int32("FFFFFFF", &v);
	ASSERT_INTS_EQUAL(r, R_INVALID_MESSAGE);
}

void testInt322Buf() {
	char buf[9];
	buf[8] = 0;
	ASSERT_INTS_EQUAL(int322Buf(buf, 16), R_SUCCESS);
	ASSERT_STRINGS_EQUAL(buf, "00000010");
	ASSERT_INTS_EQUAL(int322Buf(buf, -1), R_SUCCESS);
	ASSERT_STRINGS_EQUAL(buf, "FFFFFFFF");
}

void testBuf2Uint16() {
	apr_uint16_t v;
	int r = buf2Uint16("GFFF", &v);
	ASSERT_INTS_EQUAL(r, R_INVALID_MESSAGE);
	r = buf2Uint16("FFF", &v);
	ASSERT_INTS_EQUAL(r, R_INVALID_MESSAGE);
}

void testCreatePort() {
	int r;
	port_t *port;
	initPortManager();
	r = createPort("foo", "bar");
	ASSERT_INTS_EQUAL(r, R_SUCCESS);
	r = getPort("foo", &port);
	ASSERT_INTS_EQUAL(r, R_SUCCESS);
	ASSERT_INTS_EQUAL(strcmp(port->portId, "foo"), 0);
	ASSERT_INTS_EQUAL(strcmp(port->portType, "bar"), 0);
	ASSERT_INTS_EQUAL((int)port->lockSource, 0);
	ASSERT_INTS_EQUAL((int)port->cfg, 0);
}

void main(int argc, const char *argv[]) {
	apr_status_t st = apr_initialize();
	ASSERT_INTS_EQUAL(st, APR_SUCCESS);
	testBuf2Int32();
	testInt322Buf();
	testBuf2Uint16();
	testCreatePort();
	printf("\n");
}
