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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openremote.controller.Configuration;
import org.openremote.controller.exception.roundrobin.TCPServerStartFailException;
import org.openremote.controller.utils.ConfigFactory;

/**
 * This class provide discover groupMember function.<br /><br />
 * 
 * Firstly, startup a tcpserver for receive groupmembers' controller url.<br />
 * 
 * Secondly, send a udppackage to detect groupmembers.<br />
 * 
 * And then return the discovered groupmembers' controller url.<br />
 * 
 * @author Handy.Wang 2009-12-22
 */
public class RoundRobinClient {

   private Logger logger = Logger.getLogger(this.getClass().getName());

   private Configuration configuration = ConfigFactory.getConfig();

   /** Container of group members' url. */
   private List<String> groupMemberURLs;
   
   /** RoundRobin TCP Server. */
   private ServerSocket tcpServerSocket;
   
   /** Indicate whether roundrobin TCP server is on. */
   private boolean isTCPServerOn;
   
   /** 
    * Group name of controller.<br /><br /> 
    * <b>NOTE:</b> This property can be initialized by <b>CONSTRUCTOR</b> or <b>CONFIGURATION</b>.<br />
    * If this property is null, it will be initialized by <b>CONFIGURATION</b>.
    */
   private String groupName;
   
   public RoundRobinClient(){
      super();
      this.groupMemberURLs = Collections.synchronizedList(new ArrayList<String>());
      this.isTCPServerOn = false;
   }
   
   public RoundRobinClient(String groupName){
      super();
      this.groupName = groupName;
      this.groupMemberURLs = Collections.synchronizedList(new ArrayList<String>());
      this.isTCPServerOn = false;
   }
   
   /**
    * Public interface for providing function of get all the group members which have same group name of current controller application.
    */
   public List<String> getGroupMemberURLsList() {
      this.groupMemberURLs.clear();
      discoverGroupMembers();
      return groupMemberURLs;
   }
   
   public Set<String> getGroupMemberURLsSet() {
      this.groupMemberURLs.clear();
      discoverGroupMembers();
      Set<String> urls = new HashSet<String>();
      urls.addAll(groupMemberURLs);
      return urls;
   }
   
   /**
    * Discover all the group members which have the same group name of current controller application. 
    */
   private void discoverGroupMembers() throws TCPServerStartFailException {
      startReceiveGroupMemberURLsTCPServer();
      sendRoundRobinUDPMultiCastRequest();
      waitForGroupMemberURLS();
      shutDownTCPServer();
   }

   /**
    * Start RoundRobin TCP Server.
    */
   private void startReceiveGroupMemberURLsTCPServer() {
      logger.info("TCP Server : starting for receiving groupmember urls...");
      new Thread(new TCPServerThread()).start();
      logger.info("TCP Server : started successfully for receiving groupmember urls...");
   }

   /**
    * Send RoundRobin UDP Multicast request for detecing whether there is any group member existed. 
    */
   private void sendRoundRobinUDPMultiCastRequest() throws TCPServerStartFailException {
      boolean isWakeUpOrTCPServerOn = false;
      while (!isTCPServerOn) {
         if (isWakeUpOrTCPServerOn) {
            throw new TCPServerStartFailException("Currently, TCP Server is off.");
         }
         try {
            Thread.sleep(100);
            isWakeUpOrTCPServerOn = true;
         } catch (InterruptedException e1) {
            e1.printStackTrace();
         }
      }
      MulticastSocket socket = null;
      try {
         socket = new MulticastSocket();
         InetAddress groupMulticastAdressForRoundRobin = InetAddress.getByName(configuration.getRoundRobinMulticastAddress());
         socket.joinGroup(groupMulticastAdressForRoundRobin);
         byte[] data = null;
         if (groupName == null || "".equals(groupName)) {
            data = (configuration.getControllerGroupName()).getBytes();
         } else {
            data = groupName.getBytes();
         }
         DatagramPacket packet = new DatagramPacket(data, data.length, groupMulticastAdressForRoundRobin, configuration.getRoundRobinMulticastPort());
         socket.send(packet);
      } catch (IOException e) {
         logger.error("Created UDP request socket fail.", e);
      }
   }
   
   /**
    * Wait for a moment for giving RoundRobin TCP clients some time to response.
    */
   private void waitForGroupMemberURLS() {
      synchronized(groupMemberURLs) {
         try {
            groupMemberURLs.wait(500);
         } catch (InterruptedException e) {
            logger.error(e.getStackTrace(), e);
         }
         
      }
   }
   
   /**
    * Shutdown RoundRobin TCP Server.
    */
   private void shutDownTCPServer() {
      try {
         tcpServerSocket.close();
      } catch (IOException e) {
         logger.error("Close TCP Server socket exception.", e);
      }
   }
   


   public boolean isTCPServerOn() {
      return isTCPServerOn;
   }

   public void setGroupName(String groupName) {
      this.groupName = groupName;
   }

   /**
    * Main thread of RoundRobin TCP Server.<br / ><br / >
    * It's responsible for accept all RoundRobin TCP client.
    * 
    * @author Handy.Wang 2009-12-23
    */
   class TCPServerThread implements Runnable {
      @Override
      public void run() throws TCPServerStartFailException {
            try {
               tcpServerSocket = new ServerSocket(configuration.getRoundRobinTCPServerSocketPort());
            } catch (IOException e) {
               logger.error(e.getStackTrace(), e);
               throw new TCPServerStartFailException("Start TCP Server fail.");
            }
            Socket socket;
            logger.info("TCP Server : Waiting for groupmember response...");
            isTCPServerOn = true;
            try {
               while ((socket = tcpServerSocket.accept()) != null) {
                  logger.info("TCP Server : a new groupmember socket established...");
                  new Thread(new AppendGroupMemberThread(socket)).start();
                  try {
                     Thread.sleep(100);
                  } catch (InterruptedException e) {
                     e.printStackTrace();
                  }
               }
            } catch (IOException e) {
               logger.info("TCP Server socket closed.");
            }
      }
      
   }
   
   /**
    * Deal thread while a new RoundRobin TCP client coming.<br / ><br / >
    * it's responsible for getting the group members' controller url from the socket and then add it to container of group members' url's
    * 
    * @author Handy.Wang 2009-12-23
    */
   class AppendGroupMemberThread implements Runnable {
      
      private Socket innerSocket;
      
      public AppendGroupMemberThread(Socket innerSocket) {
         this.innerSocket = innerSocket;
      }
      
      @Override
      public void run() {
         try {
            logger.info("TCP Server deal thread : Ready for receiving groupmember url ...");
            BufferedReader br = new BufferedReader(new InputStreamReader(this.innerSocket.getInputStream()));
            String groupMemberURL = br.readLine();
            // TODO: do we need some validity check mechanism for url.
            logger.info("TCP Server deal thread : received a groundmember url : " + groupMemberURL);
            groupMemberURLs.add(groupMemberURL);
            innerSocket.close();
         } catch (IOException e) {
            logger.error("Create bufferedReader fail.", e);
         }
      }
      
   }

}
