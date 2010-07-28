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
import org.openremote.controller.protocol.knx.datatype.DataPointType;
import org.openremote.controller.protocol.knx.datatype.DataType;
import org.openremote.controller.exception.ConversionException;

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
 * Total number of APCI control bits can be either 4 or 10, depending on which {@link org.openremote.controller.protocol.knx.ApplicationLayer.Service
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
  // Constants ------------------------------------------------------------------------------------

  /**
   * Represents the full APDU (APCI + data) for Group Value Write service request with DPT 1.001
   * (Switch) to state 'ON'.
   *
   * @see org.openremote.controller.protocol.knx.ApplicationLayer.Service#GROUPVALUE_WRITE_6BIT
   * @see DataType.Boolean#ON
   */
  final static ApplicationProtocolDataUnit WRITE_SWITCH_ON = new ApplicationProtocolDataUnit
  (
      ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT,
      DataType.Boolean.ON
  );

  /**
   * Represents the full APDU (APCI + data) for Group Value Write service request with
   * DPT 1.001 (Switch) to state 'OFF'.
   */
  final static ApplicationProtocolDataUnit WRITE_SWITCH_OFF = new ApplicationProtocolDataUnit
  (
      ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT,
      DataType.Boolean.OFF
  );


  /**
   * Represents the full APDU (APCI bits) for Group Value Read request for data points with
   * DPT 1.001 (Switch) type.
   */
  final static ApplicationProtocolDataUnit READ_SWITCH_STATE = new ApplicationProtocolDataUnit
  (
      ApplicationLayer.Service.GROUPVALUE_READ,
      DataType.READ_SWITCH
  );



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
   * @see org.openremote.controller.protocol.knx.ApplicationLayer.Service#GROUPVALUE_RESPONSE_6BIT
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
   * Constructs an APDU corresponding to a Group Value Read response from Common EMI wire format.
   * The expected byte array must contain the last two bytes of the CEMI frame which includes
   * the APCI bits and data payload of the APDU.
   *
   * @param   apdu  APDU bytes of CEMI frame. Expected byte array length is two for a switch
   *                response (6-bit data payload for boolean datatype).
   *
   * @see org.openremote.controller.protocol.knx.ApplicationLayer.Service#GROUPVALUE_RESPONSE_6BIT
   * @see org.openremote.controller.protocol.knx.datatype.DataType.Boolean
   *
   * @return  APDU instance for a 6-bit group value response of a boolean datatype including
   *          the data payload.
   */
  static ApplicationProtocolDataUnit createSwitchResponse(final byte[] apdu)
  {
//    DataType switchReadResponse = new DataType()
//    {
//      public int getDataLength()
//      {
//        return apdu.length - 1;
//      }
//
//      public byte[] getData()
//      {
//        if (apdu.length == 2)
//        {
//          return new byte[] { (byte)(apdu[1] & 0x3F) };
//        }
//
//        else
//        {
//          byte[] data = new byte[apdu.length - 2];
//          System.arraycopy(apdu, 2, data, 0, data.length);
//
//          return data;
//        }
//      }
//
//      public DataPointType getDataPointType()
//      {
//        return BooleanDataPointType.SWITCH;
//      }
//    };

    return new ApplicationProtocolDataUnit(
        ApplicationLayer.Service.GROUPVALUE_RESPONSE_6BIT,
        DataType.Boolean.createSwitchResponse(apdu)
    );
  }

  /**
   * Constructs an APDU corresponding to a Group Value Write service for a device expecting
   * a 3-bit dim control data point type (DPT 3.007). <p>
   *
   * The control bit value must correspond to DPT 1.007, 1.008 or 1.014
   * ({@link DataType.Boolean#INCREASE}/{@link DataType.Boolean#DECREASE},
   *  {@link DataType.Boolean#UP}/{@link DataType.Boolean#DOWN},
   *  {@link DataType.Boolean#FIXED}/{@link DataType.Boolean#CALCULATED}, respectively). <p>
   *
   * The dim value must be a 3-bit value in the range of [0-7].
   *
   * @see DataType.Boolean
   * @see org.openremote.controller.protocol.knx.datatype.DataType.Controlled3Bit
   *
   * @param   controlValue  must be one of DataType.Boolean.INCREASE, DataType.Boolean.DECREASE,
   *                        DataType.Boolean.UP, DataType.Boolean.DOWN, DataType.Boolean.FIXED
   *                        or DataType.Boolean.CALCULATED
   *
   * @param   dimValue      must be in range of [0-7]
   *
   * @return  APDU instance for a 3-bit dim control Group Value Write service with a given control
   *          bit and range bits
   */
  static ApplicationProtocolDataUnit create3BitDimControl(DataType.Boolean controlValue,
                                                          int dimValue)
  {
    return new ApplicationProtocolDataUnit(
        ApplicationLayer.Service.GROUPVALUE_WRITE_6BIT,
        new DataType.Controlled3Bit(
            DataPointType.Control3BitDataPointType.CONTROL_DIMMING,
            controlValue,
            dimValue)
    );
  }



  // Private Instance Fields ----------------------------------------------------------------------

  private DataType datatype;
  private ApplicationLayer.Service applicationLayerService;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new APDU with a given application layer service and datatype.
   *
   * @see org.openremote.controller.protocol.knx.ApplicationLayer.Service
   * @see DataType
   *
   * @param service   application layer service as defined in
   *                  {@link org.openremote.controller.protocol.knx.ApplicationLayer.Service]
   * @param datatype  KNX data type
   */
  private ApplicationProtocolDataUnit(ApplicationLayer.Service service, DataType datatype)
  {
    this.applicationLayerService  = service;
    this.datatype = datatype;
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
   * See {@link DataType#getDataLength()} for details.
   *
   * @see DataType#getDataLength()
   *
   * @return returns the APDU data payload length in bytes, as specified in
   *         {@link DataType#getDataLength()}
   */
  int getDataLength()
  {
    return datatype.getDataLength();
  }

  /**
   * Returns the KNX datatype associated with this APDU.
   *
   * @see DataType
   *
   * @return  KNX datatype
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
  DataPointType getDataPointType()
  {
    return datatype.getDataPointType();
  }

  /**
   * TODO
   *
   * @return
   * @throws org.openremote.controller.exception.ConversionException
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

    pdu.add((byte)(TRANSPORT_LAYER_CONTROL_FIELDS + applicationLayerService.getTPCIAPCI()));
    pdu.add((byte)(applicationLayerService.getAPCIData() + apduData[0]));

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
