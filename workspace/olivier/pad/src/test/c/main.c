#include <stdio.h>

#include "apr_general.h"
#include "codes.h"
#include "test.h"
#include "serialize.h"
#include "portManager.h"

void testBuf2Int32() {
	apr_int32_t v;
	int r = buf2Int32("00000001", &v);
	ASSERT(r, R_SUCCESS);
	ASSERT(v, 1);
	r = buf2Int32("FFFFFFFF", &v);
	ASSERT(r, R_SUCCESS);
	ASSERT(v, -1);
	r = buf2Int32("GFFFFFFF", &v);
	ASSERT(r, R_INVALID_MESSAGE);
	r = buf2Int32("FFFFFFF", &v);
	ASSERT(r, R_INVALID_MESSAGE);
}

void testBuf2Uint16() {
	apr_uint16_t v;
	int r = buf2Uint16("GFFF", &v);
	ASSERT(r, R_INVALID_MESSAGE);
	r = buf2Uint16("FFF", &v);
	ASSERT(r, R_INVALID_MESSAGE);
}

void testCreatePort() {
	int r;
	port_t *port;
	initPortManager();
	r = createPort("foo", "bar");
	ASSERT(r, R_SUCCESS);
	r = getPort("foo", &port);
	ASSERT(r, R_SUCCESS)
	ASSERT(strcmp(port->portId, "foo"), 0)
	ASSERT(strcmp(port->portType, "bar"), 0)
	ASSERT((int)port->lockSource, 0)
	ASSERT((int)port->configuration, 0)
}

void main(int argc, const char *argv[]) {
	apr_status_t st = apr_initialize();
	ASSERT(st, APR_SUCCESS)
	testBuf2Int32();
	testBuf2Uint16();
	testCreatePort();
	printf("\n");
}
