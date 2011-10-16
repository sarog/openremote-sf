#ifndef _PORT_H
#define _PORT_H

typedef struct _port_t {
	char *portId;
	char *portType;
	void *configuration;
	int lockStatus;
} port_t;

int lock(port_t *port);
int unlock(port_t *port);

#endif
