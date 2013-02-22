/*
 * OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.gateway;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import org.jdom.Element;
import org.apache.log4j.Logger;
import org.openremote.controller.service.StatusCacheService;
import org.openremote.controller.gateway.command.*;
import org.openremote.controller.spring.SpringContext;
import org.openremote.controller.gateway.exception.GatewayException;
import org.openremote.controller.gateway.exception.GatewayScriptException;
import org.openremote.controller.gateway.exception.GatewayConnectionException;
import org.openremote.controller.gateway.protocol.ProtocolFactory;
import org.openremote.controller.gateway.protocol.Protocol;

/**
 * 
 * @author Rich Turner 2011-02-09
 */
public class Gateway extends Thread
{
   // Constants ------------------------------------------------------------------------------------
 
   /* String constant for the top level gateway element id attribute: ("{@value}") */
   public static final String ID_ATTRIBUTE_NAME = "id";
   
   /* String constant for the top level gateway element connection type attribute: ("{@value}") */
   public static final String CONNECTION_ATTRIBUTE_NAME = "connectionType";
    
   /* String constant for the top level gateway element polling method attribute: ("{@value}") */
   public static final String POLLING_ATTRIBUTE_NAME = "pollingMethod";
   
   /* String constant for the child property list elements: ("{@value}") */
   private static final String XML_ELEMENT_PROPERTY = "property";
   
   /* String constant for the property name attribute */
   private static final String XML_ATTRIBUTENAME_NAME = "name";
   
   /* String constant for the property value attribute */
   private static final String XML_ATTRIBUTENAME_VALUE = "value";

   /* This is the time in milliseconds between status update runs */
   private static final int DEFAULT_POLLING_INTERVAL = 1000;
   
   /* This is the max time in milliseconds between status update runs */
   private static final int MAX_POLLING_INTERVAL = 86400000;
   
   /* This is the min time in milliseconds to wait after a connection failure before trying again */
   private static final int SLEEP_INTERVAL_MIN = 5000;

   /* This is the max time in milliseconds to wait after a connection failure before trying again */
   private static final int SLEEP_INTERVAL_MAX = 60000;
   
   /* This is the time in milliseconds before connection attempt stops */
   public static final int CONNECT_TIMEOUT = 1000;   

   /* This is the time in milliseconds before read command attempt stops */
   public static final int READ_TIMEOUT = 200;
                  
   private static final String UNKNOWN_STATUS = "N/A";
      
   // Properties ------------------------------------------------------------------------------------
   /** The logger. */
   private Logger logger = Logger.getLogger(this.getClass().getName());
      
   /* This is the builder for creating the required protocol object */
   private Protocol protocol;
   
   /* This determines when connection is established to the server */
   private EnumGatewayConnectionType connectionType;

   /* This determines the method for updating sensor values */
   private EnumGatewayPollingMethod pollingMethod;
      
   /* This keeps track of current connection state */
   private EnumGatewayConnectionState connectionState = EnumGatewayConnectionState.DISCONNECTED;
   
   /* Gateway thread keep alive flag */
   private Boolean alive = true;
   
   /* The commands associated with this gateway and their IDs */
   private List<Command> commands = new ArrayList<Command>();
   
   /* Map to store sensor ID and command ID */
   private Map<Integer, Integer> pollingCommandMap = new HashMap<Integer, Integer>();
   
   /* Send command queue */
   private Map<Integer, String> queuedCommands = new HashMap<Integer, String>();
   
   /* Parent status cache service reference */
   protected StatusCacheService statusCacheService;
   
   /* Script manager */
   private ScriptManager scriptManager;
   
   /* This is the protocol factory for returning the protocol builder */
   private ProtocolFactory protocolFactory;
   
   /* Gateway ID */
   private int id;
   
   /* Polling interval */
   int pollingInterval = DEFAULT_POLLING_INTERVAL;
   
   /* Polling Timer */
   int pollingTimer = 0;
   
