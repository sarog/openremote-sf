#ifndef _TRANSACTION_H_
#define _TRANSACTION_H_

#include "serialize.h"
#include "osPort.h"  // TODO keep?

typedef struct _transaction_t {
	enum {
		WAITING_FOR_REQUEST, WAITING_FOR_RESPONSE
	} status;
	message_t *request;
	message_t *response;
	portReceive_t portReceiveCb;
} transaction_t;


#endif /* TRANSACTION_H_ */
