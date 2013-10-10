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
package org.openremote.controller.protocol.knx.ip.tunnel;

import org.openremote.controller.protocol.knx.IndividualAddress;
import org.openremote.controller.protocol.knx.ip.message.IpConnectReq;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class ConnectionResponseData
{

  // Constants ------------------------------------------------------------------------------------

  public final static int KNXNET_IP_10_TUNNELING_CRD_SIZE = 0x04;


  // Instance Fields ------------------------------------------------------------------------------

  private IndividualAddress individualAddress;


  // Constructors ---------------------------------------------------------------------------------

  public ConnectionResponseData(IndividualAddress address)
  {
    this.individualAddress = address;
  }

  public ConnectionResponseData(byte[] crd)
  {
    if (crd[0] != KNXNET_IP_10_TUNNELING_CRD_SIZE)
    {
      throw new IllegalArgumentException(
          "Expected Connection Response Data (CRD) size " + KNXNET_IP_10_TUNNELING_CRD_SIZE +
          ", got " + crd[0]
      );
    }

    if (crd[1] != IpConnectReq.ConnectionType.TUNNEL_CONNECTION.getValue())
    {
      try
      {
        IpConnectReq.ConnectionType connType = IpConnectReq.ConnectionType.resolve(crd[1]);

        throw new IllegalArgumentException(
            "Attempted to construct tunneling connection response data structure with " +
            "tunnel connection type " + connType
        );
      }

      catch (IpConnectReq.UnknownConnectionTypeException e)
      {
        throw new IllegalArgumentException("Unknown connection type : " + e.getMessage());
      }
    }

    int addrHiByte = crd[2];
    int addrLoByte = crd[3];
    int address = (addrHiByte & 0xFF) << 8;
    address += addrLoByte & 0xFF;

    this.individualAddress = new IndividualAddress(address);
  }

  
  // Instance Methods -----------------------------------------------------------------------------

  public byte[] getFrameStructure()
  {
    byte[] struct = new byte[KNXNET_IP_10_TUNNELING_CRD_SIZE];
    byte[] addr = individualAddress.getAddress();

    struct[0] = KNXNET_IP_10_TUNNELING_CRD_SIZE;
    struct[1] = IpConnectReq.ConnectionType.TUNNEL_CONNECTION.getValue();
    struct[2] = addr[0];
    struct[3] = addr[1];

    return struct;
  }

}

