/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2014, OpenRemote Inc.
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
package org.openremote.controller.protocol.enocean.packet.radio;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.protocol.enocean.DeviceID;
import org.openremote.controller.protocol.enocean.Manufacturer;
import org.openremote.controller.protocol.enocean.packet.Esp3Packet;
import org.openremote.controller.protocol.enocean.packet.Esp3PacketHeader;
import org.openremote.controller.protocol.enocean.profile.EepType;

import java.util.ArrayList;

/**
 * Unit tests for {@link Esp3UTEQueryTelegram} class.
 *
 * @author <a href="mailto:rainer@openremote.org">Rainer Hitz</a>
 */
public class Esp3UTEQueryTelegramTest
{
  private DeviceID senderID;
  private byte[] optData;
  private byte[] payload;
  private byte status;

  private boolean isBidirectional;
  private boolean isResponse;
  private int requestType;
  private int commandType;
  private int channelNumber;
  private int manufacturerID;
  private int eepRORG;
  private int eepFunc;
  private int eepType;


  // Test Lifecycle -------------------------------------------------------------------------------

  @Before public void setUp() throws Exception
  {
    senderID = DeviceID.fromString("0xFF800001");
    status = (byte)0x80;

    isBidirectional = true;
    isResponse = true;
    requestType = 0;
    commandType = 0;
    channelNumber = 1;
    manufacturerID = 0x7FF;
    eepRORG = 0xF6;
    eepFunc = 0x02;
    eepType = 0x01;

    optData = Esp3RadioTelegramOptData.ESP3_RADIO_OPT_DATA_TX.asByteArray();
    payload = createPayload(
        isBidirectional, isResponse, requestType, commandType,
        channelNumber, manufacturerID, eepRORG, eepFunc, eepType
    );
  }


  // Tests ----------------------------------------------------------------------------------------

  @Test public void testBasicConstruction() throws Exception
  {
    Esp3UTEQueryTelegram query = new Esp3UTEQueryTelegram(
        createDataGroup(senderID, payload, status), optData
    );

    assertTelegramAttributes(
        query, senderID, status, isBidirectional, isResponse, requestType,
        commandType, channelNumber, manufacturerID, eepRORG, eepFunc, eepType
    );
  }

  @Test public void testCommunicationFlag() throws Exception
  {
    isBidirectional = true;
    payload = createPayload(
        isBidirectional, isResponse, requestType, commandType,
        channelNumber, manufacturerID, eepRORG, eepFunc, eepType
    );
    Esp3UTEQueryTelegram query = new Esp3UTEQueryTelegram(
        createDataGroup(senderID, payload, status), optData
    );

    assertTelegramAttributes(
        query, senderID, status, isBidirectional, isResponse, requestType,
        commandType, channelNumber, manufacturerID, eepRORG, eepFunc, eepType
    );


    isBidirectional = false;
    payload = createPayload(
        isBidirectional, isResponse, requestType, commandType,
        channelNumber, manufacturerID, eepRORG, eepFunc, eepType
    );
    query = new Esp3UTEQueryTelegram(
        createDataGroup(senderID, payload, status), optData
    );

    assertTelegramAttributes(
        query, senderID, status, isBidirectional, isResponse, requestType,
        commandType, channelNumber, manufacturerID, eepRORG, eepFunc, eepType
    );
  }

  @Test public void testResponseFlag() throws Exception
  {
    isResponse = true;
    payload = createPayload(
        isBidirectional, isResponse, requestType, commandType,
        channelNumber, manufacturerID, eepRORG, eepFunc, eepType
    );
    Esp3UTEQueryTelegram query = new Esp3UTEQueryTelegram(
        createDataGroup(senderID, payload, status), optData
    );

    assertTelegramAttributes(
        query, senderID, status, isBidirectional, isResponse, requestType,
        commandType, channelNumber, manufacturerID, eepRORG, eepFunc, eepType
    );


    isResponse = false;
    payload = createPayload(
        isBidirectional, isResponse, requestType, commandType,
        channelNumber, manufacturerID, eepRORG, eepFunc, eepType
    );
    query = new Esp3UTEQueryTelegram(
        createDataGroup(senderID, payload, status), optData
    );

    assertTelegramAttributes(
        query, senderID, status, isBidirectional, isResponse, requestType,
        commandType, channelNumber, manufacturerID, eepRORG, eepFunc, eepType
    );
  }

