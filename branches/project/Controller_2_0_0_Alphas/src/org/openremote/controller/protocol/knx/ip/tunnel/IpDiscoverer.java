package org.openremote.controller.protocol.knx.ip.tunnel;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;

import org.openremote.controller.protocol.knx.ip.tunnel.message.Hpai;
import org.openremote.controller.protocol.knx.ip.tunnel.message.IpDiscoverReq;

public class IpDiscoverer {
   private IpProcessor processor;
   private MulticastSocket multicastSocket;
   private InetSocketAddress groupAddr;
   private DiscoveryListener discoveryListener;

   public IpDiscoverer(InetAddress srcAddr, DiscoveryListener discoveryListener) {
      try {
         this.groupAddr = new InetSocketAddress(InetAddress.getByName("224.0.23.12"), 3671);
      } catch (UnknownHostException e) {
         // Ignore
      }
      if (!(srcAddr instanceof Inet4Address)) {
         throw new InvalidParameterException("Only IPV4 addresses are supported");
      }
      this.discoveryListener = discoveryListener;
      this.processor = new IpProcessor(srcAddr);
   }

   public void start() throws KnxIpException, IOException, InterruptedException {
      this.multicastSocket = new MulticastSocket();
      this.multicastSocket.joinGroup(this.groupAddr.getAddress());

      this.processor.setDiscoverer(this);
      this.processor.start();

      // Start discovery process
      this.processor.send(this.multicastSocket, new IpDiscoverReq(new Hpai(this.processor.getSrcAddr())),
            this.groupAddr);
   }

   public void stop() throws KnxIpException, InterruptedException, IOException {
      this.multicastSocket.close();
   }

   void notifyDiscovery() {
      this.discoveryListener.notifyDiscovery(this, new IpClientImpl(this.processor));
   }
}
