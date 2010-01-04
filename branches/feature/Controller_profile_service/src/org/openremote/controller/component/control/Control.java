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
package org.openremote.controller.component.control;

import java.util.ArrayList;
import java.util.List;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.component.Component;

/**
 * The Class Control.
 * 
 * @author Handy.Wang 2009-10-15
 */
public abstract class Control extends Component {
   
    public static String CURRENT_STATUS = "OFF";

    /** The Constant CONTROL_COMMAND_REF_ATTRIBUTE_NAME. */
    public static final String CONTROL_COMMAND_REF_ATTRIBUTE_NAME = "ref";

    /** The Constant STATUS_ELEMENT_NAME. */
    public static final String STATUS_ELEMENT_NAME = "status";
    
    public static final String INCLUDE_ELEMENT_NAME = "include";
    
    public static final String INCLUDE_TYPE_SENSOR = "sensor";
    
    public static final String INCLUDE_TYPE_ATTRIBUTE_NAME = "type";
    
    /** The Constant DELAY_ELEMENT_NAME. */
    public static final String DELAY_ELEMENT_NAME = "delay";
    
    public static final String COMMAND_ELEMENT_NAME= "command";
    
    /** All commands a certain operation contains. */
    private List<ExecutableCommand> executableCommands;
    
    protected List<String> availableActions;
    
    /**
     * Instantiates a new control.
     */
    public Control() {
        super();
        executableCommands = new ArrayList<ExecutableCommand>();
        availableActions = new ArrayList<String>();
        availableActions.addAll(getAvailableActions());
    }
    
    /** All available actions of sub controls */
    protected abstract List<String> getAvailableActions();
    
    public boolean isValidActionWith(String actionParam) {
       for (String action : availableActions) {
          if (action.equalsIgnoreCase(actionParam)) {
             return true;
          }
       }
       return false;
    }
    
    /**
     * Gets the executable commands.
     * 
     * @return the executable commands
     */
    public List<ExecutableCommand> getExecutableCommands() {
       return executableCommands;
    }
    
    /**
     * add executable command into executable command list.
     */
    public void addExecutableCommand(ExecutableCommand executablecommand) {
       executableCommands.add(executablecommand);
    }
    
}
