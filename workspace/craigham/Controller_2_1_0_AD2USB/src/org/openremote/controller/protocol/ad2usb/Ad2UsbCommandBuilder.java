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
package org.openremote.controller.protocol.ad2usb;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.utils.Logger;

public class Ad2UsbCommandBuilder implements CommandBuilder {

   public final static String Ad2Usb_XMLPROPERTY_PARAMETER = "parameter1";

   public final static String Ad2Usb_XMLPROPERTY_COMMAND = "command";
   private static Logger logger = Logger.getLogger(Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "ad2usb");

   private Ad2UsbGateway mGateway;

   public Ad2UsbCommandBuilder(String hostName, String portNumber) {
      mGateway = new Ad2UsbGateway(hostName,Integer.parseInt(portNumber));
      mGateway.startGateway();
   }

   @Override
   public Command build(Element element) {

      @SuppressWarnings("unchecked")
      List<Element> propertyElements = element.getChildren(XML_ELEMENT_PROPERTY, element.getNamespace());

      String command = null;
      String parameter = null;

      for (Element el : propertyElements) {
         String propertyName = el.getAttributeValue(XML_ATTRIBUTENAME_NAME);
         String propertyValue = el.getAttributeValue(XML_ATTRIBUTENAME_VALUE);

         if (Ad2Usb_XMLPROPERTY_PARAMETER.equalsIgnoreCase(propertyName)) {
            parameter = propertyValue;
         } else if (Ad2Usb_XMLPROPERTY_COMMAND.equalsIgnoreCase(propertyName)) {
            command = propertyValue;
         } else {
            logger.warn("Unknown Ad2Usb property '<" + XML_ELEMENT_PROPERTY + " " + XML_ATTRIBUTENAME_NAME + " = \""
                  + propertyName + "\" " + XML_ATTRIBUTENAME_VALUE + " = \"" + propertyValue + "\"/>'.");
         }
      }
      // String paramValue = element.getAttributeValue(Command.DYNAMIC_VALUE_ATTR_NAME);
      if ("CMD_KEYPRESS".equals(command)) return new KeyPressCommand(mGateway, command, parameter);
      else
         return new Ad2UsbCommand("n/a", mGateway);
   }

}
