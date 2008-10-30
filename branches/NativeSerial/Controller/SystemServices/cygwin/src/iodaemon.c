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
//  My C is more than rusty so don't expect miracles here. Please send patches and corrections
//  when you spot them. I've overdocumented the code in the hope of making errors or wrong
//  assumptions more glaring.
//
//  Currently the daemon accepts following options from the command line:
//
//   -h, --help     prints supported command line options
//   -p, --port     sets the listening port of this I/O daemon
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


/**
 * Sets the default listening port for the daemon if nothing else is defined from the command
 * line options.
 */
#define DEFAULT_PORT		    9999


/**
 * TODO
 *
 * Can't find any information what the semantics of this value means exactly -- number of memory
 * node lists? The value is a random guess, value zero would create an unlimited memory pool.
 */
#define MAX_POOL_SIZE     32


// Function Prototypes ----------------------------------------------------------------------------

static apr_status_t    init(apr_pool_t **mempool, int argc, const char *argv[]);
static apr_status_t    create_server_socket(apr_socket_t **serversocket, apr_pool_t *mempool);
static void            handle_incoming_connections(apr_socket_t *serversocket, apr_pool_t *mempool);
static void            read_message(apr_socket_t *clientsocket, apr_pool_t *mempool);
static void            print_error_status(apr_status_t status);
static void            exit_with_error();
static apr_status_t    parse_options(int argc, const char *argv[], apr_pool_t *mempool);
static void            print_help_and_exit();
static void            configure_server_port(const char *port_argument_value);
static void            receive_error(apr_status_t status, apr_socket_t *clientsocket, apr_size_t len);


static struct configuration
{
  int port;

} *config;


// ------------------------------------------------------------------------------------------------
//
//   MAIN:
//
//   First initialize APR and create the necessary memory pool, then bind a listening socket
//   into local loopback address and finally parse protocol from incoming client connection.
//
// ------------------------------------------------------------------------------------------------
int main(int argc, const char *argv[])
{
  /**
   * Pointer to APR implementation of a memory pool. Most APR API calls require this.
   */
  apr_pool_t *mempool = NULL;

  /**
   * Pointer to a listening server side socket.
   */
  apr_socket_t *serversocket;

  /**
   * Type for specifying an error or status code. See apr_errno.h for possible values.
   *
   * For investigating specific error values, use the appropriate macros in apr_errno.h to
   * handle platform specific codes.
   */
  apr_status_t status;



  // Function Body ------------------------------------------------------------------------------


  /**
   * Initialize APR. Creates an APR memory pool and updates our 'mempool' pointer to it.
   */
  if ((status = init(&mempool, argc, argv)) != APR_SUCCESS)
  {
    print_error_status(status);
    exit_with_error();
  }

  /**
   * Parse command line options
   *
   * TODO
   */
  if ((status = parse_options(argc, argv, mempool)) != APR_SUCCESS)
  {
    print_error_status(status);
    exit_with_error();
  }


  /**
   * Start the server socket.
   *
   * This is a blocking TCP socket (our concurrency scale requirements are low) so any work
   * should be dished out to a separate thread.
   *
   * Pointer 'serversocket' will be set to point to the created socket structure.
   */
  if ((status = create_server_socket(&serversocket, mempool)) != APR_SUCCESS)
  {
    print_error_status(status);
    exit_with_error();
  }

  /**
   * Start handling incoming client connections
   */
  handle_incoming_connections(serversocket, mempool);

  // Clean exit...

  apr_pool_destroy(mempool);
  apr_terminate();
  exit(0);
}


/**
 * Binds a server socket and starts listening for incoming connections.
 *
 * The style of this function follows the APR API conventions:
 *
 * 1) The first argument 'server_socket' is an 'out' parameter -- the pointer will be initialized
 *    to point to the created server socket which the caller may then use.
 *
 * 2) The second argument is an 'in' parameter that must be a pointer to an already created
 *    APR memory pool.
 */
