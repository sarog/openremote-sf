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
public class IpConnectionStateResp extends IpMessage
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * KNXnet/IP CONNECTIONSTATE_RESPONSE service type identifier : {@value}  <p>
   *
   * This integer value is stored as a two byte value in the KNXnet/IP frame header.
   * The high byte value (0x02) indicates 'Core' service family, and low byte (0x08)
   * indicates connect state request service.
   */
  public final static int STI = ServiceTypeIdentifier.CONNECTIONSTATE_RESPONSE.getValue();


  // Enums ----------------------------------------------------------------------------------------

  /**
   * The status values for a connection state response.
   */
  public enum Status
  {
    /**
     * Connection state is ok.
     */
    NO_ERROR(0x00),

    /**
     * There's no connection with the given channel ID.
     */
    CONNECTION_ID_ERROR(0x21),

    /**
     * There's an error with the *data* connection between client and KNXnet/IP server.
     */
    DATA_CONNECTION_ERROR(0x26),

    /**
     * There's an error on the KNX bus the KNXnet/IP server is using to communicate with KNX devices.
     */
    KNX_CONNECTION_ERROR(0x27);


    /**
     * Resolves status byte value from {@link ServiceTypeIdentifier#CONNECTIONSTATE_RESPONSE} frame
     * into this type-safe Java enum.
     *
     * @param value   the status byte from <tt>CONNECTIONSSTATE_RESPONSE</tt> frame
     *
     * @return    status enum constant
     *
     * @throws UnknownConnectionStateStatusException
     *            if the given byte value in the method parameter cannot be resolved to enum constant
     */
    private static Status resolve(int value) throws UnknownConnectionStateStatusException
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

      throw new UnknownConnectionStateStatusException(
          "Unable to resolve '" + Strings.byteToUnsignedHexString(statusByte) +
          "' to a known connection state status."
      );
    }


    private byte value;

    private Status(int value)
    {
      this.value = (byte)(value & 0xFF);
    }


    private static class UnknownConnectionStateStatusException extends Exception
    {
      UnknownConnectionStateStatusException(String msg)
      {
        super(msg);
      }
    }
  }



  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Connection channel identifier this response is associated with.
   */
  private int channelId;

  /**
   * Connection status of this {@link ServiceTypeIdentifier#CONNECTIONSTATE_RESPONSE}.
   */
  private Status status;



  // Constructors ---------------------------------------------------------------------------------

  /**
   * Reads a KNXnet/IP {@link ServiceTypeIdentifier#CONNECTIONSTATE_RESPONSE} from the given input
   * stream.  <p>
   *
   * The assumption with the input stream is that it has been positioned to read a KNX IP frame
   * after the initial six byte (KNXNet/IP 1.0) header.
   *
   * @param   is        input stream to read the <tt>CONNECTIONSTATE_RESPONSE</tt> IP body from.
   *                    The input stream must contain a valid KNXnet/IP frame and must be
   *                    positioned to read the IP body of the frame after the initial six byte
   *                    (KNXnet/IP 1.0) frame header
   *
   * @param ipBodySize  the size of the IP body segment in this frame -- this includes all the
   *                    frame content minus the six byte (KNXnet/IP 1.0) frame header
   *
   * @throws IOException
   *            if there was an I/O error reading the <tt>CONNECTIONSTATE_RESPONSE</tt>
   */
  public IpConnectionStateResp(InputStream is, int ipBodySize) throws IOException
  {
    super(STI, ipBodySize);

    this.channelId = is.read();

    try
    {
      this.status = Status.resolve(is.read());
    }

    catch (Status.UnknownConnectionStateStatusException e)
    {
      throw new IOException(
          "Possibly corrupt KNXnet/IP frame or incorrect version of KNXnet/IP protocol : " +
          e.getMessage()
      );
    }
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


  // Instance Methods -----------------------------------------------------------------------------

  /**
   * Returns the connection channel id this response is associated with.
   *
   * @return    channel identifier for the connection state response
   */
  public int getChannelId()
  {
    return this.channelId;
  }

  /**
   * Returns the connection state status that is part of this connection state response. <p>
   *
   * @return  status of the connection state response
   */
  public Status getStatus()
  {
    return status;
  }
}
