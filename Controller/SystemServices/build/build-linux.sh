#!/bin/sh

###############################################################################
## OpenRemote, the Home of the Digital Home.
## Copyright 2008, OpenRemote Inc.
##
## See the contributors.txt file in the distribution for a
## full listing of individual contributors.
##
## This is free software; you can redistribute it and/or modify it
## under the terms of the GNU General Public License as
## published by the Free Software Foundation; either version 3.0 of
## the License, or (at your option) any later version.
##
## This software is distributed in the hope that it will be useful,
## but WITHOUT ANY WARRANTY; without even the implied warranty of
## MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
##
## You should have received a copy of the GNU General Public
## License along with this software; if not, write to the Free
## Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
## 02110-1301 USA, or see the FSF site: http://www.fsf.org.
###############################################################################

##
# Script to compile the Linux executable... yes, makefile to follow, 
# it's just a single C file for now.
#
#  Author: Juha Lindfors
#  Revision: $Id: $
##

if [ ! -d "./output" ]
then 
	mkdir ./output
fi

if [ ! -d "./output/linux" ]
then
	mkdir ./output/linux
fi

cp ../linux/apr-1.3.3/lib/libapr-1.so output/linux


echo OpenRemote I/O Daemon Build for Linux
echo --------------------------------------------
echo Compiling...

cflags=$(../linux/apr-1.3.3/bin/apr-1-config --cflags)
cppflags=$(../linux/apr-1.3.3/bin/apr-1-config --cppflags)

gcc $cflags $cppflags  \
    ../src/IODaemon.c ../src/SerialProtocolHandler.c ../src/IOProtocolHandler.c ../src/ControlProtocolHandler.c \
    -L../linux/apr-1.3.3/lib 	\
    -lapr-1 			\
    -o output/linux/iodaemon-1.0.0 \
    -I../linux/apr-1.3.3/include   \
    -I../include \
    -Wall

echo
echo DONE.
echo
echo See ./output/linux for distributables
echo --------------------------------------------

