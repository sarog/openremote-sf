package org.openremote.controller.protocol.isy994.model;

import org.openremote.controller.command.Command;
import org.openremote.controller.protocol.isy994.InsteonDeviceAddress;
import org.openremote.controller.protocol.isy994.IsyConnectionClient;

public class ISYCommandBuilder {
   public static Command createCommand(IsyConnectionClient client, InsteonDeviceAddress insteonAddress, String command,
         String paramValue) {
      if ("get-level".equals(command)|| "get-power".equals(command)) return new InsteonDeviceGetLevel(insteonAddress, client);
      else
         return new InsteonSetNodeCommand(client, insteonAddress, command, paramValue);
   }

}
