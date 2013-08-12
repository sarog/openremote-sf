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


/**
 * TODO
 *
 * @author Olivier Gandit
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class IpTunnelingReq extends IpMessage
{
  
  // Constants ------------------------------------------------------------------------------------

  /**
   * KNXnet/IP TUNNELING_REQUEST service type identifier : {@value}  <p>
   *
   * This integer value is stored as a two byte value in the KNXnet/IP frame header.
   * The high byte value (0x04) indicates 'Tunneling' service family, and low byte (0x20)
   * indicates tunneling request service.
   */
  public final static int STI = ServiceTypeIdentifier.TUNNELING_REQUEST.getValue();

  /**
   * Timeout used by this tunneling request to wait for {@link ServiceTypeIdentifier#TUNNELING_ACK}
   * frame to be sent back from the KNXnet/IP gateway/router (in milliseconds) : {@value}
   */
  public final static int KNXNET_IP_10_TUNNELING_REQUEST_TIMEOUT = 10000; // TODO


  // Class Members --------------------------------------------------------------------------------

  /**
   * Indicates whether the given KNXnet/IP frame includes a
   * {@link ServiceTypeIdentifier#TUNNELING_REQUEST} service type identifier.
   *
   * @param     knxFrame    KNXnet/IP frame as a byte array
   *
   * @return    true if the frame header includes a <tt>TUNNELING_REQUEST</tt> service type
   *            identifier, false otherwise
   */
  public static boolean isTunnelingRequest(byte[] knxFrame)
  {
    return (knxFrame[KNXNET_IP_10_HEADER_SIZE_INDEX]       == KNXNET_IP_10_HEADER_SIZE &&
            knxFrame[KNXNET_IP_10_HEADER_VERSION_INDEX]    == KNXNET_IP_10_VERSION &&
            ServiceTypeIdentifier.TUNNELING_REQUEST.isIncluded(knxFrame));
  }

  
  // Instance Fields ------------------------------------------------------------------------------


  /**
   * Connection channel identifier this request is associated with.
   */
  private int channelId;

  /**
   * TODO
   */
  private int seqCounter;

  /**
   * TODO
   */
  private byte[] cEmiFrame;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * TODO
   *
   * @param channelId
   * @param seqCounter
   * @param cEmiFrame
   */
  public IpTunnelingReq(int channelId, int seqCounter, byte[] cEmiFrame)
  {
    super(STI, 4 + cEmiFrame.length);

    this.channelId = channelId;
    this.seqCounter = seqCounter;
    this.cEmiFrame = cEmiFrame;
  }

  /**
   * TODO
   *
   * @param is
   * @param length
   * @throws IOException
   */
  public IpTunnelingReq(InputStream is, int ipBodyLength) throws IOException
  {
    super(STI, ipBodyLength);

    if (ipBodyLength < 4 + 9)
    {
      throw new IOException(
          "Incorrect body length " + ipBodyLength +
          " -- the minimum size must include connection header (4 bytes) and mandatory CEMI " +
          "fields (9 bytes)."
      );
    }

    int structureSize = is.read();

    if (structureSize != 4)
    {
      throw new IOException(
          "Corrupt KXNnet/IP TUNNELING_REQUEST frame or incorrect KNXnet/IP version -- " +
          "tunneling request connection header size must be 4, got " + structureSize
      );
    }
    
    this.channelId = is.read();
    this.seqCounter = is.read();

    int reserved = is.read();
    

    this.cEmiFrame = new byte[ipBodyLength - 4];

    int bytecount = is.read(this.cEmiFrame);

    if (bytecount != ipBodyLength - 4)
    {
      throw new IOException(
          "Unable to parse CEMI content -- expected to read " + (ipBodyLength - 4) + " bytes, got " + bytecount
      );
    }
//
//    if (structureSize != 4 + 9 + cEmiFrame[8])
//    {
//      throw new IOException(
//          "Corrupt frame or incorrect KNXnet/IP version -- was expecting IP body size " +
//          (4 + 9 + cEmiFrame[8]) + ", got " + structureSize
//      );
//    }
  }


  // IpMessage Override ---------------------------------------------------------------------------

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
   * The timeout used by client to wait for a {@link ServiceTypeIdentifier#TUNNELING_ACK}
   * frame to be sent back from KNXnet/IP gateway/router after sending this tunneling
   * request.
   *
   * @return    {@link #KNXNET_IP_10_TUNNELING_REQUEST_TIMEOUT}
   */
  @Override public int getSyncSendTimeout()
  {
    return KNXNET_IP_10_TUNNELING_REQUEST_TIMEOUT;
  }

  /**
   * TODO
   *
   * @param os      output stream to write the KNXnet/IP header to
   *
   * @throws IOException
   */
  @Override public void write(OutputStream os) throws IOException
  {
    super.write(os);

    os.write(4);
    os.write(this.channelId);
    os.write(this.seqCounter);
    os.write(0);
    os.write(this.cEmiFrame);
  }



  // Instance Methods -----------------------------------------------------------------------------

  /**
   * Returns the connection channel id this request is associated with.
   *
   * @return    channel identifier of this tunneling request
   */
  public int getChannelId()
  {
    return channelId;
  }

  public int getSeqCounter()
  {
    return seqCounter;
  }

  public byte[] getcEmiFrame()
  {
    return cEmiFrame;
  }
  
}

