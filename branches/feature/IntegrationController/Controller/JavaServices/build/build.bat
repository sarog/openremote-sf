@echo off

REM ###########################################################################
REM OpenRemote, the Home of the Digital Home.
REM Copyright 2008, OpenRemote Inc.
REM
REM See the contributors.txt file in the distribution for a
REM full listing of individual contributors.
REM
REM This is free software; you can redistribute it and/or modify it
REM under the terms of the GNU General Public License as
REM published by the Free Software Foundation; either version 3.0 of
REM the License, or (at your option) any later version.
REM
REM This software is distributed in the hope that it will be useful,
REM but WITHOUT ANY WARRANTY; without even the implied warranty of
REM MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
REM
REM You should have received a copy of the GNU General Public
REM License along with this software; if not, write to the Free
REM Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
REM 02110-1301 USA, or see the FSF site: http://www.fsf.org.
REM ###########################################################################


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