  @Test public void testRequestType() throws Exception
  {
    requestType = 0;
    payload = createPayload(
        isBidirectional, isResponse, requestType, commandType,
        channelNumber, manufacturerID, eepRORG, eepFunc, eepType
    );
    Esp3UTEQueryTelegram query = new Esp3UTEQueryTelegram(
        createDataGroup(senderID, payload, status), optData
    );

    assertTelegramAttributes(
        query, senderID, status, isBidirectional, isResponse, requestType,
        commandType, channelNumber, manufacturerID, eepRORG, eepFunc, eepType
    );


    requestType = 3;
    payload = createPayload(
        isBidirectional, isResponse, requestType, commandType,
        channelNumber, manufacturerID, eepRORG, eepFunc, eepType
    );
    query = new Esp3UTEQueryTelegram(
        createDataGroup(senderID, payload, status), optData
    );

    assertTelegramAttributes(
        query, senderID, status, isBidirectional, isResponse, requestType,
        commandType, channelNumber, manufacturerID, eepRORG, eepFunc, eepType
    );
  }

  @Test public void testCommandType() throws Exception
  {
    for(int curCommandType = 0; curCommandType < 4; curCommandType++)
    {
      commandType = curCommandType;
      payload = createPayload(
          isBidirectional, isResponse, requestType, commandType,
          channelNumber, manufacturerID, eepRORG, eepFunc, eepType
      );
      Esp3UTEQueryTelegram query = new Esp3UTEQueryTelegram(
          createDataGroup(senderID, payload, status), optData
      );

      assertTelegramAttributes(
          query, senderID, status, isBidirectional, isResponse, requestType,
          commandType, channelNumber, manufacturerID, eepRORG, eepFunc, eepType
      );
    }

    commandType = 0x0F;
    payload = createPayload(
        isBidirectional, isResponse, requestType, commandType,
        channelNumber, manufacturerID, eepRORG, eepFunc, eepType
    );
    Esp3UTEQueryTelegram query = new Esp3UTEQueryTelegram(
        createDataGroup(senderID, payload, status), optData
    );

    assertTelegramAttributes(
        query, senderID, status, isBidirectional, isResponse, requestType,
        commandType, channelNumber, manufacturerID, eepRORG, eepFunc, eepType
    );
  }

  @Test public void testChannel() throws Exception
  {
    ArrayList<Integer> channels = new ArrayList<Integer>();
    channels.add(0);
    channels.add(1);
    channels.add(0xFF);

    for(Integer curChannel : channels)
    {
      channelNumber = curChannel;
      payload = createPayload(
          isBidirectional, isResponse, requestType, commandType,
          channelNumber, manufacturerID, eepRORG, eepFunc, eepType
      );
      Esp3UTEQueryTelegram query = new Esp3UTEQueryTelegram(
          createDataGroup(senderID, payload, status), optData
      );

      assertTelegramAttributes(
          query, senderID, status, isBidirectional, isResponse, requestType,
          commandType, channelNumber, manufacturerID, eepRORG, eepFunc, eepType
      );
    }
  }

  @Test public void testRORG() throws Exception
  {
    ArrayList<Integer> rorgs = new ArrayList<Integer>();
    rorgs.add(0);
    rorgs.add(0xFF);

    for(Integer curRORG : rorgs)
    {
      eepRORG = curRORG;

      payload = createPayload(
          isBidirectional, isResponse, requestType, commandType,
          channelNumber, manufacturerID, eepRORG, eepFunc, eepType
      );
      Esp3UTEQueryTelegram query = new Esp3UTEQueryTelegram(
          createDataGroup(senderID, payload, status), optData
      );

      assertTelegramAttributes(
          query, senderID, status, isBidirectional, isResponse, requestType,
          commandType, channelNumber, manufacturerID, eepRORG, eepFunc, eepType
      );
    }
  }

  @Test public void testFunc() throws Exception
  {
    ArrayList<Integer> funcs = new ArrayList<Integer>();
    funcs.add(0);
    funcs.add(0xFF);

    for(Integer curFunc : funcs)
    {
      eepFunc = curFunc;

      payload = createPayload(
          isBidirectional, isResponse, requestType, commandType,
          channelNumber, manufacturerID, eepRORG, eepFunc, eepType
      );
      Esp3UTEQueryTelegram query = new Esp3UTEQueryTelegram(
          createDataGroup(senderID, payload, status), optData
      );

      assertTelegramAttributes(
          query, senderID, status, isBidirectional, isResponse, requestType,
          commandType, channelNumber, manufacturerID, eepRORG, eepFunc, eepType
      );
    }
  }

