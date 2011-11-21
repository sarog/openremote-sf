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

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 *  TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class TwoOctetFloat implements DataType
{

  // Constants ------------------------------------------------------------------------------------

  private final static BigDecimal KNX_FLOAT_MAXIMUM_PRECISION = new BigDecimal(0.01)
      .setScale(2, RoundingMode.HALF_UP);


  // Class Members --------------------------------------------------------------------------------


  // Instance Fields ------------------------------------------------------------------------------

  private DataPointType dpt;
  private byte[] data;


  // Constructors ---------------------------------------------------------------------------------

  public TwoOctetFloat(DataPointType dpt, float value)
  {
    this.dpt = dpt;

    data = convertToKNXFloat(new BigDecimal(value).setScale(2, RoundingMode.HALF_UP));
  }

  public TwoOctetFloat(DataPointType dpt, byte[] value)
  {
    this.dpt = dpt;

    data = value;
  }


  // Implements DataType --------------------------------------------------------------------------

  @Override public int getDataLength()
  {
    return 3;
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
    int exponent = data[0];
    exponent &= 0x78;
    exponent >>= 3;

    int mantissa = data[1];
    mantissa &= 0xFF;
    mantissa += (data[0] & 0x7) << 8;


    double d = 0.01 * mantissa * Math.pow(2, exponent);

    BigDecimal bigD = new BigDecimal(d).setScale(2, RoundingMode.HALF_UP);

    return (((data[0]) & 0x80) == 0x80) ? bigD.negate() : bigD;

//    BigDecimal sign = ((data[0] & 0x80) == 0x80)
//        ? BigDecimal.ONE.negate()
//        : BigDecimal.ONE;
//
//    BigDecimal base = (exponent == 0)
//        ? BigDecimal.ZERO
//        : basevalues[exponent-1];
//
//    BigDecimal m = new BigDecimal(mantissa).multiply(KNX_FLOAT_MAXIMUM_PRECISION);
//
//    BigDecimal value = new BigDecimal(Math.pow(2, exponent))
//        .multiply(m)
//        .add(base)
//        .multiply(sign);
//
//    return value.setScale(2, RoundingMode.HALF_UP);
  }


  
  // Private Instance Methods ---------------------------------------------------------------------



  private byte[] convertToKNXFloat(BigDecimal value)
  {
    value = value.setScale(2, RoundingMode.HALF_UP);

    int bits = 0;
    int sign = 0;

    if (value.compareTo(BigDecimal.ZERO) < 0)
    {
      value = value.multiply(BigDecimal.ONE.negate());
      sign = 0x8000;
    }

    int exponent = 0;

    for (int i = 0 ; i < 16; ++i)
    {
      BigDecimal boundary = new BigDecimal(20.47*Math.pow(2, i)).setScale(2, RoundingMode.HALF_UP);

      if (boundary.compareTo(value) >= 0)
      {
        exponent = i;
        break;
      }
    }

    bits = (value.floatValue() > 20.47)
        ? (int)Math.round((value.doubleValue() / Math.pow(2, exponent))*100)
        : Math.round(value.multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).floatValue());
    
    bits += exponent << 11;
    
    return new byte[] { (byte)(((bits + sign) & 0xFF00) >> 8), (byte)(bits & 0xFF) };
  }


}

