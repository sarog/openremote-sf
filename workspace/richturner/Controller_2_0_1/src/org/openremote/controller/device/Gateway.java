/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.controller.device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.openremote.controller.device.protocol.ActiveProtocol;
import org.openremote.controller.device.protocol.EnumProtocolStatus;
import org.openremote.controller.device.protocol.Payload;
import org.openremote.controller.device.protocol.Protocol;
import org.openremote.controller.device.protocol.ProtocolParameters;
import org.openremote.controller.model.Command;
import org.openremote.controller.model.sensor.Sensor;
/**
 * The Gateway is responsible for bridging between the controller and the
 * protocol. All command requests are initiated through the gateway not
 * directly to the protocol. In this way the protocol has no dependency on
 * the controller. The gateway should live in it's own thread and communication
 * is carried out via the gateway manager, the gateway thread is responsible
 * for managing a separate protocol thread.
 * 
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 *
 */
public class Gateway {
   /** 
    * Sleep duration in milliseconds of listener loop this
    * comes into play with active protocols whose read method
    * does not block
    */
   public static final int LISTENER_SLEEP_DURATION = 1000;
   
   public static final int DEFAULT_POLLING_INTERVAL = 2000;
   
   private Protocol protocol; 
   private ProtocolParameters protocolParameters;
   private HashMap<Integer, Command> commands = new HashMap<Integer, Command>();
   private HashMap<Integer, Sensor> sensors = new HashMap<Integer, Sensor>();
   private HashMap<Integer, HashSet<Integer>> pollingCommandMap = new HashMap<Integer, HashSet<Integer>>();
   private ArrayList<CommandSendRequest> commandQueue = new ArrayList<CommandSendRequest>(); 
   private Thread gatewayThread;
   private Thread protocolThread;
   private boolean isAlive;
   private int pollingInterval = DEFAULT_POLLING_INTERVAL;
         
   protected Gateway(Protocol protocol, ProtocolParameters protocolParameters)
   {
      this.protocol = protocol;
      this.protocolParameters = protocolParameters;
      if (protocol != null) protocol.setParameters(protocolParameters);
   }
   
   // Methods Called by Gateway Manager -------------------------------------------
   
   protected void addCommand(Command command)
   {
      commands.put(command.getId(), command);
   }
   
   protected void addSensor(Sensor sensor)
   {
      // Add sensor to sensor map
      sensors.put(sensor.getSensorID(), sensor);
      
      // Add command of sensor to polling command map, a
      // command may be associated with multiple sensors
      // that's why an Integer set is used in the polling map
      // to support the one to many relationship
      int sensorCommandId = sensor.getEventProducerID();
      
      if (!pollingCommandMap.containsKey(sensorCommandId))
      {
         HashSet<Integer> sensorIDs = new HashSet<Integer>();
         sensorIDs.add(sensor.getSensorID());
         pollingCommandMap.put(sensorCommandId, sensorIDs);
      }
      else
      {
         pollingCommandMap.get(sensorCommandId).add(sensor.getSensorID());
      }      
   }
   
   
   /**
    * Send a command using this gateway
    * @param commandId
    */
   protected void Send(CommandSendRequest sendRequest)
   {
      Send(new CommandSendRequest[] {sendRequest});
   }
   
   
   /**
    * Send multiple commands using this gateway
    * @param commandIds
    */
   protected synchronized void Send(CommandSendRequest[] sendRequests)
   {
      /*
       * Add each request to the command queue and let the
       * gateway thread handle them
       */
      for(CommandSendRequest sendRequest : sendRequests)
      {
            addRemoveCommandSendRequest(sendRequest);
      }
      
      // Notify gateway thread
      gatewayThread.notify();
   }
   
   
   /**
    * Called to start the gateway - it will poll the sensor commands
    * it owns as well as be ready to receive incoming command requests
    */
   protected void start()
   {
      if (protocol != null && commands.size() != 0)
      {
         isAlive = true;
         
         // Start the gateway thread
         gatewayThread = new Thread() {
            public void run() {
               gatewayRun();
            }
         };
         gatewayThread.start();
      }
   }
   
   protected void stop()
   {
      isAlive = false;
   }
   
   // Gateway Thread Methods ------------------------------------------------------------
   
