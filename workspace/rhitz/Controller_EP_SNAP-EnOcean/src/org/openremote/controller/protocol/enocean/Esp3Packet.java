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
package org.openremote.controller.protocol.enocean;

import java.util.Arrays;

/**
 * TODO
 *
 * @author Rainer Hitz
 */
public class Esp3Packet implements EspPacket
{

  /**
   * TODO
   *
   * @return
   */
  @Override public byte[] asByteArray()
  {
    throw new RuntimeException("Not implemented yet !");
  }


  // Nested Classes -------------------------------------------------------------------------------


  /**
   * Class for calculating EnOcean Serial Protocol 3 (ESP3) Cyclic Redundancy Check (CRC) values
   * according to EnOcean Serial Protocol Specification 3 V1.17 chapter 2.3: CRC8 calculation.
   */
  static class CRC8
  {

    // Class Members ------------------------------------------------------------------------------

    /**
     * CRC-8 lookup table.
     */
    private static final byte[] CRC8_TABLE;

    static
    {
      CRC8_TABLE = calculateCRC8LookupTable();
    }

    /**
     * Calculates and returns the CRC-8 lookup table. <p>
     *
     * The lookup table calculation is based on the following parameters:
     * Polynomial: x^8 + x^2 + x^1 + 1 -> 0x107
     * Reversed:   false
     * Init value: 0x00
     *
     * @return CRC-8 lookup table
     */
    static byte[] calculateCRC8LookupTable()
    {
      byte[] table = new byte[256];

      int poly = 0x07;
      int crc;

      for(int value = 0; value < table.length; value++)
      {
        crc = value;

        for(int i = 0; i < 8; i++)
        {
          if((crc & 0x80) != 0)
          {
            crc = (crc << 1) ^ poly;
          }
          else
          {
            crc <<= 1;
          }
        }

        table[value] = (byte)crc;
      }

      return table;
    }


    /**
     * Calculates the CRC-8 value for given byte array.
     *
     * @param  data  data for CRC-8 calculation
     *
     * @return calculated CRC-8 value
     */
    static byte calculate(byte[] data)
    {
      if(data == null)
      {
        throw new IllegalArgumentException("null data");
      }

      return calculate(data, 0, data.length);
    }

    /**
     * Calculates CRC-8 value for specified range within given byte array. <p>
     *
     * The range has to be within the limits of the byte array.
     *
     * @param  data    data for CRC-8 calculation
     *
     * @param  offset  start index of range, inclusive
     *
     * @param  length  length of range
     *
     * @return calculated CRC-8 value
     */
    static byte calculate(byte[] data, int offset , int length)
    {
      if(data == null)
      {
        throw new IllegalArgumentException("null data");
      }

      if(((offset + length) > data.length) || offset < 0 || length < 0)
      {
        throw new IllegalArgumentException("Invalid range.");
      }

      int crc8 = 0;

      for (int dataIndex = offset; dataIndex < (offset + length); dataIndex++)
      {
        crc8 = CRC8_TABLE[(crc8 ^ data[dataIndex]) & 0xFF];
      }

      return (byte)crc8;
    }

    // Constructors -------------------------------------------------------------------------------

    private CRC8()
    {
    }
  }

}
