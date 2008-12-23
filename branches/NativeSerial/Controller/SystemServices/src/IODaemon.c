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

// ------------------------------------------------------------------------------------------------
//
//  A simple daemon that accepts bytes from incoming socket and redirects them to various I/O
//  devices in the native operating system. The socket I/O is based on Apache Portable Runtime (APR)
//  library.
//
//  The socket semantics here are blocking (Java style) and the daemon is therefore itself
//  multithreaded.
//
//  My C is more than rusty so don't expect miracles here. Please send patches and corrections
//  when you spot them. I've overdocumented the code in the hope of making errors or wrong
//  assumptions more glaring.
//
//  Also you will notice the extensive use of type definitions to make the final source have a very
//  Java like feeling. If that gives you an allergic reaction then too bad for you. Hopefully it
//  makes the thing a bit more bearable for any Java developer looking at it. If you're a C hacker
//  you already know how to make it look like the way you like.
//
//
//  Author: Juha Lindfors (juha@juhalindfors.com)
//
// ------------------------------------------------------------------------------------------------

#include <stdlib.h>

#include "apr_getopt.h"
#include "apr_strings.h"

#include "org/openremote/controller/daemon/GlobalFunctions.h"
#include "org/openremote/controller/daemon/IODaemon.h"
#include "org/openremote/controller/daemon/Log.h"


/**
 * Sets the default listening port for the daemon if nothing else is defined from the command
 * line options.
 */
#define DEFAULT_PORT		  9999


/**
 * TODO
 *
 * Can't find any information what the semantics of this value means exactly -- number of memory
 * node lists? The value is a random guess, value zero would create an unlimited memory pool.
 */
#define MAX_POOL_SIZE     32


/**
 * TODO
 *
 */
static Configuration config;

/**
 * Pointer to APR implementation of a memory pool. Most APR API calls require this.
 */
static MemPool mempool;



/**
 *  MAIN:
 *
 *  First initialize APR and create the necessary memory pool, then bind a listening socket
 *  into local loopback address and finally parse protocol from incoming client connection(s).
 *
 *  Currently the daemon accepts following options from the command line:
 *
 *     -h, --help     prints supported command line options
 *     -p, --port     sets the listening port of this I/O daemon
 *
 *
 *  TODO : mempool error handler
 *  TODO : mempool data & statistics
 *  TODO : log to fprintf(...)
 */
int main(int argc, String argv[])
{
  /**
   * Pointer to a listening server side socket.
   */
  Socket serversocket;

  /**
   * Type for specifying an error or status code. See apr_errno.h for possible values.
   *
   * For investigating specific error values, use the appropriate macros in apr_errno.h to
   * handle platform specific codes.
   */
  Status status;


  /**
   * Initialize APR. Creates an APR memory pool and updates our 'mempool' pointer to it.
   */
  if ((status = init(&mempool, argc, argv)) != APR_SUCCESS)
  {
    logerror("Unable to initialize I/O daemon: %s", getErrorStatus(status));
    cleanup();
    exit(EXIT_FAILURE);
  }

  /**
   * Parse command line options. See the function header for all valid options.
   */
  parseOptions(argc, argv);


  /**
   * Start the server socket.
   *
   * This is a blocking TCP socket (our concurrency scale requirements are low) so the
   * socket accept() routine will create new thread per incoming connection.
   *
   * Pointer 'serversocket' will be set to point to the created socket structure.
   */
  if ((status = createServerSocket(&serversocket)) != APR_SUCCESS)
  {
    logerror("Cannot create server socket: %s", getErrorStatus(status));
    cleanup();
    exit(EXIT_FAILURE);
  }

  /**
   * Start handling incoming client connections
   */
  status = handleIncomingConnections(serversocket);

  cleanup();

  if (status != APR_SUCCESS)
  {
    exit(EXIT_FAILURE);
  }
  else
  {
    exit(0);
  }
}

