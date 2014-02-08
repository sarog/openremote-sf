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
import org.openremote.controller.protocol.enocean.packet.Esp3Packet;
import org.openremote.controller.protocol.enocean.packet.Esp3PacketHeader;

import java.util.ArrayList;

/**
 * Unit tests for {@link Esp3UTEQueryTelegram} class.
 *
 * @author <a href="mailto:rainer@openremote.org">Rainer Hitz</a>
 */
public class Esp3UTEResponseTelegramTest
{
  private DeviceID senderID;
  private DeviceID destinationID;
  private byte[] optData;
  private byte[] queryPayload;
  private byte status;

  private boolean isBidirectional;
  private boolean isResponse;
  private int requestType;
  private Esp3UTEResponseTelegram.StatusCode statusCode;
  private int channelNumber;
  private int manufacturerID;
  private int eepRORG;
  private int eepFunc;
  private int eepType;


  // Test Lifecycle -------------------------------------------------------------------------------

  @Before public void setUp() throws Exception
  {
    senderID = DeviceID.fromString("0xFF800001");
    destinationID = DeviceID.fromString("0xFF800002");
    status = 0;

    isBidirectional = true;
    isResponse = true;
    requestType = 0;
    statusCode = Esp3UTEResponseTelegram.StatusCode.SUCCESSFUL_TEACH_IN;
    channelNumber = 1;
    manufacturerID = 0x7FF;
    eepRORG = 0xF6;
    eepFunc = 0x02;
    eepType = 0x01;


    optData = Esp3RadioTelegramOptData.ESP3_RADIO_OPT_DATA_TX.asByteArray();
  }


  // Tests ----------------------------------------------------------------------------------------

  @Test public void testBasicConstruction() throws Exception
  {
    queryPayload = createQueryPayload(
        isBidirectional, isResponse, requestType, channelNumber,
        manufacturerID, eepRORG, eepFunc, eepType
    );

    Esp3UTEQueryTelegram query = new Esp3UTEQueryTelegram(
        createDataGroup(destinationID, queryPayload, status), optData
    );

    Esp3UTEResponseTelegram response = new Esp3UTEResponseTelegram(statusCode, senderID, query);

    assertResponseTelegram(
        response, senderID, destinationID, isBidirectional, statusCode.getValue() & 0xFF,
        channelNumber, manufacturerID, eepRORG, eepFunc, eepType
    );
  }

  @Test public void testCommunicationFlag() throws Exception
  {
    isBidirectional = true;

    queryPayload = createQueryPayload(
        isBidirectional, isResponse, requestType, channelNumber,
        manufacturerID, eepRORG, eepFunc, eepType
    );

    Esp3UTEQueryTelegram query = new Esp3UTEQueryTelegram(
        createDataGroup(destinationID, queryPayload, status), optData
    );

    Esp3UTEResponseTelegram response = new Esp3UTEResponseTelegram(statusCode, senderID, query);

    assertResponseTelegram(
       response, senderID, destinationID, isBidirectional, statusCode.getValue() & 0xFF,
       channelNumber, manufacturerID, eepRORG, eepFunc, eepType
    );


    isBidirectional = false;

    queryPayload = createQueryPayload(
        isBidirectional, isResponse, requestType, channelNumber,
        manufacturerID, eepRORG, eepFunc, eepType
    );

    query = new Esp3UTEQueryTelegram(
        createDataGroup(destinationID, queryPayload, status), optData
    );

    response = new Esp3UTEResponseTelegram(statusCode, senderID, query);

    assertResponseTelegram(
        response, senderID, destinationID, isBidirectional, statusCode.getValue() & 0xFF,
        channelNumber, manufacturerID, eepRORG, eepFunc, eepType
    );
  }