   // Constructor ------------------------------------------------------------------------------------   
   public Gateway(Element gatewayElement, List<Command> commands, StatusCacheService statusCacheService) {

      int gatewayId = Integer.parseInt(gatewayElement.getAttributeValue(ID_ATTRIBUTE_NAME));
      String connectionType = gatewayElement.getAttributeValue(CONNECTION_ATTRIBUTE_NAME);
      String pollingMethod = gatewayElement.getAttributeValue(POLLING_ATTRIBUTE_NAME);
      
      protocolFactory = (ProtocolFactory)SpringContext.getInstance().getBean("protocolFactory");
      
      List<Element> propertyEles = gatewayElement.getChildren("property", gatewayElement.getNamespace());
      
      // Set gateway id 
      this.id = gatewayId;
      
      // Set connection type
      this.connectionType = EnumGatewayConnectionType.enumValueOf(connectionType);
      
      // Set polling method
      this.pollingMethod = EnumGatewayPollingMethod.enumValueOf(pollingMethod);
      
      // Set reference to status cache for storing sensor values
      this.statusCacheService = statusCacheService;
      
      // Set protocol object info generated from gateway element
      try {
    	  this.protocol = getProtocol(gatewayElement);
      } catch (Exception e) {
    	  throw new GatewayException("Cannot create gateway, invalid protocol settings. " + e.getMessage(), e);
      }
      
      // Set commands
      this.commands = commands;
      
      // Set script manager using spring context
      this.scriptManager = (ScriptManager)SpringContext.getInstance().getBean("scriptManager");
      
      // Check no null parameters have been supplied
      if (gatewayId <= 0 || "".equals(connectionType) || "".equals(pollingMethod) || protocol == null || commands == null || statusCacheService == null) {
         throw new GatewayException("Cannot create gateway, at least one required parameter is null.");
      }
      
      /**
       * Validate connection settings by sending them to the protocol
       * for validation, the protocol will dictate what's allowed here
       * allow protocol to set these values if only one option supported
       */
      this.connectionType = this.protocol.checkSetConnectionType(this.connectionType);
      this.pollingMethod = this.protocol.checkSetPollingMethod(this.pollingMethod);
      
      // Check connection settings have been identified
      if (this.connectionType == null || this.pollingMethod == null) {
         throw new GatewayException("Cannot create gateway, invalid connection type and/or polling method.");
      }
                
      // Validate commands
      initialiseCommands();
      
      // Apply supplied params
      for (Element ele : propertyEles) {

         String paramName = ele.getAttributeValue("name");
         String paramValue = ele.getAttributeValue("value");

         if ("defaultpollinginterval".equalsIgnoreCase(paramName)) {
            try {
               int num = Integer.parseInt(paramValue);
               if ((num*1000) > DEFAULT_POLLING_INTERVAL && (num*1000) <= MAX_POLLING_INTERVAL) {
                  this.pollingInterval = (num*1000);
               }
            } catch (NumberFormatException e) {
               logger.error("Invalid default polling interval parameter supplied to gateway");  
            }
         }
      }
   }
   
   // Methods ------------------------------------------------------------------------------------
   /* Get the protocol for a particular gateway */
   private Protocol getProtocol(Element gatewayElement) throws GatewayException {
      Protocol protocol = (Protocol)protocolFactory.getProtocol(gatewayElement);
      return protocol;
   }
   
   public int getGatewayId() {
      return this.id;
   }
      
   /* Do startup prep and start thread */
   public void startUp() {
      start();
   }
   
   /* Kill connection and do any other work before terminating */
   public void closeDown() {
      kill();
      protocolDisconnect();
   }

	@Override
	public void run() {
      System.out.println(" -------- GATEWAY: Started gateway for (" +  this.protocol.getName() + ")  " + this);
   	while (alive) {
      	/**
      	 * Check connection and establish if disconnected, upon connection
      	 * failure enter sleep mode
      	 */
         try {
            if (this.connectionState == EnumGatewayConnectionState.DISCONNECTED) {
               protocolConnect();
            }
            
            // Execute the polling commands
            doPollingCommands();
            
            // Disconnect unless connection type is permanent
            if (this.connectionType != EnumGatewayConnectionType.PERMANENT) {
               protocolDisconnect();
            }
            
            // Reset timer if 24 hours have passed
            if (this.pollingTimer >= MAX_POLLING_INTERVAL) {
               this.pollingTimer = 0;  
            }
            
            // Increment polling timer and go to sleep until next polling interval
            this.pollingTimer += DEFAULT_POLLING_INTERVAL;
            
            Thread.sleep(DEFAULT_POLLING_INTERVAL);
         } catch (GatewayConnectionException e) {
            //Connection has failed
            System.out.println("Gateway connection failure for (" +  this.protocol.getName() + ") gateway will enter sleep mode and periodically try and establish connection.");
            logger.warn("Gateway connection failure: " + e.getMessage(), e);
            
            // Clear queued commands
            this.queuedCommands.clear();
            
            // Disconnect
            protocolDisconnect();
            
            // Enter sleep mode
            sleep();
         } catch (InterruptedException e) {
               // Thread has been interrupted so close it down
               closeDown();
               logger.warn("Gateway thread is interrupted", e);
         } catch (Exception e) {
            logger.error("Unhandled exception in gateway thread: " + this + " " + e.getMessage());
         }
   	}
   	System.out.println(" -------- GATEWAY: Stopped gateway for (" +  this.protocol.getName() + ")  " + this);
	}
   	
