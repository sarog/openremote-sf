package org.openremote.controller.agent.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;
import org.openremote.controller.Constants;

public class ControllerProxy extends Proxy {
   private static Logger logger = Logger.getLogger(Constants.AGENT_LOG_CATEGORY);

   public ControllerProxy(SocketChannel srcSocket)
         throws IOException {
      super(srcSocket);
   }

   public static SocketChannel makeClientSocket(String urlString, String token) throws IOException {
      logger.info("Opening socket to beehive at "+urlString);
      URL url = new URL(urlString);
      SocketChannel socket = SocketChannel.open();
      socket.configureBlocking(false);
      try{
         logger.info("Trying to connect non-blocking");
         if(socket.connect(new InetSocketAddress(url.getHost(), url.getPort()))){
            logger.info("Got socket to beehive");
            return socket;
         }
         logger.info("No luck, selecting");
         Selector selector = Selector.open();
         SelectionKey key = socket.register(selector, SelectionKey.OP_CONNECT);
         ByteBuffer buffer = ByteBuffer.wrap(token.getBytes("ASCII"));
         // timeout of 5s
         while(selector.select(5000) > 0){
            logger.info("Out of select");
            // we only have one key really
            selector.selectedKeys().clear();
            if(key.isConnectable()){
               logger.info("Connectable");
               if(socket.finishConnect()){
                  logger.info("Got socket to beehive");
                  // now start writing the token
                  key.interestOps(SelectionKey.OP_WRITE);
               }
            }
            if(key.isWritable()){
               logger.info("Writing token handshake to beehive");
               socket.write(buffer);
               if(!buffer.hasRemaining()){
                  // we wrote it all, we're good
                  return socket;
               }
               logger.info("More to write, let's loop");
            }
            logger.info("Back in select");
         }
         throw new IOException("Connection timed out");
      }catch(IOException x){
         // don't log the stack since we re-throw
         logger.info("Got an exception while connecting: "+x.getMessage());
         // failed to connect, at least close the socket
         try{
            socket.close();
         }catch(IOException x2){
            // ignore
         }
         throw x;
      }
   }

   @Override
   protected SocketChannel openDestinationSocket() throws IOException {
      return SocketChannel.open(new InetSocketAddress("localhost", 9000));
   }
}
