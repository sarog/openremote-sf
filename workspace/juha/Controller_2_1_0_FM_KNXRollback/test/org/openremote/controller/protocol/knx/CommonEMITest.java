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

import org.junit.Assert;
import org.junit.Test;
import org.openremote.controller.utils.Strings;

/**
 * Unit tests for {@link org.openremote.controller.protocol.knx.CommonEMI} class.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class CommonEMITest
{

  /**
   * Basic constructor test for the ctor that takes a CEMI frame as a byte array.
   *
   * @throws Exception  if test fails
   */
  @Test public void testFrameConstructor() throws Exception
  {
    byte[] cemiFrame = new byte[]
    {
        DataLink.DATA_REQUEST.getByteValue(),
        0x00,  // additional info length
        0x00,  // Control field 1 bits
        0x00,  // Control field 2 bits
        0x00,  // Source address high byte
        0x00,  // Source address low byte
        0x00,  // Destination address high byte
        0x00,  // Destination address low byte
        0x01,  // APDU data length
        0x00,  // TPCI/APCI bits
        0x00   // APCI bits/six-bit data field
    };

    // would throw an exception if frame validation fails...

    CommonEMI cemi = new CommonEMI(cemiFrame);


    Assert.assertTrue(cemi.getContent() [0] == DataLink.DATA_REQUEST.getByteValue());
    Assert.assertTrue(cemi.getContent() [1] == 0x00);
    Assert.assertTrue(cemi.getContent() [2] == 0x00);
    Assert.assertTrue(cemi.getContent() [3] == 0x00);

    GroupAddress destination = cemi.getDestinationAddress();

    Assert.assertTrue(destination.equals(new GroupAddress("0/0/0")));
  }

  /**
   * Test frame byte array constructor with an invalid message code value...
   */
  @Test public void testFrameConstructorUnknownMessageCode()
  {
    byte[] cemiFrame = new byte[]
    {
        (byte)0xFF,
        0x00,  // additional info length
        0x00,  // Control field 1 bits
        0x00,  // Control field 2 bits
        0x00,  // Source address high byte
        0x00,  // Source address low byte
        0x00,  // Destination address high byte
        0x00,  // Destination address low byte
        0x01,  // APDU data length
        0x00,  // TPCI/APCI bits
        0x00   // APCI bits/six-bit data field
    };

    try
    {
      new CommonEMI(cemiFrame);

      Assert.fail("Should not get here...");
    }

    catch (CommonEMI.InvalidFrameException e)
    {
      // expected...
    }
  }

  /**
   * Test frame byte array constructor with an Data.req message code value...
   *
   * @throws Exception  if test fails
   */
  @Test public void testFrameConstructorMessageCode() throws Exception
  {
    byte[] cemiFrame = new byte[]
    {
        DataLink.DATA_REQUEST.getByteValue(),
        0x00,  // additional info length
        0x00,  // Control field 1 bits
        0x00,  // Control field 2 bits
        0x00,  // Source address high byte
        0x00,  // Source address low byte
        0x00,  // Destination address high byte
        0x00,  // Destination address low byte
        0x01,  // APDU data length
        0x00,  // TPCI/APCI bits
        0x00   // APCI bits/six-bit data field
    };

    CommonEMI cemi = new CommonEMI(cemiFrame);

    Assert.assertTrue(cemi.getContent() [0] == DataLink.DATA_REQUEST.getByteValue());
  }

  /**
   * Test frame byte array constructor with a Data.ind message code value...
   *
   * @throws Exception  if test fails
   */
  @Test public void testFrameConstructorMessageCode2() throws Exception
  {
    byte[] cemiFrame = new byte[]
    {
        DataLink.DATA_INDICATE.getByteValue(),
        0x00,  // additional info length
        0x00,  // Control field 1 bits
        0x00,  // Control field 2 bits
        0x00,  // Source address high byte
        0x00,  // Source address low byte
        0x00,  // Destination address high byte
        0x00,  // Destination address low byte
        0x01,  // APDU data length
        0x00,  // TPCI/APCI bits
        0x00   // APCI bits/six-bit data field
    };

    CommonEMI cemi = new CommonEMI(cemiFrame);

    Assert.assertTrue(cemi.getContent() [0] == DataLink.DATA_INDICATE.getByteValue());
  }


  /**
   * Test frame byte array constructor with additional info fields...
   *
   * @throws Exception  if test fails
   */
  @Test public void testFrameConstructorWithAdditionalInfoLength() throws Exception
  {
    byte[] cemiFrame = new byte[]
    {
        DataLink.DATA_REQUEST.getByteValue(),
        0x02,  // additional info length
        0x0A,  // additional info 1
        0x0B,  // additional info 2
        0x01,  // Control field 1 bits
        0x02,  // Control field 2 bits
        0x00,  // Source address high byte
        0x00,  // Source address low byte
        0x03,  // Destination address high byte
        0x0A,  // Destination address low byte
        0x01,  // APDU data length
        0x00,  // TPCI/APCI bits
        0x00   // APCI bits/six-bit data field
    };

    CommonEMI cemi = new CommonEMI(cemiFrame);

    Assert.assertTrue(cemi.getContent() [0] == DataLink.DATA_REQUEST.getByteValue());
    Assert.assertTrue(cemi.getContent() [1] == 0x02);
    Assert.assertTrue(cemi.getContent() [2] == 0x0A);
    Assert.assertTrue(cemi.getContent() [3] == 0x0B);
    Assert.assertTrue(cemi.getContent() [4] == 0x01);
    Assert.assertTrue(cemi.getContent() [5] == 0x02);

    GroupAddress destination = cemi.getDestinationAddress();

    Assert.assertTrue(destination.equals(new GroupAddress("0/3/10")));
  }

  /**
   * Basic test with the frame byte array constructor to parse the destination address...
   *
   * @throws Exception  if test fails
   */
  @Test public void testFrameConstructorDestinationAddress() throws Exception
  {
    byte[] cemiFrame = new byte[]
    {
        DataLink.DATA_REQUEST.getByteValue(),
        0x00,  // additional info length
        0x00,  // Control field 1 bits
        0x00,  // Control field 2 bits
        0x00,  // Source address high byte
        0x00,  // Source address low byte
        0x01,  // Destination address high byte
        0x01,  // Destination address low byte
        0x01,  // APDU data length
        0x00,  // TPCI/APCI bits
        0x00   // APCI bits/six-bit data field
    };

    CommonEMI cemi = new CommonEMI(cemiFrame);

    GroupAddress destination = cemi.getDestinationAddress();

    Assert.assertTrue(destination.equals(new GroupAddress("0/1/1")));
  }

  /**
   * Test with the frame byte array constructor to make sure Java's signed bytes are handled
   * correctly when parsing the destination address low byte...
   *
   * @throws Exception  if test fails
   */
  @Test public void testFrameConstructorDestinationAddress2() throws Exception
  {
    byte[] cemiFrame = new byte[]
    {
        DataLink.DATA_REQUEST.getByteValue(),
        0x00,  // additional info length
        0x00,  // Control field 1 bits
        0x00,  // Control field 2 bits
        0x00,  // Source address high byte
        0x00,  // Source address low byte
        0x01,  // Destination address high byte
        (byte)0xFF,  // Destination address low byte
        0x01,  // APDU data length
        0x00,  // TPCI/APCI bits
        0x00   // APCI bits/six-bit data field
    };

    CommonEMI cemi = new CommonEMI(cemiFrame);

    GroupAddress destination = cemi.getDestinationAddress();

    Assert.assertTrue(destination.equals(new GroupAddress("0/1/255")));
  }

  /**
   * Test with the frame byte array constructor to make sure Java's signed bytes are handled
   * correctly when parsing the destination address and when additional info bytes are
   * included in the frame...
   *
   * @throws Exception  if test fails
   */
  @Test public void testFrameConstructorDestinationAddressWithAdditionalInfo() throws Exception
  {
    byte[] cemiFrame = new byte[]
    {
        DataLink.DATA_REQUEST.getByteValue(),
        0x03,  // additional info length
        0x00,  // add info 1
        0x00,  // add info 2
        0x00,  // add info 3
        0x00,  // Control field 1 bits
        0x00,  // Control field 2 bits
        0x00,  // Source address high byte
        0x00,  // Source address low byte
        (byte)0xFF,  // Destination address high byte
        (byte)0xFF,  // Destination address low byte
        0x01,  // APDU data length
        0x00,  // TPCI/APCI bits
        0x00   // APCI bits/six-bit data field
    };

    CommonEMI cemi = new CommonEMI(cemiFrame);

    GroupAddress destination = cemi.getDestinationAddress();

    Assert.assertTrue(destination.equals(new GroupAddress("31/7/255")));
  }


  /**
   * Basic test with the frame byte array constructor to parse the source address...
   *
   * @throws Exception  if test fails
   */
  @Test public void testFrameConstructorSourceAddress() throws Exception
  {
    byte[] cemiFrame = new byte[]
    {
        DataLink.DATA_REQUEST.getByteValue(),
        0x00,  // additional info length
        0x00,  // Control field 1 bits
        0x00,  // Control field 2 bits
        0x05,  // Source address high byte
        0x05,  // Source address low byte
        0x01,  // Destination address high byte
        0x01,  // Destination address low byte
        0x01,  // APDU data length
        0x00,  // TPCI/APCI bits
        0x00   // APCI bits/six-bit data field
    };

    CommonEMI cemi = new CommonEMI(cemiFrame);

    GroupAddress source = cemi.getSourceAddress();

    Assert.assertTrue(source.equals(new GroupAddress("0/5/5")));
  }

  /**
   * Test with the frame byte array constructor to make sure Java's signed bytes are handled
   * correctly when parsing the source address low byte...
   *
   * @throws Exception  if test fails
   */
  @Test public void testFrameConstructorSourceAddress2() throws Exception
  {
    byte[] cemiFrame = new byte[]
    {
        DataLink.DATA_REQUEST.getByteValue(),
        0x00,  // additional info length
        0x00,  // Control field 1 bits
        0x00,  // Control field 2 bits
        0x05,  // Source address high byte
        (byte)0xFE,  // Source address low byte
        0x01,  // Destination address high byte
        0x10,  // Destination address low byte
        0x01,  // APDU data length
        0x00,  // TPCI/APCI bits
        0x00   // APCI bits/six-bit data field
    };

    CommonEMI cemi = new CommonEMI(cemiFrame);

    GroupAddress source = cemi.getSourceAddress();

    Assert.assertTrue(source.equals(new GroupAddress("0/5/254")));
  }

  /**
   * Test with the frame byte array constructor to make sure Java's signed bytes are handled
   * correctly when parsing the source address and when additional info bytes are
   * included in the frame...
   *
   * @throws Exception  if test fails
   */
  @Test public void testFrameConstructorSourceAddressWithAdditionalInfo() throws Exception
  {
    byte[] cemiFrame = new byte[]
    {
        DataLink.DATA_REQUEST.getByteValue(),
        0x01,  // additional info length
        0x00,  // add info 1
        0x00,  // Control field 1 bits
        0x00,  // Control field 2 bits
        (byte)0xFF,  // Source address high byte
        (byte)0xFD,  // Source address low byte
        0x00,  // Destination address high byte
        0x00,  // Destination address low byte
        0x01,  // APDU data length
        0x00,  // TPCI/APCI bits
        0x00   // APCI bits/six-bit data field
    };

    CommonEMI cemi = new CommonEMI(cemiFrame);

    GroupAddress source = cemi.getSourceAddress();

    Assert.assertTrue(source.equals(new GroupAddress("31/7/253")));
  }


  /**
   * Frame constructor test with default control field 1 bits.
   *
   * @throws Exception  if test fails
   */
  @Test public void testFrameConstructorWithDefaultControlField1Bits() throws Exception
  {
    CommonEMI.ControlBits control1 = new CommonEMI.ControlBits();
    control1.setStandardFrameType(true);
    control1.setFrameRepeat(true);
    control1.setSystemBroadcast(true);
    control1.setPriority(CommonEMI.Priority.NORMAL);
    control1.setAcknowledgeRequest(false);

    byte[] cemiFrame = new byte[]
    {
        DataLink.DATA_REQUEST.getByteValue(),
        0x00,  // additional info length
        control1.controlField1ToByteValue(),
        0x00,  // Control field 2 bits
        0x00,  // Source address high byte
        0x00,  // Source address low byte
        0x00,  // Destination address high byte
        0x00,  // Destination address low byte
        0x01,  // APDU data length
        0x00,  // TPCI/APCI bits
        0x00   // APCI bits/six-bit data field
    };

    // would throw an exception if frame validation fails...

    CommonEMI cemi = new CommonEMI(cemiFrame);

    int control1Value = control1.controlField1ToByteValue() & 0xFF;

    Assert.assertTrue(
        "Was expecting control field 1 bits 0x84, got " +
        Strings.byteToUnsignedHexString(control1.controlField1ToByteValue()),
        control1Value == 0x84
    );

    Assert.assertTrue((cemi.getContent() [2] & 0xFF) == 0x84);
  }

  /**
   * Frame constructor test with control field 1 bits.
   *
   * @throws Exception  if test fails
   */
  @Test public void testFrameConstructorWithControlField1Bits() throws Exception
  {
    CommonEMI.ControlBits control1 = new CommonEMI.ControlBits();
    control1.setExtendedFrameType(true);
    control1.setFrameRepeat(false);
    control1.setBroadcast(true);
    control1.setPriority(CommonEMI.Priority.SYSTEM);
    control1.setAcknowledgeRequest(true);

    byte[] cemiFrame = new byte[]
    {
        DataLink.DATA_REQUEST.getByteValue(),
        0x00,  // additional info length
        control1.controlField1ToByteValue(),
        0x00,  // Control field 2 bits
        0x00,  // Source address high byte
        0x00,  // Source address low byte
        0x00,  // Destination address high byte
        0x00,  // Destination address low byte
        0x01,  // APDU data length
        0x00,  // TPCI/APCI bits
        0x00   // APCI bits/six-bit data field
    };

    // would throw an exception if frame validation fails...

    CommonEMI cemi = new CommonEMI(cemiFrame);

    int control1Value = control1.controlField1ToByteValue() & 0xFF;

    Assert.assertTrue(
        "Was expecting control field 1 bits 0x32, got " +
        Strings.byteToUnsignedHexString(control1.controlField1ToByteValue()),
        control1Value == 0x32
    );

    Assert.assertTrue((cemi.getContent() [2] & 0xFF) == 0x32);
  }


  /**
   * Frame constructor test with default flags for control field 2
   *
   * @throws Exception    if test fails
   */
  @Test public void testFrameConstructorWithDefaultControlField2Bits() throws Exception
  {
    CommonEMI.ControlBits control = new CommonEMI.ControlBits();
    control.setExtendedFrameType(true);
    control.setFrameRepeat(false);
    control.setBroadcast(true);
    control.setPriority(CommonEMI.Priority.SYSTEM);
    control.setAcknowledgeRequest(true);
    control.setHopCount(6);


    byte[] cemiFrame = new byte[]
    {
        DataLink.DATA_REQUEST.getByteValue(),
        0x00,  // additional info length
        control.controlField1ToByteValue(),
        control.controlField2ToByteValue(),
        0x00,  // Source address high byte
        0x00,  // Source address low byte
        0x01,  // Destination address high byte
        0x01,  // Destination address low byte
        0x01,  // APDU data length
        0x00,  // TPCI/APCI bits
        0x00   // APCI bits/six-bit data field
    };

    CommonEMI cemi = new CommonEMI(cemiFrame);

    int control1Value = control.controlField1ToByteValue() & 0xFF;
    int control2Value = control.controlField2ToByteValue() & 0xFF;

    Assert.assertTrue(
        "Was expecting control field 1 bits 0x32, got " +
        Strings.byteToUnsignedHexString(control.controlField1ToByteValue()),
        control1Value == 0x32
    );

    Assert.assertTrue((cemi.getContent() [2] & 0xFF) == 0x32);


    Assert.assertTrue(
        "Was expecting control field 2 bits 0xE0, got " +
        Strings.byteToUnsignedHexString(control.controlField2ToByteValue()),
        control2Value == 0xE0
    );

    Assert.assertTrue((cemi.getContent() [3] & 0xFF) == 0xE0);
  }


  /**
   * Frame constructor test with control field 2 flags.
   *
   * @throws Exception    if test fails
   */
  @Test public void testFrameConstructorControlField2Bits() throws Exception
  {
    CommonEMI.ControlBits control = new CommonEMI.ControlBits();
    control.setExtendedFrameType(true);
    control.setFrameRepeat(false);
    control.setBroadcast(true);
    control.setPriority(CommonEMI.Priority.SYSTEM);
    control.setAcknowledgeRequest(true);
    control.setHopCount(1);
    control.useGroupAddressForDestination(false);


    byte[] cemiFrame = new byte[]
    {
        DataLink.DATA_REQUEST.getByteValue(),
        0x00,  // additional info length
        control.controlField1ToByteValue(),
        control.controlField2ToByteValue(),
        0x00,  // Source address high byte
        0x00,  // Source address low byte
        0x01,  // Destination address high byte
        0x01,  // Destination address low byte
        0x01,  // APDU data length
        0x00,  // TPCI/APCI bits
        0x00   // APCI bits/six-bit data field
    };

    CommonEMI cemi = new CommonEMI(cemiFrame);

    int control1Value = control.controlField1ToByteValue() & 0xFF;
    int control2Value = control.controlField2ToByteValue() & 0xFF;

    Assert.assertTrue(
        "Was expecting control field 1 bits 0x32, got " +
        Strings.byteToUnsignedHexString(control.controlField1ToByteValue()),
        control1Value == 0x32
    );

    Assert.assertTrue((cemi.getContent() [2] & 0xFF) == 0x32);


    Assert.assertTrue(
        "Was expecting control field 2 bits 0x10, got " +
        Strings.byteToUnsignedHexString(control.controlField2ToByteValue()),
        control2Value == 0x10
    );

    Assert.assertTrue((cemi.getContent() [3] & 0xFF) == 0x10);
  }


  /**
   * Test the convenience constructor with message code, destination group address and
   * KNX string APDU.
   *
   * @throws Exception    if test fails
   */
  @Test public void testConstructor() throws Exception
  {
    CommonEMI cemi = new CommonEMI(
        DataLink.DATA_INDICATE,
        new GroupAddress("1/1/1"),
        ApplicationProtocolDataUnit.createKNXASCIIStringResponse("foo")
    );

    GroupAddress destination = cemi.getDestinationAddress();

    Assert.assertTrue(destination.equals(new GroupAddress("1/1/1")));

    int control1 = cemi.getContent() [2] & 0xFF;
    int control2 = cemi.getContent() [3] & 0xFF;

    CommonEMI.ControlBits bits = new CommonEMI.ControlBits(control1, control2);

    Assert.assertTrue((bits.controlField1ToByteValue() & 0xFF) == control1);
    Assert.assertTrue((bits.controlField2ToByteValue() & 0xFF) == control2);

    Assert.assertTrue(bits.isStandardFrameType());
    Assert.assertTrue(bits.hasFrameRepeatEnabled());
    Assert.assertTrue(bits.isSystemBroadcast());
    Assert.assertTrue("Got priority: " + bits.getPriority(), bits.isNormalPriority());
    Assert.assertTrue(!bits.hasAcknowledgeEnabled());

    Assert.assertTrue(bits.useGroupAddressForDestination());
    Assert.assertTrue(bits.getHopCount() == 6);

    int apduLen = cemi.getContent() [8] & 0xFF;

    Assert.assertTrue("Expected apdu len 15, got " + apduLen, apduLen == 15);

    Assert.assertTrue(cemi.getContent() [9] == ApplicationLayer.Service.GROUPVALUE_RESPONSE.getTPCIAPCI());
    Assert.assertTrue(cemi.getContent() [10] == ApplicationLayer.Service.GROUPVALUE_RESPONSE.getAPCIData());

    Assert.assertTrue(cemi.getContent() [11] == 'f');
    Assert.assertTrue(cemi.getContent() [12] == 'o');
    Assert.assertTrue(cemi.getContent() [13] == 'o');
    Assert.assertTrue(cemi.getContent() [14] == 0x00);
    Assert.assertTrue(cemi.getContent() [15] == 0x00);
    Assert.assertTrue(cemi.getContent() [16] == 0x00);
    Assert.assertTrue(cemi.getContent() [17] == 0x00);
    Assert.assertTrue(cemi.getContent() [18] == 0x00);
    Assert.assertTrue(cemi.getContent() [19] == 0x00);
    Assert.assertTrue(cemi.getContent() [20] == 0x00);
    Assert.assertTrue(cemi.getContent() [21] == 0x00);
    Assert.assertTrue(cemi.getContent() [22] == 0x00);
    Assert.assertTrue(cemi.getContent() [23] == 0x00);
    Assert.assertTrue(cemi.getContent() [24] == 0x00);
  }


  /**
   * Basic test for the CommonEMI.ControlBits dfeault constructor
   *
   * @throws Exception    if test fails
   */
  @Test public void testControlBitsConstructor() throws Exception
  {
    new CommonEMI.ControlBits(0xFF, 0xFF);
  }

  /**
   * Test hop counts in control bits implementation.
   */
  @Test public void testControlBitsHopCount()
  {
    CommonEMI.ControlBits ctrl = new CommonEMI.ControlBits();

    ctrl.setHopCount(0);

    Assert.assertTrue(ctrl.getHopCount() == 0);

    int hopValue = ctrl.controlField2ToByteValue();
    hopValue &= CommonEMI.ControlBits.CEMI_CONTROL_FIELD2_HOPCOUNT_BITMASK;
    hopValue = hopValue >> CommonEMI.ControlBits.CEMI_CONTROL_FIELD2_HOPCOUNT_BIT_POSITION;

    Assert.assertTrue(hopValue == 0);


    ctrl.setHopCount(3);

    Assert.assertTrue(ctrl.getHopCount() == 3);

    hopValue = ctrl.controlField2ToByteValue();
    hopValue &= CommonEMI.ControlBits.CEMI_CONTROL_FIELD2_HOPCOUNT_BITMASK;
    hopValue = hopValue >> CommonEMI.ControlBits.CEMI_CONTROL_FIELD2_HOPCOUNT_BIT_POSITION;

    Assert.assertTrue(hopValue == 3);

    ctrl.setHopCount(7);

    Assert.assertTrue(ctrl.getHopCount() == 7);

    hopValue = ctrl.controlField2ToByteValue();
    hopValue &= CommonEMI.ControlBits.CEMI_CONTROL_FIELD2_HOPCOUNT_BITMASK;
    hopValue = hopValue >> CommonEMI.ControlBits.CEMI_CONTROL_FIELD2_HOPCOUNT_BIT_POSITION;

    Assert.assertTrue(hopValue == 7);
  }


  /**
   * Test for illegal values in setHopCount() call.
   */
  @Test public void testControlBitsHopCountInvalidValue()
  {
    CommonEMI.ControlBits ctrl = new CommonEMI.ControlBits();

    try
    {
      ctrl.setHopCount(8);

      Assert.fail("Should not get here...");
    }

    catch (IllegalArgumentException e)
    {
      // expected...
    }

    try
    {
      ctrl.setHopCount(-1);

      Assert.fail("Should not get here...");
    }

    catch (IllegalArgumentException e)
    {
      // expected...
    }
  }


  /**
   * Test control bit constructor for standard/extended frame flag.
   *
   * @throws Exception      if test fails
   */
  @Test public void testControlBitsStandardAndExtendedFrameFlag() throws Exception
  {
    CommonEMI.ControlBits ctrl = new CommonEMI.ControlBits(1<<7, 0);

    Assert.assertTrue(ctrl.isStandardFrameType());
    Assert.assertTrue(!ctrl.isExtendedFrameType());

    int framebyte = ctrl.controlField1ToByteValue() & 0xFF;

    Assert.assertTrue("Expected 0x80, got " + Strings.byteToUnsignedHexString((byte)framebyte), framebyte == 0x80);

    ctrl = new CommonEMI.ControlBits(0, 0);

    Assert.assertTrue(!ctrl.isStandardFrameType());
    Assert.assertTrue(ctrl.isExtendedFrameType());

    framebyte = ctrl.controlField1ToByteValue() & 0xFF;

    Assert.assertTrue("Expected 0x00, got " + Strings.byteToUnsignedHexString((byte)framebyte), framebyte == 0x00);
  }

  /**
   * Test setStandardFrameType() and setExtendedFrameType() implementations in ControlBits.
   *
   * @throws Exception        if test fails
   */
  @Test public void testControlBitsFrameType() throws Exception
  {
    CommonEMI.ControlBits ctrl = new CommonEMI.ControlBits(0, 0);

    ctrl.setStandardFrameType(true);

    Assert.assertTrue(ctrl.isStandardFrameType());
    Assert.assertTrue(!ctrl.isExtendedFrameType());

    int framebyte = ctrl.controlField1ToByteValue() & 0xFF;

    Assert.assertTrue("Expected 0x80, got " + Strings.byteToUnsignedHexString((byte)framebyte), framebyte == 0x80);


    ctrl.setStandardFrameType(false);

    Assert.assertTrue(!ctrl.isStandardFrameType());
    Assert.assertTrue(ctrl.isExtendedFrameType());

    framebyte = ctrl.controlField1ToByteValue() & 0xFF;

    Assert.assertTrue("Expected 0x00, got " + Strings.byteToUnsignedHexString((byte)framebyte), framebyte == 0x00);


    ctrl.setExtendedFrameType(false);

    Assert.assertTrue(ctrl.isStandardFrameType());
    Assert.assertTrue(!ctrl.isExtendedFrameType());

    framebyte = ctrl.controlField1ToByteValue() & 0xFF;

    Assert.assertTrue("Expected 0x80, got " + Strings.byteToUnsignedHexString((byte)framebyte), framebyte == 0x80);


    ctrl.setExtendedFrameType(true);

    Assert.assertTrue(!ctrl.isStandardFrameType());
    Assert.assertTrue(ctrl.isExtendedFrameType());

    framebyte = ctrl.controlField1ToByteValue() & 0xFF;

    Assert.assertTrue("Expected 0x00, got " + Strings.byteToUnsignedHexString((byte)framebyte), framebyte == 0x00);
  }


  /**
   * Test ControlBits constructor with frame repeat flag.
   *
   * @throws Exception        if test fails
   */
  @Test public void testControlBitsFrameRepeatFlag() throws Exception
  {
    CommonEMI.ControlBits ctrl = new CommonEMI.ControlBits(1<<5, 0);

    Assert.assertTrue(!ctrl.hasFrameRepeatEnabled());

    int framebyte = ctrl.controlField1ToByteValue() & 0xFF;

    Assert.assertTrue("Expected 0x20, got " + Strings.byteToUnsignedHexString((byte)framebyte), framebyte == 0x20);


    ctrl = new CommonEMI.ControlBits(0, 0);

    Assert.assertTrue(ctrl.hasFrameRepeatEnabled());

    framebyte = ctrl.controlField1ToByteValue() & 0xFF;

    Assert.assertTrue("Expected 0x00, got " + Strings.byteToUnsignedHexString((byte)framebyte), framebyte == 0x00);
  }

  /**
   * Test setFrameRepeat() implementation in ControlBits class.
   *
   * @throws Exception      if test fails
   */
  @Test public void testControlBitsFrameRepeat() throws Exception
  {
    CommonEMI.ControlBits ctrl = new CommonEMI.ControlBits(0, 0);

    Assert.assertTrue(ctrl.hasFrameRepeatEnabled());

    ctrl.setFrameRepeat(false);

    Assert.assertTrue(!ctrl.hasFrameRepeatEnabled());

    int framebyte = ctrl.controlField1ToByteValue() & 0xFF;

    Assert.assertTrue("Expected 0x20, got " + Strings.byteToUnsignedHexString((byte)framebyte), framebyte == 0x20);

    ctrl.setFrameRepeat(true);

    framebyte = ctrl.controlField1ToByteValue() & 0xFF;

    Assert.assertTrue("Expected 0x00, got " + Strings.byteToUnsignedHexString((byte)framebyte), framebyte == 0x00);
  }


  /**
   * Test ControlBits consturctor with system broadcast and broadcast flags.
   *
   * @throws Exception    if test fails
   */
  @Test public void testControlSystemBroadcastAndBroadcastFlags() throws Exception
  {
    CommonEMI.ControlBits ctrl = new CommonEMI.ControlBits(1<<4, 0);

    Assert.assertTrue(ctrl.isBroadcast());
    Assert.assertTrue(!ctrl.isSystemBroadcast());

    int framebyte = ctrl.controlField1ToByteValue() & 0xFF;

    Assert.assertTrue("Expected 0x10, got " + Strings.byteToUnsignedHexString((byte)framebyte), framebyte == 0x10);


    ctrl = new CommonEMI.ControlBits(0, 0);

    Assert.assertTrue(!ctrl.isBroadcast());
    Assert.assertTrue(ctrl.isSystemBroadcast());

    framebyte = ctrl.controlField1ToByteValue() & 0xFF;

    Assert.assertTrue("Expected 0x00, got " + Strings.byteToUnsignedHexString((byte)framebyte), framebyte == 0x00);
  }

  /**
   * Test setSystemBroadcast() and setBroadcast() implementations in ControlBits.
   *
   * @throws Exception        if test fails
   */
  @Test public void testControlBitsBroadcast() throws Exception
  {
    CommonEMI.ControlBits ctrl = new CommonEMI.ControlBits(0, 0);

    Assert.assertTrue(ctrl.isSystemBroadcast());
    Assert.assertTrue(!ctrl.isBroadcast());

    ctrl.setBroadcast(true);

    Assert.assertTrue(!ctrl.isSystemBroadcast());
    Assert.assertTrue(ctrl.isBroadcast());

    int framebyte = ctrl.controlField1ToByteValue() & 0xFF;

    Assert.assertTrue("Expected 0x10, got " + Strings.byteToUnsignedHexString((byte)framebyte), framebyte == 0x10);


    ctrl.setBroadcast(false);

    Assert.assertTrue(ctrl.isSystemBroadcast());
    Assert.assertTrue(!ctrl.isBroadcast());

    framebyte = ctrl.controlField1ToByteValue() & 0xFF;

    Assert.assertTrue("Expected 0x00, got " + Strings.byteToUnsignedHexString((byte)framebyte), framebyte == 0x00);


    ctrl.setSystemBroadcast(false);

    Assert.assertTrue(!ctrl.isSystemBroadcast());
    Assert.assertTrue(ctrl.isBroadcast());

    framebyte = ctrl.controlField1ToByteValue() & 0xFF;

    Assert.assertTrue("Expected 0x10, got " + Strings.byteToUnsignedHexString((byte)framebyte), framebyte == 0x10);


    ctrl.setSystemBroadcast(true);

    Assert.assertTrue(ctrl.isSystemBroadcast());
    Assert.assertTrue(!ctrl.isBroadcast());

    framebyte = ctrl.controlField1ToByteValue() & 0xFF;

    Assert.assertTrue("Expected 0x00, got " + Strings.byteToUnsignedHexString((byte)framebyte), framebyte == 0x00);
  }


  /**
   * Test setPriority() implementation in ControlBits.
   *
   * @throws Exception        if test fails
   */
  @Test public void testControlBitsPriority() throws Exception
  {
    CommonEMI.ControlBits ctrl = new CommonEMI.ControlBits(0, 0);

    ctrl.setPriority(CommonEMI.Priority.NORMAL);

    Assert.assertTrue(ctrl.getPriority() == CommonEMI.Priority.NORMAL);
    Assert.assertTrue(ctrl.isNormalPriority());

    ctrl.setPriority(CommonEMI.Priority.LOW);

    Assert.assertTrue(ctrl.getPriority() == CommonEMI.Priority.LOW);
    Assert.assertTrue(ctrl.isLowPriority());

    ctrl.setPriority(CommonEMI.Priority.SYSTEM);

    Assert.assertTrue(ctrl.getPriority() == CommonEMI.Priority.SYSTEM);
    Assert.assertTrue(ctrl.isSystemPriority());

    ctrl.setPriority(CommonEMI.Priority.URGENT);

    Assert.assertTrue(ctrl.getPriority() == CommonEMI.Priority.URGENT);
    Assert.assertTrue(ctrl.isUrgentPriority());
  }


  /**
   * Test ControlBits constructor with priority values.
   *
   * @throws Exception      if test fails
   */
  @Test public void testControlPriority() throws Exception
  {
    CommonEMI.ControlBits ctrl = new CommonEMI.ControlBits(1<<2, 0);

    Assert.assertTrue(ctrl.getPriority() == CommonEMI.Priority.NORMAL);
    Assert.assertTrue(ctrl.isNormalPriority());
    Assert.assertTrue(!ctrl.isSystemPriority());
    Assert.assertTrue(!ctrl.isLowPriority());
    Assert.assertTrue(!ctrl.isUrgentPriority());

    int framebyte = ctrl.controlField1ToByteValue() & 0xFF;

    Assert.assertTrue("Expected 0x04, got " + Strings.byteToUnsignedHexString((byte)framebyte), framebyte == 0x04);


    ctrl = new CommonEMI.ControlBits(0, 0);

    Assert.assertTrue(ctrl.getPriority() == CommonEMI.Priority.SYSTEM);
    Assert.assertTrue(ctrl.isSystemPriority());
    Assert.assertTrue(!ctrl.isNormalPriority());
    Assert.assertTrue(!ctrl.isUrgentPriority());
    Assert.assertTrue(!ctrl.isLowPriority());

    framebyte = ctrl.controlField1ToByteValue() & 0xFF;

    Assert.assertTrue("Expected 0x00, got " + Strings.byteToUnsignedHexString((byte)framebyte), framebyte == 0x00);


    ctrl = new CommonEMI.ControlBits(1<<3, 0);

    Assert.assertTrue(ctrl.getPriority() == CommonEMI.Priority.URGENT);
    Assert.assertTrue(!ctrl.isSystemPriority());
    Assert.assertTrue(!ctrl.isNormalPriority());
    Assert.assertTrue(ctrl.isUrgentPriority());
    Assert.assertTrue(!ctrl.isLowPriority());

    framebyte = ctrl.controlField1ToByteValue() & 0xFF;

    Assert.assertTrue("Expected 0x08, got " + Strings.byteToUnsignedHexString((byte)framebyte), framebyte == 0x08);


    ctrl = new CommonEMI.ControlBits(3<<2, 0);

    Assert.assertTrue(ctrl.getPriority() == CommonEMI.Priority.LOW);
    Assert.assertTrue(!ctrl.isSystemPriority());
    Assert.assertTrue(!ctrl.isNormalPriority());
    Assert.assertTrue(!ctrl.isUrgentPriority());
    Assert.assertTrue(ctrl.isLowPriority());

    framebyte = ctrl.controlField1ToByteValue() & 0xFF;

    Assert.assertTrue("Expected 0x0C, got " + Strings.byteToUnsignedHexString((byte)framebyte), framebyte == 0x0C);
  }


  /**
   * Test ControlBits constructor with datalink layer ack flag
   *
   * @throws Exception        if test fails
   */
  @Test public void testControlBitsAcknowledgeFlag() throws Exception
  {
    CommonEMI.ControlBits ctrl = new CommonEMI.ControlBits(1<<1, 0);

    Assert.assertTrue(ctrl.hasAcknowledgeEnabled());

    int framebyte = ctrl.controlField1ToByteValue() & 0xFF;

    Assert.assertTrue("Expected 0x02, got " + Strings.byteToUnsignedHexString((byte)framebyte), framebyte == 0x02);


    ctrl = new CommonEMI.ControlBits(0, 0);

    Assert.assertTrue(!ctrl.hasAcknowledgeEnabled());

    framebyte = ctrl.controlField1ToByteValue() & 0xFF;

    Assert.assertTrue("Expected 0x00, got " + Strings.byteToUnsignedHexString((byte)framebyte), framebyte == 0x00);
  }

  /**
   * Tests setAcknowledgeRequest() implementation in ControlBits.
   *
   * @throws Exception      if test fails
   */
  @Test public void testControlBitsAck() throws Exception
  {
    CommonEMI.ControlBits ctrl = new CommonEMI.ControlBits(0, 0);

    Assert.assertTrue(!ctrl.hasAcknowledgeEnabled());

    ctrl.setAcknowledgeRequest(true);

    Assert.assertTrue(ctrl.hasAcknowledgeEnabled());

    int framebyte = ctrl.controlField1ToByteValue() & 0xFF;

    Assert.assertTrue("Expected 0x02, got " + Strings.byteToUnsignedHexString((byte)framebyte), framebyte == 0x02);


    ctrl.setAcknowledgeRequest(false);

    framebyte = ctrl.controlField1ToByteValue() & 0xFF;

    Assert.assertTrue("Expected 0x00, got " + Strings.byteToUnsignedHexString((byte)framebyte), framebyte == 0x00);
  }

  /**
   * Tests useGroupAddressForDestination() implementation in ControlBits.
   *
   * @throws Exception        if test fails
   */
  @Test public void testControlBitsUseGroupAddress() throws Exception
  {
    CommonEMI.ControlBits ctrl = new CommonEMI.ControlBits(0, 0);

    Assert.assertTrue(!ctrl.useGroupAddressForDestination());

    ctrl.useGroupAddressForDestination(true);

    Assert.assertTrue(ctrl.useGroupAddressForDestination());
  }

}

