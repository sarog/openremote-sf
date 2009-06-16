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
package org.openremote.beehive.exception;

/**
 * The Class LIRCrawlerException.
 * 
 * @author Tomsky
 */
public class LIRCrawlerException extends RuntimeException {

   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = 8645844364538885913L;
   
   /** The Constant CRAWLER_WRITEFILE_ERROR. */
   public static final int CRAWLER_WRITEFILE_ERROR = 445;
   
   /** The Constant CRAWLER_NETWORK_ERROR. */
   public static final int CRAWLER_NETWORK_ERROR = 446;
   
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
    * Instantiates a new lIR crawler exception.
    */
   public LIRCrawlerException() {
      super();
   }

   /**
    * The Constructor.
    * 
    * @param message the message
    */
   public LIRCrawlerException(String message) {
      super(message);
   }

   /**
    * The Constructor.
    * 
    * @param cause the cause
    */
   public LIRCrawlerException(Throwable cause) {
      super(cause);
   }

   /**
    * The Constructor.
    * 
    * @param message the message
    * @param cause the cause
    */
   public LIRCrawlerException(String message, Throwable cause) {
      super(message, cause);
   }

}
