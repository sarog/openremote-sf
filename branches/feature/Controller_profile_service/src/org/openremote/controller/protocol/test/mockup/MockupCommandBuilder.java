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
package org.openremote.controller.protocol.test.mockup;

import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;

/**
 * 
 * @author handy.wang 2010-03-18
 *
 */
public class MockupCommandBuilder implements CommandBuilder {

   private final static String STATUS_COMMAND = "STATUS";
   private final static String EXECUTE_COMMAND = "EXECUTE";
   private Logger logger = Logger.getLogger(this.getClass().getName());
   
   @SuppressWarnings("unchecked")
   @Override
   public Command build(Element element) {
      if (element.getChildren() != null && element.getChildren().size() != 0) {
         Element commandTypePropertyElement = null;
         List<Element> commandPropertyElements = element.getChildren("property", element.getNamespace());
         for (Element commandPropertyElement : commandPropertyElements) {
            if ("type".equals(commandPropertyElement.getAttributeValue("name"))) {
               commandTypePropertyElement = commandPropertyElement;
            }
         }         
         if (commandTypePropertyElement != null) {
            String commandStr = commandTypePropertyElement.getAttributeValue("value");
            MockupCommand mockupCommand = null;
            if (STATUS_COMMAND.equals(commandStr)) {
               mockupCommand = new MockupStatusCommand();
               initProperties(element, mockupCommand);
            } else if (EXECUTE_COMMAND.equals(commandStr)) {
               mockupCommand = new MockupExecutableCommand();
               initProperties(element, mockupCommand);
               
               String dynamicCommandForSlider = element.getAttributeValue(Command.DYNAMIC_VALUE_ATTR_NAME);
               if (dynamicCommandForSlider != null && !"".equals(dynamicCommandForSlider) && isNumber(dynamicCommandForSlider)) {
                  mockupCommand.setUrl(mockupCommand.getUrl().replaceAll("placeholder", dynamicCommandForSlider));
               }
            }
            return mockupCommand;
         }
      }
      return null;
   }
   
   @SuppressWarnings("unchecked")
   private void initProperties(Element mockupCommandElement, MockupCommand mockupCommand) {
      List<Element> mockupCommandPropertyElements = mockupCommandElement.getChildren("property", mockupCommandElement.getNamespace());
      for(Element mockupCommandPropertyElement : mockupCommandPropertyElements){
         if("URL".equalsIgnoreCase(mockupCommandPropertyElement.getAttributeValue("name"))){
            mockupCommand.setUrl(mockupCommandPropertyElement.getAttributeValue("value"));
         }
      }
   }
   
   private boolean isNumber(String commandStr) {
      try {
         Integer.parseInt(commandStr);
      } catch (NumberFormatException e) {
         logger.error("Invalid dynamicCommand : " + commandStr);
         return false;
      }
      return true;
   }

}
