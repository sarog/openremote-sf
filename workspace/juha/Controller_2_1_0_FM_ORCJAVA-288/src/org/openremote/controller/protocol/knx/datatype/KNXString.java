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

import java.nio.charset.Charset;

/**
 * String datatype as defined in KNX 1.1 Volume 3: System specifications Part 7:
 * Interworking, Chapter 2, Datapoint Types. <p>
 *
 * KNX string datatype is a 14 byte value. <p>
 *
 * There are 2 datapoint types that use string datatype:
 *
 * <ol>
 * <li>DPT 16.000 - DPT_String_ASCII</li>
 * <li>DPT 16.001 - DPT_String_8859_1</li>
 * </ol>
 *
 * @author <a href="mailto:marcus@openremote.org">Marcus Redeker</a>
 */
public class KNXString implements DataType
{

  // Private Instance Fields --------------------------------------------------------------------

  private byte[] data;
  private DataPointType dpt;


  // Constructors -------------------------------------------------------------------------------

  public KNXString(DataPointType.KNXString dpt, String value)
  {
    if (value == null) 
    {
      throw new Error("String value cannot be null, got <null>");
    }
    if (value.trim().length() > 14) 
    {
      throw new Error("String value can only be 14 chars long, but '" + value + "' is " + value.trim().length() + " chars");
    }

    this.data = new byte[]{0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
    if (dpt == DataPointType.KNXString.STRING_ASCII)
    {
       System.arraycopy(value.getBytes(Charset.forName("US-ASCII")), 0, data, 0, value.length());
    }
    else if (dpt == DataPointType.KNXString.STRING_8859_1)
    {
       System.arraycopy(value.getBytes(Charset.forName("ISO-8859-1")), 0, data, 0, value.length());
    }
    
    this.dpt = dpt;
  }

  public KNXString(DataPointType.KNXString dpt,byte[] value)
  {
    this.data = value;
    this.dpt = dpt;
  }

  // Implements DataType ------------------------------------------------------------------------

  public int getDataLength()
  {
    return 15;
  }

  public byte[] getData()
  {
    return this.data;
  }

  public DataPointType getDataPointType()
  {
    return dpt;
  }


  public String resolve()
  {
    if (dpt == DataPointType.KNXString.STRING_ASCII)
    {
      return new String(this.data, Charset.forName("US-ASCII")).trim();
    }
    else if (dpt == DataPointType.KNXString.STRING_8859_1)
    {
       return new String(this.data, Charset.forName("ISO-8859-1")).trim();
    }
    else
    {
      throw new Error("Unrecognized string datapoint type " + dpt);
    }
  }
}

