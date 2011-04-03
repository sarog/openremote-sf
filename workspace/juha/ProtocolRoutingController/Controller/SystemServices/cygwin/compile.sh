#!/bin/sh

mkdir output
cp win32/libapr-1.dll output
gcc src/iodaemon.c -DWIN32 -Llib -llibapr-1 -o output/iodaemon-1.0.0.exe -Iinclude/apr

