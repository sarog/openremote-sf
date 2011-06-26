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
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import org.openremote.controller.gateway.Gateway;
import org.openremote.controller.gateway.exception.GatewayException;
import org.openremote.controller.gateway.command.Command;
/**
 * 
 * @author Rich Turner 2011-04-11
 *
 */
public class GatewayCreatorUtil {

   /**
    * A List of supported gateway Protocols, these protocols will be handled
    * by the gateway manager whereas others will go through the standard controller route
    */
   public static final List<String> supportedProtocols = Arrays.asList("telnet", "http", "udp", "socket");
   
   /**
    * *********************************
    * GATEWAY ELEMENT BUILDER CODE START
    * *********************************
    */
   
   /**
    * Create unique gateway elements from command elements, this is needed because
    * the controller.xml doesn't explicitly define gateway elements at present
    */
   public static List<Element> getGatewayElements(List<Element> commandElements) {
      List<Element> gatewayElements = new ArrayList<Element>();
      if (commandElements != null) {
         for (Element commandElement : commandElements)
         {
            Element gatewayElement = getGatewayElement(commandElement, gatewayElements.size()+1);
            if(gatewayElement != null) {
               String gatewayConnStr = getGatewayConnectionString(gatewayElement);
               int matchedId = -1;
               int gatewayIndex = 0;
               for (Element gatewayElem : gatewayElements) {
                  if(gatewayConnStr.equals(getGatewayConnectionString(gatewayElem))) {
                     matchedId = gatewayIndex;
                     break;
                  }
                  gatewayIndex++;
               }
               if (matchedId < 0) {
                  gatewayElements.add(gatewayElement);
               }
            }
         }
      }
      return gatewayElements;
   }
   
   /**
    * Build Gateway Element from supplied Command Element, the property elements extracted
    * from the command Element are protocol specific and are set here at present
    */
   private static Element getGatewayElement(Element commandElement, int gatewayIndex) {
      Element gatewayElement = null;
      String protocolType = commandElement.getAttributeValue("protocol");
      if (isProtocolSupported(protocolType)) {
         Map<String, Boolean> protocolProps = getProtocolProperties(protocolType);
         if (protocolProps.size() > 0) {
            gatewayElement = buildGatewayElement(gatewayIndex, commandElement, protocolType, protocolProps);
         }
      }
      return gatewayElement;
   }
   
   /**
    * Get list of property names that the specified protocol uses, if protocol
    * not supported then an empty map is returned. Compulsory properties are indicated
    * by a true value for the keys value
    */
   private static Map<String, Boolean> getProtocolProperties(String protocolType) {
      Map<String, Boolean> props = new HashMap<String, Boolean>();
      
      if ("telnet".equals(protocolType)) {
         props.put("host", true);
         props.put("port", true);
         props.put("sendterminator", false);
      }
	   if("socket".equals(protocolType)) {
		   props.put("host", true);
		   props.put("port", true);
		   props.put("sendterminator", false);
	   }   

      if ("http".equals(protocolType)) {
          props.put("host", true);
          props.put("port", true);
          props.put("method", false);
          props.put("contenttype", false);
      }
      
      if ("udp".equals(protocolType)) {
         props.put("host", true);
         props.put("port", true);
      }
      
      // General properties applicable to all protocols
      props.put("defaultpollinginterval", false);
      props.put("connecttimeout", false);
      props.put("readtimeout", false);
      
      return props;
   }
   
   /**
    * The connection string is a semi-colon separated list of protocol connection
    * parameters that should be unique for each gateway. This is used to determine
    * if a gateway Element is unique or not.
    */
   private static String getGatewayConnectionString(Element gatewayElement) {
      String connectionStr = "";
      String protocolType = gatewayElement.getAttributeValue("protocol");
      List<Element> propertyEles = gatewayElement.getChildren();
      Map<String, Boolean> props = getProtocolProperties(protocolType);
      
      for(Element ele : propertyEles){
         Boolean isCompulsory = props.get(ele.getAttributeValue("name").toLowerCase());
         if (isCompulsory != null) { 
            if(isCompulsory) {
               connectionStr += ele.getAttributeValue("name") + "=" + ele.getAttributeValue("value") + ";";
           }
        }
      }
      if (connectionStr.length() > 0) {
         connectionStr = connectionStr.substring(0, connectionStr.length() - 1);
      }
      return connectionStr;
   }
   
   /**
    * Does the actual gateway Element construction, extracting the requested properties from the
    * command Element and sets the connectionType and pollingMethod to be used
    */
   private static Element buildGatewayElement(int gatewayIndex, Element element, String protocolType, Map<String, Boolean> props) {
      List<Element> propertyEles = element.getChildren("property", element.getNamespace());
      Element gatewayElement = new Element("gateway", element.getNamespace());
      int gatewayId = gatewayIndex;
      gatewayElement.setAttribute("protocol", protocolType);
      //For testing set gateway connection type to permanent and sensor polling method to query
      gatewayElement.setAttribute(Gateway.ID_ATTRIBUTE_NAME, Integer.toString(gatewayId));
      gatewayElement.setAttribute(Gateway.CONNECTION_ATTRIBUTE_NAME, "permanent");
      gatewayElement.setAttribute(Gateway.POLLING_ATTRIBUTE_NAME, "query");
      gatewayElement.addContent("\n		");
      for(Element ele : propertyEles){
           if(props.containsKey(ele.getAttributeValue("name").toLowerCase())) {
         	  gatewayElement.addContent((Element)ele.clone());
         	  gatewayElement.addContent("\n		");
           } else if ("http".equals(protocolType) && "url".equals(ele.getAttributeValue("name").toLowerCase())) {
      	     Element propElement = new Element("property", element.getNamespace());
      	     propElement.setAttribute("name", "host");
      	     propElement.setAttribute("value", ProtocolUtil.getUrlHost(ele.getAttributeValue("value")));
      		  gatewayElement.addContent(propElement);
      		  gatewayElement.addContent("\n		");
      	  }
      }
      return gatewayElement;
   }   
   /**
    * *********************************
    * GATEWAY ELEMENT BUILDER CODE END
    * *********************************
    */
    
   /*
    * Return the commands that use the specified gateway Element, when controller xml supports gateways will be able to 
    * just look at the gateway ref attribute of the commands, until then we manually deterine which commands
    * belong to this gateway, adds processing but allows existing XML schema to be used.
    */
   public static List<Command> getCommands(Element gatewayElement, List<Element> commandElements) {
      List<Command> commands = new ArrayList<Command> ();
      String gatewayConnStr = "";
      
      if (commandElements != null && gatewayElement != null) {
         gatewayConnStr = getGatewayConnectionString(gatewayElement);
         // Cycle through command Elements and find the ones that share the same gateway protocol settings
         for (Element commandElement : commandElements)
         {
            Element tempGatewayElement = getGatewayElement(commandElement, 0);
            if(tempGatewayElement != null) {
               if(gatewayConnStr.equalsIgnoreCase(getGatewayConnectionString(tempGatewayElement))) {
                  Integer commandId = Integer.parseInt(commandElement.getAttributeValue("id"));
                  Command command = new Command(commandId, commandElement);
                  commands.add(command);
               }
            }
         }
      }
      
      if (commands.size() == 0) {
         throw new GatewayException("No gateway commands found.");
      }
      return commands;
   }
   
   /* Determines if specified protocol is supported by gateway manager */
   public static Boolean isProtocolSupported(String protocolType) {
      return supportedProtocols.contains(protocolType);
   }
}