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
package org.openremote.controller.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.RemoteActionXMLParser;
import org.openremote.controller.gateway.Gateway;
import org.openremote.controller.gateway.GatewayBuilder;
import org.openremote.controller.exception.ControllerXMLNotFoundException;
import org.openremote.controller.service.GatewayManagerService;
import org.openremote.controller.service.StatusCacheService;
import org.openremote.controller.exception.ControllerException;
import org.openremote.controller.utils.ConfigFactory;
import org.openremote.controller.utils.PathUtil;
import org.openremote.controller.utils.CommandUtil;
import org.openremote.controller.gateway.ProtocolFactory;
import org.openremote.controller.gateway.Protocol;
import org.openremote.controller.gateway.command.Command;
/**
 * 
 * @author Rich Turner 2011-02-09
 *
 */
public class GatewayManagerServiceImpl implements GatewayManagerService {
   private static Logger log = Logger.getLogger(GatewayManagerServiceImpl.class);
   private StatusCacheService statusCacheService;
   private RemoteActionXMLParser remoteActionXMLParser;
   private StringBuffer controllerXMLFileContent = new StringBuffer();
   private StringBuffer panelXMLFileContent = new StringBuffer();
   
   /* This is the protocol factory for returning the protocol builder */
   private ProtocolFactory protocolFactory;
   
   private Logger logger = Logger.getLogger("INIT");
   private List<Gateway> gateways = new ArrayList<Gateway>();
   private Map<Integer, Integer> commandGatewayMap = new HashMap<Integer, Integer>();
   /**
    * {@inheritDoc}
    */
   @Override   
   public void initGatewaysWithControllerXML(Document document) {
      /**
       * First store controller and panel xml files as xml change service
       * will be looking for changes every 15s
       */
      storeXMLContent(Constants.CONTROLLER_XML);
      storeXMLContent(Constants.PANEL_XML);
      
      /** 
       * Get list of commands and build gateways from this info<br />
       * Build gateway XML Elements and create gateways for each unique instance
       * In future should look at explicitly defining gateways in controller.xml.
       */
      List<Element> commandElements;
      List<Element> sensorElements;
      List<Element> gatewayElements;
      
      try {
         if (document == null) {
            commandElements = remoteActionXMLParser.queryElementsFromXMLByName("command");
            sensorElements = remoteActionXMLParser.queryElementsFromXMLByName("sensor");
            gatewayElements = getGatewayElements(commandElements);
         } else {
            commandElements = remoteActionXMLParser.queryElementsFromXMLByName(document, "command");
            sensorElements = remoteActionXMLParser.queryElementsFromXMLByName(document, "sensor");
            gatewayElements = getGatewayElements(commandElements);
         }
      } catch (ControllerXMLNotFoundException e) {
         logger.warn("No commands, sensors and/or gateways to init, controller.xml not found.");
         return;
      }
      
      // Build gateways
      for (Element gatewayElement : gatewayElements)
      {
         createGateway(gatewayElement, commandElements);
      }      
      
      /* Add each polling command map to corresponding gateway */
      createPollingCommandMaps(sensorElements);
   }

   public void createPollingCommandMaps(List<Element> sensorElements) {   
      if (sensorElements != null) {
         for (Element sensorElement : sensorElements)
         {            
            Gateway gateway = null;
            Integer sensorId = Integer.parseInt(sensorElement.getAttributeValue("id"));
            Integer commandId = 0;

            /* get polling command id and string */
            List<Element>childrenOfSensor = sensorElement.getChildren();
            for (Element childOfSensor:childrenOfSensor) {
              if ("include".equalsIgnoreCase(childOfSensor.getName()) && "command".equalsIgnoreCase(childOfSensor.getAttributeValue("type"))) {
                 commandId = Integer.parseInt(childOfSensor.getAttributeValue("ref"));
                 break;
              }
            }
            if (commandId > 0) {               
               /* Get the gateway that the sensor command belongs to and add the polling command */
               gateway = getCommandGateway(commandId);
               if (gateway != null) {
                  gateway.addPollingCommand(commandId, sensorId);
               }
               statusCacheService.saveOrUpdateStatus(sensorId, "N/A");
            }
         }
      }
   }
   
   /**
    * Create unique gateway elements from command elements, this is needed because
    * the controller.xml doesn't explicitly define gateway elements at present
    */
   public List<Element> getGatewayElements(List<Element> commandElements) {
      List<Element> gatewayElements = new ArrayList<Element>();
      if (commandElements != null) {
         for (Element commandElement : commandElements)
         {
            Element gatewayElement = getGatewayElement(commandElement);            
            if(gatewayElement != null) {
               int gatewayId = gatewayElements.indexOf(gatewayElement);
               if (gatewayId < 0) {
                  gatewayElements.add(gatewayElement);
               }
            }
         }
      }
      return gatewayElements;
   }
   
   private Element getGatewayElement(Element commandElement) {
      Element gatewayElement = null;
      String protocolType = commandElement.getAttributeValue("protocol");
      if ("telnet".equals(protocolType)) {
         List<String> props = new ArrayList<String>();
         props.add("ipAddress");
         props.add("port");
         props.add("promptString");
         gatewayElement = buildGatewayElement(commandElement, protocolType, props);
      }
      return gatewayElement;
   }   
      
