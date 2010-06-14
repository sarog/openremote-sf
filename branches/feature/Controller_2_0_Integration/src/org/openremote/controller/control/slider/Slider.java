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
package org.openremote.controller.control.slider;

import org.openremote.controller.command.NoStatusCommand;
import org.openremote.controller.control.Control;
import org.openremote.controller.control.Status;

/**
 * Slider control for controlling devices with slide action.
 * 
 * @author Handy.Wang 2009-11-10
 */
public class Slider extends Control {

   /** The actions which slider allows. */
   public static final String[] AVAILABLE_ACTIONS = { "status" };
   
   /** The container element name of executable command refence of slider. */
   public static final String EXE_CONTENT_ELEMENT_NAME = "exe";

   public Slider() {
      super();
      setStatus(new Status(new NoStatusCommand()));
   }
}
