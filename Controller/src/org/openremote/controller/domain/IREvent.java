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
package org.openremote.controller.domain;

import org.openremote.controller.commander.Commander;
import org.openremote.controller.commander.IRCommander;



/**
 * The Class IREvent.
 * 
 * @author Dan
 */
public class IREvent extends Event {
   
   public final static String NODE_NAME = "irEvent"; 
   private String name;
   private String command;
   
   public IREvent(String name,String command) {
      super();
      this.command = command;
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getCommand() {
      return command;
   }

   public void setCommand(String command) {
      this.command = command;
   }

   @Override
   public Commander getCommander() {
      Commander cmd = new IRCommander();
      cmd.setEvent(this);
      return cmd;
   }

}
