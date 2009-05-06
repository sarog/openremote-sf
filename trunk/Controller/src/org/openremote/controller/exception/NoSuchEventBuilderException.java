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
 * The Class NoSuchEventBuilderException.
 * 
 * @author Dan 2009-4-30
 */
@SuppressWarnings("serial")
public class NoSuchEventBuilderException extends RuntimeException {

   /**
    * Instantiates a new no such event builder exception.
    */
   public NoSuchEventBuilderException() {
      super("Please check the property 'eventBuilders' " +
      "configuration of bean 'eventFactory' in applicationContext.xml");
   }

   /**
    * Instantiates a new no such event builder exception.
    * 
    * @param message the message
    * @param cause the cause
    */
   public NoSuchEventBuilderException(String message, Throwable cause) {
      super(message + ", please check the property 'eventBuilders' " +
      "configuration of bean 'eventFactory' in applicationContext.xml",cause);
   }

   /**
    * Instantiates a new no such event builder exception.
    * 
    * @param message the message
    */
   public NoSuchEventBuilderException(String message) {
      super(message + ", please check the property 'eventBuilders' " +
      		"configuration of bean 'eventFactory' in applicationContext.xml");
   }

   /**
    * Instantiates a new no such event builder exception.
    * 
    * @param cause the cause
    */
   public NoSuchEventBuilderException(Throwable cause) {
      super(cause);
   }
   

}
