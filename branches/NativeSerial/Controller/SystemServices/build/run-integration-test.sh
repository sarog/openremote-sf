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
#  Runs ANT build for running Java based integration tests.
#
#    - sets an explicit ANT_HOME to the Ant distribution in 'Tools' directory
#    - defines additional external library classpaths to run Ant
#
#
#  Notice that Java is just the language used for the test battery to test the
#  integration level (socket level) use of OR native I/O daemon -- that is, 
#  it *does not* test the Java API and bindings built on top of the socket level 
#  integration on the "JavaServices" side of the equation. In practice this means
#  that if any changes are made to the tests run by this script then the 
#  corresponding tests on the "JavaServices" side must also be run against the
#  final executable to guarantee full integration.
#
#  Author: Juha Lindfors
#  Revision: $Id: $
##

export ANT_HOME=../../../Tools/apache-ant-1.7.0
export PATH="$PATH:$ANT_HOME/lib/native"
$ANT_HOME/bin/ant -lib $ANT_HOME/lib/external "$@"
