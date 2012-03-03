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
import org.openremote.controller.utils.Strings;


/**
 * TODO
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
  public static final int STI = ServiceTypeIdentifier.CONNECT_RESPONSE.getValue();


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


  // Constructors ---------------------------------------------------------------------------------

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

    // TODO read CRD
    is.skip(4);
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
