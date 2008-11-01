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


#include <stdio.h>

#include <apr_file_io.h>
#include <apr_network_io.h>
#include <apr_strings.h>
#include <apr_getopt.h>
#include <apr_thread_proc.h>

#include <orc/iodaemon.h>


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
 * TODO
 *
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



  // Function Body ------------------------------------------------------------------------------


  /**
   * Initialize APR. Creates an APR memory pool and updates our 'mempool' pointer to it.
   */
  if ((status = init(&mempool, argc, argv)) != APR_SUCCESS)
  {
    printErrorStatus(status);
    exitWithError();
  }

  /**
   * Parse command line options
   *
   * TODO
   */
  if ((status = parseOptions(argc, argv)) != APR_SUCCESS)
  {
    printErrorStatus(status);
    exitWithError();
  }


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
    printErrorStatus(status);
    exitWithError();
  }


  /**
   * Start handling incoming client connections
   */
  handleIncomingConnections(serversocket);


  // TODO: Clean exit... currently handleIncomingConnections() is an infinite loop...

  apr_pool_destroy(mempool);
  apr_terminate();
  exit(0);
}


/**
 * Binds a server socket and starts listening for incoming connections.
 *
 * 1) The first argument 'serverSocket' is a result parameter -- the pointer will be initialized
 *    to point to the created server socket which the caller may then use.
 *
 * 2) The second argument must be a pointer to an already created APR memory pool.
 */
