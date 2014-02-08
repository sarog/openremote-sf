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

import org.openremote.controller.protocol.enocean.DeviceID;
import org.openremote.controller.protocol.enocean.datatype.Ordinal;
import org.openremote.controller.protocol.enocean.profile.EepData;
import org.openremote.controller.protocol.enocean.profile.EepDataField;
import org.openremote.controller.protocol.enocean.profile.EepOutOfRangeException;
import org.openremote.controller.protocol.enocean.profile.EepType;

/**
 * Represents a universal teach-in (UTE) response radio telegram as defined in the EnOcean
 * equipment profiles (EEP) 2.5 specification chapter: 3.6) UTE - Universal Uni- and
 * Bidirectional Teach-in.<p>
 *
 * The EnOcean equipment profiles (EEP) 2.5 specification defines the UTE response
 * packet structure as follows:
 *
 * <pre>
 *                                           |-------------------- Data ...
 *         +--------+------...------+--------+--------+-----...-----+-- ...
 *         |  Sync  |    Header     |  CRC8  | Choice |   Payload   |
 *         |  Byte  |               | Header | (RORG) |             |
 *         +--------+------...------+--------+--------+-----...-----+-- ...
 *           1 byte      4 bytes      1 byte   1 byte     7 bytes
 *
 *   ... Group ---------------------|
 *   ... --+------...------+--------+----------...----------+-------+
 *         |     Sender    | Status |        Optional       |  CRC8 |
 *         |      ID       |        |          Data         |  Data |
 *   ... --+------...------+--------+----------...----------+-------+
 *              4 bytes      1 byte          n bytes          1 byte
 *
 *
 *         7 Byte Payload
 *         ==============
 *
 *         |0x00 - Unidirectional communication
 *         |0x01 - Bidirectional communication
 *         | |Not used
 *         | | |0x00 - Request not accepted, general reason
 *         | | |0x01 - Request accepted, teach-in successful
 *         | | |0x02 - Request accepted, deletion of teach-in successful
 *         | | |0x03 - Request not accepted, EEP not supported
 *         | | |   |
 *         +-+-+---+-------+---------------+---------------+---------+-----+--...
 *         | | |   |  CMD  | Channel Number|   MID-8LSB    |         |MID- |
 *         | | |   |  0x1  |               |               |         |3MSB |
 *         +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+--...
 *  bits   |7|6|5|4|3|2|1|0|7|6|5|4|3|2|1|0|7|6|5|4|3|2|1|0|7|6|5|4|3|2|1|0|
 *         +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+--...
 *  offset |0              |8              |16             |24             |
 *         +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+--...
 *  byte   |     DB_6      |     DB_5      |     DB_4      |     DB_3      |
 *         +---------------+---------------+---------------+---------------+--...
 *
 *    ...--+---------------+---------------+---------------+
 *         |   EEP-Type    |    EEP-Func   |    EEP-RORG   |
 *         |               |               |               |
 *    ...--+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *  bits   |7|6|5|4|3|2|1|0|7|6|5|4|3|2|1|0|7|6|5|4|3|2|1|0|
 *    ...--+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *  offset |32             |40             |48             |
 *    ...--+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *  byte   |     DB_2      |     DB_1      |     DB_0      |
 *    ...--+---------------+---------------+---------------+
 *
 *
 * </pre>
 *
 * Universal teach-in (UTE) response radio telegrams have a 7 byte data payload field and a RORG
 * value of 0xD4. The most significant bit of the first byte indicates if the requested EnOcean
 * equipment profile (EEP) is based on a bidirectional or unidirectional communication. Bit 4 and
 * 5 indicate if the universal teach-in (UTE) query was successful. The least significant nibble
 * of the first byte is the CMD field and contains always the value 0x01 to indicate that it's a
 * UTE response telegram. The second byte is used to select a certain device channel. A value of 0xFF
 * means that the UTE response is valid for all channels. The third and fourth byte form the
 * manufacture ID. The third byte contains the 8 least significant bits of the manufacturer ID.
 * The fourth byte contains the 3 most significant bits of the manufacture ID. The last 3 bytes
 * determine the requested EnOcean equipment profile (EEP)
 * (see {@link org.openremote.controller.protocol.enocean.profile.EepType}).
 *
 * @see Esp3UTEQueryTelegram
 * @see org.openremote.controller.protocol.enocean.packet.Esp3PacketHeader
 * @see Esp3RadioTelegramOptData
 *
 *
 * @author <a href="mailto:rainer@openremote.org">Rainer Hitz</a>
 */
