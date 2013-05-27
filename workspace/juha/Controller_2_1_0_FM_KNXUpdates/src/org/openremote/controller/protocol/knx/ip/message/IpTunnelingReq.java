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
 */
public class IpTunnelingReq extends IpMessage
{
  
  // Constants ------------------------------------------------------------------------------------

  public final static int STI = ServiceTypeIdentifier.TUNNELING_REQUEST.getValue();



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

  private int channelId;
  private int seqCounter;
  private byte[] cEmiFrame;


  // Constructors ---------------------------------------------------------------------------------

  public IpTunnelingReq(int channelId, int seqCounter, byte[] cEmiFrame)
  {
    super(STI, 4 + cEmiFrame.length);

    this.channelId = channelId;
    this.seqCounter = seqCounter;
    this.cEmiFrame = cEmiFrame;
  }

  public IpTunnelingReq(InputStream is, int length) throws IOException
  {
    super(STI, length);

    // TODO check structure length
    is.skip(1);
    this.channelId = is.read();
    this.seqCounter = is.read();
    is.skip(1);

    this.cEmiFrame = new byte[length - 4];
    is.read(this.cEmiFrame);
  }


  // IpMessage Override ---------------------------------------------------------------------------

  @Override public Primitive getPrimitive()
  {
    return Primitive.REQ;
  }

  @Override public int getSyncSendTimeout()
  {
    return 10000;
  }

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

