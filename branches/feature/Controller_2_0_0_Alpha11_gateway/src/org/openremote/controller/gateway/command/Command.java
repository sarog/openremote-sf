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
import java.util.StringTokenizer;
import org.apache.log4j.Logger;
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
    * Validation property, if command is invalid it will be ignored
    * by the gateway if an attempt is made to execute it
    */
   private Boolean valid = true;
   
   /* The action array */
   private List<Action> commandActions = new ArrayList<Action> ();
   
   /* The ID of this command */
   private int id;
   
   /* Constructor */   
   public Command(Element commandElement) {
      //Extract actions from the command XML Elment
      if (commandElement != null) {
         List<Element> propertyEles = commandElement.getChildren("property", commandElement.getNamespace());
         
         for(Element element : propertyEles){
            String propertyValue = CommandUtil.parseStringWithParam(commandElement, element.getAttributeValue("value"));;
            String property = element.getAttributeValue("name");
            String value = "";
            Map<String, String> args = new HashMap<String, String>();
            
            // Only interested in property elements that are send, read or script others are protocol related
            if("send".equals(property) || "read".equals(property) || "script".equals(property)) {
               // Action arg format should be semi-colon separated param value pairs param=value;param=value;...
               
               // Check args have been specified if not a read action
               if (!"read".equals(property) && propertyValue.length() == 0) {
                     this.valid = false;                
               } else {
                  // Check args are valid and pull out action values for send and script actions
                  StringTokenizer st = new StringTokenizer(propertyValue, ";");
                  while (st.hasMoreElements()) {
                     String paramValuePair = (String) st.nextElement();
                     String[] paramArray = paramValuePair.split("=");
                     if (paramArray.length != 2) {
                        this.valid = false;
                        break;
                     } else {
                        // Pull out the action value params
                        if("command".equals(paramArray[0]) || "scriptName".equals(paramArray[0])) {
                           value = paramArray[1];
                        } else {
                           args.put(paramArray[0], paramArray[1]);
                        }
                     }
                  }
               }
               // Check a value is defined for send and script actions
               if(("send".equals(property) || "script".equals(property)) && value.length() == 0) {
                  this.valid = false;
               }
               
               // Add action if valid otherwise abort and warn user
               if (this.valid) {
                  addAction(new Action(value, EnumCommandActionType.enumValueOf(property), args));
               } else {
                  logger.error("Command action is not valid: '" + property + "'");
                  break;
               }
            }
         }
      }
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
}