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
package org.openremote.controller.protocol.knx.datatype;

import org.openremote.controller.protocol.knx.datatype.DataPointType;

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
 * <li>3-Bit Controlled</li>
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
 * @see Bool
 * @see DataType.Controlled3Bit
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public interface DataType
{

  /**
   * Returns the data length (payload) represented by this datatype. Data length is at minimum
   * 1 byte and at most 14 bytes. It does *not* include the first byte in the application layer
   * data unit which contains transport protocol and application protocol control information
   * (TPCI & APCI) -- see {@link org.openremote.controller.protocol.knx.ApplicationProtocolDataUnit}
   * for more details on the APDU structure.
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
   * @see org.openremote.controller.protocol.knx.ApplicationProtocolDataUnit
   *
   * @return KNX Application Protocol Data Unit (APDU) data payload as a byte array. The returned
   *         array has at minimum 1 byte and at most 14 bytes. It's length matches the value
   *         returned by {@link #getDataLength} of this datatype.
   */
  byte[] getData();

  /**
   * Returns the KNX datapoint type that describes the structure of the data returned by
   * {@link #getData()}.
   *
   * @return  KNX datapoint type
   */
  DataPointType getDataPointType();



  // Nested Classes -------------------------------------------------------------------------------


  /**
   * Controlled 3-bit datatype as defined in KNX 1.1 Volume 3: System specifications Part 7:
   * Interworking, Chapter 2, Datapoint Types. <p>
   *
   * KNX Controlled 3-bit datatype is a 4-bit value represented by a one-bit control and
   * a 3-bit (range [0..7] integer value. <p>
   *
   * The control bit represents one of the following Boolean DPTs: DPT 1.007 (INCREASE/DECREASE),
   * DPT 1.008 (UP/DOWN) or DTP 1.014 (FIXED/CALCULATED). <p>
   *
   * There are 3 datapoint types that use 3-bit controlled datatype:
   *
   * <ol>
   * <li>DPT 3.007 - DPT_Control_Dimming</li>
   * <li>DPT 3.008 - DPT_Control_Blinds</li>
   * <li>DPT 3.009 - DPT_Mode_Boiler</li>
   * </ol>
   *
   * DPT 3.007 control dimming must use DPT 1.007 boolean increase/decrease as its control bit.
   * DPT 3.008 control blinds must use DPT 1.008 boolean up/down as its control bit.
   * And DPT 3.009 boiler mode must use DTP 1.014 boolean fixed/calculated as its control bit. <p>
   *
   * For DTP 3.007 and DPT 3.008 control dimming and blinds the 3-bit value is interpreted as a
   * step range between [1-7]. For DPT 3.009 boiler mode the 3-bit value is interpreted as one of
   * three separate modes where value 1 maps to Mode 0, value 2 maps to Mode 1 and value 4 maps to
   * Mode 2.
   */
  public static class Controlled3Bit implements DataType
  {

    // Private Instance Fields --------------------------------------------------------------------

    private DataPointType dpt;
    private Bool controlBit;
    private int value = 0;


    // Constructors -------------------------------------------------------------------------------

    public Controlled3Bit(DataPointType.Control3BitDataPointType dpt, Bool controlBitValue, int value)
    {
      if (controlBitValue != Bool.INCREASE && controlBitValue != Bool.DECREASE &&
          controlBitValue != Bool.UP && controlBitValue != Bool.DOWN &&
          controlBitValue != Bool.FIXED && controlBitValue != Bool.CALCULATED)
      {
        throw new Error("Control bit must be DPT 1.007, DPT 1.008 or DPT 1.014");
      }

      if (value < 0 || value > 7)
      {
        throw new Error("Control dim range must be between [0-7] (received: " + value + ").");
      }

      if (dpt == DataPointType.Control3BitDataPointType.MODE_BOILER)
      {
        if (value != 1 && value != 2 && value != 4)
        {
          throw new Error("Boiler mode value must be 1, 2 or 4.");
        }
      }

      this.dpt = dpt;
      this.controlBit = controlBitValue;
      this.value = value;
    }
    
    public int getDataLength()
    {
      return 1;
    }

    public byte[] getData()
    {
      int controlData = controlBit.getData() [0];

      controlData = controlData << 3;

      return new byte[] { (byte)(controlData + value) };
    }

    public DataPointType getDataPointType()
    {
      return dpt;
    }

  }



  /**
   * Unsigned 8-bit datatype as defined in KNX 1.1 Volume 3: System specifications Part 7:
   * Interworking, Chapter 2, Datapoint Types. <p>
   *
   * KNX Unsigned 8-bit datatype is a 8-bit value in the range of [0..255]. <p>
   *
   * There are 4 datapoint types that use unsigned 8-bit datatype:
   *
   * <ol>
   * <li>DPT 5.001 - DPT_Scaling</li>
   * <li>DPT 5.003 - DPT_Angle</li>
   * <li>DPT 5.004 - DPT_RelPos_Valve</li>
   * <li>DPT 5.010 - DPT_Value_1_Ucount</li>
   * </ol>
   *
   * For DPT 5.001 scaling, the range value is interpreted as a percentage between [0%..100%]
   * giving the setting an approximately 0.4% granularity. Value zero is interpreted as 0%,
   * value 1 as "low" value and value 255 as maximum in the range (100%).  <p>
   * 
   * For DPT 5.003 angle, the range value is interpreted as a degree between [0'..360'] giving
   * the setting an approximately 1.4 degree granularity. <p>
   *
   * For DPT 5.004 relative position valve, the range value is interpreted as a percentage value
   * between [0%..255%] giving the setting a 1% granularity. <p>
   *
   * For DPT 5.010 counter value, the 8-bit unsigned value is interpreted as a counter value in
   * range of [0-255].
   */
  public static class Unsigned8Bit implements DataType
  {

    // Private Instance Fields --------------------------------------------------------------------

    private int value = 0;
    private DataPointType dpt;


    // Constructors -------------------------------------------------------------------------------

    public Unsigned8Bit(DataPointType.Unsigned8BitValue dpt, int value)
    {
      if (value < 0 || value > 255)
        throw new Error("Unsigned 8-bit value range is [0-255], got " + value);

      this.value = value;
      this.dpt = dpt;
    }


    // Implements DataType ------------------------------------------------------------------------

    public int getDataLength()
    {
      return 2;
    }

    public byte[] getData()
    {
      return new byte[] { (byte)(value & 0xFF) };
    }

    public DataPointType getDataPointType()
    {
      return dpt;
    }
  }
}