  @Test public void testStatusCode() throws Exception
  {

    for(Esp3UTEResponseTelegram.StatusCode curStatusCode : Esp3UTEResponseTelegram.StatusCode.values())
    {
      statusCode = curStatusCode;

      queryPayload = createQueryPayload(
          isBidirectional, isResponse, requestType, channelNumber,
          manufacturerID, eepRORG, eepFunc, eepType
      );

      Esp3UTEQueryTelegram query = new Esp3UTEQueryTelegram(
          createDataGroup(destinationID, queryPayload, status), optData
      );

      Esp3UTEResponseTelegram response = new Esp3UTEResponseTelegram(statusCode, senderID, query);

      assertResponseTelegram(
          response, senderID, destinationID, isBidirectional, statusCode.getValue() & 0xFF,
          channelNumber, manufacturerID, eepRORG, eepFunc, eepType
      );
    }
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

      queryPayload = createQueryPayload(
          isBidirectional, isResponse, requestType, channelNumber,
          manufacturerID, eepRORG, eepFunc, eepType
      );

      Esp3UTEQueryTelegram query = new Esp3UTEQueryTelegram(
          createDataGroup(destinationID, queryPayload, status), optData
      );

      Esp3UTEResponseTelegram response = new Esp3UTEResponseTelegram(statusCode, senderID, query);

      assertResponseTelegram(
          response, senderID, destinationID, isBidirectional, statusCode.getValue() & 0xFF,
          channelNumber, manufacturerID, eepRORG, eepFunc, eepType
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

      queryPayload = createQueryPayload(
          isBidirectional, isResponse, requestType, channelNumber,
          manufacturerID, eepRORG, eepFunc, eepType
      );

      Esp3UTEQueryTelegram query = new Esp3UTEQueryTelegram(
          createDataGroup(destinationID, queryPayload, status), optData
      );

      Esp3UTEResponseTelegram response = new Esp3UTEResponseTelegram(statusCode, senderID, query);

      assertResponseTelegram(
          response, senderID, destinationID, isBidirectional, statusCode.getValue() & 0xFF,
          channelNumber, manufacturerID, eepRORG, eepFunc, eepType
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

      queryPayload = createQueryPayload(
          isBidirectional, isResponse, requestType, channelNumber,
          manufacturerID, eepRORG, eepFunc, eepType
      );

      Esp3UTEQueryTelegram query = new Esp3UTEQueryTelegram(
          createDataGroup(destinationID, queryPayload, status), optData
      );

      Esp3UTEResponseTelegram response = new Esp3UTEResponseTelegram(statusCode, senderID, query);

      assertResponseTelegram(
          response, senderID, destinationID, isBidirectional, statusCode.getValue() & 0xFF,
          channelNumber, manufacturerID, eepRORG, eepFunc, eepType
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

      queryPayload = createQueryPayload(
          isBidirectional, isResponse, requestType, channelNumber,
          manufacturerID, eepRORG, eepFunc, eepType
      );

      Esp3UTEQueryTelegram query = new Esp3UTEQueryTelegram(
          createDataGroup(destinationID, queryPayload, status), optData
      );

      Esp3UTEResponseTelegram response = new Esp3UTEResponseTelegram(statusCode, senderID, query);

      assertResponseTelegram(
          response, senderID, destinationID, isBidirectional, statusCode.getValue() & 0xFF,
          channelNumber, manufacturerID, eepRORG, eepFunc, eepType
      );
    }
  }


  // Helpers --------------------------------------------------------------------------------------

  private void assertResponseTelegram(Esp3UTEResponseTelegram telegram, DeviceID senderID,
                                      DeviceID destinationID, boolean isBidirectional,
                                      int statusCode, int channelNumber, int manufacturerID,
                                      int eepRORG, int eepFunc, int eepType)
  {
    byte[] actualTelegramBytes = telegram.asByteArray();
    byte[] expectedTelegramBytes = createResponseTelegramAsByteArray(
            senderID, destinationID, isBidirectional, statusCode,
            channelNumber, manufacturerID, eepRORG, eepFunc, eepType
    );

    Assert.assertArrayEquals(expectedTelegramBytes, actualTelegramBytes);
  }

  private byte[] createResponseTelegramAsByteArray(DeviceID senderID, DeviceID destinationID,
                                                   boolean isBidirectional, int statusCode,
                                                   int channelNumber, int manufacturerID,
                                                   int eepRORG, int eepFunc, int eepType)
  {
    byte[] payload = createResponsePayload(
            isBidirectional, statusCode, channelNumber,
            manufacturerID, eepRORG, eepFunc, eepType
    );

    byte[] optData = createResponseOptDataGroup(destinationID);

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
        createDataGroup(senderID, payload, status), 0, packetBytes,
        copyIndex, dataGroupLength
    );

    // Optional data group ...

    copyIndex += dataGroupLength;

    System.arraycopy(
        optData, 0, packetBytes,
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

  private byte[] createQueryPayload(boolean isBidirectional, boolean isResponseExpected, int requestType,
                                    int channelNumber, int manufacturerID, int eepRORG, int eepFunc, int eepType)
  {
    byte[] payload = new byte[7];

    payload[0] |= (byte)(isBidirectional ? 0x80 : 0x00);
    payload[0] |= (byte)(isResponseExpected ? 0x00 : 0x40);
    payload[0] |= (byte)(requestType << 4);
    payload[0] |= (byte)0; // command

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

  private byte[] createResponsePayload(boolean isBidirectional, int statusCode, int channelNumber,
                                       int manufacturerID, int eepRORG, int eepFunc, int eepType)
  {
    byte[] payload = new byte[7];

    payload[0] |= (byte)(isBidirectional ? 0x80 : 0x00);
    payload[0] |= (byte)(statusCode << 4);
    payload[0] |= (byte)1;  // command

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

  private byte[] createResponseOptDataGroup(DeviceID destinationID)
  {
    byte[] optData = Esp3RadioTelegramOptData.ESP3_RADIO_OPT_DATA_TX.asByteArray();

    System.arraycopy(
        destinationID.asByteArray(), 0, optData,
        1, DeviceID.ENOCEAN_ESP_ID_LENGTH
    );

    return optData;
  }
}