public class Esp3UTEResponseTelegram extends AbstractEsp3RadioTelegram
{

  // Enums ----------------------------------------------------------------------------------------

  /**
   * Universal teach-in (UTE) status codes.
   */
  public enum StatusCode
  {
    /**
     * Teach-in query failed.
     */
    FAILURE(0x00),

    /**
     * Teach-in query was successful.
     */
    SUCCESSFUL_TEACH_IN(0x01),

    /**
     * Teach-in deletion query was successful.
     */
    SUCCESSFUL_TEACH_IN_DELETE(0x02),

    /**
     * Teach-in query failed because EnOcean equipment profile (EEP) is not supported.
     */
    FAILURE_EEP_NOT_SUPPORTED(0x03);


    // Members ------------------------------------------------------------------------------------

    public static StatusCode resolve(int value)
    {
      StatusCode[] allCodes = StatusCode.values();

      byte responseTypeByte = (byte)(value & 0xFF);

      for (StatusCode statusCode : allCodes)
      {
        if (statusCode.value == responseTypeByte)
        {
          return statusCode;
        }
      }

      return FAILURE;
    }

    private byte value;

    private StatusCode(int value)
    {
      this.value = (byte)(value & 0xFF);
    }

    byte getValue()
    {
      return value;
    }
  }


  // Constants ------------------------------------------------------------------------------------

  /**
   * Byte size of universal teach-in (UTE) response payload field: {@value}
   */
  public static final int UTE_RESPONSE_PAYLOAD_LENGTH = 7;

  /**
   * Byte size of data group: {@value}
   */
  public static final int UTE_RESPONSE_DATA_LENGTH =
      ESP3_RADIO_MIN_DATA_LENGTH +
      UTE_RESPONSE_PAYLOAD_LENGTH;

  /**
   * Communication type data field name.
   */
  static final String UTE_RESPONSE_EEP_COMM_TYPE_FIELD_NAME = "EEP_COMMUNICATION_TYPE";

  /**
   * Start bit of communication type data field.
   */
  static final int UTE_RESPONSE_EEP_COMM_TYPE_OFFSET = 0;

  /**
   * Bit size of communication type data field.
   */
  static final int UTE_RESPONSE_EEP_COMM_TYPE_SIZE = 1;

  /**
   * Status code data field name.
   */
  static final String UTE_STATUS_CODE_DATA_FIELD_NAME = "UTE_STATUS_CODE";

  /**
   * Start bit of status code data field.
   */
  static final int UTE_STATUS_CODE_OFFSET = 2;

  /**
   * Bit size of status code data field.
   */
  static final int UTE_STATUS_CODE_SIZE = 2;

  /**
   * Command data field name.
   */
  static final String UTE_RESPONSE_CMD_FIELD_NAME = "COMMAND_IDENTIFIER";

  /**
   * Start bit of command data field.
   */
  static final int UTE_RESPONSE_CMD_OFFSET = 4;

  /**
   * Bit size of command data field.
   */
  static final int UTE_RESPONSE_CMD_SIZE = 4;

  /**
   * Channel number data field name.
   */
  static final String UTE_RESPONSE_CHANNEL_FIELD_NAME = "CHANNEL_NUMBER";

  /**
   * Start bit of channel number data field.
   */
  static final int UTE_RESPONSE_CHANNEL_OFFSET = 8;

  /**
   * Start bit of channel number data field.
   */
  static final int UTE_RESPONSE_CHANNEL_SIZE = 8;

  /**
   * Manufacturer ID data field (least significant bits) name.
   */
  static final String UTE_RESPONSE_MID_8LSB_FIELD_NAME = "MID_8LSB";

