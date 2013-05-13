/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2013, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.protocol.vera;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.protocol.vera.model.VeraDevice;
import org.openremote.controller.service.Deployer;
import org.openremote.controller.utils.Logger;
import org.openremote.devicediscovery.domain.DiscoveredDeviceAttrDTO;
import org.openremote.devicediscovery.domain.DiscoveredDeviceDTO;

/**
 * 
 * @author Marcus
 */
public class VeraCommandBuilder implements CommandBuilder {

   // Constants ------------------------------------------------------------------------------------
   public final static String VERA_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "Vera";

   private final static String STR_ATTRIBUTE_NAME_DEVICE = "device";
   private final static String STR_ATTRIBUTE_NAME_COMMAND = "command";

   // Class Members --------------------------------------------------------------------------------
   private final static Logger logger = Logger.getLogger(VERA_PROTOCOL_LOG_CATEGORY);

   // Instance Fields ------------------------------------------------------------------------------
   private VeraClient client;
   private Deployer deployer;

   // Constructors ---------------------------------------------------------------------------------
   public VeraCommandBuilder(Deployer deployer) {
      this.deployer = deployer;
      Map<String, String> properties = this.deployer.getConfigurationProperties();
      String address = properties.get("vera.address");
      this.client = new VeraClient(address);
      startVeraClient();
   }

   // Implements CommandBuilder --------------------------------------------------------------------

   @SuppressWarnings("unchecked")
   @Override
   public Command build(Element element) {
      if ((this.client == null) || (!this.client.isRunning())) {
            throw new NoSuchCommandException("Vera client is not started");
      }
      logger.debug("Building Vera command");
      List<Element> propertyEles = element.getChildren("property", element.getNamespace());

      String deviceIdString = null;
      String commandValue = null;
      int deviceId;

      // read values from config xml

      for (Element ele : propertyEles) {
         String elementName = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
         String elementValue = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);

         if (STR_ATTRIBUTE_NAME_COMMAND.equals(elementName)) {
            commandValue = elementValue;
            logger.debug("Vera command: command = " + commandValue);
         }
         else if (STR_ATTRIBUTE_NAME_DEVICE.equals(elementName)) {
            deviceIdString = elementValue;
            logger.debug("Vera command: deviceId = " + deviceIdString);
         }
      }

      if (null == commandValue || null == deviceIdString ) {
         throw new NoSuchCommandException("Unable to create Vera command, missing configuration parameter(s)");
      }
      
      try {
         deviceId = Integer.parseInt(deviceIdString);
      } catch (NumberFormatException e) {
         throw new NoSuchCommandException("Given device id is not an integer: " + deviceIdString);
      }

      String paramValue = element.getAttributeValue(Command.DYNAMIC_VALUE_ATTR_NAME);
      try {
         VeraCmd cmd = VeraCmd.valueOf(commandValue.trim().toUpperCase());
         logger.debug("Vera command created successfully");
         return new VeraCommand(deviceId, cmd, paramValue, client);
      } catch (Exception e) {
         throw new NoSuchCommandException("Invlid commad: " + commandValue);
      }
   }

   private synchronized void startVeraClient() {
      Collection<VeraDevice> devices = client.startVeraClient().values();
      if ((devices != null) && (!devices.isEmpty())) {
         List<DiscoveredDeviceDTO> list = prepareDiscoveredDevices(devices);
         if (!list.isEmpty() && (deployer != null)) {
            deployer.announceDiscoveredDevices(list);
         }
      }
   }

   private List<DiscoveredDeviceDTO> prepareDiscoveredDevices(Collection<VeraDevice> list) {
      List<DiscoveredDeviceDTO> result = new ArrayList<DiscoveredDeviceDTO>();
      for (VeraDevice device : list) {
         DiscoveredDeviceDTO a = new DiscoveredDeviceDTO();
         a.setModel("N/A");
         a.setName(device.getName());
         a.setProtocol("vera");
         a.setType(device.getCategory().name());
         a.setUsed(false);
         List<DiscoveredDeviceAttrDTO> deviceAttrs = new ArrayList<DiscoveredDeviceAttrDTO>();
         DiscoveredDeviceAttrDTO b = new DiscoveredDeviceAttrDTO();
         b.setName("id");
         b.setValue("" + device.getId());
         deviceAttrs.add(b);
         a.setDeviceAttrs(deviceAttrs);
         result.add(a);
      }
      return result;
   }

}
