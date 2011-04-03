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
 * The exception class when ButtonCommand.
 * 
 * @author Dan 2009-6-2
 */
@SuppressWarnings("serial")
public class ButtonCommandException extends RuntimeException {
   
   /** The Constant EVENT_BUILDER_ERROR. */
   public final static int EVENT_BUILDER_ERROR = 418;
   
   /** The Constant NO_SUCH_BUTTON. */
   public final static int NO_SUCH_BUTTON = 419;
   
   /** The Constant NO_SUCH_EVENT_BUILDER. */
   public final static int NO_SUCH_EVENT_BUILDER = 420;
   
   /** The Constant INVALID_COMMAND_TYPE. */
   public final static int INVALID_COMMAND_TYPE = 421;
   
   /** The Constant CONTROLLER_XML_NOT_FOUND. */
   public final static int CONTROLLER_XML_NOT_FOUND = 422;
   
   /** The Constant NO_SUCH_EVENT. */
   public final static int NO_SUCH_EVENT = 423;
   
   /** The Constant INVALID_CONTROLLER_XML. */
   public final static int INVALID_CONTROLLER_XML = 424;
   
   /** The error code. */
   private int errorCode;

   /**
    * Gets the error code.
    * 
    * @return the error code
    */
   public int getErrorCode() {
      return errorCode;
   }

   /**
    * Sets the error code.
    * 
    * @param errorCode the new error code
    */
   public void setErrorCode(int errorCode) {
      this.errorCode = errorCode;
   }

   /**
    * Instantiates a new button command exception.
    */
   public ButtonCommandException() {
      super();
   }

   /**
    * Instantiates a new button command exception.
    * 
    * @param message the message
    * @param cause the cause
    */
   public ButtonCommandException(String message, Throwable cause) {
      super(message, cause);
   }

   /**
    * Instantiates a new button command exception.
    * 
    * @param message the message
    */
   public ButtonCommandException(String message) {
      super(message);
   }

   /**
    * Instantiates a new button command exception.
    * 
    * @param cause the cause
    */
   public ButtonCommandException(Throwable cause) {
      super(cause);
   }
   
   

}
