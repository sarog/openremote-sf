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
package org.openremote.controller.protocol.infrared;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.openremote.controller.event.Event;

/**
 * The Infrared Event.
 * 
 * @author Dan 2009-4-20
 */
public class IREvent extends Event {
   
   /** The logger. */
   private static Logger logger = Logger.getLogger(IREvent.class.getName());
   
   /** The remote device name. This name MUST be the name defined in lircd.conf */
   private String name;
   
   /** The button command. Such as menu, play etc. */
   private String command;
   

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

   /**
    * Gets the name.
    * 
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * Sets the name.
    * 
    * @param name the new name
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void exec() {
      irsend("SEND_ONCE");   
   }


   @Override
   public void start() {
      irsend("SEND_START");
   }

   @Override
   public void stop() {
      irsend("SEND_STOP");
   }
   

   private void irsend(String sendType) {
      String cmd = "/usr/local/bin/irsend " + sendType + " " + getName() + " " + getCommand();
      try {
         Process pro = Runtime.getRuntime().exec(cmd);
         logger.info(cmd);
         pro.waitFor();
      } catch (InterruptedException e) {
         logger.error(cmd + " was interrupted.", e);
      } catch (IOException e) {
         logger.error(cmd + " failed.", e);
      }
   }
}
