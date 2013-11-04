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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.openremote.controller.protocol.knx.ServiceTypeIdentifier;
import org.openremote.controller.utils.Strings;

/**
 * This is an implementation of a <tt>CONNECTIONSTATE_REQUEST</tt> frame in KNXnet/IP v1.0 as
 * defined in KNX 1.1 specifications Volume 3: System Specifications, Part 8: EIBnet/IP,
 * Chapter 1: Overview and Chapter 2: Core. <p>
 *
 * Client can send a connection state request to KNXnet/IP server's *control* endpoint address. <p>
 *
 * The frame structure is as follows:
 *
 * <pre>
 *   +-------- ... --------+--------+--------+-------- ... --------+
 *   |  KNXnet/IP Header   |Channel |Reserved|   Client Control    |
 *   |                     |  ID    |        |   Endpoint (HPAI)   |
 *   +-------- ... --------+--------+--------+-------- ... --------+
 *           6 bytes         1 byte   1 byte         8 bytes
 * </pre>
 *
 * KNXnet/IP header details can be found in {@link IpMessage}. Channel ID identifies
 * the connection this request is associated with. Client *control* endpoint is used by
 * the KNXnet/IP server to send its corresponding
 * {@link ServiceTypeIdentifier#CONNECTIONSTATE_RESPONSE} frame.
 *
 *
 * @see Hpai
 * @see IpConnectionStateResp
 * 
 * @author Olivier Gandit
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class IpConnectionStateReq extends IpMessage
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * KNXnet/IP CONNECTIONSTATE_REQUEST service type identifier : {@value}  <p>
   *
   * This integer value is stored as a two byte value in the KNXnet/IP frame header.
   * The high byte value (0x02) indicates 'Core' service family, and low byte (0x07)
   * indicates connect state request service.
   */
  public final static int STI = ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue();

  /**
   * Timeout used by this connection state request to wait for a
   * {@link ServiceTypeIdentifier#CONNECTIONSTATE_RESPONSE} frame to be sent back from the
   * KNXnet/IP gateway/router (in milliseconds) : {@value}
   */
  public final static int KNXNET_IP_10_CONNECTIONSTATE_REQUEST_TIMEOUT = 10000;

  /**
   * Byte order index of the channel id field in KNXnet/IP connection state request frame : {@value}
   */
  public final static int KNXNET_IP_10_CONNECTIONSTATE_REQUEST_CHANNEL_ID_INDEX =
      KNXNET_IP_10_HEADER_SIZE;

  /**
   * Byte order index of the reserved byte field in KNXnet/IP connection state request frame : {@value}
   */
  public final static int KNXNET_IP_10_CONNECTIONSTATE_REQUEST_RESERVED_INDEX =
      KNXNET_IP_10_CONNECTIONSTATE_REQUEST_CHANNEL_ID_INDEX + 1;

  /**
   * Byte order index of the start of the client control point HPAI structure  in KNXnet/IP
   * connection state request frame : {@value}
   */
  public final static int KNXNET_IP_10_CONNECTIONSTATE_REQUEST_CLIENT_HPAI_INDEX =
      KNXNET_IP_10_CONNECTIONSTATE_REQUEST_RESERVED_INDEX + 1;


  /**
   * Fixed frame length of KNXnet/IP 1.0 connection state request frame: {@value}
   */
  public final static int KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH =
      KNXNET_IP_10_HEADER_SIZE + 2 + Hpai.KNXNET_IP_10_HPAI_SIZE;

  /**
   * This is a standard response string returned by {@link #getFrameError(byte[])} method
   * when no errors are detected in the KNX connection state request: "{@value}"
   */
  public final static String VALID_KNXNET_IP_10_CONNECTIONSTATE_REQUEST =
      "<VALID KNXNET/IP 1.0 CONNECTION STATE REQUEST>";


  // Class Members --------------------------------------------------------------------------------

  /**
   * Indicates whether the given KNXnet/IP frame includes a
   * {@link ServiceTypeIdentifier#CONNECTIONSTATE_REQUEST} service type identifier. Note that
   * this implementation assumes KNXnet/IP version 1.0 only, and makes assumptions on frame
   * validity based on that version.
   *
   * @see       {@link #getFrameError(byte[])}
   *
   * @param     knxFrame    KNXnet/IP frame as a byte array
   *
   * @return    true if the frame header includes a <tt>CONNECTIONSTATE_REQUEST</tt> service type
   *            identifier, false otherwise
   */
  public static boolean isConnectionStateRequest(byte[] knxFrame)
  {
    return getFrameError(knxFrame).equals(VALID_KNXNET_IP_10_CONNECTIONSTATE_REQUEST);
  }

  /**
   * This utility method can be used to retrieve a more detailed error message when a KNX
   * connection state request frame cannot be parsed. The returned string will give details
   * about the nature of the frame error.
   *
   * @param   knxFrame  complete KNX/IP frame content
   *
   * @return  {@link #VALID_KNXNET_IP_10_CONNECTIONSTATE_REQUEST} in case there's no error.
   *          Otherwise returns a string describing the nature of the KNX frame error.
   */
  public static String getFrameError(byte[] knxFrame)
  {
    try
    {
      // validate fixed frame size...

      if (knxFrame.length != KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH)
      {
        return "Expecting KNXnet/IP 1.0 connection state request frame with length " +
            KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH + ", received " +
               knxFrame.length + " bytes instead.";
      }

      // validate KNX header fields...

      String headerError = IpMessage.getFrameError(knxFrame);

      if (!headerError.equals(VALID_KNXNET_IP_10_FRAME))
      {
        return headerError;
      }

      // validate KNX frame length field value...

      int lengthHiByte = knxFrame[KNXNET_IP_10_HEADER_FRAME_LENGTH_HIBYTE_INDEX] & 0xFF;
      int lengthLoByte = knxFrame[KNXNET_IP_10_HEADER_FRAME_LENGTH_LOBYTE_INDEX] & 0xFF;

      int len = (lengthHiByte << 8) + lengthLoByte;

      if (len != KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH)
      {
        return "Expected KNX frame lenght field with value " +
            KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH + ", received " + len +
               " bytes instead.";
      }

      // validate HPAI size...

      if (knxFrame[KNXNET_IP_10_CONNECTIONSTATE_REQUEST_CLIENT_HPAI_INDEX] != Hpai.getStructureSize())
      {
        return "KNXnet/IP 1.0 HPAI structure size must be " + Hpai.getStructureSize() + " bytes " +
               "long, KNX frame contains HPAI size field value of " +
               knxFrame[KNXNET_IP_10_CONNECTIONSTATE_REQUEST_CLIENT_HPAI_INDEX] + " instead. " +
               "(Frame: " + Strings.byteArrayToUnsignedHexString(knxFrame) + ")";
      }

      // validate service type identifier (STI) values...

      if (!ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.isIncluded(knxFrame))
      {
        return "KNX connection state request must contain service type identifier " +
               ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.toString() + " (Frame: " +
               Strings.byteArrayToUnsignedHexString(knxFrame) + ")";
      }

      return VALID_KNXNET_IP_10_CONNECTIONSTATE_REQUEST;
    }

    catch (IndexOutOfBoundsException e)
    {
      String msg = "Indexing error: " + e.getMessage();

      log.error(msg, e);

      return msg;
    }
  }


  /**
   * Utility method to parse channel ID out of a KNX connection state request frame.
   *
   * @param knxFrameContent       a byte array containing a full KNX frame (including headers)
   *
   * @return      channel identifier
   *
   * @throws FrameException       if channel ID cannot be parsed from the given KNX frame byte array
   */
  private static int parseChannelID(byte[] knxFrameContent) throws FrameException
  {
    if (knxFrameContent == null)
    {
      throw new FrameException("Null KNX frame.");
    }

    if (!isValidFrame(knxFrameContent))
    {
      throw new FrameException("Invalid KNXnet/IP 1.0 frame: " + getFrameError(knxFrameContent));
    }

    if (!isConnectionStateRequest(knxFrameContent))
    {
      throw new FrameException(
          "Frame {0} is not a KNX connection state request.",
          Strings.byteArrayToUnsignedHexString(knxFrameContent)
      );
    }

    try
    {
      return knxFrameContent[KNXNET_IP_10_CONNECTIONSTATE_REQUEST_CHANNEL_ID_INDEX] & 0xFF;
    }

    catch (ArrayIndexOutOfBoundsException e)
    {
      throw new FrameException(
          "Malformed KNX Frame. Channel ID was not found at index {0} ({1})",
          KNXNET_IP_10_CONNECTIONSTATE_REQUEST_CHANNEL_ID_INDEX,
          Strings.byteArrayToUnsignedHexString(knxFrameContent)
      );
    }
  }


  /**
   * Utility method to parse HPAI from KNX frame.
   *
   * @param knxFrameContent   a byte array containing a full KNX frame (including headers)
   *
   * @return  Host Protocol Address Information
   *
   * @throws FrameException   if HPAI cannot be parsed from the given KNX frame byte array
   */
  private static Hpai parseClientControlEndpoint(byte[] knxFrameContent) throws FrameException
  {
    if (knxFrameContent == null)
    {
      throw new FrameException("Null KNX frame.");
    }

    BufferedInputStream bin = new BufferedInputStream(new ByteArrayInputStream(knxFrameContent));

    int totalSkipBytes = KNXNET_IP_10_CONNECTIONSTATE_REQUEST_CLIENT_HPAI_INDEX;

    try
    {
      long actualBytesSkipped = bin.skip(totalSkipBytes);

      if (actualBytesSkipped != totalSkipBytes)
      {
        throw new FrameException(
            "Failed to parse KNX header. Unexpected byte skip {0}", actualBytesSkipped
        );
      }

      return new Hpai(bin);
    }

    catch (IOException e)
    {
      throw new FrameException("I/O exception in reading the KNX frame: {0}", e, e.getMessage());
    }
  }


  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Connection channel identifier this request is associated with.
   */
  private int channelId;

  /**
   * Client's control endpoint address and port. The KNXnet/IP server's
   * {@link ServiceTypeIdentifier#CONNECTIONSTATE_RESPONSE} will be sent to this address.
   */
  private Hpai controlEndpoint;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new connection state request to be sent to KNXnet/IP gateway/router *control*
   * endpoint address and port. This request frame includes the connection channel identifier and
   * the *client's* control endpoint address where the server will send its connection state
   * response.
   *
   * @see IpConnectionStateResp
   *
   * @param channelId               connection channel identifier
   *
   * @param clientControlEndpoint   client's *control* endpoint address the KNXnet/IP
   *                                gateway/router will use to send its
   *                                {@link ServiceTypeIdentifier#CONNECTIONSTATE_RESPONSE}
   */
  public IpConnectionStateReq(int channelId, Hpai clientControlEndpoint)
  {
    super(STI, 0x0A);

    if (clientControlEndpoint == null)
    {
      throw new IllegalArgumentException("Null HPAI");
    }

    if (channelId < 0 || channelId > 255)
    {
      throw new IllegalArgumentException("Channel ID must be value in range [0...255]");
    }

    this.controlEndpoint = clientControlEndpoint;
    this.channelId = channelId;
  }

  /**
   * Constructs a new <tt>CONNECTION_STATE_REQUEST</tt> from a given KNX frame.
   *
   * @param knxFrameContent   a byte array containing a full KNX frame (including headers)
   *
   * @throws FrameException   if the KNX frame cannot be parsed into a connection state request
   */
  public IpConnectionStateReq(byte[] knxFrameContent) throws FrameException
  {
    this(parseChannelID(knxFrameContent), parseClientControlEndpoint(knxFrameContent));
  }



  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * Creates a connection state response instance matching this request's channel ID and
   * with {@link IpConnectionStateResp.Status#NO_ERROR} status
   *
   * @return    connection state response with <tt>NO_ERROR</tt> status
   */
  public IpConnectionStateResp createNoErrorResponse()
  {
    return new IpConnectionStateResp(getChannelId(), IpConnectionStateResp.Status.NO_ERROR);
  }

  /**
   * Creates a connection state response instance matching this request's channel ID and
   * with {@link IpConnectionStateResp.Status#CONNECTION_ID_ERROR} status.
   *
   * @return    connection state response with <tt>CONNECTION_ID_ERROR</tt> status
   */
  public IpConnectionStateResp createConnectionIDErrorResponse()
  {
    return new IpConnectionStateResp(getChannelId(), IpConnectionStateResp.Status.CONNECTION_ID_ERROR);
  }

  /**
   * Returns the channel id of this connection state request.
   *
   * @return  channel identifier
   */
  public int getChannelId()
  {
    return channelId;
  }


  // IpMessage Overrides --------------------------------------------------------------------------

  /**
   * Indicates that this frame is a request type.
   *
   * @return  {@link IpMessage.Primitive#REQ}
   */
  @Override public Primitive getPrimitive()
  {
    return Primitive.REQ;
  }

  /**
   * The timeout used by client to wait for a {@link ServiceTypeIdentifier#CONNECTIONSTATE_RESPONSE}
   * frame to be sent back from KNXnet/IP gateway/router after sending this connection state
   * request.
   *
   * @return    {@link #KNXNET_IP_10_CONNECTIONSTATE_REQUEST_TIMEOUT}
   */
  @Override public int getSyncSendTimeout()
  {
    return KNXNET_IP_10_CONNECTIONSTATE_REQUEST_TIMEOUT;
  }

  /**
   * Writes {@link ServiceTypeIdentifier#CONNECTIONSTATE_REQUEST} frame to a given output stream.
   *
   * @param os   output stream to write the KNXnet/IP frame to
   *
   * @throws IOException  if there was an I/O error writing the frame
   */
  @Override public void write(OutputStream os) throws IOException
  {
    super.write(os);

    os.write(channelId);
    os.write(0);

    controlEndpoint.write(os);
  }


  // Object Overrides -----------------------------------------------------------------------------

  /**
   * String representation of this connection state request.
   *
   * @return  human readable description of this request
   */
  @Override public String toString()
  {
    try
    {
      return "Connection state request from client control end point " +
             controlEndpoint.getAddress() + ", channel ID " + channelId;
    }

    catch (Throwable t)
    {
      return "Cannot create connection state string: " + t.getMessage();
    }
  }
}
