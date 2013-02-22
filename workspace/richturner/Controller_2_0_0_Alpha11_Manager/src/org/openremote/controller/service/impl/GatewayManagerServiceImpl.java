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
import org.openremote.controller.utils.CommandCreatorUtil;
import org.openremote.controller.utils.GatewayCreatorUtil;
import org.openremote.controller.Constants;
import org.openremote.controller.command.RemoteActionXMLParser;
import org.openremote.controller.exception.ControllerXMLNotFoundException;
import org.openremote.controller.exception.NoSuchComponentException;
import org.openremote.controller.service.GatewayManagerService;
import org.openremote.controller.service.StatusCacheService;
import org.openremote.controller.utils.ConfigFactory;
import org.openremote.controller.utils.PathUtil;
import org.openremote.controller.gateway.Gateway;
import org.openremote.controller.gateway.exception.GatewayException;
import org.openremote.controller.gateway.command.*;
import org.openremote.controller.gateway.component.EnumComponentType;

/**
 * 
 * @author Rich Turner 2011-02-09
 *
 */
public class GatewayManagerServiceImpl implements GatewayManagerService {
   private StatusCacheService statusCacheService;
   private RemoteActionXMLParser remoteActionXMLParser;
   private StringBuffer controllerXMLFileContent = new StringBuffer();
   private StringBuffer panelXMLFileContent = new StringBuffer();
   
   private Logger logger = Logger.getLogger(GatewayManagerServiceImpl.class);
   private List<Gateway> gateways = new ArrayList<Gateway>();
   private Map<Integer, Integer> commandGatewayMap = new HashMap<Integer, Integer>();
   
   // Methods -------------------------------------------------------------------------
   /**
    * Process controller.xml and extract Gateway elements and instantiate each one
    * and associate commands with respective gateway
    */
   public void initGatewaysWithControllerXML(Document document) {
      /**
       * First store controller and panel xml files as xml change service
       * will be looking for changes every 15s
       */
      // Handled by PollingMachinesService
      // storeXMLContent(Constants.CONTROLLER_XML);
      // storeXMLContent(Constants.PANEL_XML);
      
      /** 
       * Get list of commands and build gateways from this info
       * Build gateway XML Elements and create gateways for each unique instance
       * In future should look at explicitly defining gateways in controller.xml.
       */
      List<Element> commandElements;
      List<Element> sensorElements;
      List<Element> gatewayElements;
      
      try {
         if (document == null) {
            commandElements = remoteActionXMLParser.queryElementsFromXMLByName("command");
            commandElements = filterAndFormatSupportedCommands(commandElements);
            sensorElements = remoteActionXMLParser.queryElementsFromXMLByName("sensor");
            gatewayElements = remoteActionXMLParser.queryElementsFromXMLByName("gateway");
         } else {
            commandElements = remoteActionXMLParser.queryElementsFromXMLByName(document, "command");
            commandElements = filterAndFormatSupportedCommands(commandElements);
            sensorElements = remoteActionXMLParser.queryElementsFromXMLByName(document, "sensor");
            gatewayElements = remoteActionXMLParser.queryElementsFromXMLByName("gateway");
         }
         if (gatewayElements == null) {
            gatewayElements = GatewayCreatorUtil.getGatewayElements(commandElements);
            if (gatewayElements.size() == 0) {
               throw new GatewayException();
            }
         }         
      } catch (ControllerXMLNotFoundException e) {
         logger.warn("No commands, sensors and/or gateways to init, controller.xml not found.");
         return;
      } catch (Exception e) {
         logger.warn("Error retrieving gateway elements from controller.xml.", e);
         return;
      }
      
      // Build gateways
      for (Element gatewayElement : gatewayElements)
      {
         try {
            /* Create gateway */
            createGateway(gatewayElement, commandElements);
         } catch (GatewayException e) {
            logger.error("Failed to create gateway: " + e.getMessage());
         }
            
      }      
      
      /* Add each polling command map to corresponding gateway */
      createPollingCommandMaps(sensorElements);
   }
   
