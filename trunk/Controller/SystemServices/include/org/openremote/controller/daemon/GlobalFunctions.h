/*
* OpenRemote, the Home of the Digital Home.
* Copyright 2008, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License as
* published by the Free Software Foundation; either version 3.0 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
*
* You should have received a copy of the GNU General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/

//--------------------------------------------------------------------------------------------------
//
// Declarations of globally available functions in this app.
//
//
// Author: Juha Lindfors (juha@juhalindfors.com)
//
//--------------------------------------------------------------------------------------------------

#ifndef ORC_DAEMON_GLOBALFUNCTIONS_H
#define ORC_DAEMON_GLOBALFUNCTIONS_H

#include "org/openremote/controller/daemon/Vocabulary.h"
#include "org/openremote/controller/daemon/APRVocabulary.h"
#include "org/openremote/controller/daemon/IOProtocol.h"

/**
 * ControlProtocolHandler.c
 */
int handleControlProtocol(Socket socket, String payload);

/**
 * SerialProtocolHandler.c
 */
ProtocolStatus handleSerialProtocol(Socket socket, String payload);

/**
 * IOProtocolHandler.c
 */
int readMessage(Socket socket, MemPool memoryPool);

/**
 * IODaemon.c
 */
String    getErrorStatus(Status status);
void      cleanup();


#endif

