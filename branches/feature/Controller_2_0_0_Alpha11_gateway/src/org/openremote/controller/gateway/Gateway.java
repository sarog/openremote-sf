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
/**
 * 
 * @author Rich Turner 2011-02-09
 */
public class Gateway extends Thread
{
   // Constants ------------------------------------------------------------------------------------

  /* String constant for the top level gateway element connection type attribute: ("{@value}") */
  public final static String CONNECTION_ATTRIBUTE_NAME = "connectionType";
    
  /* String constant for the top level gateway element polling method attribute: ("{@value}") */
  public final static String POLLING_ATTRIBUTE_NAME = "pollingMethod";
  
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
   
   /* This is the time in milliseconds to wait after a connection failure before trying again */
   private static final int SLEEP_INTERVAL = 30000;
      
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
   
   private boolean connectionFailure = false;
   
   /* The commands associated with this gateway and their IDs */
   private Map<Integer, Command> commands = new HashMap<Integer, Command>();
   
   /* Map to store sensor ID and command ID */
   private Map<Integer, Integer> pollingCommandMap = new HashMap<Integer, Integer>();
   
   /* Send command queue */
   private List<Integer> sendCommands = new ArrayList<Integer>();   
   
   /* Parent status cache service reference */
   private StatusCacheService statusCacheService;
   
   /* Script manager */
   private ScriptManager scriptManager;
   
   /* Gateway ID */
   private int id;
               
   // Constructor ------------------------------------------------------------------------------------   
   public Gateway(int gatewayId, Element gatewayElement, Protocol protocol, Map<Integer, Command> commands, StatusCacheService statusCacheService) {
      // Set gateway id 
      this.id = gatewayId;
      
      // Set reference to status cache for storing sensor values
      this.statusCacheService = statusCacheService;

      // Set protocol object info generated from gateway element
      this.protocol = protocol;

      // Set connection type
      String connectionType = gatewayElement.getAttributeValue(CONNECTION_ATTRIBUTE_NAME);      
      this.connectionType = EnumGatewayConnectionType.enumValueOf(connectionType);

      // Set polling method
      String pollingMethod = gatewayElement.getAttributeValue(POLLING_ATTRIBUTE_NAME);      
      this.pollingMethod = EnumGatewayPollingMethod.enumValueOf(pollingMethod);

      // Set commands
      this.commands = commands;

      // Set script manager using spring contex
      this.scriptManager = (ScriptManager)SpringContext.getInstance().getBean("scriptManager");
            
      // Validate commands
      initialiseAndValidateCommands();
                             
      // Test connection
      this.attemptConnect();
      if (this.connectionState != EnumGatewayConnectionState.CONNECTED) {
         try {
            throw new Exception("Gateway connection Failure");
         }
         catch (Exception e) {
              
         }
      } else {
         this.disconnect();
      }
   }
   
   // Methods ------------------------------------------------------------------------------------
	@Override
	public void run() {
      System.out.println(" -------- GATEWAY: Started gateway for (" +  this.protocol.getName() + ")  " + this);
      
   	while (alive) {
   		attemptConnect();
   		while (this.connectionFailure) {
      		attemptConnect();
       		if (this.connectionState == EnumGatewayConnectionState.CONNECTED) {
       			break;
       		}
         	String sleepSeconds = Integer.toString(SLEEP_INTERVAL/1000);
            System.out.println(" -------- GATEWAY: Connection failure gateway going to sleep for " + sleepSeconds + " seconds (" +  this.protocol.getName() + ")  " + this);
            try
            {
      			Thread.sleep(SLEEP_INTERVAL);
      		} catch (InterruptedException e) {
               // TODO : must be fixed to interrupt correctly
               logger.error("Gateway thread is interrupted", e);
      		}
   		}
         this.sendAndReadCommands();
   		try
         {
   			Thread.sleep(POLLING_INTERVAL);
   		} catch (InterruptedException e) {
            // TODO : must be fixed to interrupt correctly
            logger.error("Gateway thread is interrupted", e);
   		}
   	}
	}
	
   public void kill() {
	   this.alive = false;
	}
	
   public void attemptConnect() {
      Calendar endTime = Calendar.getInstance();
      endTime.add(Calendar.MILLISECOND, CONNECT_TIMEOUT);
      while (Calendar.getInstance().before(endTime) && this.connectionState != EnumGatewayConnectionState.CONNECTED) {
         switch (this.connectionState) {
            case DISCONNECTED:
            case ERROR:
               this.connectionState = this.protocol.connect();
               break;
            case CONNECTED:
               this.connectionState = this.protocol.getConnectionState();
         }
         try {
            Thread.sleep(100);
         } catch (InterruptedException e) {
            logger.error("Failed to sleep");
         }
      }
      if (this.connectionState != EnumGatewayConnectionState.CONNECTED) {
         this.connectionFailure = true;
         logger.info("Gateway connection error. Connection state is " + this.connectionState.toString());
      } else {
         this.connectionFailure = false;
      }
   }
   
   public void disconnect() {
      this.protocol.disconnect();
      this.connectionState = EnumGatewayConnectionState.DISCONNECTED;
   }
   
