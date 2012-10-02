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

import java.util.ArrayList;

import org.openremote.controller.device.protocol.ActiveProtocol;
import org.openremote.controller.device.protocol.ProtocolStatus;
import org.openremote.controller.device.protocol.PassiveProtocol;
import org.openremote.controller.device.protocol.PassiveResponsiveProtocol;
import org.openremote.controller.device.protocol.Payload;
import org.openremote.controller.device.protocol.Protocol;

/**
 * The Gateway Protocol Thread is used to manage communication with the protocol instance. It is responsible for calling
 * send and read methods of the underlying protocol and notifying the parent gateway watcher if any responses become
 * available. When it is not sending commands/waiting for responses if the protocol is an instance of ActiveProtocol
 * then it will listen for incoming messages.
 * 
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 * 
 */
public class GatewayProtocolThread extends Thread {
   private GatewayWatcher parent;
   private Protocol protocol;
   private boolean isRunning;
   private ArrayList<GatewayProtocolPacket> outboundQueue = new ArrayList<GatewayProtocolPacket>();
   private ArrayList<GatewayProtocolPacket> inboundQueue = new ArrayList<GatewayProtocolPacket>();

   public GatewayProtocolThread(GatewayWatcher parent, Protocol protocol) {
      this.parent = parent;
      this.protocol = protocol;
   }

   @Override
   public void run() {
      isRunning = true;

      while (parent.getIsAlive() && protocol.getStatus() != ProtocolStatus.ERROR) {
         try {
            if (outboundQueue.size() > 0) {
               // Process outbound commands; these take priority over listening
               GatewayProtocolPacket sendRequest = removeOutboundRequest();

               while (sendRequest != null) {

                  if (protocol instanceof ActiveProtocol) {
                     // Send request and if response is received place it in the inbound queue
                     ((ActiveProtocol) protocol).send(sendRequest.getPayload());
                     Payload response = ((ActiveProtocol) protocol).read();
                     sendRequest.setPayload(response);
                     addInboundResponse(sendRequest);
                  } else if (protocol instanceof PassiveResponsiveProtocol) {
                     // Send request and if response is received place it in the inbound queue
                     Payload response = ((PassiveResponsiveProtocol) protocol).send(sendRequest.getPayload());
                     sendRequest.setPayload(response);
                     addInboundResponse(sendRequest);
                  } else {
                     // Assume passive and so no response will be received
                     ((PassiveProtocol) protocol).send(sendRequest.getPayload());
                  }

                  sendRequest = removeOutboundRequest();
               }

               if (inboundQueue.size() > 0) {
                  // Notify the gateway watcher that there are responses waiting
                  synchronized (parent) {
                     parent.notify();
                  }
               }
            } else {
               // No commands to send so start listener
               startListener();
            }
         } catch (InterruptedException e) {
            // If we've been interrupted then it means that the gateway
            // wants us to do something so just ignore this interrupt as
            // the while loop will deal with the request
         }
      }

      isRunning = false;
   }

   // Shared Methods --------------------------------------------------------------------

   public boolean getIsRunning() {
      return isRunning;
   }

   /**
    * Remove's oldest outbound command request from the queue
    * 
    * @return Oldest outbound command request in the queue
    */
   protected synchronized GatewayProtocolPacket removeOutboundRequest() {
      return outboundQueue.size() > 0 ? outboundQueue.remove(0) : null;
   }

   /**
    * Add's a new outbound command request to the queue
    * 
    * @param newCommandSendRequest
    */
   protected synchronized void addOutboundRequest(GatewayProtocolPacket newCommandSendRequest) {
      if (newCommandSendRequest != null) {
         // Add a new request
         outboundQueue.add(newCommandSendRequest);
      }
   }

   /**
    * Remove's oldest inbound command response from the queue
    * 
    * @return Oldest inbound command response in the queue
    */
   protected synchronized GatewayProtocolPacket removeInboundResponse() {
      return inboundQueue.size() > 0 ? inboundQueue.remove(0) : null;
   }

   /**
    * Add's a new inbound command response to the queue
    * 
    * @param newCommandSendRequest
    */
   protected synchronized void addInboundResponse(GatewayProtocolPacket newCommandResponse) {
      if (newCommandResponse != null) {
         // Add a new request
         inboundQueue.add(newCommandResponse);
      }
   }

   // Private Methods -------------------------------------------------------------------

   /**
    * This is the listener handler, for active protocols it will keep calling the protocol read method until it is
    * interrupted to allow commands to be sent. If the protocol read method blocks on call then the interrupt method
    * must also be correctly implemented to allow for detection of interrupts. For passive protocols it does nothing
    * apart from wait for a thread interrupt.
    * 
    * THIS NEEDS LOOKING AT - CURRENTLY IF AN ACTIVE PROTOCOL DOESN'T CORRECTLY IMPLEMENT THE INTERRUPT METHOD THE
    * GATEWAY WON'T BE ABLE TO SEND COMMANDS.
    * 
    */
   private void startListener() throws InterruptedException {
      // Start Reading if this is an active protocol
      if (protocol instanceof ActiveProtocol) {
         ActiveProtocol activeProtocol = (ActiveProtocol) protocol;
         activeProtocol.clearReadBuffer();

         while (parent.getIsAlive() && protocol.getStatus() != ProtocolStatus.ERROR) {
            Payload response = activeProtocol.read();
            if (response != null) {
               // We have a response so let's deal with it
               addInboundResponse(new GatewayProtocolPacket(response));

               // Notify the gateway watcher that there is a response waiting
               synchronized (parent) {
                  parent.notify();
               }
            }

            // Check for an interrupt signal
            if (Thread.interrupted()) {
               throw new InterruptedException();
            }
            Thread.sleep(Gateway.LISTENER_SLEEP_DURATION);
         }
      } else {
         /*
          * We have nothing to listen for so just wait for notification of a command to send from gateway watcher.
          */
         synchronized (this) {
            wait();
         }
      }
   }
}