   /*
    * This is the work loop of the gateway thread it's purpose is to check for incoming
    * command send requests and to periodically poll sensor commands to check for changes
    */
   private synchronized void gatewayRun()
   {
      // Start the protocol thread
      protocolThread = new Thread() {
         public void run() {
            protocolRun();
         }
      };
      protocolThread.start();

      while(isAlive)
      {
         try {
            // Check if we have work to do
            if (commandQueue.size() > 0) {
               processSendRequests();
            } else {
               // TODO: Improve polling initiation logic
               // Assume we've been woken to poll sensor commands
            }
            
            gatewayThread.wait(pollingInterval);
         } catch (InterruptedException e)
         {
            // Ignore any interrupts
         }
      }
   }
   
   /**
    * This is called by the gateway thread when it has command send
    * requests to fulfil; each send request needs some standard processing
    * done to it by the gateway before it is sent to the protocol for actual
    * sending 
    */
   private void processSendRequests()
   {
      CommandSendRequest sendRequest = addRemoveCommandSendRequest(null);
      
      while(sendRequest != null) {
         int commandID = sendRequest.commandID;
         String[] dynamicParams = sendRequest.commandDynamicParameters;
         Command command = commands.get(commandID);
         Map<String, String> commandParams = command.getProperties();
   
         // Retrieve the Payload
         // TODO: Implement a better means of getting the payload, maybe
         // this should be separately exposed in the command model
         String commandString = commandParams.get("command");
         
         if (commandString != null) {
            // Send request via script engine
            
            // Substitute dynamic params in payload
            
            // Send payload to the protocol
         }
         sendRequest = addRemoveCommandSendRequest(null);
      }
   }
   
   // Shared Methods --------------------------------------------------------------------
   
   /**
    * Single method for modifying the Command Queue; as both the gateway and protocol threads
    * need to access the command queue we need to do it in a sensible way. If a CommandSendRequest
    * object is supplied into this method an add request is assumed.
    * 
    * @return Command Send Request if remove requested otherwise null
    */
   private synchronized CommandSendRequest addRemoveCommandSendRequest(CommandSendRequest newCommandSendRequest)
   {
      CommandSendRequest result = null;
      
      if (newCommandSendRequest != null)
      {
         // We're adding a new request
         commandQueue.add(newCommandSendRequest);
      } else {
         // We're removing a request
         result = commandQueue.size() > 0 ? commandQueue.remove(0) : null;
      }
      
      return result;
   }
   
   
   // Protocol Thread Methods -----------------------------------------------------------
   
   private synchronized void protocolRun()
   {
      while(isAlive && protocol.getStatus() != EnumProtocolStatus.ERROR)
      {
         try
         {
            // Check if we have commands to send; these take priority
            
            
            // No more commands to send so start listener
            startListener();
         }
         catch (InterruptedException e) {
            // If we've been interrupted then it means that the gateway
            // wants us to do something so just ignore this interrupt as
            // the start of the while loop will deal with the request
         }
      }
   }
   
   /**
    * This is the listener handler, for active protocols it will keep calling the
    * protocol read method until it is interrupted to allow commands to be sent. If
    * the protocol read method blocks on call then the interrupt method must also be
    * correctly implemented to allow for detection of interrupts. For passive protocols
    * it does nothing apart from wait for a thread interrupt. 
    * 
    * THIS NEEDS LOOKING AT - CURRENTLY IF AN ACTIVE PROTOCOL DOESN'T CORRECTLY
    * IMPLEMENT THE INTERRUPT METHOD THE GATEWAY WON'T BE ABLE TO SEND COMMANDS.
    * 
    */
   private synchronized void startListener() throws InterruptedException
   {
      // Start Reading if this is an active protocol
      if (protocol instanceof ActiveProtocol)
      {
         ActiveProtocol activeProtocol = (ActiveProtocol)protocol;
         activeProtocol.clearReadBuffer();
         
         while(isAlive)
         {
            Payload response = activeProtocol.read();
            if (response != null)
            {
               // We have a response so let's deal with it
               
            }
            // Check for an interrupt signal
            if (Thread.interrupted()) {
               throw new InterruptedException();
            }
            Thread.sleep(LISTENER_SLEEP_DURATION);
         }
      } else {
         /*
          *  We have nothing to listen for so just wait for
          *  notification of a command to send
          */
         protocolThread.wait();
      }
   }
}
