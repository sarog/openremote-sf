/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller.protocol.shellexe;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.utils.Logger;

/**
 * Builds a ShellExeCommand which can be used to execute shell scripts.
 * 
 * @author Marcus Redeker
 */
public class ShellExeCommandBuilder implements CommandBuilder {

   // Constants ------------------------------------------------------------------------------------

   public final static String SHELLEXE_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "SHELLEXE";

   private final static String STR_ATTRIBUTE_NAME_COMMAND_PATH = "commandPath";
   private final static String STR_ATTRIBUTE_NAME_COMMAND_PARAMS = "commandParams";

   // Class Members --------------------------------------------------------------------------------

   private final static Logger logger = Logger.getLogger(SHELLEXE_PROTOCOL_LOG_CATEGORY);

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public Command build(Element element) {
      logger.debug("Building WOL command");
      List<Element> propertyEles = element.getChildren("property", element.getNamespace());

      String commandPath = null;
      String commandParams = null;

      // read values from config xml

      for (Element ele : propertyEles) {
         String elementName = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
         String elementValue = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);

         if (STR_ATTRIBUTE_NAME_COMMAND_PATH.equals(elementName)) {
            commandPath = elementValue;
            logger.debug("ShellExe Command: commandPath= " + commandPath);
         } else if (STR_ATTRIBUTE_NAME_COMMAND_PARAMS.equals(elementName)) {
            commandParams = elementValue;
            logger.debug("ShellExe Command: commandParams = " + commandParams);
         }
      }

      if (null == commandPath || commandPath.trim().length() == 0) {
         throw new NoSuchCommandException("ShellExe command must have a commandPath property.");
      }

      logger.debug("ShellExe Command created successfully");

      return new ShellExeCommand(commandPath, commandParams);
   }

}
