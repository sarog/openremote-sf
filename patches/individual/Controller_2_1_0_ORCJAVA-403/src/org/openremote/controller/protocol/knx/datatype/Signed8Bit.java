/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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

/**
 * Unsigned 8-bit datatype as defined in KNX 1.1 Volume 3: System specifications Part 7:
 * Interworking, Chapter 2, Datapoint Types. <p>
 *
 * KNX Signed 8-bit datatype is a signed 8-bit value in the range of [-128..127],
 * encoded in 2's complement notation. <p>
 *
 * There are 2 datapoint types that use unsigned 8-bit datatype:
 *
 * <ol>
 * <li>DPT 6.001 - DPT_Percent_V8</li>
 * <li>DPT 6.010 - DPT_Value_1_Count</li>
 * </ol>
 *
 * For DPT 6.001 Percent V8, the range value is interpreted as a percentage between [-128%..127%]
 * giving the setting a 1% granularity.  <p>
 *
 * For DPT 6.010 counter value, the 8-bit signed value is interpreted as a counter value in
 * range of [-128..127].
 *
 * @author <a href="mailto:domo@slef.org">Stefan Langerman</a>
 * nearly identical to Unsigned8Bit.java by
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class Signed8Bit implements DataType
{

  // Private Instance Fields --------------------------------------------------------------------

  private int value = 0;
  private DataPointType dpt;


  // Constructors -------------------------------------------------------------------------------

  public Signed8Bit(DataPointType.Signed8BitValue dpt, int value)
  {
    // We are getting the value as a signed integer... this is java, after all.
    // Thus, the range we are ready to accept as representable within a byte is as follows.
	  
    if (value < -128 || value > 127) 
    {
      throw new Error("Signed 8-bit value range is [-128..127], got " + value);
    }
	  
    // Given that we passed the above test, we undo the 2-s complement interpretation
    // of the signed value.
    if (value < 0 ) 
    {
      this.value = value + 256;
    }
    else 
    {
      this.value = value;
    }

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


  public int resolve()
  {
    int val = (value < 128)? value : (value - 256);

    if (dpt == DataPointType.Signed8BitValue.PERCENT_V8)
    {
      return val;
    }

    else if (dpt == DataPointType.Signed8BitValue.VALUE_1_COUNT)
    {
      return val;
    }

    else
    {
      throw new Error("Unrecognized unsigned 8-bit datapoint type " + dpt);
    }
  }
}

