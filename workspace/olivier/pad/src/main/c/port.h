#ifndef _PORT_H
#define _PORT_H

typedef struct _port_t {
	char *portId;
	char *portType;
	void *configuration;
	char *lockSource;
} port_t;

int lock(port_t *port, char *source);
int unlock(port_t *port, char *source);

#endif
