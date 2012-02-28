/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.junit.Assert;
import org.junit.Test;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.protocol.knx.KNXCommandBuilder;

/**
 * Unit tests for {@link org.openremote.controller.protocol.knx.ip.message.Hpai} class.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Olivier Gandit
 */
public class HpaiTest
{

  private final static Logger log = Logger.getLogger(KNXCommandBuilder.KNX_LOG_CATEGORY);


  /**
   * Test fixed IPV4_UDP HPAI structure size
   */
  @Test public void testStructureSize()
  {
    Assert.assertTrue(Hpai.getStructureSize() == 8);
  }


  /**
   * Test a few stream variations to construct a HPAI.
   *
   * @throws IOException    if test fails for any reason
   */
  @Test public void testStreamConstructor() throws IOException
  {
    // test with non-buffered stream...

    ByteArrayInputStream in = new ByteArrayInputStream(
        new byte[]
            {
              Hpai.KNXNET_IP_10_HPAI_SIZE, 0x01, 0x01, 0x01, 0x01, 0x01, 0x00, 0x01
            }
    );

    Hpai hpai = new Hpai(in);

    Assert.assertTrue(hpai.getAddress().equals(new InetSocketAddress("1.1.1.1", 1)));



    // check with buffered stream too, just making sure we don't make bad I/O assumptions...

    BufferedInputStream bin = new BufferedInputStream(new ByteArrayInputStream(
        new byte[]
            {
              Hpai.KNXNET_IP_10_HPAI_SIZE, 0x01, 0x01, 0x01, 0x01, 0x01, 0x00, 0x01
            }
    ));

    hpai = new Hpai(bin);

    Assert.assertTrue(hpai.getAddress().equals(new InetSocketAddress("1.1.1.1", 1)));


    
    // test what happens if we try HPAI with a wildcard address...

    in = new ByteArrayInputStream(
        new byte[]
            {
              Hpai.KNXNET_IP_10_HPAI_SIZE, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
            }
    );

    hpai = new Hpai(in);

    Assert.assertTrue(hpai.getAddress().equals(new InetSocketAddress("0.0.0.0", 0)));


    // test with larger values to make sure we don't trip with Java's signed bytes...

    bin = new BufferedInputStream(new ByteArrayInputStream(
        new byte[]
            {
              Hpai.KNXNET_IP_10_HPAI_SIZE, 0x01, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
              (byte) 0x0F, (byte) 0xFF
            }
    ));

    hpai = new Hpai(bin);

    Assert.assertTrue(
        "Expected 255.255.255.255:4096, got " + hpai.getAddress(),
        hpai.getAddress().equals(new InetSocketAddress("255.255.255.255", 4095))
    );
  }


  /**
   * Test behavior with unsupported IPV4_TCP host protocol.
   */
  @Test public void testUnsupportedHostProtocol()
  {
    ByteArrayInputStream in = new ByteArrayInputStream(
        new byte[]
            {
              Hpai.KNXNET_IP_10_HPAI_SIZE, 0x02, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
              (byte) 0x0F, (byte) 0xFF
            }
    );

    try
    {
      new Hpai(in);

      Assert.fail("did not expect to see IPV4_TCP supported...");
    }

    catch (IOException e)
    {
      log.info("EXPECTED : IPV4_TCP is not supported -- " + e.getMessage());
    }
  }

  /**
   * Test behavior with unknown host protocol.
   */
  @Test public void testUnknownHostProtocol()
  {
    ByteArrayInputStream in = new ByteArrayInputStream(
        new byte[]
            {
              Hpai.KNXNET_IP_10_HPAI_SIZE, (byte)0xCA, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
              (byte) 0x0F, (byte) 0xFF
            }
    );

    try
    {
      new Hpai(in);

      Assert.fail("did not expect to see unknown host protocol to pass...");
    }

    catch (IOException e)
    {
      log.info("EXPECTED : unknown host protocol code -- " + e.getMessage());
    }
  }


  /**
   * Test behavior with incorrect structure size.
   */
  @Test public void testIncorrectStructureSize()
  {
    ByteArrayInputStream in = new ByteArrayInputStream(
        new byte[]
            {
              0x01, (byte)0x01, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
              (byte) 0xFF, (byte) 0xFF
            }
    );

    try
    {
      new Hpai(in);

      Assert.fail("did not expect to see structure size 1 to pass...");
    }

    catch (IOException e)
    {
      log.info("EXPECTED : incorrect structure size -- " + e.getMessage());
    }
  }



  /**
   * Test behavior with unexpected end-of-stream.
   */
  @Test public void testIncompleteStream()
  {
    ByteArrayInputStream in = new ByteArrayInputStream(
        new byte[]
            {
              Hpai.KNXNET_IP_10_HPAI_SIZE
            }
    );

    try
    {
      new Hpai(in);

      Assert.fail("Expected IO Exception with end-of-stream -1");
    }

    catch (IOException e)
    {
      log.info("EXPECTED : end-of-stream in middle of reading HPAI -- " + e.getMessage());
    }
  }



  /**
   * Test blocking behavior on misbehaving stream.
   */
  @Test public void testBlockingInputStream()
  {
    final BlockingInputStream bin = new BlockingInputStream(new ByteArrayInputStream(
        new byte[]
            {
              Hpai.KNXNET_IP_10_HPAI_SIZE
            }
    ));

    try
    {
      new Hpai(bin);

      Assert.fail("shouldn't get here...");
    }

    catch (BlockingInputStream.BlockException e)
    {
      Assert.fail("HPAI read blocked longer than " + BlockingInputStream.BLOCKING_PERIOD / 1000 + "s");
    }

    catch (IOException e)
    {
      // expected

      log.info("EXPECTED : HPAI read unblocked -- " + e.getMessage());
    }
  }


