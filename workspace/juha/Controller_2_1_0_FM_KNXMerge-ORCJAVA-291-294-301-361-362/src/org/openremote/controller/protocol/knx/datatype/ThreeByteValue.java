/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller.protocol.knx.datatype;

/**
 * TODO
 * 3 Byte datatype as defined in KNX 1.1 Volume 3: System specifications Part 7:
 * Interworking, Chapter 2, Datapoint Types. <p>
 *
 * <ol>
 * <li>DPT 232.600 - DPT_RGB_Value (3 bytes)</li>
 * </ol>
 *
*
 * @author Marcus
 */
public class ThreeByteValue implements DataType
{

  // Private Instance Fields --------------------------------------------------------------------

  private byte[] value;
  private DataPointType dpt;


  // Constructors -------------------------------------------------------------------------------

  public ThreeByteValue(DataPointType.ThreeByteValue dpt, byte[] value)
  {
         this.value = (byte[])(value);

         this.dpt = dpt;
 }


  // Implements DataType ------------------------------------------------------------------------

  public int getDataLength()
  {
    return 4;
  }

  public byte[] getData()
  {
    return value;
  }

  public DataPointType getDataPointType()
  {
    return dpt;
  }
//  
//  public float resolve()
//  {
//    if (dpt == DataPointType.Float2ByteValue.VALUE_TEMP)
//    {
//      float temperature = 0;
//
//      // false for Positive sign and true for negative sign
//      boolean sign = (value[0] & 0x80)== 0x80;
//      byte exponent = (byte) ((value[0] & 0x78)>>3);
//      int unsigned = (int)(value[1] & 0xFF);
//
//      int mantisse = (int) ((value[0]&0x07)<<8)+unsigned;
//
//      for (byte i=0; i<exponent; i++) 
//    	mantisse = mantisse*2;
//
//      if (!sign)
//    	temperature = (float)mantisse/100;
//      else
//    	temperature = -(float)mantisse/100;
//    	
//      return temperature;
//    }
//
//    else
//    {
//      throw new Error("Unrecognized Float 2 Byte datapoint type " + dpt);
//    }
//  }
//  

  
}

