#include "codes.h"
#include "client.h"

int operatePortData(apr_socket_t *sock, transaction_t *tx, apr_pool_t *txPool, char *portId, char *buf, int len) {
	// Create notify message to send to client
	CHECK(createNotify(txPool, &tx->request, portId, buf, len))
	return R_SUCCESS;
}
