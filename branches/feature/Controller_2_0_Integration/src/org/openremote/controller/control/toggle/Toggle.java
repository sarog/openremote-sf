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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /** The states. */
    private Map<String, List<ExecutableCommand>> states;

    /**
     * Instantiates a new toggle.
     */
    public Toggle() {
        super();
        setStatus(new Status(new NoStatusCommand()));
        states = new HashMap<String, List<ExecutableCommand>>();
    }
    
   /* (non-Javadoc)
     * @see org.openremote.controller.control.Control#getExecutableCommands()
     */
    @Override
    public List<ExecutableCommand> getExecutableCommands() {
       String currentStatus = getStatus().getStatusCommand().read();
       String nextStatus = "";
       if (isContainStatus(currentStatus)) {
          nextStatus = getNextStatus(currentStatus);
       }
       if (states.get(nextStatus) == null) {
          return new ArrayList<ExecutableCommand>();
       }
       return states.get(nextStatus);
    }

   /**
    * Checks if is contain state.
    * 
    * @param currentStatus the current status
    * 
    * @return true, if is contain state
    */
   private boolean isContainStatus(String currentStatus) {
       if (currentStatus == null || "".equals(currentStatus)) {
          return false;
       }
       for (String switchStatus : SWITCH_STATUSES) {
          if (switchStatus.equalsIgnoreCase(currentStatus)) {
             return true;
          }
       }
       return false;
    }
   
   /**
    * Gets the next state.
    * 
    * @param currentStatus the current status
    * 
    * @return the next state
    */
   private String getNextStatus(String currentStatus) {
      int currentStatusIndex = -1;
      for (int i = 0; i < SWITCH_STATUSES.length; i++) {
         String status = SWITCH_STATUSES[i];
         if (status.equalsIgnoreCase(currentStatus)) {
            currentStatusIndex = i;
         }
      }
      return SWITCH_STATUSES[(currentStatusIndex+1)%SWITCH_STATUSES.length];
   }

   /**
    * Gets the states.
    * 
    * @return the states
    */
   public Map<String, List<ExecutableCommand>> getStates() {
      return states;
   }

   /**
    * Sets the states.
    * 
    * @param states the states
    */
   public void setStates(Map<String, List<ExecutableCommand>> states) {
      this.states = states;
   }
   
}
