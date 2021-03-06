#ifndef _CODES_H
#define _CODES_H

// Function return codes
#define R_SUCCESS              0
#define R_INVALID_CODE        -1
#define R_INVALID_MESSAGE     -2
#define R_INVALID_VERSION     -3
#define R_NO_SUCH_PORT        -4
#define R_PORT_EXISTS         -5
#define R_WRONG_LOCK_STATUS   -6
#define R_UNLOCKED            -7
#define R_TX_RUNNING          -8
#define R_TX_NOT_FOUND        -9
#define R_INTERN_ERROR       -10
#define R_UNEXPECTED_RESP    -11
#define R_SHUTDOWN_REQUESTED -12

// Linux specific
#define R_PORT_ERROR        -100
#define R_SEND_ERROR        -101
#define R_CONFIGURE_ERROR   -102

// ACK codes
typedef enum _ackCodes_t {
	ackOk = 0, ackErr = 1
} ackCode_t;

#define CHECK(call)              {int r = call; if(r != R_SUCCESS) return r;}
#define APR_CHECK(call, ret)     {apr_status_t rv = call; if(rv != APR_SUCCESS) return ret;}

#endif
