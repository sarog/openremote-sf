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

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.utils.Strings;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;

import java.util.List;


/**
 * TODO
 *
 * @author <a href="mailto:jmisura@gmail.com">Jaroslav Misura</a>
 */
public class OneWireCommandBuilder implements CommandBuilder
{

  // Constants ------------------------------------------------------------------------------------

  public final static String ONEWIRE_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "onewire";


  private final static int INT_DEFAULT_OWSERVER_PORT = 4304;
  private final static String STR_ATTRIBUTE_NAME_HOSTNAME = "hostname";
  private final static String STR_ATTRIBUTE_NAME_PORT = "port";
  private final static String STR_ATTRIBUTE_NAME_DEVICE_ADDRESS = "deviceAddress";
  private final static String STR_ATTRIBUTE_NAME_FILENAME = "filename";
  private final static String STR_ATTRIBUTE_NAME_POLLING_INTERVAL = "pollingInterval";


  // Class Members --------------------------------------------------------------------------------

  private final static Logger logger = Logger.getLogger(ONEWIRE_PROTOCOL_LOG_CATEGORY);



  // Implements CommandBuilder --------------------------------------------------------------------

  @SuppressWarnings("unchecked")
  @Override
  public Command build(Element element)
  {
      logger.debug("Building 1-Wire command");
      List<Element> propertyEles = element.getChildren("property", element.getNamespace());

      String hostname = null;
      String portStr = String.valueOf(INT_DEFAULT_OWSERVER_PORT);
      int port = INT_DEFAULT_OWSERVER_PORT;
      String deviceAddress = null;
      String filename = null;
      String pollingIntervalStr = null;
      int pollingInterval = 0;

      // read values from config xml
      for(Element ele : propertyEles)
      {
        String elementName = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
        String elementValue = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);

        if (STR_ATTRIBUTE_NAME_HOSTNAME.equals(elementName))
        {
            hostname = elementValue;
            logger.debug("OneWire Command: hostname = " + hostname);
        }
        else if (STR_ATTRIBUTE_NAME_PORT.equals(elementName))
        {
            portStr = elementValue;
            logger.debug("OneWire Command: portStr = " + port);
        }
        else if (STR_ATTRIBUTE_NAME_DEVICE_ADDRESS.equals(elementName))
        {
            deviceAddress = elementValue;
            logger.debug("OneWire Command: deviceAddress = " + deviceAddress);
        }
        else if (STR_ATTRIBUTE_NAME_FILENAME.equals(elementName))
        {
            filename = elementValue;
            logger.debug("OneWire Command: filename = " + filename);
        }
        else if (STR_ATTRIBUTE_NAME_POLLING_INTERVAL.equals(elementName))
        {
          pollingIntervalStr = elementValue;
          logger.debug("OneWire Command: pollingInterval = " + pollingIntervalStr);
        }
      }

      // process/parse values
      try
      {
          port = Integer.parseInt(portStr);
          logger.debug("OneWire Command: port = " + port);
      }
      catch(NumberFormatException e)
      {
          logger.warn("Invalid port specified: " + portStr + "; using default owserver port (" + INT_DEFAULT_OWSERVER_PORT+")");
      }

      try
      {
        if ((null != pollingIntervalStr) && (pollingIntervalStr.trim().length() > 0)) {
          pollingInterval = Strings.convertPollingIntervalString(pollingIntervalStr);
        } else {
          throw new NoSuchCommandException("Unable to create OneWire command, no pollingInterval given");
        }
      }
      catch (NumberFormatException e)
      {
        throw new NoSuchCommandException("Unable to create OneWire command, invalid pollingInterval specified!");
      }

      if (null == hostname || null == deviceAddress || null == filename)
      {
        throw new NoSuchCommandException("Unable to create OneWireCommand, missing configuration parameter(s)");
      }

      logger.debug("OneWire Command created successfully");
      return new OneWireCommand(hostname, port, deviceAddress, filename, pollingInterval);
  }
}
