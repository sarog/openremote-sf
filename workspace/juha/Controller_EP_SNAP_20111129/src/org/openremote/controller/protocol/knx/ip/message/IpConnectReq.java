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
import org.openremote.controller.utils.Strings;


/**
 * This is an implementation of a {@link ServiceTypeIdentifier#CONNECT_REQUEST} frame
 * for *tunneling* connections for KNXnet/IP v1.0 clients as defined in KNX 1.1 specifications
 * Volume 3: System Specifications, Part 8: EIBnet/IP, Chapter 2: Core and Chapter 4: Tunnelling. <p>
 *
 * Clients will send connection requests to the *control* endpoint address and port
 * of the KNXnet/IP gateway/router. Included in the request is the *client's* control endpoint
 * address and port that the server can use for sending its
 * {@link ServiceTypeIdentifier#CONNECT_RESPONSE} frame. <p>
 *
 * The variable-size structure in a <tt>CONNECT_REQUEST</tt> is Connection Request Information (CRI)
 * block. The content of this block depends on the {@link ConnectionType}. <p>
 *
 * Therefore the generic frame structure for <tt>CONNECT_REQUEST</tt> is as follows:
 *
 * <pre>
 *   +-------- ... --------+-------- ... --------+-------- ... --------+-------- ... --------+
 *   |  KNXnet/IP Header   |   Client Control    |     Client Data     |   Connect Request   |
 *   |                     |   Endpoint (HPAI)   |   Endpoint (HPAI)   |  Information (CRI)  |
 *   +-------- ... --------+-------- ... --------+-------- ... --------+-------- ... --------+
 * </pre>
 *
 * See {@link IpMessage} for details of the KNXnet/IP header structure, and
 * {@link Hpai} for details on the HPAI frame structure. <p>
 *
 * The details of the Connect Request Information structure for a link-layer tunneling
 * connection is as follows:
 *
 * <pre>
 *   +--------+--------+---------+--------+
 *   | Length | Tunnel |KNX Layer|Reserved|
 *   |        |Connect.|         |        |
 *   +--------+--------+---------+--------+
 *     1 byte   1 byte   1 byte    1 byte
 * </pre>
 *
 * Where the length is 4 bytes, tunnel connection is defined in {@link ConnectionType} (for
 * this implementation is fixed to {@link ConnectionType#TUNNEL_CONNECTION}), KNX layer is
 * defined in {@link TunnelingKNXLayer} (for this implementation is fixed to
 * {@link IpConnectReq.TunnelingKNXLayer#TUNNEL_LINKLAYER}). The reserved byte is unused. <p>
 *
 *
 * IMPLEMENTATION NOTE : <br>
 *
 *  Even though this is a specific implementation for tunneling connections only, this
 *  implementation contains generic elements that may be later reused for other types of
 *  connections, for example, {@link ConnectionType#DEVICE_MANAGEMENT_CONNECTION}.
 *
 * @author Olivier Gandit
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class IpConnectReq extends IpMessage
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * KNXnet/IP CONNECT_REQUEST service type identifier : {@value}  <p>
   *
   * This integer value is stored as a two byte value in the KNXnet/IP frame header.
   * The high byte value (0x02) indicates 'Core' service family, and low byte (0x05)
   * indicates connect request service.
   */
  public final static int STI = ServiceTypeIdentifier.CONNECT_REQUEST.getValue();

  /**
   * Fixed Connect Request Information (CRI) structure size for link-layer tunneling connection :
   * {@value}
   */
  public final static int KNXNET_IP_10_LINKLAYER_TUNNELING_CRI_SIZE = 0x04;

  /**
   * Timeout used by this connection request to wait for a
   * {@link ServiceTypeIdentifier#CONNECT_RESPONSE} frame to be sent back from the KNXnet/IP
   * gateway/router (in milliseconds) : {@value}
   */
  public final static int KNXNET_IP_10_CONNECTION_REQUEST_TIMEOUT = 10000;


  /**
   * A fixed Connect Request Information (CRI) frame structure for link-layer tunneling connection.
   */
  private final static byte[] LINKLAYER_TUNNELING_CRI =
  {
      KNXNET_IP_10_LINKLAYER_TUNNELING_CRI_SIZE, ConnectionType.TUNNEL_CONNECTION.getValue(),
      TunnelingKNXLayer.TUNNEL_LINKLAYER.getValue(), 0x00 // reserved
  };
  

  // Enums ----------------------------------------------------------------------------------------

  /**
   * Connection types for KNX connection management.
   */
  public enum ConnectionType
  {

    /**
     * A data connection used for configuring a KNXnet/IP gateway or router.
     */
    DEVICE_MANAGEMENT_CONNECTION(0x03),

    /**
     * A data connection used for sending/receiving KNX telegrams over an IP network.
     */
    TUNNEL_CONNECTION(0x04),

    /**
     * A data connection used for configuring and sending/receiving data from a remote logging
     * server.
     */
    REMOTE_LOGGING_CONNECTION(0x06),

    /**
     * A data connection used for sending/receiving configuration with a remote configuration
     * server.
     */
    REMOTE_CONFIGURATION_CONNECTION(0x07),

    /**
     * A data connection used for configuring and communication with an object server in a
     * KNXnet/IP device.
     */
    OBJECT_SERVER_CONNECTION(0x08);


    /**
     * Resolves connection type byte value from KNXnet/IP
     * {@link ServiceTypeIdentifier#CONNECT_REQUEST} frame into this type-safe Java enum.
     *
     * @param value   the connection type byte from <tt>CONNECT_REQUEST</tt> frame
     *
     * @return    connection type enum constant
     *
     * @throws UnknownConnectionTypeException
     *            if the given byte value in the method parameter cannot be resolved to enum constant
     */
    public static ConnectionType resolve(byte value) throws UnknownConnectionTypeException
    {
      ConnectionType[] types = ConnectionType.values();

      for (ConnectionType type : types)
      {
        if (type.getValue() == value)
        {
          return type;
        }
      }

      throw new UnknownConnectionTypeException(
          "Unknown connection type value : " + Strings.byteToUnsignedHexString(value)
      );
    }


    private byte value;

    private ConnectionType(int value)
    {
      this.value = (byte)(value & 0xFF);
    }


    public byte getValue()
    {
      return value;
    }
  }


  /**
   * KNX layer type for {@link IpConnectReq.ConnectionType#TUNNEL_CONNECTION}.
   */
  enum TunnelingKNXLayer
  {
    /**
     * Link layer tunnel to KNX bus. Used for sending typical group read/write frames to the
     * KNX network.
     */
    TUNNEL_LINKLAYER(0x02),

    /**
     * RAW frame tunnel to KNX bus. RAW frames are mainly used for testing KNX devices. Support
     * for RAW tunnel connection is optional for KNXnet/IP gateways/routers.
     */
    TUNNEL_RAW(0x04),

    /**
     * Busmonitor tunnel to KNX bus.
     */
    TUNNEL_BUSMONITOR(0x80);


    private byte value;

    private TunnelingKNXLayer(int value)
    {
      this.value = (byte)(value & 0xFF);
    }

    byte getValue()
    {
      return value;
    }
  }


  // Class Members --------------------------------------------------------------------------------

  /**
   * Indicates whether the given KNXnet/IP frame includes a <tt>CONNECT_REQUEST</tt> service
   * type identifier.
   *
   * @param     knxFrame    KNXnet/IP frame as a byte array
   *
   * @return    true if the frame header includes a <tt>CONNECT_REQUEST</tt> service type
   *            identifier, false otherwise
   */
  public static boolean isConnectRequest(byte[] knxFrame)
  {
    return (knxFrame[KNXNET_IP_10_HEADER_SIZE_INDEX]       == KNXNET_IP_10_HEADER_SIZE &&
            knxFrame[KNXNET_IP_10_HEADER_VERSION_INDEX]    == KNXNET_IP_10_VERSION &&
            ServiceTypeIdentifier.CONNECT_REQUEST.isIncluded(knxFrame));
  }



  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Client's control endpoint address and port. The KNXnet/IP server's
   * {@link ServiceTypeIdentifier#CONNECT_RESPONSE} will be sent to this address.
   */
  private Hpai controlEndpoint;

  /**
   * Client's data endpoing address and port. The KNXnet/IP gateway/router will send the
   * KNX telegrams (for tunneling connections) to this address and port.
   */
  private Hpai dataEndpoint;


  // Constructors ---------------------------------------------------------------------------------


  /**
   * Constructs a new *tunneling* connection request with client's control and data endpoint
   * address and ports.
   * 
   * @param clientControlEndpoint   Client's control endpoint address and port. The KNXnet/IP
   *                                gateway/router will send its
   *                                {@link ServiceTypeIdentifier#CONNECT_RESPONSE} to this address.
   *
   * @param clientDataEndpoint      Client's data endpoint address and port. The KNXnet/IP
   *                                gateway/router will send KNX telegrams (for tunneling
   *                                connections) to this address.
   */
  public IpConnectReq(Hpai clientControlEndpoint, Hpai clientDataEndpoint)
  {
    super(STI, 0x14);

    this.controlEndpoint = clientControlEndpoint;
    this.dataEndpoint = clientDataEndpoint;
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
   * The timeout used by client to wait for a {@link ServiceTypeIdentifier#CONNECT_RESPONSE} frame
   * to be sent back from KNXnet/IP gateway/router after sending this connection request.
   *
   * @return    {@link #KNXNET_IP_10_CONNECTION_REQUEST_TIMEOUT}
   */
  @Override public int getSyncSendTimeout()
  {
    return KNXNET_IP_10_CONNECTION_REQUEST_TIMEOUT;
  }

  /**
   * Writes a link-layer tunneling {@link ServiceTypeIdentifier#CONNECT_REQUEST} frame to a given
   * output stream.
   *
   * @param os   output stream to write the KNXnet/IP frame to
   *
   * @throws IOException  if there was an I/O error writing the frame
   */
  @Override public void write(OutputStream os) throws IOException
  {
    super.write(os);

    this.controlEndpoint.write(os);
    this.dataEndpoint.write(os);
    os.write(LINKLAYER_TUNNELING_CRI);
  }



  // Nested Classes -------------------------------------------------------------------------------

  public static class UnknownConnectionTypeException extends Exception
  {
    UnknownConnectionTypeException(String msg)
    {
      super(msg);
    }
  }

}
