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

import org.openremote.controller.protocol.enocean.Manufacturer;
import org.openremote.controller.protocol.enocean.datatype.Bool;
import org.openremote.controller.protocol.enocean.datatype.Ordinal;
import org.openremote.controller.protocol.enocean.profile.EepData;
import org.openremote.controller.protocol.enocean.profile.EepDataField;
import org.openremote.controller.protocol.enocean.profile.EepType;

/**
 * Represents a universal teach-in (UTE) query radio telegram as defined in the EnOcean
 * equipment profiles (EEP) 2.5 specification chapter: 3.6) UTE - Universal Uni- and
 * Bidirectional Teach-in. <p>
 *
 * The EnOcean equipment profiles (EEP) 2.5 specification defines the UTE query
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
 *         | |0x00 - EEP teach-in response expected
 *         | |0x01 - *NO* EEP teach-in response expected
 *         | | |0x00 - Teach-in request
 *         | | |0x01 - Teach-in deletion request
 *         | | |0x02 - Teach-in or deletion of teach-in not specified
 *         | | |0x03 - Not used
 *         | | |   |
 *         +-+-+---+-------+---------------+---------------+---------+-----+--...
 *         | | |   |  CMD  | Channel Number|   MID-8LSB    |         |MID- |
 *         | | |   |  0x0  |               |               |         |3MSB |
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
 * Universal teach-in (UTE) query radio telegrams have a 7 byte data payload field and a RORG
 * value of 0xD4. The most significant bit of the first byte indicates if the requested EnOcean
 * equipment profile (EEP) is based on a bidirectional or unidirectional communication. Bit 6
 * of the first byte indicates if a universal teach-in (UTE) response is expected. With the
 * mentioned 2 most significant bits it's possible to create typical combinations (unidirectional
 * EEP communication + unidirectional teach-in or bidirectional EEP communication + bidirectional
 * teach-in) but also support a unidirectional EEP communication combined with a bidirectional
 * teach-in process. The least significant nibble of the first byte is the CMD field and contains
 * always the value 0x00 to indicate that it's a UTE query telegram. The second byte is used
 * to select a certain device channel. A value of 0xFF means that the teach-in query applies
 * to all channels, supported by the device, at once. The third and fourth byte form the manufacture ID.
 * The third byte contains the 8 least significant bits of the manufacturer ID. The fourth byte
 * contains the 3 most significant bits of the manufacture ID. The last 3 bytes determine the
 * requested EnOcean equipment profile (EEP) (see {@link org.openremote.controller.protocol.enocean.profile.EepType}).
 *
 * @see Esp3UTEResponseTelegram
 * @see org.openremote.controller.protocol.enocean.packet.Esp3PacketHeader
 * @see Esp3RadioTelegramOptData
 *
 *
 * @author <a href="mailto:rainer@openremote.org">Rainer Hitz</a>
 */
public class Esp3UTEQueryTelegram extends AbstractEsp3RadioTelegram
{

  // Enums ----------------------------------------------------------------------------------------

  /**
   * Universal teach-in (UTE) query type.
   */
  public enum QueryType
  {
    /**
     * Teach-in request.
     */
    TEACH_IN(0x00),

    /**
     * Teach-in deletion request.
     */
    DELETE(0x01),

    /**
     * Teach-in or deletion of teach-in, not specified.
     */
    TEACH_IN_OR_DELETE(0x02),

    /**
     * Unknown query type.
     */
    UNKNOWN(0x03);


    // Members ------------------------------------------------------------------------------------

    public static QueryType resolve(int value)
    {
      QueryType[] allTypes = QueryType.values();

      byte queryTypeByte = (byte)(value & 0xFF);

      for (QueryType queryType : allTypes)
      {
        if (queryType.value == queryTypeByte)
        {
          return queryType;
        }
      }

      return UNKNOWN;
    }

    private byte value;

    private QueryType(int value)
    {
      this.value = (byte)(value & 0xFF);
    }
  }

  /**
   * Universal teach-in (UTE) command type.
   */
  public enum CmdType
  {
    /**
     * Universal teach-in (UTE) query.
     */
    UTE_QUERY(0x00),

    /**
     * Universal teach-in (UTE) response.
     */
    UTE_RESPONSE(0x01),

    /**
     * Unknown universal teach-in (UTE) command.
     */
    UNKNOWN(0x02);


