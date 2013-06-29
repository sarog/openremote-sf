/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.controller.protocol.snmp;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.utils.CommandUtil;


/**
 * The Class SocketEventBuilder.
 *
 * @author Rde01
 */
public class SnmpCommandBuilder implements CommandBuilder {

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public ExecutableCommand build(Element element) {
      SnmpCommand snmpEvent = new SnmpCommand();
      List<Element> propertyEles = element.getChildren("property", element.getNamespace());
      for(Element ele : propertyEles){
         if("name".equals(ele.getAttributeValue("name"))){
            snmpEvent.setName(ele.getAttributeValue("value"));
         } else if("port".equals(ele.getAttributeValue("name"))){
            snmpEvent.setPort(ele.getAttributeValue("value"));
         } else if("ipAddress".equals(ele.getAttributeValue("name"))){
            snmpEvent.setIp(ele.getAttributeValue("value"));
         } else if("oid".equals(ele.getAttributeValue("name"))){
            snmpEvent.setOid(ele.getAttributeValue("value"));
         } else if("command".equals(ele.getAttributeValue("name"))){
            snmpEvent.setCommand(CommandUtil.parseStringWithParam(element, ele.getAttributeValue("value")));
         } else if("setvalue".equals(ele.getAttributeValue("name"))){
             snmpEvent.setSetvalue(CommandUtil.parseStringWithParam(element, ele.getAttributeValue("value")));
         } else if("settype".equals(ele.getAttributeValue("name"))){
             snmpEvent.setSettype(CommandUtil.parseStringWithParam(element, ele.getAttributeValue("value")));
         } else if("getregex".equals(ele.getAttributeValue("name"))){
             snmpEvent.setGetregex(CommandUtil.parseStringWithParam(element, ele.getAttributeValue("value")));
         } else if("getregexreplacement".equals(ele.getAttributeValue("name"))){
             snmpEvent.setGetregexreplacement(CommandUtil.parseStringWithParam(element, ele.getAttributeValue("value")));
         }
      }
      return snmpEvent;
   }

}
