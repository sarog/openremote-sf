#!/bin/sh

mkdir output
cp apr-1.3.3/lib/libapr-1.dylib output
gcc src/iodaemon.c -L./apr-1.3.3/lib -lapr-1 -o output/iodaemon-1.0.0 -Iapr-1.3.3/include/apr-1 -Iinclude