   public Map<Integer, Command> getCommands(Element gatewayElement, List<Element> commandElements) {
      // Cycle through command Elements and find the ones that share the same gateway protocol settings
      Map<Integer, Command> commands = new HashMap<Integer, Command> ();
      // Fudge for gatewayID as currently this doesn't exist in the XML
      int gatewayId = this.gateways.size();
      if (commandElements != null) {
         for (Element commandElement : commandElements)
         {
            Element commandGatewayElement = getGatewayElement(commandElement);            
            if(commandGatewayElement != null) {
               if (commandGatewayElement.toString().equals(gatewayElement.toString())) {
                  // This command belongs to this gateway
                  Integer commandId = Integer.parseInt(commandElement.getAttributeValue("id"));
                  Command command = new Command(commandElement);
                  commands.put(commandId, command);
                  addCommandMap(commandId, new Integer(gatewayId));
               }
            }
         }
      }
      return commands;
   }
      
   public void createGateway(Element gatewayElement, List<Element> commandElements) {
      Gateway gateway = null;
      int gatewayId = this.gateways.size();
      try {
         gateway = new Gateway(gatewayId, gatewayElement, getProtocol(gatewayElement), getCommands(gatewayElement, commandElements), statusCacheService);
      }
      catch (Exception e) {
         logger.error("Gateway initialisation error");
      }
      this.gateways.add(gateway);
   }   
   
   private Element buildGatewayElement(Element element, String protocolType, List<String> props) {
      List<Element> propertyEles = element.getChildren("property", element.getNamespace());
      Element gatewayElement = new Element("gateway");
      gatewayElement.setAttribute("protocol", protocolType);
      //For testing set gateway connection type to permanent and sensor polling method to query
      gatewayElement.setAttribute("connectionType", "permanent");
      gatewayElement.setAttribute("pollingMethod", "query");
      gatewayElement.addContent("\n		");
      for(Element ele : propertyEles){
           if(props.contains(ele.getAttributeValue("name"))) {
              gatewayElement.addContent((Element)ele.clone());
              gatewayElement.addContent("\n		");
           }
      }
      return gatewayElement;
   }
   
   private void addCommandMap(Integer commandId, Integer gatewayId) {
        commandGatewayMap.put(commandId, gatewayId);
   }
   
   private void storeXMLContent(String xmlFileName) {
      String xmlFilePath = PathUtil.addSlashSuffix(ConfigFactory.getCustomBasicConfigFromDefaultControllerXML().getResourcePath()) + xmlFileName;
      File xmlFile = new File(xmlFilePath);
      try {
         StringBuffer fileContent = new StringBuffer(FileUtils.readFileToString(xmlFile, "utf-8"));
         if (Constants.CONTROLLER_XML.equals(xmlFileName)) {
            setControllerXMLFileContent(fileContent);
         } else if (Constants.PANEL_XML.equals(xmlFileName)) {
            setPanelXMLFileContent(fileContent);
         }
      } catch (IOException ioe) {
         logger.warn("Skipped " + xmlFileName + " change check, Failed to read " + xmlFile.getAbsolutePath());
      }
   }
   
   public void startGateways() {
      for (Gateway gateway : gateways) {
         gateway.start();  
      }
   }
   
   public void killGateways() {
      for (Gateway gateway : gateways) {
         gateway.kill();
      }
   }   
   
   public boolean restart() {
      System.out.println("Gateway Manager is restarting");
      
      // Kill and clear existing gateway data
      killGateways();
      clearGateways();

      // Clear status info
      clearStatusCache();

      // Rebuild and start gateways
      boolean success = true;
      try {
         initGatewaysWithControllerXML(null);
         startGateways();         
      } catch (ControllerException e) {
         logger.error("Failed to init gateway manager with controller.xml ." + e.getMessage(), e);
         success = false;
      }
      return success;
   }
   
   private void clearStatusCache() {
      this.statusCacheService.clearAllStatusCache();
   }
   
   private void clearGateways() {
      this.gateways.clear();
      this.commandGatewayMap.clear();
   }
   
   /**
    *
    * Get Set Methods Below here
    *
    */
   
   /* Find correct gateway for specified command */
   private Gateway getCommandGateway(Integer commandId) {
      return gateways.get(commandGatewayMap.get(commandId));
   }
   
   /* Get the protocol for a particular gateway */
   private Protocol getProtocol(Element gatewayElement) {
      Protocol protocol = (Protocol)protocolFactory.getProtocol(gatewayElement);
      return protocol;
   }
   
   public void setStatusCacheService(StatusCacheService statusCacheService) {
      this.statusCacheService = statusCacheService;
   }

   public void setRemoteActionXMLParser(RemoteActionXMLParser remoteActionXMLParser) {
      this.remoteActionXMLParser = remoteActionXMLParser;
   }
   
   public void setProtocolFactory(ProtocolFactory protocolFactory) {
      this.protocolFactory = protocolFactory;
   }
   
   public String getControllerXMLFileContent() {
      return this.controllerXMLFileContent.toString();
   }

   public String getPanelXMLFileContent() {
      return this.panelXMLFileContent.toString();
   }
   
   public void setControllerXMLFileContent(StringBuffer controllerXMLFileContent) {
      this.controllerXMLFileContent = controllerXMLFileContent;
   }
      
   public void setPanelXMLFileContent(StringBuffer panelXMLFileContent) {
      this.panelXMLFileContent = panelXMLFileContent;
   }      
}