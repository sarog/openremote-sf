package org.openremote.controller.protocol.isy994.model;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.protocol.isy994.InsteonDeviceAddress;
import org.openremote.controller.protocol.isy994.IsyConnectionClient;

public class InsteonSetNodeCommand implements ExecutableCommand {

   private String mCommand;
   private InsteonDeviceAddress mAddress;
   private IsyConnectionClient mClient;
   private String mExtraParam = null;

   public InsteonSetNodeCommand(IsyConnectionClient client, InsteonDeviceAddress address, String command,
         String extraParam) {
      mClient = client;
      mAddress = address;
      mCommand = command;
      if (extraParam != null && false) {

         mExtraParam = Integer.toString(Integer.parseInt(extraParam) * 255 / 100);
      } else {
         mExtraParam = extraParam;
      }
      // special case for insteon...cannot set a switch off with DON - 0...we see this with a sensor back to zero
      if ("DON".equals(command) && "0".equals(extraParam))
            {
            System.out.println("Overrode command to OFF because was trying to set to Zero");
            mCommand = "DOF";
            mExtraParam = null;
            }

   }

   @Override
   public void send() {
      mClient.changeNodeState(mCommand, mExtraParam, mAddress.toString());
   }
}
