/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.controller.protocol.x10;

import org.openremote.controller.event.Event;

/**
 * The X10 Event.
 * 
 * @author Dan 2009-4-20
 */
public class X10Event extends Event {
   
   /** The address. */
   private String address;
   
   /** The command. */
   private String command;

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
    * @param address the new address
    */
   public void setAddress(String address) {
      this.address = address;
   }

   /**
    * Gets the command.
    * 
    * @return the command
    */
   public String getCommand() {
      return command;
   }

   /**
    * Sets the command.
    * 
    * @param command the new command
    */
   public void setCommand(String command) {
      this.command = command;
   }
   
   
}