/**
 * Returns a pointer to a string containing a formatted APR error status.
 *
 * @param status    APR status value
 */
String getErrorStatus(Status status)
{
  char errbuf[1024];

  apr_strerror(status, errbuf, sizeof(errbuf));

  /* allocate duplicate from the pool and return a pointer to it... */

  return apr_pstrdup(mempool, errbuf);
}

/**
 * Clean up the resources before exit
 */
void cleanup()
{
  logtrace("%s", "Cleaning up...");

  apr_pool_destroy(mempool);

  apr_terminate();

  logtrace("%s", "Clean up done.");
}


// Local Functions --------------------------------------------------------------------------------


/**
 * Binds a server socket and starts listening for incoming connections.
 *
 * @param  serverSocket  result parameter -- the pointer will be initialized
 *                       to point to the created server socket which the caller
 *                       may then use.
 */
static Status createServerSocket(SocketResult socketResult)
{
  /**
   * APR API return type. See the comments in the main() for details.
   */
  Status status;

  /**
   * Temporary result param for the apr_create_socket() call -- we'll initialize the socket
   * in this function (socket options, etc.) and then pass it back to the caller via the
   * 'socketResult' result param.
   */
  Socket newsocket;

  /**
   * A result parameter for apr_sockaddr_info_get() call.
   */
  SocketAddress socketAddress;

  /**
   * Specify a loopback address for the server socket. This ensures that only connections from
   * the same host are allowed.
   */
  String localhost = "127.0.0.1";


  /**
   * Specifying a socket address.
   *
   * 1) socketAddress is a result parameter
   *
   * 2) We intend to accept connections from the same host only, therefore using a loopback
   *    address for this server socket.
   *
   * 3) APR_INET is IPv4 address family -- use APR_INET6 for IPv6, see apr_network_io.h
   *
   * 4) configured physical port number
   *
   * 5) socket address flags, 0 == no flags. See APR_IPV4_ADDR_OK and APR_IPV6_ADDR_OK
   *    in apr_network_io.h
   */
  if ((status = apr_sockaddr_info_get(
      &socketAddress, localhost, APR_INET, config->port, 0, mempool)) != APR_SUCCESS)
  {
    return status;
  }

  /**
   * Create the socket based on the socket address.
   *
   * 1) Socket is a result parameter.
   *
   * 2) IP address family from socketAddress we created earlier.
   *
   * 3 & 4) accept streaming TCP connections (see sockets.h and apr_network_io.h respectively)
   */
  if ((status = apr_socket_create(
      &newsocket, socketAddress->family, SOCK_STREAM, APR_PROTO_TCP, mempool)) != APR_SUCCESS)
  {
    return status;
  }

  /**
   * Server socket options:
   *
   *  - APR_SO_NONBLOCK=FALSE ==> blocking socket
   *
   *  - socket timeout = -1   ==> reads and writes on this socket will block as well, not sure if
   *                              this makes any difference for a listening server socket (it will
   *                              just spawn client sockets from blocking accept() call)
   */
  if ((status = apr_socket_opt_set(newsocket, APR_SO_NONBLOCK, FALSE)) != APR_SUCCESS)
  {
    return status;
  }

  if ((status = apr_socket_timeout_set(newsocket, -1)) != APR_SUCCESS)
  {
    return status;
  }

  /**
   * Bind the listening socket to a physical port.
   */
  if ((status = apr_socket_bind(newsocket, socketAddress)) != APR_SUCCESS)
  {
    logtrace("Bind failed: %s", getErrorStatus(status));
    return status;
  }

  /**
   * Start listening. SOMAXCONN is the system wide value for incoming connection back log size.
   * See sockets.h for details.
   */
  if ((status = apr_socket_listen(newsocket, SOMAXCONN)) != APR_SUCCESS)
  {
    return status;
  }

  /**
   * Set our result param, report success and return APR_SUCCESS
   */
  *socketResult = newsocket;

  loginfo("OpenRemote I/O Daemon listening on %s:%d...", socketAddress->hostname, socketAddress->port);

  return APR_SUCCESS;
}


