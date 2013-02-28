package org.openremote.controller.protocol.test;


public class TestCommand {
   
   private TestCommandType command;
   
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
   
}
