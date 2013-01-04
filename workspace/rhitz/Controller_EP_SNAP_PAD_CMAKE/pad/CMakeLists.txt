cmake_minimum_required (VERSION 2.6)
project (pad-all)

# Directories
if (${CMAKE_SYSTEM_NAME} STREQUAL Windows)
  set (APR_PATH "${CMAKE_CURRENT_SOURCE_DIR}/apr-1.4.5/win32-x86")
  set (APR_LIB "apr-1")
elseif (${CMAKE_SYSTEM_NAME} STREQUAL Linux)
  set (APR_PATH "${CMAKE_CURRENT_SOURCE_DIR}/apr-1.4.6")
  set (APR_LIB "apr-1")
elseif (${CMAKE_SYSTEM_NAME} STREQUAL Darwin)
  set (APR_PATH "${CMAKE_CURRENT_SOURCE_DIR}/apr-1.4.6")
  set (APR_LIB "apr-1.a")
endif (${CMAKE_SYSTEM_NAME} STREQUAL Windows)

# The version number.
set (pad_VERSION_MAJOR 1)
set (pad_VERSION_MINOR 0)

# On 32bit system (eg Raspberry Pi) set extra compiler flag
if(CMAKE_SIZEOF_VOID_P EQUAL 4)
  set (CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -D_LARGEFILE64_SOURCE")
endif()

add_subdirectory (src/main/c)
add_subdirectory (src/test/c)
