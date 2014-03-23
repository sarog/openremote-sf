#!/bin/sh

##
# OpenRemote, the Internet-enabled Home.
#
# This work is licensed under Creative Commons Attribution-Noncommercial-Share Alike 3.0
# United States, http://creativecommons.org/licenses/by-nc-sa/3.0/us/
##

##
#  Runs ANT build script.
#
#    - sets an explicit ANT_HOME to the Ant distribution in 'Tools' directory
#    - defines additional external library classpaths to run Ant
#
#
#  Author: Juha Lindfors
#  Revision: $Id: $
##

export ANT_HOME=../../../Tools/apache-ant-1.7.0
export PATH="$PATH:$ANT_HOME/lib/native"
$ANT_HOME/bin/ant -lib $ANT_HOME/lib/external "$@"
