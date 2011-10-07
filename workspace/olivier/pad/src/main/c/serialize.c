#include "serialize.h"

int readRequest(apr_socket_t *sock) {
	char buf[2];
	apr_size_t len = sizeof(buf);

	apr_status_t rv = apr_socket_recv(sock, buf, &len);
	if (rv == APR_EOF || len == 0) {
		return -1;
	}

	printf("reading [%s]\n", (char *) buf);
	return 0;
}

int writeResponse(apr_socket_t *sock) {
	const char *buf = "OK";
	printf("writing [%s]\n", buf);

  int len = strlen(buf);
  apr_socket_send(sock, buf, &len);

	return 0;
}
