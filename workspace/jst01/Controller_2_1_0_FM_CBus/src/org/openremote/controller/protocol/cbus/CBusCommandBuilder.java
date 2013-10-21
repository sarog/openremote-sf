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
package org.openremote.controller.protocol.cbus;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;

/**
 * Builds the CBus commands
 */
public class CBusCommandBuilder implements CommandBuilder
{

   /**
    * A common log category name intended to be used across all classes related
    * to CBus implementation.
    */
   public final static String CBUS_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "cbus";


   /**
    * String constant for parsing CBUS protocol XML entries from
    * controller.xml file.
    */
   public final static String CBUS_XMLPROPERTY_COMMAND = "command";

   /**
    * String constant for parsing CBUS protocol XML entries from
    * controller.xml file.
    */
   public final static String CBUS_XMLPROPERTY_NETWORK = "network";

   /**
    * String constant for parsing CBUS protocol XML entries from
    * controller.xml file.
    */
   public final static String CBUS_XMLPROPERTY_APPLICATION = "application";

   /**
    * String constant for parsing CBUS protocol XML entries from
    * controller.xml file.
    */
   public final static String CBUS_XMLPROPERTY_GROUP_NAME = "group";

   /**
    * String constant for parsing CBUS protocol XML entries from
    * controller.xml file.
    */
   public final static String CBUS_XMLPROPERTY_VALUE = "command_value";

   /**
    * The logger
    */
   private final static Logger log = Logger.getLogger(CBusCommandBuilder.CBUS_LOG_CATEGORY);


   /**
    * The CBus gateway to create this command for
    */
   private CBusGateway gateway;
   
   /**
    * Default constructor
    */
   public CBusCommandBuilder()
   {

   }
   

   public CBusGateway getGateway() 
   {
      return gateway;
   }

   public void setGateway(CBusGateway gateway) 
   {
      this.gateway = gateway;
   }

   /**
    * CommandBuilder interface
    * 
    * Parses the CBUS command XML snippets and builds a
    * corresponding CBUS command instance.
    * <p>
    * 
    * The expected XML structure is:
    * 
    * <pre>
    * @code
    * <command protocol = "cbus" >
    *   <property name = "command" value = ""/> SETVALUE/ON/OFF/DIM/PULSE/STATUS
    *   <property name = "network" value = ""/> network name eg. Local Network (empty will use default network)
    *   <property name = "application" value = ""/>  application eg. Lighting/Enable/Trigger (empty will use Lighting)
    *   <property name = "group" value = ""/> group name eg. Dining Room Lts
    *   <property name = "command_value" value = ""/> pipe-delimited values for write commands
    *           
    * </command>
    * }
    * </pre>
    * 
    * Additional properties not listed here are ignored.
    * 
    * @throws NoSuchCommandException
    *            if the CBUS command instance cannot be
    *            constructed from the XML snippet for any reason
    * 
    * @return an immutable CBUS command instance
    */
   @Override
   public Command build(Element element)
   {

      String commandAsString = null;
      String networkAsString = null;
      String applicationAsString = null;
      String groupNameAsString = null;
      String commandValueAsString = null;

      CBusCommandType command = null;

      //get parameterised value for passing parameterised values from touchscreens
      //find the parameterised attribute
      String parameterisedValue = element.getAttributeValue(Command.DYNAMIC_VALUE_ATTR_NAME);
      log.debug("Parameterised value=" + parameterisedValue);


      // Get the list of properties from XML...
      @SuppressWarnings("unchecked")
      List<Element> propertyElements = element.getChildren(XML_ELEMENT_PROPERTY, element.getNamespace());

      for (Element el : propertyElements) 
      {
         String propertyName = el.getAttributeValue(XML_ATTRIBUTENAME_NAME);
         String propertyValue = el.getAttributeValue(XML_ATTRIBUTENAME_VALUE);

         if (CBUS_XMLPROPERTY_COMMAND.equalsIgnoreCase(propertyName))
         {
            commandAsString = propertyValue;
         }

         else if (CBUS_XMLPROPERTY_VALUE.equalsIgnoreCase(propertyName)) 
         {
            commandValueAsString = propertyValue;
         }
      
         else if (CBUS_XMLPROPERTY_NETWORK.equalsIgnoreCase(propertyName)) 
         {
            networkAsString = propertyValue;
         }

         else if (CBUS_XMLPROPERTY_APPLICATION.equalsIgnoreCase(propertyName)) 
         {
            applicationAsString = propertyValue;
         }

         else if (CBUS_XMLPROPERTY_GROUP_NAME.equalsIgnoreCase(propertyName)) 
         {
            groupNameAsString = propertyValue;
         }

         else if(!"name".equalsIgnoreCase(propertyName)) 
         {
            log.warn("Unknown CBUS command property '<" + XML_ELEMENT_PROPERTY + " " + XML_ATTRIBUTENAME_NAME + " = \"" + propertyName + "\" " + XML_ATTRIBUTENAME_VALUE + " = \"" + propertyValue + "\"/>'.");
         }
      }

      log.debug("Checking mandatory CBUS properties");

      // Sanity check on mandatory properties
      if (commandAsString == null || "".equals(commandAsString)) 
      {
         log.error("CBUS command must have a '" +  CBUS_XMLPROPERTY_COMMAND + "' property.");
         throw new NoSuchCommandException("CBUS command must have a '" + CBUS_XMLPROPERTY_COMMAND + "' property.");
      }
      
      if (applicationAsString == null || "".equals(applicationAsString)) 
      {
         applicationAsString = "Lighting";
      }


      if (groupNameAsString == null || "".equals(groupNameAsString)) 
      {
         log.error("CBUS command must have a '" +  CBUS_XMLPROPERTY_GROUP_NAME + "' property.");
         throw new NoSuchCommandException("CBUS command must have a '" + CBUS_XMLPROPERTY_GROUP_NAME + "' property.");
      }


      // convert command type
      StringBuilder typeBeingConverted = new StringBuilder("command: ").append(commandAsString);
      try 
      {
         command = CBusCommandType.valueOf(commandAsString.toUpperCase().trim());

      } catch (Exception e) //IllegalArgumentException or NullPointerException
      {
         log.error("Invalid CBUS Command Property: " + typeBeingConverted.toString(), e);
         throw new NoSuchCommandException(e.getMessage(), e);
      }  

      //replace any parameter placeholders with the parameterised value
      if(!command.equals(CBusCommandType.STATUS) && parameterisedValue != null && parameterisedValue.length() > 0)
      {
         Pattern pattern = Pattern.compile(Command.DYNAMIC_PARAM_PLACEHOLDER_REGEXP);      
         Matcher matcher = pattern.matcher(commandValueAsString);      

         if (matcher.find()) 
         {
            commandValueAsString = commandValueAsString.replaceAll(Command.DYNAMIC_PARAM_PLACEHOLDER_REGEXP, parameterisedValue);
         }
      }

      StringBuilder commandDetails = new StringBuilder(commandAsString).append(", ").append(networkAsString).append(", ").
            append(applicationAsString).append(", ").append(groupNameAsString).append(", ").append(commandValueAsString);
      
      Command cmd = null;
      if(gateway != null)
      {
         // Translate the command string to a type safe Command types...
         cmd = CBusCommand.createCommand(command, networkAsString, applicationAsString, groupNameAsString, commandValueAsString, gateway);
         log.debug("Created CBus Command: " + commandDetails.toString());
      }
      else
         log.error("*CBUS ERROR* Tried to create a command with no gateway set. Details: " + commandDetails.toString());

     
      return cmd;

   }



}
