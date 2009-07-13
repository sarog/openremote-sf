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
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.log4j.Logger;
import org.openremote.controller.Configuration;
import org.openremote.controller.utils.ConfigFactory;

/**
 * The Class TCPClient.
 * 
 * @author Dan 2009-6-1
 */
public class IPResponseTCPClient implements Runnable {
   
   /** The logger. */
   private static Logger logger = Logger.getLogger(IPResponseTCPClient.class.getName());
   
   /** The target ip. */
   private InetAddress targetIP;
   
   /** The TCP port. */
   public final static int TCP_PORT = 2346;
   
   /** The configuration. */
   private Configuration configuration = ConfigFactory.getConfig();
   
   

   /**
    * Instantiates a new tCP client.
    * 
    * @param targetIP the target ip
    */
   public IPResponseTCPClient(InetAddress targetIP) {
      super();
      this.targetIP = targetIP;
   }

   /* (non-Javadoc)
    * @see java.lang.Runnable#run()
    */
   @Override
   public void run() {
      sendTcp();
   }
   
   /**
    * Send tcp.
    */
   public void sendTcp() {
      String targetIPStr = targetIP.getHostAddress();
      String data = "http://" + getLocalhostIP() + ":" + configuration.getWebappPort() + "/controller";
      logger.info("Sending server IP '" + data + "' to " + targetIPStr);
      Socket skt = null;
      PrintWriter out = null;
      try {
         skt = new Socket(targetIP, TCP_PORT);
         out = new PrintWriter(skt.getOutputStream(), true);
      } catch (IOException e) {
         logger.error("Response failed! Can't create TCP socket on " + targetIPStr, e);
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
    * Gets the localhost ip in Linux.
    * 
    * @return the localhost ip
    */
   public static String getLocalhostIP() {
      String ip = "";
      try {
         Enumeration<?> interfaces = (Enumeration<?>) NetworkInterface.getNetworkInterfaces();
         while (interfaces.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface) interfaces.nextElement();
            if (ni.isUp() && ni.supportsMulticast() && !ni.isLoopback()) {
               Enumeration<?> addresses = ni.getInetAddresses();
               while (addresses.hasMoreElements()) {
                  InetAddress ia = (InetAddress) addresses.nextElement();
                  if (ia instanceof Inet6Address) {
                     continue;
                  }
                  ip = ia.getHostAddress();
               }
               if(!ip.isEmpty()){
                  break;
               }
            }
         }
      } catch (SocketException e) {
         logger.error("Can't get Network Interfaces", e);
      }
      return ip;
   }

}
