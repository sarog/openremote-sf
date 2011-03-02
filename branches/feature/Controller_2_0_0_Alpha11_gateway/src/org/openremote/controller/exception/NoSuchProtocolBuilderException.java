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
 * The Class NoSuchProtocolBuilderException.
 * 
 * @author Rich Turner 2011-02-11
 */
@SuppressWarnings("serial")
public class NoSuchProtocolBuilderException extends ControlCommandException {

   /**
    * Instantiates a new no such protocol builder exception.
    */
   public NoSuchProtocolBuilderException() {
      super("Please check the property 'protocolBuilders' " +
      "configuration of bean 'protocolFactory' in applicationContext.xml");
      setErrorCode(ControlCommandException.NO_SUCH_PROTOCOL_BUILDER);
   }

   /**
    * Instantiates a new no such command builder exception.
    * 
    * @param message the message
    * @param cause the cause
    */
   public NoSuchProtocolBuilderException(String message, Throwable cause) {
      super(message + ", please check the property 'protocolBuilders' " +
      "configuration of bean 'protocolFactory' in applicationContext.xml",cause);
      setErrorCode(ControlCommandException.NO_SUCH_PROTOCOL_BUILDER);
   }

   /**
    * Instantiates a new no such command builder exception.
    * 
    * @param message the message
    */
   public NoSuchProtocolBuilderException(String message) {
      super(message + ", please check the property 'protocolBuilders' " +
      		"configuration of bean 'protocolFactory' in applicationContext.xml");
      setErrorCode(ControlCommandException.NO_SUCH_PROTOCOL_BUILDER);
   }

   /**
    * Instantiates a new no such command builder exception.
    * 
    * @param cause the cause
    */
   public NoSuchProtocolBuilderException(Throwable cause) {
      super(cause);
      setErrorCode(ControlCommandException.NO_SUCH_PROTOCOL_BUILDER);
   }
   

}
