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

/**
 * Super class of all components
 * 
 * @author Handy.Wang 2009-12-31
 */
public abstract class Component {
   
   private Sensor sensor;
   
   /**
    * Instantiates a new Component.
    */
   public Component() {
       super();
       sensor = new Sensor();
   }
   
   
   /**
    * Gets the status command.
    * 
    * @return the status command
    */
   public StatusCommand getStatusCommand() {
       return sensor.getStatusCommand();
   }

   protected Sensor getSensor() {
      return sensor;
   }


   public void setSensor(Sensor sensor) {
      this.sensor = sensor;
   }
}
