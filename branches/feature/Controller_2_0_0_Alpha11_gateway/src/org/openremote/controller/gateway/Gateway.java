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
import java.util.Calendar;
import org.apache.log4j.Logger;
import org.openremote.controller.service.StatusCacheService;
import org.openremote.controller.gateway.command.*;
import org.openremote.controller.spring.SpringContext;
import org.openremote.controller.exception.GatewayException;
import org.openremote.controller.exception.GatewayScriptException;
import org.openremote.controller.exception.GatewayProtocolException;
/**
 * 
 * @author Rich Turner 2011-02-09
 */
public class Gateway extends Thread
{
   // Constants ------------------------------------------------------------------------------------
 
   /* String constant for the child property list elements: ("{@value}") */
   private static final String XML_ELEMENT_PROPERTY = "property";
   
   /* String constant for the property name attribute */
   private static final String XML_ATTRIBUTENAME_NAME = "name";
   
   /* String constant for the property value attribute */
   private static final String XML_ATTRIBUTENAME_VALUE = "value";

   /* This is the time in milliseconds between status update runs */
   private static final int POLLING_INTERVAL = 1000;
   
   /* This is the time in milliseconds before a connection attempt stops */
   private static final int CONNECT_TIMEOUT = 1000;
   
   /* This is the min time in milliseconds to wait after a connection failure before trying again */
   private static final int SLEEP_INTERVAL_MIN = 5000;

   /* This is the max time in milliseconds to wait after a connection failure before trying again */
   private static final int SLEEP_INTERVAL_MAX = 60000;

   /* This is the max time in milliseconds to wait for gateway to become free when busy */
   private static final int BUSY_TIMEOUT = 1000;
                  
   /* This is the time in milliseconds before send command attempt stops */
   private static final int SEND_TIMEOUT = 200;

   /* This is the time in milliseconds before send command attempt stops */
   private static final int READ_TIMEOUT = 200;
                  
   private static final String UNKNOWN_STATUS = "N/A";
      
   // Properties ------------------------------------------------------------------------------------
   /** The logger. */
   private Logger logger = Logger.getLogger(Gateway.class);
      
   /* This is the builder for creating the required protocol object */
   private Protocol protocol;
   
   /* This determines when connection is estabished to the server */
   private EnumGatewayConnectionType connectionType;

   /* This determines the method for updating sensor values */
   private EnumGatewayPollingMethod pollingMethod;
      
   /* This keeps track of current connection state */
   private EnumGatewayConnectionState connectionState = EnumGatewayConnectionState.DISCONNECTED;
   
   private Boolean alive = true;
   
   /* The commands associated with this gateway and their IDs */
   private List<Command> commands = new ArrayList<Command>();
   
   /* Map to store sensor ID and command ID */
   private Map<Integer, Integer> pollingCommandMap = new HashMap<Integer, Integer>();
   
   /* Send command queue */
   private Map<Integer, String> sendCommands = new HashMap<Integer, String>();
   
   /* Parent status cache service reference */
   private StatusCacheService statusCacheService;
   
   /* Script manager */
   private ScriptManager scriptManager;
   
   /* Gateway ID */
   private int id;
               
   // Constructor ------------------------------------------------------------------------------------   
   public Gateway(int gatewayId, String connectionType, String pollingMethod, Protocol protocol, List<Command> commands, StatusCacheService statusCacheService) {
      // Check no null parameters have been supplied
      if (gatewayId <= 0 || protocol == null || commands == null || statusCacheService == null) {
         throw new GatewayException("At least one required parameter is null.");
      }
      
      // Set gateway id 
      this.id = gatewayId;
      
      // Set reference to status cache for storing sensor values
      this.statusCacheService = statusCacheService;

      // Set protocol object info generated from gateway element
      this.protocol = protocol;

      // Set connection type
      this.connectionType = EnumGatewayConnectionType.enumValueOf(connectionType);

      // Set polling method
      this.pollingMethod = EnumGatewayPollingMethod.enumValueOf(pollingMethod);

      // Set commands
      this.commands = commands;

      // Set script manager using spring context
      this.scriptManager = (ScriptManager)SpringContext.getInstance().getBean("scriptManager");
            
      // Validate commands
      initialiseAndValidateCommands();
                             
      // Test connection if can't connect then throw exception
      connect();
      if (this.connectionState != EnumGatewayConnectionState.CONNECTED) {
         throw new GatewayException("Gateway connection failure (connection timeout)");
      }
      disconnect();
   }
   