   public void kill() {
	   this.alive = false;
	}
   
	/* Gateway sleep and reconnect loop */
   public void sleep() {
      Integer sleepPeriod = SLEEP_INTERVAL_MIN;
      Boolean initialised = false;
      while (this.connectionState == EnumGatewayConnectionState.DISCONNECTED) {
         try {
            if (initialised) {
         	   reconnect();
      	   } else {
         	   initialised = true;  
      	   }
      		if (this.connectionState == EnumGatewayConnectionState.CONNECTED) {
      			break;
      		}
         	String sleepSeconds = Integer.toString(sleepPeriod/1000);
         	
         	// Inform user that connection has failed; exception will catch it also and add to log file
         	System.out.println("Cannot establish Gateway connection [" +  this.protocol.getName() + "] so going to sleep for " + sleepSeconds + " seconds.");
         	
         	Thread.sleep(sleepPeriod);
         	
         	if (sleepPeriod < SLEEP_INTERVAL_MAX) {
   		      sleepPeriod = sleepPeriod + 5000;
   	      }
      	} catch (InterruptedException e) {
            // TODO : must be fixed to interrupt correctly
            logger.error("Gateway thread is interrupted", e);
      	}
	   }
	   System.out.println("Gateway connection [" +  this.protocol.getName() + "] re-established.");
   }
   
   /* Reconnect to be used by sleep loop to catch connection exception */
   public void reconnect() {
      try {
         protocolConnect();
      } catch (GatewayConnectionException e) {
         // Ignore as this just means the server is still unreachable
      }
   }
   
   public void addPollingCommand(Integer commandId, Integer sensorId) {
      this.pollingCommandMap.put(commandId, sensorId);
   }
   
   public void addCommand(Command command) {
      this.commands.add(command);
   }
   
   public Command getCommand(Integer commandId) {
      Command retCommand = null;
      if (this.commands != null) {
         for(Command command : this.commands) {
            if (command.getId().equals(commandId)) {
               retCommand = command;
               break;
            }
         }
      }
      return retCommand;
   }

   /**
    * Allows commands to be called from outside of gateway
    * if gateway is busy then command is queued
    */
   public void doCommand(Integer commandId, String actionParam) {
      Command command = getCommand(commandId);
      // Ignore command if invalid or null
      if (command == null || !command.isValid()) {
         return;
      }      
      
      // If gateway busy add to queue for processing after polling
      if (this.connectionState == EnumGatewayConnectionState.BUSY) {
         queueCommand(commandId, actionParam);
      } else {
         try {
            if (this.connectionState == EnumGatewayConnectionState.DISCONNECTED) {
               protocolConnect();
            }
            // Set gateway as busy
            this.connectionState = EnumGatewayConnectionState.BUSY;
            // Execute the command
            executeCommand(command, actionParam);
            // Set gateway as available
            this.connectionState = EnumGatewayConnectionState.CONNECTED;
            
            // Disconnect unless connection type is permanent
            if (this.connectionType != EnumGatewayConnectionType.PERMANENT && this.connectionType != EnumGatewayConnectionType.TIMED) {
               protocolDisconnect();
            }
         } catch (GatewayConnectionException e) {
            // Have to catch this hear or so we can disconnect, thread run loop will
            // try to reconnect and enter sleep mode if necessary
         	logger.error("Gateway failed to do command(" + commandId.toString() + "): " + e.getMessage(), e);
            protocolDisconnect();
         }
      }
   }
   
