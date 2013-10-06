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


/**
 * A two octet float datatype is used in datapoint types identified with 9 as their main number
 * (DPT 9.xxx). </p>
 *
 * As the name implies, this data type uses two bytes for its format and encodes a float using
 * the following encoding:
 *
 * <pre>
 *
 * Bits:
 *
 *     8 7654 321 87654321
 *    +----------+--------+
 *    |M EEEE MMM|MMMMMMMM|
 *    +----------+--------+
 *      hi byte   lo byte
 *
 * Encoding  : (0.01 * mantissa) * 2 ^ E
 *
 *              E = [0..15]
 *              M = [-2048..2047], two's complement notation
 *
 * Range : [-671088.64..670760.96]
 *
 *
 * Positive Exponent Ranges :
 *
 *  E = 0,  [0..20.47], granularity 0.01
 *  E = 1,  [20.49..40.94], granularity 0.02
 *  E = 2,  [40.98..81.88], granularity 0.04
 *  E = 3,  [81.96..163.76], granularity 0.08
 *  E = 4,  [163.92..327.52], granularity 0.16
 *  E = 5,  [327.68..655.04], granularity 0.32
 *  E = 6,  [655.68..1310.08], granularity 0.64
 *  E = 7,  [1311.36..2620.16], granularity 1.28
 *  E = 8,  [2622.72..5240.32], granularity 2.56
 *  E = 9,  [5245.44..10480.64], granularity 5.12
 *  E = 10, [10490.88..20961.28], granularity 10.24
 *  E = 11, [20981.76..41922.56], granularity 20.48
 *  E = 12, [41963.52..83845.12], granularity 40.96
 *  E = 13, [83927.04..167690.24], granularity 81.92
 *  E = 14, [167854.08..335380.58], granularity 163.84
 *  E = 15, [335708.26..670760.96], granularity 327.68
 *
 * Negative Exponent Ranges :
 *
 *  E = 0,  [-0.01..-20.48], granularity 0.01
 *  E = 1,  [-20.50..-40.96], granularity 0.02
 *  E = 2,  [-41.00..-81.92], granularity 0.04
 *  E = 3,  [-82.00..-163.84], granularity 0.08
 *  E = 4,  [-164.00..-327.68], granularity 0.16
 *  E = 5,  [-328.00..-655.36], granularity 0.32
 *  E = 6,  [-656.00..-1310.72], granularity 0.64
 *  E = 7,  [-1312.00..-2621.44], granularity 1.28
 *  E = 8,  [-2622.72..-5248.88], granularity 2.56
 *  E = 9,  [-5245.44..-10485.76], granularity 5.12
 *  E = 10, [-10495.90..-20766.72], granularity 10.14
 *  E = 11, [-20787.20..-41943.04], granularity 20.48
 *  E = 12, [-41984.00..-83886.08], granularity 40.96
 *  E = 13, [-83968.00..-167772.16], granularity 81.92
 *  E = 14, [-167854.08..-335544.32], granularity 163.84
 *  E = 15, [-335872.00..-671088.64], granularity 327.68
 *
 * </pre>
 *
 * The datapoint types (DPT) using this two octet float datatype are:
 *
 * <ol>
 *   <li>DPT 9.001 -- DPT_Value_Temp, Celsius units, valid range [-273...+670760]</li>
 *   <li>DPT 9.002 -- DPT_Value_Tempd, Kelvin units, valid range [-670760...+670760]</li>
 *   <li>DPT 9.003 -- DPT_Value_Tempa, K/h units, valid range [-670760...+670760]</li>
 *   <li>DPT 9.004 -- DPT_Value_Lux, Lux units, valid range [0...670760]</li>
 *   <li>DPT 9.005 -- DPT_Value_Wsp (wind speed), meters per second units, valid range [0...670760]</li>
 *   <li>DPT 9.006 -- DPT_Value_Pres (pressure), Pascal units, valid range [0...670760]</li>
 *   <li>DPT 9.010 -- DPT_Value_Time1, second units, value range [-670760...+670760]</li>
 *   <li>DPT 9.011 -- DPT_Value_Time2, millisecond units, value range [-670760...+670760]</li>
 *   <li>DPT 9.020 -- DPT_Value_Volt, milli-volt units, value range [-670760...+670760]</li>
 *   <li>DPT 9.021 -- DPT_Value_Curr, milli-ampere units, value range [-670760...+670760]</li>
 * </ol>
 *
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class TwoOctetFloat implements DataType
{

  // Constants ------------------------------------------------------------------------------------

//  private final static BigDecimal KNX_FLOAT_MAXIMUM_PRECISION = new BigDecimal(0.01)
//      .setScale(2, RoundingMode.HALF_UP);


  // Class Members --------------------------------------------------------------------------------

  /**
   * This is a helper method to convert 11-bit mantissa value of KNX two-octet float into a two's
   * complement value (excluding the most significant sign bit).
   *
   * @param mantissa    Integer with the mantissa value. Only the 11 least significant bits should
   *                    be set.
   *
   * @return            Two's complement of the given mantissa value. Note that the most
   *                    significant sign bit will not be set in the returned value.
   */
  protected static int elevenBitTwosComplement(int mantissa)
  {
    return (mantissa ^ 0x7FF) + 1;
  }


  // Instance Fields ------------------------------------------------------------------------------

  private DataPointType dpt;
  private byte[] data;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * TODO
   *
   * @param dpt
   * @param value
   */
  public TwoOctetFloat(DataPointType dpt, float value)
  {
    this.dpt = dpt;

    data = convertToKNXFloat(new BigDecimal(value).setScale(2, RoundingMode.HALF_UP));
  }

  /**
   * TODO
   *
   * @param dpt
   * @param value
   */
  public TwoOctetFloat(DataPointType dpt, byte[] value)
  {
    this.dpt = dpt;

    data = value;
  }


  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * Resolves the KNX two octet encoding in this instance into a Java's big decimal value.
   *
   * @return  KNX two octet float as a big decimal value. The returned value has at most
   *          two decimals, and value fragments have been rounded up using the
   *          {@link RoundingMode#HALF_UP} rounding rules.
   */
  public BigDecimal resolve()
  {
    // extract the exponent as a single int value...
    //
    //     8 7654 321 87654321
    //    +----------+--------+
    //    |M EEEE MMM|MMMMMMMM|
    //    +----------+--------+
    //      data[0]    data[1]

    int exponent = data[0];
    exponent &= 0x78;
    exponent >>= 3;

    // Compose the mantissa value into a single integer (three least significant bits of first
    // byte and full second byte:
    //
    //     8 7654 321 87654321
    //    +----------+--------+
    //    |M EEEE MMM|MMMMMMMM|
    //    +----------+--------+
    //      data[0]    data[1]


    int mantissa = data[1];
    mantissa &= 0xFF;
    mantissa += (data[0] & 0x7) << 8;

    // Extract the sign bit...
    //
    //     8 7654 321 87654321
    //    +----------+--------+
    //    |M EEEE MMM|MMMMMMMM|
    //    +----------+--------+
    //      data[0]    data[1]

    int sign = ((data[0] & 0x80) == 0x80) ? -1 : 1;

    // If sign is negative, invert the mantissa bits to two's complement...

    if (sign == -1)
    {
      mantissa = elevenBitTwosComplement(mantissa);
    }

    // Calculate the closest approximate decimal value: 0.01 * mantissa * 2 ^ exponent

    double d = 0.01 * mantissa * Math.pow(2, exponent) * sign;

    return new BigDecimal(d).setScale(2, RoundingMode.HALF_UP);
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



  
  // Private Instance Methods ---------------------------------------------------------------------


  /**
   * Converts Java's decimal values into KNX two octet float encoding. Note that only two
   * decimal precision is used.
   *
   * @param value   decimal value to convert to KNX two octet float encoding
   *
   * @return  KNX two octet float datatype encoding in two-byte network (big-endian) byte order
   */
  private byte[] convertToKNXFloat(BigDecimal value)
  {
    // round to closes two decimals...

    value = value.setScale(2, RoundingMode.HALF_UP);

    int sign = 0;

    // check the sign, we need to find the exponent either in above or below zero ranges...

    if (value.compareTo(BigDecimal.ZERO) < 0)
    {
      sign = 0x8000;
    }

    int exponent = 0;

    // Find the exponent in the range. The loop is far from efficient but the exponent
    // has upper limit of 15 and smaller values can be expected to be more common than large
    // exponents so most of the time the looping should stay within relatively few iterations...

    BigDecimal base = (sign == 0) ? new BigDecimal("20.47") : new BigDecimal("-20.48");

    for (int i = 0; i < 16; ++i)
    {
      BigDecimal boundary = base.multiply(new BigDecimal(Integer.toString((int)Math.pow(2, i))));

      // if the exponent range's upper or lower boundary value is larger/smaller than the value
      // we are converting, we've found the exponent value we need...

      if (sign == 0 && boundary.compareTo(value) >= 0)
      {
        exponent = i;
        break;
      }

      else if (sign != 0 && boundary.compareTo(value) <= 0)
      {
        exponent = i;
        break;
      }
    }

    // Convert the value to KNX two octet float mantissa bits:
    //
    //    value = 0.01 * Mantissa * 2 ^ exponent  =>  Mantissa = 100 * value / 2 ^ exponent

    int bits = (int)Math.round(value.doubleValue() / Math.pow(2, exponent) * 100);


    // Add exponent (four bits) to mantissa (elevent bits) and convert to two byte format
    // with correct sign bit set...
    //
    //     8 7654 321 87654321
    //    +----------+--------+
    //    |M EEEE MMM|MMMMMMMM|
    //    +----------+--------+
    //      data[0]    data[1]

    bits &= 0x7FF;
    bits += exponent << 11;
    
    return new byte[] { (byte)(((bits + sign) & 0xFF00) >> 8), (byte)(bits & 0xFF) };
  }


}

