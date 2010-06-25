package org.openremote.controller.protocol.test;

import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;

public class TestStatusCommand extends TestCommand implements StatusCommand {

   @Override
   public String read(EnumSensorType sensorType) {
      return null;
   }

}
