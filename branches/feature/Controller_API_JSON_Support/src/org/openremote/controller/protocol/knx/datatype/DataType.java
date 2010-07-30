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
 * @see DataType.Boolean
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
   * <li>DPT 1.014 - DPT_InputSource</li>
   * </ol>
   */
  static class Boolean implements DataType
  {

    // Constants ----------------------------------------------------------------------------------


    /**
     * Value type for 'Boolean' datatype (KNX 1.1 Vol 3: Part 7.2 - Datapoint Types): {@value}
     */
    private final static byte DATATYPE_BOOLEAN_ONE = 0x01;

    /**
     * Value type for 'Boolean' datatype (KNX 1.1 Vol 3: Part 7.2 - Datapoint Types): {@value}
     */
    private final static byte DATATYPE_BOOLEAN_ZERO = 0x00;

    /**
     * DPT 1.001 - DPT_Switch. Value 0 = OFF, general use.
     */
    public final static Boolean OFF = new Boolean(
        "1.001",
        DataPointType.BooleanDataPointType.SWITCH,
        DATATYPE_BOOLEAN_ZERO
    );

    /**
     * DPT 1.001 - DPT_Switch. Value 1 = ON, general use.
     */
    public final static Boolean ON = new Boolean(
        "1.001",
        DataPointType.BooleanDataPointType.SWITCH,
        DATATYPE_BOOLEAN_ONE
    );

    /**
     * DPT 1.002 - DPT_Bool. Value 0 = FALSE, general use.
     */
    public final static Boolean FALSE = new Boolean(
        "1.002",
        DataPointType.BooleanDataPointType.BOOL,
        DATATYPE_BOOLEAN_ZERO
    );

    /**
     * DPT 1.002 - DPT_Bool. Value 1 = TRUE, general use.
     */
    public final static Boolean TRUE = new Boolean(
        "1.002",
        DataPointType.BooleanDataPointType.BOOL,
        DATATYPE_BOOLEAN_ONE
    );

    /**
     * DPT 1.003 - DPT_Enable. Value 0 = DISABLE, general use.
     */
    public final static Boolean DISABLE = new Boolean(
        "1.003",
        DataPointType.BooleanDataPointType.ENABLE,
        DATATYPE_BOOLEAN_ZERO
    );

    /**
     * DPT 1.003 - DPT_Enable. Value 1 = ENABLE, general use.
     */
    public final static Boolean ENABLE = new Boolean(
        "1.003",
        DataPointType.BooleanDataPointType.ENABLE,
        DATATYPE_BOOLEAN_ONE
    );

    /**
     * DPT 1.004 - DPT_Ramp. Value 0 = NO_RAMP, functional blocks only.
     */
    public final static Boolean NO_RAMP = new Boolean(
        "1.004",
        DataPointType.BooleanDataPointType.RAMP,
        DATATYPE_BOOLEAN_ZERO
    );

    /**
     * DPT 1.004 - DPT_Ramp. Value 1 = RAMP, functional blocks only.
     */
    public final static Boolean RAMP = new Boolean(
        "1.004",
        DataPointType.BooleanDataPointType.RAMP,
        DATATYPE_BOOLEAN_ONE
    );

    /**
     * DPT 1.005 - DPT_Alarm. Value 0 = NO_ALARM, functional blocks only.
     */
    public final static Boolean NO_ALARM = new Boolean(
        "1.005",
        DataPointType.BooleanDataPointType.ALARM,
        DATATYPE_BOOLEAN_ZERO
    );

    /**
     * DPT 1.005 - DPT_Alarm. Value 1 = ALARM, functional blocks only.
     */
    public final static Boolean ALARM = new Boolean(
        "1.005",
        DataPointType.BooleanDataPointType.ALARM,
        DATATYPE_BOOLEAN_ONE
    );

    /**
     * DPT 1.006 - DPT_BinaryValue. Value 0 = LOW, functional blocks only.
     */
    public final static Boolean LOW = new Boolean(
        "1.006",
        DataPointType.BooleanDataPointType.BINARY_VALUE,
        DATATYPE_BOOLEAN_ZERO
    );

    /**
     * DPT 1.006 - DPT_BinaryValue. Value 1 = HIGH, functional blocks only.
     */
    public final static Boolean HIGH = new Boolean(
        "1.006",
        DataPointType.BooleanDataPointType.BINARY_VALUE,
        DATATYPE_BOOLEAN_ONE
    );

    /**
     * DPT 1.007 - DPT_Step. Value 0 = DECREASE, functional blocks only.
     */
    public final static Boolean DECREASE = new Boolean(
        "1.007",
        DataPointType.BooleanDataPointType.STEP,
        DATATYPE_BOOLEAN_ZERO
    );

    /**
     * DPT 1.007 - DPT_Step. Value 1 = INCREASE, functional blocks only.
     */
    public final static Boolean INCREASE = new Boolean(
        "1.007",
        DataPointType.BooleanDataPointType.STEP,
        DATATYPE_BOOLEAN_ONE
    );

    /**
     * DPT 1.008 - DPT_UpDown. Value 0 = UP, general use.
     */
    public final static Boolean UP = new Boolean(
        "1.008",
        DataPointType.BooleanDataPointType.UP_DOWN,
        DATATYPE_BOOLEAN_ZERO
    );

    /**
     * DPT 1.008 - DPT_UpDown. Value 1 = DOWN, general use.
     */
    public final static Boolean DOWN = new Boolean(
        "1.008",
        DataPointType.BooleanDataPointType.UP_DOWN,
        DATATYPE_BOOLEAN_ONE
    );

    /**
     * DPT 1.009 - DPT_OpenClose. Value 0 = OPEN, general use.
     */
    public final static Boolean OPEN = new Boolean(
        "1.009",
        DataPointType.BooleanDataPointType.OPEN_CLOSE,
        DATATYPE_BOOLEAN_ZERO
    );

    /**
     * DPT 1.009 - DPT_OpenClose. Value 1 = CLOSE, general use.
     */
    public final static Boolean CLOSE = new Boolean(
        "1.009",
        DataPointType.BooleanDataPointType.OPEN_CLOSE,
        DATATYPE_BOOLEAN_ONE
    );

    /**
     * DPT 1.010 - DPT_Start. Value 0 = STOP, general use.
     */
    public final static Boolean STOP = new Boolean(
        "1.010",
        DataPointType.BooleanDataPointType.START,
        DATATYPE_BOOLEAN_ZERO
    );

    /**
     * DPT 1.010 - DPT_Start. Value 1 = START, general use.
     */
    public final static Boolean START = new Boolean(
        "1.010",
        DataPointType.BooleanDataPointType.START,
        DATATYPE_BOOLEAN_ONE
    );

    /**
     * DPT 1.011 - DPT_State. Value 0 = INACTIVE, functional blocks only.
     */
    public final static Boolean INACTIVE = new Boolean(
        "1.011",
        DataPointType.BooleanDataPointType.STATE,
        DATATYPE_BOOLEAN_ZERO
    );

    /**
     * DPT 1.011 - DPT_State. Value 1 = ACTIVE, functional blocks only.
     */
    public final static Boolean ACTIVE = new Boolean(
        "1.011",
        DataPointType.BooleanDataPointType.STATE,
        DATATYPE_BOOLEAN_ONE
    );

    /**
     * DPT 1.012 - DPT_Invert. Value 0 = NOT INVERTED, functional blocks only.
     */
    public final static Boolean NOT_INVERTED = new Boolean(
        "1.012",
        DataPointType.BooleanDataPointType.INVERT,
        DATATYPE_BOOLEAN_ZERO
    );

    /**
     * DPT 1.012 - DPT_Invert. Value 1 = INVERTED, functional blocks only.
     */
    public final static Boolean INVERTED = new Boolean(
        "1.012",
        DataPointType.BooleanDataPointType.INVERT,
        DATATYPE_BOOLEAN_ONE
    );

    /**
     * DPT 1.013 - DPT_DimSendStyle. Value 0 = START_STOP, functional blocks only.
     */
    public final static Boolean START_STOP = new Boolean(
        "1.013",
        DataPointType.BooleanDataPointType.DIM_SEND_STYLE,
        DATATYPE_BOOLEAN_ZERO
    );

    /**
     * DPT 1.013 - DPT_DimSendStyle. Value 1 = CYCLICALLY, functional blocks only.
     */
    public final static Boolean CYCLICALLY = new Boolean(
        "1.013",
        DataPointType.BooleanDataPointType.DIM_SEND_STYLE,
        DATATYPE_BOOLEAN_ONE
    );

    /**
     * DPT 1.014 - DPT_InputSource. Value 0 = FIXED, functional blocks only.
     */
    public final static Boolean FIXED = new Boolean(
        "1.014",
        DataPointType.BooleanDataPointType.INPUT_SOURCE,
        DATATYPE_BOOLEAN_ZERO
    );

    /**
     * DPT 1.014 - DPT_InputSource. Value 1 = CALCULATED, functional blocks only.
     */
    public final static Boolean CALCULATED = new Boolean(
        "1.014",
        DataPointType.BooleanDataPointType.INPUT_SOURCE,
        DATATYPE_BOOLEAN_ONE
    );


    /**
     * Datatype used for Group Value Read service with 'SWITCH' device.
     *
     * @see org.openremote.controller.protocol.knx.datatype.DataPointType.BooleanDataPointType#SWITCH
     */
    public final static DataType READ_SWITCH = new DataType()
    {
      public int getDataLength()
      {
        return 1;
      }

      public byte[] getData()
      {
        return new byte[] { 0x00 };
      }

      public DataPointType getDataPointType()
      {
        return DataPointType.BooleanDataPointType.SWITCH;
      }
    };



    // Class Members ------------------------------------------------------------------------------


    /**
     * TODO
     *
     * @param apdu
     * @return
     */
    public static Boolean createSwitchResponse(byte[] apdu)
    {
      return new Boolean(
          "1.001",
          DataPointType.BooleanDataPointType.SWITCH,
          (byte)(apdu[1] & 0x3F)
      );
    }



    // Private Instance Fields --------------------------------------------------------------------

    /**
     * The boolean value held by this datatype instance.
     */
    private byte value = 0x00;

    /**
     * Datapoint type for this boolean datatype.
     */
    private DataPointType.BooleanDataPointType dataPointType;


    // Constructors -------------------------------------------------------------------------------

    /**
     * New KNX Boolean datatype with a given value. Valid values are 0x00 and 0x01.
     *
     * @param dptID   TODO
     * @param dpt     TODO
     * @param value   either 0x00 or 0x01, see constants in this class for how these
     *                two values are encoded (on/off, start/stop, etc.)
     */
    private Boolean(String dptID, DataPointType.BooleanDataPointType dpt, byte value)
    {
      if (value < 0 || value > 1)
        throw new Error("Implementation Error: Boolean value must be either 1 or 0.");

      this.value = value;
      this.dataPointType = dpt;
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

    /**
     * {@inheritDoc}
     */
    public DataPointType getDataPointType()
    {
      return dataPointType;
    }

    /**
     * TODO
     *
     * @param value
     * @return
     */
    public DataType.Boolean getEncodingForValue(int value)
    {
      switch (dataPointType)
      {
        case SWITCH :
          return (value == 0) ? DataType.Boolean.OFF : DataType.Boolean.ON;

        case BOOL:
          return (value == 0) ? DataType.Boolean.FALSE : DataType.Boolean.TRUE;

        case ENABLE:
          return (value == 0) ? DataType.Boolean.DISABLE : DataType.Boolean.ENABLE;

        case RAMP:
          return (value == 0) ? DataType.Boolean.NO_RAMP : DataType.Boolean.RAMP;

        case ALARM:
          return (value == 0) ? DataType.Boolean.NO_ALARM : DataType.Boolean.ALARM;

        case BINARY_VALUE:
          return (value == 0) ? DataType.Boolean.LOW : DataType.Boolean.HIGH;

        case STEP:
          return (value == 0) ? DataType.Boolean.DECREASE : DataType.Boolean.INCREASE;

        case UP_DOWN:
          return (value == 0) ? DataType.Boolean.UP : DataType.Boolean.DOWN;

        case OPEN_CLOSE:
          return (value == 0) ? DataType.Boolean.OPEN : DataType.Boolean.CLOSE;

        case START:
          return (value == 0) ? DataType.Boolean.STOP : DataType.Boolean.START;

        case STATE:
          return (value == 0) ? DataType.Boolean.INACTIVE : DataType.Boolean.ACTIVE;

        case INVERT:
          return (value == 0) ? DataType.Boolean.NOT_INVERTED : DataType.Boolean.INVERTED;

        case DIM_SEND_STYLE:
          return (value == 0) ? DataType.Boolean.START_STOP : DataType.Boolean.CYCLICALLY;

        case INPUT_SOURCE:
          return (value == 0) ? DataType.Boolean.FIXED : DataType.Boolean.CALCULATED;

        default:

          throw new Error("Unknown data point type : " + dataPointType);
      }

    }

  }


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
    private Boolean controlBit;
    private int value = 0;


    // Constructors -------------------------------------------------------------------------------

    public Controlled3Bit(DataPointType.Control3BitDataPointType dpt, Boolean controlBitValue, int value)
    {
      if (controlBitValue != Boolean.INCREASE && controlBitValue != Boolean.DECREASE &&
          controlBitValue != Boolean.UP && controlBitValue != Boolean.DOWN &&
          controlBitValue != Boolean.FIXED && controlBitValue != Boolean.CALCULATED)
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
