@echo off

REM   OpenRemote, the Internet-enabled Home.
REM
REM   This work is licensed under Creative Commons Attribution-Noncommercial-Share Alike 3.0
REM   United States, http://creativecommons.org/licenses/by-nc-sa/3.0/us/


REM   Runs ANT build script.
REM
REM      - sets an explicit ANT_HOME to the Ant distribution in 'Tools' directory
REM      - defines additional external library classpaths to run Ant
REM
REM
REM   Author: Juha Lindfors
REM   Revision: $Id: $

set ANT_HOME=..\..\..\Tools\apache-ant-1.7.0
set PATH="%PATH%;%ANT_HOME%\lib\native"
%ANT_HOME%\bin\ant -lib %ANT_HOME%\lib\external %*
