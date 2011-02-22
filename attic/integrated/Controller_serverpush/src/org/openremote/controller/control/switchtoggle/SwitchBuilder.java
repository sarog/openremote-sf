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
package org.openremote.controller.control.switchtoggle;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.control.Control;
import org.openremote.controller.control.ControlBuilder;
import org.openremote.controller.control.Status;
import org.openremote.controller.exception.NoSuchCommandException;

/**
 * It is mainly responsible for build Switch control with control element and commandParam.
 * 
 * @author Handy.Wang 2009-10-23
 */
public class SwitchBuilder extends ControlBuilder {

   /* (non-Javadoc)
    * @see org.openremote.controller.control.ControlBuilder#build(org.jdom.Element, java.lang.String)
    */
   @SuppressWarnings("unchecked")
   @Override
   public Control build(Element controlElement, String commandParam) {
      if (!isContainAction(commandParam)) {
         return new Switch();
      }
      List<Element> operationElements = controlElement.getChildren();
      Switch switchToggle = new Switch();
      for (Element operationElement : operationElements) {
         if (commandParam.equalsIgnoreCase(operationElement.getName()) && Control.STATUS_ELEMENT_NAME.equals(operationElement.getName())) {
            Element statusCommandRefElement = (Element) operationElement.getChildren().get(0);
            String statusCommandID = statusCommandRefElement.getAttributeValue(Control.CONTROL_COMMAND_REF_ATTRIBUTE_NAME);
            Element statusCommandElement = remoteActionXMLParser.queryElementFromXMLById(statusCommandID);
            if (statusCommandElement != null) {
               StatusCommand statusCommand = (StatusCommand) commandFactory.getCommand(statusCommandElement);
               switchToggle.setStatus(new Status(statusCommand));
               break;
            } else {
               throw new NoSuchCommandException("Cannot find that command with id = " + statusCommandID);
            }
         }
         if (commandParam.equalsIgnoreCase(operationElement.getName())) {
            List<Element> commandRefElements = operationElement.getChildren();
            for (Element commandRefElement : commandRefElements) {
               String commandID = commandRefElement.getAttributeValue(Control.CONTROL_COMMAND_REF_ATTRIBUTE_NAME);
               Element commandElement = remoteActionXMLParser.queryElementFromXMLById(commandID);
               Command command = commandFactory.getCommand(commandElement);
               switchToggle.addExecutableCommand((ExecutableCommand) command);
            }      
            break;
         }
      }
      return switchToggle;
   }
   
   /**
    * Checks if is contain action.
    * 
    * @return true, if is contain action
    */
   private boolean isContainAction(String commandParam) {
      for (String action : Switch.AVAILABLE_ACTIONS) {
         if (action.equalsIgnoreCase(commandParam)) {
            return true;
         }
      }
      return false;
   }

}
