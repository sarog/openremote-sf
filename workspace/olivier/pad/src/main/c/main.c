#include <stdio.h>

#include "apr_general.h"

#include "padConfig.h"
#include "server.h"
#include "serialize.h"

void main(int argc, const char *argv[]) {
  printf("%s version %d.%d\n", argv[0], pad_VERSION_MAJOR, pad_VERSION_MINOR);

  apr_status_t st = apr_initialize();
  printf("apr_initialize result : %d\n", st);

  runServer();
  apr_terminate();
}
