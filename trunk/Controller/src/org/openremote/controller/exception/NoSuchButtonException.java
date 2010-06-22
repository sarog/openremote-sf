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
 * The exception for NoSuchButton.
 * 
 * @author Dan 2009-4-30
 */
@SuppressWarnings("serial")
public class NoSuchButtonException extends ButtonCommandException {

   /**
    * Instantiates a new no such button exception.
    */
   public NoSuchButtonException() {
      super();
      setErrorCode(ButtonCommandException.NO_SUCH_BUTTON);
   }

   /**
    * Instantiates a new no such button exception.
    * 
    * @param message the message
    */
   public NoSuchButtonException(String message) {
      super(message);
      setErrorCode(ButtonCommandException.NO_SUCH_BUTTON);
   }

   /**
    * Instantiates a new no such button exception.
    * 
    * @param cause the cause
    */
   public NoSuchButtonException(Throwable cause) {
      super(cause);
      setErrorCode(ButtonCommandException.NO_SUCH_BUTTON);
   }

   /**
    * Instantiates a new no such button exception.
    * 
    * @param message the message
    * @param cause the cause
    */
   public NoSuchButtonException(String message, Throwable cause) {
      super(message, cause);
      setErrorCode(ButtonCommandException.NO_SUCH_BUTTON);
   }

}
