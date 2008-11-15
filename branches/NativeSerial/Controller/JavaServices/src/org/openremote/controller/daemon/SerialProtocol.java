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
package org.openremote.controller.daemon;

import java.util.Arrays;

import org.jboss.logging.Logger;
import org.openremote.controller.core.Bootstrap;


/**
 * This class defines the socket protocol between Java services and native I/O daemon for serial
 * port communication. The class defines necessary enums, constants and factory methods to create
 * the payload for I/O protocol message targeted at serial modules (as defined by
 * {@link org.openremote.controller.daemon.IOModule#RAW_SERIAL}). 
 *
 * @author <a href = "mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class SerialProtocol
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * Log category name for this component. The actual full log category can be constructed by
   * appending {@link org.openremote.controller.core.Bootstrap#ROOT_LOG_CATEGORY} and this
   * string using a dot '.' separator (ROOT_LOG_CATEGORY.LOG_CATEGORY).
   *
   * Value: {@value}
   */
  public final static String LOG_CATEGORY = "SERIAL PROTOCOL";


  // Enums ----------------------------------------------------------------------------------------

  /**
   * Defines parity for serial port.
   */
  public enum Parity
  {
    EVEN ("E"),
    ODD  ("O"),
    NONE ("N");

    private String serialFormat;

    /**
     * Stores the serial format for a given parity.
     *
     * @param serialFormat  string serial format used with the socket connection to native
     *                      I/O daemon
     */
    private Parity(String serialFormat)
    {
      this.serialFormat = serialFormat;
    }

    /**
     * Returns the string serial format of a given parity.
     *
     * @return a string containing the bytes for serial module message payloads
     */
    private String getSerialFormat()
    {
      return serialFormat;
    }
  }

  /**
   * Defines stop bits for serial port.
   */
  public enum StopBits
  {
    ONE ("1"),
    TWO ("2"),
    ONE_HALF ("9");

    private String serialFormat;

    /**
     * Stores the serial format for given stop bit.
     *
     * @param serialFormat  string serial format used with the socket connection to native
     *                      I/O daemon
     */
    private StopBits(String serialFormat)
    {
      this.serialFormat = serialFormat;
    }

    /**
     * Returns the serial format for a given stop bit. Notice that character '9' is used to
     * represent {@link #ONE_HALF}.
     *
     * @return a string containing the bytes for serial module message payloads
     */
    private String getSerialFormat()
    {
      return serialFormat;
    }
  }

  /**
   * Defines data bits for serial port.
   */
  public enum DataBits
  {
    FIVE  ("5"),
    SIX   ("6"),
    SEVEN ("7"),
    EIGHT ("8");

    private String serialFormat;

    /**
     * Stores the serial format for given data bit.
     *
     * @param serialFormat  string serial format used with the socket connection to native
     *                      I/O daemon
     */
    private DataBits(String serialFormat)
    {
      this.serialFormat = serialFormat;
    }

    /**
     * Returns the serial format for a given data bit.
     *
     * @return a string containing the bytes for serial module message payloads
     */
    public String getSerialFormat()
    {
      return serialFormat;
    }
  }

  /**
   * TODO
   */
  public enum FlowControl
  {
    SOFTWARE,
    HARDWARE
  }



  // Class Members --------------------------------------------------------------------------------

  /**
   * Logger API for this component. Currently uses the JBoss logging API.
   */
  private final static Logger log = Logger.getLogger(Bootstrap.ROOT_LOG_CATEGORY + "." + LOG_CATEGORY);

  /**
   * Serial I/O module command identifier for opening a new port.
   */
  private final static byte[] openPortCommand = "OPEN PORT".getBytes();

  /**
   * A field separator used in serial I/O command payload. Currently is a zero byte which is
   * interpreted as end of string by native I/O daemon.
   */
  private final static byte[] fieldSeparator = new byte[] { 0 };


  /**
   * TODO
   *
   * The payload for serial 'open port' command is as follows:
   *
   * <pre>
   * +------------------+--+------/.../------+--+--------------------+--+------+
   * |   "OPEN PORT"    |\0|   [Port ID]     |\0|   Baud Rate        |\0| DPS  |
   * |  (9 byte string) |  | Arbitrary len.  |  |  (10 byte string)  |  | 3 b  |
   * |                  |  | e.g /dev/ttyS0  |  |                    |  |      |
   * +------------------+--+------/.../------+--+--------------------+--+------+
   * </pre>
   *
   * Fields in the payload are separated by a zero byte (string end identifier in C). <p>
   *
   * The command identifier ("OPEN PORT") is defined in {@link #openPortCommand}. It is followed
   * by an arbitrary length port identifier which is operating system dependent. For example,
   * on Linux this string might contain value "/dev/ttyS0", on Windows it could contain "COM1". <p>
   *
   * The port identifier is followed by desired baud rate. This must be a 10 byte ASCII string
   * value in hexadecimal base starting with a hexadecimal value prefix defined in
   * {@link org.openremote.controller.daemon.IOProtocol#HEXADECIMAL_VALUE_PREFIX}. Values must
   * always be padded up to 10 characters long.  <p>
   *
   * The last field contains a 3 byte string representation of the serial port data bits, parity
   * and stop bits in that order. <p>
   *
   * The first byte is a string value of serial connection data bits, for example "8".  <p>
   *
   * The second byte is the parity, one of {@link Parity#ODD}, {@link Parity#EVEN}
   * or {@link Parity#NONE}. The corresponding character values are 'O', 'E' and 'N'.  <p>
   *
   * The third byte is number of stop bits, either {@link StopBits#ONE}, {@link StopBits#TWO}
   * or {@link StopBits#ONE_HALF}, characters '1', '2', '9', respectively.  <p>
   *
   * Therefore, a serial connection to be opened with 8 databits, no parity and single stop bit
   * would be represented by DPS string '8N1'.
   *
   * @param portID
   * @param baudrate
   * @param databits
   * @param parity
   * @param stopBits
   * @return
   */
  public static byte[] createOpenPortMessage(String portID, int baudrate, DataBits databits,
                                             Parity parity, StopBits stopBits)
  {
    final int COMMAND_SIZE    = openPortCommand.length;
    final int SEPARATOR_SIZE  = fieldSeparator.length;
    final int PORT_ID_SIZE    = portID.getBytes().length;
    final int BAUDRATE_SIZE   = 10;
    final int DPS_SIZE        = 3;

    final int COMMAND_OFFSET = 0;
    final int FIRST_SEPARATOR_OFFSET  = COMMAND_SIZE;
    final int PORT_ID_OFFSET          = FIRST_SEPARATOR_OFFSET + SEPARATOR_SIZE;
    final int SECOND_SEPARATOR_OFFSET = PORT_ID_OFFSET + PORT_ID_SIZE;
    final int BAUDRATE_OFFSET         = SECOND_SEPARATOR_OFFSET + SEPARATOR_SIZE;
    final int THIRD_SEPARATOR_OFFSET  = BAUDRATE_OFFSET + BAUDRATE_SIZE;
    final int DPS_OFFSET              = THIRD_SEPARATOR_OFFSET + SEPARATOR_SIZE;

    int arraySize = DPS_OFFSET + DPS_SIZE;

    byte[] message  = new byte[arraySize];
    byte[] bauds    = IOProtocol.createHexString(baudrate, BAUDRATE_SIZE, true, true).getBytes();
    byte[] dps      = (databits.getSerialFormat() + parity.getSerialFormat() + stopBits.getSerialFormat()).getBytes();

    System.arraycopy(openPortCommand, 0, message, COMMAND_OFFSET, COMMAND_SIZE);
    System.arraycopy(fieldSeparator, 0, message, FIRST_SEPARATOR_OFFSET, SEPARATOR_SIZE);
    System.arraycopy(portID.getBytes(), 0, message, PORT_ID_OFFSET, PORT_ID_SIZE);
    System.arraycopy(fieldSeparator, 0, message, SECOND_SEPARATOR_OFFSET, SEPARATOR_SIZE);
    System.arraycopy(bauds, 0, message, BAUDRATE_OFFSET, BAUDRATE_SIZE);
    System.arraycopy(fieldSeparator, 0, message, THIRD_SEPARATOR_OFFSET, SEPARATOR_SIZE);
    System.arraycopy(dps, 0, message, DPS_OFFSET, DPS_SIZE);

    log.debug(new String(message));

    return message;
  }

}
