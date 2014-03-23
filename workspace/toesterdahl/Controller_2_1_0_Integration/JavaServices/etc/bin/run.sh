#!/bin/sh

#############################################################################
#                                                                          
#  OpenRemote Controller Bootstrap                                         
#                                                                          
#############################################################################
# $Id: $

DIRNAME=`dirname $0`
PROGNAME=`basename $0`
GREP="grep"

# Use the maximum available, or set MAX_FD != -1 to use that
MAX_FD="maximum"

#
# Helper to complain.
#
warn() {
    echo "${PROGNAME}: $*"
}

#
# Helper to puke.
#
die() {
    warn $*
    exit 1
}

# OS specific support (must be 'true' or 'false').
cygwin=false;
darwin=false;
linux=false;
case "`uname`" in
    CYGWIN*)
        cygwin=true
        ;;

    Darwin*)
        darwin=true
        ;;
        
    Linux)
        linux=true
        ;;
esac

# Read an optional running configuration file
if [ "x$RUN_CONF" = "x" ]; then
    RUN_CONF="$DIRNAME/run.conf"
fi
if [ -r "$RUN_CONF" ]; then
    . "$RUN_CONF"
fi

# Force IPv4 on Linux systems since IPv6 doesn't work correctly with jdk5 and lower
if [ "$linux" = "true" ]; then
   JAVA_OPTS="$JAVA_OPTS -Djava.net.preferIPv4Stack=true"
fi

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin ; then
    [ -n "$ORC_HOME" ] &&
        ORC_HOME=`cygpath --unix "$ORC_HOME"`
    [ -n "$JAVA_HOME" ] &&
        JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
    [ -n "$JAVAC_JAR" ] &&
        JAVAC_JAR=`cygpath --unix "$JAVAC_JAR"`
fi

# Setup ORC_HOME
if [ "x$ORC_HOME" = "x" ]; then
    # get the full path (without any relative bits)
    ORC_HOME=`cd $DIRNAME/..; pwd`
fi
export ORC_HOME

# Increase the maximum file descriptors if we can
if [ "$cygwin" = "false" ]; then
    MAX_FD_LIMIT=`ulimit -H -n`
    if [ $? -eq 0 ]; then
	if [ "$MAX_FD" = "maximum" -o "$MAX_FD" = "max" ]; then
	    # use the system max
	    MAX_FD="$MAX_FD_LIMIT"
	fi

	ulimit -n $MAX_FD
	if [ $? -ne 0 ]; then
	    warn "Could not set maximum file descriptor limit: $MAX_FD"
	fi
    else
	warn "Could not query system maximum file descriptor limit: $MAX_FD_LIMIT"
    fi
fi

# Setup the JVM
if [ "x$JAVA" = "x" ]; then
    if [ "x$JAVA_HOME" != "x" ]; then
	JAVA="$JAVA_HOME/bin/java"
    else
	JAVA="java"
    fi
fi

# Setup the classpath
runjar="$ORC_HOME/bin/run.jar"
if [ ! -f "$runjar" ]; then
    die "Missing required file: $runjar"
fi
ORC_BOOT_CLASSPATH="$runjar"

# Tomcat uses the JDT Compiler
# Only include tools.jar if someone wants to use the JDK instead.
# compatible distribution which JAVA_HOME points to
if [ "x$JAVAC_JAR" = "x" ]; then
    JAVAC_JAR_FILE="$JAVA_HOME/lib/tools.jar"
else
    JAVAC_JAR_FILE="$JAVAC_JAR"
fi
if [ ! -f "$JAVAC_JAR_FILE" ]; then
   # MacOSX does not have a seperate tools.jar
   if [ "$darwin" != "true" -a "x$JAVAC_JAR" != "x" ]; then
      warn "Missing file: JAVAC_JAR=$JAVAC_JAR"
      warn "Unexpected results may occur."
   fi
   JAVAC_JAR_FILE=
fi

if [ "x$ORC_CLASSPATH" = "x" ]; then
    ORC_CLASSPATH="$ORC_BOOT_CLASSPATH"
else
    ORC_CLASSPATH="$ORC_CLASSPATH:$ORC_BOOT_CLASSPATH"
fi
if [ "x$JAVAC_JAR_FILE" != "x" ]; then
    ORC_CLASSPATH="$ORC_CLASSPATH:$JAVAC_JAR_FILE"
fi