  @Test public void testType() throws Exception
  {
    ArrayList<Integer> types = new ArrayList<Integer>();
    types.add(0);
    types.add(0xFF);

    for(Integer curType : types)
    {
      eepType = curType;

      payload = createPayload(
          isBidirectional, isResponse, requestType, commandType,
          channelNumber, manufacturerID, eepRORG, eepFunc, eepType
      );
      Esp3UTEQueryTelegram query = new Esp3UTEQueryTelegram(
          createDataGroup(senderID, payload, status), optData
      );

      assertTelegramAttributes(
          query, senderID, status, isBidirectional, isResponse, requestType,
          commandType, channelNumber, manufacturerID, eepRORG, eepFunc, eepType
      );
    }
  }

  @Test public void testManufacturer() throws Exception
  {
    ArrayList<Integer> manufacturerIDs = new ArrayList<Integer>();

    manufacturerIDs.add(0);
    manufacturerIDs.add(0xFF);
    manufacturerIDs.add(0x100);
    manufacturerIDs.add(0x1FF);

    manufacturerIDs.add(Manufacturer.PEHA.getID());
    manufacturerIDs.add(Manufacturer.HONEYWELL.getID());
    manufacturerIDs.add(Manufacturer.SPARTAN.getID());
    manufacturerIDs.add(Manufacturer.MULTI_USER_MANUFACTURER.getID());


    for(Integer curManufacturereID : manufacturerIDs)
    {
      manufacturerID = curManufacturereID;

      payload = createPayload(
          isBidirectional, isResponse, requestType, commandType,
          channelNumber, manufacturerID, eepRORG, eepFunc, eepType
      );
      Esp3UTEQueryTelegram query = new Esp3UTEQueryTelegram(
          createDataGroup(senderID, payload, status), optData
      );

      assertTelegramAttributes(
          query, senderID, status, isBidirectional, isResponse, requestType,
          commandType, channelNumber, manufacturerID, eepRORG, eepFunc, eepType
      );
    }
  }

  @Test public void testEepType() throws Exception
  {
    EepType type = EepType.EEP_TYPE_F60201;

    eepRORG = type.getRORG().getValue() & 0xFF;
    eepFunc = type.getFunc();
    eepType = type.getType();

    payload = createPayload(
        isBidirectional, isResponse, requestType, commandType,
        channelNumber, manufacturerID, eepRORG, eepFunc, eepType
    );
    Esp3UTEQueryTelegram query = new Esp3UTEQueryTelegram(
        createDataGroup(senderID, payload, status), optData
    );

    assertTelegramAttributes(
        query, senderID, status, isBidirectional, isResponse, requestType,
        commandType, channelNumber, manufacturerID, eepRORG, eepFunc, eepType
    );

    Assert.assertEquals(type, query.getEep());


    // Invalid RORG value

    eepRORG = 0;

    payload = createPayload(
      isBidirectional, isResponse, requestType, commandType,
      channelNumber, manufacturerID, eepRORG, eepFunc, eepType
    );
    query = new Esp3UTEQueryTelegram(
      createDataGroup(senderID, payload, status), optData
    );

    assertTelegramAttributes(
      query, senderID, status, isBidirectional, isResponse, requestType,
      commandType, channelNumber, manufacturerID, eepRORG, eepFunc, eepType
    );

    Assert.assertNull(query.getEep());


    // Unsupported EEP

    type = EepType.EEP_TYPE_F60201;

    eepRORG = type.getRORG().getValue() & 0xFF;
    eepFunc = type.getFunc();
    eepType = 0xFF;

    payload = createPayload(
            isBidirectional, isResponse, requestType, commandType,
            channelNumber, manufacturerID, eepRORG, eepFunc, eepType
    );
    query = new Esp3UTEQueryTelegram(
        createDataGroup(senderID, payload, status), optData
    );

    assertTelegramAttributes(
        query, senderID, status, isBidirectional, isResponse, requestType,
        commandType, channelNumber, manufacturerID, eepRORG, eepFunc, eepType
    );

    Assert.assertNull(query.getEep());
  }


  // Helpers --------------------------------------------------------------------------------------

