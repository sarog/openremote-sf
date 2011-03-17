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
import org.openremote.controller.exception.ControllerXMLNotFoundException;
import org.openremote.controller.exception.NoSuchComponentException;
import org.openremote.controller.service.GatewayManagerService;
import org.openremote.controller.service.StatusCacheService;
import org.openremote.controller.utils.ConfigFactory;
import org.openremote.controller.utils.PathUtil;
import org.openremote.controller.utils.CommandUtil;
import org.openremote.controller.gateway.Gateway;
import org.openremote.controller.gateway.GatewayBuilder;
import org.openremote.controller.gateway.exception.GatewayException;
import org.openremote.controller.gateway.ProtocolFactory;
import org.openremote.controller.gateway.Protocol;
import org.openremote.controller.gateway.command.Command;
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
   
   /* String constant for the top level gateway element connection type attribute: ("{@value}") */
   public final static String CONNECTION_ATTRIBUTE_NAME = "connectionType";
    
   /* String constant for the top level gateway element polling method attribute: ("{@value}") */
   public final static String POLLING_ATTRIBUTE_NAME = "pollingMethod";
     
   /* This is the protocol factory for returning the protocol builder */
   private ProtocolFactory protocolFactory;
   
   private Logger logger = Logger.getLogger(GatewayManagerServiceImpl.class);
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
            gatewayElements = remoteActionXMLParser.queryElementsFromXMLByName("gateway");
         } else {
            commandElements = remoteActionXMLParser.queryElementsFromXMLByName(document, "command");
            sensorElements = remoteActionXMLParser.queryElementsFromXMLByName(document, "sensor");
            gatewayElements = remoteActionXMLParser.queryElementsFromXMLByName("gateway");
         }
         if (gatewayElements == null) {
            gatewayElements = getGatewayElements(commandElements);
            if (gatewayElements.size() == 0) {
               throw new GatewayException();
            }
         }         
      } catch (ControllerXMLNotFoundException e) {
         logger.warn("No commands, sensors and/or gateways to init, controller.xml not found.");
         return;
      } catch (GatewayException e) {
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
    * *********************************
    * GATEWAY ELEMENT BUILDER CODE START
    * *********************************
    */
   
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
   
   private Element getGatewayElement(Element commandElement) {
      Element gatewayElement = null;
      String protocolType = commandElement.getAttributeValue("protocol");
      if ("telnet".equals(protocolType)) {
         List<String> props = new ArrayList<String>();
         props.add("ipAddress");
         props.add("port");
         props.add("promptString");
         props.add("timeOut");
         gatewayElement = buildGatewayElement(commandElement, protocolType, props);
      }
      return gatewayElement;
   }
   
   private String getGatewayConnectionString(Element gatewayElement) {
      String connectionStr = "";
      String protocolType = gatewayElement.getAttributeValue("protocol");
      List<Element> propertyEles = gatewayElement.getChildren();
      if ("telnet".equals(protocolType)) {
         String ipAddress = "";
         String port = "";
         for(Element ele : propertyEles){
              if("ipAddress".equals(ele.getAttributeValue("name"))) {
                 ipAddress = ele.getAttributeValue("value");
              } else if ("port".equals(ele.getAttributeValue("name"))) {
                 port = ele.getAttributeValue("value");
              }
         }
         if (!"".equals(port) && !"".equals(ipAddress)) {
            connectionStr = "ipAddress=" + ipAddress + ";port=" + port;  
         }
      }
      return connectionStr;
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
   /**
    * *********************************
    * GATEWAY ELEMENT BUILDER CODE END
    * *********************************
    */   

   // Instantiate the gateway objects and add to the gateway manager
   public void createGateway(Element gatewayElement, List<Element> commandElements) {
      Gateway gateway = null;
      int gatewayId = this.gateways.size() + 1;
      List<Command> commands = getCommands(gatewayElement, commandElements);
      gateway = new Gateway(gatewayId, gatewayElement.getAttributeValue(CONNECTION_ATTRIBUTE_NAME), gatewayElement.getAttributeValue(POLLING_ATTRIBUTE_NAME), getProtocol(gatewayElement), commands, statusCacheService);
      if (gateway != null) {
         /* Map commands to this gateway */
         for(Command command : commands) {
            addCommandMap(command.getId(), new Integer(gatewayId));
         }
      }
      this.gateways.add(gateway);
   }

   /*
    * Get the commnd map for this gateway, when controller xml supports gateways will be able to 
    * just look at the ref attribute of the commands, until then we manually deterine which commands
    * belong to this gateway, adds prcessing but allows existing XML schema to be used.
    */
   public List<Command> getCommands(Element gatewayElement, List<Element> commandElements) {
      List<Command> commands = new ArrayList<Command> ();
      String gatewayConnStr = "";
      
      if (commandElements != null && gatewayElement != null) {
         gatewayConnStr = getGatewayConnectionString(gatewayElement);
         // Cycle through command Elements and find the ones that share the same gateway protocol settings
         for (Element commandElement : commandElements)
         {
            Element tempGatewayElement = getGatewayElement(commandElement);
            if(tempGatewayElement != null) {
               if(gatewayConnStr.equals(getGatewayConnectionString(tempGatewayElement))) {
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
   
   /* Get the protocol for a particular gateway */
   private Protocol getProtocol(Element gatewayElement) {
      Protocol protocol = (Protocol)protocolFactory.getProtocol(gatewayElement);
      return protocol;
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
      killGateways();
      clearGateways();

      /* Clear status info */
      clearStatusCache();

      /* Rebuild and start gateways */
      initGatewaysWithControllerXML(null);
      startGateways();
   }
   
   /* Stop each gateway */
   public void killGateways() {
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
   public void trigger(String controlId, String controlAction) {
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
      
      if (actionValid) {
         /* Get all of the commands associated with this control */
         List<Integer> commandIds = getControlCommandIds(controlElement, controlAction);
         
         /* Execute each command through respective gateway */
         try {
            for (Integer commandId : commandIds) {
               Gateway commandGateway = getCommandGateway(commandId);
               commandGateway.doCommand(commandId, controlAction);
            }
         } catch (Exception e) {
            logger.error("Unhandled gateway exception: " + e.getMessage(), e);
         }
      }
   }
   
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
   
   
   /**
    * Returns the commands that should be triggered by the control
    * look at the <include type="command" ref="" /> elements up to two levels
    * deep below the control Element
    */
   private List<Integer> getControlCommandIds(Element controlElem, String controlAction) {
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
            commandIds.add(commandId);
         }
         if (childElem.getChildren().size() > 0) {
            List<Element> childChildElems = childElem.getChildren();
            for (Element childChildElem : childChildElems) {
               commandId = getElemCommandId(childChildElem);
               if (commandId != null) {
                  commandIds.add(commandId);
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
   
   /**
    *
    * Get Set Methods Below here
    *
    */
   
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