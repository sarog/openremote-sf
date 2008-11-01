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
// Header definitions for iodaemon.c
//
//
// Author: Juha Lindfors (juha@juhalindfors.com)
//
//--------------------------------------------------------------------------------------------------


#define loginfo(content, args...)           \
    printf("[INFO] " content "\n", args);   \
    fflush(stdout);

#define logerror(content, args...)          \
    printf("[ERROR] " content "\n", args);  \
    fflush(stdout);

#define logdebug(content, args...)          \
    printf("[DEBUG] " content "\n", args);  \
    fflush(stdout);


#define PROTOCOL_RECEIVE_ERROR_SOCKET_CLOSED    -1
#define PROTOCOL_MESSAGE_READ_OK                 0


typedef apr_socket_t      *Socket;
typedef Socket            *SocketResult;
typedef apr_sockaddr_t    *SocketAddress;
typedef apr_pool_t        *MemPool;
typedef MemPool           *MemPoolResult;
typedef apr_thread_t      *Thread;
typedef apr_threadattr_t  *ThreadAttributes;
typedef apr_status_t       Status;
typedef const char        *String;
typedef void              *Any;

#define Runnable          void *APR_THREAD_FUNC
#define Private           static


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

Private Status    init(MemPoolResult mempool, int argc, String argv[]);
Private Status    createServerSocket(SocketResult socketResult);
Private int       readMessage(Socket socket);
Private void      printErrorStatus(Status status);
Private void      exitWithError();
Private Status    parseOptions(int argc, String argv[]);
Private void      printHelpAndExit();
Private void      configureServerPort(String portArgumentValue);
Private void      handleIncomingConnections(Socket serverSocket);
Private Runnable  socketThread(Thread thread, void *socket_thread_context);
Private String    getErrorStatus(Status status);



