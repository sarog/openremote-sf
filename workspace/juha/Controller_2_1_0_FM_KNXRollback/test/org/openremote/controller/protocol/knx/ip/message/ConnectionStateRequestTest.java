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
package org.openremote.controller.protocol.knx.ip.message;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;

import junit.framework.Assert;
import org.junit.Test;
import org.openremote.controller.protocol.knx.ServiceTypeIdentifier;
import org.openremote.controller.utils.Strings;

/**
 * Unit tests for {@link org.openremote.controller.protocol.knx.ip.message.IpConnectionStateReq}
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class ConnectionStateRequestTest
{

  // Constructor Tests ----------------------------------------------------------------------------

  /**
   * Basic typed constructor test.
   */
  @Test public void testConstructor()
  {
    new IpConnectionStateReq(1, new Hpai(new InetSocketAddress("0.0.0.0", 0)));
  }

  /**
   * Basic typed constructor test with channel identifier value below minimum range.
   */
  @Test public void testConstructorInvalidChannel()
  {
    try
    {
      new IpConnectionStateReq(-1, new Hpai(new InetSocketAddress("0.0.0.0", 0)));

      Assert.fail("should not get here...");
    }

    catch (IllegalArgumentException e)
    {
      // expected...
    }
  }

  /**
   * Basc typed constructor test with channel identifier value above maximum range.
   */
  @Test public void testConstructorInvalidChannel2()
  {
    try
    {
      new IpConnectionStateReq(256, new Hpai(new InetSocketAddress("0.0.0.0", 0)));

      Assert.fail("should not get here...");
    }

    catch (IllegalArgumentException e)
    {
      // expected...
    }
  }


  /**
   * Basic typed constructor test with null arg for HPAI.
   */
  @Test public void testConstructorNullHPAI()
  {
    try
    {
      new IpConnectionStateReq(255, null);

      Assert.fail("should not get here...");
    }

    catch (IllegalArgumentException e)
    {
      // expected...
    }
  }


  // Frame Constructor Tests ----------------------------------------------------------------------

  /**
   * Basic test for KNX frame constructor.
   *
   * @throws Exception    if test fails
   */
  @Test public void testFrameConstructor() throws Exception
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
        0x00,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
        0x01,     // channel ID
        0x00,     // reserved
        (byte)Hpai.getStructureSize(),
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        100
    };

    IpConnectionStateReq stateRequest = new IpConnectionStateReq(frame);

    Assert.assertTrue(stateRequest.getChannelId() == 1);
    Assert.assertTrue(stateRequest.getPrimitive() == IpMessage.Primitive.REQ);

    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    stateRequest.write(bout);

    byte[] writebackFrame = bout.toByteArray();


    Assert.assertTrue((writebackFrame [0] & 0xFF) == IpMessage.KNXNET_IP_10_HEADER_SIZE);
    Assert.assertTrue((writebackFrame [1] & 0xFF) == IpMessage.KNXNET_IP_10_VERSION);
    Assert.assertTrue((writebackFrame [2] & 0xFF) == 0x02);
    Assert.assertTrue((writebackFrame [3] & 0xFF) == 0x07);
    Assert.assertTrue((writebackFrame [4] & 0xFF) == 0x00);
    Assert.assertTrue((writebackFrame [5] & 0xFF) == IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH);
    Assert.assertTrue((writebackFrame [6] & 0xFF) == 0x01);
    Assert.assertTrue((writebackFrame [7] & 0xFF) == 0x00);
    Assert.assertTrue((writebackFrame [8] & 0xFF) == Hpai.getStructureSize());
    Assert.assertTrue((writebackFrame [9] & 0xFF) == Hpai.HostProtocolCode.IPV4_UDP.getValue());
    Assert.assertTrue((writebackFrame [10] & 0xFF) == 127);
    Assert.assertTrue((writebackFrame [11] & 0xFF) == 0);
    Assert.assertTrue((writebackFrame [12] & 0xFF) == 0);
    Assert.assertTrue((writebackFrame [13] & 0xFF) == 1);
    Assert.assertTrue((writebackFrame [14] & 0xFF) == 0);
    Assert.assertTrue((writebackFrame [15] & 0xFF) == 100);
  }

  /**
   * Basic test for KNX frame constructor with max channel value (signed vs. unsigned value
   * handling).
   *
   * @throws Exception    if test fails
   */
  @Test public void testFrameConstructorMaxChannel() throws Exception
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
        0x00,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
        (byte)0xFF,     // channel ID
        0x00,           // reserved
        (byte)Hpai.getStructureSize(),
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        (byte)128
    };

    IpConnectionStateReq stateRequest = new IpConnectionStateReq(frame);

    Assert.assertTrue(
        "Got " + Strings.byteToUnsignedHexString((byte)stateRequest.getChannelId()) + ", expected 0xFF",
        stateRequest.getChannelId() == 0xFF
    );

    Assert.assertTrue(stateRequest.getPrimitive() == IpMessage.Primitive.REQ);


    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    stateRequest.write(bout);

    byte[] writebackFrame = bout.toByteArray();


    Assert.assertTrue((writebackFrame [0] & 0xFF) == IpMessage.KNXNET_IP_10_HEADER_SIZE);
    Assert.assertTrue((writebackFrame [1] & 0xFF) == IpMessage.KNXNET_IP_10_VERSION);
    Assert.assertTrue((writebackFrame [2] & 0xFF) == 0x02);
    Assert.assertTrue((writebackFrame [3] & 0xFF) == 0x07);
    Assert.assertTrue((writebackFrame [4] & 0xFF) == 0x00);
    Assert.assertTrue((writebackFrame [5] & 0xFF) == IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH);
    Assert.assertTrue((writebackFrame [6] & 0xFF) == 0xFF);
    Assert.assertTrue((writebackFrame [7] & 0xFF) == 0x00);
    Assert.assertTrue((writebackFrame [8] & 0xFF) == Hpai.getStructureSize());
    Assert.assertTrue((writebackFrame [9] & 0xFF) == Hpai.HostProtocolCode.IPV4_UDP.getValue());
    Assert.assertTrue((writebackFrame [10] & 0xFF) == 127);
    Assert.assertTrue((writebackFrame [11] & 0xFF) == 0);
    Assert.assertTrue((writebackFrame [12] & 0xFF) == 0);
    Assert.assertTrue((writebackFrame [13] & 0xFF) == 1);
    Assert.assertTrue((writebackFrame [14] & 0xFF) == 0);
    Assert.assertTrue((writebackFrame [15] & 0xFF) == 128);
  }

  /**
   * Basic test for KNX frame constructor with max values for channel, address and port (signed vs.
   * unsigned value handling) and max value for reserved byte (should be ignored).
   *
   * @throws Exception    if test fails
   */
  @Test public void testFrameConstructorMaxValues() throws Exception
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
        0x00,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
        (byte)0xFF,     // channel ID
        (byte)0xFF,           // reserved
        (byte)Hpai.getStructureSize(),
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        (byte)255,
        (byte)255,
        (byte)255,
        (byte)255,
        (byte)0xFF,
        (byte)0xFF
    };

    IpConnectionStateReq stateRequest = new IpConnectionStateReq(frame);

    Assert.assertTrue(
        "Got " + Strings.byteToUnsignedHexString((byte)stateRequest.getChannelId()) + ", expected 0xFF",
        stateRequest.getChannelId() == 0xFF
    );

    Assert.assertTrue(stateRequest.getPrimitive() == IpMessage.Primitive.REQ);


    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    stateRequest.write(bout);

    byte[] writebackFrame = bout.toByteArray();


    Assert.assertTrue((writebackFrame [0] & 0xFF) == IpMessage.KNXNET_IP_10_HEADER_SIZE);
    Assert.assertTrue((writebackFrame [1] & 0xFF) == IpMessage.KNXNET_IP_10_VERSION);
    Assert.assertTrue((writebackFrame [2] & 0xFF) == 0x02);
    Assert.assertTrue((writebackFrame [3] & 0xFF) == 0x07);
    Assert.assertTrue((writebackFrame [4] & 0xFF) == 0x00);
    Assert.assertTrue((writebackFrame [5] & 0xFF) == IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH);
    Assert.assertTrue((writebackFrame [6] & 0xFF) == 0xFF);   // channel
    Assert.assertTrue((writebackFrame [7] & 0xFF) == 0x00);   // reserved
    Assert.assertTrue((writebackFrame [8] & 0xFF) == Hpai.getStructureSize());
    Assert.assertTrue((writebackFrame [9] & 0xFF) == Hpai.HostProtocolCode.IPV4_UDP.getValue());
    Assert.assertTrue((writebackFrame [10] & 0xFF) == 255);
    Assert.assertTrue((writebackFrame [11] & 0xFF) == 255);
    Assert.assertTrue((writebackFrame [12] & 0xFF) == 255);
    Assert.assertTrue((writebackFrame [13] & 0xFF) == 255);
    Assert.assertTrue((writebackFrame [14] & 0xFF) == 0xFF);
    Assert.assertTrue((writebackFrame [15] & 0xFF) == 0xFF);
  }


  /**
   * Basic test for KNX frame constructor with non-null reserved byte value (which should
   * get ignored).
   *
   * @throws Exception    if test fails
   */
  @Test public void testFrameConstructorIgnoreReservedByte() throws Exception
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
        0x00,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
        (byte)128,      // channel ID
        0x01,           // reserved
        (byte)Hpai.getStructureSize(),
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        (byte)129
    };

    IpConnectionStateReq stateRequest = new IpConnectionStateReq(frame);

    Assert.assertTrue(stateRequest.getChannelId() == 128);

    Assert.assertTrue(stateRequest.getPrimitive() == IpMessage.Primitive.REQ);


    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    stateRequest.write(bout);

    byte[] writebackFrame = bout.toByteArray();


    Assert.assertTrue((writebackFrame [0] & 0xFF) == IpMessage.KNXNET_IP_10_HEADER_SIZE);
    Assert.assertTrue((writebackFrame [1] & 0xFF) == IpMessage.KNXNET_IP_10_VERSION);
    Assert.assertTrue((writebackFrame [2] & 0xFF) == 0x02);
    Assert.assertTrue((writebackFrame [3] & 0xFF) == 0x07);
    Assert.assertTrue((writebackFrame [4] & 0xFF) == 0x00);
    Assert.assertTrue((writebackFrame [5] & 0xFF) == IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH);
    Assert.assertTrue((writebackFrame [6] & 0xFF) == 128);
    Assert.assertTrue((writebackFrame [7] & 0xFF) == 0x00);
    Assert.assertTrue((writebackFrame [8] & 0xFF) == Hpai.getStructureSize());
    Assert.assertTrue((writebackFrame [9] & 0xFF) == Hpai.HostProtocolCode.IPV4_UDP.getValue());
    Assert.assertTrue((writebackFrame [10] & 0xFF) == 127);
    Assert.assertTrue((writebackFrame [11] & 0xFF) == 0);
    Assert.assertTrue((writebackFrame [12] & 0xFF) == 0);
    Assert.assertTrue((writebackFrame [13] & 0xFF) == 1);
    Assert.assertTrue((writebackFrame [14] & 0xFF) == 0);
    Assert.assertTrue((writebackFrame [15] & 0xFF) == 129);
  }

  /**
   * Basic test for KNX frame constructor with max port value (signed vs unsigned value handling).
   *
   * @throws Exception    if test fails
   */
  @Test public void testFrameConstructorPortMax() throws Exception
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
        0x00,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
        0x01,     // channel ID
        0x00,     // reserved
        (byte)Hpai.getStructureSize(),
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        (byte)0xFF,
        (byte)0xFF
    };

    IpConnectionStateReq stateRequest = new IpConnectionStateReq(frame);

    Assert.assertTrue(stateRequest.getChannelId() == 1);
    Assert.assertTrue(stateRequest.getPrimitive() == IpMessage.Primitive.REQ);


    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    stateRequest.write(bout);

    byte[] writebackFrame = bout.toByteArray();


    Assert.assertTrue((writebackFrame [0] & 0xFF) == IpMessage.KNXNET_IP_10_HEADER_SIZE);
    Assert.assertTrue((writebackFrame [1] & 0xFF) == IpMessage.KNXNET_IP_10_VERSION);
    Assert.assertTrue((writebackFrame [2] & 0xFF) == 0x02);
    Assert.assertTrue((writebackFrame [3] & 0xFF) == 0x07);
    Assert.assertTrue((writebackFrame [4] & 0xFF) == 0x00);
    Assert.assertTrue((writebackFrame [5] & 0xFF) == IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH);
    Assert.assertTrue((writebackFrame [6] & 0xFF) == 0x01);
    Assert.assertTrue((writebackFrame [7] & 0xFF) == 0x00);
    Assert.assertTrue((writebackFrame [8] & 0xFF) == Hpai.getStructureSize());
    Assert.assertTrue((writebackFrame [9] & 0xFF) == Hpai.HostProtocolCode.IPV4_UDP.getValue());
    Assert.assertTrue((writebackFrame [10] & 0xFF) == 127);
    Assert.assertTrue((writebackFrame [11] & 0xFF) == 0);
    Assert.assertTrue((writebackFrame [12] & 0xFF) == 0);
    Assert.assertTrue((writebackFrame [13] & 0xFF) == 1);
    Assert.assertTrue((writebackFrame [14] & 0xFF) == 0xFF);
    Assert.assertTrue((writebackFrame [15] & 0xFF) == 0xFF);
  }


  /**
   * Test KNX frame constructor behavior when the STI does not match KNX connection state request.
   */
  @Test public void testFrameConstructorIncorrectSTI()
  {
    try
    {
      byte[] frame = new byte[]
      {
          IpMessage.KNXNET_IP_10_HEADER_SIZE,
          IpMessage.KNXNET_IP_10_VERSION,
          (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
          (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() & 0xFF),
          0x00,
          IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
          0x01,     // channel ID
          0x00,     // reserved
          (byte)Hpai.getStructureSize(),
          Hpai.HostProtocolCode.IPV4_UDP.getValue(),
          127,
          0,
          0,
          1,
          (byte)0xFF,
          (byte)0xFF
      };

      new IpConnectionStateReq(frame);

      Assert.fail("should not get here...");
    }

    catch (IpMessage.FrameException e)
    {
      // expected....
    }
  }

  /**
   * Test KNX frame constructor behavior when the STI (low byte) does not match KNX connection
   * state request.
   */
  @Test public void testFrameConstructorIncorrectSTI2()
  {
    try
    {
      byte[] frame = new byte[]
      {
          IpMessage.KNXNET_IP_10_HEADER_SIZE,
          IpMessage.KNXNET_IP_10_VERSION,
          0x10,
          (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
          0x00,
          IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
          0x01,     // channel ID
          0x00,     // reserved
          (byte)Hpai.getStructureSize(),
          Hpai.HostProtocolCode.IPV4_UDP.getValue(),
          127,
          0,
          0,
          1,
          (byte)0xFF,
          (byte)0xFF
      };

      new IpConnectionStateReq(frame);

      Assert.fail("should not get here...");
    }

    catch (IpMessage.FrameException e)
    {
      // expected....
    }
  }

  /**
   * Test KNX frame constructor with incorrect KNXnet 1.0 frame header.
   */
  @Test public void testFrameConstructorIncorrectHeader()
  {
    try
    {
      byte[] frame = new byte[]
      {
          IpMessage.KNXNET_IP_10_HEADER_SIZE + 1,
          IpMessage.KNXNET_IP_10_VERSION,
          (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
          (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
          0x00,
          IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
          0x01,     // channel ID
          0x00,     // reserved
          (byte)Hpai.getStructureSize(),
          Hpai.HostProtocolCode.IPV4_UDP.getValue(),
          127,
          0,
          0,
          1,
          (byte)0xFF,
          (byte)0xFF
      };

      new IpConnectionStateReq(frame);

      Assert.fail("should not get here...");
    }

    catch (IpMessage.FrameException e)
    {
      // expected....
    }
  }


  /**
   * Test KNX frame constructor behavior with incorrect frame length value for a connection state
   * request.
   */
  @Test public void testFrameConstructorWrongLength()
  {
    try
    {
      byte[] frame = new byte[]
      {
          IpMessage.KNXNET_IP_10_HEADER_SIZE,
          IpMessage.KNXNET_IP_10_VERSION,
          (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
          (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
          0x00,
          IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH - 1,
          0x01,     // channel ID
          0x00,     // reserved
          (byte)Hpai.getStructureSize(),
          Hpai.HostProtocolCode.IPV4_UDP.getValue(),
          127,
          0,
          0,
          1,
          (byte)0xFF,
          (byte)0xFF
      };

      new IpConnectionStateReq(frame);

      Assert.fail("should not get here...");
    }

    catch (IpMessage.FrameException e)
    {
      // expected....
    }
  }


  /**
   * Test KNX frame constructor behavior with incorrect HPAI structure length
   */
  @Test public void testFrameConstructorBrokenHPAI()
  {
    try
    {
      byte[] frame = new byte[]
      {
          IpMessage.KNXNET_IP_10_HEADER_SIZE,
          IpMessage.KNXNET_IP_10_VERSION,
          (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
          (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
          0x00,
          IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
          0x01,     // channel ID
          0x00,     // reserved
          (byte)(Hpai.getStructureSize() + 2),
          Hpai.HostProtocolCode.IPV4_UDP.getValue(),
          127,
          0,
          0,
          1,
          (byte)0xFF,
          (byte)0xFF
      };

      new IpConnectionStateReq(frame);

      Assert.fail("should not get here...");
    }

    catch (IpMessage.FrameException e)
    {
      // expected....
    }
  }

  /**
   * Test KNX frame constructor behavior with null arg
   */
  @Test public void testFrameConstructorWithNullArg()
  {
    try
    {
      new IpConnectionStateReq(null);

      Assert.fail("should not get here...");
    }

    catch (IpMessage.FrameException e)
    {
      // expected....
    }
  }



  // IsConnectionStateRequest() Tests -------------------------------------------------------------

  /**
   * Basic test for isConnectionStateRequest() call.
   */
  @Test public void testIsConnectionStateRequest()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
        0x00,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
        0x01,     // channel ID
        0x00,     // reserved
        (byte)Hpai.getStructureSize(),
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        100
    };

    Assert.assertTrue(IpConnectionStateReq.getFrameError(frame), IpConnectionStateReq.isConnectionStateRequest(frame));
  }

  /**
   * Test isConnectionStateRequest() behavior with incomplete KNX frame.
   */
  @Test public void testIsConnectionStateRequestHeaderOnly()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
        0x00,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH
    };

    Assert.assertTrue(
        IpConnectionStateReq.getFrameError(frame),
        !IpConnectionStateReq.isConnectionStateRequest(frame)
    );
  }


  /**
   * Test isConnectionStateRequest() behavior with incorrect lenght field value in KNX frame.
   */
  @Test public void testIsConnectionStateRequestWrongLength()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
        0x00,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH - 1,
        0x01,     // channel ID
        0x00,     // reserved
        (byte)Hpai.getStructureSize(),
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        100
    };

    Assert.assertTrue(
        IpConnectionStateReq.getFrameError(frame),
        !IpConnectionStateReq.isConnectionStateRequest(frame)
    );
  }

  /**
   * Test isConnectionStateRequest() behavior with incorrect service type identifier value.
   */
  @Test public void testIsConnectionStateRequestWrongSTI()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        0x00,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
        0x00,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
        0x01,     // channel ID
        0x00,     // reserved
        (byte)Hpai.getStructureSize(),
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        100
    };

    Assert.assertTrue(
        IpConnectionStateReq.getFrameError(frame),
        !IpConnectionStateReq.isConnectionStateRequest(frame)
    );
  }


  /**
   * Test isConnectionStateRequest() behavior with incorrect service type identifier (low byte)
   * value.
   */
  @Test public void testIsConnectionStateRequestWrongSTILowByte()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
        0x00,
        0x00,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
        0x01,     // channel ID
        0x00,     // reserved
        (byte)Hpai.getStructureSize(),
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        100
    };

    Assert.assertTrue(
        IpConnectionStateReq.getFrameError(frame),
        !IpConnectionStateReq.isConnectionStateRequest(frame)
    );
  }

  /**
   * Test isConnectionStateRequest() behavior with non KNXnet/IP 1.0 header size value.
   */
  @Test public void testIsConnectionStateRequestWrongHeaderSize()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE + 1,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
        0x00,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
        0x01,     // channel ID
        0x00,     // reserved
        (byte)Hpai.getStructureSize(),
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        100
    };

    Assert.assertTrue(
        IpConnectionStateReq.getFrameError(frame),
        !IpConnectionStateReq.isConnectionStateRequest(frame)
    );
  }

  /**
   * Test isConnectionStateRequest() behavior with non KNXnet/IP 1.0 header version.
   */
  @Test public void testIsConnectionStateRequestWrongFrameVersion()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION - 1,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
        0x00,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
        0x01,     // channel ID
        0x00,     // reserved
        (byte)Hpai.getStructureSize(),
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        100
    };

    Assert.assertTrue(
        IpConnectionStateReq.getFrameError(frame),
        !IpConnectionStateReq.isConnectionStateRequest(frame)
    );
  }


  /**
   * Test isConnectionStateRequest() behavior with large channel value (signed vs unsigned value
   * behavior)
   */
  @Test public void testIsConnectionStateRequestMaxChannelID()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
        0x00,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
        (byte)0xFF,     // channel ID
        0x00,     // reserved
        (byte)Hpai.getStructureSize(),
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        100
    };

    Assert.assertTrue(
        IpConnectionStateReq.getFrameError(frame),
        IpConnectionStateReq.isConnectionStateRequest(frame)
    );
  }

  /**
   * Test isConnectionStateRequest() behavior when a non-null value is used in the reserved
   * byte of the request.
   */
  @Test public void testIsConnectionStateRequestNonNullReservedByte()
  {
    // Reserved byte has been set -- while it should be ignored by the implementation,
    // we still make a decision to consider such a frame 'valid'...

    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
        0x00,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
        0x01,     // channel ID
        0x01,     // reserved
        (byte)Hpai.getStructureSize(),
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        100
    };

    Assert.assertTrue(
        IpConnectionStateReq.getFrameError(frame),
        IpConnectionStateReq.isConnectionStateRequest(frame)
    );
  }


  /**
   * Test isConnectionStateRequest() behavior with incorrect KNX 1.0 HPAI structure size in
   * KNX frame.
   */
  @Test public void testIsConnectionStateRequestWrongHPAISize()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
        0x00,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
        0x01,     // channel ID
        0x00,     // reserved
        (byte)(Hpai.getStructureSize() - 1),
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        100
    };

    Assert.assertTrue(
        IpConnectionStateReq.getFrameError(frame),
        !IpConnectionStateReq.isConnectionStateRequest(frame)
    );
  }

  /**
   * Test isConnectionStateRequest() behavior with long frame length value.
   */
  @Test public void testIsConnectionStateRequestLongFrameLength()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
        0x01,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
        0x01,     // channel ID
        0x00,     // reserved
        (byte)(Hpai.getStructureSize() - 1),
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        100,

        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    Assert.assertTrue(
        IpConnectionStateReq.getFrameError(frame),
        !IpConnectionStateReq.isConnectionStateRequest(frame)
    );
  }


  // GetFrameError() Tests ------------------------------------------------------------------------

  /**
   * Basic test for getFrameError() call.
   */
  @Test public void testGetFrameError()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
        0x00,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
        0x01,     // channel ID
        0x00,     // reserved
        (byte)Hpai.getStructureSize(),
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        100
    };

    Assert.assertTrue(
        IpConnectionStateReq.getFrameError(frame),
        IpConnectionStateReq.getFrameError(frame).equals(
            IpConnectionStateReq.VALID_KNXNET_IP_10_CONNECTIONSTATE_REQUEST
        )
    );
  }

  /**
   * Test getFrameError() behavior with incomplete KNX frame.
   */
  @Test public void testGetFrameErrorHeaderOnly()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
        0x00,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH
    };

    Assert.assertTrue(
        IpConnectionStateReq.getFrameError(frame),
        !IpConnectionStateReq.getFrameError(frame).equals(
            IpConnectionStateReq.VALID_KNXNET_IP_10_CONNECTIONSTATE_REQUEST
        )
    );
  }


  /**
   * Test getFrameError() behavior with incorrect length field value in KNX frame.
   */
  @Test public void testGetFrameErrorWrongLength()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
        0x00,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH - 1,
        0x01,     // channel ID
        0x00,     // reserved
        (byte)Hpai.getStructureSize(),
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        100
    };

    Assert.assertTrue(
        IpConnectionStateReq.getFrameError(frame),
        !IpConnectionStateReq.getFrameError(frame).equals(
            IpConnectionStateReq.VALID_KNXNET_IP_10_CONNECTIONSTATE_REQUEST
        )
    );
  }

  /**
   * Test getFrameError() behavior with incorrect service type identifier value.
   */
  @Test public void testGetFrameErrorWrongSTI()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        0x00,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
        0x00,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
        0x01,     // channel ID
        0x00,     // reserved
        (byte)Hpai.getStructureSize(),
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        100
    };

    Assert.assertTrue(
        IpConnectionStateReq.getFrameError(frame),
        !IpConnectionStateReq.getFrameError(frame).equals(
            IpConnectionStateReq.VALID_KNXNET_IP_10_CONNECTIONSTATE_REQUEST
        )
    );

  }


  /**
   * Test getFrameError() behavior with incorrect service type identifier (low byte)
   * value.
   */
  @Test public void testGetFrameErrorWrongSTILowByte()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
        0x00,
        0x00,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
        0x01,     // channel ID
        0x00,     // reserved
        (byte)Hpai.getStructureSize(),
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        100
    };

    Assert.assertTrue(
        IpConnectionStateReq.getFrameError(frame),
        !IpConnectionStateReq.getFrameError(frame).equals(
            IpConnectionStateReq.VALID_KNXNET_IP_10_CONNECTIONSTATE_REQUEST
        )
    );
  }

  /**
   * Test getFrameError() behavior with non KNXnet/IP 1.0 header size value.
   */
  @Test public void testGetFrameErrorWrongHeaderSize()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE + 1,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
        0x00,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
        0x01,     // channel ID
        0x00,     // reserved
        (byte)Hpai.getStructureSize(),
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        100
    };

    Assert.assertTrue(
        IpConnectionStateReq.getFrameError(frame),
        !IpConnectionStateReq.getFrameError(frame).equals(
            IpConnectionStateReq.VALID_KNXNET_IP_10_CONNECTIONSTATE_REQUEST
        )
    );
  }

  /**
   * Test getFrameError() behavior with non KNXnet/IP 1.0 header version.
   */
  @Test public void testGetFrameErrorWrongFrameVersion()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION - 1,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
        0x00,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
        0x01,     // channel ID
        0x00,     // reserved
        (byte)Hpai.getStructureSize(),
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        100
    };

    Assert.assertTrue(
        IpConnectionStateReq.getFrameError(frame),
        !IpConnectionStateReq.getFrameError(frame).equals(
            IpConnectionStateReq.VALID_KNXNET_IP_10_CONNECTIONSTATE_REQUEST
        )
    );
  }


  /**
   * Test getFrameError() behavior with large channel value (signed vs unsigned value
   * behavior)
   */
  @Test public void testGetFrameErrorMaxChannelID()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
        0x00,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
        (byte)0xFF,     // channel ID
        0x00,     // reserved
        (byte)Hpai.getStructureSize(),
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        100
    };

    Assert.assertTrue(
        IpConnectionStateReq.getFrameError(frame),
        IpConnectionStateReq.getFrameError(frame).equals(
            IpConnectionStateReq.VALID_KNXNET_IP_10_CONNECTIONSTATE_REQUEST
        )
    );
  }

  /**
   * Test getFrameError() behavior when a non-null value is used in the reserved
   * byte of the request.
   */
  @Test public void testGetFrameErrorNonNullReservedByte()
  {
    // Reserved byte has been set -- while it should be ignored by the implementation,
    // we still make a decision to consider such a frame 'valid'...

    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
        0x00,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
        0x01,     // channel ID
        0x01,     // reserved
        (byte)Hpai.getStructureSize(),
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        100
    };

    Assert.assertTrue(
        IpConnectionStateReq.getFrameError(frame),
        IpConnectionStateReq.getFrameError(frame).equals(
            IpConnectionStateReq.VALID_KNXNET_IP_10_CONNECTIONSTATE_REQUEST
        )
    );
  }


  /**
   * Test getFrameError() behavior with incorrect KNX 1.0 HPAI structure size in
   * KNX frame.
   */
  @Test public void testGetFrameErrorWrongHPAISize()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
        0x00,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
        0x01,     // channel ID
        0x00,     // reserved
        (byte)(Hpai.getStructureSize() - 1),
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        100
    };

    Assert.assertTrue(
        IpConnectionStateReq.getFrameError(frame),
        !IpConnectionStateReq.getFrameError(frame).equals(
            IpConnectionStateReq.VALID_KNXNET_IP_10_CONNECTIONSTATE_REQUEST
        )
    );
  }

  /**
   * Test getFrameError() behavior with long frame length value.
   */
  @Test public void testGetFrameErrorLongFrameLength()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.CONNECTIONSTATE_REQUEST.getValue() & 0xFF),
        0x01,
        IpConnectionStateReq.KNXNET_IP_10_CONNECTIONSTATE_REQUEST_FRAME_LENGTH,
        0x01,     // channel ID
        0x00,     // reserved
        (byte)(Hpai.getStructureSize() - 1),
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        100,

        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    Assert.assertTrue(
        IpConnectionStateReq.getFrameError(frame),
        !IpConnectionStateReq.getFrameError(frame).equals(
            IpConnectionStateReq.VALID_KNXNET_IP_10_CONNECTIONSTATE_REQUEST
        )
    );
  }


  // CreateNoErrorResponse Tests ------------------------------------------------------------------

  /**
   * Basic test for createNoErrorResponse() call.
   */
  @Test public void testCreateNoErrorResponse()
  {

    IpConnectionStateReq request = new IpConnectionStateReq(
        128, new Hpai(new InetSocketAddress("0.0.0.0", 0))
    );

    IpConnectionStateResp response = request.createNoErrorResponse();

    Assert.assertTrue(response.getChannelId() == request.getChannelId());
    Assert.assertTrue(response.getStatus() == IpConnectionStateResp.Status.NO_ERROR);
  }


  // CreateConnectionIDErrorResponse Tests --------------------------------------------------------

  /**
   * Basic test for createConnectionIDErrorResponse() call.
   */
  @Test public void testCreateConnectionIDErrorResponse()
  {

    IpConnectionStateReq request = new IpConnectionStateReq(
        200, new Hpai(new InetSocketAddress("2.5.9.30", 122))
    );

    IpConnectionStateResp response = request.createConnectionIDErrorResponse();

    Assert.assertTrue(response.getChannelId() == request.getChannelId());
    Assert.assertTrue(response.getStatus() == IpConnectionStateResp.Status.CONNECTION_ID_ERROR);

  }

  // ToString Tests -------------------------------------------------------------------------------

  /**
   * Basic toString() test. Expecting to see client's IP address, port and channel ID in the
   * output based on current implementation.
   */
  @Test public void testToString()
  {
    IpConnectionStateReq stateRequest = new IpConnectionStateReq(
        128, new Hpai(new InetSocketAddress("1.2.3.4", 55))
    );

    Assert.assertTrue(stateRequest.toString().contains("1.2.3.4:55"));
    Assert.assertTrue(stateRequest.toString().contains("128"));
  }
}

