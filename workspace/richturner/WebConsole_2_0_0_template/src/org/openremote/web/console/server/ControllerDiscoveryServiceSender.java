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
package org.openremote.web.console.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Controller IP auto discovery client, this is a Multicast UDP client broadcasting request to Controllers for asking
 * their IPs.
 * 
 * @authors Tomsky Wang, Rich Turner
 * 
 */
public class ControllerDiscoveryServiceSender implements Runnable {
   /**
    * Starts UDP broadcasting.
    * The address is "224.0.1.100", port is "3333".
    */
   public void run() {
      try {
         DatagramSocket socket = new DatagramSocket();
         byte[] b = new byte[512];
         DatagramPacket dgram;
         dgram = new DatagramPacket(b, b.length, InetAddress.getByName(ControllerDiscoveryService.MULTICAST_ADDRESS), ControllerDiscoveryService.MULTICAST_PORT);
         socket.send(dgram);
      } catch (Exception e) {
      }
   }
   
}
