package org.openremote.controller.protocol.test;

import java.util.Map;

import org.apache.log4j.Logger;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;


public class TestCommand implements ExecutableCommand, StatusCommand {
   
   private TestCommandType command;
   
   private Logger logger = Logger.getLogger(this.getClass().getName());
   
   private String commandValue;


   public TestCommand() {
      super();
   }

   public TestCommand(TestCommandType command) {
      super();
      this.command = command;
   }

   public TestCommandType getCommand() {
      return command;
   }

   public void setCommand(TestCommandType command) {
      this.command = command;
   }

   public String getCommandValue() {
      return commandValue;
   }

   public void setCommandValue(String commandValue) {
      this.commandValue = commandValue;
   }
   
   @Override
   public void send() {
      logger.info("Send command to real device.");
   }
   
   @Override
   public String read(EnumSensorType sensorType, Map<String, String> statusMap) {
      return null;
   }
   
}