# If -server not set in JAVA_OPTS, set it, if supported
SERVER_SET=`echo $JAVA_OPTS | $GREP "\-server"`
if [ "x$SERVER_SET" = "x" ]; then

    # Check for SUN(tm) JVM w/ HotSpot support
    if [ "x$HAS_HOTSPOT" = "x" ]; then
	HAS_HOTSPOT=`"$JAVA" -version 2>&1 | $GREP -i HotSpot`
    fi

    # Enable -server if we have Hotspot, unless we can't
    if [ "x$HAS_HOTSPOT" != "x" ]; then
	# MacOS does not support -server flag
	if [ "$darwin" != "true" ]; then
	    JAVA_OPTS="-server $JAVA_OPTS"
	fi
    fi
fi

# Setup ORC Native library path
ORC_NATIVE_DIR="$ORC_HOME/bin/native"
if [ -d "$ORC_NATIVE_DIR" ]; then
    if $cygwin ; then
        export PATH="$ORC_NATIVE_DIR:$PATH"
        ORC_NATIVE_DIR=`cygpath --dos "$ORC_NATIVE_DIR"`
    fi
    if [ "x$LD_LIBRARY_PATH" = "x" ]; then
        LD_LIBRARY_PATH="$ORC_NATIVE_DIR"
    else
        LD_LIBRARY_PATH="$ORC_NATIVE_DIR:$LD_LIBRARY_PATH"
    fi
    export LD_LIBRARY_PATH
    if [ "x$JAVA_OPTS" = "x" ]; then
        JAVA_OPTS="-Djava.library.path=$ORC_NATIVE_DIR"
    else
        JAVA_OPTS="$JAVA_OPTS -Djava.library.path=$ORC_NATIVE_DIR"
    fi
fi

# Setup Controller specific properties
JAVA_OPTS="-Dprogram.name=$PROGNAME -Djboss.server.name=controller $JAVA_OPTS"

# Set JVM options
JAVA_OPTS="-Xms64m -Xmx64m -XX:MaxPermSize=24m $JAVA_OPTS"

# Setup the java endorsed dirs
ORC_ENDORSED_DIRS="$ORC_HOME/lib/endorsed"

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
    ORC_HOME=`cygpath --path --windows "$ORC_HOME"`
    JAVA_HOME=`cygpath --path --windows "$JAVA_HOME"`
    ORC_CLASSPATH=`cygpath --path --windows "$ORC_CLASSPATH"`
    ORC_ENDORSED_DIRS=`cygpath --path --windows "$ORC_ENDORSED_DIRS"`
fi

# Display our environment
echo "---------------------------------------------------------------------------"
echo ""
echo "  OpenRemote Controller V0.1"
echo ""
echo "---------------------------------------------------------------------------"
echo ""
echo "  JAVA: $JAVA"
echo ""
echo "Starting..."
echo ""

while true; do
   if [ "x$LAUNCH_ORC_IN_BACKGROUND" = "x" ]; then
      # Execute the JVM in the foreground
      "$JAVA" $JAVA_OPTS \
         -Djava.endorsed.dirs="$ORC_ENDORSED_DIRS" \
         -classpath "$ORC_CLASSPATH" \
         org.jboss.Main "$@"
      ORC_STATUS=$?
   else
      # Execute the JVM in the background
      "$JAVA" $JAVA_OPTS \
         -Djava.endorsed.dirs="$ORC_ENDORSED_DIRS" \
         -classpath "$ORC_CLASSPATH" \
         org.jboss.Main "$@" &
      ORC_PID=$!
      # Trap common signals and relay them to the ORC process
      trap "kill -HUP  $ORC_PID" HUP
      trap "kill -TERM $ORC_PID" INT
      trap "kill -QUIT $ORC_PID" QUIT
      trap "kill -PIPE $ORC_PID" PIPE
      trap "kill -TERM $ORC_PID" TERM
      # Wait until the background process exits
      WAIT_STATUS=0
      while [ "$WAIT_STATUS" -ne 127 ]; do
         ORC_STATUS=$WAIT_STATUS
         wait $ORC_PID 2>/dev/null
         WAIT_STATUS=$?
      done
   fi
   # If restart doesn't work, check the following if you're running Red Hat 7.0
   #    http://developer.java.sun.com/developer/bugParade/bugs/4465334.html   
   if [ $ORC_STATUS -eq 10 ]; then
      echo "Restarting Open Remote Controller..."
   else
      exit $ORC_STATUS
   fi
done

