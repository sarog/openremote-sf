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
package org.openremote.controller.control.gesture;

import org.openremote.controller.command.NoStatusCommand;
import org.openremote.controller.control.Control;
import org.openremote.controller.control.Status;
/**
 * This class is used to store the information for a gesture. 
 * @author Javen
 *
 */
public class Gesture extends Control {
   public Gesture(){
      super();
      setStatus(new Status(new NoStatusCommand()));
   }
}
