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
package org.openremote.controller.protocol.knx;

import org.openremote.controller.protocol.knx.ip.message.IpMessage;

/**
 * Service type identifier field in KNXnet/IP frame header.
 * See {@link org.openremote.controller.protocol.knx.ip.message.IpMessage} for additional
 * detail.  <p>
 *
 * Service type identifier is sent with two bytes in the KNXnet/IP header. The first byte
 * (high byte) indicates a generic service family category and the second (low) byte
 * indicates a specific service type within the family. <p>
 *
 * This implementation is according to KNX 1.1 specification and version 1.0 of the
 * KNXnet/IP protocol. See Volume 3: System Specification, Part 8: EIBnet/IP, Chapter 1:
 * Overview and Chapter 2: Core for more information.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public enum ServiceTypeIdentifier
{


  // Core Service Family...

  /**
   * Request sent by KNXnet/IP client to search for gateways/routers with a multicast broadcast
   * packet (address 224.0.23.12, port 3671).
   */
  SEARCH_REQUEST(0x201),

  /**
   * Response frame sent by gateway/router in response to {@link #SEARCH_REQUEST}. The response
   * will be sent to the client address and port included in the client's
   * {@link org.openremote.controller.protocol.knx.ip.message.Hpai} block in the original
   * <tt>SEARCH_REQUEST</tt>.
   */
  SEARCH_RESPONSE(0x202),

  /**
   * Request that client may send to gateway/router to discover its supported services.
   */
  DESCRIPTION_REQUEST(0x203),

  /**
   * Gateway/router response to a {@link #DESCRIPTION_REQUEST}.
   */
  DESCRIPTION_RESPONSE(0x204),

  /**
   * Request for connection to gateway/router sent by KNXnet/IP client. Client will include
   * host protocol address information (HPAI) for both its control and data endpoints with
   * this request.
   */
  CONNECT_REQUEST(0x205),

  /**
   * Gateway/router response to {@link #CONNECT_REQUEST} sent to client's *control* HPAI address
   * and port. The response also includes a unique channel ID for the communication between
   * the client and gateway/router.
   */
  CONNECT_RESPONSE(0x206),

  /**
   * Connection state request sent by the KNXnet/IP client to gateway/router. This is sent to
   * the gateway/router *control* address and port. Client includes its own control HPAI
   * address and port with the request.
   */
  CONNECTIONSTATE_REQUEST(0x207),

  /**
   * Connection state response from gateway/router in response to {@link #CONNECTIONSTATE_REQUEST}
   * with status code. The gateway/router sends this to the client's *control* address and port
   * defined in the HPAI of the original request.
   */
  CONNECTIONSTATE_RESPONSE(0x208),

  /**
   * Client request to disconnect the communication channel.
   */
  DISCONNECT_REQUEST(0x209),

  /**
   * Gateway/router response to {@link #DISCONNECT_REQUEST}.
   */
  DISCONNECT_RESPONSE(0x20A),


  // Device Management Service Family...

  // TODO
  DEVICE_CONFIGURATION_REQUEST(0x310),

  // TODO
  DEVICE_CONFIGURATION_RESPONSE(0x311),


  // Tunneling Service Family...

  // TODO
  TUNNELING_REQUEST(0x420),

  // TODO
  TUNNELING_ACK(0x421),


  // Routing Service Family...

  // TODO
  ROUTING_INDICATION(0x530),

  // TODO
  ROUTING_LOST_MESSAGE(0x531);
  


  // Enum Instance Fields -------------------------------------------------------------------------

  /**
   * KNX frame header service type identifier (STI).
   */
  private int sti;


  // Enum Constructors ----------------------------------------------------------------------------

  private ServiceTypeIdentifier(int serviceTypeIdentifier)
  {
    this.sti = serviceTypeIdentifier;
  }


  // Enum Instance Methods ------------------------------------------------------------------------

  /**
   * Returns the integer value of this service type identifier. <p>
   *
   * Service type identifier is included in every KNXnet/IP frame header as a two byte value.
   * The high byte ({@code (getValue() >> 8) & 0xFF)}) is a service family identifier. The low
   * byte ({@code getValue() & 0xFF}) is the service identifier within the service family. <p>
   *
   * @return    service type identifier value as an integer
   */
  public int getValue()
  {
    return sti;
  }


  /**
   * Indicates if this service type identifier field is included in the given KNXnet/IP frame.
   *
   * @param     knxFrame    KNXnet/IP frame as byte array
   *
   * @return    true if this service type identifier (STI) is included in the KNX frame header,
   *            false otherwise
   */
  public boolean isIncluded(byte[] knxFrame)
  {
    return knxFrame[IpMessage.KNXNET_IP_10_HEADER_STI_HIBYTE_INDEX] == ((sti >> 8) & 0xFF) &&
           knxFrame[IpMessage.KNXNET_IP_10_HEADER_STI_LOBYTE_INDEX] == (sti & 0xFF);
  }

}