   // Methods ------------------------------------------------------------------------------------
   /* Make connection if permanent and start thread */
   public void startUp() {
      if (this.connectionType == EnumGatewayConnectionType.PERMANENT) {
         connect();
      }
      start();
   }
   
   /* Kill connection and do any other work before terminating */
   public void closeDown() {
      disconnect();
   }
   
   
	@Override
	public void run() {
      System.out.println(" -------- GATEWAY: Started gateway for (" +  this.protocol.getName() + ")  " + this);
   	while (alive) {
   		
   		// Execute the polling commands
         executePollingCommands();
         
         // Go to sleep until next polling interval
   		try
         {
   			Thread.sleep(POLLING_INTERVAL);
   		} catch (InterruptedException e) {
            // TODO : must be fixed to interrupt correctly
            logger.error("Gateway thread is interrupted", e);
   		}
   	}
   	
   	/* Clean up */
   	closeDown();
	}
   	
   public void kill() {
	   this.alive = false;
	}
	
   public void connect() {
      try {
         // If connection busy then wait for it to become available
      	if (this.connectionState == EnumGatewayConnectionState.BUSY) {
            waitForGatewayFree();
         }
      
         // Disconnect if still busy
         if(this.connectionState == EnumGatewayConnectionState.BUSY) {
      	   disconnect();
         }
         
         // Try and establish connection
         establishConnection();
         
      	// If still not connected then enter sleep mode
      	if (this.connectionState == EnumGatewayConnectionState.DISCONNECTED) {
            sleep();
      	}
      	
      	// Clear the input stream (may have connection info in it which we don't want
      	this.protocol.clearInputStream();
	   } catch (GatewayException e) {
   	   logger.error("Gateway connection failure: " + e.getMessage(), e);  
	   }
   }
   
   public void establishConnection() {
      Calendar endTime = Calendar.getInstance();
      endTime.add(Calendar.MILLISECOND, CONNECT_TIMEOUT);
      while (Calendar.getInstance().before(endTime) && this.connectionState != EnumGatewayConnectionState.CONNECTED) {
         switch (this.connectionState) {
            case DISCONNECTED:
               this.connectionState = this.protocol.connect();
               break;
         }
         try {
            Thread.sleep(100);
         } catch (InterruptedException e) {
            logger.error("Failed to sleep");
         }
      }      
   }

	/* Gateway sleep and reconnect loop */
   public void sleep() {
      Integer sleepPeriod = SLEEP_INTERVAL_MIN;
      Boolean initialised = false;
      while (this.connectionState == EnumGatewayConnectionState.DISCONNECTED) {
         if (initialised) {
      	   establishConnection();
   	   } else {
      	   initialised = true;  
   	   }
   		if (this.connectionState == EnumGatewayConnectionState.CONNECTED) {
   			break;
   		}
   		if (sleepPeriod < SLEEP_INTERVAL_MAX) {
   		   sleepPeriod = sleepPeriod + 1000;
   	   }
      	String sleepSeconds = Integer.toString(sleepPeriod/1000);
      	logger.error("Gateway [" +  this.protocol.getName() + "] connection lost going to sleep for " + sleepSeconds + " seconds.");
         try
         {
      		Thread.sleep(sleepPeriod);
      	} catch (InterruptedException e) {
            // TODO : must be fixed to interrupt correctly
            logger.error("Gateway thread is interrupted", e);
      	}
	   }
   }
   
   /* Waits until gateway becomes free or busy timeout exceeded */
   public void waitForGatewayFree() {
      Calendar endTime = Calendar.getInstance();
      endTime.add(Calendar.MILLISECOND, BUSY_TIMEOUT);
      while (Calendar.getInstance().before(endTime) && this.connectionState == EnumGatewayConnectionState.BUSY) {
         try
         {
      		Thread.sleep(50);
      	} catch (InterruptedException e) {
            // TODO : must be fixed to interrupt correctly
            logger.error("Gateway thread is interrupted", e);
      	}
	   }
   }
         
   public void disconnect() {
      this.protocol.disconnect();
      this.connectionState = EnumGatewayConnectionState.DISCONNECTED;
   }
   
   public void addPollingCommand(Integer commandId, Integer sensorId) {
      this.pollingCommandMap.put(commandId, sensorId);
   }
   
   public void addCommand(Command command) {
      this.commands.add(command);
   }
   
   public Command getCommand(Integer commandId) {
      Command retCommand = null;
      if (commands != null) {
         for(Command command : commands) {
            if (command.getId() == commandId) {
               retCommand = command;
            }
         }
      }
      return retCommand;
   }
      
