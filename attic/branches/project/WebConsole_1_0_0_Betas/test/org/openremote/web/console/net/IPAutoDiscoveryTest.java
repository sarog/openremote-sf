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

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

public class IPAutoDiscoveryTest {

   @Test
   public void testGetAutoServers() {
      new Thread(new IPAutoDiscoveryControllerServer()).start();
      List<String> autoServers = IPAutoDiscovery.getAutoServers();
      Assert.assertEquals("http://127.0.0.1:8080/controller", autoServers.get(0));
   }
}

class IPAutoDiscoveryControllerServer implements Runnable {
   
   /**
    * {@inheritDoc}
    */
   public void run() {
      final int MULTICAST_PORT = IPAutoDiscovery.MULTICAST_PORT;
      final String MULTICAST_ADDRESS = IPAutoDiscovery.MULTICAST_ADDRESS;
      String multicastLocation = MULTICAST_ADDRESS + ":" + MULTICAST_PORT;
      MulticastSocket socket = null;
      InetAddress address = null;
      try {
         socket = new MulticastSocket(MULTICAST_PORT);
         address = InetAddress.getByName(MULTICAST_ADDRESS);
         System.out.println("Created IP discover multicast server !");
      } catch (IOException e) {
         System.out.println("Can't create multicast socket on " + multicastLocation);
      }
      try {
         socket.joinGroup(address);
         System.out.println("Joined a group : "+multicastLocation);
      } catch (IOException e) {
         System.out.println("Can't join group of " + multicastLocation);
      }
      byte[] buf = new byte[512];
      DatagramPacket packet = new DatagramPacket(buf, buf.length);
      while (true) {
         try {
            System.out.println("Listening on  " + multicastLocation);
            socket.receive(packet);
            System.out.println("Received an IP auto-discovery request from " + packet.getAddress().getHostAddress());
         } catch (IOException e) {
            System.out.println("Can't receive packet on " + MULTICAST_ADDRESS + ":" + MULTICAST_PORT);
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
}

class IPResponseTCPClient implements Runnable {
   
   /** The target ip. */
   private InetAddress targetIP;
   
   /**
    * Instantiates a new tCP client.
    * 
    * @param targetIP the target ip
    */
   public IPResponseTCPClient(InetAddress targetIP) {
      super();
      this.targetIP = targetIP;
   }

   @Override
   public void run() {
      sendTcp();
   }
   
   /**
    * Send tcp.
    */
   public void sendTcp() {
      String targetIPStr = targetIP.getHostAddress();
      String data = "http://127.0.0.1:8080/controller";
      System.out.println("Sending server IP '" + data + "' to " + targetIPStr);
      Socket skt = null;
      PrintWriter out = null;
      try {
         skt = new Socket(targetIP, IPAutoDiscovery.TCP_PORT);
         out = new PrintWriter(skt.getOutputStream(), true);
      } catch (IOException e) {
         System.out.println("Response failed! Can't create TCP socket on " + targetIPStr);
      } finally {
         out.print(data);
         out.close();
         try {
            skt.close();
         } catch (IOException e) {
            System.out.println("Can't close socket");
         }
      }

   }
}
