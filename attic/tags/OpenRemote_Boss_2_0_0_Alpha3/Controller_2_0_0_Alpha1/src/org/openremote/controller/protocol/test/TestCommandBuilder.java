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
package org.openremote.controller.protocol.test;

import org.jdom.Element;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;

public class TestCommandBuilder implements CommandBuilder {
   
   private final static String STATUS_COMMAND = "STATUS";

   @Override
   public Command build(Element element) {
//      String commandStr = element.getTextTrim();
      String commandStr = element.getAttributeValue("value");

      TestCommandType testCommand = null;

      if (TestCommandType.SWITCH_ON.isEqual(commandStr))
         testCommand = TestCommandType.SWITCH_ON;
      else if (TestCommandType.SWITCH_OFF.isEqual(commandStr))
         testCommand = TestCommandType.SWITCH_OFF;
      else if (TestCommandType.STATUS.isEqual(commandStr)) {
         testCommand = TestCommandType.STATUS;
      } else if (Integer.parseInt(commandStr) >= 0 && Integer.parseInt(commandStr) <= 100) {
         testCommand = TestCommandType.NUMBER_COMAND;
      }else {
         throw new NoSuchCommandException("Couldn't find command " + commandStr + " in TestCommandType.");
      }

      Command command = null;
      if (commandStr != null && !"".equals(commandStr.trim()) && commandStr.equalsIgnoreCase(STATUS_COMMAND)) {
         command =  new TestStatusCommand();
      } else {
         command = new TestExecutableCommand(testCommand);
      }
      return command;
   }

}