  /**
   * Start bit of manufacturer ID data field (least significant bits).
   */
  static final int UTE_RESPONSE_MID_8LSB_OFFSET = 16;

  /**
   * Bit size of manufacturer ID data field (least significant bits).
   */
  static final int UTE_RESPONSE_MID_8LSB_SIZE = 8;

  /**
   * Manufacturer ID data field (most significant bits) name.
   */
  static final String UTE_RESPONSE_MID_3MSB_FIELD_NAME = "MID_3MSB";

  /**
   * Start bit of manufacturer ID data field (most significant bits).
   */
  static final int UTE_RESPONSE_MID_3MSB_OFFSET = 29;

  /**
   * Bit size of manufacturer ID data field (most significant bits).
   */
  static final int UTE_RESPONSE_MID_3MSB_SIZE = 3;

  /**
   * EnOcean equipment profile type data field name.
   */
  static final String UTE_RESPONSE_EEP_TYPE_FIELD_NAME = "EEP_TYPE";

  /**
   * Start bit of EnOcean equipment profile type data field.
   */
  static final int UTE_RESPONSE_EEP_TYPE_OFFSET = 32;

  /**
   * Bit size of EnOcean equipment profile type data field.
   */
  static final int UTE_RESPONSE_EEP_TYPE_SIZE = 8;

  /**
   * EnOcean equipment profile function data field name.
   */
  static final String UTE_RESPONSE_EEP_FUNC_FIELD_NAME = "EEP_FUNC";

  /**
   * Start bit of EnOcean equipment profile function data field.
   */
  static final int UTE_RESPONSE_EEP_FUNC_OFFSET = 40;

  /**
   * Bit size of EnOcean equipment profile function data field.
   */
  static final int UTE_RESPONSE_EEP_FUNC_SIZE = 8;

  /**
   * EnOcean equipment profile RORG (choice) data field name.
   */
  static final String UTE_RESPONSE_EEP_RORG_FIELD_NAME = "EEP_RORG";

  /**
   * Start bit of EnOcean equipment profile RORG (choice) data field.
   */
  static final int UTE_RESPONSE_EEP_RORG_OFFSET = 48;

  /**
   * Bit size of EnOcean equipment profile RORG (choice) data field.
   */
  static final int UTE_RESPONSE_EEP_RORG_SIZE = 8;


  // Class Members --------------------------------------------------------------------------------

  private static byte[] createPayload(Esp3UTEQueryTelegram query, StatusCode statusCode)
  {
    EepData payload = new EepData(
            EepType.EEP_TYPE_A50201, UTE_RESPONSE_PAYLOAD_LENGTH,
            createCommunicationType(query),
            createStatusCode(statusCode),
            createTelegramType(),
            createChannel(query),
            createMid8LSB(query),
            createMid3MSB(query),
            createEepType(query),
            createEepFunc(query),
            createEepRORG(query)
    );

    byte[] payloadBytes = null;
    try
    {
      payloadBytes = payload.asByteArray();
    }
    catch (EepOutOfRangeException e)
    {
      throw new RuntimeException();
    }

    return payloadBytes;
  }

  private static Ordinal createCommunicationType(Esp3UTEQueryTelegram query)
  {
    EepDataField dataField = new EepDataField(
            UTE_RESPONSE_EEP_COMM_TYPE_FIELD_NAME, UTE_RESPONSE_EEP_COMM_TYPE_OFFSET, UTE_RESPONSE_EEP_COMM_TYPE_SIZE
    );
    Ordinal telegramType = new Ordinal(dataField, query.isBidirectionalEEP() ? 0x01 : 0x00);

    return telegramType;
  }

  private static Ordinal createStatusCode(StatusCode statusCode)
  {
    EepDataField dataField = new EepDataField(
            UTE_STATUS_CODE_DATA_FIELD_NAME, UTE_STATUS_CODE_OFFSET, UTE_STATUS_CODE_SIZE
    );
    Ordinal queryType = new Ordinal(dataField, (int)statusCode.value);

    return queryType;
  }

