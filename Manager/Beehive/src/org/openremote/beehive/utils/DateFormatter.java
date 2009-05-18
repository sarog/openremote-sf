/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.beehive.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Class DateFormatter.
 */
public class DateFormatter {
    
    /** The Constant ABSOLUTE. */
    public static final int ABSOLUTE = 0;
    
    /** The Constant RELATIVE. */
    public static final int RELATIVE = 1;
    
    /** The Constant ABSOLUT_FORMAT. */
    protected static final String ABSOLUT_FORMAT = "dd.MM.yyyy HH:mm:ss";

    /**
     * Format.
     * 
     * @param date the date
     * 
     * @return the string
     */
    public static String format(Date date) {
        return DateFormatter.format(date, DateFormatter.RELATIVE);
    }

    /**
     * Format.
     * 
     * @param date the date
     * @param type the type
     * 
     * @return the string
     */
    public static String format(Date date, int type) {
        if (date == null) {
            return "";
        }        
        if (type == DateFormatter.ABSOLUTE) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormatter.ABSOLUT_FORMAT);
            return dateFormat.format(date);
        } else if (type == DateFormatter.RELATIVE) {
            return DateFormatter.getAge(date).toString();
        } else {
            return date.toString();
        }
    }

    /**
     * Gets the age.
     * 
     * @param date the date
     * 
     * @return the age
     */
    protected static Age getAge(Date date) {
        PastTime pastTime = new PastTime(date, new Date());
        return new Age(
                pastTime.getYears(),
                pastTime.getMonths(),
                pastTime.getWeeks(),
                pastTime.getDays(),
                pastTime.getHours(),
                pastTime.getMinutes());        
    }
}
