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
//   Implementation of the I/O daemon serial protocol handler.
//
//   The serial protocol payload is string based. Fields are separated by zero byte to determine
//   string ends.
//
//   See IOProtocol for details how this serial payload fits into IOProtocol frame. What is
//   described below is the IOProtocol's payload field.
//
//   The expected structure of serial (R_SERIAL) payload is as follows:
//
//   +----------/.../----------+--+------------/.../------------+--+
//   |      Command String     |\0|     Command Parameters      |\0|
//   |        (n bytes)        |  |        (n bytes)            |  |
//   +----------/.../----------+--+------------/.../------------+--+
//   ^                                                             ^
//   | (total size specified by IOProtocol Message Length header)  |
//   +-------------------------------------------------------------+
//
//   The payload is divided into two fields. First is a command string which determines what
//   parameters are expected in the second command parameters string and parses them accordingly.
//   The structure of the command parameters is not defined and depends on the command
//   implementation.
//
//   The fields must be separated by zero byte and the payload must end with a zero byte. The
//   total length of the payload, including the ending zero byte, must not exceed the length
//   specified with the message length header of the I/O protocol frame.
//
//   Currently supported serial commands are:
//
//     - "OPEN PORT (PortIdentifier, BaudRate, DataBits, Parity, StopBits)"
//
//
//  Author: Juha Lindfors (juha@juhalindfors.com)
//
// ------------------------------------------------------------------------------------------------


#include "org/openremote/controller/daemon/GlobalFunctions.h"
#include "org/openremote/controller/daemon/SerialProtocol.h"
#include "org/openremote/controller/daemon/SerialProtocolHandler.h"
#include "org/openremote/controller/daemon/Log.h"



/**
 *  The main parsing routine for I/O serial protocol.
 *
 *  @param  socket    TODO
 *  @param  payload   TODO
 *
 */
ProtocolStatus handleSerialProtocol(Socket socket, String payload)
{
  static String openPortCommand = "OPEN PORT";

  size_t openPortCommandLength = strlen(openPortCommand);
  String command = payload;

  logtrace("Serial protocol payload command: '%s'", payload);

  /**
   * Parse "OPEN PORT" command.
   */
  if (strncmp(command, openPortCommand, openPortCommandLength) == 0)
  {
    String commandParameters = payload + openPortCommandLength + 1;

    // TODO : ProtocolStatus
    handleOpenPortCommand(commandParameters);
  }
}


// LOCAL FUNCTIONS --------------------------------------------------------------------------------


/**
 * Parses the command parameters from the "OPEN PORT" command.
 *
 * The command parameters for open port command are string based, and each parameter is
 * separated by a zero byte to mark the string end.
 *
 * The structure of open port parameters are as follows:
 *
 * +--------/.../--------+--+--------------------+--+------+
 * |  "Port Identifier"  |\0|     Baud Rate      |\0| DPS  |
 * |    (n bytes)        |  |    (10 bytes)      |  |  3   |
 * |                     |  |                    |  | bytes|
 * +--------/.../--------+--+--------------------+--+------+
 *
 * Notice that the last DPS string will have its ending zero byte coming from the
 * "Command Parameters" field-end of the serial I/O payload.
 *
 * The port identifier is a system specific string for the serial port. On Linux something
 * like "/dev/ttyS0", on Cygwin could be "/dev/com3", etc. Arbitrary length string is allowed,
 * ending with a zero byte.
 *
 * The baudrate is a 10 character hexadecimal string value. It must start with a leading '0x'
 * prefix and small values must be padded with zeroes to make the string exactly 10 characters
 * long.
 *
 * TODO : make the baud rate requirements less stringent
 *
 * The last string is DPS which stands for Data bits, Parity, Stop bits. It must be 3 characters
 * long. A typical value could be "8N1" or "7E1" meaning 8 databits, no parity, one stop bit or
 * seven databits, even parity, one stop bit respectively.
 *
 * The valid values for databits are currently characters "5", "6", "7", "8" representing five,
 * six, seven or eight databits.
 *
 * Valid values for parity are characters "N", "E", "O" representing no parity, even parity
 * and odd parity, respectively.
 *
 * Valid values for stop bit are characters "1", "2", "9", representing one stop bit, two stop
 * bits or one and half stop bits, respectively.
 *
 * @param commandParameters TODO
 */
