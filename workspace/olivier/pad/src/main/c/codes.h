#ifndef _CODES_H
#define _CODES_H

#define R_SUCCESS            0
#define R_INVALID_CODE      -1
#define R_INVALID_MESSAGE   -2
#define R_INVALID_VERSION   -3
#define R_NO_SUCH_PORT      -4
#define R_PORT_EXISTS       -5
#define R_WRONG_LOCK_STATUS -6

#define RETURN_IF(call, r)              r = call; if(r != R_SUCCESS) return r;
#define APR_RETURN_IF(call, rv, ret)    rv = call; if(rv != APR_SUCCESS) return ret;

#endif
