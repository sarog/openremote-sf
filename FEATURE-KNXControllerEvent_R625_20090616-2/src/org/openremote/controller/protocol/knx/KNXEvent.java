/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.controller.protocol.knx;

import org.openremote.controller.event.Event;
import org.apache.log4j.Logger;

/**
 * TODO: The KNX Event.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Dan 2009-4-20
 */
public class KNXEvent extends Event
{

  // Class Members --------------------------------------------------------------------------------

  private final static Logger log = Logger.getLogger(KNXEventBuilder.KNX_LOG_CATEGORY);


  // Instance Fields ------------------------------------------------------------------------------

  private String groupAddress = null;
  private KNXConnectionManager connectionManager = null;
  private KNXCommand command = null;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * TODO : javadoc
   *
   */
  public KNXEvent(KNXConnectionManager connectionManager, String groupAddress, KNXCommand command)
  {
    this.connectionManager = connectionManager;

    // TODO : specify group address string form semantics
    
    this.groupAddress = groupAddress;
    this.command = command;
  }


  // Event Overrides ------------------------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override public void exec()
  {
    try
    {
      KNXConnection connection = connectionManager.getConnection();
    
      connection.send(groupAddress, command);
    }
    catch (ConnectionException e)
    {
      log.error(e);   // TODO
    }
  }


}
