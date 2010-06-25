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
package org.openremote.controller.command;

import org.openremote.controller.component.EnumSensorType;


/**
 * interface command
 * 
 * @author Handy.Wang 2009-10-15
 */
public interface StatusCommand extends Command {

   /**
    * Read raw status from device and return it.<br />
    * 
    * And you also can translate the raw status to readable string with sensoryType and then return it.
    * 
    * @return the string
    */
   public String read(EnumSensorType sensoryType);
   
    
}
