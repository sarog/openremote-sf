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
package org.openremote.controller.utils;

import org.jdom.Element;
import org.jdom.Namespace;

import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.openremote.controller.gateway.Gateway;
import org.openremote.controller.gateway.exception.GatewayException;
import org.openremote.controller.gateway.command.Command;
import org.openremote.controller.gateway.command.EnumCommandActionType;
import org.openremote.controller.utils.ProtocolUtil;
/**
 * 
 * @author Rich Turner 2011-04-11
 *
 */
public class CommandCreatorUtil {

   /**
    * This class simply converts the existing XML command structure
    * into the new gateway XML command structure; gateway properties
    * are also added at present but ultimately the gateways will have
    * their own XML elements and commands will reference a gateway
    * can only assume intent of old commands is a send action (read actions
    * are automatically added by the gateway if the command was called by
    * a sensor polling request
    */
   
   // Convert property elements to new gateway format which is protocol independent
   public static Element convertCommandElement(Element commandElement) throws Exception {
      Element newCommandElement = new Element("command", commandElement.getNamespace());
      String protocolType = commandElement.getAttributeValue("protocol");
      List<Element> propertyEles = commandElement.getChildren("property", commandElement.getNamespace());

      String actionValue = "";
      Boolean isNewFormat = false;
      newCommandElement.addContent("\n		");
      
      // Cycle through the properties and pick out the ones needed to build this protocol command
      for(Element element : propertyEles) {
         String propertyValue = element.getAttributeValue("value");
         String property = element.getAttributeValue("name").toLowerCase();
         EnumCommandActionType actionType = EnumCommandActionType.enumValueOf(property);
         Namespace ns = commandElement.getNamespace();
         
         /**
          * If command contains a new command action element assume entire
          * command is already in new format and just return it
          */
         if (actionType != null) {
            // Assume this is a new format command
            newCommandElement = (Element)commandElement.clone();
            isNewFormat = true;
            break;
         } else {
         	// TELNET COMMAND
            if ("telnet".equals(protocolType)) {
            	// Process protocol properties
               if ("ipaddress".equals(property)) {
                  newCommandElement.addContent(buildPropertyElement("host", propertyValue, ns));
                  newCommandElement.addContent("\n		");
               }
               // Process command properties
               else if ("command".equals(property)) {
               	// Could have multiple send commands in one using the pipe as a separator, check for this
                  if (propertyValue.indexOf("|") >= 0) {
                     StringTokenizer st = new StringTokenizer(propertyValue, "|");
                     int count = 0;
                     while (st.hasMoreElements()) {
                        String cmd = (String) st.nextElement();
                        if (count % 2 != 0) {
                           newCommandElement.addContent(buildPropertyElement("send", "command=" + cmd, ns));
                           newCommandElement.addContent("\n		");
                        }
                        count++;
                     }
                  } else {
                  	if (propertyValue.indexOf("command=") < 0) {
                  		propertyValue = "command=" + propertyValue;
                  	}	
                  	newCommandElement.addContent(buildPropertyElement("send", propertyValue, ns));
                  	newCommandElement.addContent("\n		");
                  }
               }
               // Add all other properties
               else {
                  newCommandElement.addContent(buildPropertyElement(property, propertyValue, ns));
                  newCommandElement.addContent("\n		");
               }
            }
            // HTTP COMMAND
            else if ("http".equals(protocolType)) {
            	// Process protocol and command properties
            	if ("url".equals(property)) {
                  URL url = new URL(propertyValue);
                  newCommandElement.addContent(buildPropertyElement("host", url.getHost(), ns));
                  newCommandElement.addContent("\n		");
                  newCommandElement.addContent(buildPropertyElement("port", Integer.toString(url.getPort()), ns));
                  newCommandElement.addContent("\n		");
                  newCommandElement.addContent(buildPropertyElement("send", "command=" + url.getFile(), ns));
                  newCommandElement.addContent("\n		");
               }
               // Add all other properties
               else {
                  newCommandElement.addContent(buildPropertyElement(property, propertyValue, ns));
                  newCommandElement.addContent("\n		");
               }
            }
//		         } else if ("x10".equals(protocolType)) {
//		            props.add("address");
//		            props.add("command");
//		         } else if ("onewire".equals(protocolType)) {
//		            props.add("filename");
//		            props.add("deviceaddress");
//		         } else if ("knx".equals(protocolType)) {
//		            props.add("groupaddress");
//		            props.add("dpt");
//		            props.add("command");
//		         } else if ("socket".equals(protocolType)) {
//		            props.add("command");
//		         } else if ("udp".equals(protocolType)) {
//		            props.add("command");
//		         }
         }
      }
      
      if (!isNewFormat) {
         // Assign existing command attributes to new command
         newCommandElement.setAttribute("id", commandElement.getAttributeValue("id"));
         newCommandElement.setAttribute("protocol", protocolType);
      }
      
      return newCommandElement;
   }
   
   /* Build the new property Element */
   private static Element buildPropertyElement(String name, String value, Namespace ns) {
      Element propElement = new Element("property", ns);
      propElement.setAttribute("name", name);
      propElement.setAttribute("value", value);
      return propElement;
   }
        
   /**
    * *********************************
    * COMMAND ELEMENT BUILDER CODE END
    * *********************************
    */
}