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
link_directories("${APR_PATH}/lib")


# add the executable
add_library(pa socket.c server.c client.c serialize.c portManager.c port.c serialPort${CMAKE_SYSTEM_NAME}.c)
add_executable(pad main.c socket.c server.c client.c serialize.c serialPort${CMAKE_SYSTEM_NAME}.c portManager.c port.c)
target_link_libraries(pad ${APR_LIB})
if (${CMAKE_SYSTEM_NAME} STREQUAL Linux)
target_link_libraries(pad pthread)
elseif (${CMAKE_SYSTEM_NAME} STREQUAL Windows)
target_link_libraries(pad ws2_32.lib wsock32.lib)
endif (${CMAKE_SYSTEM_NAME} STREQUAL Linux)

# install
set (CMAKE_INSTALL_PREFIX /media/ntfs/ogandit/src/pad)
install (TARGETS pad DESTINATION bin)
install (FILES padConfig.h DESTINATION include)
