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

import java.security.InvalidParameterException;
import java.util.List;

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
   private final static String EXECUTE_COMMAND = "EXCUTE";
   
   @Override
   public Command build(Element element) {
      String commandStr = element.getAttributeValue("value");
      
      MockupCommand mockupCommand = null;
      if (STATUS_COMMAND.equals(commandStr)) {
         mockupCommand = new MockupStatusCommand();
         initProperties(element, mockupCommand);
      } else if (EXECUTE_COMMAND.equals(commandStr)) {
         mockupCommand = new MockupExecutableCommand();
         initProperties(element, mockupCommand);
      } else if (isNumber(commandStr)) {
         mockupCommand = new MockupExecutableCommand();
         initProperties(element, mockupCommand);
         mockupCommand.setUrl(mockupCommand.getUrl().replaceAll("placeholder", commandStr));
         
      } else {
         throw new InvalidParameterException("No such command parameter value : " + commandStr);
      }
      return mockupCommand;
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
         return false;
      }
      return true;
   }

}
