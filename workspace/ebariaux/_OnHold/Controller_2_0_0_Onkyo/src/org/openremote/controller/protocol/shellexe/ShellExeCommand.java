/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2011, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.protocol.shellexe;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.openremote.controller.command.ExecutableCommand;

public class ShellExeCommand implements ExecutableCommand {

   /** The logger. */
   private static Logger logger = Logger.getLogger(ShellExeCommandBuilder.SHELLEXE_PROTOCOL_LOG_CATEGORY);

   /** The full path to executable that should be started */
   private String commandPath;

   /** The params that should be attached to the executable */
   private String commandParams;

   /**
    * ShellExeCommand is a protocol to start shell scripts on the controller
    * @param commandPath
    * @param commandParams
    */
   public ShellExeCommand(String commandPath, String commandParams) {
      this.commandPath = commandPath;
      this.commandParams = commandParams;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public void send() {
      logger.debug("Will start shell command: " + commandPath + " and use params: " + commandParams);
      try
      {
        if (commandParams == null) {
          Runtime.getRuntime().exec(new String[]{commandPath});
        } else {
          Runtime.getRuntime().exec(new String[]{commandPath, commandParams});
        }
      } catch (IOException e)
      {
        logger.error("Could not execute shell command: "+commandPath, e);
      }
   }
}
