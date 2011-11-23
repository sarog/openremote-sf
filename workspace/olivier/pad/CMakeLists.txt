cmake_minimum_required (VERSION 2.6)
project (pad-all)

# Directories
set (APR_PATH "C:/Users/Olivier Gandit/Documents/src/apr-1.4.5")

# The version number.
set (pad_VERSION_MAJOR 1)
set (pad_VERSION_MINOR 0)
  
add_subdirectory (src/main/c)
add_subdirectory (src/test/c)
