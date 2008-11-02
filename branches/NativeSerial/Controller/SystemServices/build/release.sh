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
# Simple script to copy the built executables to their correct location
# in JavaServices runtime.
#
#  Author: Juha Lindfors
#  Revision: $Id: $
##

version=1.0.0

echo "--------------------------------------------------"
echo " Copying files to JavaServices/runtime..."
echo ""

cygwin_target=../../JavaServices/runtime/server/controller/system/services/io-proxy.service/native/cygwin
macosx_target=../../JavaServices/runtime/server/controller/system/services/io-proxy.service/native/macosx


if [ -d "./output/cygwin" ]
then
	echo " Cygwin:"
	echo ""

	cp output/cygwin/iodaemon-$version.exe $cygwin_target
	cp ../cygwin/apr-1.2.2/LICENSE $cygwin_target
	cp ../cygwin/apr-1.2.2/NOTICE $cygwin_target

	echo " Done."
fi

if [ -d "./output/macosx" ]
then
	echo " Mac OS X:"
	echo ""

	executable_name=iodaemon-$(echo $version)

	cp ./output/macosx/$executable_name $macosx_target
	cp ../macosx/apr-1.3.3/LICENSE $macosx_target
	cp ../macosx/apr-1.3.3/NOTICE $macosx_target

	echo " Done."
fi

echo "----------------------------------------------------"


