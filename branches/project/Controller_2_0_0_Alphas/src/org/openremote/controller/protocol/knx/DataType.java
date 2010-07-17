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


  enum Boolean implements DataType
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

    Boolean(byte value)
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
  
}
