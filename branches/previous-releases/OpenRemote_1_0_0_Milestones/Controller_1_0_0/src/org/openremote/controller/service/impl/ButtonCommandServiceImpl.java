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
package org.openremote.controller.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.openremote.controller.event.CommandType;
import org.openremote.controller.event.Event;
import org.openremote.controller.event.RemoteActionXMLParser;
import org.openremote.controller.service.ButtonCommandService;


/**
 * The implementation for ButtonCommandService class.
 * 
 * @author Dan 2009-4-3
 */
public class ButtonCommandServiceImpl implements ButtonCommandService {

   /** The remote action xml parser. */
   private RemoteActionXMLParser remoteActionXMLParser;
   
   /**
    * The Macro command execution delay.if you execute IR commands with irsend too fast, the receiving device won't be
    * able to process them. For TV for example, you can usually send IR commands no faster than every 0.5 seconds.
    *  So in the macro definition, there should be some delay between the command execution.
    * */
   private Long macroCmdExecutionDelay;
   
   /** The logger. */
   private static Logger logger = Logger.getLogger(ButtonCommandServiceImpl.class.getName());
   
   /**
    * {@inheritDoc}
    */
   public void trigger(String buttonID){
      trigger(buttonID, CommandType.SEND_ONCE);
   }
   
   /**
    * {@inheritDoc}
    */
   public void trigger(String buttonID, CommandType commandType) {
      List<Event> events = remoteActionXMLParser.findEventsByButtonID(buttonID);
      for (Event event : events) {
           
         switch (commandType) {
         case SEND_ONCE:
            event.exec();
            break;
         case SEND_START:
            event.start();
            break;
         case SEND_STOP:
            event.stop();
            break;
         default:
            event.exec();
            break;
         }
         
         try {
            //if this is a macro, then there should be the delay
            if (events.size() > 1) {
               Thread.sleep(macroCmdExecutionDelay + event.getDelay() * 1000 );
            }
         } catch (InterruptedException e) {
            logger.error("ButtonCommandService was interrupted.", e);
         }
      }
   }
   
   /**
    * Sets the remote action xml parser.
    * 
    * @param remoteActionXMLParser the new remote action xml parser
    */
   public void setRemoteActionXMLParser(RemoteActionXMLParser remoteActionXMLParser) {
      this.remoteActionXMLParser = remoteActionXMLParser;
   }

   /**
    * Sets the macro cmd execution delay.
    * 
    * @param macroCmdExecutionDelay the new macro cmd execution delay
    */
   public void setMacroCmdExecutionDelay(Long macroCmdExecutionDelay) {
      this.macroCmdExecutionDelay = macroCmdExecutionDelay;
   }

   
   
   
}
