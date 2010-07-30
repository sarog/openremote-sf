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

import org.apache.log4j.Logger;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;
import org.openremote.controller.protocol.knx.datatype.DataPointType;
import org.openremote.controller.protocol.knx.datatype.Bool;
import org.openremote.controller.exception.ConversionException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Read command representing KNX Group Value Read service. This class implements the
 * {@link StatusCommand} interface and therefore acts as an entry point in controller/protocol SPI.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
class GroupValueRead extends KNXCommand implements StatusCommand
{


  // Class Members --------------------------------------------------------------------------------

  /**
   * KNX logger. Uses a common category for all KNX related logging.
   */
  private final static Logger log = Logger.getLogger(KNXCommandBuilder.KNX_LOG_CATEGORY);

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
    booleanCommandLookup.put("STATUS", ApplicationProtocolDataUnit.READ_SWITCH_STATE);
  }


  /**
   * Factory method for creating new KNX read command instances based on user configured
   * command name. The command name must be one of the pre-specified command names in this class.
   *
   * @param name      User-configured command name used in tools and configuration files. This
   *                  name is mapped to a typed KNX Application Protocol Data Unit instance.
   * @param mgr       Connection manager reference this command will use for transmission.
   * @param address   Destination group address for this command.
   *
   * @return          a new KNX read command instance, or <code>null</code> if the lookup name
   *                  could not be matched to any command
   */
  static GroupValueRead createCommand(String name, KNXConnectionManager mgr, GroupAddress address)
  {
    name = name.trim().toUpperCase();

    ApplicationProtocolDataUnit apdu = booleanCommandLookup.get(name);

    if (apdu == null)
      return null;
    
    return new GroupValueRead(mgr, address, apdu);
  }



  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new KNXReadCommand instance with a given connection manager, group address
   * and protocol data unit.
   *
   * @param connectionManager   connection manager used to send this KNX command
   * @param groupAddress        destination group address for this command
   * @param apdu                APDU payload for this command
   */
  private GroupValueRead(KNXConnectionManager connectionManager, GroupAddress groupAddress,
                         ApplicationProtocolDataUnit apdu)
  {
    super(connectionManager, groupAddress, apdu);
  }


  // Implements StatusCommand ---------------------------------------------------------------------

  /**
   * TODO
   *
   * @param sensorType
   *
   * @param statusMap
   * @return
   */
  public String read(EnumSensorType sensorType, Map<String, String> statusMap)
  {

    log.debug("Polling device status for " + this);

    ApplicationProtocolDataUnit responseAPDU = super.read(this);

    if (responseAPDU == null)
    {
        return "";      // TODO : check how caller handles invalid return values
    }

    DataPointType dpt = getAPDU().getDataPointType();

    if (dpt == DataPointType.BooleanDataPointType.SWITCH)
    {
      try
      {
        Bool bool = responseAPDU.convertToBooleanDataType();

        Bool datatype = (Bool)responseAPDU.getDataType();

        if (datatype == Bool.ON)
        {
          return "on";
        }
        else
        {
          return "off";
        }
      }
      catch (ConversionException e)
      {
        log.error(
            "Cannot convert the frame payload (" + responseAPDU.dataAsString() +
            ") to SWITCH datatype.", e
        );

        return "";    // TODO : check how caller handles invalid return types
      }
    }

    log.warn("Unrecognized datatype. This implementation cannot handle " + dpt);

    return null;    // TODO : check how caller handles invalid return types
  }

}
