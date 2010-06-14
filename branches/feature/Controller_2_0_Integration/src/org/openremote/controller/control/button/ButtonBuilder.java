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
package org.openremote.controller.control.button;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.command.DelayCommand;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.control.Control;
import org.openremote.controller.control.ControlBuilder;

/**
 * The Class ButtonBuilder.
 * 
 * @author Handy.Wang 2009-10-15
 */
public class ButtonBuilder extends ControlBuilder {

    /**
     * Build Button with button xml element.
     * 
     * Button instance has been initialized with property status = Status(new NoStatusCommand()). 
     * So, if commandParam is non-executable command(e.g: status),
     * the Button instance will get default status with read method in the StatusCommand. 
     */
    @SuppressWarnings("unchecked")
   @Override
    public Control build(Element buttonElement, String commandParam) {
       //commandParam could be "click" 
       Button button = new Button();
       List<Element> commandRefElements = buttonElement.getChildren();
       for (Element commandRefElement : commandRefElements) {
           if (Control.DELAY_ELEMENT_NAME.equalsIgnoreCase(commandRefElement.getName())) {
               button.addExecutableCommand(new DelayCommand(commandRefElement.getTextTrim()));
               continue;
           }
           String commandID = commandRefElement.getAttributeValue(Control.CONTROL_COMMAND_REF_ATTRIBUTE_NAME);
           Element commandElement = remoteActionXMLParser.queryElementFromXMLById(buttonElement.getDocument(),commandID);
           ExecutableCommand command = (ExecutableCommand) commandFactory.getCommand(commandElement);
           button.addExecutableCommand(command);
       }
        return button;
    }
}
