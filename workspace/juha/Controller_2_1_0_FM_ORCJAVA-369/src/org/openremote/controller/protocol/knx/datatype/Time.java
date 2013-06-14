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
public class Time implements DataType
{

  // Constants ------------------------------------------------------------------------------------

 private final static BigDecimal KNX_FLOAT_MAXIMUM_PRECISION = new BigDecimal(1)
     .setScale(0, RoundingMode.HALF_UP);
  
  // Class Members --------------------------------------------------------------------------------


  // Instance Fields ------------------------------------------------------------------------------

  private DataPointType dpt;
  private byte[] data;


  // Constructors ---------------------------------------------------------------------------------

  public Time(DataPointType dpt, int value)
  {
    this.dpt = dpt;

    data = convertToKNXFloat(value);
  }

  public Time(DataPointType dpt, byte[] value)
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
    int day = (data[0] & 0xE0) >> 5;
    int hour = (data[0]& 0x1F);
    int min = data[1];
    int sec = data[2];

    String[] days = new String[] {"", "Mon ", "Tue ", "Wed ", 
	                          "Thu ", "Fri ", "Sat ", "Sun "};

    String time = String.format("%02d:%02d:%02d", hour, min, sec);
	          
    String knxtime = (days[day]) + time ;
	            
    return knxtime;

  }

  
  // Private Instance Methods ---------------------------------------------------------------------


  private byte[] convertToKNXFloat(int value)
  {
    int dayOfWeek = 0;
    int hourOfDay = 0;
    int minute = 0;
    int second = 0;

    if (value == 0)
    {
      Calendar calendar = Calendar.getInstance();
      dayOfWeek  = calendar.get(Calendar.DAY_OF_WEEK) -1;
          
      if (dayOfWeek == 0)
      {
        dayOfWeek = 7;
      }
        
      hourOfDay  = calendar.get(Calendar.HOUR_OF_DAY);
      minute     = calendar.get(Calendar.MINUTE);
      second     = calendar.get(Calendar.SECOND);
	  
    }

    else
    {
      dayOfWeek = (value / (1000000) );
      hourOfDay = ((value - (dayOfWeek * 1000000))/ 10000);
      minute = ((value - (dayOfWeek * 1000000)-(hourOfDay * 10000)) / 100 );
      second = (value - (dayOfWeek * 1000000)-(hourOfDay * 10000) - (minute * 100));
    
      if (dayOfWeek < 1 || dayOfWeek > 7) 
      {
        throw new Error("DayOfWeek value range is [1-7], got " + dayOfWeek);
      }
    
      if (hourOfDay < 0 || hourOfDay > 23)
      {
        throw new Error("hourOfDay value range is [0-23], got " + hourOfDay);
      }

      if (minute < 0 || minute > 59)
      {
        throw new Error("minute value range is [0-59], got " + minute);	
      }

      if (second < 0  || second > 59)
      {
        throw new Error("second value range is [0-59], got " + second);	 
      }
    }
  
    int DayAndHour = (((dayOfWeek & 0x07) << 5) + (hourOfDay & 0x1F)) ;
	   
    return new byte[] { (byte)(DayAndHour), (byte)(minute), (byte)(second)};	    		  
  }

}
