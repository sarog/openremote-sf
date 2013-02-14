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
package org.openremote.controller.protocol.elexolUSB;

/**
 * Enumeration of supported Elexol USB device I/O Ports (A,B and C).
 *
 * Not sure if Java Enum ain't more trouble then it's worth but I'll go
 * with it for the time being.
 *
 * @author <a href="mailto:johnfwhitmore@gmail.com">John Whitmore</a>
 */
public enum PortType
{
  PORT_A(new String ("A")),

  PORT_B(new String ("B")),

  PORT_C(new String ("C"));

  // Enum Fields ----------------------------------------------------------------------------------

  private String portString = null;
  
  // Enum Constructors ----------------------------------------------------------------------------

  private PortType(String portString)
  {
    this.portString = portString;
  }

  // Enum Methods ---------------------------------------------------------------------------------

  /**
   * @return true if the passed command is equal to this command, false otherwise
   */
  boolean isEqual(String port)
  {
    if (portString.equalsIgnoreCase(port))
    {
      return true;
    }
    return false;
  }

  public String toString() 
  {
    return portString;
  }
}
