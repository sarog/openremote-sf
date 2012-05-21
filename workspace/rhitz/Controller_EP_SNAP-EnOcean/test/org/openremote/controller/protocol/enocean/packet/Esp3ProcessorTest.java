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
package org.openremote.controller.protocol.enocean.packet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import org.openremote.controller.protocol.enocean.DeviceID;
import org.openremote.controller.protocol.enocean.EspException;
import org.openremote.controller.protocol.enocean.packet.command.Esp3RdIDBaseCommand;
import org.openremote.controller.protocol.enocean.packet.command.Esp3RdIDBaseResponse;
import org.openremote.controller.protocol.enocean.packet.radio.Esp3RPSTelegram;
import org.openremote.controller.protocol.enocean.packet.radio.Esp3RadioTelegramOptData;
import org.openremote.controller.protocol.enocean.port.Esp3ComPortAdapter;
import org.openremote.controller.protocol.enocean.port.EspPortConfiguration;
import org.openremote.controller.protocol.enocean.port.MockPort;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Unit tests for {@link Esp3Processor} class.
 *
 * @author Rainer Hitz
 */
public class Esp3ProcessorTest
{

  // Class Members --------------------------------------------------------------------------------

  private static final String COM_PORT = "/dev/cu.usbserial-FTUOKF2Q";


  // Instance Fields ------------------------------------------------------------------------------

  private EspPortConfiguration portConfig;
  private MockPort mockPort;
  private Esp3ComPortAdapter portAdapter;
  private Esp3Processor processor;

  private DeviceID senderID;
  private DeviceID baseID;

  private Esp3RdIDBaseCommand rdIDBaseCommand;
  private Esp3RdIDBaseResponse rdIDBaseResponse;

  private Esp3RPSTelegram radioTelegram;
  private Esp3ResponsePacket radioTelegramRespose;


  // Test Lifecycle -------------------------------------------------------------------------------

  @Before public void setUp() throws Exception
  {
    portConfig = new EspPortConfiguration();
    portConfig.setComPort(COM_PORT);

    mockPort = new MockPort();
    mockPort.setRequestResponseMode();

    portAdapter = new Esp3ComPortAdapter(mockPort, portConfig);
    processor = new Esp3Processor(portAdapter);

    processor.start();

    baseID = DeviceID.fromString("0xFF800000");
    senderID = DeviceID.fromString("0xFF800001");

    rdIDBaseCommand = new Esp3RdIDBaseCommand();
    rdIDBaseResponse = createRdIDBaseResponse(Esp3ResponsePacket.ReturnCode.RET_OK);

    radioTelegram = new Esp3RPSTelegram(senderID);
    radioTelegramRespose = createRadioTelegramResponse(Esp3ResponsePacket.ReturnCode.RET_OK);
  }

  @After public void tearDown() throws Exception
  {
    processor.stop();
  }


  // Tests ----------------------------------------------------------------------------------------

  @Test public void testSendCommand() throws Exception
  {
    mockPort.addDataToReturn(rdIDBaseResponse.asByteArray());

    rdIDBaseCommand.send(processor);

    Assert.assertEquals(Esp3ResponsePacket.ReturnCode.RET_OK, rdIDBaseCommand.getReturnCode());
    Assert.assertEquals(baseID, rdIDBaseCommand.getBaseID());
  }

  @Test public void testSendRadioTelegram() throws Exception
  {
    mockPort.addDataToReturn(radioTelegramRespose.asByteArray());

    radioTelegram.send(processor);

    Assert.assertEquals(Esp3ResponsePacket.ReturnCode.RET_OK, radioTelegram.getReturnCode());
  }

  @Test public void testRepeatedSend() throws Exception
  {
    int repeatCount = 10;

    for(int i = 0; i < repeatCount; i++)
    {
      mockPort.addDataToReturn(rdIDBaseResponse.asByteArray());
    }

    for(int i = 0; i < repeatCount; i++)
    {
      Esp3RdIDBaseCommand command = new Esp3RdIDBaseCommand();

      command.send(processor);

      Assert.assertEquals(Esp3ResponsePacket.ReturnCode.RET_OK, command.getReturnCode());
      Assert.assertEquals(baseID, command.getBaseID());
    }
  }

  @Test public void testResponseTimeout() throws Exception
  {
    try
    {
      radioTelegram.send(processor);

      Assert.fail();
    }
    catch (EspException e)
    {
      // expected
    }
  }

