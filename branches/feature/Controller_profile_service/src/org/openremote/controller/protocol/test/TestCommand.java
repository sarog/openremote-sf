package org.openremote.controller.protocol.test;


public class TestCommand {
   
   private TestCommandType command;
   
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
   
}