static apr_status_t create_server_socket(apr_socket_t **serversocket, apr_pool_t *mempool)
{
  /**
   * APR API return type. See the comments in the main() for details.
   */
  apr_status_t status;

  /**
   * Temporary 'out' param for the apr_create_socket() call -- we'll initialize the socket
   * in this function (socket options, etc.) and then pass it back to the caller via the
   * 'serversocket' out param of this function.
   */
  apr_socket_t *newsocket;

  /**
   * An 'out' parameter for apr_sockaddr_info_get() call.
   */
  apr_sockaddr_t *socket_address;

  /**
   * Specify a loopback address for the server socket. This ensures that only connections from
   * the same host are allowed.
   */
  const char *localhost = "127.0.0.1";


  // Function Body --------------------------------------------------------------------------------


  /**
   * Specifying a socket address.
   *
   * 1) socket_address is an 'out' parameter as per APR conventions
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
   *
   * 6) mempool pointer as per the APR API conventions
   */
  if ((status = apr_sockaddr_info_get(
      &socket_address, localhost, APR_INET, config->port, 0, mempool)) != APR_SUCCESS)
  {
    return status;
  }

  /**
   * Create the socket based on the socket address.
   *
   * 1) Socket 'out' parameter.
   *
   * 2) IP address family from socket_address we created earlier.
   *
   * 3 & 4) accept streaming TCP connections (see sockets.h and apr_network_io.h respectively)
   *
   * 5) memory pool pointer as per APR API conventions
   */
  if ((status = apr_socket_create(
      &newsocket, socket_address->family, SOCK_STREAM, APR_PROTO_TCP, mempool)) != APR_SUCCESS)
  {
    return status;
  }

  /**
   * Server socket options:
   *
   *  - blocking socket -- this is not for highly scalable server, we are going to serve one
   *    local (albeit potentially multithreaded) process only
   *
   *  - socket timeout: -1 means reads and writes on this socket will block as well, not sure if
   *    this makes any difference for a listening server socket (it will just spawn client sockets
   *    from [blocking] accept() call)
   */
  if ((status = apr_socket_opt_set(newsocket, APR_SO_NONBLOCK, 0)) != APR_SUCCESS)
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
  if ((status = apr_socket_bind(newsocket, socket_address)) != APR_SUCCESS)
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
   * Set our 'out' param, report success and return APR_SUCCESS
   */
  *serversocket = newsocket;

  printf("OpenRemote I/O Daemon listening on %s:%d...\n", socket_address->hostname, socket_address->port);
  fflush(stdout);

  return APR_SUCCESS;
}


/**
 * TODO
 */
static void handle_incoming_connections(apr_socket_t *serversocket, apr_pool_t *mempool)
{
  /**
   * Pointer to client socket we will get back from accept()
   */
  apr_socket_t *clientsocket;

  /**
   * APR status, see main() for more details.
   */
  apr_status_t status;


  // Function Body --------------------------------------------------------------------------------


  while (TRUE)
  {
    // this will block since we have a blocking server socket...

    if ((status = apr_socket_accept(&clientsocket, serversocket, mempool)) == APR_SUCCESS)
    {
      // TODO : this should be spawned to a new socket asap
      // TODO : also set timeout on blocking client socket I/O
      
      apr_socket_opt_set(clientsocket, APR_SO_NONBLOCK, 0);
      apr_socket_timeout_set(clientsocket, -1);

      read_message(clientsocket, mempool);

      apr_socket_close(clientsocket);
    }

    else
    {
      printf("APR ERROR CODE %d", status);   // TODO
      fflush(stdout);
    }
  }
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
 *       _CONTROL    Control id for giving commands to the daemon itself
 *       R_SERIAL    Serial module to write uninterpreted bytes to
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
static void read_message(apr_socket_t *socket, apr_pool_t *mempool)
{
  static const int moduleid_header_len = 8;
  static const int msglength_header_len = 10;

  char module_id[moduleid_header_len + 1];
  char message_length[msglength_header_len + 1];

  char *payload;
  int char_size = sizeof(char);

  apr_size_t payload_size;
  apr_size_t len;
  apr_status_t status;

  static const char *control_id = "_CONTROL";
  static const char *serial_id  = "R_SERIAL";


  // Function Body --------------------------------------------------------------------------------

  /**
   * Read first eight characters as module ID... (and make it into string on the last extra char)
   */
  len = sizeof(module_id) - char_size;

  status = apr_socket_recv(socket, module_id, &len);

  if (status != APR_SUCCESS || len != moduleid_header_len)
  {
    receive_error(status, socket, len);
    return;
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
    receive_error(status, socket, len);
    return;
  }

  message_length[msglength_header_len] = '\0';
  long int msglen = strtol(message_length, NULL, 16 /* base 16 hex */);

  if (msglen == 0)
  {
    receive_error(-1, socket, -1);
    return;
  }

  payload_size = char_size * msglen + char_size;
  payload = apr_pcalloc(mempool, payload_size);


  /**
   * Finally read in the payload.
   */
  len = payload_size - char_size;

  status = apr_socket_recv(socket, payload, &len);

  if (status != APR_SUCCESS || len != payload_size - char_size)
  {
    receive_error(status, socket, len);
    return;
  }

  printf("======= PAYLOAD: %s\n", payload);
  fflush(stdout);

  
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
    static const char *ping_request = "ARE YOU THERE";
    static const char *ping_response = "I AM HERE";

    static const char *kill_request = "D1ED1ED1E";
    static const char *kill_response = "GOODBYE CRUEL WORLD";

    if (strcmp(payload, ping_request) == 0)
    {
      printf("Responding to a ping...\n");
      fflush(stdout);

      // TODO : check socket send status
      apr_size_t len = strlen(ping_response);
      apr_socket_send(socket, ping_response, &len);

      // TODO : close socket?
    }

    else if (strcmp(payload, kill_request) == 0)
    {
      printf("Shutting down...\n");
      fflush(stdout);

      // TODO : check socket send status
      apr_size_t len = strlen(kill_response);
      apr_socket_send(socket, kill_response, &len);

      apr_socket_close(socket);
      apr_pool_destroy(mempool);
      apr_terminate();
      exit(0);
    }
  }
}


