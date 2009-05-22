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
# Script to compile MS Windows executable on Cygwin... yes, makefile to follow,
# it's just a single C file for now.
#
#  Author: Juha Lindfors
#  Revision: $Id: $
##


apr_version=1.2.12
daemon_version=1.0.0

if [ ! -d "./output" ]
then
	mkdir ./output
fi

if [ ! -d "./output/cygwin" ]
then 
	mkdir ./output/cygwin
fi


cp ../cygwin/apr-$(echo $apr_version)/win32/libapr-1.dll ./output/cygwin

gcc ../src/IODaemon.c ../src/Serial.c ../src/SerialProtocolHandler.c ../src/IOProtocolHandler.c ../src/ControlProtocolHandler.c \
	-DWIN32      \
	-L../cygwin/apr-$apr_version/lib \
	-llibapr-1 -lcrypt   \
	-o output/cygwin/iodaemon-$daemon_version.exe \
	-I../cygwin/apr-$apr_version/include \
	-I../include \
        -Wall

