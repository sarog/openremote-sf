#!/bin/sh

# OpenRemote, the Home of the Digital Home.
# Copyright 2008-2011, OpenRemote Inc.
#
# See the contributors.txt file in the distribution for a
# full listing of individual contributors.
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program. If not, see <http://www.gnu.org/licenses/>.


#
# Configuration

AGENT=org.openremote.controller.agent.BackendCommandsAgent
LIB_DIR=lib
PID_FILE=run/agent.pid
JAVA=java
CLASSPATH=$LIB_DIR

#
# Functions

usage () {
	echo "Usage: $0 (start|stop|status)"
	exit 1
}

build_classpath() {
	for j in $LIB_DIR/*.jar
	do
		CLASSPATH="$CLASSPATH:$j"
	done
}

is_agent_process(){
	local PID=$1
	if [ -f /proc/$PID/cmdline ] && grep $JAVA /proc/$PID/cmdline 2>&1 > /dev/null && grep agent.jar /proc/$PID/cmdline 2>&1 > /dev/null
	then
		return 0
	else
		return 1
	fi
}

is_running() {
	if [ \! -f "$PID_FILE" ]
	then
		return 1
	fi
	PID=`cat $PID_FILE`
	if [ -z "$PID" ]
	then
		return 1
	fi
	# we have a PID but is it still valid?
	if is_agent_process $PID
	then
		return 0
	else
		return 1
	fi
}

stop() {
	if [ \! -f "$PID_FILE" ]
	then
		echo "Missing PID file $PID_FILE"
		exit 0
	fi
	PID=`cat $PID_FILE`
	if [ -z "$PID" ]
	then
		echo "Empty PID file"
		exit 1
	fi
	# we have a PID but is it still valid?
	if is_agent_process $PID
	then
		# kill it
		kill -9 $PID
		echo "Stopped"
		rm $PID_FILE
		exit 0	
	else
		echo "Stale PID"
		exit 1
	fi
}

#
# Actions

if [ $# -ne 1 ]
then
	usage
fi

COMMAND=$1

case $COMMAND in 
start)
	if is_running
	then
		echo "Already running"
		exit 1
	fi
	build_classpath
	echo "Classpath: $CLASSPATH"
	$JAVA -classpath $CLASSPATH $AGENT &
	echo $! > $PID_FILE
	echo "Started"
;;

stop)
	stop
;;

status)
	if is_running
	then
		echo "Running"
	else
		echo "Stopped"
	fi
;;

*)
	usage
;;
esac

