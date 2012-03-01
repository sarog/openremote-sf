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
import java.io.OutputStream;

import org.openremote.controller.protocol.knx.ServiceTypeIdentifier;
import org.openremote.controller.protocol.knx.dib.SupportedServiceFamily;
import org.openremote.controller.protocol.knx.dib.DeviceInformation;

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
 * requests), and two Description Information Blocks (DIBs). <p>
 *
 * The first description information block includes device information where the gateway/router
 * includes its hardware self-description -- manufacturer, serial number, MAC address, etc.
 * See {@link org.openremote.controller.protocol.knx.dib.DeviceInformation} for more details. <p>
 *
 * The second description information block includes supported
 * {@link org.openremote.controller.protocol.knx.ServiceTypeIdentifier service family}
 * implementations. See {@link SupportedServiceFamily} for more details. <p>
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
 * field details. See {@link org.openremote.controller.protocol.knx.dib.DeviceInformation} for device information block and
 * support service families DIB field details.
 *
 *
 * @see IpMessage
 * @see Hpai
 * @see SupportedServiceFamily
 * @see org.openremote.controller.protocol.knx.dib.DescriptionInformationBlock
 * @see org.openremote.controller.protocol.knx.dib.DeviceInformation
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

  /**
   * Device Information DIB for this KNXnet/IP gateway/router.
   */
  private DeviceInformation deviceInfo;

  /**
   * List of supported services for this KNXnet/IP gateway/router.
   */
  private SupportedServiceFamily supportedServices;



  // Constructors ---------------------------------------------------------------------------------


  /**
   * Constructs a {@link ServiceTypeIdentifier#SEARCH_RESPONSE} frame with given KNXnet/IP
   * gateway/router *control* endpoint address and port, router/gateway device information
   * and a list of supported service families (see {@link ServiceTypeIdentifier.Family}).
   *
   * @see Hpai
   * @see DeviceInformation
   * @see SupportedServiceFamily
   *
   * @param serverControlHPAI   Host Protocol Address Information (HPAI) for the gateway/routers
   *                            *control* endpoint address and port. Client can use the control
   *                            endpoint for further point-to-point requests such as connection
   *                            or connection state requests.
   *
   * @param deviceInfo          Description Information Block (DIB) for Device Information.
   *                            Contains description of this KNXnet/IP devices KNX and IP addresses,
   *                            serial number, name, project and installation codes, etc. See
   *                            {@link DeviceInformation} for further details.
   *
   * @param supportedServices   List of services supported by this KNXnet/IP gateway/router.
   *                            See {@link ServiceTypeIdentifier.Family} for additional details.
   */
  public IpDiscoverResp(Hpai serverControlHPAI, DeviceInformation deviceInfo,
                        SupportedServiceFamily supportedServices)
  {
    super(ServiceTypeIdentifier.SEARCH_RESPONSE.getValue(),
          Hpai.getStructureSize() +
          deviceInfo.getStructureSize() + 
          supportedServices.getStructureSize()
    );

    this.controlEndpoint = serverControlHPAI;
    this.deviceInfo = deviceInfo;
    this.supportedServices = supportedServices;
  }
  
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

    // TODO : http://jira.openremote.org/browse/ORCJAVA-216 -- Read device hardware DIB
    int l = is.read();
    is.skip(l - 1);

    // TODO : http://jira.openremote.org/browse/ORCJAVA-216 -- Read supported service families DIB
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

  /**
   * Writes a {@link ServiceTypeIdentifier#SEARCH_RESPONSE} KNXnet/IP frame to a given
   * output stream.
   *
   * @param out   output stream to write the KNXnet/IP frame to
   *
   * @throws IOException  if there was an I/O error writing the frame
   */
  @Override public void write(OutputStream out) throws IOException
  {
    super.write(out);                                 // write the KNXnet/IP frame header...

    controlEndpoint.write(out);                       // write HPAI structure bytes...

    out.write(deviceInfo.getFrameStructure());        // write device info DIB bytes...

    out.write(supportedServices.getFrameStructure()); // write supported service DIB bytes...
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
