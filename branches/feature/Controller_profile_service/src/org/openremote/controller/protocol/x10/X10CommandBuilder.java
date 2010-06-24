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
package org.openremote.controller.protocol.x10;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.utils.CommandUtil;

/**
 * The Class X10EventBuilder.
 * 
 * @author Dan 2009-4-30
 */
public class X10CommandBuilder implements CommandBuilder {

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public ExecutableCommand build(Element element) {
      X10Command x10Command = new X10Command();
      List<Element> propertyEles = element.getChildren("property", element.getNamespace());
      for(Element ele : propertyEles){
         if("address".equals(ele.getAttributeValue("name"))){
            x10Command.setAddress(ele.getAttributeValue("value"));
         } else if("command".equals(ele.getAttributeValue("name"))){
            x10Command.setCommand(CommandUtil.parseStringWithParam(element, ele.getAttributeValue("value")));
         }
      }
      return x10Command;
   }

}
