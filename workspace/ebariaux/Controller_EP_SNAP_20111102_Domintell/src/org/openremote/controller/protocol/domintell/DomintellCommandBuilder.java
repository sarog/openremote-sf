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
package org.openremote.controller.protocol.domintell;

import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;

public class DomintellCommandBuilder implements CommandBuilder {

   /**
    * A common log category name intended to be used across all classes related
    * to Domintell implementation.
    */
   public final static String DOMINTELL_LOG_CATEGORY = "Domintell";

   /**
    * String constant for parsing Domintell protocol XML entries from
    * controller.xml file.
    */
   public final static String DOMINTELL_XMLPROPERTY_MODULE_TYPE = "module_type";

   /**
    * String constant for parsing Domintell protocol XML entries from
    * controller.xml file.
    */
   public final static String DOMINTELL_XMLPROPERTY_ADDRESS = "address";
   
   /**
    * String constant for parsing Domintell protocol XML entries from
    * controller.xml file.
    */
   public final static String DOMINTELL_XMLPROPERTY_COMMAND = "command";

   /**
    * String constant for parsing Domintell protocol XML entries from
    * controller.xml file.
    */
   public final static String DOMINTELL_XMLPROPERTY_OUTPUT = "output";

   // Class Members --------------------------------------------------------------------------------

   /**
    * Domitell logger. Uses a common category for all Domintell related logging.
    */
   private final static Logger log = Logger.getLogger(DomintellCommandBuilder.DOMINTELL_LOG_CATEGORY);

   // Instance Fields ------------------------------------------------------------------------------

   private DomintellGateway gateway;


  // Constructors ---------------------------------------------------------------------------------

  public DomintellCommandBuilder()
  {

  }


  
   // Implements EventBuilder ----------------------------------------------------------------------

   /**
    * Parses the Lutron HomeWorks command XML snippets and builds a
    * corresponding Lutron HomeWorks command instance.
    * <p>
    * 
    * The expected XML structure is:
    * 
    * <pre>
    * @code
    * <command protocol = "lutron_homeworks" >
    *   <property name = "address" value = ""/>
    *   <property name = "command" value = ""/>
    *   <property name = "scene" value = ""/>
    *   <property name = "key" value = ""/>
    *   <property name = "level" value = ""/>
    * </command>
    * }
    * </pre>
    * 
    * Additional properties not listed here are ignored.
    * 
    * @throws NoSuchCommandException
    *             if the Lutron HomeWorks command instance cannot be
    *             constructed from the XML snippet for any reason
    * 
    * @return an immutable Lutron HomeWorks command instance with known
    *         configured properties set
    */
   @Override
   public Command build(Element element) {
      String moduleTypeAsString = null;
      String addressAsString = null;
      String commandAsString = null;
      String outputAsString = null;
/*    
      LutronHomeWorksAddress address = null;
      Integer scene = null;
      Integer key = null;
      Integer level = null;
  */  
      Integer output = null;
      
      // Get the list of properties from XML...

      @SuppressWarnings("unchecked")
    List<Element> propertyElements = element.getChildren(XML_ELEMENT_PROPERTY, element.getNamespace());

      for (Element el : propertyElements) {
         String propertyName = el.getAttributeValue(XML_ATTRIBUTENAME_NAME);
         String propertyValue = el.getAttributeValue(XML_ATTRIBUTENAME_VALUE);

         if (DOMINTELL_XMLPROPERTY_MODULE_TYPE.equalsIgnoreCase(propertyName)) {
            moduleTypeAsString = propertyValue;
         }
         
         else if (DOMINTELL_XMLPROPERTY_ADDRESS.equalsIgnoreCase(propertyName)) {
            addressAsString = propertyValue;
         }

         else if (DOMINTELL_XMLPROPERTY_COMMAND.equalsIgnoreCase(propertyName)) {
            commandAsString = propertyValue;
         }
         
         else if (DOMINTELL_XMLPROPERTY_OUTPUT.equalsIgnoreCase(propertyName)) {
            outputAsString = propertyValue;
         }

         else {
            log.warn("Unknown Domintell property '<" + XML_ELEMENT_PROPERTY + " " + XML_ATTRIBUTENAME_NAME + " = \"" + propertyName + "\" " + XML_ATTRIBUTENAME_VALUE + " = \"" + propertyValue + "\"/>'.");
         }
      }

      // Sanity check on mandatory properties 'module type' and 'command'

      if (moduleTypeAsString == null || "".equals(moduleTypeAsString)) {
         throw new NoSuchCommandException("Domintell command must have a '" + DOMINTELL_XMLPROPERTY_MODULE_TYPE + "' property.");         
      }
      
      if (commandAsString == null || "".equals(commandAsString)) {
         throw new NoSuchCommandException("Domintell command must have a '" + DOMINTELL_XMLPROPERTY_COMMAND + "' property.");
      }

      /*
      // If an address was provided, attempt to build Lutron Address
      // instance...

      if (addressAsString != null && !"".equals(addressAsString)) {
         log.info("Will attemp to build address");

         try {
            address = new LutronHomeWorksAddress(addressAsString.trim());
         } catch (InvalidLutronHomeWorksAddressException e) {
           log.error("Invalid Lutron HomeWorks address", e);
            // TODO: re-check, message is not clear when address is invalid

            throw new NoSuchCommandException(e.getMessage(), e);
         }
      }
*/
      // If an output was provided, attempt to convert to integer
      if (outputAsString != null && !"".equals(outputAsString)) {
         try {
            output = Integer.parseInt(outputAsString);
         } catch (NumberFormatException e) {
           log.error("Invalid output number", e);
            throw new NoSuchCommandException(e.getMessage(), e);
         }
      }
  /*    
      
      if (level == null) {
         // No specific level provided, check for parameter (passed in from Slider)
         String paramValue = element.getAttributeValue(Command.DYNAMIC_VALUE_ATTR_NAME);
         if (paramValue != null && !paramValue.equals("")) {
            try {
               level = Integer.parseInt(paramValue);
            } catch (NumberFormatException e) {
              log.error("Invalid param value", e);
               throw new NoSuchCommandException(e.getMessage(), e);
            }
         }
      }
      
      // Translate the command string to a type safe Lutron Command types...
*/
      Command cmd = DomintellCommand.createCommand(commandAsString, gateway, moduleTypeAsString, new DomintellAddress(addressAsString), output);

//      log.info("Created Lutron Command " + cmd + " for address '" + address + "'");

      return cmd;

   }



   // Getters / Setters ----------------------------------------------------------------------------

   public DomintellGateway getGateway() {
      return gateway;
   }

   public void setGateway(DomintellGateway gateway) {
      this.gateway = gateway;
   }
}