    // Members ------------------------------------------------------------------------------------

    public static CmdType resolve(int value)
    {
      CmdType[] allTypes = CmdType.values();

      byte cmdTypeByte = (byte)(value & 0xFF);

      for (CmdType cmdType : allTypes)
      {
        if (cmdType.value == cmdTypeByte)
        {
          return cmdType;
        }
      }

      return UNKNOWN;
    }

    private byte value;

    private CmdType(int value)
    {
      this.value = (byte)(value & 0xFF);
    }

    public byte getValue()
    {
      return value;
    }
  }


  // Constants ------------------------------------------------------------------------------------

  /**
   * Byte size of universal teach-in (UTE) query payload field: {@value}
   */
  public static final int UTE_QUERY_PAYLOAD_LENGTH = 7;

  /**
   * Byte size of universal teach-in (UTE) query data group field: {@value}
   */
  public static final int UTE_QUERY_DATA_LENGTH =
      ESP3_RADIO_MIN_DATA_LENGTH +
      UTE_QUERY_PAYLOAD_LENGTH;


  /**
   * Universal teach-in (UTE) query refers to all channels.
   */
  public static final int UTE_QUERY_ALL_CHANNELS = 0xFF;

  /**
   * Communication type data field name.
   */
  static final String UTE_QUERY_EEP_COMM_TYPE_FIELD_NAME = "EEP_COMMUNICATION_TYPE";

  /**
   * Start bit of communication type data field.
   */
  static final int UTE_QUERY_EEP_COMM_TYPE_OFFSET = 0;

  /**
   * Bit size of communication type data field.
   */
  static final int UTE_QUERY_EEP_COMM_TYPE_SIZE = 1;

  /**
   * Communication type data field value indicating a bidirectional communication.
   */
  static final int UTE_QUERY_EEP_COMM_TYPE_BI_VALUE = 1;

  /**
   * Description for a communication data field value indicating a bidirectional communication.
   */
  static final String UTE_QUERY_EEP_COMM_TYPE_BI_DESC = "Bidirectional EEP";

  /**
   * Communication type data field value indicating an unidirectional communication.
   */
  static final int UTE_QUERY_EEP_COMM_TYPE_UNI_VALUE = 0;

  /**
   * Description for a communication data field value indicating an unidirectional communication.
   */
  static final String UTE_QUERY_EEP_COMM_TYPE_UNI_DESC = "Unidirectional EEP";

  /**
   * EEP teach-in response data field name.
   */
  static final String UTE_QUERY_RESPONSE_FIELD_NAME = "RESPONSE_EXPECTATION";

  /**
   * Start bit of EEP teach-in response data field.
   */
  static final int UTE_QUERY_RESPONSE_OFFSET = 1;

  /**
   * Bit size of EEP teach-in response data field.
   */
  static final int UTE_QUERY_RESPONSE_SIZE = 1;

  /**
   * EEP teach-in response data field value indicating that no response is expected.
   */
  static final int UTE_QUERY_RESPONSE_NOT_EXPECTED_VALUE = 1;

  /**
   * Description for a EEP teach-in response data field value indicating that no response
   * is expected.
   */
  static final String UTE_QUERY_RESPONSE_NOT_EXPECTED_DESC = "No teach-in response expected";

  /**
   * EEP teach-in response data field value indicating that a response is expected.
   */
  static final int UTE_QUERY_RESPONSE_EXPECTED_VALUE = 0;

  /**
   * Description for a EEP teach-in response data field value indicating that a response
   * is expected.
   */
  static final String UTE_QUERY_RESPONSE_EXPECTED_DESC = "Teach-in response expected";

  /**
   * Command data field name.
   */
  static final String UTE_QUERY_CMD_FIELD_NAME = "COMMAND_IDENTIFIER";

  /**
   * Start bit of command data field.
   */
  static final int UTE_QUERY_CMD_OFFSET = 4;

  /**
   * Bit size of command data field.
   */
  static final int UTE_QUERY_CMD_SIZE = 4;

  /**
   * Channel number data field name.
   */
  static final String UTE_QUERY_CHANNEL_FIELD_NAME = "CHANNEL_NUMBER";

  /**
   * Start bit of channel number data field.
   */
  static final int UTE_QUERY_CHANNEL_OFFSET = 8;

