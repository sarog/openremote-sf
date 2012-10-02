/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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

import org.openremote.controller.device.protocol.Payload;

/**
 * Provides a wrapper around a protocol payload which could either be an outbound or an inbound payload. The wrapper
 * allows the payload to be associated with a command send request so that the gateway knows what command this packet is
 * associated with. If no command send request ID is defined then it will be entirely up to the user to manually update
 * the status cache if they so wish.
 * 
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 * 
 */
public class GatewayProtocolPacket {
   private CommandSendRequest sendRequest;
   private Payload payload;

   /**
    * Create a new payload not associated with a send request, the gateway will not be able to associate the response
    * with a command and therefore a sensor so it will be up to the user via scripting to decide what to do with this
    * response packet.
    * 
    * @param payload
    */
   public GatewayProtocolPacket(Payload payload) {
      this(payload, null);
   }

   /**
    * Create a new payload and associate it with the specified send request.
    * 
    * @param payload
    * @param sendRequest
    */
   public GatewayProtocolPacket(Payload payload, CommandSendRequest sendRequest) {
      this.payload = payload;
      this.sendRequest = sendRequest;
   }

   public CommandSendRequest getSendRequest() {
      return sendRequest;
   }

   public Payload getPayload() {
      return payload;
   }

   public void setPayload(Payload payload) {
      this.payload = payload;
   }
}
