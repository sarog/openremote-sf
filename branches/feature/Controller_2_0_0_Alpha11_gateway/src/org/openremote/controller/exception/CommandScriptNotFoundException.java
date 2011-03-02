/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.controller.exception;

/**
 * The exception class when gateway command script not found.
 * 
 * @author Rich Turner 2011-02-27
 */
@SuppressWarnings("serial")
public class CommandScriptNotFoundException extends ControlCommandException {

   public CommandScriptNotFoundException() {
      super("gateway command script not found.");
      setErrorCode(ControlCommandException.NO_SUCH_COMMAND_SCRIPT);
   }

   /**
    * 
    * @param message the message
    * @param cause the cause
    */
   public CommandScriptNotFoundException(String message, Throwable cause) {
      super("gateway command script not found." + message, cause);
      setErrorCode(ControlCommandException.NO_SUCH_COMMAND_SCRIPT);
   }

   /**
    * 
    * @param message the message
    */
   public CommandScriptNotFoundException(String message) {
      super("gateway command script not found." + message);
      setErrorCode(ControlCommandException.NO_SUCH_COMMAND_SCRIPT);
   }
}
