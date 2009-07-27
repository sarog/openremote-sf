/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.controller.exception;

/**
 * The exception class when controller.xml Not Found.
 * 
 * @author Dan 2009-5-22
 */
@SuppressWarnings("serial")
public class ControllerXMLNotFoundException extends ButtonCommandException {

   /**
    * Instantiates a new controller xml not found exception.
    */
   public ControllerXMLNotFoundException() {
      super("*controller.xml* not found.");
      setErrorCode(ButtonCommandException.CONTROLLER_XML_NOT_FOUND);
   }

   /**
    * Instantiates a new controller xml not found exception.
    * 
    * @param message the message
    * @param cause the cause
    */
   public ControllerXMLNotFoundException(String message, Throwable cause) {
      super("*controller.xml* not found." + message, cause);
      setErrorCode(ButtonCommandException.CONTROLLER_XML_NOT_FOUND);
   }

   /**
    * Instantiates a new controller xml not found exception.
    * 
    * @param message the message
    */
   public ControllerXMLNotFoundException(String message) {
      super("*controller.xml* not found." + message);
      setErrorCode(ButtonCommandException.CONTROLLER_XML_NOT_FOUND);
   }
   

}
