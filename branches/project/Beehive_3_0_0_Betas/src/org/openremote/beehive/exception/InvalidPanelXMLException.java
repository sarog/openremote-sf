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
package org.openremote.beehive.exception;

public class InvalidPanelXMLException extends RuntimeException {

   private static final long serialVersionUID = 728441142203047285L;

   public InvalidPanelXMLException() {
      super();
   }

   public InvalidPanelXMLException(String message, Throwable cause) {
      super(message, cause);
   }

   public InvalidPanelXMLException(String message) {
      super(message);
   }

   public InvalidPanelXMLException(Throwable cause) {
      super(cause);
   }

}
