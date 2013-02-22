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
package org.openremote.android.controller.protocol.socket;

import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.openremote.android.controller.command.CommandBuilder;
import org.openremote.android.controller.command.ExecutableCommand;
import org.openremote.android.controller.util.CommandUtil;


/**
 * The Class SocketEventBuilder.
 *
 * @author Marcus 2009-4-26
 * @author marcf@openremote.org
 */
public class TCPSocketCommandBuilder implements CommandBuilder {

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public ExecutableCommand build(Element element) {
	   
	   // The command 
      TCPSocketCommand tcpEvent = new TCPSocketCommand();
      
      // Get the properties as elements
      NodeList properties= element.getElementsByTagName("property");
      
      for (int i = 0; i< properties.getLength(); i++) {
    	  
    	  Element property = (Element) properties.item(i);
    	
    	  if("name".equals(property.getAttribute("name"))){
              tcpEvent.setName(property.getAttribute("value"));
           } else if("port".equals(property.getAttribute("name"))){
              tcpEvent.setPort(property.getAttribute("value"));
           } else if("ipAddress".equals(property.getAttribute("name"))){
              tcpEvent.setIp(property.getAttribute("value"));
           } else if("command".equals(property.getAttribute("name"))){
              tcpEvent.setCommand(CommandUtil.parseStringWithParam(element, property.getAttribute("value")));
           }
      }
      return tcpEvent;
   }

}