static void receive_error(apr_status_t status, apr_socket_t *client_socket, apr_size_t len)
{
  printf("Receive error: ");

  if (status == -1 && len == -1)
  {
    printf("message size conversion error\n");
  }

  else
  {
    print_error_status(status);
  }

  apr_socket_close(client_socket);
}

/**
 *
 * TODO
 *
 */
static apr_status_t init(apr_pool_t **mempool, int argc, const char *argv[])
{
  /**
   * Type for specifying an error or status code. See apr_errno.h for possible values. If you
   * want to investigate specific error values, use the appropriate macros to handle cross
   * platform issues.
   */
  apr_status_t status;

  /**
   * TODO
   */
  apr_pool_t *newpool;

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

  apr_allocator_t* pool_allocator = apr_pool_allocator_get(newpool);

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


static void print_error_status(apr_status_t status)
{
  char errbuf[1024];

  apr_strerror(status, errbuf, sizeof(errbuf));

  printf("OpenRemote I/O Daemon Error: %d, %s\n", status, errbuf);
  fflush(stdout);
}

static void exit_with_error()
{
  // apr_terminate should also be registered with atexit() but it doesn't
  // seem to hurt to do it twice...

  apr_terminate();

  exit(EXIT_FAILURE);
}

/**
 * TODO
 */
static apr_status_t parse_options(int argc, const char *argv[], apr_pool_t *mempool)
{

  /**
   * TODO
   */
  apr_getopt_t *opt;

  apr_status_t status;

  int option_character;

  const char *option_argument;

  /**
   * TODO
   */
  static const apr_getopt_option_t option_list[] =
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

  config = apr_palloc(mempool, sizeof(config));

  while ((status = apr_getopt_long(opt, option_list, &option_character, &option_argument)) == APR_SUCCESS)
  {
    switch (option_character)
    {
      case 'h':

        print_help_and_exit();

        break;

      case 'p':

        configure_server_port(option_argument);

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
static void print_help_and_exit()
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
static void configure_server_port(const char *port_argument_value)
{
  config->port = DEFAULT_PORT;

  long int port_number;

  /**
   * Strtol() returns the converted value, if any. Zero (FALSE) is returned and errno *may* be set
   * to EINVAL if conversion cannot be made.
   *
   * Using base 0 allows a decimal constant, octal constant or hexadecimal constant preceded by
   * a + or - sign.
   */
  port_number = strtol(port_argument_value, NULL, 0 /* base */);

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
