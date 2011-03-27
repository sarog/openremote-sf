/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.controller.gateway.command;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.jdom.Element;
import java.util.Arrays;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import java.util.StringTokenizer;
import org.openremote.controller.utils.CommandUtil;
/**
 * 
 * @author Rich Turner 2011-02-26
 */
public class Command
{
   /* The logger. */
   private Logger logger = Logger.getLogger(Command.class);
      
   /* String constant for the child property list elements: ("{@value}") */
   private static final String XML_ELEMENT_PROPERTY = "property";
   
   /* String constant for the property name attribute */
   private static final String XML_ATTRIBUTENAME_NAME = "name";
   
   /* String constant for the property value attribute */
   private static final String XML_ATTRIBUTENAME_VALUE = "value";   
   
   /** 
    * Attribute name of dynamic command value for slider, colorpicker.<br />
    * This attribute is temporary for holding dynamic control command value from REST API. <br />
    * Take slider for example: <br />
    * REST API: http://localhost:8080/controller/rest/control/{slider_id}/10 <br />
    * <b>10</b> means control command value of slider, which will be stored into the attribute named <b>DYNAMIC_VALUE_ATTR_NAME</b> of Command DOM element.
    */
   public static final String DYNAMIC_VALUE_ARG_NAME = "dynamicValue";

   /**
    * Dynamic parameter place holder regular expression.
    * When a command contains a dynamic value taken from a slider or color picker etc., 
    * this could be as simple as allowing '${param}' literal somewhere in the command value, 
    * any command builder should replace '${param}' with the command param value got from REST call.  
    */
   public static final String DYNAMIC_PARAM_PLACEHOLDER_REGEXP = "\\$\\{param\\}";

   /* Minimum Allowed Polling Interval */
   public static final Integer MIN_POLLING_INTERVAL = 1000;
   
   /**
    * Validation property, if command is invalid it will be ignored
    * by the gateway if an attempt is made to execute it
    */
   private Boolean valid = true;
   
   /* The action array */
   private List<Action> commandActions = new ArrayList<Action> ();
   
   /* The ID of this command */
   private Integer id;
   
   /* Optional polling interval */
   private Integer pollingInterval = 0;
   
   /* Constructor */   
   public Command(Integer commandId, Element commandElement) {
      //Extract actions from the command XML Elment
      if (commandElement != null) {
         this.id = commandId;
         String protocolType = commandElement.getAttributeValue("protocol");
         List<Element> propertyEles = commandElement.getChildren("property", commandElement.getNamespace());
         
         // Convert property elements to new gateway format which is protocol independent
         propertyEles = convertProperties(protocolType, propertyEles);
         
         for(Element element : propertyEles){
            String propertyValue = CommandUtil.parseStringWithParam(commandElement, element.getAttributeValue("value"));;
            String property = element.getAttributeValue("name");
            String value = "";
            Map<String, String> args = new HashMap<String, String>();
            
            // Look for optional command polling interval parameter
            if("pollinginterval".equalsIgnoreCase(property)) {
               try {
                  Integer num = Integer.parseInt(propertyValue);
                  if (num > MIN_POLLING_INTERVAL) {
                     this.pollingInterval = num;
                  }
               } catch (NumberFormatException e) {
                  logger.warn("Invalid command polling interval value.");
               }
            }
            /**
             * Build command actions list. Only interested in property
             * elements that are send, read or script others are protocol related
             */
            if("send".equals(property)) {
               // Doesn't support action args as only means of communication with protocol is through the outputStream
               value = propertyValue;
               if (value.length() == 0) {
                  this.valid = false;  
               }               
            } else if ("read".equals(property)) {
               // Doesn't take any parameters just reads inputStream
               value = "";
            } else if ("script".equals(property)) {
               // Script actions take scriptName as value and then any number of args
               // Action arg format should be semi-colon separated param value pairs param=value;param=value;...
               List<String> valArgPairs = Arrays.asList(Pattern.compile("[^\\];").split(propertyValue));
               for (String valArgPair : valArgPairs){
                  valArgPair = Pattern.compile("\\;").matcher(valArgPair).replaceAll(";");
                  String[] paramArray = valArgPair.split("=");
                  if (paramArray.length != 2) {
                     this.valid = false;
                     break;
                  } else {
                     // Pull out the action value params
                     if("scriptName".equals(paramArray[0])) {
                        value = paramArray[1];
                     } else {
                        args.put(paramArray[0], paramArray[1]);
                     }
                  }
               }
               if (value.length() == 0) {
                  this.valid = false;  
               }
            }
               
            // Add action if valid otherwise abort and warn user
            if (this.valid) {
               addAction(new Action(value, EnumCommandActionType.enumValueOf(property), args));
            } else {
               logger.error("Command action is not valid: '" + property + ": " + propertyValue + "'");
               break;
            }
         }
         
         // Check there's at least one action
         if (this.commandActions.size() == 0) {
            this.valid = false;  
         }
      }
   }
   
