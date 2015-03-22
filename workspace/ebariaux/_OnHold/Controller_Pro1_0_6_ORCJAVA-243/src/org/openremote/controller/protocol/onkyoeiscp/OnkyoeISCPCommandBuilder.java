/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.controller.protocol.onkyoeiscp;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.utils.Logger;

/**
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 * 
 */
public class OnkyoeISCPCommandBuilder implements CommandBuilder {

   // Constants ------------------------------------------------------------------------------------

   /**
    * A common log category name intended to be used across all classes related to Onkyo eISCP implementation.
    */
   public final static String ONKYO_EISCP_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "ONKYO_EISCP";

   /**
    * String constant for parsing Onkyo eISCP protocol XML entries from controller.xml file.
    */
   public final static String ONKYO_EISCP_XMLPROPERTY_NAME = "name";

   public final static String ONKYO_EISCP_XMLPROPERTY_COMMAND = "command";

   public final static String ONKYO_EISCP_XMLPROPERTY_PARAMETER = "parameter";

   // Class Members --------------------------------------------------------------------------------

   /**
    * Onkyo eISCP logger. Uses a common category for all Onkyo eISCP related logging.
    */
   private final static Logger log = Logger.getLogger(OnkyoeISCPCommandBuilder.ONKYO_EISCP_LOG_CATEGORY);

   // Instance Fields ------------------------------------------------------------------------------

   private OnkyoeISCPGateway gateway;

   // Constructors ---------------------------------------------------------------------------------

   public OnkyoeISCPCommandBuilder() {

   }

   /**
    * Parses the Onkyo eISCP command XML snippets and builds a corresponding Onkyo eISCP command instance.
    * <p>
    * 
    * The expected XML structure is:
    * 
    * <pre>
    * @code
    * <command protocol = "onkyo_eISCP" >
    *   <property name = "command" value = ""/>
    *   <property name = "parameter" value = ""/>
    * </command>
    * }
    * </pre>
    * 
    * Additional properties not listed here are ignored.
    * 
    * @throws NoSuchCommandException
    *            if the Onkyo eISCP command instance cannot be constructed from the XML snippet for any reason
    * 
    * @return an immutable Onkyo eISCP command instance with known configured properties set
    */
   @Override
   public Command build(Element element) {
      String commandAsString = null;
      String parameter = null;

      // Get the list of properties from XML...

      @SuppressWarnings("unchecked")
      List<Element> propertyElements = element.getChildren(XML_ELEMENT_PROPERTY, element.getNamespace());

      for (Element el : propertyElements) {
         String propertyName = el.getAttributeValue(XML_ATTRIBUTENAME_NAME);
         String propertyValue = el.getAttributeValue(XML_ATTRIBUTENAME_VALUE);

         if (ONKYO_EISCP_XMLPROPERTY_COMMAND.equalsIgnoreCase(propertyName)) {
            commandAsString = propertyValue;
         }

         else if (ONKYO_EISCP_XMLPROPERTY_PARAMETER.equalsIgnoreCase(propertyName)) {
            parameter = propertyValue;
         }

         else if (!ONKYO_EISCP_XMLPROPERTY_NAME.equalsIgnoreCase(propertyName)) { // name property is allowed but we're not interested in its value
            log.warn("Unknown Onkyo eISCP property '<" + XML_ELEMENT_PROPERTY + " " + XML_ATTRIBUTENAME_NAME + " = \"" + propertyName + "\" " + XML_ATTRIBUTENAME_VALUE + " = \"" + propertyValue + "\"/>'.");
         }
      }
      
      // Sanity check on mandatory property 'command'

      if (commandAsString == null || "".equals(commandAsString)) {
         throw new NoSuchCommandException("Onkyo eISCP command must have a '" + ONKYO_EISCP_XMLPROPERTY_COMMAND + "' property.");
      }

      if (parameter == null) {
         // No specific parameter provided, check for parameter (passed in from Slider)
         String paramValue = element.getAttributeValue(Command.DYNAMIC_VALUE_ATTR_NAME);
         if (paramValue != null && !paramValue.equals("")) {
            parameter = paramValue;
         }
      }
      
      // Translate the command string to a type safe Onkyo eISCP Command types...

      Command cmd = OnkyoeISCPCommand.createCommand(commandAsString, gateway, parameter);

      log.info("Created Onkyo eISCP Command " + cmd);

      return cmd;
   }

   // Getters / Setters ----------------------------------------------------------------------------

   public OnkyoeISCPGateway getGateway() {
      return gateway;
   }

   public void setGateway(OnkyoeISCPGateway gateway) {
      this.gateway = gateway;
   }

}