   public void executePollingCommands() {
      String tempBuffer = "";
         
      // Process polling commands
      Set<Map.Entry<Integer, Integer>> pollingMaps = this.pollingCommandMap.entrySet();
      for (Map.Entry<Integer, Integer> pollingMap : pollingMaps)
      {
         Integer commandId = pollingMap.getKey();
         Integer sensorId = pollingMap.getValue();
         String result = UNKNOWN_STATUS;
         Command command = getCommand(commandId);
         String commandResult = "";
         
         // Skip command if invalid or null
         if (command == null || !command.isValid()) {
            continue;
         }
         
         /** QUERY polling then clear the response buffer before sending each command
          * BROADCAST polling then read in any unread server response then add it to temp
          * buffer and try and find information relating to each sensor
          */
         switch (this.pollingMethod) {
            case QUERY:
               this.protocol.clearInputStream();
               
               // Execute command
               commandResult = executeCommand(command);
               
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
   }
   
   /**
    * Cycles through each command and checks each action is valid
    * if any are not valid then mark entire command as invalid
    */
   public void initialiseAndValidateCommands() {
      int validCommandCount = 0;
      for (Command command : commands)
      {
         Integer commandId = command.getId();
         
         // If command is already invalid then skip it
         Boolean commandIsValid = command.isValid();
         if (!commandIsValid) {
            continue;  
         }
         
         List<Action> commandActions = command.getActions();
         try {
            for (Action commandAction : commandActions)
            {
               String value = commandAction.getValue();
               Map<String, String> args = commandAction.getArgs();
               switch (commandAction.getType()) {
                  case SEND:
                     // Validate command and args using protocol
                     validateSendAction(value, args);
                     break;
                  case READ:
                     validateReadAction(value, args);
                     break;
                  case SCRIPT:
                     scriptManager.addScript(value);
               }
            }
            validCommandCount++;
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
   
   /**
    * Validate a send action by checking with the protocol that the
    * args format is correct
    */
   public void validateSendAction(String value, Map<String, String> args) {
      this.protocol.validateSendAction(value, args);
   }
   
   /**
    * Validate a read action by checking with the protocol that the
    * args format is correct
    */
   public void validateReadAction(String value, Map<String, String> args) {
      // Read command has no parameters at present so ignore
   }
   
   /**
    * Execute a command and return the result string, used for polling
    * and send commands as both can contain a mixture of send, read or sript actions
    * send commands can push a dynamic value into the command as sensors, buttons and
    * other components need to push the value back to the appropriate gateway server
    */
   public String executeCommand(Integer commandId, String actionParam) {
      return executeCommand(getCommand(commandId), actionParam);  
   }
   public String executeCommand(Command command) {
      return executeCommand(command, null);   
   }   
   public String executeCommand(Command command, String actionParam) {
      List<Action> commandActions = command.getActions();
      String commandResult = "";
      if(commandActions.size() == 0) {
         return commandResult;
      } else {
         // Connect if we need to
         connect();
      }
      try {
         this.connectionState = EnumGatewayConnectionState.BUSY;
         for (Action commandAction : commandActions)
         {
            String value = commandAction.getValue();
            Map<String, String> args = commandAction.getArgs();
            switch (commandAction.getType()) {
               case SEND:
                  // Send action using the protocol first replace regex ${PARAM} with
                  // actionParam
                  if (actionParam != null) {
                     value = value.replaceAll(Command.DYNAMIC_PARAM_PLACEHOLDER_REGEXP, actionParam);
                  }
                  this.protocol.send(value, args);
                  break;
               case READ:
                  commandResult = this.protocol.read(value, args);
                  if ("".equals(commandResult)) {
                     throw new GatewayProtocolException("No response from read command.");
                  }
                  break;
               case SCRIPT:
                  //Inject actionParam as dynamicValue arg to script
                  args.put(Command.DYNAMIC_VALUE_ARG_NAME, actionParam);            
                  commandResult = scriptManager.executeScript(value, args, commandResult);
            }
         }
         // Disconnect if not permanent connection
         if(connectionType != EnumGatewayConnectionType.PERMANENT) {
            disconnect();
         } else {
            this.connectionState = EnumGatewayConnectionState.CONNECTED;
         }
      } catch (Exception e) {
         logger.error("Gateway command execution failed: " + e.getMessage(), e);
         disconnect();
      }      
      return commandResult;
   }
}