Private Status createServerSocket(SocketResult socketResult)
{
  /**
   * APR API return type. See the comments in the main() for details.
   */
  Status status;

  /**
   * Temporary result param for the apr_create_socket() call -- we'll initialize the socket
   * in this function (socket options, etc.) and then pass it back to the caller via the
   * 'socketResult' result param of this function.
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


  // Function Body --------------------------------------------------------------------------------


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
   *  - blocking socket
   *
   *  - socket timeout: -1 means reads and writes on this socket will block as well, not sure if
   *    this makes any difference for a listening server socket (it will just spawn client sockets
   *    from [blocking] accept() call)
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
Private void handleIncomingConnections(Socket serverSocket)
{
  /**
   * Pointer to client socket we will get back from accept()
   */
  Socket clientSocket;

  /**
   * APR status, see main() for more details.
   */
  Status status;

  ThreadAttributes threadAttributes;

  Thread thread;

  SocketThreadContext threadContext;


  // Function Body --------------------------------------------------------------------------------


  while (TRUE)
  {
    // this will block since we have a blocking server socket...

    if ((status = apr_socket_accept(&clientSocket, serverSocket, mempool)) == APR_SUCCESS)
    {
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

      apr_thread_create(&thread, threadAttributes, socketThread, threadContext, mempool);
    }
    else
    {
      logerror("Listening socket accept() failed: %s", getErrorStatus(status));
      exitWithError();
    }
  }
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
Private Runnable socketThread(Thread thread, Any socketThreadContext)
{
  SocketThreadContext ctx = socketThreadContext;

  apr_socket_opt_set(ctx->socket, APR_SO_NONBLOCK, FALSE);
  apr_socket_opt_set(ctx->socket, APR_SO_KEEPALIVE, TRUE);
  apr_socket_timeout_set(ctx->socket, -1);

  int running = TRUE;
  int status  = TRUE;

  while (running)
  {
    status = readMessage(ctx->socket);

    if (status != PROTOCOL_MESSAGE_READ_OK)
    {
      running = FALSE;
    }
  }

  if ((status = apr_socket_close(ctx->socket)) != APR_SUCCESS)
  {
    logerror("Could not close socket: %s", getErrorStatus(status));
  }

  return NULL;
}


/**
 * This one parses the incoming message on the socket and sends the appropriate response.
 *
 * The message structure is string based for the sake of simplicity and expects the following
 * format:
 *
 *  - First 8 characters are a module identifier. This indicates what I/O module the message
 *    is intended for. Current valid values are:
 *
 *       DCONTROL    Control id for giving commands to the daemon itself
 *       R_SERIAL    Serial module to write uninterpreted (raw) bytes to serial port(s)
 *
 *
 *  - Next 10 characters are a message payload length. This must be a correct value for the
 *    length of the rest of the message payload.
 *
 *    Message length is a 10 character long hexadecimal value, in uppercase and with leading
 *    '0X' characters. It must always be exactly 10 characters long so the actual value is padded
 *    with leading zeroes. Payload size zero is *not* a valid value.
 *
 *    Examples of valid values are:
 *
 *       '0X0000DEAD', '0XCAFEBABE' and '0X0000000D'
 *
 *  - The remainder of the message string is the I/O module specific payload. The length is
 *    determined by the previous message payload length header. The content of the payload
 *    is specific to the I/O module. See below for protocol details of each module.
 */
Private int readMessage(Socket socket)
{
  static const int moduleid_header_len = 8;
  static const int msglength_header_len = 10;

  static String control_id = "DCONTROL";
  static String serial_id  = "R_SERIAL";

  static int char_size = sizeof(char);

  char module_id[moduleid_header_len + 1];
  char message_length[msglength_header_len + 1];
  char *payload;

  apr_size_t payload_size;
  apr_size_t len;
  Status status;


  // Function Body --------------------------------------------------------------------------------


  /**
   * Read first eight characters as module ID... (and make it into string on the last extra char)
   */
  len = sizeof(module_id) - char_size;

  status = apr_socket_recv(socket, module_id, &len);

  if (status != APR_SUCCESS || len != moduleid_header_len)
  {
    if (status != APR_SUCCESS)
    {
      logerror("Receive error: %s", getErrorStatus(status));
    }

    else
    {
      module_id[len] = '\0';
      logerror("Was expecting %d bytes of module ID header, got %d instead ('%s')", \
               moduleid_header_len, len, module_id);
    }

    return PROTOCOL_RECEIVE_ERROR_SOCKET_CLOSED;
  }

  module_id[moduleid_header_len] = '\0';


  /**
   * Read following ten characters as message payload length hex string and convert to int,
   * then allocate required memory chunk for the payload from the pool...
   */
  len = sizeof(message_length) - char_size;

  status = apr_socket_recv(socket, message_length, &len);

  if (status != APR_SUCCESS || len != msglength_header_len)
  {
    if (status != APR_SUCCESS)
    {
      logerror("Receive error: %s", getErrorStatus(status));
    }

    else
    {
      message_length[len] = '\0';
      logerror("Was expecting %d bytes of message length header, got %d instead ('%s')", \
               msglength_header_len, len, message_length);
    }

    return PROTOCOL_RECEIVE_ERROR_SOCKET_CLOSED;
  }

  message_length[msglength_header_len] = '\0';
  long int msglen = strtol(message_length, NULL, 16 /* base 16 hex */);

  if (msglen == 0)
  {
    logerror("Unable to convert '%s' to integer.", message_length);

    return PROTOCOL_RECEIVE_ERROR_SOCKET_CLOSED;
  }

  payload_size = char_size * msglen + char_size;
  payload = apr_pcalloc(mempool, payload_size);

  // TODO : validate payload size against module id

  /**
   * Finally read in the payload.
   */
  len = payload_size - char_size;

  status = apr_socket_recv(socket, payload, &len);

  if (status != APR_SUCCESS || len != payload_size - char_size)
  {
    if (status != APR_SUCCESS)
    {
      logerror("Receive error: %s", getErrorStatus(status));
    }

    else
    {
      payload[len] = '\0';
      logerror("Was expecting %d bytes of payload, got %d instead ('%s')", \
                payload_size - char_size, len, payload);
    }

    return PROTOCOL_RECEIVE_ERROR_SOCKET_CLOSED;
  }

  payload[len] = '\0';

  logdebug("PAYLOAD: %s", payload);


  /**
   * CONTROL PROTOCOL
   *
   * For giving control commands to the daemon itself. Supported commands are:
   *
   *  - PING ("ARE YOU THERE")
   *
   *      Only requires "I AM HERE" response, no other action
   *
   *  - KILL ("D1ED1ED1E")
   *
   *      Respond with "GOODBYE CRUEL WORLD" and execute an orderly shutdown
   */
  if (strncmp(module_id, control_id, 8) == 0)
  {
    static String ping_request = "ARE YOU THERE";
    static String ping_response = "I AM HERE";

    static String kill_request = "D1ED1ED1E";
    static String kill_response = "GOODBYE CRUEL WORLD";

    if (strcmp(payload, ping_request) == 0)
    {
      logdebug("%s", "Responding to a ping...");

      // TODO : check socket send status
      apr_size_t len = strlen(ping_response);
      apr_socket_send(socket, ping_response, &len);
    }

    else if (strcmp(payload, kill_request) == 0)
    {
      loginfo("%s", "Shutting down...");

      // TODO : check socket send status
      apr_size_t len = strlen(kill_response);
      apr_socket_send(socket, kill_response, &len);

      apr_socket_close(socket);
      apr_pool_destroy(mempool);

      apr_terminate();
      exit(0);
    }
  }

  /**
   *  SERIAL PROTOCOL
   *
   *  Payload is expected to start with a 3 character configuration string, specifying
   *  number of data bits, parity and number of stop bits.
   *
   *  For example, serial options for 8 databits, no parity, one stop bit would start with '8N1'.
   *  Seven databits with even parity and two stop bits would be '7E2'. Odd parity is indicated
   *  with character 'O'.
   *
   *  TODO
   */
  else if (strncmp(module_id, serial_id, 8) == 0)
  {
    char databit = payload[0];
    char parity  = payload[char_size];
    char stopbit = payload[char_size*2];

    logdebug("Serial Options: %c%c%c", databit, parity, stopbit);
  }

  return PROTOCOL_MESSAGE_READ_OK;
}


/**
 *
 * TODO
 *
 */
Private Status init(MemPoolResult mempool, int argc, String argv[])
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
    printf("WARNING: Error registering cleanup function(s).\n");
    fflush(stdout);
  }

  printf("Starting OpenRemote I/O daemon...\n");
  fflush(stdout);

  /**
   * Set up an application with normalized argc, argv (and optionally env) in order to deal with
   * platform-specific oddities, such as Win32 services, code pages and signals. This must be the
   * first function called for any APR program. This should only be done once per process.
   */
  status = apr_app_initialize(&argc, &args, NULL /* no env */);

  if (status != APR_SUCCESS)
    return status;

  // Create APR memory pool...

  status = apr_pool_create(&newpool, NULL /* No parent pool */);

  if (status != APR_SUCCESS)
    return status;

  // Set the max limit on the memory pool size so some memory will return back to the system,
  // eventually. This is done via apr_allocator_t reference.

  apr_allocator_t *pool_allocator = apr_pool_allocator_get(newpool);

  if (pool_allocator)
  {
    apr_allocator_max_free_set(pool_allocator, MAX_POOL_SIZE);
  }
  else
  {
    printf(
        "Failed to set memory pool size threshold to %d, continuing with default " \
        "memory pool size (unlimited?)", MAX_POOL_SIZE
    );
    fflush(stdout);
  }

  *mempool = newpool;

  return status;
}


