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

import java.util.List;
import java.util.ArrayList;


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
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
class ApplicationProtocolDataUnit
{

  // Class Members --------------------------------------------------------------------------------

  private final static Logger log = Logger.getLogger(KNXCommandBuilder.KNX_LOG_CATEGORY);


  // Constants ------------------------------------------------------------------------------------

  private final static byte DATATYPE_BOOLEAN_ONE = 0x01;
  private final static byte DATATYPE_BOOLEAN_ZERO = 0x00;


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
     * PDU:
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
    GROUPVALUE_WRITE(
        0x00,           // TPCI (6 bits) & APCI high bits (2 bits) - 0x00000000
        0x80            // APCI low bits (2 bits) + data (6 bits)  - 0x10000000
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
        0x00,           // TPCI (6 bits) & APCI high bits (2 bits) - 0x00000000
        0x00            // APCI low bits (8 bits)                  - 0x10000000
    );


    // Enum Instance Fields -----------------------------------------------------------------------

    private int apciHiBits = 0x00;
    private int apciLoBits = 0x00;


    // Enum Constructor ---------------------------------------------------------------------------

    private Service(int apciHiBits, int apciLoBits)
    {
      this.apciHiBits = apciHiBits;
      this.apciLoBits = apciLoBits;
    }
  }


  enum DataTypeBoolean implements DataType
  {
    OFF(DATATYPE_BOOLEAN_ZERO),           ON(DATATYPE_BOOLEAN_ONE),         // DPT 1.001 - DPT_Switch
    FALSE(DATATYPE_BOOLEAN_ZERO),         TRUE(DATATYPE_BOOLEAN_ONE),       // DPT 1.002 - DPT_Bool
    DISABLE(DATATYPE_BOOLEAN_ZERO),       ENABLE(DATATYPE_BOOLEAN_ONE),     // DPT 1.003 - DPT_Enable
    NO_RAMP(DATATYPE_BOOLEAN_ZERO),       RAMP(DATATYPE_BOOLEAN_ONE),       // DPT 1.004 - DPT_Ramp
    NO_ALARM(DATATYPE_BOOLEAN_ZERO),      ALARM(DATATYPE_BOOLEAN_ONE),      // DPT 1.005 - DPT_Alarm
    LOW(DATATYPE_BOOLEAN_ZERO),           HIGH(DATATYPE_BOOLEAN_ONE),       // DPT 1.006 - DPT_BinaryValue
    DECREASE(DATATYPE_BOOLEAN_ZERO),      INCREASE(DATATYPE_BOOLEAN_ONE),   // DPT 1.007 - DPT_Step
    UP(DATATYPE_BOOLEAN_ZERO),            DOWN(DATATYPE_BOOLEAN_ONE),       // DPT 1.008 - DPT_UpDown
    OPEN(DATATYPE_BOOLEAN_ZERO),          CLOSE(DATATYPE_BOOLEAN_ONE),      // DPT 1.009 - DPT_OpenClose
    STOP(DATATYPE_BOOLEAN_ZERO),          START(DATATYPE_BOOLEAN_ONE),      // DPT 1.010 - DPT_Start
    INACTIVE(DATATYPE_BOOLEAN_ZERO),      ACTIVE(DATATYPE_BOOLEAN_ONE),     // DPT 1.011 - DPT_State
    NOT_INVERTED(DATATYPE_BOOLEAN_ZERO),  INVERTED(DATATYPE_BOOLEAN_ONE),   // DPT 1.012 - DPT_Invert
    START_STOP(DATATYPE_BOOLEAN_ZERO),    CYCLICALLY(DATATYPE_BOOLEAN_ONE), // DPT 1.013 - DPT_DimSendStyle
    FIXED(DATATYPE_BOOLEAN_ZERO),         CALCULATED(DATATYPE_BOOLEAN_ONE); // DPT 1.014 - DPT_InputSource

    private byte value = 0x00;

    DataTypeBoolean(byte value)
    {
      this.value = value;
    }

    public int getDataLength()
    {
      return 1;
    }

    public byte[] getData()
    {
      return new byte[] { value };
    }
  }


  enum DataPointType
  {
    SWITCH,         // DPT 1.001
    BOOL,           // DPT 1.002
    ENABLE,         // DPT 1.003
    RAMP,           // DPT 1.004
    ALARM,          // DPT 1.005
    BINARY_VALUE,   // DPT 1.006
    STEP,           // DPT 1.007
    UPDOWN,         // DPT 1.008
    OPENCLOSE,      // DPT 1.009
    START,          // DPT 1.010
    STATE,          // DPT 1.011
    INVERT,         // DPT 1.012
    DIM_SEND_STYLE, // DPT 1.013
    INPUT_SOURCE    // DPT 1.014
  }


  // Constants ------------------------------------------------------------------------------------

  final static ApplicationProtocolDataUnit WRITE_SWITCH_ON = new ApplicationProtocolDataUnit
  (
      Service.GROUPVALUE_WRITE,
      DataPointType.SWITCH,
      DataTypeBoolean.ON
  );

  final static ApplicationProtocolDataUnit WRITE_SWITCH_OFF = new ApplicationProtocolDataUnit
  (
      Service.GROUPVALUE_WRITE,
      DataPointType.SWITCH,
      DataTypeBoolean.OFF
  );

  final static ApplicationProtocolDataUnit READ_SWITCH_STATE = new ApplicationProtocolDataUnit
  (
      Service.GROUPVALUE_READ,
      DataPointType.SWITCH
  );



  // Private Instance Fields ----------------------------------------------------------------------

  private DataType datatype;
  private Service applicationLayerService;


  // Constructors ---------------------------------------------------------------------------------

  private ApplicationProtocolDataUnit(Service service, DataPointType dpt, DataType datatype)
  {
    this(service, dpt);
    this.datatype = datatype;
  }

  private ApplicationProtocolDataUnit(Service service, DataPointType dpt)
  {
    this.applicationLayerService = service;
  }

  // Package-Private Instance Methods ------------------------------------------------------------

  int getDataLength()
  {
    return datatype.getDataLength();
  }

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
