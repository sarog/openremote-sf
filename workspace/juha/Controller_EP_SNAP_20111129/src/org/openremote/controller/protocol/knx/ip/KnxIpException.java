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
package org.openremote.controller.protocol.knx.ip;

/**
 * TODO
 *
 * @author Olivier Gandit
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class KnxIpException extends Exception
{

  // Serialization ID -----------------------------------------------------------------------------

  private static final long serialVersionUID = 1L;


  // Enums ----------------------------------------------------------------------------------------

  public static enum Code
  {
    ALREADY_CONNECTED,
    NOT_CONNECTED,
    UNKNOWN_HOST,
    NO_RESPONSE_FROM_INTERFACE,
    RESPONSE_ERROR,
    WRONG_SEQUENCE_COUNTER_VALUE,
    WRONG_CHANNEL_ID,
    WRONG_RESPONSE_TYPE,
    INVALID_HEADER,
    UNEXPECTED_SERVICE_TYPE
  }


  // Instance Fields ------------------------------------------------------------------------------

  private Code code;


  // Constructors ---------------------------------------------------------------------------------

  public KnxIpException(Code code, String msg)
  {
    super(msg);
    this.code = code;
  }


  // Instance Methods -----------------------------------------------------------------------------

  public Code getCode()
  {
    return code;
  }


}
