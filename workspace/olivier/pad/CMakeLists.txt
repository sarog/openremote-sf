cmake_minimum_required (VERSION 2.6)
project (pad-all)

# Directories
if (${CMAKE_SYSTEM_NAME} STREQUAL Windows)
set (APR_PATH "C:/Users/Olivier Gandit/Documents/src/apr-1.4.5")
set (APR_LIB "${APR_PATH}/Release/libapr-1.lib")
elseif (${CMAKE_SYSTEM_NAME} STREQUAL Linux)
set (APR_PATH "C:/Users/Olivier Gandit/Documents/src/apr-1.4.5")
set (APR_LIB "${APR_PATH}/.libs/libapr-1.a")
elseif (${CMAKE_SYSTEM_NAME} STREQUAL Darwin)
set (APR_PATH "/Users/marcus/_Projects/OpenRemote/PAD/pad/apr-1.4.5")
set (APR_LIB "${APR_PATH}/.libs/libapr-1.a")
# include_directories("${APR_PATH}/include")
endif (${CMAKE_SYSTEM_NAME} STREQUAL Windows)

# The version number.
set (pad_VERSION_MAJOR 1)
set (pad_VERSION_MINOR 0)
  
add_subdirectory (src/main/c)
add_subdirectory (src/test/c)
