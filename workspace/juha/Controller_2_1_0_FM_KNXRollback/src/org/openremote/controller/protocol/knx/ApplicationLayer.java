/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
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


/**
 * Represents application layer in KNX communication stack.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
class ApplicationLayer 
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * Bitmask to extract APCI bits from the CEMI TPCI/APCI control information field.
   */
  private final static int CEMI_TPCI_APCI_FIELD_BITMASK = 0x03;

  /**
   * Bitmask to extract APCI bits from the CEMI APCI/Data field.
   */
  private final static int CEMI_APCI_DATA_FIELD_BITMASK = 0xC0;



  // Enums ----------------------------------------------------------------------------------------

  /**
   * Transport layer and application layer control information (TPCI & APCI) for application
   * protocol data units (APDU) in common EMI frame.
   */
  enum Service
  {
    /**
     * Group Value Write Service (6-bit data payload) <p>
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
     * Group Value Write Service  <p>
     *
     * PDU for data values greater than 6 bits in length:
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
     * |.|.|.|.|.|0|0||1|0|0|0|0|0|0|0|
     * +-+-+-+-+-+-+-++-+-+-+-+-+-+-+-+
     *
     * }</pre>
     */
    GROUPVALUE_WRITE(
        0x00,
        0x80
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
    ),

    /**
     * Group Value Response Service  <p>
     *
     * PDU for data values greater than 6 bits in length:
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
     * |.|.|.|.|.|0|0||0|1|0|0|0|0|0|0|
     * +-+-+-+-+-+-+-++-+-+-+-+-+-+-+-+
     *
     * }</pre>
     */
    GROUPVALUE_RESPONSE
    (
        0x00,         // TPCI (6 bits) & APCI high bits (2 bits) -  bits 00000000
        0x40          // APCI low bits (8 bits)                  -  bits 01000000
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


    // Enum Class Methods -------------------------------------------------------------------------

    /**
     * Resolves the application layer {@link Service} from the TPCI/APCI and APCI/Data bytes
     * in a common EMI frame.
     *
     * @param dataLength      the data length of the CEMI frame -- should be value of 1 for
     *                        six bit payloads, two or higher for 8-bit and above payloads.
     *
     * @param cemiTpciApci    the first byte in the CEMI frame after the data length field
     *                        which is the TPCI/APCI control information field
     *
     * @param cemiApciData    the second byte in the CEMI frame after the data length field
     *                        which is the APCI (if 8-bit or above data payload) or
     *                        APCI + six bit data in case of six bit data payloads
     *
     * @return                the service type identifier
     *
     * @throws CommonEMI.InvalidFrameException  if the CEMI bytes cannot be parsed
     */
    protected static Service resolveCEMIServiceType(int dataLength,
                                                    int cemiTpciApci,
                                                    int cemiApciData)
        throws CommonEMI.InvalidFrameException
    {
      // IMPLEMENTATION NOTE:
      //
      //   We do need the data length to determine the difference between 6-bit and above
      //   six bit payloads -- otherwise the difference between a six bit value with data
      //   of 0x00 is not distinguishable from 8-bit or higher data payloads from the TPCI/APCI
      //   and APCI/Data bytes alone.

      if (dataLength <= 0)
      {
        throw new CommonEMI.InvalidFrameException("CEMI data length is less than 1: {0}", dataLength);
      }

      if (isSixBitGroupValueResponse(dataLength, cemiTpciApci, cemiApciData))
      {
        return GROUPVALUE_RESPONSE_6BIT;
      }

      else if (isGroupValueResponse(dataLength, cemiTpciApci, cemiApciData))
      {
        return GROUPVALUE_RESPONSE;
      }

      else if (isGroupValueRead(dataLength, cemiTpciApci, cemiApciData))
      {
        return GROUPVALUE_READ;
      }

      else if (isSixBitGroupValueWrite(dataLength, cemiTpciApci, cemiApciData))
      {
        return GROUPVALUE_WRITE_6BIT;
      }

      else if (isGroupValueWrite(dataLength, cemiTpciApci, cemiApciData))
      {
        return GROUPVALUE_WRITE;
      }

      else
      {
        throw new CommonEMI.InvalidFrameException(
            "Common EMI frame structure error. Could not resolve the CEMI " +
            "frame to any known application layer service. "
        );
      }
    }

    /**
     * Parses the CEMI data length, TPCI/APCI and APCI/Data fields to determine whether the
     * given frame byte values represent a six bit group value response service.
     *
     * @param dataLength    the data length byte in CEMI frame
     * @param tpciApci      the TPCI/APCI byte in CEMI frame
     * @param apciData      the APCI/Data byte in CEMI frame
     *
     * @return    true if the CEMI frame values represent a six bit group value response service;
     *            false otherwise
     */
    protected static boolean isSixBitGroupValueResponse(int dataLength, int tpciApci, int apciData)
    {
      // IMPLEMENTATION NOTE:
      //
      //   We do need the data length to determine the difference between 6-bit vs larger
      //   payloads -- otherwise the difference between a six bit value with data of 0x00 is
      //   not distinguishable from 8-bit or higher data payloads from the TPCI/APCI
      //   and APCI/Data bytes alone.

      int cemiAPCIHiBits = tpciApci & CEMI_TPCI_APCI_FIELD_BITMASK;
      int cemiAPCILoBits = apciData & CEMI_APCI_DATA_FIELD_BITMASK;

      return
          dataLength == 1 &&
          cemiAPCIHiBits == GROUPVALUE_RESPONSE_6BIT.getTPCIAPCI() &&
          cemiAPCILoBits == GROUPVALUE_RESPONSE_6BIT.getAPCIData();
    }

    /**
     * Parses the CEMI data length, TPCI/APCI and APCI/Data fields to determine whether the
     * given frame byte values represent a six bit group value write service.
     *
     * @param dataLength    the data length byte in CEMI frame
     * @param tpciApci      the TPCI/APCI byte in CEMI frame
     * @param apciData      the APCI/Data byte in CEMI frame
     *
     * @return    true if the CEMI frame values represent a six bit group value write service;
     *            false otherwise
     */
    protected static boolean isSixBitGroupValueWrite(int dataLength, int tpciApci, int apciData)
    {
      // IMPLEMENTATION NOTE:
      //
      //   We do need the data length to determine the difference between 6-bit vs larger
      //   payloads -- otherwise the difference between a six bit value with data of 0x00 is
      //   not distinguishable from 8-bit or higher data payloads from the TPCI/APCI
      //   and APCI/Data bytes alone.

      int cemiAPCIHiBits = tpciApci & CEMI_TPCI_APCI_FIELD_BITMASK;
      int cemiAPCILoBits = apciData & CEMI_APCI_DATA_FIELD_BITMASK;

      return
          dataLength == 1 &&
          cemiAPCIHiBits == GROUPVALUE_WRITE_6BIT.getTPCIAPCI() &&
          cemiAPCILoBits == GROUPVALUE_WRITE.getAPCIData();
    }

    /**
     * Parses the CEMI data length, TPCI/APCI and APCI/Data fields to determine whether the
     * given frame byte values represent a group value read service.
     *
     * @param dataLength    the data length byte in CEMI frame
     * @param tpciApci      the TPCI/APCI byte in CEMI frame
     * @param apciData      the APCI/Data byte in CEMI frame
     *
     * @return    true if the CEMI frame values represent a six bit group value read service;
     *            false otherwise
     */
    protected static boolean isGroupValueRead(int dataLength, int tpciApci, int apciData)
    {
      int cemiAPCIHiBits = tpciApci & CEMI_TPCI_APCI_FIELD_BITMASK;
      int cemiAPCILoBits = apciData & CEMI_APCI_DATA_FIELD_BITMASK;

      return
          dataLength == 1 &&
          cemiAPCIHiBits == GROUPVALUE_READ.getTPCIAPCI() &&
          cemiAPCILoBits == GROUPVALUE_READ.getAPCIData();
    }

    /**
     * Parses the CEMI data length, TPCI/APCI and APCI/Data fields to determine whether the
     * given frame byte values represent a group value response (not 6-bit) service.
     *
     * @param dataLength    the data length byte in CEMI frame
     * @param tpciApci      the TPCI/APCI byte in CEMI frame
     * @param apciData      the APCI/Data byte in CEMI frame
     *
     * @return    true if the CEMI frame values represent a group value response service;
     *            false otherwise
     */
    protected static boolean isGroupValueResponse(int dataLength, int tpciApci, int apciData)
    {
      int cemiAPCIHiBits = tpciApci & CEMI_TPCI_APCI_FIELD_BITMASK;
      int cemiAPCILoBits = apciData & CEMI_APCI_DATA_FIELD_BITMASK;

      return
          dataLength > 1 &&
          cemiAPCIHiBits == GROUPVALUE_RESPONSE.getTPCIAPCI() &&
          cemiAPCILoBits == GROUPVALUE_RESPONSE.getAPCIData();
    }

    /**
     * Parses the CEMI data length, TPCI/APCI and APCI/Data fields to determine whether the
     * given frame byte values represent a six bit group value write (not 6-bit) service.
     *
     * @param dataLength    the data length byte in CEMI frame
     * @param tpciApci      the TPCI/APCI byte in CEMI frame
     * @param apciData      the APCI/Data byte in CEMI frame
     *
     * @return    true if the CEMI frame values represent a group value write service;
     *            false otherwise
     */
    protected static boolean isGroupValueWrite(int dataLength, int tpciApci, int apciData)
    {
      int cemiAPCIHiBits = tpciApci & CEMI_TPCI_APCI_FIELD_BITMASK;
      int cemiAPCILoBits = apciData & CEMI_APCI_DATA_FIELD_BITMASK;

      return
          dataLength > 1 &&
          cemiAPCIHiBits == GROUPVALUE_WRITE.getTPCIAPCI() &&
          cemiAPCILoBits == GROUPVALUE_WRITE.getAPCIData();
    }


    // Enum Instance Methods ----------------------------------------------------------------------

    /**
     * Returns the high byte of application protocol data unit (APDU) which contains transport
     * protocol control information (TPCI) and application protocol control information (APCI). <p>
     *
     * TPCI bits (first 6 bits) are left at zero values. Last two bits contains the two high bits
     * of APCI. <p>
     *
     * For details of the entire APDU structure, see
     * {@link org.openremote.controller.protocol.knx.ApplicationProtocolDataUnit}
     *
     * @see #getAPCIData()
     *
     * @return TPCI/APCI byte (8 bits) in Common EMI frame
     */
    protected int getTPCIAPCI()
    {
      return apciHiBits;
    }

    /**
     * Returns the low byte of application protocol data unit (APDU) which contains application
     * protocol control information (APCI) and data (in case of 6-bit data payloads). <p>
     *
     * Two high bits of the byte are APCI low bits that should be used in combination with the
     * high bits retrieved from {@link #getTPCIAPCI()}. The low six bits may be either additional
     * ACPI bits (in case of data payloads larger than 6 bits) or a 6-bit data payload, depending
     * on the application layer service and datatype being used. In case of 6-bit data payloads,
     * the last 6 bits are set to zeros.
     *
     * For details of the entire APDU structure, see
     * {@link org.openremote.controller.protocol.knx.ApplicationProtocolDataUnit}
     *
     * @see #getTPCIAPCI()
     * @see org.openremote.controller.protocol.knx.datatype.DataType
     *
     * @return APCI/Data byte (8 bits) in Common EMI frame
     */
    protected int getAPCIData()
    {
      return apciLoBits;
    }
  }
}

