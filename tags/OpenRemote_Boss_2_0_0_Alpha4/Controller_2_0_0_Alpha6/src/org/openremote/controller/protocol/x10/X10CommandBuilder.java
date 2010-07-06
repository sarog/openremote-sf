/*
 * OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.protocol.x10;

import org.jdom.Element;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.exception.CommandBuildException;

import java.util.List;

/**
 * The Class X10EventBuilder.
 * 
 * @author Dan 2009-4-30
 * @author Jerome Velociter
 * @author <a href="mailto:juha@openremote.org>Juha Lindfors</a>
 */
public class X10CommandBuilder implements CommandBuilder {

   public final static String X10_LOG_CATEGORY = "X10";
   public final static String X10_ADDRESS_XML_PROPERTY_NAME = "address";
   public final static String X10_COMMAND_XML_PROPERTY_NAME = "command";

   private X10ControllerManager connectionManager = new X10ControllerManager();

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public ExecutableCommand build(Element element)
   {
      String address = null;
      String commandAsString = null;

      List<Element> propertyElements = element.getChildren("property", element.getNamespace());

      for (Element el : propertyElements)
      {
        String x10CommandPropertyName = el.getAttributeValue("name");

        if (X10_ADDRESS_XML_PROPERTY_NAME.equals(x10CommandPropertyName))
        {
          address = el.getAttributeValue("value");
        }
        else if (X10_COMMAND_XML_PROPERTY_NAME.equals(x10CommandPropertyName))
        {
          commandAsString = el.getAttributeValue("value");
        }
      }

      if (commandAsString == null || commandAsString.trim().equals("") ||
          address == null || address.trim().equals(""))
      {
        throw new CommandBuildException(
            "Can not build a X10Command with empty command: " + commandAsString +
            " or address: " + address
        );
      }

      //String address = element.getAttributeValue(ADDRESS_XML_ATTRIBUTE);
      //String commandAsString = element.getAttributeValue(COMMAND_XML_ATTRIBUTE);

      X10CommandType commandType = null;

      if (X10CommandType.ALL_UNITS_OFF.isEqual(commandAsString)) {
         commandType = X10CommandType.ALL_UNITS_OFF;
      } else if (X10CommandType.SWITCH_ON.isEqual(commandAsString)) {
         commandType = X10CommandType.SWITCH_ON;
      } else if (X10CommandType.SWITCH_OFF.isEqual(commandAsString)) {
         commandType = X10CommandType.SWITCH_OFF;
      }

// TODO : integrate ${param} handling
//        CommandUtil.parseStringWithParam(element, ele.getAttributeValue("value")

      X10Command event = new X10Command(connectionManager, address, commandType);

      return event;
   }

}
       