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
import java.io.InputStream;

import org.openremote.controller.protocol.knx.ServiceTypeIdentifier;

/**
 * This is an implementation of a <tt>SEARCH_RESPONSE</tt> frame in KNXnet/IP v1.0 as
 * defined in KNX 1.1 specifications Volume 3: System Specifications, Part 8: EIBnet/IP,
 * Chapter 1: Overview and Chapter 2: Core. <p>
 *
 * The search response frame is sent back to client's *discovery* address and port which
 * the client included in the Host Protocol Address Information (HPAI) block in the original
 * {@link IpDiscoverReq} request. <p>
 *
 * Included in the search response are gateway/router's *control* address and port in a HPAI
 * block (which the client can use to send control requests, such as connection creation
 * requests), and two Device Information Blocks (DIBs) where the gateway/router includes
 * (first DIB block) its own hardware self-description (manufacturer, serial number,
 * MAC address, etc.) and (second DIB block) description of supported
 * {@link org.openremote.controller.protocol.knx.ServiceTypeIdentifier service family}
 * implementations.
 *
 * The <tt>SEARCH_RESPONSE</tt> frame is therefore:
 *
 * <pre>
 *         +------- ... -------+--------------- ... ---------------+---...
 *         |  KNXnet/IP Header |      Server *Control* HPAI        |
 *         |                   |                                   |
 *         +------- ... -------+--------------- ... ---------------+---...
 *                6 bytes                 8 bytes (IPv4 UDP)
 *
 *   ...---+--------------- ... ---------------+--------------- ... --------------+
 *         |      Device Information DIB       |  Supported Service Families DIB  |
 *         |                                   |                                  |
 *   ...---+--------------- ... ---------------+--------------- ... --------------+
 *                       54 bytes                             n bytes
 * </pre>
 *
 * The full <tt>SEARCH_RESPONSE</tt> frame has therefore an arbitrary size depending on the
 * size of the last supported service families device information block. <p>
 *
 * See {@link IpMessage} for header block field details. See {@link Hpai} for HPAI block
 * field details. See {@link DeviceInformationBlock} for device information block and
 * support service families DIB field details.
 *
 *
 * @see IpMessage
 * @see Hpai
 * @see DeviceInformationBlock
 *
 * @author Olivier Gandit
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class IpDiscoverResp extends IpMessage
{
  // Constants ------------------------------------------------------------------------------------

  /**
   * KNXnet/IP SEARCH_RESPONSE service type identifier : {@value}  <p>
   *
   * This integer value is stored as a two byte value in the KNXnet/IP frame header.
   * The high byte value (0x02) indicates 'Core' service family, and low byte (0x02)
   * indicates search request service.
   */
  public final static int STI = ServiceTypeIdentifier.SEARCH_RESPONSE.getValue();


  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Gateway/router *control* endpoint address and port the client can use for further control
   * requests (such as connection requests).
   */
  private Hpai controlEndpoint;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Reads a KNXnet/IP {@link ServiceTypeIdentifier#SEARCH_RESPONSE} from the given input
   * stream.  <p>
   *
   * The assumption with the input stream is that it has been positioned to read a KNX IP frame
   * after the initial six byte (KNXNet/IP 1.0) header.
   *
   * @param   is        input stream to read the <tt>SEARCH_RESPONSE</tt> IP body from. The input
   *                    stream must contain a valid KNXnet/IP frame and must be positioned to
   *                    read the IP body of the frame after the initial six byte (KNXnet/IP 1.0)
   *                    frame header
   * @param ipBodySize  the size of the IP body segment in this frame -- this includes all the
   *                    frame content minus the six byte (KNXnet/IP 1.0) frame header
   *
   * @throws IOException
   *            if there was an I/O error reading the <tt>SEARCH_RESPONSE</tt> IP body of the frame
   */
  public IpDiscoverResp(InputStream is, int ipBodySize) throws IOException
  {
    super(STI, ipBodySize);

    this.controlEndpoint = new Hpai(is);

    // TODO Read device hardware DIB
    int l = is.read();
    is.skip(l - 1);

    // TODO Read supported service families DIB
    l = is.read();
    is.skip(l - 1);
  }


  // IpMessage Overrides --------------------------------------------------------------------------

  /**
   * Indicates that this frame is a response type.
   *
   * @return  {@link IpMessage.Primitive#RESP}
   */
  @Override public Primitive getPrimitive()
  {
    return Primitive.RESP;
  }


  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * Returns the control endpoint (address and port) of gateway/router. The KNXnet/IP client
   * can use control endpoint for making further control requests, such as connection or
   * connection state request.
   *
   * @return    gateway/router control HPAI
   */
  public Hpai getControlEndpoint()
  {
    return this.controlEndpoint;
  }
}
