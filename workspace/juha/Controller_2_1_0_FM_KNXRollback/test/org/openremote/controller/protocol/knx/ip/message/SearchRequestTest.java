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

import junit.framework.Assert;
import org.junit.Test;
import org.openremote.controller.protocol.knx.ServiceTypeIdentifier;

/**
 * Unit tests for {@link org.openremote.controller.protocol.knx.ip.message.IpDiscoverReq} class.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class SearchRequestTest
{


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
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() & 0xFF),
        0,
        IpDiscoverReq.KNXNET_IP_10_SEARCH_REQUEST_FRAME_LENGTH,
        Hpai.KNXNET_IP_10_HPAI_SIZE,
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        50
    };

    IpDiscoverReq discoverRequest = new IpDiscoverReq(frame);

    Assert.assertTrue(discoverRequest.getPrimitive() == IpMessage.Primitive.REQ);

    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    discoverRequest.write(bout);

    byte[] writebackFrame = bout.toByteArray();

    Assert.assertTrue((writebackFrame [0] & 0xFF) == IpMessage.KNXNET_IP_10_HEADER_SIZE);
    Assert.assertTrue((writebackFrame [1] & 0xFF) == IpMessage.KNXNET_IP_10_VERSION);
    Assert.assertTrue((writebackFrame [2] & 0xFF) == 0x02);
    Assert.assertTrue((writebackFrame [3] & 0xFF) == 0x01);
    Assert.assertTrue((writebackFrame [4] & 0xFF) == 0x00);
    Assert.assertTrue((writebackFrame [5] & 0xFF) == IpDiscoverReq.KNXNET_IP_10_SEARCH_REQUEST_FRAME_LENGTH);
    Assert.assertTrue((writebackFrame [6] & 0xFF) == Hpai.KNXNET_IP_10_HPAI_SIZE);
    Assert.assertTrue((writebackFrame [7] & 0xFF) == Hpai.HostProtocolCode.IPV4_UDP.getValue());
    Assert.assertTrue((writebackFrame [8] & 0xFF) == 127);
    Assert.assertTrue((writebackFrame [9] & 0xFF) == 0);
    Assert.assertTrue((writebackFrame [10] & 0xFF) == 0);
    Assert.assertTrue((writebackFrame [11] & 0xFF) == 1);
    Assert.assertTrue((writebackFrame [12] & 0xFF) == 0);
    Assert.assertTrue((writebackFrame [13] & 0xFF) == 50);
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
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() & 0xFF),
        0,
        IpDiscoverReq.KNXNET_IP_10_SEARCH_REQUEST_FRAME_LENGTH,
        Hpai.KNXNET_IP_10_HPAI_SIZE,
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        (byte)0xFF,
        (byte)0xFF
    };

    IpDiscoverReq discoverRequest = new IpDiscoverReq(frame);

    Assert.assertTrue(discoverRequest.getPrimitive() == IpMessage.Primitive.REQ);

    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    discoverRequest.write(bout);

    byte[] writebackFrame = bout.toByteArray();

    Assert.assertTrue((writebackFrame [0] & 0xFF) == IpMessage.KNXNET_IP_10_HEADER_SIZE);
    Assert.assertTrue((writebackFrame [1] & 0xFF) == IpMessage.KNXNET_IP_10_VERSION);
    Assert.assertTrue((writebackFrame [2] & 0xFF) == 0x02);
    Assert.assertTrue((writebackFrame [3] & 0xFF) == 0x01);
    Assert.assertTrue((writebackFrame [4] & 0xFF) == 0x00);
    Assert.assertTrue((writebackFrame [5] & 0xFF) == IpDiscoverReq.KNXNET_IP_10_SEARCH_REQUEST_FRAME_LENGTH);
    Assert.assertTrue((writebackFrame [6] & 0xFF) == Hpai.KNXNET_IP_10_HPAI_SIZE);
    Assert.assertTrue((writebackFrame [7] & 0xFF) == Hpai.HostProtocolCode.IPV4_UDP.getValue());
    Assert.assertTrue((writebackFrame [8] & 0xFF) == 127);
    Assert.assertTrue((writebackFrame [9] & 0xFF) == 0);
    Assert.assertTrue((writebackFrame [10] & 0xFF) == 0);
    Assert.assertTrue((writebackFrame [11] & 0xFF) == 1);
    Assert.assertTrue((writebackFrame [12] & 0xFF) == 0xFF);
    Assert.assertTrue((writebackFrame [13] & 0xFF) == 0xFF);
  }


  /**
   * Test KNX frame constructor with maxed value for IP address and port (signed vs. unsigned
   * value handling)
   *
   * @throws Exception      if test fails
   */
  @Test public void testFrameConstructorAddressAndPortMax() throws Exception
  {

    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() & 0xFF),
        0,
        IpDiscoverReq.KNXNET_IP_10_SEARCH_REQUEST_FRAME_LENGTH,
        Hpai.KNXNET_IP_10_HPAI_SIZE,
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        (byte)0xFF,
        (byte)0xFF,
        (byte)0xFF,
        (byte)0xFF,
        (byte)0xFF,
        (byte)0xFF
    };

    IpDiscoverReq discoverRequest = new IpDiscoverReq(frame);

    Assert.assertTrue(discoverRequest.getPrimitive() == IpMessage.Primitive.REQ);

    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    discoverRequest.write(bout);

    byte[] writebackFrame = bout.toByteArray();

    Assert.assertTrue((writebackFrame [0] & 0xFF) == IpMessage.KNXNET_IP_10_HEADER_SIZE);
    Assert.assertTrue((writebackFrame [1] & 0xFF) == IpMessage.KNXNET_IP_10_VERSION);
    Assert.assertTrue((writebackFrame [2] & 0xFF) == 0x02);
    Assert.assertTrue((writebackFrame [3] & 0xFF) == 0x01);
    Assert.assertTrue((writebackFrame [4] & 0xFF) == 0x00);
    Assert.assertTrue((writebackFrame [5] & 0xFF) == IpDiscoverReq.KNXNET_IP_10_SEARCH_REQUEST_FRAME_LENGTH);
    Assert.assertTrue((writebackFrame [6] & 0xFF) == Hpai.KNXNET_IP_10_HPAI_SIZE);
    Assert.assertTrue((writebackFrame [7] & 0xFF) == Hpai.HostProtocolCode.IPV4_UDP.getValue());
    Assert.assertTrue((writebackFrame [8] & 0xFF) == 0xFF);
    Assert.assertTrue((writebackFrame [9] & 0xFF) == 0xFF);
    Assert.assertTrue((writebackFrame [10] & 0xFF) == 0xFF);
    Assert.assertTrue((writebackFrame [11] & 0xFF) == 0xFF);
    Assert.assertTrue((writebackFrame [12] & 0xFF) == 0xFF);
    Assert.assertTrue((writebackFrame [13] & 0xFF) == 0xFF);
  }


  /**
   * Test KNX frame constructor behavior when the STI does not match KNX search request.
   */
  @Test public void testFrameConstructorWrongSTI()
  {
    try
    {
      byte[] frame = new byte[]
      {
          IpMessage.KNXNET_IP_10_HEADER_SIZE,
          IpMessage.KNXNET_IP_10_VERSION,
          (byte)0xFF,
          (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() & 0xFF),
          0,
          IpDiscoverReq.KNXNET_IP_10_SEARCH_REQUEST_FRAME_LENGTH,
          Hpai.KNXNET_IP_10_HPAI_SIZE,
          Hpai.HostProtocolCode.IPV4_UDP.getValue(),
          127,
          0,
          0,
          1,
          0,
          50
      };

      new IpDiscoverReq(frame);

      Assert.fail("should not get here...");
    }

    catch (IpMessage.FrameException e)
    {
      // expected...
    }
  }

  /**
   * Test KNX frame constructor behavior when the STI (low byte) does not match KNX search
   * request.
   */
  @Test public void testFrameConstructorWrongSTI2()
  {
    try
    {
      byte[] frame = new byte[]
      {
          IpMessage.KNXNET_IP_10_HEADER_SIZE,
          IpMessage.KNXNET_IP_10_VERSION,
          (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() >> 8),
          (byte)0xFF,
          0,
          IpDiscoverReq.KNXNET_IP_10_SEARCH_REQUEST_FRAME_LENGTH,
          Hpai.KNXNET_IP_10_HPAI_SIZE,
          Hpai.HostProtocolCode.IPV4_UDP.getValue(),
          127,
          0,
          0,
          1,
          0,
          50
      };

      new IpDiscoverReq(frame);

      Assert.fail("should not get here...");
    }

    catch (IpMessage.FrameException e)
    {
      // expected...
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
          IpMessage.KNXNET_IP_10_HEADER_SIZE - 1,
          IpMessage.KNXNET_IP_10_VERSION,
          (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() >> 8),
          (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() & 0xFF),
          0,
          IpDiscoverReq.KNXNET_IP_10_SEARCH_REQUEST_FRAME_LENGTH,
          Hpai.KNXNET_IP_10_HPAI_SIZE,
          Hpai.HostProtocolCode.IPV4_UDP.getValue(),
          127,
          0,
          0,
          1,
          0,
          50
      };

      new IpDiscoverReq(frame);

      Assert.fail("should not get here...");
    }

    catch (IpMessage.FrameException e)
    {
      // expected...
    }
  }

  /**
   * Test KNX frame constructor behavior with incorrect frame length value for a search
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
          (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() >> 8),
          (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() & 0xFF),
          0,
          IpDiscoverReq.KNXNET_IP_10_SEARCH_REQUEST_FRAME_LENGTH - 1,
          Hpai.KNXNET_IP_10_HPAI_SIZE,
          Hpai.HostProtocolCode.IPV4_UDP.getValue(),
          127,
          0,
          0,
          1,
          0,
          50
      };

      new IpDiscoverReq(frame);

      Assert.fail("should not get here...");
    }

    catch (IpMessage.FrameException e)
    {
      // expected...
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
          (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() >> 8),
          (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() & 0xFF),
          0,
          IpDiscoverReq.KNXNET_IP_10_SEARCH_REQUEST_FRAME_LENGTH,
          Hpai.KNXNET_IP_10_HPAI_SIZE - 1,
          Hpai.HostProtocolCode.IPV4_UDP.getValue(),
          127,
          0,
          0,
          1,
          0,
          50
      };

      new IpDiscoverReq(frame);

      Assert.fail("should not get here...");
    }

    catch (IpMessage.FrameException e)
    {
      // expected...
    }
  }

  /**
   * Test KNX frame constructor behavior with missing HPAI structure in frame.
   */
  @Test public void testFrameConstructorHeaderOnly()
  {
    try
    {
      byte[] frame = new byte[]
      {
          IpMessage.KNXNET_IP_10_HEADER_SIZE,
          IpMessage.KNXNET_IP_10_VERSION,
          (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() >> 8),
          (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() & 0xFF),
          0,
          6
      };

      new IpDiscoverReq(frame);

      Assert.fail("should not get here...");
    }

    catch (IpMessage.FrameException e)
    {
      // expected...
    }
  }


  /**
   * Test KNX frame constructor behavior with null arg.
   */
  @Test public void testFrameConstructorNullArg()
  {
    try
    {
      new IpDiscoverReq((byte[])null);

      Assert.fail("should not get here...");
    }

    catch (IpMessage.FrameException e)
    {
      // expected...
    }
  }


  // IsSearchRequest Tests ------------------------------------------------------------------------


  /**
   * Basic test for isSearchRequest() method.
   */
  @Test public void testIsSearchRequest()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() & 0xFF),
        0,
        IpDiscoverReq.KNXNET_IP_10_SEARCH_REQUEST_FRAME_LENGTH,
        Hpai.KNXNET_IP_10_HPAI_SIZE,
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        50
    };

    Assert.assertTrue(IpDiscoverReq.isSearchRequest(frame));
  }


  /**
   * Test isSearchRequest() for a frame that only contains the KNX frame header.
   */
  @Test public void testIsSearchRequestHeaderOnly()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() & 0xFF),
        0,
        IpDiscoverReq.KNXNET_IP_10_SEARCH_REQUEST_FRAME_LENGTH
    };

    Assert.assertTrue(!IpDiscoverReq.isSearchRequest(frame));
  }

  /**
   * Test isSearchRequest() for a frame that is not versioned as KNXnet/IP 1.0 frame.
   */
  @Test public void testIsSearchRequestNonKNX10Header()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION + 1,
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() & 0xFF),
        0,
        IpDiscoverReq.KNXNET_IP_10_SEARCH_REQUEST_FRAME_LENGTH,
        Hpai.KNXNET_IP_10_HPAI_SIZE,
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        50
    };

    Assert.assertTrue(!IpDiscoverReq.isSearchRequest(frame));
  }

  /**
   * Test isSearchRequest() for a frame that has a non KNX 1.0 header length
   */
  @Test public void testIsSearchRequestNonKNX10HeaderLen()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE + 1,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() & 0xFF),
        0,
        IpDiscoverReq.KNXNET_IP_10_SEARCH_REQUEST_FRAME_LENGTH,
        Hpai.KNXNET_IP_10_HPAI_SIZE,
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        50
    };

    Assert.assertTrue(!IpDiscoverReq.isSearchRequest(frame));
  }


  /**
   * Test isSearchRequest() for frame that doesn't include the correct STI hi byte
   */
  @Test public void testIsSearchRequestWrongSTIHiByte()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        0x00,
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() & 0xFF),
        0,
        IpDiscoverReq.KNXNET_IP_10_SEARCH_REQUEST_FRAME_LENGTH,
        Hpai.KNXNET_IP_10_HPAI_SIZE,
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        50
    };

    Assert.assertTrue(!IpDiscoverReq.isSearchRequest(frame));
  }

  /**
   * Test isSearchRequest() with frame that doesn't include the correct STI low byte
   */
  @Test public void testIsSearchRequestWrongSTI2()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() >> 8),
        0x00,
        0,
        IpDiscoverReq.KNXNET_IP_10_SEARCH_REQUEST_FRAME_LENGTH,
        Hpai.KNXNET_IP_10_HPAI_SIZE,
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        50
    };

    Assert.assertTrue(!IpDiscoverReq.isSearchRequest(frame));
  }

  /**
   * Test isSearchRequest() with frame length mismatch (overflow).
   */
  @Test public void testIsSearchRequestIncorrectFrameLen()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() & 0xFF),
        0,
        IpDiscoverReq.KNXNET_IP_10_SEARCH_REQUEST_FRAME_LENGTH + 1,
        Hpai.KNXNET_IP_10_HPAI_SIZE,
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        50
    };

    Assert.assertTrue(!IpDiscoverReq.isSearchRequest(frame));
  }

  /**
   * Test isSearchRequest() with frame length mismatch (too low)
   */
  @Test public void testIsSearchRequestIncorrectFrameLen2()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() & 0xFF),
        0,
        IpDiscoverReq.KNXNET_IP_10_SEARCH_REQUEST_FRAME_LENGTH - 1,
        Hpai.KNXNET_IP_10_HPAI_SIZE,
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        50
    };

    Assert.assertTrue(!IpDiscoverReq.isSearchRequest(frame));
  }

  /**
   * Test isSearchRequest() with non KNX 1.0 HPAI length.
   */
  @Test public void testIsSearchRequestIncorrectHPAILen()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() & 0xFF),
        0,
        IpDiscoverReq.KNXNET_IP_10_SEARCH_REQUEST_FRAME_LENGTH,
        Hpai.KNXNET_IP_10_HPAI_SIZE + 1,
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        50
    };

    Assert.assertTrue(!IpDiscoverReq.isSearchRequest(frame));
  }

  /**
   * Test isSearchRequest() with non KNX 1.0 HPAI length.
   */
  @Test public void testIsSearchRequestIncorrectHPAILen2()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() & 0xFF),
        0,
        IpDiscoverReq.KNXNET_IP_10_SEARCH_REQUEST_FRAME_LENGTH,
        Hpai.KNXNET_IP_10_HPAI_SIZE - 1,
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        50
    };

    Assert.assertTrue(!IpDiscoverReq.isSearchRequest(frame));
  }

  /**
   * Test isSearchRequest() with frame that contains too many bytes.
   */
  @Test public void testIsSearchRequestIncorrectExtraBytes()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() & 0xFF),
        0,
        IpDiscoverReq.KNXNET_IP_10_SEARCH_REQUEST_FRAME_LENGTH,
        Hpai.KNXNET_IP_10_HPAI_SIZE,
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        50,
        0
    };

    Assert.assertTrue(!IpDiscoverReq.isSearchRequest(frame));
  }

  /**
   * Test isSearchRequest() with frame that doesn't contain all the bytes (missing port value).
   */
  @Test public void testIsSearchRequestIncorrectTooFewBytes()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() & 0xFF),
        0,
        IpDiscoverReq.KNXNET_IP_10_SEARCH_REQUEST_FRAME_LENGTH,
        Hpai.KNXNET_IP_10_HPAI_SIZE,
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0
    };

    Assert.assertTrue(!IpDiscoverReq.isSearchRequest(frame));
  }

  /**
   * Test isSearchRequest() with frame that contains above 8-bit frame length value.
   */
  @Test public void testIsSearchRequestIncorrectLength()
  {
    byte[] frame = new byte[]
    {
        IpMessage.KNXNET_IP_10_HEADER_SIZE,
        IpMessage.KNXNET_IP_10_VERSION,
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() >> 8),
        (byte)(ServiceTypeIdentifier.SEARCH_REQUEST.getValue() & 0xFF),
        1,
        IpDiscoverReq.KNXNET_IP_10_SEARCH_REQUEST_FRAME_LENGTH,
        Hpai.KNXNET_IP_10_HPAI_SIZE,
        Hpai.HostProtocolCode.IPV4_UDP.getValue(),
        127,
        0,
        0,
        1,
        0,
        50,

        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    Assert.assertTrue(IpDiscoverReq.getFrameError(frame), !IpDiscoverReq.isSearchRequest(frame));
  }

}

