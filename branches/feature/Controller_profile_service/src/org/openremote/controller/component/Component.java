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
package org.openremote.controller.component;

import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.control.Status;

/**
 * Super class of all components
 * 
 * @author Handy.Wang 2009-12-31
 */
public abstract class Component {
   
   /** The status. */
   private Status status;
   
   /**
    * Instantiates a new Component.
    */
   public Component() {
       super();
       status = new Status();
   }
   
   
   /**
    * Gets the status command.
    * 
    * @return the status command
    */
   public StatusCommand getStatusCommand() {
       return status.getStatusCommand();
   }

   /**
    * Gets the status.
    * 
    * @return the status
    */
   public Status getStatus() {
       return status;
   }

   /**
    * Sets the status.
    * 
    * @param status the new status
    */
   public void setStatus(Status status) {
       this.status = status;
   }

}
