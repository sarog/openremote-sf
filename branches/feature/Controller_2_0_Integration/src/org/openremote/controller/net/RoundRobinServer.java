/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.controller.net;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.openremote.controller.Configuration;
import org.openremote.controller.RoundRobinConfig;
import org.openremote.controller.exception.roundrobin.TCPClientEstablishException;
import org.openremote.controller.exception.roundrobin.UDPServerStartFailException;
import org.openremote.controller.utils.ConfigFactory;
import org.openremote.controller.utils.NetworkUtil;

/**
 * RoundRobin Server.<br /><br />
 * It's mainly responsible for : <br />
 * 1) Starting a RoundRobin UDP multicast server for observing whether any UDP multicast request is coming.<br />
 * 2) If the coming UDP multicast request is from same group, then response to RoundRobin client with current controller application url.
 * 
 * @author Handy.Wang 2009-12-22
 */
public class RoundRobinServer implements Runnable {
   
   private Logger logger = Logger.getLogger(this.getClass().getName());
   
   private Configuration configuration = ConfigFactory.getConfig();
   
   private RoundRobinConfig roundRobinConfig = ConfigFactory.getRoundRobinConfig();
   
   /**
    * Group name of controller.
    */
   private String groupName;
   
   /** Protocol header of controller application url. */
   private static final String CONTROLLER_URL_PROTOCOL_HEADER = "http://";
   
   /** Separator of controller application url. */
   private static final String CONTROLLER_URL_SEPARATOR = "/";
   
   public RoundRobinServer() {
      super();
   }
   
   public RoundRobinServer(String controllerGroupName) {
      this.groupName = controllerGroupName;
   }
   
   /**
    * Main thread of RoundRobin UDP multicast Server.<br /><br />
    * It's responsible for starting a RoundRobin UDP multicast server and the observing if any udp multicast request is coming from RoundRobin client.
    */
   @Override
   public void run() {

      MulticastSocket roundRobinUDPMulticastServerSocket = null;
      InetAddress multiCastAddress = null;
      try {
         logger.info("UDP Server : Starting UDP server...");
         roundRobinUDPMulticastServerSocket = new MulticastSocket(roundRobinConfig.getRoundRobinMulticastPort());
         multiCastAddress = InetAddress.getByName(roundRobinConfig.getRoundRobinMulticastAddress());
         roundRobinUDPMulticastServerSocket.joinGroup(multiCastAddress);
         logger.info("UDP Server : Started UDP server successfully.");
      } catch (Exception e) {
         throw new UDPServerStartFailException("UDP Server : Startup roundRobin UDP multicast serversocket fail.", e);
      }
      byte[] data = new byte[1024];
      DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
      while (true) {
         try {
            roundRobinUDPMulticastServerSocket.receive(datagramPacket);
            new Thread(new UDPServerDealThread(groupName, datagramPacket)).start();
            try {
               Thread.sleep(100);
            } catch (InterruptedException e) {
               logger.error(e.getStackTrace(), e);
            }
         } catch (IOException e) {
            logger.error("Receive UDP multicast request data erroe.", e);
         }
      }
   }

   /**
    * Thread of UDP server deal the datagramPacket from RoundRobin MultiCast request. 
    * 
    * @author Handy.Wang 2009-12-22
    */
   class UDPServerDealThread implements Runnable {
      
      private DatagramPacket datagramPacket;
      private String groupName;
      
      public UDPServerDealThread(String groupName, DatagramPacket datagramPacket) {
         this.datagramPacket = datagramPacket;
         this.groupName = groupName;
      }
      
      @Override
      public void run() {
         if (groupName == null || "".equals(groupName)) {
            groupName = roundRobinConfig.getControllerGroupName();
         }
         String roundRobinClientControllerGroupName = new String(datagramPacket.getData()).trim();
         if (groupName.equalsIgnoreCase(roundRobinClientControllerGroupName)) {
            logger.info("UDP Server : Received the controller multicast request from the same group " + roundRobinClientControllerGroupName);
            Socket tcpClientSocket = null;
            PrintWriter printWriter = null;
            try {
               tcpClientSocket = new Socket(datagramPacket.getAddress(), roundRobinConfig.getRoundRobinTCPServerSocketPort());
               printWriter = new PrintWriter(tcpClientSocket.getOutputStream(), true);
            } catch (Exception e) {
               throw new TCPClientEstablishException("Established TCP Client socket fail.");
            }
            String controllerURL = CONTROLLER_URL_PROTOCOL_HEADER + NetworkUtil.getLocalhostIP() + ":" + configuration.getWebappPort() + CONTROLLER_URL_SEPARATOR + roundRobinConfig.getControllerApplicationName();
            printWriter.println(controllerURL);
            printWriter.close();
            try {
               tcpClientSocket.close();
            } catch (IOException e) {
               logger.error("Close tcpClientSocket exception", e);
            }
         } else {
            logger.info("UDP Server : The client controller groupname " + roundRobinClientControllerGroupName + " doesn't equals self-groupname " + groupName);
         }
      }
   }
   
}
