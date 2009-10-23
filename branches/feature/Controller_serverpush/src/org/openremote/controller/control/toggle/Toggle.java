/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.controller.control.toggle;

import java.util.ArrayList;
import java.util.List;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.NoStatusCommand;
import org.openremote.controller.control.Control;
import org.openremote.controller.control.Status;

/**
 * The Class Toggle.
 * 
 * @author Handy.Wang 2009-10-15
 */
public class Toggle extends Control {
    
    /** The Constant AVAILABLE_ACTIONS. */
    public static final String[] AVAILABLE_ACTIONS = { "next", "status" };
   
    /** The switch states. */
    public static final String[] SWITCH_STATUSES = {"ON", "OFF"};

    /** All commands the operation(1 or 2 or 3...) contains. */
    private List<ExecutableCommand> commands;

    /**
     * Instantiates a new toggle.
     */
    public Toggle() {
        super();
        setStatus(new Status(new NoStatusCommand()));
        commands = new ArrayList<ExecutableCommand>();
    }
    
   /* (non-Javadoc)
     * @see org.openremote.controller.control.Control#getExecutableCommands()
     */
    @Override
    public List<ExecutableCommand> getExecutableCommands() {
       return commands;
    }
   
    public void addExecutableCommand(ExecutableCommand command) {
       commands.add(command);
    }

}
