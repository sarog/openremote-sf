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
package org.openremote.controller.control;

import org.openremote.controller.command.StatusCommand;

/**
 * The Class Status.
 * 
 * @author Handy.Wang 2009-10-15
 */
public class Status {

    /** The status command. */
    private StatusCommand statusCommand;
    
    /**
     * Instantiates a new status.
     */
    public Status() {
        super();
    }

    /**
     * Instantiates a new status.
     * 
     * @param statusCommand the status command
     */
    public Status(StatusCommand statusCommand) {
        super();
        this.statusCommand = statusCommand;
    }

    /**
     * Gets the status command.
     * 
     * @return the status command
     */
    public StatusCommand getStatusCommand() {
        return statusCommand;
    }

    /**
     * Sets the status command.
     * 
     * @param statusCommand the new status command
     */
    public void setStatusCommand(StatusCommand statusCommand) {
        this.statusCommand = statusCommand;
    }
}
