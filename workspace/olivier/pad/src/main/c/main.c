#include <stdio.h>

#include "apr_general.h"

#include "padConfig.h"
#include "socket.h"
#include "portManager.h"

void main(int argc, const char *argv[]) {
  printf("pad version %d.%d\n", pad_VERSION_MAJOR, pad_VERSION_MINOR);

  apr_initialize();

  initPortManager();

  runServer();

  apr_terminate();
}
