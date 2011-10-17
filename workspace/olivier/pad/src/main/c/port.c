#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "codes.h"
#include "port.h"

int lock(port_t *port, char *source) {
	if (port->lockSource != NULL)
		return R_WRONG_LOCK_STATUS;
	port->lockSource = malloc(strlen(source) + 1);
	strcpy(port->lockSource, source);
	printf("port '%s' locked by '%s'\n", port->portId, port->lockSource);
	return R_SUCCESS;
}

int unlock(port_t *port, char *source) {
	if (port->lockSource == NULL || strcmp(port->lockSource, source) != 0)
		return R_WRONG_LOCK_STATUS;
	free(port->lockSource);
	port->lockSource = NULL;
	printf("port '%s' unlocked by '%s' \n", port->portId, port->lockSource);
	return R_SUCCESS;
}
