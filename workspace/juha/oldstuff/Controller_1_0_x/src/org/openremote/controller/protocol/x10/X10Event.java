/* 
 * OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.protocol.x10;

import org.apache.log4j.Logger;
import org.openremote.controller.event.Event;

/**
 * The X10 Event.
 * 
 * @author Dan 2009-4-20
 * @author Jerome Velociter
 */
public class X10Event extends Event {

   /** The address. */
   private String address;

   /** The command. */
   private X10Command command;

   /** Controller manager */
   private X10ControllerManager controllerManager;

   private final static Logger log = Logger.getLogger(X10EventBuilder.X10_LOG_CATEGORY);

   public X10Event(X10ControllerManager manager, String address, X10Command command) {
      this.controllerManager = manager;
      this.address = address;
      this.command = command;
   }

   /**
    * Gets the address.
    * 
    * @return the address
    */
   public String getAddress() {
      return address;
   }

   /**
    * Sets the address.
    * 
    * @param address
    *           the new address
    */
   public void setAddress(String address) {
      this.address = address;
   }

   /**
    * Gets the command.
    * 
    * @return the command
    */
   public X10Command getCommand() {
      return command;
   }

   /**
    * Sets the command.
    * 
    * @param command
    *           the new command
    */
   public void setCommand(X10Command command) {
      this.command = command;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void exec() {

      X10Controller device;

      try {
         device = this.controllerManager.getDevice();

         device.send(this.getAddress(), command);

      } catch (ConnectionException e) {
         log.error(e);
      }

   }

}
