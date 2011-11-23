#include <stdlib.h>
#ifdef LINUX
#include <execinfo.h>
#endif
#include <stdio.h>
#include <string.h>

void printStackTrace(void) {
#ifdef LINUX
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
#else
	printf("No backtrace\n");
#endif
}

void assertIntsEqual(char *file, int line, const int testValue, const int expectedValue) {
	printf(".");
	if (testValue != expectedValue) {
		printf("\nAt %s:%d, expected %d, got %d\n", file, line, expectedValue, testValue);
		printStackTrace();
		exit(-1);
	}
}

void assertStringsEqual(char *file, int line, const char *testValue, const char *expectedValue) {
	printf(".");
	if (strcmp(testValue, expectedValue)) {
		printf("\nAt %s:%d, expected '%s', got '%s'\n", file, line, expectedValue, testValue);
		printStackTrace();
		exit(-1);
	}
}
