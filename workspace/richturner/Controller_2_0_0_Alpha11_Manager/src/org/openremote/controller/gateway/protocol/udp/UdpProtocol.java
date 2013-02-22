/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.gateway.protocol.udp;

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import org.apache.log4j.Logger;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import org.openremote.controller.gateway.protocol.Protocol;
import org.openremote.controller.gateway.EnumGatewayConnectionType;
import org.openremote.controller.gateway.EnumGatewayPollingMethod;
import org.openremote.controller.gateway.command.Action;
import org.openremote.controller.gateway.command.EnumCommandActionType;

/**
 * The HTTP Protocol.
 * 
 * Author Rich Turner 2011-04-15
 */
public class UdpProtocol extends Protocol {

   // Common Protocol Properties --------------------------------------------------------
   /** A name to identify protocol in logs */
   protected String name;
   
   /* Set supported connection Types */
   protected static List<EnumGatewayConnectionType> allowedConnectionTypes = Arrays.asList(EnumGatewayConnectionType.NONE);
   
   /* Set supported polling methods */
   protected static List<EnumGatewayPollingMethod> allowedPollingMethods = Arrays.asList(EnumGatewayPollingMethod.QUERY, EnumGatewayPollingMethod.BROADCAST);
   
   // Protocol Properties ----------------------------------------------------------------
   String host = "";
   
   String port = "";
   
   // Protocol Get Set Methods -----------------------------------------------------------
   public List<EnumGatewayConnectionType> getAllowedConnectionTypes() {
      return allowedConnectionTypes;
   }
   
   public List<EnumGatewayPollingMethod> getAllowedPollingMethods() {
      return allowedPollingMethods;
   }
   
   /**
    * Gets the host
    * 
    * @return the host
    */
   public String getHost() {
      return host;
   }

   /**
    * Sets and validates the host
    * 
    * @param host
    *           the new host
    */
   public void setHost(String host) {
   	this.host = host;
   }
   
   /**
    * Gets the port
    * 
    * @return the port
    */
   public String getPort() {
      return this.port;
   }

   /**
    * Sets the port 
    * @param port the new port
    */
   public void setPort(String port) {
      this.port = port;
   }

   // Protocol Methods --------------------------------------------------------------------
   public String buildNameString() {
      return "udp://" + getHost() + ":" + getPort() + "/";
   }
   
   /**
    * Use this method to validate a command action for this protocol
    */
   public Boolean isValidAction(Action action) {
      // Ensure required args are supplied and action type is supported
      Boolean result = true;
      return result;
   }
   
   /**
    * Perform protocol action usually send and read actions
    */
   public String doAction(Action commandAction) throws Exception {
      Map<String, String> args = commandAction.getArgs();
      EnumCommandActionType actionType = commandAction.getType();
      String actionResult = "";
      
      switch (actionType) {
         case SEND:
         	// Create socket no need to specify port for sending
         	DatagramSocket socket = new DatagramSocket();
         	
         	// Create packet
         	InetAddress address = InetAddress.getByName(getHost());
         	int port = Integer.parseInt(getPort());
         	byte[] bytes = args.get("command").getBytes();
         	DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, port);
         	
         	// Send the packet
         	socket.send(packet);
         	break;
         case READ:
         	// Do nothing
            break;
      }
      return actionResult;
   }
}
