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
package org.openremote.controller.gateway.protocol.udp;

import java.util.List;
import org.jdom.Element;
import org.openremote.controller.gateway.Gateway;
import org.openremote.controller.gateway.protocol.Protocol;
import org.openremote.controller.gateway.protocol.ProtocolBuilder;

/**
 * The Class UdpProtocolBuilder.
 *
 * @author Rich Turner 2011-04-15
 */
public class UdpProtocolBuilder implements ProtocolBuilder {

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public Protocol build(Element element) {
      UdpProtocol protocol = new UdpProtocol();
      List<Element> propertyEles = element.getChildren();
      for(Element ele : propertyEles){
         // COMMON PROPERTIES SHOULD BE SUPPORTED BY ALL PROTOCOLS
         if("name".equalsIgnoreCase(ele.getAttributeValue("name"))){
            protocol.setName(ele.getAttributeValue("value"));
         } else if("readtimeout".equalsIgnoreCase(ele.getAttributeValue("name"))){
            protocol.setReadTimeout(ele.getAttributeValue("value"));
         } else if("connecttimeout".equalsIgnoreCase(ele.getAttributeValue("name"))){
            protocol.setConnectTimeout(ele.getAttributeValue("value"));
         }
         
         // PROTOCOL SPECIFIC PROPERTIES SHOULD BE AT LEAST ONE
         else if("host".equalsIgnoreCase(ele.getAttributeValue("name"))){
            protocol.setHost(ele.getAttributeValue("value"));
         } else if("port".equalsIgnoreCase(ele.getAttributeValue("name"))){
            protocol.setPort(ele.getAttributeValue("value"));
         }
      }
      return protocol;
   }

}
