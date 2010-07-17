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

/**
 * This interface represents a KNX datatype.  <p>
 *
 * KNX 1.1 Datatypes are specified in Volume 3: Systems Specifications, Part 7: Interworking,
 * Section 2: Datapoint Types.  <p>
 *
 * Specified datatypes are:
 *
 * <ol>
 * <li>Boolean</li>
 * <li>1-Bit Controlled</li>
 * <li>3-Bit Controller</li>
 * <li>Character Set</li>
 * <li>8-Bit Unsigned Value</li>
 * <li>8-Bit Signed Value</li>
 * <li>Status with Mode</li>
 * <li>2-Octet Unsigned Value</li>
 * <li>2-Octet Signed Value</li>
 * <li>2-Octet Float Value</li>
 * <li>Time</li>
 * <li>Date</li>
 * <li>4-Octet Unsigned Value</li>
 * <li>4-Octet Signed Value</li>
 * <li>4-Octet Float Value</li>
 * <li>Access</li>
 * <li>String</li>
 * </ol>
 *
 * @see DataType.Boolean
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
interface DataType 
{

  /**
   * Value type for 'Boolean' datatype (KNX 1.1 Vol 3: Part 7.2 - Datapoint Types): {@value}
   */
  final static byte DATATYPE_BOOLEAN_ONE = 0x01;

  /**
   * Value type for 'Boolean' datatype (KNX 1.1 Vol 3: Part 7.2 - Datapoint Types): {@value}
   */
  final static byte DATATYPE_BOOLEAN_ZERO = 0x00;


  /**
   * Returns the data length (payload) represented by this datatype. Data length is at minimum
   * 1 byte and at most 14 bytes. It does *not* include the first byte in the application layer
   * data unit which contains transport protocol and application protocol control information
   * (TPCI & APCI) -- see {@link ApplicationProtocolDataUnit} for more details on the APDU
   * structure.
   *
   * @return  length of the data in Common EMI frame Application Protocol Data Unit (APDU) payload;
   *          minimum of 1 byte, maximum of 14 bytes.
   */
  int getDataLength();

  /**
   * Returns the data bytes in Application Protocol Data Unit (APDU). This is the actual data
   * bytes not including the Transport Protocol or Application Protocol Control Information
   * (TCPI & ACPI, respectively).
   *
   * @see #getDataLength
   * @see ApplicationProtocolDataUnit
   *
   * @return KNX Application Protocol Data Unit (APDU) data payload as a byte array. The returned
   *         array has at minimum 1 byte and at most 14 bytes. It's length matches the value
   *         returned by {@link #getDataLength} of this datatype.
   */
  byte[] getData();

  

  // Nested Enums ---------------------------------------------------------------------------------


  /**
   * Boolean datatype as defined in KNX 1.1 Volume 3: System specifications Part 7: Interworking,
   * Chapter 2, Datapoint Types. <p>
   *
   * KNX Boolean datatype is a 1-bit value represented by either 0 or 1 integer value. There are
   * 14 different datapoint types that use Boolean, each with its own encoding for the 1-bit value:
   *
   * <ol>
   * <li>DPT 1.001 - DPT_Switch</li>
   * <li>DPT 1.002 - DPT_Bool</li>
   * <li>DPT 1.003 - DPT_Enable</li>
   * <li>DPT 1.004 - DPT_Ramp</li>
   * <li>DPT 1.005 - DPT_Alarm</li>
   * <li>DPT 1.006 - DPT_BinaryValue</li>
   * <li>DPT 1.007 - DPT_Step</li>
   * <li>DPT 1.008 - DPT_UpDown</li>
   * <li>DPT 1.009 - DPT_OpenClose</li>
   * <li>DPT 1.010 - DPT_Start</li>
   * <li>DPT 1.011 - DPT_State</li>
   * <li>DPT 1.012 - DPT_Invert</li>
   * <li>DPT 1.013 - DPT_DimSendStyle</li>
   * <li>DPT 1.014 - DPT_Inputsource</li>
   * </ol>
   */
  enum Boolean implements DataType
  {
    /**
     * DPT 1.001 - DPT_Switch. Value 0 = OFF, Value 1 = ON, general use.
     */
    OFF(DATATYPE_BOOLEAN_ZERO),           ON(DATATYPE_BOOLEAN_ONE),

    /**
     * DPT 1.002 - DPT_Bool. Value 0 = FALSE, Value 1 = TRUE, general use.
     */
    FALSE(DATATYPE_BOOLEAN_ZERO),         TRUE(DATATYPE_BOOLEAN_ONE),

    /**
     * DPT 1.003 - DPT_Enable. Value 0 = DISABLE, Value 1 = ENABLE, general use.
     */
    DISABLE(DATATYPE_BOOLEAN_ZERO),       ENABLE(DATATYPE_BOOLEAN_ONE),

    /**
     * DPT 1.004 - DPT_Ramp. Value 0 = NO_RAMP, Value 1 = RAMP, functional blocks only.
     */
    NO_RAMP(DATATYPE_BOOLEAN_ZERO),       RAMP(DATATYPE_BOOLEAN_ONE),

    /**
     * DPT 1.005 - DPT_Alarm. Value 0 = NO_ALARM, Value 1 = ALARM, functional blocks only.
     */
    NO_ALARM(DATATYPE_BOOLEAN_ZERO),      ALARM(DATATYPE_BOOLEAN_ONE),

    /**
     * DPT 1.006 - DPT_BinaryValue. Value 0 = LOW, Value 1 = HIGH, functional blocks only.
     */
    LOW(DATATYPE_BOOLEAN_ZERO),           HIGH(DATATYPE_BOOLEAN_ONE),

    /**
     * DPT 1.007 - DPT_Step. Value 0 = DECREASE, Value 1 = INCREASE, functional blocks only.
     */
    DECREASE(DATATYPE_BOOLEAN_ZERO),      INCREASE(DATATYPE_BOOLEAN_ONE),

    /**
     * DPT 1.008 - DPT_UpDown. Value 0 = UP, Value 1 = DOWN, general use.
     */
    UP(DATATYPE_BOOLEAN_ZERO),            DOWN(DATATYPE_BOOLEAN_ONE),

    /**
     * DPT 1.009 - DPT_OpenClose. Value 0 = OPEN, Value 1 = CLOSE, general use.
     */
    OPEN(DATATYPE_BOOLEAN_ZERO),          CLOSE(DATATYPE_BOOLEAN_ONE),

    /**
     * DPT 1.010 - DPT_Start. Value 0 = STOP, Value 1 = START, general use.
     */
    STOP(DATATYPE_BOOLEAN_ZERO),          START(DATATYPE_BOOLEAN_ONE),

    /**
     * DPT 1.011 - DPT_State. Value 0 = INACTIVE, Value 1 = ACTIVE, functional blocks only.
     */
    INACTIVE(DATATYPE_BOOLEAN_ZERO),      ACTIVE(DATATYPE_BOOLEAN_ONE),

    /**
     * DPT 1.012 - DPT_Invert. Value 0 = NOT INVERTED, Value 1 = INVERTED, functional blocks only.
     */
    NOT_INVERTED(DATATYPE_BOOLEAN_ZERO),  INVERTED(DATATYPE_BOOLEAN_ONE),

    /**
     * DPT 1.013 - DPT_DimSendStyle. Value 0 = START_STOP, Value 1 = CYCLICALLY,
     * functional blocks only.
     */
    START_STOP(DATATYPE_BOOLEAN_ZERO),    CYCLICALLY(DATATYPE_BOOLEAN_ONE),

    /**
     * DPT 1.014 - DPT_InputSource. Value 0 = FIXED, Value 1 = CALCULATED, functional blocks only.
     */
    FIXED(DATATYPE_BOOLEAN_ZERO),         CALCULATED(DATATYPE_BOOLEAN_ONE); // DPT 1.014 - DPT_InputSource


    // Enum Instance Fields -----------------------------------------------------------------------

    /**
     * The boolean value held by this enum instance.
     */
    private byte value = 0x00;


    // Enum Constructors --------------------------------------------------------------------------

    /**
     * New KNX Boolean enum with a given value. Valid values are 0x00 and 0x01.
     *
     * @param value   either 0x00 or 0x01, see the enum constant documentation for how these
     *                two values are encoded (on/off, start/stop, etc.)
     */
    Boolean(byte value)
    {
      if (value < 0 || value > 1)
        throw new Error("Implementation Error: Boolean value must be either 1 or 0.");

      this.value = value;
    }


    // Implements DataType ------------------------------------------------------------------------

    /**
     * Returns data length of 1 for all boolean datatype points.
     *
     * @return value of 1
     */
    public int getDataLength()
    {
      return 1;
    }

    /**
     * Returns a single byte array for all boolean datatype points. The first and only byte
     * in the array contains the current datatype value, either 0x00 or 0x01.
     *
     * @return datatype point value as a single byte array
     */
    public byte[] getData()
    {
      return new byte[] { value };
    }
  }
  
}
