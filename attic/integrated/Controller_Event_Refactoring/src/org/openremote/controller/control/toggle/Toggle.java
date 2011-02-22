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

import java.util.List;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.control.Control;

/**
 * The Class Toggle.
 * 
 * @author Handy.Wang 2009-10-15
 */
public class Toggle extends Control {
    
    /** The Constant SWITCH_TOGGLE. */
    public static final int SWITCH_TOGGLE = 2;
    
    /** The Constant MULTI_STATES_TOGGLE. */
    public static final int MULTI_STATES_TOGGLE = 3;
    
    /** The Constant MULTI_STATES_SWITCH_COMMAND. */
    public static final String MULTI_STATES_SWITCH_COMMAND = "next";

    /** The next state. */
    private State nextState;

    /**
     * Instantiates a new toggle.
     */
    public Toggle() {
        super();
        nextState = new State();
    }
    
    /* (non-Javadoc)
     * @see org.openremote.controller.control.Control#getExecutableCommands()
     */
    @Override
    public List<ExecutableCommand> getExecutableCommands() {
        return nextState.getExecutableCommands();
    }
    
    /**
     * Gets the state.
     * 
     * @return the state
     */
    public State getState() {
        return nextState;
    }

    /**
     * Sets the state.
     * 
     * @param state the new state
     */
    public void setState(State state) {
        this.nextState = state;
    }
    
}