   /* Start the gateways */
   public void startGateways() {
      for (Gateway gateway : gateways) {
         gateway.startUp();  
      }
   }   
   
   /* Fetch gateways from controller.xml and start them */
   public void restartGateways() {
      String restartMsg = "Gateway Manager is restarting"; 
      logger.info(restartMsg);
      System.out.println(restartMsg);
      
      /* Kill and clear existing gateway data */
      stopGateways();
      clearGateways();

      /* Clear status info */
      clearStatusCache();

      /* Rebuild and start gateways */
      initGatewaysWithControllerXML(null);
      startGateways();
   }
   
   /* Stop each gateway */
   public void stopGateways() {
      for (Gateway gateway : gateways) {
         gateway.closeDown();
      }
   }
   
   /* Clear existing status data */
   private void clearStatusCache() {
      this.statusCacheService.clearAllStatusCache();
   }
   
   /* Remove gateways from manager */
   private void clearGateways() {
      this.gateways.clear();
      this.commandGatewayMap.clear();
   }
   
   /* Trigger the commands associated with a panel control */
   public void trigger(String controlId, String controlAction, List<Integer> controlCommands) {
      Element controlElement = remoteActionXMLParser.queryElementFromXMLById(controlId);
      if (controlElement == null) {
         throw new NoSuchComponentException("No such component id :" + controlId);
      }
            
      /* Get control type without having to instantiate control */
      String componentTypeStr = controlElement.getName();
      EnumComponentType componentType = EnumComponentType.enumValueOf(componentTypeStr);
      if (componentType == null) {
         throw new GatewayException("No such component type in component type enum definition:" + componentTypeStr);
      }
      
      // Validate control action      
      Boolean actionValid = isComponentActionValid(componentType, controlAction);
      
      if (actionValid && controlCommands != null) {         
         /* Execute each command through respective gateway */
         try {
            for (Integer commandId : controlCommands) {
               Gateway commandGateway = getCommandGateway(commandId);
               commandGateway.doCommand(commandId, controlAction);
            }
         } catch (Exception e) {
            logger.error("Unhandled gateway exception: " + e.getMessage(), e);
         }
      }
   }
   
