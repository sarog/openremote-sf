#include <stdlib.h>
#include <execinfo.h>
#include <stdio.h>

void printStackTrace(void) {
	void *array[100];
	int size;
	char **strings;
	int i;

	size = backtrace(array, 10);
	strings = backtrace_symbols(array, size);
	printf("------------------------\n");
	for (i = 1; i < size; i++)
		printf("%s\n", strings[i]);
	printf("------------------------\n");
	free(strings);
}

void assertEquals(char *file, int line, int testValue, int expectedValue) {
	printf(".");
	if (testValue != expectedValue) {
		printf("\nAt %s:%d, expected %d, got %d\n", file, line, expectedValue, testValue);
		printStackTrace();
		exit(-1);
	}
}
