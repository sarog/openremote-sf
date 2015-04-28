project (padTest)
 
# add the binary tree to the search path for include files
# so that we will find padConfig.h
include_directories("${PROJECT_BINARY_DIR}")
include_directories(${APR_PATH}/include)
include_directories("../../main/c")
 
# external libraries
if (${CMAKE_SYSTEM_NAME} STREQUAL Windows)
  link_directories("${APR_PATH}/lib")
else (${CMAKE_SYSTEM_NAME} STREQUAL Windows)
  link_directories("${APR_PATH}/.libs")
endif (${CMAKE_SYSTEM_NAME} STREQUAL Windows)

# add the executable
add_executable(padTest main.c test.c)
target_link_libraries(padTest pa)
target_link_libraries(padTest ${APR_LIB})
if (${CMAKE_SYSTEM_NAME} STREQUAL Linux)
target_link_libraries(padTest pthread)
elseif (${CMAKE_SYSTEM_NAME} STREQUAL Windows)
target_link_libraries(padTest ws2_32.lib wsock32.lib)
endif (${CMAKE_SYSTEM_NAME} STREQUAL Linux)
