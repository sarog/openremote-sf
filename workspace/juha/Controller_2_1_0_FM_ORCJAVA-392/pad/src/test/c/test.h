#ifndef _TEST_H
#define _TEST_H

#define ASSERT_INTS_EQUAL(t, e)    assertIntsEqual(__FILE__, __LINE__, t, e)
#define ASSERT_STRINGS_EQUAL(t, e) assertStringsEqual(__FILE__, __LINE__, t, e)

void assertIntsEqual(char *file, const int line, const int testValue, int expectedValue);
void assertStringsEqual(char *file, int line, const char *testValue, const char *expectedValue);

#endif
