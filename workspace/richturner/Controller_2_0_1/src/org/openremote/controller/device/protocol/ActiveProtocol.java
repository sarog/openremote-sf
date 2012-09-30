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
package org.openremote.controller.device.protocol;

import org.openremote.controller.protocol.EventProducer;

/**
 * A Protocol that does require a managed connection for communication
 * and response messages are received independently of send messages.
 * Response messages may be received without a corresponding send message
 * as the device can produce notification messages. This type of protocol
 * is an Event Producer.
 * 
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 *
 */
public interface ActiveProtocol extends EventProducer {
   /**
    * Used by the gateway to send data to the device, for active
    * devices it is expected that the response will be retrieved
    * using the read method.
    * 
    * @param payload
    */
   public void send(Payload payload);
   
   /**
    * Used by the gateway to read data from the device, for devices
    * that support persistent connections this method will be used
    * to retrieve data from the device. The gateway will manage the
    * calls to this method, it is the responsibility of the method
    * to return complete response Payloads and optionally it may
    * block until a response is available, if it does block then the
    * interrupt method must be implemented correctly.
    * 
    * @return Payload response message
    */
   public Payload read() throws InterruptedException;
   
   /**
    * Used by the gateway to force clear any read buffer associated
    * with the protocol. If there isn't a read buffer then this method
    * doesn't have to do anything
    * 
    */
   public void clearReadBuffer();
   
   /**
    * Must be capable of interrupting a blocking read method. This will
    * be called by the owning gateway when it needs to send a command.
    */
   void interrupt();
}
