/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.controller.protocol.elexolUSB;
//package org.openremote.controller.protocol.x10;

/**
 * Enumeration of supported Elexol USB device Pins.
 * 
 * 1 - 8
 *
 * @author John Whitmore
 * @author <a href="mailto:johnfwhitmore@gmail.com">Juha Lindfors</a>
 */
public enum PinType
{
    PIN_1(1),
    PIN_2(2),
    PIN_3(3),
    PIN_4(4),
    PIN_5(5),
    PIN_6(6),
    PIN_7(7),
    PIN_8(8);

  // Enum Fields ----------------------------------------------------------------------------------

  private int pin = 0;
  
  // Enum Constructors ----------------------------------------------------------------------------

  private PinType(int pin)
  {
    this.pin = pin;
  }

  public static PinType convert( String pinString ) {

      int pin = Integer.parseInt(pinString);

      for ( PinType current : values() ) {
	  if ( current.ordinal() == pin ) {
	      return current;
	  }
      }
      return null;
  }

  public byte toByte(){
      return (byte)this.pin;
  }
}
