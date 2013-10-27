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

/**
 * Unit tests for class {@link org.openremote.controller.protocol.knx.ApplicationLayer}
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class ApplicationLayerTest
{


  // resolveCEMIServiceType Tests -----------------------------------------------------------------

  /**
   * Tests that known service types are resolved as expected.
   *
   * @throws Exception    if test fails
   */
  @Test public void testResolveCEMIServiceType() throws Exception
  {
    ApplicationLayer.Service s = ApplicationLayer.Service.resolveCEMIServiceType(
        1,
        ApplicationLayer.Service.GROUPVALUE_READ.getTPCIAPCI(),
        ApplicationLayer.Service.GROUPVALUE_READ.getAPCIData()
    );

    Assert.assertTrue(s == ApplicationLayer.Service.GROUPVALUE_READ);


    s = ApplicationLayer.Service.resolveCEMIServiceType(
        200,
        ApplicationLayer.Service.GROUPVALUE_RESPONSE.getTPCIAPCI(),
        ApplicationLayer.Service.GROUPVALUE_RESPONSE.getAPCIData()
    );

    Assert.assertTrue(s == ApplicationLayer.Service.GROUPVALUE_RESPONSE);


    s = ApplicationLayer.Service.resolveCEMIServiceType(
        1,
        ApplicationLayer.Service.GROUPVALUE_RESPONSE_6BIT.getTPCIAPCI(),
        ApplicationLayer.Service.GROUPVALUE_RESPONSE_6BIT.getAPCIData()
    );

    Assert.assertTrue(s == ApplicationLayer.Service.GROUPVALUE_RESPONSE_6BIT);



    s = ApplicationLayer.Service.resolveCEMIServiceType(
        1,
        ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT.getTPCIAPCI(),
        ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT.getAPCIData()
    );

    Assert.assertTrue(s == ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT);


    s = ApplicationLayer.Service.resolveCEMIServiceType(
        8,
        ApplicationLayer.Service.GROUPVALUE_WRITE.getTPCIAPCI(),
        ApplicationLayer.Service.GROUPVALUE_WRITE.getAPCIData()
    );

    Assert.assertTrue(s == ApplicationLayer.Service.GROUPVALUE_WRITE);
  }


  /**
   * Test resolveCEMIServiceType() behavior if the CEMI data lenght field value doesn't
   * match the expected length.
   */
  @Test public void testResolveCEMIServiceTypeDataLenMismatch()
  {

    // GroupValueRead should always have data length field with value 1...

    try
    {
      ApplicationLayer.Service.resolveCEMIServiceType(
          2,
          ApplicationLayer.Service.GROUPVALUE_READ.getTPCIAPCI(),
          ApplicationLayer.Service.GROUPVALUE_READ.getAPCIData()
      );

      Assert.fail("should not get here...");
    }

    catch (CommonEMI.InvalidFrameException e)
    {
      // expected...
    }

    try
    {
      ApplicationLayer.Service.resolveCEMIServiceType(
          0,
          ApplicationLayer.Service.GROUPVALUE_READ.getTPCIAPCI(),
          ApplicationLayer.Service.GROUPVALUE_READ.getAPCIData()
      );

      Assert.fail("should not get here...");
    }

    catch (CommonEMI.InvalidFrameException e)
    {
      // expected...
    }


    // GroupValue Response will have either length 1 (6 bit response) or above...

    try
    {
      ApplicationLayer.Service s = ApplicationLayer.Service.resolveCEMIServiceType(
          0,
          ApplicationLayer.Service.GROUPVALUE_RESPONSE.getTPCIAPCI(),
          ApplicationLayer.Service.GROUPVALUE_RESPONSE.getAPCIData()
      );

      Assert.fail(s.name() + " should not get here...");
    }

    catch (CommonEMI.InvalidFrameException e)
    {
      // expected....
    }


    try
    {
      ApplicationLayer.Service.resolveCEMIServiceType(
          0,
          ApplicationLayer.Service.GROUPVALUE_RESPONSE_6BIT.getTPCIAPCI(),
          ApplicationLayer.Service.GROUPVALUE_RESPONSE_6BIT.getAPCIData()
      );

      Assert.fail("should not get here...");
    }

    catch (CommonEMI.InvalidFrameException e)
    {
      // expected...
    }


    // GroupValue Write will have either length 1 (6 bit response) or above...

    try
    {
      ApplicationLayer.Service.resolveCEMIServiceType(
          0,
          ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT.getTPCIAPCI(),
          ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT.getAPCIData()
      );

      Assert.fail("should not get here...");
    }

    catch (CommonEMI.InvalidFrameException e)
    {
      // expected...
    }

    try
    {
      ApplicationLayer.Service.resolveCEMIServiceType(
          0,
          ApplicationLayer.Service.GROUPVALUE_WRITE.getTPCIAPCI(),
          ApplicationLayer.Service.GROUPVALUE_WRITE.getAPCIData()
      );

      Assert.fail("should not get here...");
    }

    catch (CommonEMI.InvalidFrameException e)
    {
      // expected...
    }
  }


  /**
   * Tests resolveCemiServiceType() behavior with values that cannot be resolved.
   */
  @Test public void testResolveCEMIServiceTypeUnknownValues()
  {
    try
    {
      ApplicationLayer.Service.resolveCEMIServiceType(0xFF, 0xFF, 0xFF);

      Assert.fail("should not get here...");
    }

    catch (CommonEMI.InvalidFrameException e)
    {
      // expected...
    }
  }

  /**
   * Tests resolveCEMIServiceType() behavior with value that are above the byte (8 bit) range.
   */
  @Test public void testResolveCEMIServiceTypeOutOfRangeValues()
  {
    try
    {
      ApplicationLayer.Service.resolveCEMIServiceType(0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF);

      Assert.fail("should not get here...");
    }

    catch (CommonEMI.InvalidFrameException e)
    {
      // expected...
    }
  }


  // isSixBitGroupValueResponse Tests -------------------------------------------------------------

  /**
   * Basic test for isSixBitGroupValueRespose() call.
   */
  @Test public void testIsSixBitGroupValueResponse()
  {
    boolean b = ApplicationLayer.Service.isSixBitGroupValueResponse(
        1,
        ApplicationLayer.Service.GROUPVALUE_RESPONSE_6BIT.getTPCIAPCI(),
        ApplicationLayer.Service.GROUPVALUE_RESPONSE_6BIT.getAPCIData()
    );

    Assert.assertTrue(b);
  }

  /**
   * Test isSixBigGroupValueResponse() with unexpected data length value.
   */
  @Test public void testIsSixBitGroupValueResponseWrongDataLen()
  {
    boolean b = ApplicationLayer.Service.isSixBitGroupValueResponse(
        2,
        ApplicationLayer.Service.GROUPVALUE_RESPONSE_6BIT.getTPCIAPCI(),
        ApplicationLayer.Service.GROUPVALUE_RESPONSE_6BIT.getAPCIData()
    );

    Assert.assertTrue(!b);
  }


  /**
   * Test isSixBigGroupValueResponse() with unexpected data length value.
   */
  @Test public void testIsSixBitGroupValueResponseWrongDataLen2()
  {
    boolean b = ApplicationLayer.Service.isSixBitGroupValueResponse(
        0,
        ApplicationLayer.Service.GROUPVALUE_RESPONSE_6BIT.getTPCIAPCI(),
        ApplicationLayer.Service.GROUPVALUE_RESPONSE_6BIT.getAPCIData()
    );

    Assert.assertTrue(!b);
  }

  /**
   * Test isSixBigGroupValueResponse() with unexpected tpci/apci and apci/data values.
   */
  @Test public void testIsSixBitGroupValueResponseWrongBits()
  {
    boolean b = ApplicationLayer.Service.isSixBitGroupValueResponse(
        1, 0x00, 0x00
    );

    Assert.assertTrue(!b);
  }

  /**
   * Test isSixBigGroupValueResponse() with unexpected tpci/apci and apci/data values.
   */
  @Test public void testIsSixBitGroupValueResponseWrongBits2()
  {
    boolean b = ApplicationLayer.Service.isSixBitGroupValueResponse(
        1, 0xFF, 0xFF
    );

    Assert.assertTrue(!b);
  }



  // isSixBitGroupValueWrite Tests ----------------------------------------------------------------


  /**
   * Basic test for isSixBitGroupValueWrite() call.
   */
  @Test public void testIsSixBitGroupValueWrite()
  {
    boolean b = ApplicationLayer.Service.isSixBitGroupValueWrite(
        1,
        ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT.getTPCIAPCI(),
        ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT.getAPCIData()
    );

    Assert.assertTrue(b);
  }

  /**
   * Test isSixBigGroupValueWrite() with unexpected data length value.
   */
  @Test public void testIsSixBitGroupValueWriteWrongDataLen()
  {
    boolean b = ApplicationLayer.Service.isSixBitGroupValueWrite(
        2,
        ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT.getTPCIAPCI(),
        ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT.getAPCIData()
    );

    Assert.assertTrue(!b);
  }


  /**
   * Test isSixBigGroupValueWrite() with unexpected data length value.
   */
  @Test public void testIsSixBitGroupValueWriteWrongDataLen2()
  {
    boolean b = ApplicationLayer.Service.isSixBitGroupValueWrite(
        0,
        ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT.getTPCIAPCI(),
        ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT.getAPCIData()
    );

    Assert.assertTrue(!b);
  }

  /**
   * Test isSixBigGroupValueResponse() with unexpected tpci/apci and apci/data values.
   */
  @Test public void testIsSixBitGroupValueWriteWrongBits()
  {
    boolean b = ApplicationLayer.Service.isSixBitGroupValueWrite(
        1, 0x00, 0x00
    );

    Assert.assertTrue(!b);
  }

  /**
   * Test isSixBigGroupValueResponse() with unexpected tpci/apci and apci/data values.
   */
  @Test public void testIsSixBitGroupValueWriteWrongBits2()
  {
    boolean b = ApplicationLayer.Service.isSixBitGroupValueWrite(
        1, 0xFF, 0xFF
    );

    Assert.assertTrue(!b);
  }


  // isGroupValueRead Tests -----------------------------------------------------------------------


  /**
   * Basic test for isGroupValueRead() call.
   */
  @Test public void testIsGroupValueRead()
  {
    boolean b = ApplicationLayer.Service.isGroupValueRead(
        1,
        ApplicationLayer.Service.GROUPVALUE_READ.getTPCIAPCI(),
        ApplicationLayer.Service.GROUPVALUE_READ.getAPCIData()
    );

    Assert.assertTrue(b);
  }


  /**
   * Test isGroupValueRead() with unexpected data length value.
   */
  @Test public void testIsGroupValueReadWrongDataLen()
  {
    boolean b = ApplicationLayer.Service.isGroupValueRead(
        2,
        ApplicationLayer.Service.GROUPVALUE_READ.getTPCIAPCI(),
        ApplicationLayer.Service.GROUPVALUE_READ.getAPCIData()
    );

    Assert.assertTrue(!b);
  }

  /**
   * Test isGroupValueRead() with unexpected data length value.
   */
  @Test public void testGroupValueReadWrongDataLen2()
  {
    boolean b = ApplicationLayer.Service.isGroupValueRead(
        0,
        ApplicationLayer.Service.GROUPVALUE_READ.getTPCIAPCI(),
        ApplicationLayer.Service.GROUPVALUE_READ.getAPCIData()
    );

    Assert.assertTrue(!b);
  }

  /**
   * Test isGroupValueRead() with unexpected tpci/apci and apci/data values.
   */
  @Test public void testIsGroupValueREADWrongBits()
  {
    boolean b = ApplicationLayer.Service.isGroupValueRead(
        1, 0xFF, 0xFF
    );

    Assert.assertTrue(!b);
  }

  /**
   * Test isGroupValueRead() with unexpected tpci/apci and apci/data values.
   */
  @Test public void testIsGroupValueReadWrongBits2()
  {
    boolean b = ApplicationLayer.Service.isGroupValueRead(
        1, 0x00, 0x40
    );

    Assert.assertTrue(!b);
  }


  /**
   * Test isGroupValueRead() with unexpected tpci/apci and apci/data values.
   */
  @Test public void testIsGroupValueReadWrongBits3()
  {
    boolean b = ApplicationLayer.Service.isGroupValueRead(
        1, 0x00, 0x80
    );

    Assert.assertTrue(!b);
  }


  // isGroupValueResponse Tests -------------------------------------------------------------------


  /**
   * Basic test for isGroupValueResponse() call.
   */
  @Test public void testIsGroupValueResponse()
  {
    boolean b = ApplicationLayer.Service.isGroupValueResponse(
        2,
        ApplicationLayer.Service.GROUPVALUE_RESPONSE.getTPCIAPCI(),
        ApplicationLayer.Service.GROUPVALUE_RESPONSE.getAPCIData()
    );

    Assert.assertTrue(b);
  }


  /**
   * Test isGroupValueResponse() with unexpected data length value.
   */
  @Test public void testIsGroupValueResponseWrongDataLen()
  {
    boolean b = ApplicationLayer.Service.isGroupValueResponse(
        1,
        ApplicationLayer.Service.GROUPVALUE_RESPONSE.getTPCIAPCI(),
        ApplicationLayer.Service.GROUPVALUE_RESPONSE.getAPCIData()
    );

    Assert.assertTrue(!b);
  }

  /**
   * Test isGroupValueResponse() with unexpected data length value.
   */
  @Test public void testGroupValueResponseWrongDataLen2()
  {
    boolean b = ApplicationLayer.Service.isGroupValueResponse(
        0,
        ApplicationLayer.Service.GROUPVALUE_RESPONSE.getTPCIAPCI(),
        ApplicationLayer.Service.GROUPVALUE_RESPONSE.getAPCIData()
    );

    Assert.assertTrue(!b);
  }

  /**
   * Test isGroupValueResponse() with unexpected tpci/apci and apci/data values.
   */
  @Test public void testIsGroupValueResponseWrongBits()
  {
    boolean b = ApplicationLayer.Service.isGroupValueResponse(
        2, 0x00, 0x00
    );

    Assert.assertTrue(!b);
  }

  /**
   * Test isGroupValueResponse() with unexpected tpci/apci and apci/data values.
   */
  @Test public void testIsGroupValueResponseWrongBits2()
  {
    boolean b = ApplicationLayer.Service.isGroupValueResponse(
        20, 0x00, 0x80
    );

    Assert.assertTrue(!b);
  }


  /**
   * Test isGroupValueResponse() with unexpected tpci/apci and apci/data values.
   */
  @Test public void testIsGroupValueResponseWrongBits3()
  {
    boolean b = ApplicationLayer.Service.isGroupValueRead(
        100, 0x01, 0x01
    );

    Assert.assertTrue(!b);
  }


  // isGroupValueWrite Tests ----------------------------------------------------------------------



  /**
   * Basic test for isGroupValueWrite() call.
   */
  @Test public void testIsGroupValueWrite()
  {
    boolean b = ApplicationLayer.Service.isGroupValueWrite(
        2,
        ApplicationLayer.Service.GROUPVALUE_WRITE.getTPCIAPCI(),
        ApplicationLayer.Service.GROUPVALUE_WRITE.getAPCIData()
    );

    Assert.assertTrue(b);
  }


  /**
   * Test isGroupValueWrite() with unexpected data length value.
   */
  @Test public void testIsGroupValueWriteWrongDataLen()
  {
    boolean b = ApplicationLayer.Service.isGroupValueWrite(
        1,
        ApplicationLayer.Service.GROUPVALUE_WRITE.getTPCIAPCI(),
        ApplicationLayer.Service.GROUPVALUE_WRITE.getAPCIData()
    );

    Assert.assertTrue(!b);
  }

  /**
   * Test isGroupValueWrite() with unexpected data length value.
   */
  @Test public void testGroupValueWriteWrongDataLen2()
  {
    boolean b = ApplicationLayer.Service.isGroupValueWrite(
        0,
        ApplicationLayer.Service.GROUPVALUE_WRITE.getTPCIAPCI(),
        ApplicationLayer.Service.GROUPVALUE_WRITE.getAPCIData()
    );

    Assert.assertTrue(!b);
  }


  /**
   * Test isGroupValueWrite() with unexpected tpci/apci and apci/data values.
   */
  @Test public void testIsGroupValueWriteWrongBits()
  {
    boolean b = ApplicationLayer.Service.isGroupValueWrite(
        20, 0x00, 0x00
    );

    Assert.assertTrue(!b);
  }

  /**
   * Test isGroupValueWrite() with unexpected tpci/apci and apci/data values.
   */
  @Test public void testIsGroupValueWriteWrongBits2()
  {
    boolean b = ApplicationLayer.Service.isGroupValueWrite(
        200, 0x00, 0x40
    );

    Assert.assertTrue(!b);
  }


  /**
   * Test isGroupValueWrite() with unexpected tpci/apci and apci/data values.
   */
  @Test public void testIsGroupValueWriteWrongBits3()
  {
    boolean b = ApplicationLayer.Service.isGroupValueRead(
        100, 0x01, 0x01
    );

    Assert.assertTrue(!b);
  }

}