/**
 * Runs in a loop, blocking on accept() taking in connections and sending each incoming
 * client connection off to a new thread to handle.
 *
 * @param serverSocket    the bound server socket which accepts incoming connections
 */
static Status handleIncomingConnections(Socket serverSocket)
{
  /**
   * Pointer to client socket we will get back from accept()
   */
  Socket clientSocket;

  /**
   * APR status, see main() for more details.
   */
  Status status;

  // TODO
  ThreadAttributes threadAttributes;

  // TODO
  Thread thread;

  /**
   * Data structure to use for passing contextual data to the thread. There are also
   * apr_thread_data accessor functions to set key/value pairs to APR threads. In theory,
   * those could be used instead.
   */
  SocketThreadContext threadContext;

  /**
   * Once set to false, we will exit the loop and this process...
   */
  static int acceptingConnections = TRUE;


  while (acceptingConnections)
  {
    logtrace("%s", "Waiting at server socket accept()...");


    /* The accept() will block since we have a blocking server socket... */

    if ((status = apr_socket_accept(&clientSocket, serverSocket, mempool)) == APR_SUCCESS)
    {

      /* Create thread attributes... */

      if ((status = apr_threadattr_create(&threadAttributes, mempool)) != APR_SUCCESS)
      {
    	  logerror("Cannot create new thread: %s", getErrorStatus(status));
    	  continue;
      }

      /**
       * Establish socket thread context with some data (currently just pointer
       * to the client socket).
       */

      threadContext = apr_pcalloc(mempool, SocketThreadContextSize);
      threadContext->socket = clientSocket;

      /* Spawn the thread... */

      if ((status = apr_thread_create(&thread, threadAttributes,
                                      socketThread, threadContext, mempool)) != APR_SUCCESS)
      {
        logerror("Cannot create a new thread: %s", getErrorStatus(status));
        continue;
      }
    }
    else
    {
      logerror("Listening socket accept() failed: %s", getErrorStatus(status));

      acceptingConnections = FALSE;
    }
  }

  return status;
}


/**
 * This is the client socket thread implementation. It unmarshals the socket thread
 * context, sets the client socket options and starts running in a loop to read
 * incoming client messages.
 *
 * The client socket is blocking (hence we are here in our own thread) with no time out.
 * The socket is configured with keep-alive connection. In practice this means the
 * client side (Java side in our case) opens one or few connections and keeps using
 * the persistent connection(s) to multiplex I/O operations.
 */
static Runnable socketThread(Thread thread, Any socketThreadContext)
{
  SocketThreadContext ctx = socketThreadContext;

  apr_socket_opt_set(ctx->socket, APR_SO_NONBLOCK, FALSE);
  apr_socket_opt_set(ctx->socket, APR_SO_KEEPALIVE, TRUE);
  apr_socket_timeout_set(ctx->socket, -1);

  MemPool threadMemPool = apr_thread_pool_get(thread);

  int running = TRUE;
  int status  = TRUE;

  static int numberOfThreads = 0;

  logtrace("Number of threads %d", ++numberOfThreads);


  while (running)
  {
    status = readMessage(ctx->socket, threadMemPool);

    if (status != PROTOCOL_MESSAGE_OK)
    {
      running = FALSE;
    }
  }

  if ((status = apr_socket_close(ctx->socket)) != APR_SUCCESS)
  {
    logerror("Could not close socket: %s", getErrorStatus(status));
  }

  numberOfThreads--;

  apr_pool_destroy(threadMemPool);

  apr_thread_exit(thread, status);

  return NULL;
}



/**
 *
 * TODO
 *
 */
