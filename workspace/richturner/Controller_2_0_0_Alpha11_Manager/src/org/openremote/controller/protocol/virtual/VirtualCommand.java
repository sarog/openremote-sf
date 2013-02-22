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
package org.openremote.controller.protocol.virtual;

import java.util.Map;
import java.util.HashMap;

import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.component.EnumSensorType;

/**
 * OpenRemote virtual command implementation.  <p>
 *
 * Maintains a virtual-machine-wide state for each address. Default implementation maintains
 * a simple on/off status.  <p>
 *
 * Other types of virtual decices may be added later.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class VirtualCommand implements ExecutableCommand, StatusCommand
{

  // Class Members --------------------------------------------------------------------------------

  private final static Map<String, String>virtualDevices = new HashMap<String, String>(20);



  // Instance Fields ------------------------------------------------------------------------------

  /**
   * The address for this particular 'command' instance.
   */
  private String address = null;

  /**
   * The command string for this particular 'command' instance.
   */
  private String command = null;



  // Constructors ---------------------------------------------------------------------------------

  public VirtualCommand(String address, String command)
  {
    this.address = address;
    this.command = command;
  }



  // Implements ExecutableCommand -----------------------------------------------------------------

  public void send()
  {
    virtualDevices.put(address, command);  
  }



  // Implements StatusCommand ---------------------------------------------------------------------

  public String read(EnumSensorType sensorType, Map<String, String> stateMap)
  {
    String state = virtualDevices.get(address);

    if (state == null)
    {
      return "off";
    }

    else if (state.equalsIgnoreCase("on"))
    {
      return "on";
    }

    else if (state.equalsIgnoreCase("off"))
    {
      return "off";
    }

    else
    {
      return "off";
    }
  }
}

