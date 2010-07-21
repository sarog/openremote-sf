/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.controller.protocol.knx;

import org.apache.log4j.Logger;
import org.openremote.controller.utils.Strings;

import java.util.ArrayList;
import java.util.List;


/**
 * This class represents application protocol data unit (APDU) in KNX specification. <p>
 *
 * APDU is defined in KNX 1.1 Volume 3: System Specifications, Part 3 Communication,
 * Chapter 7, Application Layer. <p>
 *
 * In the Common EMI frame, the APDU payload is defined as follows:
 *
 * <pre>{@code
 *
 *  +--------+--------+--------+--------+--------+
 *  | TPCI + | APCI + |  Data  |  Data  |  Data  |
 *  |  APCI  |  Data  |        |        |        |
 *  +--------+--------+--------+--------+--------+
 *    byte 1   byte 2  byte 3     ...     byte 16
 *
 *}</pre>
 *
 * For data that is 6 bits or less in length, only the first two bytes are used in a Common EMI
 * frame. Common EMI frame also carries the information of the expected length of the Protocol
 * Data Unit (PDU). Data payload can be at most 14 bytes long.  <p>
 *
 * The first byte is a combination of transport layer control information (TPCI) and application
 * layer control information (APCI). First 6 bits are dedicated for TPCI while the two least
 * significant bits of first byte hold the two most significant bits of APCI field, as follows:
 *
 * <pre>{@code
 *
 *    Bit 1    Bit 2    Bit 3    Bit 4    Bit 5    Bit 6    Bit 7    Bit 8      Bit 1   Bit 2
 *  +--------+--------+--------+--------+--------+--------+--------+--------++--------+----....
 *  |        |        |        |        |        |        |        |        ||        |
 *  |  TPCI  |  TPCI  |  TPCI  |  TPCI  |  TPCI  |  TPCI  | APCI   |  APCI  ||  APCI  |
 *  |        |        |        |        |        |        |(bit 1) |(bit 2) ||(bit 3) |
 *  +--------+--------+--------+--------+--------+--------+--------+--------++--------+----....
 *  +                            B  Y  T  E    1                            ||       B Y T E  2
 *  +-----------------------------------------------------------------------++-------------....
 *
 * }</pre>
 *
 * Total number of APCI control bits can be either 4 or 10, depending on which {@link Service
 * application layer service} is being used. The second byte bit structure is as follows:
 *
 * <pre>{@code
 *
 *    Bit 1    Bit 2    Bit 3    Bit 4    Bit 5    Bit 6    Bit 7    Bit 8      Bit 1   Bit 2
 *  +--------+--------+--------+--------+--------+--------+--------+--------++--------+----....
 *  |        |        |        |        |        |        |        |        ||        |
 *  |  APCI  |  APCI  | APCI/  |  APCI/ |  APCI/ |  APCI/ | APCI/  |  APCI/ ||  Data  |  Data
 *  |(bit 3) |(bit 4) | Data   |  Data  |  Data  |  Data  | Data   |  Data  ||        |
 *  +--------+--------+--------+--------+--------+--------+--------+--------++--------+----....
 *  +                            B  Y  T  E    2                            ||       B Y T E  3
 *  +-----------------------------------------------------------------------++-------------....
 *
 * }</pre>
 *
 * @see DataType
 * 
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
class ApplicationProtocolDataUnit
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * KNX logger. Uses a common category for all KNX related logging.
   */
  private final static Logger log = Logger.getLogger(KNXCommandBuilder.KNX_LOG_CATEGORY);


  /**
   * Determines from the Application Protocol Control Information (APCI) bits in an application
   * protocol data unit (APDU) bytes whether the application level service corresponds to Group
   * Value Response service.
   *
   * @see Service#GROUPVALUE_RESPONSE_6BIT
   *
   * @param apdu  Byte array containing the application protocol data unit payload. Only the first
   *              two bytes are inspected. This parameter can therefore contain only a partial
   *              APDU with only the first two bytes of APCI information or the entire APDU with
   *              data included.
   *
   * @return      true if the APDU corresponds to Group Value Response service; false otherwise
   */
  static boolean isGroupValueResponse(byte[] apdu)
  {
    // Expected bit structure :
    //
    //  Byte 1 : bits xxxxxx00          - first six bits are part of TPCI
    //  Byte 2 : bits 01xxxxxx          - last six bits are either data (6 bit return values)
    //                                    or zero for larger than 6 bit return values

    return ((apdu[0] & 0x3) == 0x00 && (apdu[1] & 0xC0) == 0x40);
  }


  /**
   * TODO
   *
   * @param apdu
   * @return
   */
  static ApplicationProtocolDataUnit createGroupValueResponse(final byte[] apdu)
  {
    DataType unknown = new DataType()
    {
      public int getDataLength()
      {
        return apdu.length - 1;
      }

      public byte[] getData()
      {
        if (apdu.length == 2)
        {
          return new byte[] { (byte)(apdu[1] & 0x3F) };
        }

        else
        {
          byte[] data = new byte[apdu.length - 2];
          System.arraycopy(apdu, 2, data, 0, data.length);

          return data;
        }
      }
    };

    return new ApplicationProtocolDataUnit(Service.GROUPVALUE_RESPONSE_6BIT, null, unknown);
  }


  // Enums ----------------------------------------------------------------------------------------

  /**
   * Transport layer and application layer control information (TPCI & APCI) for application
   * protocol data units (APDU) in common EMI frame.
   */
  enum Service
  {
    /**
     * Group Value Write Service  <p>
     *
     * PDU for data values equal or less than 6 bits in length:
     *
     * <pre>{@code
     *
     * +-------------++---------------+
     * |    Byte 1   ||    Byte 2     |
     * +-+-+-+-+-+-+-++-+-+-+-+-+-+-+-+
     * |T|T|T|T|T|A|A||A|A|D|D|D|D|D|D|
     * |P|P|P|P|P|P|P||P|P|a|a|a|a|a|a|
     * |C|C|C|C|C|C|C||C|C|t|t|t|t|t|t|
     * |I|I|I|I|I|I|I||I|I|a|a|a|a|a|a|
     * +-+-+-+-+-+-+-++-+-+-+-+-+-+-+-+
     * |.|.|.|.|.|0|0||1|0|.|.|.|.|.|.|
     * +-+-+-+-+-+-+-++-+-+-+-+-+-+-+-+
     *
     * }</pre>
     */
    GROUPVALUE_WRITE_6BIT(
        0x00,               // TPCI (6 bits) & APCI high bits (2 bits) - bits 00000000
        0x80                // APCI low bits (2 bits) + data (6 bits)  - bits 10000000
    ),

    /**
     * Group Value Read Service  <p>
     *
     * PDU:
     *
     * <pre>{@code
     *
     * +-------------++---------------+
     * |    Byte 1   ||    Byte 2     |
     * +-+-+-+-+-+-+-++-+-+-+-+-+-+-+-+
     * |T|T|T|T|T|A|A||A|A|A|A|A|A|A|A|
     * |P|P|P|P|P|P|P||P|P|P|P|P|P|P|P|
     * |C|C|C|C|C|C|C||C|C|C|C|C|C|C|C|
     * |I|I|I|I|I|I|I||I|I|I|I|I|I|I|I|
     * +-+-+-+-+-+-+-++-+-+-+-+-+-+-+-+
     * |.|.|.|.|.|0|0||0|0|0|0|0|0|0|0|
     * +-+-+-+-+-+-+-++-+-+-+-+-+-+-+-+
     *
     * }</pre>
     */
    GROUPVALUE_READ(
        0x00,           // TPCI (6 bits) & APCI high bits (2 bits) -  bits 00000000
        0x00            // APCI low bits (8 bits)                  -  bits 10000000
    ),

    /**
     * Group Value Response Service  <p>
     *
     * PDU for data values equal or less than 6 bits in length:
     *
     * <pre>{@code
     *
     * +-------------++---------------+
     * |    Byte 1   ||    Byte 2     |
     * +-+-+-+-+-+-+-++-+-+-+-+-+-+-+-+
     * |T|T|T|T|T|A|A||A|A|D|D|D|D|D|D|
     * |P|P|P|P|P|P|P||P|P|a|a|a|a|a|a|
     * |C|C|C|C|C|C|C||C|C|t|t|t|t|t|t|
     * |I|I|I|I|I|I|I||I|I|a|a|a|a|a|a|
     * +-+-+-+-+-+-+-++-+-+-+-+-+-+-+-+
     * |.|.|.|.|.|0|0||0|1|.|.|.|.|.|.|
     * +-+-+-+-+-+-+-++-+-+-+-+-+-+-+-+
     *
     * }</pre>
     */
    GROUPVALUE_RESPONSE_6BIT
    (
        0x00,           // TPCI (6 bits) & APCI high bits (2 bits) -  bits 00000000
        0x40            // APCI low bits (2 bits) + data (6 bits)  -  bits 01000000
    );


    // Enum Instance Fields -----------------------------------------------------------------------

    /**
     * APCI high bits used in the first byte of APDU -- only 2 least significant bits are ever
     * used, making the value range [0x00..0x03]
     */
    private int apciHiBits = 0x00;

    /**
     * APCI low bits used in the second byte of APDU -- in case of 6-bit values this uses the
     * two most significant bits in the byte. In case of larger data values, all 8 bits are used
     * for APCI.  <p>
     *
     * For 6-bit data values, valid low bit values are 0x80, 0x40 and 0xC0. <p>
     *
     * For larger data values, full range of APCI from 0x00 to 0xFF are possible.
     */
    private int apciLoBits = 0x00;


    // Enum Constructor ---------------------------------------------------------------------------

    /**
     * Constructs application layer service instance with APCI bits split to first and second bytes
     * of the APDU.
     *
     * @param apciHiBits  two least significant bits of the first byte in APDU
     * @param apciLoBits  two most significant bits (in case of 6-bit values) or all bits for the
     *                    second byte in APDU
     */
    private Service(int apciHiBits, int apciLoBits)
    {
      this.apciHiBits = apciHiBits;
      this.apciLoBits = apciLoBits;
    }
  }


  /**
   * TODO
   */
  enum DataPointType
  {
    SWITCH          ("1.001",   DataType.Boolean.OFF,         DataType.Boolean.ON),
    BOOL            ("1.002",   DataType.Boolean.FALSE,       DataType.Boolean.TRUE),
    ENABLE          ("1.003",   DataType.Boolean.DISABLE,     DataType.Boolean.ENABLE),
    RAMP            ("1.004",   DataType.Boolean.NO_RAMP,     DataType.Boolean.RAMP),
    ALARM           ("1.005",   DataType.Boolean.NO_ALARM,    DataType.Boolean.ALARM),
    BINARY_VALUE    ("1.006",   DataType.Boolean.LOW,         DataType.Boolean.HIGH),
    STEP            ("1.007",   DataType.Boolean.DECREASE,    DataType.Boolean.INCREASE),
    UPDOWN          ("1.008",   DataType.Boolean.UP,          DataType.Boolean.DOWN),
    OPENCLOSE       ("1.009",   DataType.Boolean.OPEN,        DataType.Boolean.CLOSE),
    START           ("1.010",   DataType.Boolean.STOP,        DataType.Boolean.START),
    STATE           ("1.011",   DataType.Boolean.INACTIVE,    DataType.Boolean.ACTIVE),
    INVERT          ("1.012",   DataType.Boolean.NOT_INVERTED,DataType.Boolean.INVERTED),
    DIM_SEND_STYLE  ("1.013",   DataType.Boolean.START_STOP,  DataType.Boolean.CYCLICALLY),
    INPUT_SOURCE    ("1.014",   DataType.Boolean.FIXED,       DataType.Boolean.CALCULATED);

    String dptID;
    DataType.Boolean zeroValueEncoding;
    DataType.Boolean oneValueEncoding;

    DataPointType(String dptID, DataType.Boolean zeroEncoding, DataType.Boolean oneEncoding)
    {
      this.dptID = dptID;
      this.zeroValueEncoding = zeroEncoding;
      this.oneValueEncoding = oneEncoding;
    }

    DataType.Boolean getEncodingForValue(int value)
    {
      if (value == 0) return zeroValueEncoding;
      else return oneValueEncoding;
    }
  }


  // Constants ------------------------------------------------------------------------------------

  /**
   * Represents the full APDU (APCI + data) for Group Value Write service request with DPT 1.001
   * (Switch) to state 'ON'.
   *
   * @see Service#GROUPVALUE_WRITE_6BIT
   * @see DataPointType#SWITCH
   * @see DataType.Boolean#ON
   */
  final static ApplicationProtocolDataUnit WRITE_SWITCH_ON = new ApplicationProtocolDataUnit
  (
      Service.GROUPVALUE_WRITE_6BIT,
      DataPointType.SWITCH,
      DataType.Boolean.ON
  );

  /**
   * Represents the full APDU (APCI + data) for Group Value Write service request with
   * DPT 1.001 (Switch) to state 'OFF'.
   */
  final static ApplicationProtocolDataUnit WRITE_SWITCH_OFF = new ApplicationProtocolDataUnit
  (
      Service.GROUPVALUE_WRITE_6BIT,
      DataPointType.SWITCH,
      DataType.Boolean.OFF
  );

  /**
   * Represents the full APDU (APCI bits) for Group Value Read request for data points with
   * DPT 1.001 (Switch) type.
   */
  final static ApplicationProtocolDataUnit READ_SWITCH_STATE = new ApplicationProtocolDataUnit
  (
      Service.GROUPVALUE_READ,
      DataPointType.SWITCH,
      DataType.EMPTY              // there's no data on read request
  );



  // Private Instance Fields ----------------------------------------------------------------------

  private DataType datatype;
  private Service applicationLayerService;
  private DataPointType dpt;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new APDU with a given application layer service, datapoint type and datatype
   *
   * @see Service
   * @see DataPointType
   * @see DataType
   *
   * @param service   application layer service as defined in {@link Service]
   * @param dpt       KNX datapoint type
   * @param datatype  KNX data type
   */
  private ApplicationProtocolDataUnit(Service service, DataPointType dpt, DataType datatype)
  {
    this.applicationLayerService  = service;
    this.datatype = datatype;
    this.dpt = dpt;

    // TODO : embed datatype into DPT ?
  }




  // Package-Private Instance Methods ------------------------------------------------------------


  /**
   * Returns the actual data payload without application protocol control information (APCI) bits
   * as a string value. The bytes in the payload are formatted as unsigned hex strings. An example
   * output could look like:
   *
   * <pre>{@code
   *
   *   0x00 0x0F
   *
   * }</pre>
   *
   * @return APDU data payload formatted as a sequence of unsigned hex strings
   */
  String dataAsString()
  {
    int len = getDataLength();
    byte[] data = datatype.getData();

    // sanity check...

    if (len != data.length)
    {
      log.error(
          "APDU datalength does not match returned datatype data length: " +
          len + " != " + data.length
      );
    }

    StringBuffer buffer = new StringBuffer(256);

    for (int offset = 0; offset < len && offset < data.length; ++offset)
    {
      buffer.append(Strings.byteToUnsignedHexString(data[offset]));
      buffer.append(" ");
    }

    return buffer.toString().trim();
  }


  /**
   * Returns the data length of the data type associated with this APDU.
   * See {@link org.openremote.controller.protocol.knx.DataType#getDataLength()} for details.
   *
   * @see org.openremote.controller.protocol.knx.DataType#getDataLength()
   *
   * @return returns the APDU data payload length in bytes, as specified in
   *         {@link org.openremote.controller.protocol.knx.DataType#getDataLength()}
   */
  int getDataLength()
  {
    return datatype.getDataLength();
  }

  /**
   * TODO : used ?
   *
   * @return
   */
  DataType getDataType()
  {
    return datatype;
  }

  /**
   * TODO
   *
   * @return
   */
  DataPointType  getDataPointType()
  {
    return dpt;
  }

  /**
   * TODO
   *
   * @return
   * @throws ConversionException
   */
  int convertToBooleanDataType() throws ConversionException
  {
    byte[] data = datatype.getData();

    if (data.length == 1)
    {
      if (data[0] <= 1)
      {
        return data[0];
      }
      else
      {
        log.warn(""); // TODO
        return 1;
      }
    }

    else
    {
      throw new ConversionException("data too large");
    }
  }


  /**
   * Returns the application protocol data unit (APDU) including the Control Information (ACPI)
   * bits and data value. <p>
   *
   * Returned byte array is at minimum 2 bytes long (for 6-bit data values), and maximum of 16
   * bytes long (with a largest possible 14 byte data value). <p>
   *
   * The six most significant bits of the first byte in the array are Transport Protocol Control
   * Information (TCPI) bits which are all set to zero.
   *
   *
   * @return full APDU as byte array with APCI bits and data value set
   */
  Byte[] getProtocolDataUnit()
  {
    final int TRANSPORT_LAYER_CONTROL_FIELDS = 0x00;

    int dataLen = getDataLength();
    byte[] apduData = datatype.getData();

    List<Byte> pdu = new ArrayList<Byte>(2);

    pdu.add((byte)(TRANSPORT_LAYER_CONTROL_FIELDS + applicationLayerService.apciHiBits));
    pdu.add((byte)(applicationLayerService.apciLoBits + apduData[0]));

    if (dataLen > 1)
    {
      // sanity check...

      if (apduData.length != dataLen)
      {
        throw new Error(
            "Application Protocol Data Unit (APDU) length field does not match actual data " +
            "payload length: Datatype data length " + dataLen + " != " + apduData.length +
            "payload data length"
        );
      }

      for (int apduDataIndex = 1; apduDataIndex < apduData.length; ++apduDataIndex)
      {
        pdu.add(apduData[apduDataIndex]);
      }
    }

    Byte[] pduBytes = new Byte[pdu.size()];
    return pdu.toArray(pduBytes);
  }
}
