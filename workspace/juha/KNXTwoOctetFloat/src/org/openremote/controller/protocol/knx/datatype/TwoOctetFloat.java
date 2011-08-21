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
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class TwoOctetFloat implements DataType
{

  // Constants ------------------------------------------------------------------------------------

  private final static BigDecimal KNX_FLOAT_MAXIMUM_PRECISION = new BigDecimal(0.01)
      .setScale(2, RoundingMode.HALF_UP);


  // Class Members --------------------------------------------------------------------------------

  private final static BigDecimal[] basevalues = new BigDecimal[]
      {
          new BigDecimal(0.0 + 20.47 * Math.pow(2, 0)).setScale(2, RoundingMode.HALF_UP),
          new BigDecimal(20.47 + 20.47 * Math.pow(2, 1)).setScale(2, RoundingMode.HALF_UP),
          new BigDecimal(61.41 + 20.47 * Math.pow(2, 2)).setScale(2, RoundingMode.HALF_UP),
          new BigDecimal(143.29 + 20.47 * Math.pow(2, 3)).setScale(2, RoundingMode.HALF_UP),
          new BigDecimal(307.05 + 20.47 * Math.pow(2, 4)).setScale(2, RoundingMode.HALF_UP),
          new BigDecimal(634.57 + 20.47 * Math.pow(2, 5)).setScale(2, RoundingMode.HALF_UP),
          new BigDecimal(1289.61 + 20.47 * Math.pow(2, 6)).setScale(2, RoundingMode.HALF_UP),
          new BigDecimal(2599.69 + 20.47 * Math.pow(2, 7)).setScale(2, RoundingMode.HALF_UP),
          new BigDecimal(5219.85 + 20.47 * Math.pow(2, 8)).setScale(2, RoundingMode.HALF_UP),
          new BigDecimal(10460.17 + 20.47 * Math.pow(2, 9)).setScale(2, RoundingMode.HALF_UP),
          new BigDecimal(20940.81 + 20.47 * Math.pow(2, 10)).setScale(2, RoundingMode.HALF_UP),
          new BigDecimal(41902.09 + 20.47 * Math.pow(2, 11)).setScale(2, RoundingMode.HALF_UP),
          new BigDecimal(83824.65 + 20.47 * Math.pow(2, 12)).setScale(2, RoundingMode.HALF_UP),
          new BigDecimal(167669.77 + 20.47 * Math.pow(2, 13)).setScale(2, RoundingMode.HALF_UP),
          new BigDecimal(335360.01 + 20.47 * Math.pow(2, 14)).setScale(2, RoundingMode.HALF_UP)
      };


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

    BigDecimal sign = ((data[0] & 0x80) == 0x80)
        ? BigDecimal.ONE.negate()
        : BigDecimal.ONE;

    BigDecimal base = (exponent == 0)
        ? BigDecimal.ZERO
        : basevalues[exponent-1];

    BigDecimal m = new BigDecimal(mantissa).multiply(KNX_FLOAT_MAXIMUM_PRECISION);

    BigDecimal value = new BigDecimal(Math.pow(2, exponent))
        .multiply(m)
        .add(base)
        .multiply(sign);

    return value.setScale(2, RoundingMode.HALF_UP);
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

    for (int i = 14; i >= 0; --i)
    {
      BigDecimal precision = (i < 14)
          ? new BigDecimal(0.01*Math.pow(2, i+1)).setScale(2, RoundingMode.HALF_UP)
          : BigDecimal.ZERO;
      
      BigDecimal boundary = basevalues[i].add(precision);

      if (boundary.compareTo(value) <= 0)
      {
        bits = getBits(value, i+1);
        break;
      }
    }

    if (bits == 0)
      bits = getBits(value, 0);

    return new byte[] { (byte)(((bits + sign) & 0xFF00) >> 8), (byte)(bits & 0xFF) };
  }


  private int getBits(BigDecimal decimal, int exponent)
  {
    if (decimal.compareTo(basevalues[exponent]) > 0)
      decimal = basevalues[exponent];

    if (exponent == 0)
    {
      return Math.round(decimal.divide(KNX_FLOAT_MAXIMUM_PRECISION).floatValue()) & 0x7FF;
    }
    else
    {
      BigDecimal precision = new BigDecimal(0.01*Math.pow(2, exponent)).setScale(2, RoundingMode.HALF_UP);
      int mantissa = Math.round(decimal.subtract(basevalues[exponent-1]).divide(precision).floatValue()) & 0x7FF;

      return mantissa += exponent << 11;
    }
  }

}