  /**
   * Bit size of channel number data field.
   */
  static final int UTE_QUERY_CHANNEL_SIZE = 8;

  /**
   * Manufacturer ID data field (least significant bits) name.
   */
  static final String UTE_QUERY_MID_8LSB_FIELD_NAME = "MID_8LSB";

  /**
   * Start bit of manufacturer ID data field (least significant bits).
   */
  static final int UTE_QUERY_MID_8LSB_OFFSET = 16;

  /**
   * Bit size of manufacturer ID data field (least significant bits).
   */
  static final int UTE_QUERY_MID_8LSB_SIZE = 8;

  /**
   * Manufacturer ID data field (most significant bits) name.
   */
  static final String UTE_QUERY_MID_3MSB_FIELD_NAME = "MID_3MSB";

  /**
   * Start bit of manufacturer ID data field (most significant bits).
   */
  static final int UTE_QUERY_MID_3MSB_OFFSET = 29;

  /**
   * Bit size of manufacturer ID data field (most significant bits).
   */
  static final int UTE_QUERY_MID_3MSB_SIZE = 3;

  /**
   * EnOcean equipment profile type data field name.
   */
  static final String UTE_QUERY_EEP_TYPE_FIELD_NAME = "EEP_TYPE";

  /**
   * Start bit of EnOcean equipment profile type data field.
   */
  static final int UTE_QUERY_EEP_TYPE_OFFSET = 32;

  /**
   * Bit size of EnOcean equipment profile type data field.
   */
  static final int UTE_QUERY_EEP_TYPE_SIZE = 8;

  /**
   * EnOcean equipment profile function data field name.
   */
  static final String UTE_QUERY_EEP_FUNC_FIELD_NAME = "EEP_FUNC";

  /**
   * Start bit of EnOcean equipment profile function data field.
   */
  static final int UTE_QUERY_EEP_FUNC_OFFSET = 40;

  /**
   * Bit size of EnOcean equipment profile function data field.
   */
  static final int UTE_QUERY_EEP_FUNC_SIZE = 8;

  /**
   * EnOcean equipment profile RORG (choice) data field name.
   */
  static final String UTE_QUERY_EEP_RORG_FIELD_NAME = "EEP_RORG";

  /**
   * Start bit of EnOcean equipment profile RORG (choice) data field.
   */
  static final int UTE_QUERY_EEP_RORG_OFFSET = 48;

  /**
   * Bit size of EnOcean equipment profile RORG (choice) data field.
   */
  static final int UTE_QUERY_EEP_RORG_SIZE = 8;

  /**
   * Query type data field name.
   */
  static final String UTE_QUERY_TYPE_DATA_FIELD_NAME = "UTE_QUERY_TYPE";

  /**
   * Start bit of query type data field.
   */
  static final int UTE_QUERY_TYPE_OFFSET = 2;

  /**
   * Bit size of query type data field.
   */
  static final int UTE_QUERY_TYPE_SIZE = 2;


  // Private Instance Fields ----------------------------------------------------------------------

  /**
   * Communication type data field.
   */
  private Bool bidirectionalEEP;

  /**
   * Response data field.
   */
  private Bool responseExpected;

  /**
   * Query type data field.
   */
  private Ordinal queryType;

  /**
   * Command type data field.
   */
  private Ordinal cmdType;

  /**
   * Channel number data field.
   */
  private Ordinal channelNumber;

  /**
   * Manufacturer ID data field (least significant bits).
   */
  private Ordinal mid8LSB;

  /**
   * Manufacturer ID data field (most significant bits).
   */
  private Ordinal mid3MSB;

  /**
   * EnOcean device manufacturer.
   */
  private Manufacturer manufacturer;

  /**
   * EnOcean equipment profile type data field.
   */
  private Ordinal eepType;

  /**
   * EnOcean equipment profile function data field.
   */
  private Ordinal eepFunc;

  /**
   * EnOcean equipment profile RORG (choice) data field.
   */
  private Ordinal eepRORG;

  /**
   * Payload data field.
   */
  private EepData payload;

