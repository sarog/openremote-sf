/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.modeler.exception;

/**
 * Exception class indicating a data access error in designer. Often this means
 * an unrecoverable error related to data integrity or constraints and there are
 * no meaningful non-heuristic resolutions. However, this is introduced as a checked
 * exception to ensure it is handled properly and that the user is presented with
 * some meaningful message for action rather than an incomprehensible or odd error
 * message. <p>
 *
 * Normally when such persistence exceptions occur, the admins should be notified to
 * resolve the issue.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class PersistenceException extends OpenRemoteException
{

  /**
   * Constructs a new exception with a given message.
   *
   * @param msg  human-readable error message
   */
  public PersistenceException(String msg)
  {
    super(msg);
  }

  /**
   * Constructs a new exception with a parameterized message.
   *
   * @param msg     human-readable error message
   * @param params  exception message parameters -- message parameterization must be
   *                compatible with {@link java.text.MessageFormat} API
   *
   * @see java.text.MessageFormat
   */
  public PersistenceException(String msg, Object... params)
  {
    super(format(msg, params));
  }

  /**
   * Constructs a new exception with a given message and root cause.
   *
   * @param msg     human-readable error message
   * @param cause   root exception cause
   */
  public PersistenceException(String msg, Throwable cause)
  {
    super(msg, cause);
  }

  /**
   * Constructs a new exception with a parameterized message and root cause.
   *
   * @param msg     human-readable error message
   * @param cause   root exception cause
   * @param params  exception message parameters -- message parameterization must be
   *                compatible with {@link java.text.MessageFormat} API
   *
   * @see java.text.MessageFormat
   */
  public PersistenceException(String msg, Throwable cause, Object... params)
  {
    super(format(msg, params), cause);
  }

}

