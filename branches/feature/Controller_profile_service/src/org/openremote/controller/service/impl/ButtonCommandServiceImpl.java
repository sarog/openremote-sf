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

import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandType;
import org.openremote.controller.command.DelayCommand;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.RemoteActionXMLParser;
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
    * {@inheritDoc}
    */
   public void trigger(String buttonID){
      trigger(buttonID, CommandType.SEND_ONCE);
   }
   
   /**
    * {@inheritDoc}
    */
   public void trigger(String buttonID, CommandType commandType) {
//      List<Command> commands = remoteActionXMLParser.findCommandsByControlID(buttonID);
//      for (Command command : commands) {
//           
//         switch (commandType) {
//         case SEND_ONCE:
//            doTrigger(command);
//            break;
//         case SEND_START:
////            event.start();
//            break;
//         case SEND_STOP:
////            event.stop();
//            break;
//         default:
//             doTrigger(command);
//            break;
//         }
//      }
   }
   
   public void doTrigger(Command command) {
       if (command instanceof ExecutableCommand) {
           ((ExecutableCommand)command).send();
       } else if (command instanceof DelayCommand) {
           ((DelayCommand)command).send();
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
   
}
