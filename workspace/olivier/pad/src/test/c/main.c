#include <stdio.h>

#include "apr_general.h"
#include "test.h"
#include "serialize.h"

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

void main(int argc, const char *argv[]) {
  testBuf2Int32();
  printf("\n");
}
