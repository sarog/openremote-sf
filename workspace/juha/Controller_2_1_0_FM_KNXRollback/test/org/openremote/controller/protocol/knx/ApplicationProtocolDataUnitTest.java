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
package org.openremote.controller.protocol.knx;

import junit.framework.Assert;
import org.junit.Test;
import org.openremote.controller.command.CommandParameter;
import org.openremote.controller.exception.ConversionException;
import org.openremote.controller.protocol.knx.datatype.DataPointType;
import org.openremote.controller.protocol.knx.datatype.KNXString;
import org.openremote.controller.protocol.knx.datatype.Unsigned8Bit;
import org.openremote.controller.utils.Strings;

/**
 * Unit tests for class {@link org.openremote.controller.protocol.knx.ApplicationProtocolDataUnit}.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class ApplicationProtocolDataUnitTest
{

  // CreateRange Tests ----------------------------------------------------------------------------

  /**
   * Basic test for createRange() call (DPT 5.010 GroupValue Write VALUE_1_UCOUNT - Unsigned 8 bit
   * value).
   *
   * @throws Exception    if test fails
   */
  @Test public void testCreateRange2() throws Exception
  {
    ApplicationProtocolDataUnit apdu = ApplicationProtocolDataUnit.createRange(
        new CommandParameter("1")
    );

    Assert.assertTrue(apdu.getApplicationLayerService() == ApplicationLayer.Service.GROUPVALUE_WRITE);
    Assert.assertTrue(apdu.getDataLength() == 2);
    Assert.assertTrue(apdu.getDataType() instanceof Unsigned8Bit);

    Assert.assertTrue(apdu.getDataType().getDataPointType() == DataPointType.Unsigned8BitValue.VALUE_1_UCOUNT);
    Assert.assertTrue(apdu.getDataType().getDataLength() == 2);
    Assert.assertTrue(((Unsigned8Bit) apdu.getDataType()).resolve() == 1);

    byte[] data = apdu.getDataType().getData();

    Assert.assertTrue(Strings.byteToUnsignedHexString(data [0]), (data [0] & 0xFF) == 1);
  }


  /**
   * Test createRange() call (DPT 5.010 GroupValue Write VALUE_1_UCOUNT - Unsigned 8 bit value)
   * at minimum boundary.
   *
   * @throws Exception      if test fails
   */
  @Test public void testCreateRangeMinValue() throws Exception
  {
    ApplicationProtocolDataUnit apdu = ApplicationProtocolDataUnit.createRange(
        new CommandParameter("0")
    );

    Assert.assertTrue(apdu.getApplicationLayerService() == ApplicationLayer.Service.GROUPVALUE_WRITE);
    Assert.assertTrue(apdu.getDataLength() == 2);
    Assert.assertTrue(apdu.getDataType() instanceof Unsigned8Bit);

    Assert.assertTrue(apdu.getDataType().getDataPointType() == DataPointType.Unsigned8BitValue.VALUE_1_UCOUNT);
    Assert.assertTrue(apdu.getDataType().getDataLength() == 2);
    Assert.assertTrue(((Unsigned8Bit) apdu.getDataType()).resolve() == 0);

    byte[] data = apdu.getDataType().getData();

    Assert.assertTrue(Strings.byteToUnsignedHexString(data [0]), (data [0] & 0xFF) == 0);
  }

  /**
   * Test createRange() call (DPT 5.010 GroupValue Write VALUE_1_UCOUNT - Unsigned 8 bit value) at
   * maximum boundary.
   *
   * @throws Exception    if test fails
   */
  @Test public void testCreateRangeMaxValue() throws Exception
  {
    ApplicationProtocolDataUnit apdu = ApplicationProtocolDataUnit.createRange(
        new CommandParameter("255")
    );

    Assert.assertTrue(apdu.getApplicationLayerService() == ApplicationLayer.Service.GROUPVALUE_WRITE);
    Assert.assertTrue(apdu.getDataLength() == 2);
    Assert.assertTrue(apdu.getDataType() instanceof Unsigned8Bit);

    Assert.assertTrue(apdu.getDataType().getDataPointType() == DataPointType.Unsigned8BitValue.VALUE_1_UCOUNT);
    Assert.assertTrue(apdu.getDataType().getDataLength() == 2);
    Assert.assertTrue(((Unsigned8Bit) apdu.getDataType()).resolve() == 255);

    byte[] data = apdu.getDataType().getData();

    Assert.assertTrue(Strings.byteToUnsignedHexString(data [0]), (data [0] & 0xFF) == 0xFF);
  }

  /**
   * Test createRange() call (DPT 5.010 GroupValue Write VALUE_1_UCOUNT - Unsigned 8 bit value)
   * with value overflow below minimum boundary.
   */
  @Test public void testCreateRangeUnderLowBoundary()
  {
    try
    {
      ApplicationProtocolDataUnit.createRange(new CommandParameter("-1"));

      Assert.fail("should not get here...");
    }

    catch (ConversionException e)
    {
      // expected...
    }
  }

  /**
   * Test createRange() call (DPT 5.010 GroupValue Write VALUE_1_UCOUNT - Unsigned 8 bit value)
   * with value overflow over maximum boundary.
   */
  @Test public void testCreateRangeAboveHighBoundary()
  {
    try
    {
      ApplicationProtocolDataUnit.createRange(new CommandParameter("256"));

      Assert.fail("should not get here...");
    }

    catch (ConversionException e)
    {
      // expected...
    }
  }

  /**
   * Test createRange() call (DPT 5.010 GroupValue Write VALUE_1_UCOUNT - Unsigned 8 bit value)
   * with fractional overflow over maximum boundary.
   *
   * @throws Exception    if test fails
   */
  @Test public void testCreateRangeFractionalAboveHighBoundary() throws Exception
  {

    // Note that the conversion from BigDecimal to int in the createRange() implementation will
    // discard all fractions (no rounding) so 255.9 will still translate to 255.

    ApplicationProtocolDataUnit apdu = ApplicationProtocolDataUnit.createRange(
        new CommandParameter("255.9")
    );

    Assert.assertTrue(apdu.getApplicationLayerService() == ApplicationLayer.Service.GROUPVALUE_WRITE);
    Assert.assertTrue(apdu.getDataLength() == 2);
    Assert.assertTrue(apdu.getDataType() instanceof Unsigned8Bit);

    Assert.assertTrue(apdu.getDataType().getDataPointType() == DataPointType.Unsigned8BitValue.VALUE_1_UCOUNT);
    Assert.assertTrue(apdu.getDataType().getDataLength() == 2);
    Assert.assertTrue(((Unsigned8Bit) apdu.getDataType()).resolve() == 255);

    byte[] data = apdu.getDataType().getData();

    Assert.assertTrue(Strings.byteToUnsignedHexString(data [0]), (data [0] & 0xFF) == 0xFF);
  }

  /**
   * Test createRange() call (DPT 5.010 GroupValue Write VALUE_1_UCOUNT - Unsigned 8 bit value)
   * with fractional overflow below minimum boundary.
   *
   * @throws Exception    if test fails
   */
  @Test public void testCreateRangeFractionalBelowLowBoundary() throws Exception
  {

    // Note that the conversion from BigDecimal to int in the createRange() implementation will
    // discard all fractions (no rounding) so -0.9 will still translate to 0.

    ApplicationProtocolDataUnit apdu = ApplicationProtocolDataUnit.createRange(
        new CommandParameter("-0.9")
    );

    Assert.assertTrue(apdu.getApplicationLayerService() == ApplicationLayer.Service.GROUPVALUE_WRITE);
    Assert.assertTrue(apdu.getDataLength() == 2);
    Assert.assertTrue(apdu.getDataType() instanceof Unsigned8Bit);

    Assert.assertTrue(apdu.getDataType().getDataPointType() == DataPointType.Unsigned8BitValue.VALUE_1_UCOUNT);
    Assert.assertTrue(apdu.getDataType().getDataLength() == 2);
    Assert.assertTrue(((Unsigned8Bit) apdu.getDataType()).resolve() == 0);

    byte[] data = apdu.getDataType().getData();

    Assert.assertTrue(Strings.byteToUnsignedHexString(data [0]), (data [0] & 0xFF) == 0x00);
  }

  /**
   * Test createRange() call (DPT 5.010 GroupValue Write VALUE_1_UCOUNT - Unsigned 8 bit value)
   * with null argument.
   */
  @Test public void testCreateRangeNullArg()
  {
    try
    {
      ApplicationProtocolDataUnit.createRange(null);

      Assert.fail("Should not get here...");
    }

    catch (ConversionException e)
    {
      // expected
    }
  }


  // CreateKNXASCIIResponse Tests -------------------------------------------------------------

  /**
   * Test creation of KNX ASCII string group value *response* APDU (DPT 16.001)
   *
   * @throws Exception    if test fails
   */
  @Test public void testCreateKNXASCIIResponse() throws Exception
  {
    ApplicationProtocolDataUnit apdu = ApplicationProtocolDataUnit.createKNXASCIIStringResponse("foo");

    Assert.assertTrue(apdu.getApplicationLayerService() == ApplicationLayer.Service.GROUPVALUE_RESPONSE);
    Assert.assertTrue(apdu.getDataLength() == 15);
    Assert.assertTrue(apdu.getDataType() instanceof KNXString);

    Assert.assertTrue(apdu.getDataType().getDataPointType() == DataPointType.KNXString.STRING_ASCII);
    Assert.assertTrue(apdu.getDataType().getDataLength() == 15);
    Assert.assertTrue(((KNXString) apdu.getDataType()).resolve().equals("foo"));

    byte[] data = apdu.getDataType().getData();

    Assert.assertTrue("" + (char)data [0], (data [0] & 0xFF) == 'f');
    Assert.assertTrue("" + (char)data [1], (data [1] & 0xFF) == 'o');
    Assert.assertTrue("" + (char)data [2], (data [2] & 0xFF) == 'o');
    Assert.assertTrue("" + (char)data [3], (data [3] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [4], (data [4] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [5], (data [5] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [6], (data [6] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [7], (data [7] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [8], (data [8] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [9], (data [9] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [10], (data [10] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [11], (data [11] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [12], (data [12] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [13], (data [13] & 0xFF) == 0x00);
  }

  /**
   * Test creation of KNX ASCII string group value *response* APDU (DPT 16.001) with null
   * string value arg (we convert to empty string, no exceptions thrown)
   *
   * @throws Exception    if test fails
   */
  @Test public void testCreateKNXASCIIResponseNullArg() throws Exception
  {
    // We opt to convert nulls to empty strings in the implementation, therefore no exception...

    ApplicationProtocolDataUnit apdu = ApplicationProtocolDataUnit.createKNXASCIIStringResponse(null);

    Assert.assertTrue(apdu.getApplicationLayerService() == ApplicationLayer.Service.GROUPVALUE_RESPONSE);
    Assert.assertTrue(apdu.getDataLength() == 15);
    Assert.assertTrue(apdu.getDataType() instanceof KNXString);

    Assert.assertTrue(apdu.getDataType().getDataPointType() == DataPointType.KNXString.STRING_ASCII);
    Assert.assertTrue(apdu.getDataType().getDataLength() == 15);
    Assert.assertTrue(((KNXString) apdu.getDataType()).resolve().equals(""));

    byte[] data = apdu.getDataType().getData();

    Assert.assertTrue("" + (char)data [0], (data [0] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [1], (data [1] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [2], (data [2] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [3], (data [3] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [4], (data [4] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [5], (data [5] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [6], (data [6] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [7], (data [7] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [8], (data [8] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [9], (data [9] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [10], (data [10] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [11], (data [11] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [12], (data [12] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [13], (data [13] & 0xFF) == 0x00);
  }

  /**
   * Test creation of KNX ASCII string group value *response* APDU (DPT 16.001) with non-ascii
   * characters (relying on default charset replacement byte conversion which may or may not
   * be a stable assumption)
   *
   * @throws Exception    if test fails
   */
  @Test public void testCreateKNXASCIIResponseNonASCII() throws Exception
  {

    ApplicationProtocolDataUnit apdu = ApplicationProtocolDataUnit.createKNXASCIIStringResponse("äö");

    Assert.assertTrue(apdu.getApplicationLayerService() == ApplicationLayer.Service.GROUPVALUE_RESPONSE);
    Assert.assertTrue(apdu.getDataLength() == 15);
    Assert.assertTrue(apdu.getDataType() instanceof KNXString);

    Assert.assertTrue(apdu.getDataType().getDataPointType() == DataPointType.KNXString.STRING_ASCII);
    Assert.assertTrue(apdu.getDataType().getDataLength() == 15);


    // NOTE : This behavior is actually indeterminate and therefore makes the test somewhat brittle.
    //        We currently rely on the 'default' charset replacement byte conversion in the ASCII
    //        charset 'US-ASCII' used in KNXString implementation. It appears to convert non-ascii
    //        characters to question marks. It's a fairly reasonable assumption that this works
    //        across all implementations and deployments but to fix it reliably, the Charset
    //        encoder class should be used as described in the Javadoc for String.getBytes(Charset)


    Assert.assertTrue(
        ((KNXString) apdu.getDataType()).resolve(),
        ((KNXString) apdu.getDataType()).resolve().equals("??")
    );

    byte[] data = apdu.getDataType().getData();

    Assert.assertTrue("" + (char)data [0], (data [0] & 0xFF) == '?');
    Assert.assertTrue("" + (char)data [1], (data [1] & 0xFF) == '?');
    Assert.assertTrue("" + (char)data [2], (data [2] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [3], (data [3] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [4], (data [4] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [5], (data [5] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [6], (data [6] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [7], (data [7] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [8], (data [8] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [9], (data [9] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [10], (data [10] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [11], (data [11] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [12], (data [12] & 0xFF) == 0x00);
    Assert.assertTrue("" + (char)data [13], (data [13] & 0xFF) == 0x00);
  }

  /**
   * Test creation of KNX ASCII string group value *response* APDU (DPT 16.001) with string
   * value above 14 character limit.
   */
  @Test public void testCreateKNXASCIIResponseStringTooLong()
  {

    // We currently throw an exception -- trimming the string instead might be just as valid
    // approach. But currently we can't determine the error type due to it being untyped so
    // exception it is. KNXString should be modified for better error reporting.

    try
    {
      ApplicationProtocolDataUnit.createKNXASCIIStringResponse("123456789012345");

      Assert.fail("should not get here...");
    }

    catch (ConversionException e)
    {
      // expected...
    }
  }


  // ParseKNXFrameAPDU Tests ------------------------------------------------------------------

  /**
   * Test APDU parse from frame bytes with GroupValue Write service and a single byte payload
   *
   * @throws Exception      if test fails
   */
  @Test public void testParseKNXFrameAPDU() throws Exception
  {
    ApplicationProtocolDataUnit apdu = ApplicationProtocolDataUnit.parseKNXFrameAPDU(

        2,     // data length

        new byte[]
            {
                (byte) ApplicationLayer.Service.GROUPVALUE_WRITE.getTPCIAPCI(),
                (byte) ApplicationLayer.Service.GROUPVALUE_WRITE.getAPCIData(),
                0x01
            }
    );

    Assert.assertTrue(apdu.getApplicationLayerService() == ApplicationLayer.Service.GROUPVALUE_WRITE);
    Assert.assertTrue(apdu.getDataLength() == 2);

    Assert.assertTrue((apdu.getDataType().getData() [0] & 0xFF) == 0x01);
  }


  /**
   * Test APDU parse from frame bytes with GroupValue Write service and a single byte payload
   * (upper boundary, signed to unsigned byte conversion)
   *
   * @throws Exception    if test fails
   */
  @Test public void testParseKNXFrameAPDUMaximumValue() throws Exception
  {
    ApplicationProtocolDataUnit apdu = ApplicationProtocolDataUnit.parseKNXFrameAPDU(

        2,     // data length

        new byte[]
            {
                (byte) ApplicationLayer.Service.GROUPVALUE_WRITE.getTPCIAPCI(),
                (byte) ApplicationLayer.Service.GROUPVALUE_WRITE.getAPCIData(),
                (byte) 0xFF
            }
    );

    Assert.assertTrue(apdu.getApplicationLayerService() == ApplicationLayer.Service.GROUPVALUE_WRITE);
    Assert.assertTrue(apdu.getDataLength() == 2);

    byte[] data = apdu.getDataType().getData();

    Assert.assertTrue(Strings.byteToUnsignedHexString(data [0]), (data [0] & 0xFF) == 0xFF);
  }


  /**
   * Test APDU parse from frame bytes with GroupValue Write service and a 4 byte payload.
   *
   * @throws Exception  if test fails
   */
  @Test public void testParseKNXFrameAPDUWriteFourBytes() throws Exception
  {
    ApplicationProtocolDataUnit apdu = ApplicationProtocolDataUnit.parseKNXFrameAPDU(

        5,     // data length

        new byte[]
            {
                (byte) ApplicationLayer.Service.GROUPVALUE_WRITE.getTPCIAPCI(),
                (byte) ApplicationLayer.Service.GROUPVALUE_WRITE.getAPCIData(),
                (byte) 0xAA,
                (byte) 0xBB,
                (byte) 0xCC,
                (byte) 0xDD
            }
    );

    Assert.assertTrue(apdu.getApplicationLayerService() == ApplicationLayer.Service.GROUPVALUE_WRITE);
    Assert.assertTrue(apdu.getDataLength() == 5);

    byte[] data = apdu.getDataType().getData();

    Assert.assertTrue(Strings.byteToUnsignedHexString(data [0]), (data [0] & 0xFF) == 0xAA);
    Assert.assertTrue(Strings.byteToUnsignedHexString(data [1]), (data [1] & 0xFF) == 0xBB);
    Assert.assertTrue(Strings.byteToUnsignedHexString(data [2]), (data [2] & 0xFF) == 0xCC);
    Assert.assertTrue(Strings.byteToUnsignedHexString(data [3]), (data [3] & 0xFF) == 0xDD);
  }


  /**
   * Test APDU parse from frame bytes behavior with GroupValue Write service and a four
   * byte payload when non-zero bits are inserted in unused 6-bit data field.
   *
   * @throws Exception  if test fails
   */
  @Test public void testParseKNXFrameAPDUWriteFourBytesWithSixBitNoise() throws Exception
  {
    // The extra data bits (0x10) in the six bit data field of APCI/Data in CEMI frame should
    // be ignored since we are using the non-six-bit GroupValue Write service...

    ApplicationProtocolDataUnit apdu = ApplicationProtocolDataUnit.parseKNXFrameAPDU(

        5,     // data length

        new byte[]
            {
                (byte) ApplicationLayer.Service.GROUPVALUE_WRITE.getTPCIAPCI(),
                (byte) (ApplicationLayer.Service.GROUPVALUE_WRITE.getAPCIData() + 0x10),
                (byte) 0xAA,
                (byte) 0xBB,
                (byte) 0xCC,
                (byte) 0xDD
            }
    );

    Assert.assertTrue(apdu.getApplicationLayerService() == ApplicationLayer.Service.GROUPVALUE_WRITE);
    Assert.assertTrue(apdu.getDataLength() == 5);

    byte[] data = apdu.getDataType().getData();

    Assert.assertTrue(Strings.byteToUnsignedHexString(data [0]), (data [0] & 0xFF) == 0xAA);
    Assert.assertTrue(Strings.byteToUnsignedHexString(data [1]), (data [1] & 0xFF) == 0xBB);
    Assert.assertTrue(Strings.byteToUnsignedHexString(data [2]), (data [2] & 0xFF) == 0xCC);
    Assert.assertTrue(Strings.byteToUnsignedHexString(data [3]), (data [3] & 0xFF) == 0xDD);
  }

  /**
   * Test APDU parse from frame bytes behavior when data bytes are missing.
   */
  @Test public void testParseKNXFrameAPDUWithMissingDataBytesWithWriteService()
  {
    try
    {
      ApplicationProtocolDataUnit.parseKNXFrameAPDU(

          5,     // data length

          new byte[]
              {
                  (byte) ApplicationLayer.Service.GROUPVALUE_WRITE.getTPCIAPCI(),
                  (byte) ApplicationLayer.Service.GROUPVALUE_WRITE.getAPCIData()
              }
      );

      Assert.fail("should not get here...");
    }

    catch (CommonEMI.InvalidFrameException e)
    {
      // expected
    }
  }

  /**
   * Test APDU parse from frame bytes behavior when data bytes are missing.
   */
  @Test public void testParseKNXFrameAPDUWithMissingDataBytesWithResponseService()
  {
    try
    {
      ApplicationProtocolDataUnit.parseKNXFrameAPDU(

          5,     // data length

          new byte[]
              {
                  (byte) ApplicationLayer.Service.GROUPVALUE_RESPONSE.getTPCIAPCI(),
                  (byte) ApplicationLayer.Service.GROUPVALUE_RESPONSE.getAPCIData()
              }
      );

      Assert.fail("should not get here...");
    }

    catch (CommonEMI.InvalidFrameException e)
    {
      // expected
    }
  }

  /**
   * Test APDU parse from frame bytes behavior when data length and data size mismatch.
   *
   * @throws Exception    if test fails
   */
  @Test public void testConstructKNXFrameAPDUWithIncompleteDataBytes() throws Exception
  {
    try
    {
      ApplicationProtocolDataUnit.parseKNXFrameAPDU(

          5,     // data length

          new byte[]
              {
                  (byte) ApplicationLayer.Service.GROUPVALUE_WRITE.getTPCIAPCI(),
                  (byte) ApplicationLayer.Service.GROUPVALUE_WRITE.getAPCIData(),
                  (byte) 0xFA
              }
      );

      Assert.fail("should not get here...");
    }

    catch (CommonEMI.InvalidFrameException e)
    {
      // expected...
    }
  }


  /**
   * Test APDU parse from frame bytes with 6-bit GroupValue Write service.
   *
   * @throws Exception      if test fails
   */
  @Test public void testParseKNXFrameAPDUSixBitWrite() throws Exception
  {
    ApplicationProtocolDataUnit apdu = ApplicationProtocolDataUnit.parseKNXFrameAPDU(

        1,     // data length

        new byte[]
            {
                (byte) ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT.getTPCIAPCI(),
                (byte) (ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT.getAPCIData() + 0x01)
            }
    );

    Assert.assertTrue(apdu.getApplicationLayerService() == ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT);
    Assert.assertTrue(apdu.getDataLength() == 1);

    byte[] data = apdu.getDataType().getData();

    Assert.assertTrue(Strings.byteToUnsignedHexString(data [0]), (data [0] & 0xFF) == 0x01);
  }


  /**
   * Test APDU parse from frame bytes with 6-bit GroupValue Response service.
   *
   * @throws Exception      if test fails
   */
  @Test public void testConstructKNXFrameAPDUSixBitResponse() throws Exception
  {
    ApplicationProtocolDataUnit apdu = ApplicationProtocolDataUnit.parseKNXFrameAPDU(

        1,     // data length

        new byte[]
            {
                (byte) ApplicationLayer.Service.GROUPVALUE_RESPONSE_6BIT.getTPCIAPCI(),
                (byte) (ApplicationLayer.Service.GROUPVALUE_RESPONSE_6BIT.getAPCIData() + 0x0F)
            }
    );

    Assert.assertTrue(apdu.getApplicationLayerService() == ApplicationLayer.Service.GROUPVALUE_RESPONSE_6BIT);
    Assert.assertTrue(apdu.getDataLength() == 1);

    byte[] data = apdu.getDataType().getData();

    Assert.assertTrue(Strings.byteToUnsignedHexString(data [0]), (data [0] & 0xFF) == 0x0F);
  }


  /**
   * Test APDU parse from frame bytes with GroupValue Response service.
   *
   * @throws Exception      if test fails
   */
  @Test public void testParseKNXFrameAPDUGroupValueResponse() throws Exception
  {
    ApplicationProtocolDataUnit apdu = ApplicationProtocolDataUnit.parseKNXFrameAPDU(

        4,     // data length

        new byte[]
            {
                (byte) ApplicationLayer.Service.GROUPVALUE_RESPONSE.getTPCIAPCI(),
                (byte) ApplicationLayer.Service.GROUPVALUE_RESPONSE.getAPCIData(),
                (byte) 0x01,
                (byte) 0x02,
                (byte) 0x03
            }
    );

    Assert.assertTrue(apdu.getApplicationLayerService() == ApplicationLayer.Service.GROUPVALUE_RESPONSE);
    Assert.assertTrue(apdu.getDataLength() == 4);

    byte[] data = apdu.getDataType().getData();

    Assert.assertTrue(Strings.byteToUnsignedHexString(data [0]), (data [0] & 0xFF) == 0x01);
    Assert.assertTrue(Strings.byteToUnsignedHexString(data [1]), (data [1] & 0xFF) == 0x02);
    Assert.assertTrue(Strings.byteToUnsignedHexString(data [2]), (data [2] & 0xFF) == 0x03);
  }

  /**
   * Test APDU parse from frame bytes with GroupValue Response service when extra data bits
   * are inserted in the 6-bit data field (they should be ignored).
   *
   * @throws Exception      if test fails
   */
  @Test public void testParseKNXFrameAPDUGroupValueResponseUnusedDataBits() throws Exception
  {
    // The extra data in 6-bit data field (0x05) should be ignored since we are using the
    // non-six-bit GroupValue Response service...

    ApplicationProtocolDataUnit apdu = ApplicationProtocolDataUnit.parseKNXFrameAPDU(

        4,     // data length

        new byte[]
            {
                (byte) ApplicationLayer.Service.GROUPVALUE_RESPONSE.getTPCIAPCI(),
                (byte) (ApplicationLayer.Service.GROUPVALUE_RESPONSE.getAPCIData() + 0x05),
                (byte) 0x01,
                (byte) 0x02,
                (byte) 0x03
            }
    );

    Assert.assertTrue(apdu.getApplicationLayerService() == ApplicationLayer.Service.GROUPVALUE_RESPONSE);
    Assert.assertTrue(apdu.getDataLength() == 4);

    byte[] data = apdu.getDataType().getData();

    Assert.assertTrue(Strings.byteToUnsignedHexString(data [0]), (data [0] & 0xFF) == 0x01);
    Assert.assertTrue(Strings.byteToUnsignedHexString(data [1]), (data [1] & 0xFF) == 0x02);
    Assert.assertTrue(Strings.byteToUnsignedHexString(data [2]), (data [2] & 0xFF) == 0x03);
  }

  /**
   * Test APDU parse from frame bytes with 6-bit GroupValue Write service (minimum value)
   *
   * @throws Exception      if test fails
   */
  @Test public void testParseKNXFrameAPDUSixBitWriteMinimumValue() throws Exception
  {
    ApplicationProtocolDataUnit apdu = ApplicationProtocolDataUnit.parseKNXFrameAPDU(

        1,     // data length

        new byte[]
            {
                (byte) ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT.getTPCIAPCI(),
                (byte) (ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT.getAPCIData() + 0x00)
            }
    );

    Assert.assertTrue(apdu.getApplicationLayerService() == ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT);
    Assert.assertTrue(apdu.getDataLength() == 1);

    byte[] data = apdu.getDataType().getData();

    Assert.assertTrue(Strings.byteToUnsignedHexString(data [0]), (data [0] & 0xFF) == 0x00);
  }


  /**
   * Test APDU parse from frame bytes with 6-bit GroupValue Write service (maximum value)
   *
   * @throws Exception      if test fails
   */
  @Test public void testParseKNXFrameAPDUSixBitWriteMaximumValue() throws Exception
  {
    ApplicationProtocolDataUnit apdu = ApplicationProtocolDataUnit.parseKNXFrameAPDU(

        1,     // data length

        new byte[]
            {
                (byte) ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT.getTPCIAPCI(),
                (byte) (ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT.getAPCIData() + 0x3F)
            }
    );

    Assert.assertTrue(apdu.getApplicationLayerService() == ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT);
    Assert.assertTrue(apdu.getDataLength() == 1);

    byte[] data = apdu.getDataType().getData();

    Assert.assertTrue(Strings.byteToUnsignedHexString(data [0]), (data [0] & 0xFF) == 0x3F);
  }



  /**
   * Test APDU parse from frame bytes with GroupValue Read service.
   *
   * @throws Exception      if test fails
   */
  @Test public void testParseKNXFrameAPDUGroupValueRead() throws Exception
  {

    // For GroupValue Read, the data bits (0x0A) should be ignored and reset to zero...

    ApplicationProtocolDataUnit apdu = ApplicationProtocolDataUnit.parseKNXFrameAPDU(

        1,     // data length

        new byte[]
            {
                (byte) ApplicationLayer.Service.GROUPVALUE_READ.getTPCIAPCI(),
                (byte) (ApplicationLayer.Service.GROUPVALUE_READ.getAPCIData() + 0x0A)
            }
    );

    Assert.assertTrue(apdu.getApplicationLayerService() == ApplicationLayer.Service.GROUPVALUE_READ);
    Assert.assertTrue(apdu.getDataLength() == 1);

    byte[] data = apdu.getDataType().getData();

    Assert.assertTrue(Strings.byteToUnsignedHexString(data [0]), (data [0] & 0xFF) == 0x00);
  }

  /**
   * Test APDU parse behavior with null frame array.
   *
   * @throws Exception      if test fails
   */
  @Test public void testConstructKNXFrameAPDUNullFrameArray() throws Exception
  {
    try
    {
      ApplicationProtocolDataUnit.parseKNXFrameAPDU(1, null);

      Assert.fail("should not get here...");
    }

    catch (CommonEMI.InvalidFrameException e)
    {
      // expected...
    }
  }

  /**
   * Test APDU parse behavior with too short frame array.
   *
   * @throws Exception      if test fails
   */
  @Test public void testConstructKNXFrameAPDUtooShortFrameArray() throws Exception
  {
    try
    {
      ApplicationProtocolDataUnit.parseKNXFrameAPDU(

          1, // data length

          new byte[]
              {
                  (byte) ApplicationLayer.Service.GROUPVALUE_WRITE.getTPCIAPCI()
              }
      );

      Assert.fail("should not get here...");
    }

    catch (CommonEMI.InvalidFrameException e)
    {
      // expected...
    }
  }



  /**
   * Test APDU parse behavior with unknown 6-bit service identifier.
   *
   * @throws Exception      if test fails
   */
  @Test public void testParseKNXFrameAPDUUnknownSixBitServiceType() throws Exception
  {
    try
    {
      ApplicationProtocolDataUnit.parseKNXFrameAPDU(

          1, // data length

          new byte[]
              {
                  (byte) 0xFF,
                  (byte) 0xFF
              }
      );

      Assert.fail("should not get here...");
    }

    catch (CommonEMI.InvalidFrameException e)
    {
      // expected...
    }
  }


  /**
   * Test APDU parse behavior with unknown service type identifier.
   *
   * @throws Exception      if test fails
   */
  @Test public void testParseKNXFrameAPDUUnknownServiceType() throws Exception
  {
    try
    {
      ApplicationProtocolDataUnit.parseKNXFrameAPDU(

          3, // data length

          new byte[]
              {
                  (byte) 0xFF,
                  (byte) 0xFF,
                  (byte) 0xFF,
                  (byte) 0xFF
              }
      );

      Assert.fail("should not get here...");
    }

    catch (CommonEMI.InvalidFrameException e)
    {
      // expected...
    }
  }

}

