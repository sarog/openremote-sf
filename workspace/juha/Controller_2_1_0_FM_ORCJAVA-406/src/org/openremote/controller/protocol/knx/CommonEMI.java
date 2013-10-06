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
package org.openremote.controller.protocol.knx;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class CommonEMI
{

  private int msgCode;
  private GroupAddress destinationAddress;
  private GroupAddress sourceAddress;
  private ApplicationProtocolDataUnit apdu;

  public CommonEMI(byte[] frameStructure)
  {
    msgCode = frameStructure[0] & 0xFF;

    // TODO : ignoring additional info length

    int destAddrHiByte = frameStructure[6] & 0xFF;
    int destAddrLoByte = frameStructure[7] & 0xFF;

    destinationAddress = new GroupAddress((byte)destAddrHiByte, (byte)destAddrLoByte);
  }

  public CommonEMI(int mc, GroupAddress destAddress, ApplicationProtocolDataUnit apdu)
  {
    this.msgCode = mc;

    this.destinationAddress = destAddress;

    this.apdu = apdu;

  }

  GroupAddress getDestinationAddress()
  {
    return destinationAddress;
  }


  public byte[] getFrameStructure()
  {
    //   Control Field 1

    /* A bit for standard common EMI frame type (not extended) in the first control field. */
    final int STANDARD_FRAME_TYPE = 0x01 << 7;

    /* Use frame repeat in the first control field. */
    final int REPEAT_FRAME = 0x00;

    /* Use system broadcast in the first control field. */
    final int SYSTEM_BROADCAST = 0x00;

    /* Bits for normal frame priority (%01) in the first control field of the common EMI frame. */
    final int NORMAL_PRIORITY = 0x01 << 2;

    /* Bit for requesting an ACK (L_Data.req only) for the frame in the first control field. */
    /*
     * 2011-04-14 OG : We force this bit to 0 (ACK not requested). If set to 1, the KNX/IP interface from Jung (IPS
     * 100 REG) and the Hager as well don't transmit telegrams to the KNX bus, for some unknown reason. Not requesting
     * the ACK is not a big deal as the GroupValue_Write.con telegram sent by the the server acts as an applicative
     * ACK.
     *
     */
    final int REQUEST_ACK = 0x00 << 1;


    //   Control Field 2

    /* Destination Address Type bit for group address in the second control field of the common
     * EMI frame - most significant bit of the byte. */
    final int GROUP_ADDRESS = 0x01 << 7;

    /* Hop count. Default to six. Bits 4 to 6 in the second control field of the cEMI frame. */
    final int HOP_COUNT =  0x06 << 4;

    /* Non-extended frame format in the second control field of the common EMI frame
     *(four zero bits) */
    final int NON_EXTENDED_FRAME_FORMAT = 0x0;




    byte[] struct = new byte[9 + apdu.getProtocolDataUnit().length];

    byte[] destAddrBytes = destinationAddress.asByteArray();
    
    struct[0] = (byte)(msgCode & 0xFF);
    struct[1] = 0x00; // additional info length
    struct[2] = (byte)(STANDARD_FRAME_TYPE +
                  REPEAT_FRAME +
                  SYSTEM_BROADCAST +
                  NORMAL_PRIORITY +
                  REQUEST_ACK);

    struct[3] = (byte)(GROUP_ADDRESS +
                   HOP_COUNT +
                   NON_EXTENDED_FRAME_FORMAT);

    struct[4] = 0x00;
    struct[5] = 0x00;

    struct[6] = destAddrBytes[0]; // dest address
    struct[7] = destAddrBytes[1]; // dest address

    struct[8] = (byte)(apdu.getDataLength() & 0xFF);

    Byte[] apduBytes = apdu.getProtocolDataUnit();

    for (int i = 0; i < apduBytes.length; i++)
    {
      struct[9 + i] = apduBytes[i];
    }

    return struct;
  }
  
}

