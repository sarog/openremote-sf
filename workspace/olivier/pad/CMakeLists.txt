cmake_minimum_required (VERSION 2.6)
project (pad-all)

# Directories
if (${CMAKE_SYSTEM_NAME} STREQUAL Windows)
set (APR_PATH "../../../apr-1.4.5/win32")
set (APR_LIB "${APR_PATH}/lib/apr-1")
elseif (${CMAKE_SYSTEM_NAME} STREQUAL Linux)
set (APR_PATH "${CMAKE_CURRENT_SOURCE_DIR}/apr-1.4.5/linux32-x86")
set (APR_LIB "apr-1")
elseif (${CMAKE_SYSTEM_NAME} STREQUAL Darwin)
set (APR_PATH "${CMAKE_CURRENT_SOURCE_DIR}/apr-1.4.5/darwin64-x86")
set (APR_LIB "apr-1")
endif (${CMAKE_SYSTEM_NAME} STREQUAL Windows)

# The version number.
set (pad_VERSION_MAJOR 1)
set (pad_VERSION_MINOR 0)
  
add_subdirectory (src/main/c)
add_subdirectory (src/test/c)
