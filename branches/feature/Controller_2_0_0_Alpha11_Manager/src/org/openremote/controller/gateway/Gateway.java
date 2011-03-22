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
import org.openremote.controller.gateway.exception.GatewayException;
import org.openremote.controller.gateway.exception.GatewayScriptException;
import org.openremote.controller.gateway.exception.GatewayConnectionException;
import java.io.IOException;
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
   
   /* This is the min time in milliseconds to wait after a connection failure before trying again */
   private static final int SLEEP_INTERVAL_MIN = 5000;

   /* This is the max time in milliseconds to wait after a connection failure before trying again */
   private static final int SLEEP_INTERVAL_MAX = 60000;

   /* This is the max time in milliseconds to wait for gateway to become free when busy */
   private static final int BUSY_TIMEOUT = 1000;
   
   /* This is the max time in milliseconds to wait for gateway to become free when busy */
   private static final int CONNECT_TIMEOUT = 1000;   

   /* This is the time in milliseconds before send command attempt stops */
   private static final int READ_TIMEOUT = 200;
   
   private static final String SEND_TERMINATOR = "\n";
                  
   private static final String UNKNOWN_STATUS = "N/A";
      
   // Properties ------------------------------------------------------------------------------------
   /** The logger. */
   private Logger logger = Logger.getLogger(this.getClass().getName());
      
   /* This is the builder for creating the required protocol object */
   private Protocol protocol;
   
   /* This determines when connection is estabished to the server */
   private EnumGatewayConnectionType connectionType;

   /* This determines the method for updating sensor values */
   private EnumGatewayPollingMethod pollingMethod;
      
   /* This keeps track of current connection state */
   private EnumGatewayConnectionState connectionState = EnumGatewayConnectionState.DISCONNECTED;
   
   /* Gateway thread keep alive flag */
   private Boolean alive = true;
   
   /* Falg to indicate that gateway is valid and should be run */
   private Boolean isValid = true;
   
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
   
   /* Gateway ID */
   private int id;
   
   /* Connect timeout */
   Integer connectTimeout = CONNECT_TIMEOUT;

   /* Read timeout */
   Integer readTimeout = READ_TIMEOUT;
   
   /* Send Terminator */
   String sendTerminator = SEND_TERMINATOR;
   
   /* Polling Timer */
   Integer pollingTimer = 0;
   
   // Constructor ------------------------------------------------------------------------------------   
   public Gateway(int gatewayId, String connectionType, String pollingMethod, Protocol protocol, List<Command> commands, StatusCacheService statusCacheService) {
        this(gatewayId, connectionType, pollingMethod, protocol, commands, statusCacheService, null);
   }
   public Gateway(int gatewayId, String connectionType, String pollingMethod, Protocol protocol, List<Command> commands, StatusCacheService statusCacheService, Map<String, String> params) {
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
      initialiseCommands();
      
      // Apply supplied params
      if (params != null) {
         Set<Map.Entry<String, String>> paramMaps = params.entrySet();
         for (Map.Entry<String, String> paramMap : paramMaps)
         {
            String paramName = paramMap.getKey();
            String paramValue = paramMap.getValue();
            if ("connecttimeout".equalsIgnoreCase(paramName)) {
               try {
                  Integer num = Integer.parseInt(paramValue);
                  this.connectTimeout = num;
               } catch (NumberFormatException e) {
                  logger.error("Invalid connect timeout parameter supplied to gateway");  
               }
            } else if ("readtimeout".equalsIgnoreCase(paramName)) {
               try {
                  Integer num = Integer.parseInt(paramValue);
                  this.readTimeout = num;
               } catch (NumberFormatException e) {
                  logger.error("Invalid read timeout parameter supplied to gateway");  
               }
            } else if ("sendterminator".equalsIgnoreCase(paramName)) {
               this.sendTerminator = paramValue;
            }
         }
      }
   }
   
   // Methods ------------------------------------------------------------------------------------
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
            if (this.pollingTimer >= 86400000) {
               this.pollingTimer = 0;  
            }
            
            // Increment polling timer and go to sleep until next polling interval
            this.pollingTimer += POLLING_INTERVAL;
            
            Thread.sleep(POLLING_INTERVAL);
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
            if (this.connectionType != EnumGatewayConnectionType.PERMANENT) {
               protocolDisconnect();
            }
         } catch (GatewayConnectionException e) {
            // Have to catch this hear or so we can disconnect, thread run loop will
            // try to reconnect and enter sleep modei if necessary
            protocolDisconnect();
         }
      }
   }
   
   public void doPollingCommands() {
      String tempBuffer = "";
         
      // Process polling commands if any queeud commands appear between
      // each polling command then give them priority
      Set<Map.Entry<Integer, Integer>> pollingMaps = this.pollingCommandMap.entrySet();
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
         Integer pollingInterval = command.getPollingInterval();
         
         // If polling interval set then check it's time to run it
         if (pollingInterval > 0) {
            if (this.pollingTimer % pollingInterval != 0) {
               continue;
            }
         }
         
         /** QUERY polling then clear the response buffer before sending each command
          * BROADCAST polling then read in any unread server response then add it to temp
          * buffer and try and find information relating to each sensor
          */

         // Set gateway as busy
         this.connectionState = EnumGatewayConnectionState.BUSY;
         
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
         
         // Set gateway as available
         this.connectionState = EnumGatewayConnectionState.CONNECTED;
         
         this.statusCacheService.saveOrUpdateStatus(sensorId, result);
      }
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
               String value = commandAction.getValue();
               Map<String, String> args = commandAction.getArgs();
               switch (commandAction.getType()) {
                  case SEND:
                     // Validate command and args using protocol
                     valid = validateSendAction(value, args);
                     if (!valid) {
                        throw new GatewayException("Send action is invalid");
                     }
                     break;
                  case READ:
                     valid = validateReadAction(value, args);
                     if (!valid) {
                        throw new GatewayException("Read action is invalid");
                     }
                     break;
                  case SCRIPT:
                     valid = scriptManager.addScript(value);
                     if (!valid) {
                        throw new GatewayException("Send action is invalid");
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
   
   /**
    * Validate a send action by checking with the protocol that the
    * args format is correct
    */
   public Boolean validateSendAction(String value, Map<String, String> args) {
      Boolean result = false;
      result = this.protocol.validateSendAction(value, args);
      return result;
   }
   
   /**
    * Validate a read action by checking with the protocol that the
    * args format is correct
    */
   public Boolean validateReadAction(String value, Map<String, String> args) {
      Boolean result = true;
//       Set<Map.Entry<String, String>> paramMaps = args.entrySet();
//       for (Map.Entry<String, String> paramMap : paramMaps)
//       {
//          String paramName = paramMap.getKey();
//          String paramValue = paramMap.getValue();
//          if ("timeout".equalsIgnoreCase(paramName)) {
//             try {
//                Integer num = Integer.parseInt(paramValue);
//                this.connectTimeout = num;
//             } catch (NumberFormatException e) {
//                logger.warn("Invalid read action timeout value");
//                result = false;
//             }
//          }
//       }
      return result;
   }
   
   /* Add command to queue */
   public void queueCommand(Integer commandId, String actionParam) {
        this.queuedCommands.put(commandId, actionParam);
   }
   
   /**
    * Execute a command and return the result string, used for polling
    * and send commands as both can contain a mixture of send, read or sript actions
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
      List<Action> commandActions = command.getActions();
      
      // Exit if no actions or command is invalid
      if(!command.isValid()) {
         return commandResult;
      }
      
      try {
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
                  protocolSend(value, args);
                  break;
               case READ:
                  commandResult = protocolRead(value, args);
                  break;
               case SCRIPT:
                  try {
                     // Inject actionParam as dynamicValue arg to script
                     args.put(Command.DYNAMIC_VALUE_ARG_NAME, actionParam);
                     // Inject sensorId that command is linked to
                     args.put("sensorId", sensorId.toString());
                  } catch (NullPointerException e) {
                     // Ignore this error
                  }                     
                  // Call script manager execute script and send in status cache service
                  commandResult = scriptManager.executeScript(value, args, commandResult, this.statusCacheService);
            }
         }
      } catch (GatewayScriptException e) {
         logger.error("Gateway script error, command will be marked as invalid: " + e.getMessage(), e);
         
         // Script error so mark the command as invalid
         command.setCommandIsValid(false);
      }
      return commandResult;
   }
   
   // Proocol Communication methods -------------------------------------------------------
   /**
    * Connect to server catch all excpetions as it's impossible to know
    * what code has been used inside the protocol implementation then throw
    * a gateway connection exception
    */
   public void protocolConnect() {
      try {
         // Call protocol connect method with timeout parameter
         this.protocol.connect(this.connectTimeout);
         this.connectionState = EnumGatewayConnectionState.CONNECTED;
      } catch (Exception e) {
         throw new GatewayConnectionException("Gateway connection error: " + e.getMessage(), e);
   	}
   }
   /**
    * Dicconnect from server catch all excpetions as it's impossible to know
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
   public void protocolSend(String value, Map<String, String> args) {
      try {
         this.protocol.outputStream.write((value + this.sendTerminator).getBytes());
         this.protocol.outputStream.flush();
      } catch (IOException e) {
         throw new GatewayConnectionException("Gateway connection send error: " + e.getMessage(), e);
      }
   }

   /**
    * Read from server input stream catch IO exception, indicates a connection
    * issue so throw a gateway connection exception
    */
   public String protocolRead(String value, Map<String, String> args) {
      String readString = "";
      Calendar endTime = Calendar.getInstance();
      endTime.add(Calendar.MILLISECOND, this.readTimeout);
      try {
         while (Calendar.getInstance().before(endTime)) {
            while (this.protocol.inputStream.available() != 0) {
               readString += (char) this.protocol.inputStream.read();
            }
            // If data recieved then assume this is what we're waiting for
            if (readString.length() > 0) {
               break;
            }
            try {
               Thread.sleep(50);
            } catch (Exception e) {}
         }
      } catch (IOException e) {
         throw new GatewayConnectionException("Gateway connection read error: " + e.getMessage(), e);
      }
      return readString;
   }
   
   /**
    * Read out server input stream to clear it, catch IO exception, indicates a connection
    * issue so throw a gateway connection exception
    */
   public void protocolClear() {
      // Just read inputStream content but don't store it
      try {
         while (this.protocol.inputStream.available() != 0) {
            this.protocol.inputStream.read();
         }
      } catch (IOException e) {
         throw new GatewayConnectionException("Gateway connection read error: " + e.getMessage(), e);
      }
   }
}