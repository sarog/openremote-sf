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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;

/**
 * TODO
 */
class KNXWriteCommand extends KNXCommand implements ExecutableCommand
{
    

  // Class Members --------------------------------------------------------------------------------

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
    booleanCommandLookup.put("ON", ApplicationProtocolDataUnit.WRITE_SWITCH_ON);
    booleanCommandLookup.put("OFF", ApplicationProtocolDataUnit.WRITE_SWITCH_OFF);
  }


  static KNXWriteCommand createCommand(String name, KNXConnectionManager mgr, GroupAddress address)
  {
    name = name.trim().toUpperCase();

    ApplicationProtocolDataUnit apdu = booleanCommandLookup.get(name);

    return new KNXWriteCommand(mgr, address, apdu);
  }



  // Constructors ---------------------------------------------------------------------------------
  
  private KNXWriteCommand(KNXConnectionManager connectionManager, GroupAddress groupAddress,
                          ApplicationProtocolDataUnit apdu)
  {
    super(connectionManager, groupAddress, apdu);
  }


  // Implements ExecutableCommand -----------------------------------------------------------------

  public void send()
  {
    super.send(this);
  }
    

}