static Status init(MemPoolResult mempool, int argc, String argv[])
{
  /**
   * Type for specifying an error or status code. See apr_errno.h for possible values. If you
   * want to investigate specific error values, use the appropriate macros to handle cross
   * platform issues.
   */
  Status status;

  /**
   * TODO
   */
  MemPool newpool;

  /**
   * TODO
   */
  const char * const *args = argv;



  // Function Body --------------------------------------------------------------------------------

  /**
   * Register to invoke apr_terminate at process exit -- apr_terminate() will tear down any APR
   * internal data structures which aren't torn down automatically. An APR program must call this
   * function at termination once it has stopped using APR services.
   */
  if (atexit(apr_terminate) != 0)
  {
    logwarn("%s", "Error registering cleanup function(s).");
  }

  loginfo("%s", "Starting OpenRemote I/O daemon...");

  /**
   * Set up an application with normalized argc, argv (and optionally env) in order to deal with
   * platform-specific oddities, such as Win32 services, code pages and signals. This must be the
   * first function called for any APR program. This should only be done once per process.
   */
  if ((status = apr_app_initialize(&argc, &args, NULL /* no env */)) != APR_SUCCESS)
  {
    return status;
  }

  /**
   *  Create APR memory pool...
   */
  if ((status = apr_pool_create(&newpool, NULL /* No parent pool */)) != APR_SUCCESS)
  {
    return status;
  }

  /**
   * Set the max limit on the memory pool size so some memory will return back to the system,
   * eventually.
   */
  apr_allocator_t *pool_allocator = apr_pool_allocator_get(newpool);

  if (pool_allocator)
  {
    apr_allocator_max_free_set(pool_allocator, MAX_POOL_SIZE);
  }
  else
  {
    logwarn(
        "Failed to set memory pool size threshold to %d, continuing with default " \
        "memory pool size (unlimited?)", MAX_POOL_SIZE
    );
  }

  *mempool = newpool;

  return status;
}


/**
 * Parses command line options using getopt() conventions. See main() for all supported
 * command line options.
 */
static void parseOptions(int argc, String argv[])
{
  apr_getopt_t *opt;

  Status status;

  int optionCharacter;

  String optionArgument;

  static const apr_getopt_option_t optionList[] =
  {
    {
      "port", 'p', TRUE, "listening port for the I/O daemon"
    },

    {
      "help", 'h', FALSE, "display available options"
    },

    {
      NULL, 0, 0, NULL
    }
  };


  apr_getopt_init(&opt, mempool, argc, argv);

  config = apr_palloc(mempool, ConfigurationSize);

  config->port = DEFAULT_PORT;


  while ((status = apr_getopt_long(opt, optionList, &optionCharacter, &optionArgument)) == APR_SUCCESS)
  {
    switch (optionCharacter)
    {
      case 'h':

        printHelpAndExit();

        break;

      case 'p':

        configureServerPort(optionArgument);

        break;
    }
  }

  if (status != APR_EOF)
  {
    logerror("Error parsing command line options: %s", getErrorStatus(status));
  }
}


/**
 * Prints help to standard output stream
 */
static void printHelpAndExit()
{
  printf("\nOpenRemote I/O daemon accepts the following command line arguments:\n");
  printf("\n");
  printf("-p, --port        I/O daemon listening port\n");
  printf("-h, --help        this message\n\n");
  fflush(stdout);

  cleanup();
  exit(0);
}



/**
 * Configures server's listening port from command line arguments.
 */
static void configureServerPort(String portArgumentValue)
{
  long int port_number;

  /**
   * Strtol() returns the converted value, if any. Zero (FALSE) is returned and errno *may* be set
   * to EINVAL if conversion cannot be made.
   *
   * Using base 0 allows a decimal constant, octal constant or hexadecimal constant preceded by
   * a + or - sign.
   */
  port_number = strtol(portArgumentValue, NULL, 0 /* base */);

  if (port_number < 0 || port_number > 65535)
  {
    logwarn("Invalid port number %ld, falling back to default port %d...",
            port_number, DEFAULT_PORT);

    port_number = DEFAULT_PORT;
  }

  config->port = port_number;
}

