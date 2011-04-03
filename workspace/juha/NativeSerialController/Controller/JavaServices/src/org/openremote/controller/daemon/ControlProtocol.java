/*
* OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.daemon;


/**
 * This class defines the native I/O daemon control protocol used by I/O proxy.  <p>
 *
 * The protocol is rather static (no variables) so only simple constants are defined here.
 *
 * @see org.openremote.controller.daemon.IOProtocol
 * @see org.openremote.controller.daemon.IOProxy
 *
 * @author <a href = "mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class ControlProtocol
{

  // Daemon PING request/response protocol --------------------------------------------------------


  /**
   * The payload of ping request
   */
  private final static String PING_REQUEST_PAYLOAD    = "ARE YOU THERE";

  /**
   * The payload of ping response
   */
  private final static String PING_RESPONSE_PAYLOAD   = "I AM HERE";

  /**
   * The full I/O protocol request message for pinging the native I/O daemon.
   *
   * @see #PING_RESPONSE
   * @see org.openremote.controller.daemon.IOProtocol
   */
  protected final static String PING_REQUEST =
      IOModule.CONTROL.getModuleID() +
      IOProtocol.getMessageLength(PING_REQUEST_PAYLOAD.length()) +
      PING_REQUEST_PAYLOAD;

  /**
   * The full I/O protocol response message expected back from the native I/O daemon as
   * response to a ping request
   *
   * @see #PING_REQUEST
   * @see org.openremote.controller.daemon.IOProtocol
   */
  protected final static String PING_RESPONSE = PING_RESPONSE_PAYLOAD;


  // Daemon KILL request/response protocol --------------------------------------------------------


  /**
   * The payload of shutdown request
   */
  private final static String KILL_REQUEST_PAYLOAD    = "D1ED1ED1E";

  /**
   * The payload of shutdown response
   */
  private final static String KILL_RESPONSE_PAYLOAD   = "GOODBYE CRUEL WORLD";

  /**
   * The full I/O protocol request message for shutting down the native I/O daemon.
   *
   * @see #KILL_RESPONSE
   * @see org.openremote.controller.daemon.IOProtocol
   */
  protected final static String KILL_REQUEST =
      IOModule.CONTROL.getModuleID() +
      IOProtocol.getMessageLength(KILL_REQUEST_PAYLOAD.length()) +
      KILL_REQUEST_PAYLOAD;

  /**
   * The full I/O protocol response message expected back from the native I/O daemon as
   * the response to a kill (shutdown) request.
   *
   * @see #KILL_REQUEST
   * @see org.openremote.controller.daemon.IOProtocol
   */
  protected final static String KILL_RESPONSE = KILL_RESPONSE_PAYLOAD;

}