Private void printErrorStatus(Status status)
{
  char errbuf[1024];

  apr_strerror(status, errbuf, sizeof(errbuf));

  printf("OpenRemote I/O Daemon Error: %d, %s\n", status, errbuf);
  fflush(stdout);
}

Private String getErrorStatus(Status status)
{
  char errbuf[1024];

  apr_strerror(status, errbuf, sizeof(errbuf));

  return apr_pstrdup(mempool, errbuf);
}

Private void exitWithError()
{
  // apr_terminate should also be registered with atexit() but it doesn't
  // seem to hurt to do it twice...

  apr_terminate();

  exit(EXIT_FAILURE);
}

/**
 * TODO
 */
Private Status parseOptions(int argc, String argv[])
{

  /**
   * TODO
   */
  apr_getopt_t *opt;

  Status status;

  int optionCharacter;

  String optionArgument;

  /**
   * TODO
   */
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


  // Function Body --------------------------------------------------------------------------------

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

  if (status == APR_EOF)
    return APR_SUCCESS;
  else
    return status;
}


/**
 * TODO
 */
Private void printHelpAndExit()
{
  // TODO

  printf("\nHelp should be written here.\n");
  printf("\n");
  printf("-p, --port        I/O daemon listening port\n");
  printf("-h, --help        this message\n\n");
  fflush(stdout);

  apr_terminate();
  exit(0);
}


/**
 * TODO
 */
Private void configureServerPort(String portArgumentValue)
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
    printf(
      "\n[WARNING] Invalid port number %d, falling back to default port %d...\n\n",
      port_number, DEFAULT_PORT
    );
    fflush(stdout);

    port_number = DEFAULT_PORT;
  }

  config->port = port_number;
}

