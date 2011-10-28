#include <stdio.h>

#include "apr_general.h"

#include "padConfig.h"
#include "socket.h"
#include "portManager.h"

void main(int argc, const char *argv[]) {
  printf("%s version %d.%d\n", argv[0], pad_VERSION_MAJOR, pad_VERSION_MINOR);

  apr_status_t st = apr_initialize();

  initPortManager();

  runServer();

  apr_terminate();
}
