#!/bin/sh

if [ ! -d "./output" ]
then 
	mkdir ./output
	mkdir ./output/macosx
fi

if [ ! -d "./output/macosx" ]
then
	mkdir ./output/macosx
fi

cp ../macosx/apr-1.3.3/lib/libapr-1.dylib output/macosx


echo OpenRemote I/O Daemon Build for Mac OSX
echo --------------------------------------------
echo Compiling...

gcc ../src/iodaemon.c -L../macosx/apr-1.3.3/lib -lapr-1 -o output/macosx/iodaemon-1.0.0 -I../macosx/apr-1.3.3/include -I../include

echo
echo DONE.
echo
echo See ./output/macosx for distributables
echo --------------------------------------------

