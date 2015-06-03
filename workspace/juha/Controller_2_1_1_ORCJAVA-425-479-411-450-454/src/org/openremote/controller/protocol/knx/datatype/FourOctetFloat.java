/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.math.BigInteger;
import java.math.MathContext;


/**
 *  TODO
 *
 * @author Kenneth Stridh
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class FourOctetFloat implements DataType
{

  // Constants ------------------------------------------------------------------------------------

  private final static BigDecimal KNX_FLOAT_MAXIMUM_PRECISION = new BigDecimal(0.0001)
      .setScale(4, RoundingMode.HALF_UP);


  // Class Members --------------------------------------------------------------------------------


  // Instance Fields ------------------------------------------------------------------------------

  private DataPointType dpt;
  private byte[] data;


  // Constructors ---------------------------------------------------------------------------------

  public FourOctetFloat(DataPointType dpt, float value)
  {
    this.dpt = dpt;

    data = convertToKNXFloat(value);
  }

  public FourOctetFloat(DataPointType dpt, byte[] value)
  {
    this.dpt = dpt;

    data = value;
  }


  // Implements DataType --------------------------------------------------------------------------

  @Override public int getDataLength()
  {
    return 5;
  }

  @Override public byte[] getData()
  {
    return data;
  }

  @Override public DataPointType getDataPointType()
  {
    return dpt;
  }

  public BigDecimal resolve()
  {
    int intBit = data[3];
    
    intBit &= 0xFF;
    intBit += (data[2] & 0xFF) << 8;
    intBit += (data[1] & 0xFF) << 16;
    intBit += (data[0] & 0xFF) << 24;

    Float Sample = Float.intBitsToFloat(intBit);  
    
    BigDecimal bigD = new BigDecimal(Sample);
    
    int prec = (new BigDecimal(String.valueOf(Sample))).precision();
    
    MathContext mc = new MathContext(prec, RoundingMode.HALF_EVEN);
    
    bigD = (bigD.round(mc));
     
    return bigD;
  }

  
  // Private Instance Methods ---------------------------------------------------------------------


  private byte[] convertToKNXFloat(Float value)
  {
    byte[] bytes = new byte[4];
       
    int intBits = Float.floatToIntBits(value);
       
    bytes[3]=(byte)((intBits&0x000000ff)>>0);
    bytes[2]=(byte)((intBits&0x0000ff00)>>8);
    bytes[1]=(byte)((intBits&0x00ff0000)>>16);
    bytes[0]=(byte)((intBits&0xff000000)>>24);

    return new byte[] { (byte)(bytes[0]), (byte)(bytes[1]), (byte)(bytes[2]), (byte)(bytes[3])};
  }


}