  /**
   * EnOcean equipment profile type.
   */
  private EepType eep;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs an universal teach-in (UTE) query radio telegram with given data.
   *
   * @param data           data group
   *
   * @param optionalData   optional data group
   */
  public Esp3UTEQueryTelegram(byte[] data, byte[] optionalData)
  {
    super(
        RORG.UTE, UTE_QUERY_PAYLOAD_LENGTH,
        data, optionalData
    );

    this.bidirectionalEEP = createCommunicationFlag();
    this.responseExpected = createResponseFlag();
    this.queryType = createQueryType();
    this.cmdType = createTelegramType();
    this.channelNumber = createChannel();
    this.mid8LSB = createMid8LSB();
    this.mid3MSB = createMid3MSB();
    this.eepType = createEepType();
    this.eepFunc = createEepFunc();
    this.eepRORG = createEepRORG();

    // TODO : remove dummy type EEP_TYPE_A50201
    this.payload = new EepData(
        EepType.EEP_TYPE_A50201, UTE_QUERY_PAYLOAD_LENGTH,
        bidirectionalEEP, responseExpected, queryType, cmdType,
        channelNumber, mid8LSB, mid3MSB, eepFunc, eepType, eepRORG
    );

    byte[] payloadBytes = new byte[UTE_QUERY_PAYLOAD_LENGTH];
    System.arraycopy(data, 1, payloadBytes, 0, UTE_QUERY_PAYLOAD_LENGTH);
    payload.update(payloadBytes);

    this.eep = createEepType(
        eepRORG.ordinalValue(), eepFunc.ordinalValue(), eepType.ordinalValue()
    );

    this.manufacturer = Manufacturer.fromID(
        mid8LSB.ordinalValue() | (mid3MSB.ordinalValue() << 8)
    );
  }


  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * Returns communication type.
   *
   * @return  true to indicate a bidirectional EEP communication, otherwise false
   */
  public boolean isBidirectionalEEP()
  {
    return bidirectionalEEP.boolValue();
  }

  /**
   * Returns the response type.
   *
   * @return  true to indicate that a response is expected, otherwise false
   */
  public boolean isResponseExpected()
  {
    return responseExpected.boolValue();
  }

  /**
   * Returns the universal teach-in (UTE) query type.
   *
   * @return  the query type
   */
  public QueryType getQueryType()
  {
    return QueryType.resolve(queryType.ordinalValue());
  }

  /**
   * Returns the universal teach-in (UTE) command type.
   *
   * @return  the command type
   */
  public CmdType getCommandType()
  {
    return CmdType.resolve(cmdType.ordinalValue());
  }

  /**
   * Returns the universal teach-in (UTE) channel number.
   *
   * @return  zero based channel number, {@link #UTE_QUERY_ALL_CHANNELS} if the UTE query refers
   *          to all channels
   */
  public int getChannelNumber()
  {
    return channelNumber.ordinalValue();
  }

  /**
   * Returns the EnOcean device manufacturer.
   *
   * @return  EnOcean device manufacturer
   */
  public Manufacturer getManufacturer()
  {
    return manufacturer;
  }

  /**
   * Returns the EnOcean equipment profile (EEP) type value.
   *
   * @return  EEP type value
   */
  public int getEepTypeValue()
  {
    return eepType.ordinalValue();
  }

  /**
   * Returns the EnOcean equipment profile (EEP) function type value.
   *
   * @return  EEP function type value
   */
  public int getEepFuncValue()
  {
    return eepFunc.ordinalValue();
  }

  /**
   * Returns the EnOcean equipment profile (EEP) RORG (choice) value.
   *
   * @return  EEP RORG value
   */
  public int getEepRORGValue()
  {
    return eepRORG.ordinalValue();
  }

  /**
   * Returns the EnOcean equipment profile (EEP) type.
   *
   * @return  EEP type, null if EEP type is not supported by OpenRemote EnOcean protocol
   */
  public EepType getEep()
  {
    return eep;
  }

  /**
   * Returns the EnOcean equipment profile string identifier.
   *
   * @return  EEP string identifier
   */
  public String getEepAsString()
  {
    return String.format(
        "%02X-%02X-%02X", eepRORG.ordinalValue(), eepFunc.ordinalValue(), eepType.ordinalValue()
    );
  }


  // Private Instance Methods ---------------------------------------------------------------------

