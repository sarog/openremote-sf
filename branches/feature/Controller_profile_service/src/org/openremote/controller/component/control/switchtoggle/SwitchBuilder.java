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
package org.openremote.controller.component.control.switchtoggle;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.component.ComponentBuilder;
import org.openremote.controller.component.control.Control;

/**
 * It is mainly responsible for build Switch control with control element and commandParam.
 * 
 * @author Handy.Wang 2009-10-23
 */
public class SwitchBuilder extends ComponentBuilder {

   /* (non-Javadoc)
    * @see org.openremote.controller.control.ControlBuilder#build(org.jdom.Element, java.lang.String)
    */
   @SuppressWarnings("unchecked")
   @Override
   public Control build(Element controlElement, String commandParam) {
      Switch switchToggle = new Switch();
      if (!switchToggle.isValidActionWith(commandParam)) {
         return switchToggle;
      }
      List<Element> operationElements = controlElement.getChildren();
      for (Element childElementOfControl : operationElements) {
         if (isIncludedSensorElement(childElementOfControl, commandParam)) {
            //TODO: Parse status command with sensor element, because the following commented codes aren't useful for sensor-controller.xml any more.
//            Element statusCommandRefElement = (Element) childElementOfControl.getChildren().get(0);
//            String statusCommandID = statusCommandRefElement.getAttributeValue(Control.CONTROL_COMMAND_REF_ATTRIBUTE_NAME);
//            Element statusCommandElement = remoteActionXMLParser.queryElementFromXMLById(controlElement.getDocument(),statusCommandID);
//            if (statusCommandElement != null) {
//               StatusCommand statusCommand = (StatusCommand) commandFactory.getCommand(statusCommandElement);
//               switchToggle.setStatus(new Status(statusCommand));
//               break;
//            } else {
//               throw new NoSuchCommandException("Cannot find that command with id = " + statusCommandID);
//            }
         }
         if (commandParam.equalsIgnoreCase(childElementOfControl.getName())) {
            List<Element> commandRefElements = childElementOfControl.getChildren();
            for (Element commandRefElement : commandRefElements) {
               String commandID = commandRefElement.getAttributeValue(Control.CONTROL_COMMAND_REF_ATTRIBUTE_NAME);
               Element commandElement = remoteActionXMLParser.queryElementFromXMLById(controlElement.getDocument(),commandID);
               Command command = commandFactory.getCommand(commandElement);
               switchToggle.addExecutableCommand((ExecutableCommand) command);
            }      
            break;
         }
      }
      return switchToggle;
   }
   
   private boolean isIncludedSensorElement(Element childElementOfControl, String commandParam) {
      boolean isStatusCommandParam = commandParam.equalsIgnoreCase(Control.STATUS_ELEMENT_NAME);
      boolean isIncludeChildElememntOfControl = Control.INCLUDE_ELEMENT_NAME.equalsIgnoreCase(childElementOfControl.getName());
      boolean isIncludedSensor = Control.INCLUDE_TYPE_SENSOR.equals(childElementOfControl.getAttributeValue(Control.INCLUDE_TYPE_ATTRIBUTE_NAME));
      return isStatusCommandParam && isIncludeChildElememntOfControl && isIncludedSensor;
   }

}
