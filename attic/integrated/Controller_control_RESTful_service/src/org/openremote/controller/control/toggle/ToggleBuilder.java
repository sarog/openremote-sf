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
package org.openremote.controller.control.toggle;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.control.Control;
import org.openremote.controller.control.ControlBuilder;
import org.openremote.controller.exception.NoSuchCommandException;

/**
 * The Class ToggleBuilder.
 * 
 * @author Handy.Wang 2009-10-15
 */
public class ToggleBuilder extends ControlBuilder {
   
    /* (non-Javadoc)
     * @see org.openremote.controller.control.ControlBuilder#build(org.jdom.Element, java.lang.String)
     */
   @SuppressWarnings("unchecked")
   @Override
    public Control build(Element toggleElement, String commandParam) {
      if (!isContainAction(commandParam)) {
         return new Toggle();
      }
      Toggle toggle = new Toggle();
      List<Element> subElements = toggleElement.getChildren();

      for (int i = 0; i < subElements.size(); i++) {
         Element element = subElements.get(i);
         // status element
         if (Control.STATUS_ELEMENT_NAME.equalsIgnoreCase(element.getName())) {
            Element commandElementRef = (Element) element.getChildren().get(0);
            String statusCommandID = commandElementRef.getAttributeValue(Control.CONTROL_COMMAND_REF_ATTRIBUTE_NAME);
            Element statusCommandElement = remoteActionXMLParser.queryElementFromXMLById(statusCommandID);
            if (statusCommandElement != null) {
               StatusCommand statusCommand = (StatusCommand) commandFactory.getCommand(statusCommandElement);
               toggle.getStatus().setStatusCommand(statusCommand);
               continue;
            } else {
               throw new NoSuchCommandException("Cannot find that command with id = " + statusCommandID);
            }
         }

         // non-status elements
         List<Element> commandRefElements = element.getChildren();
         List<ExecutableCommand> executableCommands = new ArrayList<ExecutableCommand>();
         for (Element commandRefElement : commandRefElements) {
            String commandID = commandRefElement.getAttributeValue(Control.CONTROL_COMMAND_REF_ATTRIBUTE_NAME);
            Element commandElement = remoteActionXMLParser.queryElementFromXMLById(commandID);
            Command command = commandFactory.getCommand(commandElement);
            executableCommands.add((ExecutableCommand) command);
         }
         toggle.getStates().put(Toggle.SWITCH_STATUSES[i], executableCommands);
      }
      return toggle;
    }

   /**
    * Checks if is contain action.
    * 
    * @param commandParam the command param
    * 
    * @return true, if is contain action
    */
   private boolean isContainAction(String commandParam) {
      for (String action : Toggle.AVAILABLE_ACTIONS) {
         if (action.equals(commandParam)) {
            return true;
         }
      }
      return false;
   }
}
