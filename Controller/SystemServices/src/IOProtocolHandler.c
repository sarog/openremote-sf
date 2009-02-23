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
// Implements the handling of incoming I/O message.
//
// The I/O message structure is ASCII character based for the sake of simplicity and expects
// the following format:
//
// +----------------+--------------------+------------/.../------------+
// |  [Module ID]   |  [Payload Length]  |     Arbitrary payload       |
// |   (8 bytes)    |     (10 bytes)     |                             |
// +----------------+--------------------+------------/.../------------+
//
//
//  - First 8 characters are a module identifier. This indicates what I/O module the message
//    is intended for. Current valid values are:
//
//       DCONTROL    Control id for giving commands to the daemon itself
//       R_SERIAL    Serial module to write uninterpreted (raw) bytes to serial port(s)
//
//
//  - Next 10 characters are a message payload length. This must be a correct value for the
//    length of the rest of the message payload.
//
//    Message length is a 10 character long hexadecimal value, in uppercase and with leading
//    '0X' characters. It must always be exactly 10 characters long so the actual value is padded
//    with leading zeroes. Payload size zero is *not* a valid value.
//
//    Examples of valid values are:
//
//       '0X0000DEAD', '0XCAFEBABE' and '0X0000000D'
//
//  - The remainder of the message string is the I/O module specific payload. The length is
//    determined by the previous message payload length header. The content of the payload
//    is specific to the I/O module. See below for protocol details of each module.
//
//
// Author: Juha Lindfors (juha@juhalindfors.com)
//
//
// TODO : make the message length header format less stringent
// TODO : make it string based with \0 separator for fields
//
// ------------------------------------------------------------------------------------------------

#include <stdlib.h>

#include "org/openremote/controller/daemon/GlobalFunctions.h"
#include "org/openremote/controller/daemon/Log.h"



/**
 * TODO
 */
int readMessage(Socket socket, MemPool memoryPool)
{
  static const int moduleidHeaderLength = 8;
  static const int msglengthHeaderLength = 10;

  static String controlIDString = "DCONTROL";
  static String serialIDString  = "R_SERIAL";

  char moduleIDHeader[moduleidHeaderLength + 1];
  char msgLengthHeader[msglengthHeaderLength + 1];
  char *payload;

  apr_size_t payloadSize;
  apr_size_t len;
  Status status;


  /**
   * Read first eight characters as module ID... (and make it into string on the last extra char)
   */
  len = sizeof(moduleIDHeader) - charSize;

  status = apr_socket_recv(socket, moduleIDHeader, &len);

  if (status != APR_SUCCESS || len != moduleidHeaderLength)
  {
    if (status != APR_SUCCESS)
    {
      logerror("Receive error: %s", getErrorStatus(status));
    }

    else
    {
      moduleIDHeader[len] = '\0';
      logerror("Was expecting %d bytes of module ID header, got %d instead ('%s')", \
               moduleidHeaderLength, len, moduleIDHeader);
    }

    return PROTOCOL_RECEIVE_ERROR;
  }

  moduleIDHeader[moduleidHeaderLength] = '\0';


  /**
   * Read following ten characters as message payload length hex string and convert to int,
   * then allocate required memory chunk for the payload from the pool...
   */
  len = sizeof(msgLengthHeader) - charSize;

  status = apr_socket_recv(socket, msgLengthHeader, &len);

  if (status != APR_SUCCESS || len != msglengthHeaderLength)
  {
    if (status != APR_SUCCESS)
    {
      logerror("Receive error: %s", getErrorStatus(status));
    }

    else
    {
      msgLengthHeader[len] = '\0';
      logerror("Was expecting %d bytes of message length header, got %d instead ('%s')", \
               msglengthHeaderLength, len, msgLengthHeader);
    }

    return PROTOCOL_RECEIVE_ERROR;
  }

  msgLengthHeader[msglengthHeaderLength] = '\0';
  long int msglen = strtol(msgLengthHeader, NULL, 16 /* base 16 hex */);

  if (msglen == 0)
  {
    logerror("Unable to convert '%s' to integer.", msgLengthHeader);

    return PROTOCOL_RECEIVE_ERROR;
  }

  payloadSize = charSize * msglen + charSize;
  payload = apr_pcalloc(memoryPool, payloadSize);

  /**
   * Finally read in the payload.
   */
  len = payloadSize - charSize;

  status = apr_socket_recv(socket, payload, &len);

  if (status != APR_SUCCESS || len != payloadSize - charSize)
  {
    if (status != APR_SUCCESS)
    {
      logerror("Receive error: %s", getErrorStatus(status));
    }

    else
    {
      payload[len] = '\0';
      logerror("Was expecting %d bytes of payload, got %d instead ('%s')", \
                payloadSize - charSize, len, payload);
    }

    return PROTOCOL_RECEIVE_ERROR;
  }

  payload[len] = '\0';

  logdebug("PAYLOAD: %s", payload);

  /**
   * Control Protocol
   */
  if (strncmp(moduleIDHeader, controlIDString, 8) == 0)
  {
    handleControlProtocol(socket, payload);
  }

  /**
   * Serial Protocol
   */
  else if (strncmp(moduleIDHeader, serialIDString, 8) == 0)
  {
    handleSerialProtocol(socket, payload);
  }

  return PROTOCOL_MESSAGE_OK;
}

