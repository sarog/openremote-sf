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

/**
 * The Class X10EventBuilder.
 * 
 * @author Dan 2009-4-30
 * @author Jerome Velociter
 */
public class X10CommandBuilder implements CommandBuilder {

   public final static String X10_LOG_CATEGORY = "X10";
   public final static String ADDRESS_XML_ATTRIBUTE = "address";
   public final static String COMMAND_XML_ATTRIBUTE = "command";

   private X10ControllerManager connectionManager = new X10ControllerManager();

   /**
    * {@inheritDoc}
    */
   public ExecutableCommand build(Element element) {

      String address = element.getAttributeValue(ADDRESS_XML_ATTRIBUTE);
      String commandAsString = element.getAttributeValue(COMMAND_XML_ATTRIBUTE);

      X10CommandType commandType = null;

      if (X10CommandType.ALL_UNITS_OFF.isEqual(commandAsString)) {
         commandType = X10CommandType.ALL_UNITS_OFF;
      } else if (X10CommandType.SWITCH_ON.isEqual(commandAsString)) {
         commandType = X10CommandType.SWITCH_ON;
      } else if (X10CommandType.SWITCH_OFF.isEqual(commandAsString)) {
         commandType = X10CommandType.SWITCH_OFF;
      }

      X10Command event = new X10Command(connectionManager, address, commandType);

      return event;
   }

}
