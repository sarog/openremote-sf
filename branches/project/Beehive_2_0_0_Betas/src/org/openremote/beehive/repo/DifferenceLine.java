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
package org.openremote.beehive.repo;


/**
 * DifferenceLine define the type of line's change 
 * @author Tomsky
 *
 */
public class DifferenceLine {
   public static final int EMPTY_NUMBER = -1;

   public static final char NOT_CHANGED = 'N';
   public static final char ADDED = 'A';
   public static final char DELETED = 'D';
   public static final char MODIFIED = 'M';

   protected int number = DifferenceLine.EMPTY_NUMBER;
   protected char type;
   protected String line;

   public DifferenceLine(int number, char type, String line) {
      this.number = number;
      this.type = type;
      this.line = line;
   }

   public int getNumber() {
      return this.number;
   }

   public void setNumber(int number) {
      this.number = number;
   }

   public char getType() {
      return this.type;
   }

   public void setType(char type) {
      this.type = type;
   }

   public String getLine() {
      return this.line;
   }

   public void setLine(String line) {
      this.line = line;
   }
}