  /**
   * Test the frame byte output.
   *
   * @throws Exception  if test fails
   */
  @Test public void testFrameWrite() throws Exception
  {
    Hpai hpai = new Hpai(new InetSocketAddress("1.1.1.1", 1));

    ByteArrayOutputStream out = new ByteArrayOutputStream();

    hpai.write(out);

    byte[] struct = out.toByteArray();

    Assert.assertTrue(struct[0] == Hpai.KNXNET_IP_10_HPAI_SIZE);
    Assert.assertTrue(struct[1] == Hpai.HostProtocolCode.IPV4_UDP.getValue());
    Assert.assertTrue(struct[2] == 0x1);
    Assert.assertTrue(struct[3] == 0x1);
    Assert.assertTrue(struct[4] == 0x1);
    Assert.assertTrue(struct[5] == 0x1);
    Assert.assertTrue(struct[6] == 0x0);
    Assert.assertTrue(struct[7] == 0x1);

    Assert.assertTrue(struct.length == Hpai.KNXNET_IP_10_HPAI_SIZE);



    // check with larger values just to make sure we don't trip on Java's signed bytes...

    hpai = new Hpai(new InetSocketAddress("255.255.255.255", 65535));

    out = new ByteArrayOutputStream();

    hpai.write(out);

    struct = out.toByteArray();

    Assert.assertTrue(struct[0] == Hpai.KNXNET_IP_10_HPAI_SIZE);

    Assert.assertTrue(
        "Expected " + Hpai.HostProtocolCode.IPV4_UDP.getValue() + ", got " + struct[1],
        struct[1] == Hpai.HostProtocolCode.IPV4_UDP.getValue()
    );

    int address1 = struct[2] & 0xFF;
    int address2 = struct[3] & 0xFF;
    int address3 = struct[4] & 0xFF;
    int address4 = struct[5] & 0xFF;

    Assert.assertTrue("Expected 255, got " + address1, address1 == 255);
    Assert.assertTrue("Expected 255, got " + address2, address2 == 255);
    Assert.assertTrue("Expected 255, got " + address3, address3 == 255);
    Assert.assertTrue("Expected 255, got " + address4, address4 == 255);

    int port1 = struct[6] & 0xFF;
    int port2 = struct[7] & 0xFF;

    Assert.assertTrue(255 == port1);
    Assert.assertTrue(255 == port2);

    Assert.assertTrue(struct.length == Hpai.KNXNET_IP_10_HPAI_SIZE);
  }
  

  @Test public void testConstructorInvalidAddress()
  {
    try
    {
      new Hpai(new InetSocketAddress("abc", 1));
      
      Assert.fail("should not get here...");
    }

    catch (IllegalArgumentException e)
    {
      // expected

      log.info("EXPECTED : 'abc' does not resolve to an IPv4 address -- " + e.getMessage());
    }
  }


  @Test public void testConstructorInvalidAddress2()
  {
    try
    {
      new Hpai(new InetSocketAddress("255.255.0.0", 65555));

      Assert.fail("should not get here...");
    }

    catch (IllegalArgumentException e)
    {
      // expected

      log.info("EXPECTED : 65555 is out of port range -- " + e.getMessage());
    }
  }



  @Test public void testConstructorInvalidAddress3()
  {
    try
    {
      new Hpai(new InetSocketAddress("256.256.0.0", 1000));

      Assert.fail("should not get here...");
    }

    catch (IllegalArgumentException e)
    {
      // expected

      log.info("EXPECTED : '256.256.0.0' is not valid IPv4 address -- " + e.getMessage());
    }
  }
  


  @Test public void testHpai() throws IOException
  {
    Hpai h1 = new Hpai(new InetSocketAddress(InetAddress.getByName("255.128.127.1"), 2555));
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    h1.write(os);

    byte[] r = os.toByteArray();
    Assert.assertArrayEquals(
        new byte[] { 0x08, 0x01, (byte) 255, (byte) 128, 127, 1, 0x09, (byte) 0xFB }, r);

    byte[] i = new byte[] { 0x08, 0x01, (byte) 255, (byte) 128, 127, 1, 0x09, (byte) 0xFB };
    ByteArrayInputStream is = new ByteArrayInputStream(i);

    Hpai h2 = new Hpai(is);

    Assert.assertEquals(new InetSocketAddress(InetAddress.getByName("255.128.127.1"), 2555), h2.getAddress());
  }



  // Nested Classes -------------------------------------------------------------------------------

  /**
   * Simulates a very slow stream (that essentially doesn't produce any new data for
   * BLOCKING_PERIOD but does not close the stream nor mark the stream end.
   */
  private static class BlockingInputStream extends FilterInputStream
  {
    final static int BLOCKING_PERIOD = 60000;

    BlockingInputStream(InputStream in)
    {
      super(in);
    }

    @Override public int read() throws IOException
    {
      int b = in.read();

      if (b == -1)
      {
        // broken stream -- no more input is coming, though expected (simulated by ignoring
        // the -1 from underlying stream), not closing

        try
        {
          Thread.sleep(BLOCKING_PERIOD);

          throw new BlockException("Read blocked for 60 seconds...");
        }

        catch (InterruptedException e)
        {
          Thread.currentThread().interrupt();

          throw new IOException("Blocking was interrupted...");
        }
      }

      else
      {
        return b;
      }
    }

    static class BlockException extends IOException
    {
      BlockException(String msg)
      {
        super(msg);
      }
    }
  }
}
