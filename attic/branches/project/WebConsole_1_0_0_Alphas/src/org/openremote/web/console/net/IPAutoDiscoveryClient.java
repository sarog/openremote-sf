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
package org.openremote.web.console.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.log4j.Logger;

/**
 * Controller IP auto discovery client, this is a Multicast UDP client broadcasting request to Controllers for asking
 * their IPs.
 * 
 * @author Tomsky Wang
 * 
 */
public class IPAutoDiscoveryClient implements Runnable {
   private static Logger log = Logger.getLogger(IPAutoDiscoveryClient.class);
   
   /**
    * Starts UDP broadcasting.
    * The address is "224.0.1.100", port is "3333".
    */
   public void run() {
      try {
         DatagramSocket socket = new DatagramSocket();
         byte[] b = new byte[512];
         DatagramPacket dgram;
         dgram = new DatagramPacket(b, b.length, InetAddress.getByName(IPAutoDiscovery.MULTICAST_ADDRESS), IPAutoDiscovery.MULTICAST_PORT);
         socket.send(dgram);
         log.info("Auto discovery on " + IPAutoDiscovery.MULTICAST_ADDRESS + ":" + IPAutoDiscovery.MULTICAST_PORT);
      } catch (Exception e) {
         log.error("Auto discovery on " + IPAutoDiscovery.MULTICAST_ADDRESS + ":" + IPAutoDiscovery.MULTICAST_PORT
               + " failed", e);
      }

   }
   
}