   public void doPollingCommands() {         
      // Process polling commands if any queued commands appear between
      // each polling command then give them priority
      Set<Map.Entry<Integer, Integer>> pollingMaps = this.pollingCommandMap.entrySet();
      
      // Set gateway as busy
      this.connectionState = EnumGatewayConnectionState.BUSY;
      
      for (Map.Entry<Integer, Integer> pollingMap : pollingMaps)
      {
         /* Clear any queued commands before continuing */
         doQueuedCommands();
                  
         Integer commandId = pollingMap.getKey();
         Integer sensorId = pollingMap.getValue();
         Command command = getCommand(commandId);
         String result = UNKNOWN_STATUS;         

         String commandResult = "";
         
         // Skip command if invalid or null
         if (command == null || !command.isValid()) {
            continue;
         }
         int pollingInterval = command.getPollingInterval();
         
         // If polling interval not set then use default
         if (pollingInterval == 0) {
            pollingInterval = this.pollingInterval;  
         }
            
         // Only run command if it time to
         if (this.pollingTimer % pollingInterval != 0) {
            continue;
         }
         
         /** QUERY polling then clear the response buffer before sending each command
          * BROADCAST polling then read in any unread server response then add it to temp
          * buffer and try and find information relating to each sensor
          */
         
         switch (this.pollingMethod) {
            case QUERY:
               // Clear protocol input stream
               protocolClear();
               
               // Execute command
               commandResult = executeCommand(command, sensorId);
               
               if (!"".equals(commandResult)) {
                  result = commandResult;
               }
               break;
            case BROADCAST:
               // Check temp buffer for sensor info
               // TO DO: HOW TO DEFINE BRAODCAST MESSAGE TO LOOK FOR
               //tempBuffer += this.protocol.read(command.getValue(), command.getArgs());
         }
         
         this.statusCacheService.saveOrUpdateStatus(sensorId, result);
      }
      
      // Set gateway as available
      this.connectionState = EnumGatewayConnectionState.CONNECTED;
   }
   
   /* Execute any queued commands */
   public void doQueuedCommands() {
      // Do queued commands
      Set<Map.Entry<Integer, String>> queuedMaps = this.queuedCommands.entrySet();
      for (Map.Entry<Integer, String> queuedMap : queuedMaps)
      {
         Integer commandId = queuedMap.getKey();
         String actionParam = queuedMap.getValue();
         
         // Set gateway as busy
         this.connectionState = EnumGatewayConnectionState.BUSY;
         
         Command command = getCommand(commandId);
         executeCommand(command, actionParam);

         // Set gateway as available
         this.connectionState = EnumGatewayConnectionState.CONNECTED;
      }
      this.queuedCommands.clear();
   }
   
   /**
    * Cycles through each command and checks each action is valid
    * if any are not valid then marks entire command as invalid
    */
   public void initialiseCommands() {
      int validCommandCount = 0;
      for (Command command : commands)
      {         
         // If command is already invalid then skip it
         Boolean commandIsValid = command.isValid();
         if (!commandIsValid) {
            continue;  
         }
         
         List<Action> commandActions = command.getActions();
         try {
            Boolean valid = true;
            for (Action commandAction : commandActions)
            {
               switch (commandAction.getType()) {
                  case SCRIPT:
                     Map<String,String> args = commandAction.getArgs();
                     if (args.containsKey("scriptname")) {
                        valid = scriptManager.addScript(args.get("scriptname"));
                     } else {
                        valid = false;
                     }
                     if (!valid) {
                        throw new GatewayException("Script action is invalid");
                     }
                     break;
                  default:
                     // Validate action using protocol
                     valid = this.protocol.isValidAction(commandAction);
                     if (!valid) {
                        throw new GatewayException("Command action is invalid");
                     }               
               }
            }
            if (valid) {
               validCommandCount++;
            }
         } catch (GatewayException e) {
               logger.error("Invalid gateway command: " + e.getMessage(), e);
               command.setCommandIsValid(false);
         }
      }
      
      // Check that there is at least one valid command otherwise throw exception
      if (validCommandCount == 0) {
         throw new GatewayException("No valid gateway commands");
      }
   }
   
   /* Add command to queue */
   public void queueCommand(Integer commandId, String actionParam) {
        this.queuedCommands.put(commandId, actionParam);
   }
   
