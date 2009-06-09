/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.controller.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.apache.log4j.Logger;
import org.openremote.controller.Configuration;
import org.openremote.controller.utils.ConfigFactory;

/**
 * The Class IP Auto Discovery Server.
 * This server will listen to all the clients on a multicast address.
 * After a client sends a UDP packet to this server with its IP,it will create a TCP server to receive the response.
 * Then this server will respond its HTTP URL to the client via TCP connection.
 * 
 * @author Dan 2009-5-18
 */
public class IPAutoDiscoveryServer implements Runnable {
   
   /** The logger. */
   private static Logger logger = Logger.getLogger(IPAutoDiscoveryServer.class.getName());
   
   /** The configuration. */
   private Configuration configuration = ConfigFactory.getConfig();
   

   /**
    * {@inheritDoc}
    */
   public void run() {

      final int MULTICAST_PORT = configuration.getMulticastPort();
      final String MULTICAST_ADDRESS = configuration.getMulticastAddress();
      MulticastSocket socket = null;
      InetAddress address = null;
      try {
         socket = new MulticastSocket(MULTICAST_PORT);
         address = InetAddress.getByName(MULTICAST_ADDRESS);
      } catch (IOException e) {
         logger.error("Can't create multicast socket on " + MULTICAST_ADDRESS + ":" + MULTICAST_PORT, e);
      }
      try {
         socket.joinGroup(address);
      } catch (IOException e) {
         logger.error("Can't join group of " + MULTICAST_ADDRESS + ":" + MULTICAST_PORT, e);
      }
      byte[] buf = new byte[512];
      DatagramPacket packet = new DatagramPacket(buf, buf.length);
      while (true) {
         try {
            socket.receive(packet);
         } catch (IOException e) {
            logger.error("Can't receive packet on " + MULTICAST_ADDRESS + ":" + MULTICAST_PORT, e);
         }
         sendLocalIPBack(packet); 
      }
   }



   /**
    * Send local ip back.
    * 
    * @param packet the packet
    */
   private void sendLocalIPBack(DatagramPacket packet) {
      new Thread(new IPResponseTCPClient(packet.getAddress())).start();
   }



   /**
    * Sets the configuration.
    * 
    * @param configuration the new configuration
    */
   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }
   

}