  private static Ordinal createTelegramType()
  {
    EepDataField dataField = new EepDataField(
            UTE_RESPONSE_CMD_FIELD_NAME, UTE_RESPONSE_CMD_OFFSET, UTE_RESPONSE_CMD_SIZE
    );
    Ordinal telegramType = new Ordinal(dataField, (int)Esp3UTEQueryTelegram.CmdType.UTE_RESPONSE.getValue());

    return telegramType;
  }

  private static Ordinal createChannel(Esp3UTEQueryTelegram query)
  {
    EepDataField dataField = new EepDataField(
            UTE_RESPONSE_CHANNEL_FIELD_NAME, UTE_RESPONSE_CHANNEL_OFFSET, UTE_RESPONSE_CHANNEL_SIZE
    );
    Ordinal channel = new Ordinal(dataField, query.getChannelNumber());

    return channel;
  }

  private static Ordinal createMid8LSB(Esp3UTEQueryTelegram query)
  {
    EepDataField dataField = new EepDataField(
            UTE_RESPONSE_MID_8LSB_FIELD_NAME, UTE_RESPONSE_MID_8LSB_OFFSET, UTE_RESPONSE_MID_8LSB_SIZE
    );

    Ordinal mid8LSB = new Ordinal(dataField, query.getManufacturer().getID() & 0xFF);

    return mid8LSB;
  }

  private static Ordinal createMid3MSB(Esp3UTEQueryTelegram query)
  {
    EepDataField dataField = new EepDataField(
            UTE_RESPONSE_MID_3MSB_FIELD_NAME, UTE_RESPONSE_MID_3MSB_OFFSET, UTE_RESPONSE_MID_3MSB_SIZE
    );

    Ordinal mid3MSB = new Ordinal(dataField, (query.getManufacturer().getID() & 0x700) >> 8);

    return mid3MSB;
  }

  private static Ordinal createEepType(Esp3UTEQueryTelegram query)
  {
    EepDataField dataField = new EepDataField(
            UTE_RESPONSE_EEP_TYPE_FIELD_NAME, UTE_RESPONSE_EEP_TYPE_OFFSET, UTE_RESPONSE_EEP_TYPE_SIZE
    );
    Ordinal type = new Ordinal(dataField, query.getEepTypeValue());

    return type;
  }

  private static Ordinal createEepFunc(Esp3UTEQueryTelegram query)
  {
    EepDataField dataField = new EepDataField(
            UTE_RESPONSE_EEP_FUNC_FIELD_NAME, UTE_RESPONSE_EEP_FUNC_OFFSET, UTE_RESPONSE_EEP_FUNC_SIZE
    );
    Ordinal func = new Ordinal(dataField, query.getEepFuncValue());

    return func;
  }

  private static Ordinal createEepRORG(Esp3UTEQueryTelegram query)
  {
    EepDataField dataField = new EepDataField(
            UTE_RESPONSE_EEP_RORG_FIELD_NAME, UTE_RESPONSE_EEP_RORG_OFFSET, UTE_RESPONSE_EEP_RORG_SIZE
    );
    Ordinal rorg = new Ordinal(dataField, query.getEepRORGValue());

    return rorg;
  }


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs an universal teach-in (UTE) response radio telegram based on the response status
   * code, sender ID and universal teach-in (UTE) query.
   *
   * @param statusCode  response status code
   *
   * @param senderID    EnOcean sender ID
   *
   * @param query       Universal teach-in (UTE) query
   */
  public Esp3UTEResponseTelegram(StatusCode statusCode, DeviceID senderID, Esp3UTEQueryTelegram query)
  {
    super(
        RORG.UTE, UTE_RESPONSE_PAYLOAD_LENGTH,
        AbstractEsp3RadioTelegram.createDataGroup(
            RORG.UTE, UTE_RESPONSE_PAYLOAD_LENGTH, senderID,
            createPayload(query, statusCode), (byte)0x00
        ),
        Esp3RadioTelegramOptData.createOptDataADT(query.getSenderID()).asByteArray()
    );
  }

}
