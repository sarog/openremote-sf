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
package org.openremote.controller.control.monitor;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.control.Control;
import org.openremote.controller.control.ControlBuilder;
import org.openremote.controller.control.Status;
/**
 * This class is used to builder a Monitor from controller.xml 
 * @author Javen
 *
 */
public class MonitorBuilder extends ControlBuilder {

   /**
    * Build Monitor with monitor xml element.
    * 
    * Monitor instance has been initialized with property status = Status(new NoStatusCommand()). 
    * So, if commandParam is non-executable command(e.g: status),
    * the Monitor instance will get default status with read method in the StatusCommand. 
    */
   @SuppressWarnings("unchecked")
  @Override
   public Control build(Element monitorElement, String commandParam) {
      Monitor monitor = new Monitor();
      List<Element> statuses = monitorElement.getChildren();
      for (Element status : statuses) {
         if (status.getName().equalsIgnoreCase("status")) {
            List<Element> commands = status.getChildren();
            if (commands != null && commands.size() != 0) {
               for (Element command : commands) {
                  String commandID = command.getAttributeValue(Control.CONTROL_COMMAND_REF_ATTRIBUTE_NAME);
                  Element commandElement = remoteActionXMLParser.queryElementFromXMLById(commandID);
                  StatusCommand statusCommand = (StatusCommand) commandFactory.getCommand(commandElement);
                  monitor.setStatus(new Status(statusCommand));
               }
            }
         }
      }
       return monitor;
   }

}
