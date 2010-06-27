/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.controller.protocol.knx;

import org.openremote.controller.command.ExecutableCommand;

/**
 * TODO: The KNX Event.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Dan 2009-4-20
 */
public class KNXExecutableCommand extends KNXCommand implements ExecutableCommand {

  public KNXExecutableCommand() {}
  
  /**
   * TODO : javadoc
   *
   */
  public KNXExecutableCommand(KNXConnectionManager connectionManager, String groupAddress, KNXCommandType command) {
      super(connectionManager, groupAddress, command);
  }

  /**
   * {@inheritDoc}
   */
  @Override public void send() {
    try
    {
      KNXConnection connection = getConnectionManager().getConnection();    
      connection.send(getGroupAddress(), getKnxCommandType());
    }
    catch (ConnectionException e)
    {
      log.error(e);   // TODO
    }
  }


}
