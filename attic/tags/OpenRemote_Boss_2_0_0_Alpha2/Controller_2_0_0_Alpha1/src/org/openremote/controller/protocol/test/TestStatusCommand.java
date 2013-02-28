package org.openremote.controller.protocol.test;

import org.openremote.controller.command.StatusCommand;

public class TestStatusCommand extends TestCommand implements StatusCommand {

   @Override
   public String read() {
      return null;
   }

}
