package org.openremote.controller.protocol.port;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Map;

/**
 * A physical bus using datagram sockets to send and receive messages.
 * 
 * @see Port
 */
public class DatagramSocketPort implements Port {
   private DatagramSocket inSocket, outSocket;

   @Override
   public void configure(Map<String, Object> configuration) {
      this.inSocket = (DatagramSocket) configuration.get("inSocket");
      this.outSocket = (DatagramSocket) configuration.get("outSocket");
   }

   @Override
   public void start() {
   }

   @Override
   public void stop() {
      if (this.inSocket != null) this.inSocket.close();
      if (this.outSocket != null) this.outSocket.close();
   }

   @Override
   public void send(Message message) throws IOException {
      DatagramSocketMessage m = (DatagramSocketMessage) message;
      this.outSocket.send(new DatagramPacket(m.getContent(), m.getContent().length, m.getDestAddr()));
   }

   @Override
   public Message receive() throws IOException {
      byte[] buffer = new byte[1024];
      DatagramPacket p = new DatagramPacket(buffer, buffer.length);
      this.inSocket.receive(p);
      return new DatagramSocketMessage((InetSocketAddress) this.inSocket.getLocalSocketAddress(), p.getData());
   }
}
