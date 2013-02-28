package org.openremote.controller.protocol.test;

import org.apache.log4j.Logger;
import org.openremote.controller.command.ExecutableCommand;

public class TestExecutableCommand extends TestCommand implements ExecutableCommand {

   private Logger logger = Logger.getLogger(this.getClass().getName());
   
   public TestExecutableCommand(TestCommandType testCommand) {
      super(testCommand);
   }

   @Override
   public void send() {
      logger.info("Send command to real device.");
   }

}
