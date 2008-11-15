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


/**
 * This class defines helper methods to construct I/O protocol messages to be passed to the
 * native I/O daemon.  <p>
 *
 * The I/O protocol message is defined as follows:
 *
 * <pre>
 *   +----------------+--------------------+--------------------/.../--------------------+
 *   | IO Module ID   | Message Length     |  Arbitrary Payload                          |
 *   | 8 byte string  | 10 byte hex string |  n byte string (n specified by msg len hdr) |
 *   +----------------+--------------------+---------------------------------------------+
 * </pre>
 *
 * The first 8 byte ASCII string is an I/O module identifier (specified in
 * {@link org.openremote.controller.daemon.IOModule}). This string identifies which I/O handler
 * on the native I/O daemon should attempt to parse the contents of the payload.   <p>
 *
 * The next 10 byte ASCII string is a hexadecimal message length header. This header specifies
 * the byte length of the payload that will follow. See {@link #getMessageLength(int)} for
 * details on the formatting of this string.  <p>
 *
 * The last n bytes are an ASCII string payload. The number of bytes must match the number of
 * bytes specified in the message length header. The content of the payload depends on the
 * targeted I/O module ID (specified by the first header). The details of payload structures
 * are documented in their corresponding protocol implementations (for example, see
 * {@link org.openremote.controller.daemon.SerialProtocol}).
 *
 * @see org.openremote.controller.daemon.IOModule
 * @see org.openremote.controller.daemon.SerialProtocol
 * @see org.openremote.controller.daemon.ControlProtocol
 *
 * @author <a href = "mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class IOProtocol
{

  /**
   * Creates a byte array that is a valid I/O protocol message. A valid I/O protocol message
   * starts with a 8 byte I/O module identifier header, followed by a 10 byte message length
   * header and ends with an arbitrary length payload.
   *
   * @param module    I/O module identifier header for this message
   * @param payload   I/O module payload for this message
   *
   * @return  byte array that contains the a valid I/O protocol message with required headers
   *          and payload in its wire serialization format
   */
  protected static byte[] createMessage(IOModule module, byte[] payload)
  {
    return (module.getModuleID() +
            getMessageLength(payload.length) +
            new String(payload)).getBytes();
  }

  /**
   * See description in {@link #createMessage(IOModule, byte[])}.
   *
   * @param module    I/O module identifier header for this message
   * @param payload   I/O module payload for this message
   *
   * @return  byte array that contains the a valid I/O protocol message with required headers
   *          and payload in its wire serialization format
   */
  protected static byte[] createMessage(IOModule module, String payload)
  {
    return createMessage(module, payload.getBytes());
  }

  /**
   * Returns message length field for message header as specified in the protocol -- an
   * uppercase hex value with leading '0X' including leading zeroes up to string of length 10.
   *
   * @param value integer value to translate
   *
   * @return a ten character long hex string in uppercase with leading zeroes, such as
   *         '0X0000DEAD', '0XCAFEBABE' or '0X00000005'
   */
  protected static String getMessageLength(int value)
  {
    String hexValue = Integer.toHexString(value).toUpperCase();

    // add leading zeroes....

    if (value <= 0xF)
      return "0X0000000" + hexValue;
    if (value <= 0xFF)
      return "0X000000" + hexValue;
    if (value <= 0xFFF)
      return "0X00000" + hexValue;
    if (value <= 0xFFFF)
      return "0X0000" + hexValue;
    if (value <= 0xFFFFF)
      return "0X000" + hexValue;
    if (value <= 0xFFFFFF)
      return "0X00" + hexValue;
    if (value <= 0xFFFFFFF)
      return "0X0" + hexValue;

    return "0X" + hexValue;
  }
}
