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
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class Unsigned8Bit implements DataType
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

