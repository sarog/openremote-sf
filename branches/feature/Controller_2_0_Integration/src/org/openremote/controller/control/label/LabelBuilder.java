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
package org.openremote.controller.control.label;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.control.Control;
import org.openremote.controller.control.ControlBuilder;
import org.openremote.controller.control.Status;
/**
 * This class is used to build a Label by parse controll.xml
 * @author Javen
 *
 */
public class LabelBuilder extends ControlBuilder {

   @SuppressWarnings("unchecked")
   @Override
   public Control build(Element controlElement, String commandParam) {
      Control control = new Label();
      List<Element> statuses = controlElement.getChildren(Control.STATUS_ELEMENT_NAME, controlElement.getNamespace());
      for (Element status : statuses) {
         List<Element> cmdsEle = status.getChildren(Control.COMMAND_ELEMENT_NAME, controlElement.getNamespace());
         for(Element cmd :cmdsEle){
            String cmdID = cmd.getAttributeValue(Control.CONTROL_COMMAND_REF_ATTRIBUTE_NAME);
            Element commandElement = remoteActionXMLParser.queryElementFromXMLById(controlElement.getDocument(),cmdID);
            StatusCommand statusCommand = (StatusCommand) commandFactory.getCommand(commandElement);
            control.setStatus(new Status(statusCommand));
         }
      }
      return control;
   }

}
