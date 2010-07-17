/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2010, OpenRemote Inc.
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

import org.openremote.controller.component.EnumSensorType;
import org.openremote.controller.command.StatusCommand;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
class KNXReadCommand extends KNXCommand implements StatusCommand
{

  // Class Members --------------------------------------------------------------------------------

  private final static Logger log = Logger.getLogger(KNXCommandBuilder.KNX_LOG_CATEGORY);

  private final static Map<String, ApplicationProtocolDataUnit> booleanCommandLookup =
      new ConcurrentHashMap<String, ApplicationProtocolDataUnit>();

  /*
   * IMPLEMENTATION NOTE:
   *
   *   if new valid values for command names are added (in 'commandTranslations'), the
   *   unit tests should be added accordingly into KNXCommandBuilderTest
   */

  static
  {
    booleanCommandLookup.put("STATUS", ApplicationProtocolDataUnit.READ_SWITCH_STATE);
  }


  static KNXReadCommand createCommand(String name, KNXConnectionManager mgr, GroupAddress address)
  {
    name = name.trim().toUpperCase();

    ApplicationProtocolDataUnit apdu = booleanCommandLookup.get(name);

    return new KNXReadCommand(mgr, address, apdu);
  }



  // Constructors ---------------------------------------------------------------------------------

  private KNXReadCommand(KNXConnectionManager connectionManager, GroupAddress groupAddress,
                         ApplicationProtocolDataUnit apdu)
  {
    super(connectionManager, groupAddress, apdu);
  }


  // Implements StatusCommand ---------------------------------------------------------------------

  public String read(EnumSensorType sensorType, Map<String, String> statusMap)
  {
System.out.println("++++++++++    Polling Device Status..........");

    // TODO

    try
    {
      ApplicationProtocolDataUnit apdu = super.read(this);

      return apdu.toString(); // TODO
    }
    catch (ConnectionException e)
    {
      return "";    // TODO
    }

  }

}
