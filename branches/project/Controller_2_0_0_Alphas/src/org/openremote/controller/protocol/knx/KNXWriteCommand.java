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

import org.openremote.controller.command.ExecutableCommand;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Write command representing KNX Group Value Write service. This class implements the
 * {@link ExecutableCommand} interface and therefore acts as an entry point in
 * controller/protocol SPI.
 */
class KNXWriteCommand extends KNXCommand implements ExecutableCommand
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * Lookup map from user defined command strings in the designer (from which they end up
   * into controller.xml) to type safe APDUs for KNX CEMI frames.
   */
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


  /**
   * Factory method for creating new KNX write command instances based on user configured
   * command name. The command name must be one of the pre-specified command names in this class.
   *
   * @param name      User-configured command name used in tools and configuration files. This
   *                  name is mapped to a typed KNX Application Protocol Data Unit instance.
   * @param mgr       Connection manager reference this command will use for transmission.
   * @param address   Destination group address for this command.
   * 
   * @return  a new KNX write command instance, or <code>null</code> if the lookup name could not
   *          be matched to any command
   */
  static KNXWriteCommand createCommand(String name, KNXConnectionManager mgr, GroupAddress address)
  {
    name = name.trim().toUpperCase();

    ApplicationProtocolDataUnit apdu = booleanCommandLookup.get(name);

    if (apdu == null)
      return null;
    
    return new KNXWriteCommand(mgr, address, apdu);
  }



  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new KNXWriteCommand instance with a given connection manager, group address and
   * application protocol data unit.
   *
   * @param connectionManager   connection manager used to send this KNX command
   * @param groupAddress        destination group address for this command
   * @param apdu                APDU payload for this command
   */
  private KNXWriteCommand(KNXConnectionManager connectionManager, GroupAddress groupAddress,
                          ApplicationProtocolDataUnit apdu)
  {
    super(connectionManager, groupAddress, apdu);
  }


  // Implements ExecutableCommand -----------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  public void send()
  {
    // delegate to super class...

    super.write(this);
  }
    

}
