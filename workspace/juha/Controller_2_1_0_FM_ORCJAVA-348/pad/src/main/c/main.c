#include <stdio.h>

#define APR_DECLARE_STATIC
#include "apr_general.h"

#include "padConfig.h"
#include "socket.h"
#include "portManager.h"

int main(int argc, const char *argv[]) {
  printf("pad version %d.%d\n", pad_VERSION_MAJOR, pad_VERSION_MINOR);

  apr_initialize();

  initPortManager();

  runServer();

  apr_terminate();
	return 0;
}
