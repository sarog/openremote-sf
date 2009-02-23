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
// Implementation of control protocol handler. Control protocol is intended for giving commands
// to the daemon itself. Currently the I/O protocol payload for command module is a single string
// command.
//
// Supported commands are:
//
//  - PING ("ARE YOU THERE")
//
//      Only requires "I AM HERE" response, no other action
//
//  - KILL ("D1ED1ED1E")
//
//      Respond with "GOODBYE CRUEL WORLD" and execute an orderly shutdown
//
//
//  Author: Juha Lindfors (juha@juhalindfors.com)
//
// ------------------------------------------------------------------------------------------------

#include <stdlib.h>

#include "org/openremote/controller/daemon/GlobalFunctions.h"
#include "org/openremote/controller/daemon/Log.h"




/**
 * Manages the incoming (D_CONTROL) control module commands to this daemon.
 *
 * @param socket    the client socket to use for sending a response
 * @param payload   control protocol payload (payload field only, not including the
 *                  I/O protocol frame)
 */
int handleControlProtocol(Socket socket, String payload)
{
  static String pingRequest = "ARE YOU THERE";
  static String pingResponse = "I AM HERE";

  static String killRequest = "D1ED1ED1E";
  static String killResponse = "GOODBYE CRUEL WORLD";

  Status status;

  // TODO : strlen checks

  /**
   * PING:
   *   REQ:   ARE YOU THERE
   *   RSP:   I AM HERE
   */
  if (strcmp(payload, pingRequest) == 0)
  {
    logdebug("%s", "Responding to a ping...");

    apr_size_t len = strlen(pingResponse);
    status = apr_socket_send(socket, pingResponse, &len);

    if (status != APR_SUCCESS || len != strlen(pingResponse))
    {
      if (status != APR_SUCCESS)
      {
        logerror("Sending ping response failed: %s", getErrorStatus(status));
      }
      else
      {
        logerror("Failed to send full response, only %d out of %d bytes sent.", \
                 len, strlen(pingResponse));
      }

      return PROTOCOL_SEND_ERROR;
    }
  }

  /**
   * KILL:
   *  REQ:    D1ED1ED1E
   *  RSP:    GOODBYE CRUEL WORLD
   */
  else if (strcmp(payload, killRequest) == 0)
  {
    loginfo("%s", "Shutting down...");

    apr_size_t len = strlen(killResponse);

    if ((status = apr_socket_send(socket, killResponse, &len)) != APR_SUCCESS)
    {
      logerror("Failed to respond to shutdown request: %s", getErrorStatus(status));
    }

    if ((status = apr_socket_close(socket)) != APR_SUCCESS)
    {
      logerror("Failed to close socket: %s", getErrorStatus(status));
    }

    cleanup();
    exit(0);
  }

  return PROTOCOL_MESSAGE_OK;
}

