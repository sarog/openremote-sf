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
 * @author Tomsky
 *
 */
public class SVNException extends RuntimeException {


   /**
    * 
    */
   private static final long serialVersionUID = 123118923087651262L;

   public SVNException() {
      super();
   }

   public SVNException(Throwable cause) {
      super(cause);
   }

   public SVNException(String message, Throwable cause) {
      super(message, cause);
   }

   public SVNException(String message) {
      super(message);
   }

   
   
}