   public void addPollingCommand(Integer commandId, Integer sensorId) {
      this.pollingCommandMap.put(commandId, sensorId);
   }
   
   public void addCommand(Integer commandId, Command command) {
      this.commands.put(commandId, command);
   }
   
   public Command getCommand(Integer commandId) {
      return this.commands.get(commandId);
   }
      
   private void updateConnectionState() {
      this.connectionState = this.protocol.getConnectionState();           
   }
      
   public void sendAndReadCommands() {
      String tempBuffer = "";
      
      // Update connection state before we begin
      updateConnectionState();
      
      // Ensure we're connected
      if (this.connectionState != EnumGatewayConnectionState.CONNECTED) {
         attemptConnect();
      }
      
      // If not connected then abort
      if (this.connectionState != EnumGatewayConnectionState.CONNECTED) {
         sendCommands.clear();
         logger.info("Gateway Error: connection failure during send and read loop");
         return;
      }
      
      // If BROADCAST Polling then capture response buffer before we begin
      if (this.pollingMethod == EnumGatewayPollingMethod.BROADCAST) {
         tempBuffer += this.protocol.read(null, null);
      }
         
      // Send all queued commands first
      for (Integer commandId : sendCommands)
      {
         Command command = getCommand(commandId);
         
         if (command != null) {
            executeCommand(command);
         }   
            
         // Check connection state
         updateConnectionState();
      }
         
      // If BROADCAST Polling then check for updates to response buffer
      if (this.pollingMethod == EnumGatewayPollingMethod.BROADCAST) {
         tempBuffer += this.protocol.read(null, null);
      }
         
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
         
         // Check connection state
         updateConnectionState();
         
         /** QUERY polling then clear the response buffer before sending each command
          * BROADCAST polling then read in any unread server response then add it to temp
          * buffer and try and find information relating to each sensor
          */
         switch (this.pollingMethod) {
            case QUERY:
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
                  
      // Disconnect if not permanent connection
      if(connectionType != EnumGatewayConnectionType.PERMANENT) {
         this.disconnect();
      }
   }
   
   /**
    * Cycles through each command and checks each action is valid
    * if any are not valid then mark entire command as invalid
    */
   public void initialiseAndValidateCommands() {
      Set<Map.Entry<Integer, Command>> commandMaps = this.commands.entrySet();
      for (Map.Entry<Integer, Command> commandMap : commandMaps)
      {
         Integer commandId = commandMap.getKey();
         Command command = commandMap.getValue();
         
         // If command is already invalid then skip it
         Boolean commandIsValid = command.isValid();
         if (!commandIsValid) {
            continue;  
         }
         
         List<Action> commandActions = command.getActions();
         
         for (Action commandAction : commandActions)
         {
            Boolean actionIsValid = true;
            String value = commandAction.getValue();
            Map<String, String> args = commandAction.getArgs();
            switch (commandAction.getType()) {
               case SEND:
                  // Validate command and args using protocol
                  actionIsValid = validateSendAction(value, args);
                  break;
               case READ:
                  actionIsValid = validateReadAction(value, args);
                  break;
               case SCRIPT:
                  actionIsValid = scriptManager.addScript(value);
            }
            if (!actionIsValid) {
               command.setCommandIsValid(false);
               break;
            }
         }
      }
   }
   
   /**
    * Validate a send action by checking with the protocol that the
    * args format is correct
    */
   public Boolean validateSendAction(String value, Map<String, String> args) {
      return this.protocol.validateSendAction(value, args);
   }
   
   /**
    * Validate a read action by checking with the protocol that the
    * args format is correct
    */
   public Boolean validateReadAction(String value, Map<String, String> args) {
      // Read command has no parameters at present so ignore
      return true;
   }
   
   /**
    * Execute a command and return the result string, used for polling
    * and send commands as both can contain a mixture of send, read or sript actions
    */
   public String executeCommand(Command command) {
      List<Action> commandActions = command.getActions();
      Boolean commandSuccess = true;
      String result = "";
      String commandResult = "";
      for (Action commandAction : commandActions)
      {
         Boolean actionSuccess = true;
         String value = commandAction.getValue();
         Map<String, String> args = commandAction.getArgs();
         switch (commandAction.getType()) {
            case SEND:
               // Send action using the protocol
               EnumProtocolIOResult sendResult = this.protocol.send(value, args);
               if (sendResult != EnumProtocolIOResult.SUCCESS) {
                  logger.error("Gateway Error: Send command failed: '" + value + "'");
                  actionSuccess = false;
               }
               break;
            case READ:
               commandResult = this.protocol.read(value, args);
               if ("".equals(commandResult)) {
                  actionSuccess = false;  
               }                     
               break;
            case SCRIPT:
               commandResult = scriptManager.executeScript(value, args, commandResult);
               if ("".equals(commandResult)) {
                  actionSuccess = false;  
               }
         }
         // If Action failed then abort command
         if (!actionSuccess) {
            commandSuccess = false;
            break;
         }
      }
      if (commandSuccess && !"".equals(commandResult)) {
           result = commandResult;
      }
      return result;
   }
}