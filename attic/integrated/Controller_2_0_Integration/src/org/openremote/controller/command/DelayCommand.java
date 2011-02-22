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

import org.apache.log4j.Logger;
import org.openremote.controller.service.impl.ControlCommandServiceImpl;

/**
 * The Class DelayEvent.
 * 
 * @author Handy.Wang 2009-10-15
 */
public class DelayCommand implements ExecutableCommand {
    
   /** The delay seconds. */
   private long delaySeconds;

   /** The logger. */
   private static Logger logger = Logger.getLogger(ControlCommandServiceImpl.class.getName());
   
   /**
    * Instantiates a new delay event.
    */
   public DelayCommand() {
      super();
   }

   /**
    * Instantiates a new delay event.
    * 
    * @param delaySeconds the delay seconds
    */
   public DelayCommand(String delaySecondsStr) {
      super();
      this.delaySeconds = (delaySecondsStr == null || "".equals(delaySecondsStr)) ? 0 : Long.parseLong(delaySecondsStr);
   }

   /**
    * Gets the delay seconds.
    * 
    * @return the delay seconds
    */
   public long getDelaySeconds() {
      return delaySeconds;
   }

   /**
    * Sets the delay seconds.
    * 
    * @param delaySeconds the new delay seconds
    */
   public void setDelaySeconds(long delaySeconds) {
      this.delaySeconds = delaySeconds;
   }

   /**
    * Execute delay.
    */
   @Override
   public void send() {
      try {
         Thread.sleep(delaySeconds * 1000);
      } catch (InterruptedException e) {
         logger.error("DelayEvent was interrupted.", e);
      }
   }
}
