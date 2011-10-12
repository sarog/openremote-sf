#define ASSERT(t, e) assertEquals(__FILE__, __LINE__, t, e);

void assertEquals(char *file, int line, int testValue, int expectedValue);