   /**
    * Convert command properties to new gateway format
    * Can only assume old commands are send actions in new gateway command structure
    * as we don't have any more information available at this point
    */
   private List<Element> convertProperties(String protocolType, List<Element> propertyEles) {
      List<Element> formattedEles = new ArrayList<Element>();
      List<String> props = new ArrayList<String>();
      String actionValue = "";
      
      // Get list of properties that form a command for this protocol
      if ("telnet-gateway".equals(protocolType)) {
         props.add("command");
      } else if ("http-gateway".equals(protocolType)) {
         props.add("url");
      } else if ("x10-gateway".equals(protocolType)) {
         props.add("address");
         props.add("command");
      } else if ("onewire-gateway".equals(protocolType)) {
         props.add("filename");
         props.add("deviceaddress");
      } else if ("knx-gateway".equals(protocolType)) {
         props.add("groupaddress");
         props.add("dpt");
         props.add("command");
      } else if ("socket-gateway".equals(protocolType)) {
         props.add("command");
      } else if ("udp-gateway".equals(protocolType)) {
         props.add("command");
      }
      
      // Cycle through the properties and pick out the ones needed to build this protocol command
      for (Element element : propertyEles) {
         String propertyValue = element.getAttributeValue("value");
         String property = element.getAttributeValue("name").toLowerCase();
         if(props.contains(property)) {
            // If props only contains 1 item then this is a simple command so no need to store paramValue pair list
            if (props.size() == 1) {
               actionValue = propertyValue;
            } else {
               actionValue += property + "=" + propertyValue + ";";
            }
         }
      }
      if (actionValue.length() > 0) {
         // Telnet protocol could have multiple send commands in one using the pipe as a seperator, check for this
         if ("telnet-gateway".equals(protocolType) && actionValue.indexOf("|") >= 0) {
            StringTokenizer st = new StringTokenizer(actionValue, "|");
            int count = 0;
            while (st.hasMoreElements()) {
               String cmd = (String) st.nextElement();
               if (count % 2 != 0) {
                  formattedEles.add(buildPropertyElement(cmd));
               }
               count++;
            }
         } else {
            formattedEles.add(buildPropertyElement(actionValue));
         }
      }
      return formattedEles;
   }
   
   /* Build the new property Element */
   private Element buildPropertyElement(String value) {
      Element propElement = new Element("property");
      propElement.setAttribute("name", "send");
      propElement.setAttribute("value", value);
      return propElement;
   }
   
   /* Set command valid flag */
   public void setCommandIsValid(Boolean valid) {
      this.valid = valid;
   }
   
   /* Get command valid flag */
   public Boolean isValid() {
      return this.valid;
   }
   
   /* Add an action to the command */
   public void addAction(Action action) {
      if(action != null) {
         this.commandActions.add(action);
      }  
   }
   
   /* Remove last action */
   public void removeAction() {
      removeAction(-1);  
   }
   
   /* Remove an action */
   public void removeAction(int actionNum) {
      if(actionNum >= 0) {
           actionNum = this.commandActions.size() - 1;
      }
      if (actionNum >=0) {
         try {
            this.commandActions.remove(actionNum);
         } catch (Exception e) {}  
      }
   }
   
   /* Get actions */
   public List<Action> getActions() {
      return this.commandActions;  
   }
   
   /* Get number of actions in command */
   public int getActionCount() {
      return this.commandActions.size();  
   }
   
   /* Get command id */
   public Integer getId() {
      return this.id;  
   }

   /* Get polling interval */
   public Integer getPollingInterval() {
      return this.pollingInterval;
   }
}