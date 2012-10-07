/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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

import java.text.MessageFormat;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class ProtocolException extends Exception
{

  private String message = null;

  /**
   * Constructs a new exception with a given message.
   *
   * @param msg  human-readable error message
   */
  public ProtocolException(String msg)
  {
    super(msg);
  }

  /**
   * Constructs a new exception with a given message and root cause.
   *
   * @param msg     human-readable error message
   * @param cause   root exception cause
   */
  public ProtocolException(String msg, Throwable cause)
  {
    super(msg, cause);
  }

  public ProtocolException(String msg, Object... args)
  {
    try
    {
      this.message = MessageFormat.format(msg, args);
    }
    catch (Throwable t)
    {
      this.message = msg + "[Message could not be formatted: " + t.getMessage()+ "]";
    }
  }

  public ProtocolException(Throwable cause, String msg, Object... args)
  {
    this(msg, args);
    super.initCause(cause);
  }

  

  @Override public String getMessage()
  {
    if (message == null)
      return super.getMessage();

    else
      return message;
  }
}

