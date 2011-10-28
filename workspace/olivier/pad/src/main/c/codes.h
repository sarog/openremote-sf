#ifndef _CODES_H
#define _CODES_H

// Function return codes
#define R_SUCCESS            0
#define R_INVALID_CODE      -1
#define R_INVALID_MESSAGE   -2
#define R_INVALID_VERSION   -3
#define R_NO_SUCH_PORT      -4
#define R_PORT_EXISTS       -5
#define R_WRONG_LOCK_STATUS -6
#define R_UNLOCKED          -7
// Linux specific
#define R_PORT_ERROR        -100
#define R_SEND_ERROR        -101
#define R_CONFIGURE_ERROR   -102

// ACK codes
#define ACK_OK               0

#define CHECK(call)              {int r = call; if(r != R_SUCCESS) return r;}
#define APR_CHECK(call, ret)     {apr_status_t rv = call; if(rv != APR_SUCCESS) return ret;}

#endif
