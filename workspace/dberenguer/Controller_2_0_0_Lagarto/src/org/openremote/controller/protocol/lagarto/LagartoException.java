/**
 * Copyright (c) 2012 Daniel Berenguer <dberenguer@usapiens.com>
 *
 * This file is part of the lagarto project.
 *
 * lagarto  is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * lagarto is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with panLoader; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 *
 *  @author Daniel Berenguer
 *  @date   2012-09-14
 */

package org.openremote.controller.protocol.lagarto;

import org.openremote.controller.Constants;
import org.openremote.controller.utils.Logger;

/**
 * Main exception class
 */
public class LagartoException extends Exception
{
  /**
   * Description of the exception
   */
   private String description;

  /**
   * The logger
   */
   public final static String LAGARTOLISTENER_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "LAGARTO_LISTENER";
   private final static Logger logger = Logger.getLogger(LAGARTOLISTENER_PROTOCOL_LOG_CATEGORY);

  /**
   * Class constructor
   */
   public LagartoException(String message)
   {
       super(message);
       description = message;
   }

   /**
    * Log exception
    */
   public void log() 
   {
     System.out.println("LagartoException: " + description);
     logger.error("LagartoException: " + description);
   }

   /**
    * out
    * 
    * Display exception on the stdout output
    */
   public void out() 
   {
      System.out.println("Exception: " + description);
      if (this.getCause() != null)
        System.out.println("Origin: " + this.getCause().toString());
   }
}