  @Test public void testHeaderCRC8() throws Exception
  {
    byte[] invalidResponse = rdIDBaseResponse.asByteArray();
    invalidResponse[Esp3PacketHeader.ESP3_HEADER_CRC8_INDEX] =
        (byte)(invalidResponse[Esp3PacketHeader.ESP3_HEADER_CRC8_INDEX] + 1);

    mockPort.addDataToReturn(invalidResponse);

    try
    {
      rdIDBaseCommand.send(processor);

      Assert.fail();
    }
    catch (EspException e)
    {
      // expected
    }

    List<byte[]> returnData = new ArrayList<byte[]>();
    returnData.add(invalidResponse);
    returnData.add(rdIDBaseResponse.asByteArray());

    mockPort.addDataToReturn(returnData);

    rdIDBaseCommand.send(processor);

    Assert.assertEquals(Esp3ResponsePacket.ReturnCode.RET_OK, rdIDBaseCommand.getReturnCode());
    Assert.assertEquals(baseID, rdIDBaseCommand.getBaseID());
  }

  @Test public void testDataCRC8() throws Exception
  {
    byte[] invalidResponse = rdIDBaseResponse.asByteArray();
    // Invalidate data CRC8
    invalidResponse[invalidResponse.length - 1] =
        (byte)(invalidResponse[invalidResponse.length - 1] + 1);

    mockPort.addDataToReturn(invalidResponse);

    try
    {
      rdIDBaseCommand.send(processor);

      Assert.fail();
    }
    catch (EspException e)
    {
      // expected
    }

    List<byte[]> returnData = new ArrayList<byte[]>();
    returnData.add(invalidResponse);
    returnData.add(rdIDBaseResponse.asByteArray());

    mockPort.addDataToReturn(returnData);

    rdIDBaseCommand.send(processor);

    Assert.assertEquals(Esp3ResponsePacket.ReturnCode.RET_OK, rdIDBaseCommand.getReturnCode());
    Assert.assertEquals(baseID, rdIDBaseCommand.getBaseID());
  }

  @Test public void testFragmentedResponse() throws Exception
  {
    List<Integer> boundaries = new ArrayList<Integer>();

    boundaries.add(Esp3PacketHeader.ESP3_HEADER_SYNC_BYTE_INDEX);
    boundaries.add(Esp3PacketHeader.ESP3_HEADER_SIZE / 2);
    boundaries.add(Esp3PacketHeader.ESP3_HEADER_CRC8_INDEX);
    boundaries.add(
        Esp3PacketHeader.ESP3_HEADER_CRC8_INDEX +
            (Esp3RdIDBaseResponse.ESP3_RESPONSE_RD_IDBASE_DATA_LENGTH / 2)
    );

    List<byte[]> fragments = fragmentPacket(rdIDBaseResponse, boundaries);

    mockPort.addDataToReturn(fragments);

    rdIDBaseCommand.send(processor);

    Assert.assertEquals(Esp3ResponsePacket.ReturnCode.RET_OK, rdIDBaseCommand.getReturnCode());
    Assert.assertEquals(baseID, rdIDBaseCommand.getBaseID());
  }


  // Helpers --------------------------------------------------------------------------------------

  private Esp3RdIDBaseResponse createRdIDBaseResponse(Esp3ResponsePacket.ReturnCode returnCode)
      throws EspException
  {
    byte[] dataBytes = new byte[Esp3RdIDBaseResponse.ESP3_RESPONSE_RD_IDBASE_DATA_LENGTH];

    dataBytes[Esp3ResponsePacket.ESP3_RESPONSE_RETURN_CODE_INDEX] = returnCode.getValue();

    System.arraycopy(
        baseID.asByteArray(), 0, dataBytes,
        Esp3ResponsePacket.ESP3_RESPONSE_RETURN_CODE_INDEX + 1,
        DeviceID.ENOCEAN_ESP_ID_LENGTH
    );

    return new Esp3RdIDBaseResponse(dataBytes);
  }

  private Esp3ResponsePacket createRadioTelegramResponse(Esp3ResponsePacket.ReturnCode returnCode)
      throws EspException
  {
    return new Esp3ResponsePacket(
        new byte[] {returnCode.getValue()},
        Esp3RadioTelegramOptData.ESP3_RADIO_OPT_DATA_TX.asByteArray()
    );
  }

  private List<byte[]> fragmentPacket(Esp3Packet packet, List<Integer> boundaries)
  {

    List<byte[]> fragments = new LinkedList<byte[]>();
    byte[] packetBytes = packet.asByteArray();

    int startIndex = 0;
    int stopIndex = 0;

    for(int index = 0; index < (boundaries.size() + 1); index++)
    {
      if(index == 0)
      {
        startIndex = 0;
      }
      else
      {
        startIndex = stopIndex + 1;
      }

      if(index < boundaries.size())
      {
        stopIndex = boundaries.get(index);
      }
      else
      {
        stopIndex = packetBytes.length - 1;
      }

      int length = stopIndex - startIndex + 1;
      byte[] fragmentBytes = new byte[length];
      System.arraycopy(
          packetBytes, startIndex, fragmentBytes, 0, length
      );

      fragments.add(fragmentBytes);
    }

    return fragments;
  }
}
