/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.control.slider;

import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.control.Control;
import org.openremote.controller.control.ControlBuilder;
import org.openremote.controller.control.Status;
import org.openremote.controller.exception.InvalidElementException;
import org.openremote.controller.exception.NoSuchCommandException;

/**
 * Slider builder.
 * 
 * @author Handy.Wang 2009-11-10
 */
public class SliderBuilder extends ControlBuilder {
   
   private Logger logger = Logger.getLogger(this.getClass().getName());

   /* (non-Javadoc)
    * @see org.openremote.controller.control.ControlBuilder#build(org.jdom.Element, java.lang.String)
    */
   @SuppressWarnings("unchecked")
   @Override
   public Control build(Element controlElement, String commandParam) {
      if (!isContainAction(commandParam)) {
         return new Slider();
      }
      
      Slider slider = new Slider();
      List<Element> operationElements = controlElement.getChildren(); 
      for (Element operationElement : operationElements) {
         /** Status Element */
         if (commandParam.equalsIgnoreCase(operationElement.getName()) && Control.STATUS_ELEMENT_NAME.equals(operationElement.getName())) {
            Element statusCommandRefElement = (Element) operationElement.getChildren().get(0);
            String statusCommandID = statusCommandRefElement.getAttributeValue(Control.CONTROL_COMMAND_REF_ATTRIBUTE_NAME);
            Element statusCommandElement = remoteActionXMLParser.queryElementFromXMLById(statusCommandID);
            if (statusCommandElement != null) {
               StatusCommand statusCommand = (StatusCommand) commandFactory.getCommand(statusCommandElement);
               slider.setStatus(new Status(statusCommand));
               break;
            } else {
               throw new NoSuchCommandException("Cannot find that command with id = " + statusCommandID);
            }
         }
         
         /** Non-Status Element */
         if (Slider.EXE_CONTENT_ELEMENT_NAME.equalsIgnoreCase(operationElement.getName())) {
            Element commandRefElement = (Element) operationElement.getChildren().get(0);
            String commandID = commandRefElement.getAttributeValue(Control.CONTROL_COMMAND_REF_ATTRIBUTE_NAME);
            Element commandElement = remoteActionXMLParser.queryElementFromXMLById(commandID);
            commandElement.setText(commandParam);
            Command command = commandFactory.getCommand(commandElement);
            slider.addExecutableCommand((ExecutableCommand) command);
            break;
         } else {
            throw new InvalidElementException("Don't support element name \"" + operationElement.getName() + "\" in slider.");
         }
      }
      return slider;
   }

   /**
    * Check whether the commandParam contained the action whitch slider allows. 
    */
   private boolean isContainAction(String commandParam) {
      for (String action : Slider.AVAILABLE_ACTIONS) {
         if (action.equalsIgnoreCase(commandParam)) {
            return true;
         }
      }
      try {
         Integer.parseInt(commandParam);
         return true;
      } catch (NumberFormatException e) {
         logger.error("Parsed numberCommand exception in slider builder.", e);
         throw new NoSuchCommandException("Don't support command param " + commandParam + " in slider." , e);
      }
   }

}
