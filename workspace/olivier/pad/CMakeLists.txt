cmake_minimum_required (VERSION 2.6)
project (pad-all)

# Directories
if (${CMAKE_SYSTEM_NAME} STREQUAL Windows)
set (APR_PATH "../../../apr-1.4.5/win32")
set (APR_LIB "${APR_PATH}/lib/apr-1")
elseif (${CMAKE_SYSTEM_NAME} STREQUAL Linux)
set (APR_PATH "/media/ntfs/ogandit/src/apr-1.4.5")
set (APR_LIB "${APR_PATH}/.libs/libapr-1.a")
elseif (${CMAKE_SYSTEM_NAME} STREQUAL Darwin)
set (APR_PATH "/Users/ogandit/Documents/src/apr-1.4.5")
set (APR_LIB "${APR_PATH}/.libs/libapr-1.a")
# include_directories("${APR_PATH}/include")
endif (${CMAKE_SYSTEM_NAME} STREQUAL Windows)

# The version number.
set (pad_VERSION_MAJOR 1)
set (pad_VERSION_MINOR 0)
  
add_subdirectory (src/main/c)
add_subdirectory (src/test/c)
