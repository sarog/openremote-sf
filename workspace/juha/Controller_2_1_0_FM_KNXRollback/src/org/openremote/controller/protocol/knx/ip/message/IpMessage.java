/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.protocol.knx.ip.message;

import java.io.IOException;
import java.io.OutputStream;

import org.openremote.controller.utils.Strings;

/**
 * A generic KNXnet/IP tunneling message. Manages the common IP frame header,
 * which includes information about the variable frame length, the KNXnet/IP version used and
 * a service type identifier (STI). The fixed header is included with each written
 * KNXnet/IP message. <p>
 *
 * Each IP message may represent either a request or response message as defined
 * in {@link IpMessage.Primitive}.  <p>
 *
 * This implementation is based on the version 1.1 of the KNX specification as defined in
 * Volume 3: System Specifications, Part 8: EIBnet/IP, Chapter 2: Core. <p> 
 *
 * The structure of the KNXnet/IP 1.0 header is following:
 *
 * <pre>
 *   +--------+--------+----------------+----------------+------- ... -------+
 *   | Header |Version | Service Type   |  Total Frame   |   Frame Body      |
 *   |  Size  |        |  Identifier    |      Size      |                   |
 *   +--------+--------+----------------+----------------+------- ... -------+
 *     1 byte   1 byte      2 bytes          2 bytes            n bytes
 * </pre>
 *
 * The header size for version 1.0 is fixed at 6 bytes (see {@link #KNXNET_IP_10_HEADER_SIZE}). <p>
 *
 * Version identifier for version 1.0 is defined in {@link #KNXNET_IP_10_VERSION}. <p>
 *
 * Service type identifiers are defined in
 * {@link org.openremote.controller.protocol.knx.ServiceTypeIdentifier} enum.  <p>
 *
 * Total frame size should indicate the length of the entire frame in bytes -- including the
 * fixed header size of 6 bytes plus the variable length frame body size. The various other
 * structures within the frame body may have other fields that indicate the length of their
 * individual sub-parts. <p>
 *
 * Frame body varies in content and length depending on the service type identifier. <p>
 *
 * Byte order: the frames are always in big endian byte order.
 *
 * @author Olivier Gandit
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public abstract class IpMessage
{

  // IMPLEMENTATION NOTES:
  //
  //   TODO:
  //
  //     With regards to KNX IP frames, the specification (KNX 1.1 Extensions - Vol. 3, Part 8:
  //     EIBnet/IP Chapter 2: Core) does mention that frame lengths longer than 252 bytes are
  //     possible. Frame structures always start with the length of the structure followed by
  //     some kind of frame type identifier byte. All data follows these two bytes.
  //
  //     However, where data length exceeds 252 bytes, the first byte (length) is marked with
  //     byte 0xFF and the next two bytes contain the structure lenght as a 16-bit (two byte)
  //     value, followed by the frame type identifier, so a total of 4 bytes. Data then starts
  //     at the fifth byte.
  //
  //     None of our implementation currently takes this into account. Should review if there
  //     are implementation cases where it would be possible to exceed the 252 data length.
  //     In most cases the data structure lengths are fixed length or where variable length,
  //     have strict upper limits below the 252 byte mark.
  //                                                                                  [JPL]


  // Constants ------------------------------------------------------------------------------------

  /**
   * Indicates message status (for response messages and acknowledgement messages).
   */
  public final static int OK = 0;

  /**
   * The fixed size of a KNXNet/IP 1.0 frame header : {@value}
   */
  public final static byte KNXNET_IP_10_HEADER_SIZE = 0x06;

  /**
   * The version identifier of KNXnet/IP 1.0 frame :  {@value}
   */
  public final static byte KNXNET_IP_10_VERSION = 0x10;

  
  /**
   * Byte order index of the header size field in KNXnet/IP frame : {@value}
   */
  public final static int KNXNET_IP_10_HEADER_SIZE_INDEX = 0;

  /**
   * Byte order index of the header version field in KNXnet/IP frame : {@value}
   */
  public final static int KNXNET_IP_10_HEADER_VERSION_INDEX = 1;

  /**
   * Byte order index of the Service Type Identifier (STI) high byte in KNXnet/IP frame : {@value}
   */
  public final static int KNXNET_IP_10_HEADER_STI_HIBYTE_INDEX = 2;

  /**
   * Byte order index of the Service Type Identifier (STI) low byte in KNXnet/IP frame : {@value}
   */
  public final static int KNXNET_IP_10_HEADER_STI_LOBYTE_INDEX = 3;

  /**
   * This is a standard response string returned by {@link #getFrameError(byte[])} method
   * when no errors are detected in the KNX frame: "{@value}"
   */
  public final static String VALID_KNXNET_IP_10_FRAME = "<VALID KNXNET/IP 1.0 FRAME>";


  /**
   * The first two bytes of an KNXnet/IP frame header. <p>
   *
   * First byte indicates the header length (not including the variable size IP body) which
   * is a fixed size of 6 bytes in version 1.0 of KNXnet/IP protocol.  <p>
   *
   * The second byte is the version identifier this KNXNet/IP frame adheres to (0x10 stands
   * for version 1.0)
   */
  private static final byte[] HEADER = { KNXNET_IP_10_HEADER_SIZE, KNXNET_IP_10_VERSION };


  // Enums ----------------------------------------------------------------------------------------

  /**
   * Used to indicate the message primitive, either a request message or a response message.
   */
  public static enum Primitive
  {
    /**
     * KNXnet/IP request message
     */
    REQ,

    /**
     * KNXnet/IP response message
     */
    RESP
  }



  // Class Members --------------------------------------------------------------------------------

  /**
   * Does a simple validation on given KNXnet/IP frame content to check if it has a valid
   * header values -- checks for header length, version identifier and total frame size. <p>
   *
   * This method applies to version 1.0 of KNXnet/IP specification.
   *
   * @param     content   KNXnet/IP frame as byte array
   *
   * @return    true if valid frame, false otherwise
   */
  public static boolean isValidFrame(byte[] content)
  {
    // IMPLEMENTATION NOTE:
    //
    //   TODO:
    //     Should abstract the frames at higher level, at minimum could accept a
    //     port package Message implementation instead of byte array. Ideally
    //     though this KNX IP Message implementation would be a port package Message
    //     implementation itself, and we validate frames at construction, i.e.
    //
    //       IpMessage.create(byte[] content) throws InvalidKNXFrameException
    //
    //     Or in subclasses:
    //
    //       SomeRespose.create(byte[] content) throws InvalidResponseException
    //
    //     Where in the latter case the specific Response or Request implementations
    //     can include more detailed frame validation as they are subclasses of
    //     this KNX IP Message.
    //                                                                        [JPL]

    if (content == null)
    {
      return false;
    }

    return getFrameError(content).equals(VALID_KNXNET_IP_10_FRAME);
  }

  /**
   * This utility method can be used to retrieve a more detailed error message when a KNX
   * frame cannot be parsed. The returned string will give details about the nature of
   * the frame error.
   *
   * @param content  complete KNX/IP frame content
   *
   * @return  {@link #VALID_KNXNET_IP_10_FRAME} in case there's no error. Otherwise
   *          returns a string describing the nature of the KNX frame error.
   */
  public static String getFrameError(byte[] content)
  {

    // IMPLEMENTATION NOTE:
    //
    // TODO:
    //    See comments above on the isValidFrame() method on subclassing and
    //    using port package Message abstraction. Same applies here.
    //                                                                    [JPL]
    //
    // TODO:
    //    We could validate the service type identifier bytes to make sure we
    //    recognize it as a known type...

    if (content == null)
    {
      return "KNX frame content was null.";
    }

    if (content.length < 6)
    {
      return "KNX frame must be at least 6 bytes long. Received frame content " +
             "with " + content.length + " bytes.";
    }

    if (content[0] != KNXNET_IP_10_HEADER_SIZE)
    {
      return "KNX IP 1.0 frame header must be 6 bytes long. Received KNX frame that " +
             "claims a frame header with size " + content[0] + ". This frame type is not " +
             "supported.";
    }

    if (content[1] != KNXNET_IP_10_VERSION)
    {
      return "Only KNX IP version 1.0 (frame version byte value " +
             Strings.byteToUnsignedHexString(KNXNET_IP_10_VERSION) + ") is supported. The " +
             "incoming frame has version value " + Strings.byteToUnsignedHexString(content[1]) +
             ". This frame version is not supported.";
    }

    int totalFrameSizeHiByte = content[4] & 0xFF;
    int totalFrameSizeLoByte = content[5] & 0xFF;

    int totalFrameSize = (totalFrameSizeHiByte << 8) + totalFrameSizeLoByte;

    if (totalFrameSize < KNXNET_IP_10_HEADER_SIZE)
    {
      return "The total KNX frame size in the incoming frame is " +
             Integer.toHexString(totalFrameSize) + ". This is below the minimum frame size of " +
             "KNX IP 1.0 frame (" + Strings.byteToUnsignedHexString(KNXNET_IP_10_HEADER_SIZE) +
             " bytes). This frame is not supported.";
    }

    if (totalFrameSize != content.length)
    {
      return
          "Frame length field and actual frame length do not match. Frame lenght field value " +
          "is " + totalFrameSize + " (" + Strings.toUpperCase(Integer.toHexString(totalFrameSize)) +
          ") bytes, actual frame length is " + content.length + " (" +
          Strings.toUpperCase(Integer.toHexString(content.length)) +   ") bytes.";
    }

    return VALID_KNXNET_IP_10_FRAME;
  }


  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Service type identifier for this message.  <p>
   *
   * For example:
   *
   * <pre>
   *   SEARCH_REQUEST           --  0x0201
   *   SEARCH_RESPONSE          --  0x0202
   *   DESCRIPTION_REQUEST      --  0x0203
   *   DESCRIPTION_RESPONSE     --  0x0204
   *   CONNECT_REQUEST          --  0x0205
   *   CONNECT_RESPONSE         --  0x0206
   *   CONNECTIONSTATE_REQUEST  --  0x0207
   *   CONNECTIONSTATE_RESPONSE --  0x0208
   *   DISCONNECT_REQUEST       --  0x0209
   *   DISCONNECT_RESPONSE      --  0x020A
   * </pre>
   */
  private int sti;

  /**
   * The size of the variable length IP body in this KNXnet/IP frame.
   */
  private int variableLength;



  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new KNXnet/IP message with a given service type identifier and a given size of
   * the variable length IP body.
   *
   * @param sti               service type identifier
   * @param variableLength    the length of the variable size IP body in this KNXnet/IP frame
   */
  public IpMessage(int sti, int variableLength)
  {
    this.sti = sti;
    this.variableLength = variableLength;
  }


  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * Writes an KNXnet/IP message *header* to a given output stream.
   *
   * @param os      output stream to write the KNXnet/IP header to
   *
   * @throws IOException
   *                if there was an I/O error writing to the output stream
   */
  public void write(OutputStream os) throws IOException
  {
    // KNXNet/IP frame length and version (two bytes)...

    os.write(HEADER);

    // Service type identifier as a two-byte value...

    int d = this.getServiceTypeIdentifier();
    os.write((d >> 8) & 0xFF);
    os.write(d & 0xFF);

    // Total frame size (two byte value)...

    d = this.getVariableLength() + KNXNET_IP_10_HEADER_SIZE;
    os.write((d >> 8) & 0xFF);
    os.write(d & 0xFF);
  }

  /**
   * Timeout used in connection with request frames to indicate how long to wait for a
   * device response frame or acknowledgement.
   *
   * @return    timeout in milliseconds
   */
  public int getSyncSendTimeout()
  {
    return 0;
  }


  /**
   * Indicates whether this frame is a request or a response frame.
   *
   * @see IpMessage.Primitive
   *
   * @return    this frame's type
   */
  public abstract Primitive getPrimitive();


  // Private Instance Methods ---------------------------------------------------------------------

  /**
   * Returns the service type identifier of this frame.
   *
   * @return  service type identifier
   */
  private int getServiceTypeIdentifier()
  {
    // TODO : can be replaced with direct variable access

    return this.sti;
  }

  /**
   * Returns the length of the IP body in this frame.
   *
   * @return  size of the IP body portion of this KNXNet/IP frame
   */
  private int getVariableLength()
  {
    // TODO : can be replaced with direct variable access

    return this.variableLength;
  }

}
