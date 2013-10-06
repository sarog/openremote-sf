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

import org.junit.Assert;
import org.junit.Test;
import org.openremote.controller.utils.Strings;

/**
 * Unit tests for {@link org.openremote.controller.protocol.knx.datatype.TwoOctetFloat} class. <p>
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
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class TwoOctetFloatTest
{

  /**
   * Test various positive values in the E = 0 range [0.00...20.47].
   */
  @Test public void testBasicValuesNoExponent()
  {
    // value 1.0 translates to 100 in mantissa (0.01 precision), hibyte should be zeroes

    TwoOctetFloat knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 1);
    byte[] data = knxFloat.getData();

    Assert.assertTrue("Expected 100, got " + data[1], data[1] == 100);
    Assert.assertTrue("Expected 0, got " + data[0], data[0] == 0);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    int value = knxFloat.resolve().intValue();

    Assert.assertTrue("Expected 1, got " + value, value == 1);


    // value 2.0 translates to 200 (0xC8) in mantissa (0.01 precision), hibyte should be zeroes
    // note that Java's signed byte means the most significant bit when set is translated as
    // negative value

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 2);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 8, got " + (data[1] & 0xF), (data[1] & 0xF) == 8);
    Assert.assertTrue("Expected 12, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 12);
    Assert.assertTrue("Expected 0, got " + data[0], data[0] == 0);


    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve().intValue();

    Assert.assertTrue("Expected 2, got " + value, value == 2);



    // value 3.0 translates to 300 (0x12C) in mantissa (0.01 precision),
    // hibyte should be 1 (no exponent) and lobyte should be 0x2C

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 3);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 12, got " + (data[1] & 0xF), (data[1] & 0xF) == 12);
    Assert.assertTrue("Expected 2, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 2);
    Assert.assertTrue("Expected 1, got " + data[0], data[0] == 1);


    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve().intValue();

    Assert.assertTrue("Expected 3, got " + value, value == 3);



    // value 4.0 translates to 400 (0x190) in mantissa (0.01 precision),
    // hibyte should be 1 (no exponent) and lobyte should be 0x90
    // note that Java's signed byte means the most significant bit when set is translated as
    // negative value

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 4);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 0, got " + (data[1] & 0xF), (data[1] & 0xF) == 0);
    Assert.assertTrue("Expected 9, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 9);
    Assert.assertTrue("Expected 1, got " + data[0], data[0] == 1);


    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve().intValue();

    Assert.assertTrue("Expected 4, got " + value, value == 4);



    // value 5.0 translates to 500 (0x1F4) in mantissa (0.01 precision),
    // hibyte should be 1 (no exponent) and lobyte should be 0xF4
    // note that Java's signed byte means the most significant bit when set is translated as
    // negative value

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 5);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 4, got " + (data[1] & 0xF), (data[1] & 0xF) == 4);
    Assert.assertTrue("Expected 15, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 15);
    Assert.assertTrue("Expected 1, got " + data[0], data[0] == 1);


    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve().intValue();

    Assert.assertTrue("Expected 5, got " + value, value == 5);




    // value 10.0 translates to 1000 (0x3E8) in mantissa (0.01 precision),
    // hibyte should be 3 (no exponent) and lobyte should be 0xE8
    // note that Java's signed byte means the most significant bit when set is translated as
    // negative value

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 10);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 8, got " + (data[1] & 0xF), (data[1] & 0xF) == 8);
    Assert.assertTrue("Expected 14, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 14);
    Assert.assertTrue("Expected 3, got " + data[0], data[0] == 3);


    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve().intValue();

    Assert.assertTrue("Expected 10, got " + value, value == 10);




    // zero should be zero

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 0);
    data = knxFloat.getData();

    Assert.assertTrue(data[0] == 0);
    Assert.assertTrue(data[1] == 0);


    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve().intValue();

    Assert.assertTrue("Expected 0, got " + value, value == 0);



    // value 20.47 translates to 2047 (0x7FF) in mantissa (0.01 precision),
    // hibyte should be 7 (no exponent) and lobyte should be 0xFF
    // note that Java's signed byte means the most significant bit when set is translated as
    // negative value

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 20.47f);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 15, got " + (data[1] & 0xF), (data[1] & 0xF) == 15);
    Assert.assertTrue("Expected 15, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 15);
    Assert.assertTrue("Expected 7, got " + data[0], data[0] == 7);


    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve().multiply(BigDecimal.TEN.pow(2)).intValue();

    Assert.assertTrue("Expected 2047, got " + value, value == 2047);


    // value 0.01 translates to 1 (0x1) in mantissa (0.01 precision),
    // hibyte should be 0 (no exponent) and lobyte should be 0x01
    // note that Java's signed byte means the most significant bit when set is translated as
    // negative value

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, .01f);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 1, got " + (data[1] & 0xF), (data[1] & 0xF) == 1);
    Assert.assertTrue("Expected 0, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 0);
    Assert.assertTrue("Expected 0, got " + data[0], data[0] == 0);


    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve().multiply(BigDecimal.TEN.pow(2)).intValue();

    Assert.assertTrue("Expected 1, got " + value, value == 1);

  }


  /**
   * Test negative values in E = 0 exponent range.
   */
  @Test public void testBasicNegativeValuesNoExponent()
  {
    // value -0.01 translates to 0x7FF in mantissa (ignoring most significant sign bit),
    // lobyte should be 255 [0xFF] (ignoring Java's signed bytes) and hibyte should be
    // 135 [0x87] (ignoring Java's signed bytes) == 0b10000111  where first bit is sign, next
    // four bits are exponent (zero) and last three bits are the most significant bits of
    // mantissa value (0x7).

    TwoOctetFloat knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, -0.01f);
    int hibyte = knxFloat.getData() [0];
    int lobyte = knxFloat.getData() [1];

    hibyte &= 0xFF;
    lobyte &= 0xFF;

    Assert.assertTrue("Expected 255, got " + lobyte, lobyte == 0xFF);
    Assert.assertTrue("Expected 135, got " + hibyte, hibyte == 135);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, new byte[] { (byte)hibyte, (byte)lobyte });
    BigDecimal value = knxFloat.resolve();
    BigDecimal val = new BigDecimal("-0.01");

    Assert.assertTrue("Expected " + val + ", got " + value, value.compareTo(val) == 0);



    // value -1.00 translates to 0x79C in mantissa (ignoring most significant sign bit),
    // lobyte should be 0x9C and hibyte should be 0x87 == 0b10000111 where first bit is sign, next
    // four bits are exponent (zero) and last three bits are the most significant bits of
    // mantissa value (0x7).

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, -1.00f);
    hibyte = knxFloat.getData() [0];
    lobyte = knxFloat.getData() [1];

    hibyte &= 0xFF;
    lobyte &= 0xFF;

    Assert.assertTrue("Expected 156, got " + lobyte, lobyte == 0x9C);
    Assert.assertTrue("Expected 135, got " + hibyte, hibyte == 0x87);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, new byte[] { (byte)hibyte, (byte)lobyte });
    int valInt = knxFloat.resolve().intValue();

    Assert.assertTrue("Expected -1.0, got " + valInt, valInt == -1);



    // value -2.00 translates to 0x738 in mantissa (ignoring most significant sign bit),
    // lobyte should be 0x38 and hibyte should be 0x87 == 0b10000111 where first bit is sign, next
    // four bits are exponent (zero) and last three bits are the most significant bits of
    // mantissa value (0x7).

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, -2.00f);
    hibyte = knxFloat.getData() [0];
    lobyte = knxFloat.getData() [1];

    hibyte &= 0xFF;
    lobyte &= 0xFF;

    Assert.assertTrue("Expected 56, got " + lobyte, lobyte == 0x38);
    Assert.assertTrue("Expected 135, got " + hibyte, hibyte == 0x87);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, new byte[] { (byte)hibyte, (byte)lobyte });
    valInt = knxFloat.resolve().intValue();

    Assert.assertTrue("Expected -2.0, got " + valInt, valInt == -2);



    // value -3.00 translates to 0x6D4 in mantissa (ignoring most significant sign bit),
    // lobyte should be 0xD4 and hibyte should be 0x86 == 0b10000110 where first bit is sign, next
    // four bits are exponent (zero) and last three bits are the most significant bits of
    // mantissa value (0x6).

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, -3.00f);
    hibyte = knxFloat.getData() [0];
    lobyte = knxFloat.getData() [1];

    hibyte &= 0xFF;
    lobyte &= 0xFF;

    Assert.assertTrue("Expected 212, got " + lobyte, lobyte == 0xD4);
    Assert.assertTrue("Expected 134, got " + hibyte, hibyte == 0x86);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, new byte[] { (byte)hibyte, (byte)lobyte });
    valInt = knxFloat.resolve().intValue();

    Assert.assertTrue("Expected -3.0, got " + valInt, valInt == -3);


    // value -4.00 translates to 0x670 in mantissa (ignoring most significant sign bit),
    // lobyte should be 0x70 and hibyte should be 0x86 == 0b10000110 where first bit is sign, next
    // four bits are exponent (zero) and last three bits are the most significant bits of
    // mantissa value (0x6).

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, -4.00f);
    hibyte = knxFloat.getData() [0];
    lobyte = knxFloat.getData() [1];

    hibyte &= 0xFF;
    lobyte &= 0xFF;

    Assert.assertTrue("Expected 112, got " + lobyte, lobyte == 0x70);
    Assert.assertTrue("Expected 134, got " + hibyte, hibyte == 0x86);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, new byte[] { (byte)hibyte, (byte)lobyte });
    valInt = knxFloat.resolve().intValue();

    Assert.assertTrue("Expected -4.0, got " + valInt, valInt == -4);



    // value -5.00 translates to 0x60C in mantissa (ignoring most significant sign bit),
    // lobyte should be 0x0C and hibyte should be 0x86 == 0b10000110 where first bit is sign, next
    // four bits are exponent (zero) and last three bits are the most significant bits of
    // mantissa value (0x6).

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, -5.00f);
    hibyte = knxFloat.getData() [0];
    lobyte = knxFloat.getData() [1];

    hibyte &= 0xFF;
    lobyte &= 0xFF;

    Assert.assertTrue("Expected 12, got " + lobyte, lobyte == 0x0C);
    Assert.assertTrue("Expected 134, got " + hibyte, hibyte == 0x86);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, new byte[] { (byte)hibyte, (byte)lobyte });
    valInt = knxFloat.resolve().intValue();

    Assert.assertTrue("Expected -5.0, got " + valInt, valInt == -5);



    // value -10.00 translates to 0x418 in mantissa (ignoring most significant sign bit),
    // lobyte should be 0x18 and hibyte should be 0x84 == 0b10000100 where first bit is sign, next
    // four bits are exponent (zero) and last three bits are the most significant bits of
    // mantissa value (0x4).

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, -10.00f);
    hibyte = knxFloat.getData() [0];
    lobyte = knxFloat.getData() [1];

    hibyte &= 0xFF;
    lobyte &= 0xFF;

    Assert.assertTrue("Expected 24, got " + lobyte, lobyte == 0x18);
    Assert.assertTrue("Expected 132, got " + hibyte, hibyte == 0x84);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, new byte[] { (byte)hibyte, (byte)lobyte });
    valInt = knxFloat.resolve().intValue();

    Assert.assertTrue("Expected -10.0, got " + valInt, valInt == -10);



    // value -20.47 translates to 0x01 in mantissa (ignoring most significant sign bit),
    // lobyte should be 0x01 and hibyte should be 0x80 == 0b10000000 where first bit is sign, next
    // four bits are exponent (zero) and last three bits are the most significant bits of
    // mantissa value (0x0).

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, -20.47f);
    hibyte = knxFloat.getData() [0];
    lobyte = knxFloat.getData() [1];

    hibyte &= 0xFF;
    lobyte &= 0xFF;

    Assert.assertTrue("Expected 1, got " + lobyte, lobyte == 0x01);
    Assert.assertTrue("Expected 128, got " + hibyte, hibyte == 0x80);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, new byte[] { (byte)hibyte, (byte)lobyte });
    val = knxFloat.resolve();
    value = new BigDecimal("-20.47");

    Assert.assertTrue("Expected " + value + ", got " + val, val.compareTo(value) == 0);




    // value -20.48 translates to 0x01 in mantissa (ignoring most significant sign bit),
    // lobyte should be 0x00 and hibyte should be 0x80 == 0b10000000 where first bit is sign, next
    // four bits are exponent (zero) and last three bits are the most significant bits of
    // mantissa value (0x0).

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, -20.48f);
    hibyte = knxFloat.getData() [0];
    lobyte = knxFloat.getData() [1];

    hibyte &= 0xFF;
    lobyte &= 0xFF;

    Assert.assertTrue("Expected 0, got " + lobyte, lobyte == 0x00);
    Assert.assertTrue("Expected 128, got " + hibyte, hibyte == 0x80);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, new byte[] { (byte)hibyte, (byte)lobyte });
    val = knxFloat.resolve();
    value = new BigDecimal("-20.48");

    Assert.assertTrue("Expected " + value + ", got " + val, val.compareTo(value) == 0);

  }


  /**
   * Test random positive values in different exponent ranges.
   */
  @Test public void testBasicValuesWithExponent()
  {
    // value 100.0 translates to 10000 which is broken down to 1250 (0x4E2) in mantissa
    // (0.04 precision) and exponent of 3, hibyte should be 0x1C == 0001 1100 == 0x4 + 0x3 << 3
    // and lobyte should be 0xE2
    //
    // note that Java's signed byte means the most significant bit when set is translated as
    // negative value
    TwoOctetFloat knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 100);
    byte[] data = knxFloat.getData();

    Assert.assertTrue("Expected 2, got " + (data[1] & 0xF), (data[1] & 0xF) == 2);
    Assert.assertTrue("Expected 14, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 14);
    Assert.assertTrue("Expected 12, got " + (data[0] & 0xF), (data[0] & 0xF) == 12);
    Assert.assertTrue("Expected 1, got " + ((data[0] & 0XF0) >> 4), ((data[0] & 0xF0) >> 4) == 1);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    BigDecimal value = knxFloat.resolve();

    Assert.assertTrue(
        "Expected 100.00 +- 0.02, got " + value,
        value.compareTo(new BigDecimal(99.98)) >= 0 &&
        value.compareTo(new BigDecimal(100.02)) <= 0);




    // value 1000.00 translates to 100000 which is broken down to 1563 (0x61B) in mantissa
    // (0.64 precision) and exponent of 6, hibyte should be 0x36  == 0011 0110 == 0x6 + 0x6 << 3
    // and lobyte should be 0x1B
    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 1000);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 11, got " + (data[1] & 0xF), (data[1] & 0xF) == 0xB);
    Assert.assertTrue("Expected 1, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 0x1);
    Assert.assertTrue("Expected 6, got " + (data[0] & 0xF), (data[0] & 0xF) == 0x6);
    Assert.assertTrue("Expected 3, got " + ((data[0] & 0XF0) >> 4), ((data[0] & 0xF0) >> 4) == 0x3);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve();

    Assert.assertTrue(
        "Expected 1000.00 +- 0.32, got " + value,
        value.compareTo(new BigDecimal(999.68)) >= 0 &&
        value.compareTo(new BigDecimal(1000.32)) <= 0
    );


    // value 2000.0 translates to 200000 which is broken down to 1563 (0x61B) in mantissa
    // (1.28 precision) and exponent of 7, hibyte should be 0x3E == 0011 1110 == 0x6 + 0x7 << 3
    // and lobyte should be 0x1B
    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 2000);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 11, got " + (data[1] & 0xF), (data[1] & 0xF) == 0xB);
    Assert.assertTrue("Expected 1, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 1);
    Assert.assertTrue("Expected 14, got " + (data[0] & 0xF), (data[0] & 0xF) == 0xE);
    Assert.assertTrue("Expected 3, got " + ((data[0] & 0XF0) >> 4), ((data[0] & 0xF0) >> 4) == 3);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve();

    Assert.assertTrue(
        "Expected 2000.00 +- 0.64, got " + value,
        value.compareTo(new BigDecimal(1999.36)) >= 0 &&
        value.compareTo(new BigDecimal(2000.64)) <= 0
    );

    // value 10000.0 translates to 1,000,000 which is broken down to 1953 (0x7A1) in mantissa
    // (5.12 precision) and exponent of 9, hibyte should be 0x4F == 0100 1111 == 0x7 + 0x9 << 3
    // and lobyte should be 0xA1
    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 10000);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 1, got " + (data[1] & 0xF), (data[1] & 0xF) == 0x1);
    Assert.assertTrue("Expected 10, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 0xA);
    Assert.assertTrue("Expected 15, got " + (data[0] & 0xF), (data[0] & 0xF) == 0xF);
    Assert.assertTrue("Expected 4, got " + ((data[0] & 0XF0) >> 4), ((data[0] & 0xF0) >> 4) == 4);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve();

    Assert.assertTrue(
        "Expected 10000.00 +- 2.56, got " + value,
        value.compareTo(new BigDecimal(9998.44)) >= 0 &&
        value.compareTo(new BigDecimal(10002.56)) <= 0
    );

    // value 20000.0 translates to 2,000,000 which is broken down to 1953 (0x7A1) in mantissa
    // (10.24 precision) and exponent of 10, hibyte should be 0x57 == 0101 0111 == 0x7 + 0xA << 3
    // and lobyte should be 0xA1
    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, 20000);
    data = knxFloat.getData();

    Assert.assertTrue("Expected 1, got " + (data[1] & 0xF), (data[1] & 0xF) == 1);
    Assert.assertTrue("Expected 10, got " + ((data[1] & 0xF0) >> 4), ((data[1] & 0xF0) >> 4) == 0xA);
    Assert.assertTrue("Expected 7, got " + (data[0] & 0xF), (data[0] & 0xF) == 7);
    Assert.assertTrue("Expected 5, got " + ((data[0] & 0XF0) >> 4), ((data[0] & 0xF0) >> 4) == 5);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
    value = knxFloat.resolve();

    Assert.assertTrue(
        "Expected 20000.00 +- 5.12, got " + value,
        value.compareTo(new BigDecimal(19994.88)) >= 0 &&
        value.compareTo(new BigDecimal(20005.12)) <= 0
    );

  }


  /**
   * Test random negative values in different exponent ranges.
   */
  @Test public void testBasicNegativeWithExponent()
  {

    // value -100.00 translates to 0x31E in mantissa (ignoring most significant sign bit),
    // lobyte should be 0x1E and hibyte should be 0x9B == 0b10011011 where first bit is sign, next
    // four bits are exponent (0x3) and last three bits are the most significant bits of
    // mantissa value (0x3).

    TwoOctetFloat knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, -100.00f);
    int hibyte = knxFloat.getData() [0];
    int lobyte = knxFloat.getData() [1];

    hibyte &= 0xFF;
    lobyte &= 0xFF;

    Assert.assertTrue("Expected 30, got " + lobyte, lobyte == 0x1E);
    Assert.assertTrue("Expected 155, got " + hibyte, hibyte == 0x9B);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, new byte[] { (byte)hibyte, (byte)lobyte });
    BigDecimal value = knxFloat.resolve();

    Assert.assertTrue(
        "Expected -100.00 +- 0.02, got " + value,
        value.compareTo(new BigDecimal(-99.98)) <= 0 &&
        value.compareTo(new BigDecimal(-100.02)) >= 0
    );


    // value -1000.00 translates to 0x1E6 in mantissa (ignoring most significant sign bit),
    // lobyte should be 0xE6 and hibyte should be 0xB1 == 0b10110001 where first bit is sign, next
    // four bits are exponent (0x6) and last three bits are the most significant bits of
    // mantissa value (0x1).

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, -1000.00f);
    hibyte = knxFloat.getData() [0];
    lobyte = knxFloat.getData() [1];

    hibyte &= 0xFF;
    lobyte &= 0xFF;

    Assert.assertTrue("Expected 230, got " + lobyte, lobyte == 0xE6);
    Assert.assertTrue("Expected 177, got " + hibyte, hibyte == 0xB1);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, new byte[] { (byte)hibyte, (byte)lobyte });
    value = knxFloat.resolve();

    Assert.assertTrue(
        "Expected -1000.00 +- 0.32, got " + value,
        value.compareTo(new BigDecimal(-999.68)) <= 0 &&
        value.compareTo(new BigDecimal(-1000.32)) >= 0
    );


    // value -2000.00 translates to 0x1E6 in mantissa (ignoring most significant sign bit),
    // lobyte should be 0xE6 and hibyte should be 0xB9 == 0b10111001 where first bit is sign, next
    // four bits are exponent (0x7) and last three bits are the most significant bits of
    // mantissa value (0x1).

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, -2000.00f);
    hibyte = knxFloat.getData() [0];
    lobyte = knxFloat.getData() [1];

    hibyte &= 0xFF;
    lobyte &= 0xFF;

    Assert.assertTrue("Expected 230, got " + lobyte, lobyte == 0xE6);
    Assert.assertTrue("Expected 185, got " + hibyte, hibyte == 0xB9);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, new byte[] { (byte)hibyte, (byte)lobyte });
    value = knxFloat.resolve();

    Assert.assertTrue(
        "Expected -2000.00 +- 0.64, got " + value,
        value.compareTo(new BigDecimal(-1999.36)) <= 0 &&
        value.compareTo(new BigDecimal(-2000.64)) >= 0
    );




    // value -10000.0 translates to 0x5F in mantissa (ignoring most significant sign bit),
    // lobyte should be 0x5F, and hibyte should be 0xC8 == 0b11001000 where first bit is sign,
    // next four bits are exponent (0x9) and last three bits are the most significant bits of
    // mantissa value (0x00).

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, -10000.00f);
    hibyte = knxFloat.getData() [0];
    lobyte = knxFloat.getData() [1];

    hibyte &= 0xFF;
    lobyte &= 0xFF;

    Assert.assertTrue("Expected 95, got " + lobyte, lobyte == 0x5F);
    Assert.assertTrue("Expected 200, got " + hibyte, hibyte == 0xC8);

    // translate back to Java float...

    knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, new byte[] { (byte)hibyte, (byte)lobyte });
    value = knxFloat.resolve();

    Assert.assertTrue(
        "Expected -10000.00 +- 5.12, got " + value,
        value.compareTo(new BigDecimal(-9994.88)) <= 0 &&
        value.compareTo(new BigDecimal(-10005.12)) >= 0
    );
  }




  @Test public void testPositiveZeroExponentRange()
  {
    byte[] mantissaBitPattern = new byte[2048];

    BigDecimal val = new BigDecimal("0.01");
    BigDecimal increment = new BigDecimal("0.01");
    BigDecimal boundary = new BigDecimal("20.47");

    for (; val.compareTo(boundary) <= 0; val = val.add(increment))
    {
      TwoOctetFloat knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, val.floatValue());
      byte[] data = knxFloat.getData();

      Assert.assertTrue((data[0] & 0xF8) == 0);

      int mantissaValue = data[1];
      mantissaValue &= 0xFF;
      mantissaValue += (data[0] & 0x7) << 8;

      mantissaBitPattern[mantissaValue] += 1;

      knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
      BigDecimal value = knxFloat.resolve();

      Assert.assertTrue(value.compareTo(val) == 0);
    }


    Assert.assertTrue(mantissaBitPattern[0] == 0);
    
    for (int i = 1; i < 2048; ++i)
    {
      Assert.assertTrue(
          "Found " + mantissaBitPattern[i] + " at index " + i,
          mantissaBitPattern[i] == 1
      );
    }
  }

  @Test public void testNegativeZeroExponentRange()
  {
    byte[] mantissaBitPattern = new byte[2048];

    BigDecimal val = new BigDecimal("-0.01");
    BigDecimal increment = new BigDecimal("-0.01");
    BigDecimal boundary = new BigDecimal("-20.47");

    for (; val.compareTo(boundary) >= 0; val = val.add(increment))
    {
      TwoOctetFloat knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, val.floatValue());
      byte[] data = knxFloat.getData();

      Assert.assertTrue((data[0] & 0xF8) == 0x80);

      int mantissaValue = data[1];
      mantissaValue &= 0xFF;
      mantissaValue += (data[0] & 0x7) << 8;

      mantissaBitPattern[mantissaValue] += 1;


      knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
      BigDecimal value = knxFloat.resolve();

      Assert.assertTrue(value.compareTo(val) == 0);
    }

    for (int i = 1; i < 2048; ++i)
    {
      Assert.assertTrue(
          "Found " + mantissaBitPattern[i] + " at index " + i,
          mantissaBitPattern[i] == 1
      );
    }
  }

  /**
   * Test conversions back and forth between two constructors, one that takes Java float
   * and converts to two-byte data format and another that takes the byte format and converts
   * to Java's decimal. Do this in the KNX two-octet float exponent 1 encoding range.
   */
  @Test public void testPositiveFirstExponentRange()
  {
    // Test values from lower boundary 20.49 to upper boundary 40.94...

    int val = 2049;
    int boundary = 4094;
    int increment = 1;

    // Iterate through values based on increment...

    for (; val <= boundary; val += increment)
    {
      // Java float to KNX byte format...

      TwoOctetFloat knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, (float)val / 100);
      byte[] data = knxFloat.getData();


      // Assert the exponent = 1...

      Assert.assertTrue(
          "Expected 0x8, got " + (data[0] & 0xF8) + " at value " + val,
          (data[0] & 0xF8) == 0x8
      );


      // resolve back to integer...

      knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
      int value = (int)(knxFloat.resolve().doubleValue() * 100);


      Assert.assertTrue(

          // Adjust to rounding errors that may occur due to E = 1 => 2^E = 2 : 0.02 value granularity

          "Comparing " + value + " to " + val,
          value >= val - 1 && value <= val + 1

      );

    }
  }


  /**
   * Test conversions back and forth between two constructors, one that takes Java float
   * and converts to two-byte data format and another that takes the byte format and converts
   * to Java's decimal. Do this in the KNX two-octet float exponent 1 encoding (negative) range.
   */
  @Test public void testNegativeFirstExponentRange()
  {
    // Test values from upper boundary -20.50 to lower boundary -40.94...

    BigDecimal val = new BigDecimal(-20.50).setScale(2, RoundingMode.HALF_UP);
    BigDecimal increment = new BigDecimal(-0.02).setScale(2, RoundingMode.HALF_UP);
    BigDecimal boundary = new BigDecimal(-20.48 - 20.48).setScale(2, RoundingMode.HALF_UP);

    // Iterate through the values with -0.02 increments...

    for (; val.compareTo(boundary) >= 0; val = val.add(increment))
    {
      // Java float to KNX byte format...

      TwoOctetFloat knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, val.floatValue());
      byte[] data = knxFloat.getData();

      // Assert exponent = 1...

      Assert.assertTrue(
          "Expected 0x8, got " + (data[0] & 0x8) + " at value " + val,
          (data[0] & 0x8) == 0x8
      );

      // Assert sign bit is set...

      Assert.assertTrue(
          "Expected 0x8, got " + ((data[0] & 0x80) >> 4) + " at value " + val,
          ((data[0] & 0x80) >> 4) == 0x8
      );
      

      // Convert from byte format to decimal....

      knxFloat = new TwoOctetFloat(DataPointType.VALUE_TEMP, data);
      BigDecimal value = knxFloat.resolve();


      // We should end with the same value we started with...

      Assert.assertTrue(
          "Not equal : " + value + ", " + val,
          value.compareTo(val) == 0
      );
    }

  }

}

