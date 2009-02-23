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
// Non-shared definitions of iodaemon.c
//
//
// Author: Juha Lindfors (juha@juhalindfors.com)
//
//--------------------------------------------------------------------------------------------------


#include "org/openremote/controller/daemon/Vocabulary.h"
#include "org/openremote/controller/daemon/APRVocabulary.h"



/**
 * TODO: should be an enum
 *
 * Function return values.
 */
#define PROTOCOL_RECEIVE_ERROR            -1
#define PROTOCOL_SEND_ERROR               -2
#define PROTOCOL_MESSAGE_OK                0


// Vocabulary -------------------------------------------------------------------------------------

#define Runnable          void *APR_THREAD_FUNC


typedef struct socket_thread_context
{
  Socket        socket;
} *SocketThreadContext;

#define SocketThreadContextSize    sizeof(SocketThreadContext)

typedef struct configuration
{
  int port;

} *Configuration;

#define ConfigurationSize         sizeof(Configuration)




// Function Prototypes ----------------------------------------------------------------------------

static Status    init(MemPoolResult mempool, int argc, String argv[]);
static Status    createServerSocket(SocketResult socketResult);
static void      parseOptions(int argc, String argv[]);
static void      printHelpAndExit();
static void      configureServerPort(String portArgumentValue);
static Status    handleIncomingConnections(Socket serverSocket);
static Runnable  socketThread(Thread thread, void *socket_thread_context);



