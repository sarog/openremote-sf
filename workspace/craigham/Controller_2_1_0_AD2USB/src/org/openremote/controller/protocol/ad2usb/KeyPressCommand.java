/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller.protocol.ad2usb;

import org.openremote.controller.Constants;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.utils.Logger;

public class KeyPressCommand implements ExecutableCommand{

   private final static Logger log = Logger.getLogger(Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "ad2usb");
   private String mCommand;
   private Ad2UsbGateway mGateway;
   private String mParameter;
   public KeyPressCommand(Ad2UsbGateway gateway, String command,String parameter)
   {
      mGateway = gateway;
      mCommand = command;
      mParameter = parameter;
   }
   @Override
   public void send() {
      mGateway.sendCommand(this);
   }

   public String toString()
   {
      return "Command: " + mCommand;
   }
   /**
    * Returns the string representation of the output required to send to the Ad2Usb
    * @return
    */
   public String getCommandOutput()
   {
      return mParameter;
   }
}
