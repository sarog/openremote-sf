#include <stddef.h>
#include "codes.h"
#include "client.h"

int createClientTransaction(apr_pool_t *pool, clientTransaction_t **tx) {
	if (*tx != NULL)
		return R_TX_RUNNING;
	*tx = apr_palloc(pool, sizeof(clientTransaction_t));
	(*tx)->request = NULL;
	(*tx)->response = NULL;
	return R_SUCCESS;
}

void clearClientTransaction(apr_pool_t *pool, clientTransaction_t **tx) {
	apr_pool_clear(pool);
	*tx = NULL;
}

int operatePortData(clientTransaction_t *tx, apr_pool_t *txPool, char *portId, char *buf, int len) {
	// Create notify message to send to client
	CHECK(createNotify(txPool, &tx->request, portId, buf, len))
	return R_SUCCESS;
}

int operateResponse(apr_socket_t *sock, clientTransaction_t *tx, apr_pool_t *txPool, char code) {
	CHECK(readBody(sock, &tx->response, txPool, code))

	switch (tx->response->code) {
	case ACK:
		// TODO check ACK code
		break;
	default:
		return R_UNEXPECTED_RESP;
	}

	return R_SUCCESS;
}
