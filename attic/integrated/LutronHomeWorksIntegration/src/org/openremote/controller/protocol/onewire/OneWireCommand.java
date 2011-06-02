/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller.protocol.onewire;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;
import org.openremote.controller.utils.Logger;
import org.owfs.jowfsclient.Enums.*;
import org.owfs.jowfsclient.OwfsClient;
import org.owfs.jowfsclient.OwfsClientFactory;
import org.owfs.jowfsclient.OwfsException;

import java.io.IOException;
import java.util.Map;

/**
 * 1-wire protocol implementation for OpenRemote. Program accesses 1-wire network (owserver) via
 * library jowfsclient.
 *
 * Parameters used:
 *
 * hostname        hostname of owserver
 * port            port where owserver is running, by default it is 4304
 * deviceAddress   address of the 1-wire device, such as /1F.E9E803000000/main/28.25E9E3010000
 * filename        or sensor attribute - is filename in owfs that holds values, such as
 *                 "temperature", "temperature9", "humidity" or similar
 * refreshTime     time interval in seconds between two calls to owserver; any request between
 *                 these two calls will use cached value. In fact, it is cache timeout value.
 *
 * @author <a href="mailto:jmisura@gmail.com">Jaroslav Misura</a>
 */
public class OneWireCommand implements ExecutableCommand, StatusCommand
{

  // Class Members --------------------------------------------------------------------------------

  private final static Logger logger = Logger.getLogger(OneWireCommandBuilder.ONEWIRE_PROTOCOL_LOG_CATEGORY);


  // Instance Fields ------------------------------------------------------------------------------

  private String hostname;
  private int port = 4304;
  private String deviceAddress;
  private String filename;
  private long refreshTime = 0;

  private String lastValue = "?";
  private long lastUpdateTimestamp = 0;



  // Constructors ---------------------------------------------------------------------------------

  public OneWireCommand(String hostname, int port, String deviceAddress, String filename, long refreshTime)
  {
    this.hostname = hostname;
    this.port = port;
    this.deviceAddress = deviceAddress;
    this.filename = filename;
    this.refreshTime = refreshTime;

    logger.debug("OneWireCommand created with values hostname=" + hostname +
               "; port=" + port + "; deviceAddress=" + deviceAddress + "; filename=" +filename +
               "; refreshTime=" + refreshTime);
  }


  // Implements StatusCommand ---------------------------------------------------------------------

  @Override public String read(EnumSensorType sensorType, Map<String, String> stateMap)
  {
    logger.debug("Reading sensor " + deviceAddress + "/" + filename);

    long timestamp = System.currentTimeMillis();

    // check if we should read now or use cached value
    if (timestamp > lastUpdateTimestamp + refreshTime)
    {
      logger.debug("1-Wire sensor " + deviceAddress + "/" + filename + " value is going to be updated.");
      OwfsClient client = OwfsClientFactory.newOwfsClient(hostname, port, true);

      client.setDeviceDisplayFormat(OwDeviceDisplayFormat.OWNET_DDF_F_DOT_I);
      client.setBusReturn(OwBusReturn.OWNET_BUSRETURN_ON);
      client.setPersistence(OwPersistence.OWNET_PERSISTENCE_ON);
      client.setTemperatureScale(OwTemperatureScale.OWNET_TS_CELSIUS);
      String value = null;

      logger.debug("Client created, before call");

      try
      {
        value = client.read(deviceAddress+"/"+filename);
      }

      catch (OwfsException e)
      {
        logger.error("OneWire error, unable to read from OWSERVER.", e);
      }

      catch (IOException e)
      {
        logger.error("OneWire IO error, unable to read from OWSERVER.", e);
      }

      logger.debug("After client call, value = '"+value+"'");

      if (null == value)
      {
        // we were not able to read value, so let's use old one + "!" to show that value is not
        //  up to date, do not update lastUpdateTimestamp, we will retry to read value in next turn
        value = lastValue + "!";
      }

      else
      {
        value = value.trim();
        lastValue = value;
        lastUpdateTimestamp = System.currentTimeMillis();
      }

      logger.debug("Sensor " + deviceAddress + "/" + filename + " returns value = '" + value + "'");

      return value;
    }

    else
    {
      logger.debug(
          "1-Wire sensor " + deviceAddress + "/" + filename +
          " value is still within cache timeout limit -> No update."
      );

      return lastValue;
    }
  }


  // Implements ExecutableCommand -----------------------------------------------------------------

  public void send()
  {

    // not implemented yet

  }

}
