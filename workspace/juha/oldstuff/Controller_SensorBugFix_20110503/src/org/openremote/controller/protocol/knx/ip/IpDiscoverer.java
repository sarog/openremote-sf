package org.openremote.controller.protocol.knx.ip;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import org.openremote.controller.protocol.knx.ip.message.Hpai;
import org.openremote.controller.protocol.knx.ip.message.IpDiscoverReq;
import org.openremote.controller.protocol.knx.ip.message.IpDiscoverResp;
import org.openremote.controller.protocol.knx.ip.message.IpMessage;

public class IpDiscoverer implements IpProcessorListener {
   private IpProcessor processor;
   private MulticastSocket multicastSocket;
   private DiscoveryListener discoveryListener;

   public IpDiscoverer(InetAddress srcAddr, DiscoveryListener discoveryListener) {
      this.discoveryListener = discoveryListener;
      this.processor = new IpProcessor(srcAddr, this);
   }

   public void start() throws KnxIpException, IOException, InterruptedException {
      this.multicastSocket = new MulticastSocket();
      try {
         InetSocketAddress groupAddr = new InetSocketAddress(InetAddress.getByName("224.0.23.12"), 3671);
         this.multicastSocket.joinGroup(groupAddr.getAddress());

         this.processor.start();

         // Start discovery process
         // TODO send regularly requests until a response is received
         this.processor.send(new IpDiscoverReq(new Hpai(this.processor.getSrcAddr())), groupAddr, this.multicastSocket);
      } catch (UnknownHostException e) {
         throw new KnxIpException(e.getMessage());
      }
   }

   public void stop() throws InterruptedException {
      this.multicastSocket.close();

      this.processor.stop();
   }

   @Override
   public void notifyMessage(IpMessage message) {
      // Handle discovery responses only, ignore other messages
      if (message instanceof IpDiscoverResp) {
         this.discoveryListener.notifyDiscovery(this, ((IpDiscoverResp) message).getControlEndpoint().getAddress());
      }
   }

   public InetAddress getSrcAddr() {
      return this.processor.getSrcAddr().getAddress();
   }

}
