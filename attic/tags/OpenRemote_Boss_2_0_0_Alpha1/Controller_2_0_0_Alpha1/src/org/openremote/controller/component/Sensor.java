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

import org.openremote.controller.command.NoStatusCommand;
import org.openremote.controller.command.StatusCommand;

/**
 * Sensor class for referencing status command
 * 
 * @author Handy.Wang 2010-01-04
 *
 */
public class Sensor {
   
   private int sensorID;
   
   private StatusCommand statusCommand;
   
   public Sensor() {
      super();
      this.statusCommand = new NoStatusCommand();
   }

   public Sensor(StatusCommand statusCommand) {
      super();
      this.statusCommand = statusCommand;
   }

   public int getSensorID() {
      return sensorID;
   }

   public void setSensorID(int sensorID) {
      this.sensorID = sensorID;
   }

   public StatusCommand getStatusCommand() {
      return statusCommand;
   }

   public void setStatusCommand(StatusCommand statusCommand) {
      this.statusCommand = statusCommand;
   }
}
