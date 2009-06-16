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
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.log4j.Logger;

/**
 * The Class IP Auto Discovery Server. 
 * This server will listen to client on a multicast address.
 * When a client send a UDP packet to this server, the client will tell this server its IP and create a TCP server.
 * Then the server will respond its HTTP URL to the client via TCP connection.
 * 
 * 
 * @author Dan 2009-5-18
 */
public class IPAutoDiscoveryServer implements Runnable {
   
   /** The logger. */
   private static Logger logger = Logger.getLogger(IPAutoDiscoveryServer.class.getName());
   
   /** The multicast address. */
   public static String MULTICAST_ADDRESS= "224.0.1.100";
   
   /** The multicast port. */
   public static int MULTICAST_PORT = 3333;
   
   /** The TCP port. */
   public static int TCP_PORT = 2346;
   

   /**
    * {@inheritDoc}
    */
   public void run() {

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
         sendTcp(packet.getAddress());
      }
   }
   
   /**
    * Send tcp.
    * 
    * @param targetIP the ip
    */
   public static void sendTcp(InetAddress targetIP) {
      String data = "http://" + getLocalhostIP() + ":8080/controller";
      Socket skt = null;
      PrintWriter out = null;
      try {
         skt = new Socket(targetIP, TCP_PORT);
         out = new PrintWriter(skt.getOutputStream(), true);
      } catch (IOException e) {
         logger.error("Can't create TCP socket on " + MULTICAST_ADDRESS + ":" + MULTICAST_PORT, e);
      } finally {
         out.print(data);
         out.close();
         try {
            skt.close();
         } catch (IOException e) {
            logger.error("Can't close socket", e);
         }
      }

   }
   

   /**
    * Gets the localhost ip.
    * 
    * @return the localhost ip
    */
   public static String getLocalhostIP() {
      String ip = "";
      try {
         Enumeration<?> e1 = (Enumeration<?>) NetworkInterface.getNetworkInterfaces();
         while (e1.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface) e1.nextElement();
            if (!ni.getName().equals("eth0")) {
               continue;
            } else {
               Enumeration<?> e2 = ni.getInetAddresses();
               while (e2.hasMoreElements()) {
                  InetAddress ia = (InetAddress) e2.nextElement();
                  if (ia instanceof Inet6Address) continue;
                  ip = ia.getHostAddress();
               }
               break;
            }
         }
      } catch (SocketException e) {
         logger.error("Can't get Network Interfaces", e);
      }
      return ip;
   }

}
