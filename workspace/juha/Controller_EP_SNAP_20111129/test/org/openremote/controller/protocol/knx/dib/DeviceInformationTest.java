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
package org.openremote.controller.protocol.knx.dib;

import java.net.NetworkInterface;
import java.util.Enumeration;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import org.openremote.controller.protocol.knx.IndividualAddress;

/**
 * Unit tests for {@link DeviceInformation} class.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class DeviceInformationTest
{

  private NetworkInterface nic;


  // Test Lifecycle -------------------------------------------------------------------------------

  @Before public void setup()
  {
    try
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
        throw new Error("Can't resolve a valid network interface required for the test.");
      }
    }

    catch (Exception e)
    {
      Assert.fail(
          "Test setup failed, could not retrieve a valid network interface for test : " +
          e.getMessage()
      );
    }
  }


  // Tests ----------------------------------------------------------------------------------------

  @Test public void testCtor() throws Exception
  {
    
    DeviceInformation di = new DeviceInformation(
        "Test Name",
        DeviceInformation.KNXMedium.TP0,
        new IndividualAddress(0xFF01),
        nic, 1, 1, 123456789
    );

    Assert.assertTrue(
        "Expected structure size " + DeviceInformation.KNXNET_IP_10_DEVICEINFORMATION_DIB_SIZE +
        ", got " + di.getStructureSize(),
        di.getStructureSize() == DeviceInformation.KNXNET_IP_10_DEVICEINFORMATION_DIB_SIZE
    );

    byte[] struct = di.getFrameStructure();
    int indAddressHi = struct[4] & 0xFF;
    int serial1 = struct[8] & 0xFF;
    int serial2 = struct[9] & 0xFF;
    int serial3 = struct[10] & 0xFF;
    int serial4 = struct[11] & 0xFF;
    int serial5 = struct[12] & 0xFF;
    int serial6 = struct[13] & 0xFF;

    Assert.assertTrue(struct[0] == DeviceInformation.KNXNET_IP_10_DEVICEINFORMATION_DIB_SIZE);
    Assert.assertTrue(struct[1] == DescriptionInformationBlock.TypeCode.DEVICE_INFO.getValue());
    Assert.assertTrue(struct[2] == DeviceInformation.KNXMedium.TP0.getValue());
    Assert.assertTrue(struct[3] == 0x00);     // device status

    Assert.assertTrue(
        "Expected ind.address hi byte with value 255, got " + indAddressHi,
        indAddressHi == 255
    );
    
    Assert.assertTrue(struct[5] == 0x01);     // ind.address lo byte


    Assert.assertTrue(struct[6] == 0x00);     // project-install hi byte
    Assert.assertTrue(struct[7] == 0x11);     //     -- " --     lo byte
    Assert.assertTrue(serial1 == 0);          // serial number most significant byte
    Assert.assertTrue(serial2 == 0);
    Assert.assertTrue(serial3 == 7);
    Assert.assertTrue(serial4 == 91);
    Assert.assertTrue(serial5 == 205);
    Assert.assertTrue(serial6 == 21);         // serial number least significant byte
    Assert.assertTrue(struct[14] == 0);       // routing multicast address
    Assert.assertTrue(struct[15] == 0);       //       ---- " ----
    Assert.assertTrue(struct[16] == 0);       //       ---- " ----
    Assert.assertTrue(struct[17] == 0);       //       ---- " ----
    Assert.assertTrue(struct[24] == 'T');
    Assert.assertTrue(struct[25] == 'e');
    Assert.assertTrue(struct[26] == 's');
    Assert.assertTrue(struct[27] == 't');
    Assert.assertTrue(struct[28] == ' ');
    Assert.assertTrue(struct[29] == 'N');
    Assert.assertTrue(struct[30] == 'a');
    Assert.assertTrue(struct[31] == 'm');
    Assert.assertTrue(struct[32] == 'e');
    Assert.assertTrue(struct[33] == 0);
    Assert.assertTrue(struct[34] == 0);
    Assert.assertTrue(struct[35] == 0);
    Assert.assertTrue(struct[36] == 0);
    Assert.assertTrue(struct[37] == 0);
    Assert.assertTrue(struct[38] == 0);
    Assert.assertTrue(struct[39] == 0);
    Assert.assertTrue(struct[40] == 0);
    Assert.assertTrue(struct[41] == 0);
    Assert.assertTrue(struct[42] == 0);
    Assert.assertTrue(struct[43] == 0);
    Assert.assertTrue(struct[44] == 0);
    Assert.assertTrue(struct[45] == 0);
    Assert.assertTrue(struct[46] == 0);
    Assert.assertTrue(struct[47] == 0);
    Assert.assertTrue(struct[48] == 0);
    Assert.assertTrue(struct[49] == 0);
    Assert.assertTrue(struct[50] == 0);
    Assert.assertTrue(struct[51] == 0);
    Assert.assertTrue(struct[52] == 0);
    Assert.assertTrue(struct[53] == 0);

    Assert.assertTrue(struct.length == DeviceInformation.KNXNET_IP_10_DEVICEINFORMATION_DIB_SIZE);
  }


  /**
   * Test Device Information with name longer than 29 chars...
   *
   * @throws Exception if test fails
   */
  @Test public void testOverlongName() throws Exception
  {

    DeviceInformation di = new DeviceInformation(
        "Test Name1Test Name2Test Name3",
        DeviceInformation.KNXMedium.TP0,
        new IndividualAddress(0xFF01),
        nic, 1, 1, 123456789
    );

    Assert.assertTrue(
        "Expected structure size " + DeviceInformation.KNXNET_IP_10_DEVICEINFORMATION_DIB_SIZE +
        ", got " + di.getStructureSize(),
        di.getStructureSize() == DeviceInformation.KNXNET_IP_10_DEVICEINFORMATION_DIB_SIZE
    );

    byte[] struct = di.getFrameStructure();

    Assert.assertTrue(struct[0] == DeviceInformation.KNXNET_IP_10_DEVICEINFORMATION_DIB_SIZE);
    Assert.assertTrue(struct[1] == DescriptionInformationBlock.TypeCode.DEVICE_INFO.getValue());
    Assert.assertTrue(struct[2] == DeviceInformation.KNXMedium.TP0.getValue());
    Assert.assertTrue(struct[3] == 0x00);     // device status

    Assert.assertTrue(struct[6] == 0x00);     // project-install hi byte
    Assert.assertTrue(struct[7] == 0x11);     //     -- " --     lo byte
    Assert.assertTrue(struct[14] == 0);       // routing multicast address
    Assert.assertTrue(struct[15] == 0);       //       ---- " ----
    Assert.assertTrue(struct[16] == 0);       //       ---- " ----
    Assert.assertTrue(struct[17] == 0);       //       ---- " ----
    Assert.assertTrue(struct[24] == 'T');
    Assert.assertTrue(struct[25] == 'e');
    Assert.assertTrue(struct[26] == 's');
    Assert.assertTrue(struct[27] == 't');
    Assert.assertTrue(struct[28] == ' ');
    Assert.assertTrue(struct[29] == 'N');
    Assert.assertTrue(struct[30] == 'a');
    Assert.assertTrue(struct[31] == 'm');
    Assert.assertTrue(struct[32] == 'e');
    Assert.assertTrue(struct[33] == '1');
    Assert.assertTrue(struct[34] == 'T');
    Assert.assertTrue(struct[35] == 'e');
    Assert.assertTrue(struct[36] == 's');
    Assert.assertTrue(struct[37] == 't');
    Assert.assertTrue(struct[38] == ' ');
    Assert.assertTrue(struct[39] == 'N');
    Assert.assertTrue(struct[40] == 'a');
    Assert.assertTrue(struct[41] == 'm');
    Assert.assertTrue(struct[42] == 'e');
    Assert.assertTrue(struct[43] == '2');
    Assert.assertTrue(struct[44] == 'T');
    Assert.assertTrue(struct[45] == 'e');
    Assert.assertTrue(struct[46] == 's');
    Assert.assertTrue(struct[47] == 't');
    Assert.assertTrue(struct[48] == ' ');
    Assert.assertTrue(struct[49] == 'N');
    Assert.assertTrue(struct[50] == 'a');
    Assert.assertTrue(struct[51] == 'm');
    Assert.assertTrue(struct[52] == 'e');
    Assert.assertTrue(struct[53] == 0);

    Assert.assertTrue(struct.length == DeviceInformation.KNXNET_IP_10_DEVICEINFORMATION_DIB_SIZE);
  }


  /**
   * Test long serial numbers..
   *
   * @throws Exception if test fails
   */
  @Test public void testLongSerialNumber() throws Exception
  {

    DeviceInformation di = new DeviceInformation(
        "Test Name",
        DeviceInformation.KNXMedium.TP0,
        new IndividualAddress(0xFF01),
        nic, 1, 1, 140894675855327l
    );


    Assert.assertTrue(
        "Expected structure size " + DeviceInformation.KNXNET_IP_10_DEVICEINFORMATION_DIB_SIZE +
        ", got " + di.getStructureSize(),
        di.getStructureSize() == DeviceInformation.KNXNET_IP_10_DEVICEINFORMATION_DIB_SIZE
    );

    byte[] struct = di.getFrameStructure();
    int serial1 = struct[8] & 0xFF;
    int serial2 = struct[9] & 0xFF;
    int serial3 = struct[10] & 0xFF;
    int serial4 = struct[11] & 0xFF;
    int serial5 = struct[12] & 0xFF;
    int serial6 = struct[13] & 0xFF;

    Assert.assertTrue(struct.length == DeviceInformation.KNXNET_IP_10_DEVICEINFORMATION_DIB_SIZE);
    Assert.assertTrue(struct[0] == DeviceInformation.KNXNET_IP_10_DEVICEINFORMATION_DIB_SIZE);
    Assert.assertTrue(struct[1] == DescriptionInformationBlock.TypeCode.DEVICE_INFO.getValue());
    Assert.assertTrue(struct[2] == DeviceInformation.KNXMedium.TP0.getValue());
    Assert.assertTrue(struct[3] == 0x00);     // device status

    Assert.assertTrue("Expected 128, got " + serial1, serial1 == 128); // serial number most significant byte
    Assert.assertTrue("Expected 36, got " + serial2, serial2 == 36);
    Assert.assertTrue(serial3 == 153);
    Assert.assertTrue(serial4 == 26);
    Assert.assertTrue(serial5 == 231);
    Assert.assertTrue(serial6 == 223);                                 // serial number least significant byte
  }


  /**
   * Test character encoding to 8-bit units..
   *
   * @throws Exception if test fails
   */
  @Test public void test() throws Exception
  {

   
    DeviceInformation di = new DeviceInformation(
        "密码",
        DeviceInformation.KNXMedium.TP0,
        new IndividualAddress(0xFF01),
        nic, 1, 1, 140894675855327l
    );

    Assert.assertTrue(
        "Expected structure size " + DeviceInformation.KNXNET_IP_10_DEVICEINFORMATION_DIB_SIZE +
        ", got " + di.getStructureSize(),
        di.getStructureSize() == DeviceInformation.KNXNET_IP_10_DEVICEINFORMATION_DIB_SIZE
    );

    byte[] struct = di.getFrameStructure();

    Assert.assertTrue(struct.length == DeviceInformation.KNXNET_IP_10_DEVICEINFORMATION_DIB_SIZE);
    Assert.assertTrue(struct[0] == DeviceInformation.KNXNET_IP_10_DEVICEINFORMATION_DIB_SIZE);
    Assert.assertTrue(struct[1] == DescriptionInformationBlock.TypeCode.DEVICE_INFO.getValue());
    Assert.assertTrue(struct[2] == DeviceInformation.KNXMedium.TP0.getValue());
    Assert.assertTrue(struct[3] == 0x00);     // device status

    // should have only two bytes for name, using ISO-8859-1 unmapped character

    Assert.assertTrue(struct[24] != 0);
    Assert.assertTrue(struct[25] != 0);
    Assert.assertTrue(struct[26] == 0);

  }

}

