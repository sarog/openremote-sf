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
 * Enumeration of supported Elexol USB device  commands that can be sent over the USB.
 * 
 * Right now supports ON, OFF and PULSE
 *
 * @author John Whitmore
 * @author <a href="mailto:johnfwhitmore@gmail.com">Juha Lindfors</a>
 */
public enum CommandType
{

  /**
   * Switches on the target Output Pin
   */
  SWITCH_ON(new String("ON")),

  /**
   * Switches off the target Output Pin
   */
  SWITCH_OFF(new String("OFF")),

  /**
   * Pulses the target Output Pin
   */
  PULSE(new String("PULSE"));

  // Enum Fields ----------------------------------------------------------------------------------

  private String command = null;
  
  // Enum Constructors ----------------------------------------------------------------------------

  private CommandType(String command)
  {
    this.command = command;
  }

  // Enum Methods ---------------------------------------------------------------------------------

  /**
   * @return true if the passed command is equal to this command, false otherwise
   */
  boolean isEqual(String command)
  {
      if (this.command.equalsIgnoreCase(command))
      {
        return true;
      }

    return false;
  }
}
