#!/bin/sh

mkdir output
cp win32/libapr-1.dll output
gcc src/iodaemon.c -Llib -llibapr-1 -o output/iodaemon.exe -Iinclude/apr