   /**
    * Execute a command and return the result string, used for polling
    * and send commands as both can contain a mixture of send, read or script actions
    * send commands can push a dynamic value into the command as sensors, buttons and
    * other components need to push the value back to the appropriate gateway server
    */

   public String executeCommand(Command command) {
      return executeCommand(command, null, null);   
   }
   public String executeCommand(Command command, Integer sensorId) {
      return executeCommand(command, null, sensorId);
   }
   public String executeCommand(Command command, String actionParam) {
      return executeCommand(command, actionParam, null);
   }
   public String executeCommand(Command command, String actionParam, Integer sensorId) {
      String commandResult = "";
      EnumCommandActionType lastActionType = null;
      List<Action> commandActions = command.getActions();
      
      // Exit if no actions or command is invalid
      if(!command.isValid()) {
         return commandResult;
      }
      
      try {
         for (Action commandAction : commandActions)
         {
            Map<String, String> args = commandAction.getArgs();
            lastActionType = commandAction.getType();
            Action tempAction = null;
            
            switch (lastActionType) {
               case SCRIPT:
                  try {
                     // Inject actionParam as dynamicValue arg to script
                     args.put(Command.DYNAMIC_VALUE_ARG_NAME, actionParam);
                     // Inject sensorId that command is linked to
                     if (sensorId != null) {
                        args.put("sensorId", sensorId.toString());
                     }
                     tempAction = new Action(lastActionType, args);
                  } catch (NullPointerException e) {
                     // Ignore this error
                  }                     
                  // Call script manager execute script and send in status cache service
                  commandResult = scriptManager.executeScript(tempAction, commandResult, this.statusCacheService);
                  break;
               default:
                  // Send action using the protocol first replace regex ${PARAM} with
                  // actionParam
                  if (actionParam != null) {
                     args.put("command", args.get("command").replaceAll(Command.DYNAMIC_PARAM_PLACEHOLDER_REGEXP, actionParam));
                  }
                  tempAction = new Action(lastActionType, args);
                  commandResult = protocolDoAction(tempAction);
                  break;
            }
         }
         
         // If command execution is for a sensor update then ensure a read action is carried out
         if ("".equals(commandResult) && lastActionType != EnumCommandActionType.READ && sensorId != null) {
            Action tempAction = new Action(EnumCommandActionType.READ, null);
            commandResult = protocolDoAction(tempAction);
         }
      } catch (GatewayScriptException e) {
         logger.error("Gateway script error, command will be marked as invalid: " + e.getMessage(), e);
         
         // Script error so mark the command as invalid
         command.setCommandIsValid(false);
      }
      return commandResult;
   }
   
   // Protocol Communication methods -------------------------------------------------------
   /**
    * Connect to server catch all exceptions as it's impossible to know
    * what code has been used inside the protocol implementation then throw
    * a gateway connection exception
    */
   public void protocolConnect() {
      try {
         // Call protocol connect method with timeout parameter
         this.protocol.connect();
         this.connectionState = EnumGatewayConnectionState.CONNECTED;
      } catch (Exception e) {
         throw new GatewayConnectionException("Gateway connection error: " + e.getMessage(), e);
   	}
   }
   /**
    * Disconnect from server catch all exceptions as it's impossible to know
    * what code has been used inside the protocol implementation then throw
    * a gateway connection exception
    */
   public void protocolDisconnect() {
      try {
         this.protocol.disconnect();
         this.connectionState = EnumGatewayConnectionState.DISCONNECTED;
      } catch (Exception e) {
           // Don't react to disconnect exception chances are connection is already dead
      }
   }

   /**
    * Write to server output stream catch IO exception, indicates a connection
    * issue so throw a gateway connection exception
    */
   public String protocolDoAction(Action action) {
      String response = "";
      try {
         response = this.protocol.doAction(action);
      } catch (Exception e) {
         throw new GatewayConnectionException("Gateway send/receive error: " + e.getMessage(), e);
      }
      return response;
   }
   
   /**
    * Read out server input stream to clear it, catch IO exception, indicates a connection
    * issue so throw a gateway connection exception
    */
   public void protocolClear() {
      // Just read inputStream content but don't store it
      try {
         this.protocol.clearBuffer();
      } catch (Exception e) {
         throw new GatewayConnectionException("Gateway connection read error: " + e.getMessage(), e);
      }
   }
}