   /**
    * Find command for each sensor and then find the gateway that command
    * belongs to, finally add the commandId and SensorId to a Map
    */
   private void createPollingCommandMaps(List<Element> sensorElements) {   
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
       
   /* Instantiate the gateway objects and add to the gateway manager */
   private void createGateway(Element gatewayElement, List<Element> commandElements) {
      Gateway gateway = null;
      List<Command> commands = GatewayCreatorUtil.getCommands(gatewayElement, commandElements);
      gateway = new Gateway(gatewayElement, commands, statusCacheService);
      
      if (gateway != null) {
         /* Map commands to this gateway */
         for(Command command : commands) {
            addCommandMap(command.getId(), gateway.getGatewayId());
         }
      }
      this.gateways.add(gateway);
   }
   
   /* Filter command elements and return only supported protocol command elemets */
   private List<Element> filterAndFormatSupportedCommands(List<Element> commandElements) throws Exception {
      List<Element> supportedCommandElements = new ArrayList<Element> ();
      for (Element commandElement : commandElements)
      {
         String protocolType = commandElement.getAttributeValue("protocol");
         if (GatewayCreatorUtil.isProtocolSupported(protocolType)) {
            commandElement = CommandCreatorUtil.convertCommandElement(commandElement);
            supportedCommandElements.add(commandElement);  
         }
      }
      return supportedCommandElements;
   }
   
   /* Add a Command Gateway Map */
   private void addCommandMap(Integer commandId, Integer gatewayId) {
        commandGatewayMap.put(commandId, gatewayId);
   }
   
   /* Find correct gateway for specified command */
   private Gateway getCommandGateway(Integer commandId) {
      Gateway gateway = null;
      if(gateways.size() > 0) {
         Integer gatewayIndex = commandGatewayMap.get(commandId);
         if (gatewayIndex != null) {
            gateway = gateways.get(gatewayIndex - 1);
         }
      }
      return gateway;
   }
   
   /* Store the panel and controller xml file content */
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
   
   /*
    * ****************************************
    * PANEL CONTROL COMMAND KLUDGE CODE START
    * ****************************************
    */
    
   /**
    * There's no way to get supported actions from current component model without
    * instantiating, this is not efficient and should be reviewed
    */
   public Boolean isComponentActionValid(EnumComponentType componentType, String controlAction) {
      Boolean response = false;
      Boolean actionValidated = false;
      List<String> supportedActions = new ArrayList<String>();
      switch(componentType) {
         case BUTTON:
            supportedActions.add("click");
            break;
         case TOGGLE:
            break;
         case SWITCH:
            supportedActions.add("on");
            supportedActions.add("off");
            break;
         case LABEL:
            break;
         case SLIDER:
            // An integer isn't a very descriptive action type
            try {
               Integer.parseInt(controlAction);
               response = true;
            } catch (NumberFormatException e) {}
            actionValidated = true;
            break;
         case GESTURE:
            supportedActions.add("swipe");
            break;
         case IMAGE:
            break;
      }
      if(!actionValidated) {
         response = supportedActions.contains(controlAction);
      }
      return response;
   }
   
   
   
   /*
    *
    * Returns the commands that should be triggered by the control
    * look at the <include type="command" ref="" /> elements up to two levels
    * deep below the control Element. This is an alternative to instantiating the control
    * object.
    */
   public List<Integer> getComponentCommandIds(String controlId, String controlAction) {
      Element controlElem = remoteActionXMLParser.queryElementFromXMLById(controlId);
      if (controlElem == null) {
         throw new NoSuchComponentException("No such component id :" + controlId);
      }
      
      List<Integer> commandIds = new ArrayList<Integer>();
      String controlType = controlElem.getName();
      if (controlType == "switch") {
         controlElem = controlElem.getChild(controlAction, controlElem.getNamespace());
      }
      if (controlElem == null) {
         return commandIds;
      }
      List<Element> children = controlElem.getChildren();
      for (Element childElem : children) {
         Integer commandId = getElemCommandId(childElem);
         if (commandId != null) {
            // Get the commandElement and determine protocol
            Element commandElement = remoteActionXMLParser.queryElementFromXMLById(commandId.toString());
            if (commandElement != null) {
               String protocolType = commandElement.getAttributeValue("protocol");
               if (GatewayCreatorUtil.isProtocolSupported(protocolType)) {
                  commandIds.add(commandId);
               } else {
                  commandIds.clear();
                  break;  
               }
            }
         }
         if (childElem.getChildren().size() > 0) {
            List<Element> childChildElems = childElem.getChildren();
            for (Element childChildElem : childChildElems) {
               commandId = getElemCommandId(childChildElem);
               if (commandId != null) {
                  // Get the commandElement and determine protocol
                  Element commandElement = remoteActionXMLParser.queryElementFromXMLById(commandId.toString());
                  if (commandElement != null) {
                     String protocolType = commandElement.getAttributeValue("protocol");
                     if (GatewayCreatorUtil.isProtocolSupported(protocolType)) {                  
                        commandIds.add(commandId);
                     } else {
                        commandIds.clear();
                        break;  
                     }  
                  }
               }
            }
         }
      }
      return commandIds;
   }
   
   private Integer getElemCommandId(Element elem) {
      Integer commandId = null;
      if("include".equalsIgnoreCase(elem.getName())) {
         if("command".equalsIgnoreCase(elem.getAttributeValue("type"))) {
            try {
               commandId = (Integer.parseInt(elem.getAttributeValue("ref")));
            } catch (Exception e) {}
         }
      }
      return commandId;      
   }

   /*
    * ****************************************
    * PANEL CONTROL COMMAND KLUDGE CODE END
    * ****************************************
    */

   /**
    * ****************************************
    * Spring Context Set Methods Below here
    * ****************************************
    */
   
   public void setStatusCacheService(StatusCacheService statusCacheService) {
      this.statusCacheService = statusCacheService;
   }

   public void setRemoteActionXMLParser(RemoteActionXMLParser remoteActionXMLParser) {
      this.remoteActionXMLParser = remoteActionXMLParser;
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