  private void assertTelegramAttributes(Esp3UTEQueryTelegram query, DeviceID deviceID, int status,
                                        Boolean isBidirectional,Boolean isResponse, int requestType,
                                        int commandType, int channelNumber, int manufacturerID,
                                        int eepRORG, int eepFunc, int eepType)
  {
    Assert.assertEquals(Esp3PacketHeader.PacketType.RADIO, query.getPacketType());
    Assert.assertEquals(AbstractEsp3RadioTelegram.RORG.UTE, query.getRORG());
    Assert.assertEquals(deviceID, query.getSenderID());
    Assert.assertEquals(status, query.getStatusByte());

    Assert.assertEquals(isBidirectional, query.isBidirectionalEEP());
    Assert.assertEquals(isResponse, query.isResponseExpected());
    Assert.assertEquals(Esp3UTEQueryTelegram.QueryType.resolve(requestType), query.getQueryType());
    Assert.assertEquals(Esp3UTEQueryTelegram.CmdType.resolve(commandType), query.getCommandType());
    Assert.assertEquals(channelNumber, query.getChannelNumber());
    Assert.assertEquals(Manufacturer.fromID(manufacturerID), query.getManufacturer());

    Assert.assertEquals(eepRORG, query.getEepRORGValue());
    Assert.assertEquals(eepFunc, query.getEepFuncValue());
    Assert.assertEquals(eepType, query.getEepTypeValue());
  }

  private byte[] createTelegramAsByteArray(DeviceID deviceID, byte[] payload, byte status)
  {
    int dataGroupLength = 13;

    int length = Esp3PacketHeader.ESP3_HEADER_SIZE + dataGroupLength +
        Esp3RadioTelegramOptData.ESP3_RADIO_OPT_DATA_LENGTH + 1;


    byte[] packetBytes = new byte[length];

    // Header ...

    Esp3PacketHeader header = new Esp3PacketHeader(
        Esp3PacketHeader.PacketType.RADIO, dataGroupLength,
        Esp3RadioTelegramOptData.ESP3_RADIO_OPT_DATA_LENGTH
    );

    int copyIndex = 0;

    System.arraycopy(
        header.asByteArray(), 0, packetBytes, copyIndex,
        Esp3PacketHeader.ESP3_HEADER_SIZE
    );

    // Data group ...

    copyIndex += Esp3PacketHeader.ESP3_HEADER_SIZE;

    System.arraycopy(
        createDataGroup(deviceID, payload, status), 0, packetBytes,
        copyIndex, dataGroupLength
    );

    // Optional data group ...

    copyIndex += dataGroupLength;

    System.arraycopy(
        Esp3RadioTelegramOptData.ESP3_RADIO_OPT_DATA_TX.asByteArray(), 0, packetBytes,
        copyIndex, Esp3RadioTelegramOptData.ESP3_RADIO_OPT_DATA_LENGTH
    );

    // CRC8D ...

    packetBytes[packetBytes.length - 1] = Esp3Packet.CRC8.calculate(
        packetBytes, Esp3PacketHeader.ESP3_HEADER_SIZE,
        packetBytes.length - Esp3PacketHeader.ESP3_HEADER_SIZE - 1
    );

    return packetBytes;
  }

  private byte[] createDataGroup(DeviceID deviceID, byte[] payload, byte status)
  {
    int dataGroupLength = 13;

    byte[] dataGroupBytes = new byte[dataGroupLength];

    // RORG...

    dataGroupBytes[0] = AbstractEsp3RadioTelegram.RORG.UTE.getValue();

    // Payload...

    System.arraycopy(payload, 0, dataGroupBytes, 1, payload.length);

    // Sender ID...

    System.arraycopy(
        deviceID.asByteArray(), 0, dataGroupBytes,
        payload.length + 1, DeviceID.ENOCEAN_ESP_ID_LENGTH
    );

    // Status...

    dataGroupBytes[dataGroupBytes.length - 1] = status;

    return dataGroupBytes;
  }

  private byte[] createPayload(boolean isBidirectional, boolean isResponseExpected, int requestType, int commandType,
                               int channelNumber, int manufacturerID, int eepRORG, int eepFunc, int eepType)
  {
    byte[] payload = new byte[7];

    payload[0] |= (byte)(isBidirectional ? 0x80 : 0x00);
    payload[0] |= (byte)(isResponseExpected ? 0x00 : 0x40);
    payload[0] |= (byte)(requestType << 4);
    payload[0] |= (byte)commandType;

    // Channel number...

    payload[1] = (byte)channelNumber;

    // Manufacturer ID (MID)...

    payload[2] = (byte)(manufacturerID & 0xFF);
    payload[3] = (byte)((manufacturerID & 0x700) >> 8);

    // EEP...

    payload[4] = (byte)eepType;
    payload[5] = (byte)eepFunc;
    payload[6] = (byte)eepRORG;

    return payload;
  }
}
