package org.openremote.controller.protocol.test;

import org.jdom.Element;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;

public class TestCommandBuilder implements CommandBuilder {
   
   private final static String STATUS_COMMAND = "STATUS";

   @Override
   public Command build(Element element) {
      String commandStr = element.getTextTrim();

      TestCommandType testCommand = null;

      if (TestCommandType.SWITCH_ON.isEqual(commandStr))
         testCommand = TestCommandType.SWITCH_ON;
      else if (TestCommandType.SWITCH_OFF.isEqual(commandStr))
         testCommand = TestCommandType.SWITCH_OFF;
      else if (TestCommandType.STATUS.isEqual(commandStr)) {
         testCommand = TestCommandType.STATUS;
      } else if (Integer.parseInt(commandStr) >= -10 || Integer.parseInt(commandStr) <= 35) {
         testCommand = TestCommandType.NUMBER_COMAND;
      }else {
         throw new NoSuchCommandException("Couldn't find command " + commandStr + " in TestCommandType.");
      }

      Command command = null;
      if (commandStr != null && !"".equals(commandStr) && commandStr.equalsIgnoreCase(STATUS_COMMAND)) {
         command =  new TestStatusCommand();
      } else {
         command = new TestExecutableCommand(testCommand);
      }
      return command;
   }

}