  private EepType createEepType(int rorgValue, int func, int type) //throws EepType.InvalidEepTypeExpception
  {
    EepType eep = null;
    try
    {
      eep = EepType.lookup(getEepAsString());
    }

    catch (EepType.InvalidEepTypeExpception invalidEepTypeExpception)
    {
      // Return null in case of unsupported EEP
    }

    return eep;
  }

  private Bool createCommunicationFlag()
  {
    return Bool.createBool(
        UTE_QUERY_EEP_COMM_TYPE_FIELD_NAME, UTE_QUERY_EEP_COMM_TYPE_OFFSET, UTE_QUERY_EEP_COMM_TYPE_SIZE,
        UTE_QUERY_EEP_COMM_TYPE_BI_DESC, UTE_QUERY_EEP_COMM_TYPE_BI_VALUE,
        UTE_QUERY_EEP_COMM_TYPE_UNI_DESC, UTE_QUERY_EEP_COMM_TYPE_UNI_VALUE
    );
  }

  private Bool createResponseFlag()
  {
    return Bool.createBool(
        UTE_QUERY_RESPONSE_FIELD_NAME, UTE_QUERY_RESPONSE_OFFSET, UTE_QUERY_RESPONSE_SIZE,
        UTE_QUERY_RESPONSE_EXPECTED_DESC, UTE_QUERY_RESPONSE_EXPECTED_VALUE,
        UTE_QUERY_RESPONSE_NOT_EXPECTED_DESC, UTE_QUERY_RESPONSE_NOT_EXPECTED_VALUE
    );
  }

  private Ordinal createQueryType()
  {
    EepDataField dataField = new EepDataField(
        UTE_QUERY_TYPE_DATA_FIELD_NAME, UTE_QUERY_TYPE_OFFSET, UTE_QUERY_TYPE_SIZE
    );
    Ordinal queryType = new Ordinal(dataField, 0);

    return queryType;
  }

  private Ordinal createTelegramType()
  {
    EepDataField dataField = new EepDataField(
        UTE_QUERY_CMD_FIELD_NAME, UTE_QUERY_CMD_OFFSET, UTE_QUERY_CMD_SIZE
    );
    Ordinal telegramType = new Ordinal(dataField, 0);

    return telegramType;
  }

  private Ordinal createChannel()
  {
    EepDataField dataField = new EepDataField(
        UTE_QUERY_CHANNEL_FIELD_NAME, UTE_QUERY_CHANNEL_OFFSET, UTE_QUERY_CHANNEL_SIZE
    );
    Ordinal channel = new Ordinal(dataField, 0);

    return channel;
  }

  private Ordinal createMid8LSB()
  {
    EepDataField dataField = new EepDataField(
        UTE_QUERY_MID_8LSB_FIELD_NAME, UTE_QUERY_MID_8LSB_OFFSET, UTE_QUERY_MID_8LSB_SIZE
    );
    Ordinal mid8LSB = new Ordinal(dataField, 0);

    return mid8LSB;
  }

  private Ordinal createMid3MSB()
  {
    EepDataField dataField = new EepDataField(
        UTE_QUERY_MID_3MSB_FIELD_NAME, UTE_QUERY_MID_3MSB_OFFSET, UTE_QUERY_MID_3MSB_SIZE
    );
    Ordinal mid3MSB = new Ordinal(dataField, 0);

    return mid3MSB;
  }

  private Ordinal createEepType()
  {
    EepDataField dataField = new EepDataField(
        UTE_QUERY_EEP_TYPE_FIELD_NAME, UTE_QUERY_EEP_TYPE_OFFSET, UTE_QUERY_EEP_TYPE_SIZE
    );
    Ordinal type = new Ordinal(dataField, 0);

    return type;
  }

  private Ordinal createEepFunc()
  {
    EepDataField dataField = new EepDataField(
        UTE_QUERY_EEP_FUNC_FIELD_NAME, UTE_QUERY_EEP_FUNC_OFFSET, UTE_QUERY_EEP_FUNC_SIZE
    );
    Ordinal func = new Ordinal(dataField, 0);

    return func;
  }

  private Ordinal createEepRORG()
  {
    EepDataField dataField = new EepDataField(
        UTE_QUERY_EEP_RORG_FIELD_NAME, UTE_QUERY_EEP_RORG_OFFSET, UTE_QUERY_EEP_RORG_SIZE
    );
    Ordinal rorg = new Ordinal(dataField, 0);

    return rorg;
  }
}
