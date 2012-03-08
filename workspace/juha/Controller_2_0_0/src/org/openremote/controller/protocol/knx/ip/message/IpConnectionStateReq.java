/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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

import org.openremote.controller.protocol.knx.ServiceTypeIdentifier;

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


  // Class Members --------------------------------------------------------------------------------

  /**
   * Indicates whether the given KNXnet/IP frame includes a
   * {@link ServiceTypeIdentifier#CONNECTIONSTATE_REQUEST} service type identifier.
   *
   * @param     knxFrame    KNXnet/IP frame as a byte array
   *
   * @return    true if the frame header includes a <tt>CONNECTIONSTATE_REQUEST</tt> service type
   *            identifier, false otherwise
   */
  public static boolean isConnectionStateRequest(byte[] knxFrame)
  {
    return (knxFrame[KNXNET_IP_10_HEADER_SIZE_INDEX]       == KNXNET_IP_10_HEADER_SIZE &&
            knxFrame[KNXNET_IP_10_HEADER_VERSION_INDEX]    == KNXNET_IP_10_VERSION &&
            ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.isIncluded(knxFrame));
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

    this.controlEndpoint = clientControlEndpoint;
    this.channelId = channelId;
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

}
