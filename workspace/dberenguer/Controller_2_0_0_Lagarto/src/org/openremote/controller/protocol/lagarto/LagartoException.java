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
 * along with lagarto; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 *
 *  @author Daniel Berenguer
 *  @date   2012-09-14
 */

package org.openremote.controller.protocol.lagarto;

import org.openremote.controller.utils.Logger;

/**
 * Main exception class
 */
public class LagartoException extends Exception
{
  /**
   * Logger
   */
  private final static Logger logger = Logger.getLogger(LagartoCommandBuilder.LAGARTO_PROTOCOL_LOG_CATEGORY);

  /**
  * Description of the exception
  */
  private String description;

  /**
  * Class constructor
  */
  public LagartoException(String message)
  {
    super(message);
    description = message;
  }

  /**
  * Log exception as an error
  */
  public void logError()
  {
    logger.error("LagartoException: " + description);
  }

  /**
  * Log exception as an info
  */
  public void logInfo()
  {
    logger.info("LagartoException: " + description);
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
