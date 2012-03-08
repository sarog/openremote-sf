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
import org.openremote.controller.protocol.knx.IndividualAddress;
import org.openremote.controller.protocol.knx.tunnel.ConnectionResponseData;
import org.openremote.controller.utils.Strings;


/**
 * This is an implementation of a {@link ServiceTypeIdentifier#CONNECT_RESPONSE} frame
 * for KNXnet/IP v1.0 clients as defined in KNX 1.1 specifications Volume 3: System Specifications,
 * Part 8: EIBnet/IP, Chapter 2: Core and Chapter 4: Tunnelling. <p>
 *
 * The connect response is sent back by KNXnet/IP gateway or router in response to a connect
 * request sent by the client. It will be sent back to the client's *control* endpoint address
 * and port that was included in the originating connect request.  <p>
 *
 * The generic structure of <tt>CONNECT_RESPONSE</tt> frame is as follows:
 *
 * <pre>
 *   +------- ... -------+--------+--------+--------- ... --------+-------- ... --------+
 *   |  KNXnet/IP Header |Channel | Status | Server Data Endpoint | Connection Response |
 *   |                   |  ID    |        |        (HPAI)        |     Data (CRD)      |
 *   +------- ... -------+--------+--------+--------- ... --------+-------- ... --------+
 *          6 bytes        1 byte   1 byte         8 bytes                n bytes
 * </pre>
 *
 * The channel ID is used as an identifier for further connection related requests. The status
 * byte is defined in {@link Status}. HPAI structure is defined in {@link Hpai}. <p>
 *
 * The Connection Response Data for IP *tunneling* connections is as follows:
 *
 * <pre>
 *   +--------+--------+----------------+
 *   |  Size  | Tunnel | KNX Individual |
 *   |        |Connect.|    Address     |
 *   +--------+--------+----------------+
 *     1 byte   1 byte      2 bytes
 * </pre>
 *
 * Where the size is fixed at 4 bytes, the value for a tunneling connection is 0x04 and the
 * individual address is the assigned address for this connectio by the gateway / router (service
 * container).  <p>
 *
 * IMPLEMENTATION NOTE : this implementation is currently specific to *tunneling* connection
 * responses via dependency to
 * {@link org.openremote.controller.protocol.knx.tunnel.ConnectionResponseData}. It is otherwise
 * fairly generic to the 'core' CRD so can be fairly easily modified to other connection types
 * if necessary.
 *
 * @see ConnectionResponseData
 * @see IpConnectReq
 *
 * @author Olivier Gandit
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class IpConnectResp extends IpMessage
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * KNXnet/IP CONNECT_RESPONSE service type identifier : {@value}  <p>
   *
   * This integer value is stored as a two byte value in the KNXnet/IP frame header.
   * The high byte value (0x02) indicates 'Core' service family, and low byte (0x06)
   * indicates connect response service.
   */
  public final static int STI = ServiceTypeIdentifier.CONNECT_RESPONSE.getValue();

  /**
   * Fixed Connect Response Data (CRD) structure size for tunneling connection : {@value}
   */
  public final static int KNXNET_IP_10_TUNNELING_CRD_SIZE = 0x04;



  // Enums ----------------------------------------------------------------------------------------

  /**
   * Connection status information included in this connection response.
   */
  public enum Status
  {
    /**
     * No error. Connection was created successfully.
     */
    NO_ERROR(0x00),

    /**
     * The {@link IpConnectReq.ConnectionType connection type} requested in the originating
     * {@link ServiceTypeIdentifier#CONNECT_REQUEST} is not supported by the KNXnet/IP
     * gateway/router. 
     */
    CONNECTION_TYPE_ERROR(0x22),

    /**
     * If the connection is not supported by KNXnet/IP gateway/router. For example, for tunneling
     * connections, not all {@link IpConnectReq.TunnelingKNXLayer} options are required in
     * gateway/router implementations.
     */
    CONNECTION_OPTION_ERROR(0x23),

    /**
     * Gateway/router has no more available connections.
     */
    NO_MORE_CONNECTIONS_ERROR(0x24);


    /**
     * Resolves a byte value from KNXnet/IP {@link ServiceTypeIdentifier#CONNECT_RESPONSE} frame
     * into this type-safe Java enum.
     *
     * @param value   the status byte from <tt>CONNECT_RESPONSE</tt> frame
     *
     * @return    status enum constant
     *
     * @throws UnknownConnectionStatusException
     *            if the given byte value in the method parameter cannot be resolved to enum constant
     */
    private static Status resolve(int value) throws UnknownConnectionStatusException
    {
      Status[] allStatuses = Status.values();

      byte statusByte = (byte)(value & 0xFF);

      for (Status status : allStatuses)
      {
        if (status.value == statusByte)
        {
          return status;
        }
      }

      throw new UnknownConnectionStatusException(
          "Unable to resolve '" + Strings.byteToUnsignedHexString(statusByte) +
          "' to a known connection status."
      );
    }


    private byte value;

    private Status(int value)
    {
      this.value = (byte)(value & 0xFF);
    }

    byte getValue()
    {
      return value;
    }


    private static class UnknownConnectionStatusException extends Exception
    {
      UnknownConnectionStatusException(String msg)
      {
        super(msg);
      }
    }
  }


  // Instance Fields ------------------------------------------------------------------------------


  /**
   * The channel identifier of a successful connect response.
   */
  private int channelId;

  /**
   * Connection status of this {@link ServiceTypeIdentifier#CONNECT_RESPONSE}.
   */
  private Status status;

  /**
   * A successful <tt>CONNECT_RESPONSE</tt> frame will contain a *data* endpoint address and
   * port of the server that the client can use to send data requests (such as tunneling KNX
   * telegrams over IP).
   */
  private Hpai dataEndpoint;

  /**
   * Connection Response Data (CRD) block for *tunneling* connections.
   */
  private ConnectionResponseData responseData;


  // Constructors ---------------------------------------------------------------------------------


  /**
   * Constructs an in-memory {@link ServiceTypeIdentifier@CONNECT_RESPONSE} frame.
   *
   * @param   channelID
   *            channel identifier for the established connection
   *
   * @param   status
   *            connection status -- {@link Status#NO_ERROR} for successful connections
   *
   * @param   serverDataEndpoint
   *            the data endpoint address and port of the KNXnet/IP gateway/router -- client
   *            can use the data endpoint for connection data transfer, for example with tunneling
   *            connections sending the KNX telegrams over IP network
   *            
   * @param   connectionAddress
   *            KNX individual address associated with this connection (service container
   *            individual address)
   */
  public IpConnectResp(int channelID, Status status, Hpai serverDataEndpoint,
                       IndividualAddress connectionAddress)
  {
    super(STI, 2 + Hpai.KNXNET_IP_10_HPAI_SIZE + KNXNET_IP_10_TUNNELING_CRD_SIZE);

    this.channelId = channelID;

    this.status = status;

    this.dataEndpoint = serverDataEndpoint;

    this.responseData = new ConnectionResponseData(connectionAddress);
  }


  /**
   * Reads a KNXnet/IP {@link ServiceTypeIdentifier#CONNECT_RESPONSE} from the given input
   * stream.  <p>
   *
   * The assumption with the input stream is that it has been positioned to read a KNX IP frame
   * after the initial six byte (KNXNet/IP 1.0) header.
   *
   * @param   is        input stream to read the <tt>CONNECT_RESPONSE</tt> IP body from. The input
   *                    stream must contain a valid KNXnet/IP frame and must be positioned to
   *                    read the IP body of the frame after the initial six byte (KNXnet/IP 1.0)
   *                    frame header
   *
   * @param ipBodySize  the size of the IP body segment in this frame -- this includes all the
   *                    frame content minus the six byte (KNXnet/IP 1.0) frame header
   *
   * @throws IOException
   *            if there was an I/O error reading the <tt>CONNECT_RESPONSE</tt> IP body of the frame
   */
  public IpConnectResp(InputStream is, int ipBodySize) throws IOException
  {
    super(STI, ipBodySize);

    this.channelId = is.read();

    try
    {
      this.status = Status.resolve(is.read());
    }

    catch (Status.UnknownConnectionStatusException e)
    {
      throw new IOException("Corrupt CONNECT_RESPONSE frame : " + e.getMessage());
    }

    // TODO :
    //
    //   Something to review -- spec says that *successful* connect responsed will contain
    //   the following HPAI and CRD blocks, possibly implying that refused connections
    //   (such as no more free connections, or unsupported connection type) would not.
    //
    //   If that's the case (would need to test) then the current implementation will fail
    //   with an I/O exception (most likely) due to end-of-stream which is not correct --
    //   instead the response instance should be constructed completely and the status
    //   code set to indicate the error state.
    //
    //   Not changing this now since the exception handling will change and have no time
    //   to test those changes.
    //                                                                          [JPL]

    this.dataEndpoint = new Hpai(is);

    byte[] crd = new byte[ConnectionResponseData.KNXNET_IP_10_TUNNELING_CRD_SIZE];
    int bytecount = is.read(crd);

    if (bytecount != crd.length)
    {
      throw new IOException();
    }

    this.responseData = new ConnectionResponseData(crd);
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
   * Writes a {@link ServiceTypeIdentifier#CONNECT_RESPONSE} KNXnet/IP frame to a given
   * output stream.
   *
   * @param out   output stream to write the KNXnet/IP frame to
   *
   * @throws IOException  if there was an I/O error writing the frame
   */
  @Override public void write(OutputStream out) throws IOException
  {
    super.write(out);

    out.write((byte)(channelId & 0xFF));

    out.write(status.getValue());

    dataEndpoint.write(out);

    out.write(responseData.getFrameStructure());
  }


  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * Returns the channel id for a successful connect response. Clients should first check the
   * connection status error code via {@link #getStatus} call -- the channel ID is only valid
   * for connect responses with status {@link Status#NO_ERROR}.
   *
   * @return    channel identifier for the requested connection
   */
  public int getChannelId()
  {
    return channelId;
  }


  /**
   * Returns the connection status that is part of this connection response. <p>
   *
   * NOTE: Not sure this will work correctly with connection responses that have error status.
   *       See notes on the constructor implementation.
   *
   * @return  status of the connection request
   */
  public Status getStatus()
  {
    return status;
  }


  /**
   * A successful <tt>CONNECT_RESPONSE</tt> frame will contain a *data* endpoint address and
   * port of the server that the client can use to send data requests (such as tunneling KNX
   * telegrams over IP).
   *
   * @see ServiceTypeIdentifier#CONNECT_RESPONSE
   *
   * @return    gateway/router *data* endpoint address and port
   */
  public Hpai getDataEndpoint()
  {
    return this.dataEndpoint;
  }
}
