package org.openremote.controller.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.openremote.controller.RoundRobinConfig;
import org.openremote.controller.exception.roundrobin.TCPServerStartFailException;
import org.openremote.controller.spring.SpringContext;
import org.openremote.controller.utils.ConfigFactory;

/**
 * RoundRobin TCP Server.<br / ><br / >
 * It's responsible for accept all RoundRobin TCP client.
 * 
 * @author Handy.Wang 2009-12-24
 */
public class RoundRobinTCPServer implements Runnable {
   
   private Logger logger = Logger.getLogger(this.getClass().getName());
   
   private RoundRobinConfig roundRobinConfig = ConfigFactory.getRoundRobinConfig();
   
   private static final String SEPARATOR_BETWEEN_MSG_KEY_AND_GROUP_NAME = RoundRobinClient.SEPARATOR_BETWEEN_MSG_KEY_AND_GROUP_NAME;
   
   @Override
   public void run() throws TCPServerStartFailException {
      logger.info("TCP Server : starting for receiving groupmember urls...");
      ServerSocket tcpServerSocket = null;
      try {
         tcpServerSocket = new ServerSocket(roundRobinConfig.getRoundRobinTCPServerSocketPort());
      } catch (IOException e) {
         logger.error(e.getStackTrace(), e);
         throw new TCPServerStartFailException("Start TCP Server fail.");
      }
      Socket socket;
      logger.info("TCP Server : started successfully for receiving groupmember urls...");
      logger.info("TCP Server : Waiting for groupmember response...");
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
      
      @SuppressWarnings("unchecked")
      @Override
      public void run() {
         try {
            logger.info("TCP Server deal thread : Ready for receiving groupmember url ...");
            BufferedReader br = new BufferedReader(new InputStreamReader(this.innerSocket.getInputStream()));
            RoundRobinData roundRobinData = splitReceivedDataFromRoundRobinUDPServer(br.readLine());
            String groupMemberURL = roundRobinData.getContent();
            logger.info("TCP Server deal thread : received a groundmember url : " + groupMemberURL);
            ((ConcurrentHashMap<String, String>)(SpringContext.getInstance().getBean("servers"))).put(roundRobinData.getContent(), roundRobinData.getMsgKey());
            innerSocket.close();
         } catch (IOException e) {
            logger.error("Create bufferedReader fail.", e);
         }
      }
      
      private RoundRobinData splitReceivedDataFromRoundRobinUDPServer(String roundRobinRawData) {
         String[] datas = roundRobinRawData.split(SEPARATOR_BETWEEN_MSG_KEY_AND_GROUP_NAME);
         RoundRobinData rrd = new RoundRobinData();
         if (datas.length > 0) {
            rrd.setMsgKey(datas[0]);
         }
         if (datas.length > 1) {
            rrd.setContent(datas[1]);
         }
         return rrd;
      }
   }
   
}