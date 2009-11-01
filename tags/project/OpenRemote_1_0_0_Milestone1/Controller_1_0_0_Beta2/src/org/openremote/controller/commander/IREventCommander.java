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
package org.openremote.controller.commander;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.openremote.irbuilder.domain.IREvent;


/**
 * The Class IREventCommander.
 * 
 * @author Dan 2009-4-3
 */
public class IREventCommander extends EventCommander {

   /** The logger. */
   private static Logger logger = Logger.getLogger(IREventCommander.class.getName());
   
   /**
    * {@inheritDoc}
    */
   @Override
   public void execute() {
      IREvent irEvent = (IREvent)getEvent();
      String cmd = "irsend send_once " + irEvent.getName() + " " + irEvent.getCommand();
      try {
         Process pro = Runtime.getRuntime().exec(cmd);
         logger.info(cmd);
         pro.waitFor();
      } catch (InterruptedException e) {
         logger.error(cmd+" was interrupted.",e);
      } catch (IOException e) {
         logger.error(cmd+" failed.",e);
      }
   }
   
}
