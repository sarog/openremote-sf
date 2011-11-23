project (pad)
 
# configure a header file to pass some of the CMake settings
# to the source code
configure_file (
  "${PROJECT_SOURCE_DIR}/padConfig.h.in"
  "${PROJECT_BINARY_DIR}/padConfig.h"
  )

# add the binary tree to the search path for include files
# so that we will find padConfig.h
include_directories("${PROJECT_BINARY_DIR}")
include_directories("${APR_PATH}/include")
 
# external libraries
add_library(apr-1 STATIC IMPORTED)
set_property(TARGET apr-1 PROPERTY IMPORTED_LOCATION ${APR_PATH}/.libs/libapr-1.a)
#add_library(pthread MODULE IMPORTED)

# add the executable
add_library(pa socket.c serialize.c portManager.c port.c linuxSerialPort.c)
add_executable(pad main.c socket.c server.c client.c serialize.c linuxSerialPort.c portManager.c port.c)
target_link_libraries(pad apr-1)
target_link_libraries(pad pthread)

# install
set (CMAKE_INSTALL_PREFIX /media/ntfs/ogandit/src/pad)
install (TARGETS pad DESTINATION bin)
install (FILES padConfig.h DESTINATION include)
