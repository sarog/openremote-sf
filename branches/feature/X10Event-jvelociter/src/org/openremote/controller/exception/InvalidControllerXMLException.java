/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
 * The exception class when InvalidControllerXML.
 * 
 * @author Dan 2009-5-23
 */
@SuppressWarnings("serial")
public class InvalidControllerXMLException extends ButtonCommandException {

   /**
    * Instantiates a new invalid controller xml exception.
    */
   public InvalidControllerXMLException() {
      super();
      setErrorCode(ButtonCommandException.INVALID_CONTROLLER_XML);
   }

   /**
    * Instantiates a new invalid controller xml exception.
    * 
    * @param message the message
    * @param cause the cause
    */
   public InvalidControllerXMLException(String message, Throwable cause) {
      super(message, cause);
      setErrorCode(ButtonCommandException.INVALID_CONTROLLER_XML);
   }

   /**
    * Instantiates a new invalid controller xml exception.
    * 
    * @param message the message
    */
   public InvalidControllerXMLException(String message) {
      super(message);
      setErrorCode(ButtonCommandException.INVALID_CONTROLLER_XML);
   }
   

}