static ProtocolStatus handleOpenPortCommand(String commandParameters)
{
  logtrace("%s", "Parsing command parameters for serial 'OPEN PORT' command.");

  /**
   * Get the port identifier string.
   */
  String portIdentifier = commandParameters;
  size_t portIdentifierLength = strlen(portIdentifier);

  size_t offset = portIdentifierLength + 1;

  /**
   * Baud rate string. It must always be 10 characters long.
   */
  String baudrate = commandParameters + offset;
  size_t baudrateLength = 10;

  if (strlen(baudrate) != baudrateLength)
  {
    logerror(
        "PROTOCOL ERROR: baudrate length was %d bytes, was expecting %d bytes",
        strlen(baudrate), baudrateLength
    );

    return PROTOCOL_PARSE_ERROR;
  }

  offset += baudrateLength + 1;

  /**
   * Databits, parity, stopbits string.
   */
  String dps = commandParameters + offset;
  size_t dpsLength = 3;

  if (strlen(dps) != dpsLength)
  {
    logerror(
        "PROTOCOL ERROR: dps length was %d bytes, was expecting %d bytes",
        strlen(dps), dpsLength
    );

    return PROTOCOL_PARSE_ERROR;
  }

  logtrace("Parsing DPS '%s'.", dps);

  /**
   * Parse DPS
   */
  ProtocolStatus status = -1;
  DataBits databits     = -1;
  Parity parity         = -1;
  StopBits stopbits     = -1;

  if ((status = parseDataBits(&databits, dps[0])) != PROTOCOL_MESSAGE_OK)
  {
    return status;
  }

  if ((status = parseParity(&parity, dps[1])) != PROTOCOL_MESSAGE_OK)
  {
    return status;
  }

  if ((status = parseStopBits(&stopbits, dps[2])) != PROTOCOL_MESSAGE_OK)
  {
    return status;
  }

  logtrace("Databits: %d, Parity: %d, Stopbits: %d", databits, parity, stopbits);

  /*
  // TODO
  openSerialPort(portIdentifier, baudrate, dataBits, parity, stopBits);
  */
}

/**
 * Match incoming character from serial protocol to DataBit enum value.
 *
 * @param   result  result parameter: pointer to an allocated DataBits enum which will be updated
 *                  with the translated enum constant
 *
 * @param   c       the character to translate
 *
 * @return          PROTOCOL_MESSAGE_OK if translation successful, PROTOCOL_PARSE_ERROR otherwise
 */
static ProtocolStatus parseDataBits(DataBitsResult result, char c)
{
  logtrace("Resolving data bits from '%c'...", c);

  switch (c)
  {
    case '5':
      *result = FIVE;
      return PROTOCOL_MESSAGE_OK;

    case '6':
      *result = SIX;
      return PROTOCOL_MESSAGE_OK;

    case '7':
      *result = SEVEN;
      return PROTOCOL_MESSAGE_OK;

    case '8':
      *result = EIGHT;
      return PROTOCOL_MESSAGE_OK;

    default:
      logerror("PROTOCOL ERROR: unrecognized databit value '%d'", c);
      return PROTOCOL_PARSE_ERROR;
  }
}


/**
 * Simple translation of serial I/O protocol to StopBits enum value.
 *
 * @param result    result parameter: pointer to an allocated StopBits enum which will be updated
 *                  with the translated enum constant
 *
 * @param c         the character to translate
 *
 * @return          PROTOCOL_MESSAGE_OK if translation successful, PROTOCOL_PARSE_ERROR otherwise
 */
static ProtocolStatus parseStopBits(StopBitsResult result, char c)
{
  logtrace("Resolving stop bits from '%c'...", c);

  switch (c)
  {
    case '1':
      *result = ONE;
      return PROTOCOL_MESSAGE_OK;

    case '2':
      *result = TWO;
      return PROTOCOL_MESSAGE_OK;

    case '9':
      *result = ONE_HALF;
      return PROTOCOL_MESSAGE_OK;

    default:
      logerror("PROTOCOL ERROR: unrecognized stop bits value '%d'", c);
      return PROTOCOL_PARSE_ERROR;
  }
}

/**
 * Simple translation of serial I/O protocol to Parity enum value.
 *
 * @param result  result parameter: pointer to an allocated Parity enum which will be updated
 *                with the translated enum constant
 *
 * @param c       the character to translate
 *
 * @return        PROTOCOL_MESSAGE_OK if translation successful, PROTOCOL_PARSE_ERROR otherwise
 */
static ProtocolStatus parseParity(ParityResult result, char c)
{
  logtrace("Resolving parity from '%c'...", c);

  switch (c)
  {
    case 'N':
      *result = NONE;
      return PROTOCOL_MESSAGE_OK;

    case 'O':
      *result = ODD;
      return PROTOCOL_MESSAGE_OK;

    case 'E':
      *result = EVEN;
      return PROTOCOL_MESSAGE_OK;

    default:
      logerror("PROTOCOL ERROR: unrecognized parity value '%d'", c);
      return PROTOCOL_PARSE_ERROR;
  }
}
