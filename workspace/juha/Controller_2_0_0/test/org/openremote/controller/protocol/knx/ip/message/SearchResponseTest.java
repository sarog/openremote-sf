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

import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.io.ByteArrayOutputStream;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import org.openremote.controller.protocol.knx.dib.DeviceInformation;
import org.openremote.controller.protocol.knx.dib.SupportedServiceFamily;
import org.openremote.controller.protocol.knx.dib.DescriptionInformationBlock;
import org.openremote.controller.protocol.knx.IndividualAddress;
import org.openremote.controller.protocol.knx.ServiceTypeIdentifier;


/**
 * Unit tests for {@link IpDiscoverResp} class.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class SearchResponseTest 
{

  private NetworkInterface nic;


  // Test LifeCycle -------------------------------------------------------------------------------

  @Before public void setup() throws Exception
  {
    nic = NetworkInterface.getByName("127.0.0.1");

    if (nic == null)
    {
      nic = NetworkInterface.getByName("localhost");
    }

    if (nic == null)
    {
      Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();

      if (nics.hasMoreElements())
      {
        nic = nics.nextElement();
      }
    }

    if (nic == null)
    {
      throw new Error("Cannot resolve a network interface required for these tests.");
    }
  }


  // Tests ----------------------------------------------------------------------------------------

  /**
   * Test contructor to build full search response frame from in-memory data structures.
   *
   * @throws Exception    if test fails
   */
  @Test public void testConstructor() throws Exception
  {
    InetSocketAddress controlAddress = new InetSocketAddress("127.0.0.1", 1000);

    Hpai hpai = new Hpai(controlAddress);

    DeviceInformation info = new DeviceInformation(
        "Test", DeviceInformation.KNXMedium.TP0, new IndividualAddress(0x0101), nic, 1, 1, 1
    );

    Map<ServiceTypeIdentifier.Family, Integer> services =
        new HashMap<ServiceTypeIdentifier.Family, Integer>();

    SupportedServiceFamily ssf = new SupportedServiceFamily(services);

    IpDiscoverResp searchResponse = new IpDiscoverResp(hpai, info, ssf);

    Assert.assertTrue(searchResponse.getPrimitive() == IpMessage.Primitive.RESP);

    Assert.assertTrue(searchResponse.getControlEndpoint().getAddress().equals(controlAddress));
  }


  /**
   * Tests writing of frame from given in-memory data.
   *
   * @throws Exception    if test fails
   */
  @Test public void testFrameWrite() throws Exception
  {
    InetSocketAddress controlAddress = new InetSocketAddress("255.255.0.0", 0xCA01);

    Hpai hpai = new Hpai(controlAddress);

    DeviceInformation info = new DeviceInformation(
        "Test", DeviceInformation.KNXMedium.TP0, new IndividualAddress(0x0101), nic, 1, 2, 1
    );

    Map<ServiceTypeIdentifier.Family, Integer> services =
        new HashMap<ServiceTypeIdentifier.Family, Integer>();

    services.put(ServiceTypeIdentifier.Family.CORE,  10);

    SupportedServiceFamily ssf = new SupportedServiceFamily(services);

    IpDiscoverResp searchResponse = new IpDiscoverResp(hpai, info, ssf);

    ByteArrayOutputStream out = new ByteArrayOutputStream();

    searchResponse.write(out);

    byte[] frame = out.toByteArray();

    int size = IpMessage.KNXNET_IP_10_HEADER_SIZE + Hpai.KNXNET_IP_10_HPAI_SIZE +
               DeviceInformation.KNXNET_IP_10_DEVICEINFORMATION_DIB_SIZE + 4;


    // KNXnet/IP header...

    Assert.assertTrue(
        "Expected frame lenght " + size + ", got " + frame.length, frame.length == size
    );

    Assert.assertTrue(frame[0] == IpMessage.KNXNET_IP_10_HEADER_SIZE);
    Assert.assertTrue(frame[1] == IpMessage.KNXNET_IP_10_VERSION);

    Assert.assertTrue(ServiceTypeIdentifier.SEARCH_RESPONSE.isIncluded(frame));

    Assert.assertTrue(frame[4] == 0);

    Assert.assertTrue(
        "Expected frame size " + size + ", got " + frame[5], frame[5] == size
    );


    // HPAI...

    int controlAddr1 = frame[8] & 0xFF;
    int controlAddr2 = frame[9] & 0xFF;
    int port1 = frame[12] & 0xFF;
    int port2 = frame[13] & 0xFF;

    Assert.assertTrue(frame[6] == 8);
    Assert.assertTrue(frame[7] == Hpai.HostProtocolCode.IPV4_UDP.getValue());

    Assert.assertTrue(controlAddr1 == 255);
    Assert.assertTrue(controlAddr2 == 255);
    Assert.assertTrue(frame[10] == 0);
    Assert.assertTrue(frame[11] == 0);

    Assert.assertTrue(port1 == 0xCA);
    Assert.assertTrue(port2 == 0x01);


    // DeviceInfo...

    Assert.assertTrue(frame[14] == 54);
    Assert.assertTrue(frame[15] == DescriptionInformationBlock.TypeCode.DEVICE_INFO.getValue());
    Assert.assertTrue(frame[16] == DeviceInformation.KNXMedium.TP0.getValue());

    Assert.assertTrue(frame[17] == 0);

    Assert.assertTrue(frame[18] == 1);
    Assert.assertTrue(frame[19] == 1);

    Assert.assertTrue("Expected 0, got " + frame[20], frame[20] == 0);
    Assert.assertTrue("Expected 17, got " + frame[21], frame[21] == 0x12);

    Assert.assertTrue(frame[22] == 0);
    Assert.assertTrue(frame[23] == 0);
    Assert.assertTrue(frame[24] == 0);
    Assert.assertTrue(frame[25] == 0);
    Assert.assertTrue(frame[26] == 0);
    Assert.assertTrue(frame[27] == 1);

    Assert.assertTrue(frame[28] == 0);
    Assert.assertTrue(frame[29] == 0);
    Assert.assertTrue(frame[30] == 0);
    Assert.assertTrue(frame[31] == 0);

    Assert.assertTrue(frame[67] == 0);
    Assert.assertTrue(frame[68] == 4);
    Assert.assertTrue(frame[69] == DescriptionInformationBlock.TypeCode.SUPPORTED_SERVICE_FAMILIES.getValue());
    Assert.assertTrue(frame[70] == ServiceTypeIdentifier.Family.CORE.getValue());
    Assert.assertTrue(frame[71] == 10);
    
  }
}

