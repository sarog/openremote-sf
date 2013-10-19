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
package org.openremote.controller.protocol.knx.datatype;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.math.BigInteger;
import java.util.Calendar;


/**
 *  TODO
 *
 * @author Kenneth Stridh
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class Date implements DataType
{

  // Constants ------------------------------------------------------------------------------------

  private final static BigDecimal KNX_FLOAT_MAXIMUM_PRECISION = new BigDecimal(1)
      .setScale(0, RoundingMode.HALF_UP); 

  // Class Members --------------------------------------------------------------------------------


  // Instance Fields ------------------------------------------------------------------------------

  private DataPointType dpt;
  private byte[] data;


  // Constructors ---------------------------------------------------------------------------------

  public Date(DataPointType dpt, int value)
  {
    this.dpt = dpt;

    data = convertToKNXFloat(value);
  }

  public Date(DataPointType dpt, byte[] value)
  {
    this.dpt = dpt;

    data = value;
  }


  // Implements DataType --------------------------------------------------------------------------

  @Override public int getDataLength()
  {
    return 4;
  }

  @Override public byte[] getData()
  {
    return data;
  }

  @Override public DataPointType getDataPointType()
  {
    return dpt;
  }

 public String resolve()
  {
    int dayOfMonth = data[0];
    int month = data[1];
    int year = data[2];

    if (year >= 90)
    {
      year = year + 1900;
    }

    else if (year < 90)
    {
      year =  year + 2000;
    }
	          
    
    String knxdate = String.format("%02d.%02d.%02d", dayOfMonth, month, year);
	            
    return knxdate;
  }

  
  // Private Instance Methods ---------------------------------------------------------------------


  
  private byte[] convertToKNXFloat(int value)
  {
    int dayOfMonth = 0;
    int month = 0;
    int year = 0;
  
    if (value == 0)
    {
      Calendar calendar = Calendar.getInstance();
	
      dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);	
      month      = calendar.get(Calendar.MONTH) +1;
      int PCyear = calendar.get(Calendar.YEAR);

      if (PCyear < 1990 || PCyear > 2089) 
      {
        throw new Error("The KNX year range is [1990-2089], your PC year is " + PCyear);
      }
      else if (PCyear < 2000 && PCyear > 1989)
      {
        year = PCyear -1900;
      }
      else
      {
        year =  PCyear -2000;
      }
    }
    else
    {
      dayOfMonth = (value / 10000);
      month = ((value - (dayOfMonth * 10000)) / 100 );
      year = (value - (dayOfMonth * 10000)- (month * 100));
    

      if (dayOfMonth < 1 || dayOfMonth > 31) 
      {
        throw new Error("dayOfMonth value range is [1-31], got " + dayOfMonth);
      }
    
      if (month < 1 || month > 12)
      {
        throw new Error("month value range is [1-12], got " + month);  
      }
    
    }
  
    return new byte[] { (byte)(dayOfMonth), (byte)(month), (byte)(year)};
  }

}