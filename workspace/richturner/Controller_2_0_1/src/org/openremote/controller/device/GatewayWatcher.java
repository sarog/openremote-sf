/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2012, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.device;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import org.openremote.controller.device.protocol.Payload;
import org.openremote.controller.device.protocol.Protocol;

/**
 * The gateway watcher is used to watch for incoming command send requests and to periodically poll sensor commands to
 * check for changes. It doesn't directly do the device communication but instead leaves this to the protocol Thread
 * which must be notified that there is work to do or it will stay in a listening state (Active Protocols) or lie
 * dormant (Passive Protocols).
 * 
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 * 
 */
public class GatewayWatcher extends Thread {
   private Gateway parent;
   private Protocol protocol;
   private GatewayProtocolThread protocolThread;
   private ArrayList<GatewayProtocolPacket> processQueue = new ArrayList<GatewayProtocolPacket>();
   private int pollingInterval = Gateway.DEFAULT_POLLING_INTERVAL;
   private Calendar pollExpiry;

   public GatewayWatcher(Gateway parent, Protocol protocol) {
      this.parent = parent;
      this.protocol = protocol;
      pollExpiry = Calendar.getInstance();

      // Update polling interval if it is set in the protocol parameters
      String pollingIntervalStr = this.protocol.getParameters().getParameterValue("pollinginterval");
      if (pollingIntervalStr != null) {
         try {
            int requestedPollingInterval = Integer.parseInt(pollingIntervalStr);
            this.pollingInterval = requestedPollingInterval >= Gateway.MIN_POLLING_INTERVAL ? requestedPollingInterval
                  : Gateway.MIN_POLLING_INTERVAL;
         } catch (Exception e) {
            // TODO: Log exception
         }
      }
   }

   @Override
   public void run() {
      // Start the protocol thread
      protocolThread = new GatewayProtocolThread(this, protocol);
      protocolThread.start();

      while (getIsAlive()) {
         try {
            // Process any commands waiting to be sent
            processSendRequests();

            // Process any incoming responses
            processIncomingResponses();

            if (Calendar.getInstance().after(pollExpiry)) {
               // Poll sensor commands
               // TODO: Improve polling initiation logic

               pollExpiry = Calendar.getInstance();
               pollExpiry.add(Calendar.MILLISECOND, pollingInterval);
            }

            // Wait until it's time to poll sensors or we get notified
            // that there's work to do by the gateway or protocol thread
            wait(pollingInterval);

         } catch (InterruptedException e) {
            // Ignore any interrupts
         }
      }
   }

   // Shared Methods --------------------------------------------------------------------

   protected boolean getIsAlive() {
      return parent.getIsAlive() && protocolThread.getIsRunning();
   }

   /**
    * Remove's oldest command request from the queue
    * 
    * @return Oldest command send request in the queue
    */
   protected synchronized GatewayProtocolPacket removeOutboundRequest() {
      return processQueue.size() > 0 ? processQueue.remove(0) : null;
   }

   /**
    * Add's a new command request to the queue
    * 
    * @param newCommandSendRequest
    */
   protected synchronized void addOutboundRequest(GatewayProtocolPacket newCommandSendRequest) {
      if (newCommandSendRequest != null) {
         // Add a new request
         processQueue.add(newCommandSendRequest);
      }
   }

   // Private Methods -------------------------------------------------------------------

   /**
    * This is called by the gateway thread to check if there are command requests to fulfil; each command request needs
    * some standard processing done to it by the gateway before it is sent to the protocol for actual sending to the
    * device.
    */
   private void processSendRequests() {
      GatewayProtocolPacket sendPacket = removeOutboundRequest();
      boolean notificationRequired = false;

      while (sendPacket != null) {
         // Send request via script engine

         // Substitute dynamic params into payload
         Payload payload = sendPacket.getPayload();
         String payloadContent = payload.getContent();
         payloadContent = payloadContent.replaceAll(Gateway.LEGACY_DYNAMIC_PARAM_PLACEHOLDER_REGEXP, "{0}");
         try {
            payloadContent = MessageFormat.format(payloadContent, sendPacket.getSendRequest()
                  .getDynamicParameterValues());
         } catch (Exception e) {
         }

         // Update the payload
         payload.setContent(payloadContent);

         // Move this request into the protocol outbound queue

         // GatewayProtocolCallback protocolCallback = new GatewayProtocolCallback(commandID);
         // GatewaySendCallback sendCallback = new GatewaySendCallback(payload, null)

         // Send payload to the protocol
         protocolThread.addOutboundRequest(sendPacket);
         notificationRequired = true;

         sendPacket = removeOutboundRequest();
      }

      if (notificationRequired) {
         // Notify the protocol
         synchronized (protocolThread) {
            protocolThread.notify();
         }
      }
   }

   /**
    * This is called by the gateway thread to check if there are any incoming responses to process; if there are then
    * they are routed through the script engine and then if they originate from a command and the command is linked to
    * sensor(s) then these sensor values are updated in the status cache.
    */
   private void processIncomingResponses() {
      GatewayProtocolPacket responsePacket = protocolThread.removeInboundResponse();

      while (responsePacket != null) {
         // Send response via script engine

         // Get sensors associated with this command
         CommandSendRequest sendRequest = responsePacket.getSendRequest();
         if (sendRequest != null) {
            // Get request command ID
            sendRequest.getCommandID();
         }

         responsePacket = protocolThread.removeInboundResponse();
      }
   }
}
