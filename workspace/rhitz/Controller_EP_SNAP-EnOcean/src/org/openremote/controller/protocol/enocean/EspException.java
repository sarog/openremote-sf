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
package org.openremote.controller.protocol.enocean;

/**
 * Indicates a connectivity error related to an EnOcean Serial Protocol error.
 *
 * @author Rainer Hitz
 */
public class EspException extends ConnectionException
{

  // Enums ----------------------------------------------------------------------------------------

  /**
   * TODO
   */
  public enum ErrorCode
  {
    /**
     * TODO
     */
    RESP_ERROR,

    /**
     * TODO
     */
    RESP_NOT_SUPPORTED,

    /**
     * TODO
     */
    RESP_WRONG_PARAM,

    /**
     * TODO
     */
    RESP_OPERATION_DENIED,

    /**
     * TODO
     */
    RESP_INVALID_RESPONSE,

    /**
     * TODO
     */
    RESP_UNKNOWN_RETURN_CODE,

    /**
     * TODO
     */
    RESP_UNKNOWN_PACKET_TYPE,

    /**
     * TODO
     */
    RESP_INVALID_DEVICE_ID,

    /**
     * TODO
     */
    RESP_TIMEOUT
  }

  // Private Instance Fields ----------------------------------------------------------------------

  /**
   *
   */
  private ErrorCode errorCode;

  /**
   * Constructs a new exception with a given message.
   *
   * @param code error code
   *
   * @param msg  human-readable error message
   */
  public EspException(ErrorCode code, String msg)
  {
    super(msg);

    this.errorCode = code;
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
  public EspException(ErrorCode code, String msg, Object... params)
  {
    super(format(msg, params));

    this.errorCode = code;
  }

  /**
   * Constructs a new exception with a given message and root cause.
   *
   * @param msg     human-readable error message
   * @param cause   root exception cause
   */
  public EspException(ErrorCode code, String msg, Throwable cause)
  {
    super(msg, cause);

    this.errorCode = code;
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
  public EspException(ErrorCode code, String msg, Throwable cause, Object... params)
  {
    super(format(msg, params), cause);

    this.errorCode = code;
  }

  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * Return error code
   *
   * @return error code
   */
  public ErrorCode getErrorCode()
  {
    return errorCode;
  }
}
