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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.openremote.controller.device.protocol.PayloadFormat;
import org.openremote.controller.device.protocol.Payload;
import org.openremote.controller.device.protocol.Protocol;
import org.openremote.controller.model.Command;
import org.openremote.controller.model.sensor.Sensor;

/**
 * The Gateway is responsible for bridging between the controller and the protocol. All command requests are initiated
 * through the gateway not directly to the protocol. In this way the protocol has no dependency on the controller. The
 * gateway should live in it's own thread and communication is carried out via the gateway manager, the gateway thread
 * is responsible for managing a separate protocol thread.
 * 
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 * 
 */
public class Gateway {
   /**
    * Sleep duration in milliseconds of listener loop this comes into play with active protocols whose read method does
    * not block
    */
   public static final int LISTENER_SLEEP_DURATION = 1000;

   public static final int DEFAULT_POLLING_INTERVAL = 2000;

   public static final int MIN_POLLING_INTERVAL = 500;

   public static final String PAYLOAD_PARAMETER_NAME = "command";

   public static final String PAYLOAD_FORMAT_PARAMETER_NAME = "format";

   public static final String LEGACY_DYNAMIC_PARAM_PLACEHOLDER_REGEXP = "\\$\\{param\\}";

   private HashMap<Integer, Command> commands = new HashMap<Integer, Command>();
   private HashMap<Integer, Sensor> sensors = new HashMap<Integer, Sensor>();
   private HashMap<Integer, HashSet<Integer>> pollingCommandMap = new HashMap<Integer, HashSet<Integer>>();
   private GatewayWatcher gatewayWatcher;
   private boolean isAlive;

   protected Gateway(Protocol protocol) {
      gatewayWatcher = new GatewayWatcher(this, protocol);
   }

   // Public Methods --------------------------------------------------------------------

   public boolean getIsAlive() {
      return isAlive;
   }

   public Command getCommand(int commandID) {
      return commands.get(commandID);
   }

   // Methods Called by Gateway Manager -------------------------------------------------

   protected void addCommand(Command command) {
      commands.put(command.getId(), command);
   }

   protected void addSensor(Sensor sensor) {
      // Add sensor to sensor map
      sensors.put(sensor.getSensorID(), sensor);

      // Add command of sensor to polling command map, a
      // command may be associated with multiple sensors
      // that's why an Integer set is used in the polling map
      // to support the one to many relationship
      int sensorCommandId = sensor.getEventProducerID();

      if (!pollingCommandMap.containsKey(sensorCommandId)) {
         HashSet<Integer> sensorIDs = new HashSet<Integer>();
         sensorIDs.add(sensor.getSensorID());
         pollingCommandMap.put(sensorCommandId, sensorIDs);
      } else {
         pollingCommandMap.get(sensorCommandId).add(sensor.getSensorID());
      }
   }

   /**
    * Called to start the gateway - it will poll the sensor commands it owns as well as be ready to receive incoming
    * command requests
    */
   protected void start() {
      isAlive = true;

      /*
       * Start the gateway watcher this will in turn start the protocol thread.
       */
      gatewayWatcher.start();
   }

   /**
    * Stops the gateway gracefully
    */
   protected void stop() {
      isAlive = false;
      synchronized (gatewayWatcher) {
         gatewayWatcher.notify();
      }
   }

   /**
    * Send a command using this gateway
    * 
    * @param commandId
    */
   protected void Send(CommandSendRequest commandSendRequest) {
      Send(new CommandSendRequest[] { commandSendRequest });
   }

   /**
    * Send multiple commands using this gateway
    * 
    * @param commandIds
    */
   protected synchronized void Send(CommandSendRequest[] commandSendRequests) {
      /*
       * Add each request to the command queue and let the gateway thread handle them
       */
      for (CommandSendRequest sendRequest : commandSendRequests) {
         Command command = getCommand(sendRequest.getCommandID());
         Map<String, String> commandParams = command.getProperties();

         // Retrieve the Payload
         // TODO: Implement a better means of getting the payload, maybe
         // this should be separately exposed in the command model
         String commandString = commandParams.get(PAYLOAD_PARAMETER_NAME);

         if (commandString != null) {
            // Build the payload
            String payloadFormat = commandParams.get(Gateway.PAYLOAD_FORMAT_PARAMETER_NAME);
            PayloadFormat payloadFormatType = PayloadFormat.fromString(payloadFormat);
            Payload payload = new Payload(commandString, payloadFormatType);

            GatewayProtocolPacket packet = new GatewayProtocolPacket(payload, sendRequest);
            gatewayWatcher.addOutboundRequest(packet);
         }
      }

      // Notify gateway watcher that there's command(s) to process
      synchronized (gatewayWatcher) {
         gatewayWatcher.notify();
      }
   }
}
