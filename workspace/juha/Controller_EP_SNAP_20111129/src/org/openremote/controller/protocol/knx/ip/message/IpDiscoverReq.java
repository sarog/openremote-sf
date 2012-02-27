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
 * This is an implementation of a <tt>SEARCH_REQUEST</tt> frame in KNXnet/IP v1.0 as
 * defined in KNX 1.1 specifications Volume 3: System Specifications, Part 8: EIBnet/IP,
 * Chapter 1: Overview and Chapter 2: Core. <p>
 *
 * Search request is sent via multicast to KNX IP gateways and routers. The fixed
 * gateway/router multicast listening address is defined as <tt>224.0.23.12</tt> with
 * port 3671. In order for gateway/router to connect back to the client, the client
 * includes Host Protocol Address Information (HPAI) (see {@link Hpai}) block as part
 * of the frame body. <p>
 *
 * The <tt>SEARCH_REQUEST</tt> frame is therefore:
 *
 * <pre>
 *   +------- ... -------+--------------- ... ---------------+
 *   |  KNXnet/IP Header |            Client HPAI            |
 *   |                   |                                   |
 *   +------- ... -------+--------------- ... ---------------+
 *         6 bytes                 8 bytes (IPv4 UDP)
 * </pre>
 *
 * See {@link IpMessage} for header field details and {@link Hpai} for HPAI field details.
 *
 * @author Olivier Gandit
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class IpDiscoverReq extends IpMessage
{
  // Constants ------------------------------------------------------------------------------------

  /**
   * The amount of time (in milliseconds) the client will wait for a response to a
   * <tt>SEARCH_REQUEST</tt> frame : {@value}
   *
   * <pre>
   * TODO :
   *
   *   Check value -- this is not defined in 3/8/1 Overview or 3/8/2 Core.
   *   In the absence of definition defaulting to 10 seconds seems reasonable
   *   (other timeouts use same 10s value).
   * </pre>
   */
  public final static int SEARCH_TIMEOUT = 10000;

  /**
   * KNXnet/IP SEARCH_REQUEST service type identifier : {@value}  <p>
   *
   * This integer value is stored as a two byte value in the KNXnet/IP frame header.
   * The high byte value (0x02) indicates 'Core' service family, and low byte (0x01)
   * indicates search request service.
   */
  public final static int STI = ServiceTypeIdentifier.SEARCH_REQUEST.getValue();



  // Class Members --------------------------------------------------------------------------------

  /**
   * Indicates whether the given KNXnet/IP frame includes a <tt>SEARCH_REQUEST</tt> service
   * type identifier.
   * 
   * @param     knxFrame    KNXnet/IP frame as a byte array
   *
   * @return    true if the frame header includes a <tt>SEARCH_REQUEST</tt> service type
   *            identifier, false otherwise
   */
  public static boolean isSearchRequest(byte[] knxFrame)
  {
    return (knxFrame[KNXNET_IP_10_HEADER_SIZE_INDEX]       == KNXNET_IP_10_HEADER_SIZE &&
            knxFrame[KNXNET_IP_10_HEADER_VERSION_INDEX]    == KNXNET_IP_10_VERSION &&
            ServiceTypeIdentifier.SEARCH_REQUEST.isIncluded(knxFrame));
  }



  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Client address and port the gateway/router can use to directly send a search response
   * frame to.
   */
  private Hpai discoveryEndpoint;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new <tt>SEARCH_REQUEST</tt> frame with a given Host Protocol Address
   * Information (HPAI)
   *
   * @param hpai    client IP address and port the gateway can send a response frame to
   */
  public IpDiscoverReq(Hpai hpai)
  {
    super(STI, Hpai.getLength());

    this.discoveryEndpoint = hpai;
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
   * Writes a KNXnet/IP <tt>SEARCH_REQUEST</tt> frame to a given output stream. This includes
   * the complete KNXnet/IP frame header plus the client host protocol address information
   * block that listens to possible search response frames.
   *
   * @param os      output stream to write the KNXnet/IP search request frame to
   *
   * @throws IOException
   *                if there was an I/O error while writing to the output stream
   */
  @Override public void write(OutputStream os) throws IOException
  {
    super.write(os);

    this.discoveryEndpoint.write(os);
  }

  /**
   * Returns the timeout value (in milliseconds) that the client waits for a response frame
   * to this search request.
   *
   * @return  {@link #SEARCH_TIMEOUT}
   */
  @Override public int getSyncSendTimeout()
  {
    // TODO : could use TimeUnit instead of int

    return SEARCH_TIMEOUT;
  }
}
