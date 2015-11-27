#ifndef _PORTMANAGER_H
#define _PORTMANAGER_H

#include "codes.h"
#include "port.h"

int initPortManager();
int createPort(char *portId, char *portType);
int getPort(char *portId, port_t **port);

#endif
