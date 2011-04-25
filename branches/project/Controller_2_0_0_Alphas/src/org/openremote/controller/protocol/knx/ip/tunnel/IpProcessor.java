package org.openremote.controller.protocol.knx.ip.tunnel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.InvalidParameterException;

import org.openremote.controller.protocol.knx.ip.tunnel.message.IpConnectAck;
import org.openremote.controller.protocol.knx.ip.tunnel.message.IpDisconnectAck;
import org.openremote.controller.protocol.knx.ip.tunnel.message.IpDiscoverAck;
import org.openremote.controller.protocol.knx.ip.tunnel.message.IpMessage;
import org.openremote.controller.protocol.knx.ip.tunnel.message.IpMessage.Primitive;
import org.openremote.controller.protocol.knx.ip.tunnel.message.IpTunnelingReq;
import org.openremote.controller.protocol.knx.ip.tunnel.message.IpTunnelingAck;

public class IpProcessor {
   private DatagramSocket socket;
   private Object syncLock;
   private IpMessage con;
   private IpListener listener;
   private InetSocketAddress srcAddr;
   private InetSocketAddress destControlEndpointAddr;
   private InetSocketAddress destDataEndpointAddr;
   private IpDiscoverer discoverer;
   private IpClientImpl client;

   public class IpListener extends Thread {
      private byte[] buffer;

      public IpListener() {
         super("IpListener for " + IpProcessor.this.srcAddr);
         this.buffer = new byte[1024];
      }

      @Override
      public void run() {
         DatagramPacket p = new DatagramPacket(this.buffer, this.buffer.length);
         synchronized (this) {
            this.notify();
         }
         while (!this.isInterrupted()) {
            try {
               IpProcessor.this.socket.receive(p);
               // TODO Check sender address?

               // Create an IpMessage from received data
               IpMessage m = IpProcessor.this.create(new ByteArrayInputStream(p.getData()));

               // Handle Discovery responses specifically
               if (m instanceof IpDiscoverAck) {
                  IpProcessor.this.destControlEndpointAddr = ((IpDiscoverAck) m).getControlEndpoint().getAddress();
                  IpDiscoverer d = IpProcessor.this.discoverer;
                  if (d != null) {
                     d.notifyDiscovery();
                  }
               } else {
                  // Handle other messages
                  if (m.getPrimitive() == Primitive.ACK) {

                     // Handle ACKs
                     synchronized (IpProcessor.this.syncLock) {
                        IpProcessor.this.con = m;
                        IpProcessor.this.syncLock.notify();
                     }
                  } else {
                     // Handle requests
                     if (m instanceof IpTunnelingReq) {
                        IpProcessor.this.receive((IpTunnelingReq) m);
                     } else {
                        // Ignore others
                     }
                  }
               }
            } catch (IOException x) {
               // Socket read error, ignore
               Thread.currentThread().interrupt();
            } catch (KnxIpException x) {
               // Invalid message, ignore
            } catch (Throwable t) {
               // TODO Ignore?
            }
         }
      }
   }

   private IpProcessor() {
      this.syncLock = new Object();
   }

   IpProcessor(InetAddress srcAddr) {
      this();
      if (!(srcAddr instanceof Inet4Address)) {
         throw new InvalidParameterException("Only IPV4 addresses are supported");
      }
      this.srcAddr = new InetSocketAddress(srcAddr, 0);
      this.destControlEndpointAddr = null;
      this.destDataEndpointAddr = null;
   }

   public IpProcessor(InetSocketAddress srcAddr, InetSocketAddress destControlEndpointAddr) {
      this();
      this.srcAddr = srcAddr;
      this.destControlEndpointAddr = destControlEndpointAddr;
   }

   public void start() throws KnxIpException, IOException, InterruptedException {
      this.socket = new DatagramSocket();
      this.srcAddr = new InetSocketAddress(this.srcAddr.getAddress(), this.socket.getLocalPort());
      this.listener = new IpListener();
      synchronized (this.listener) {
         this.listener.start();
         this.listener.wait();
      }
   }

   public void stop() throws KnxIpException, InterruptedException, IOException {
      // Close socket
      this.socket.close();

      // Stop IpListener
      this.listener.interrupt();
      this.listener.join();
   }

   public InetSocketAddress getSrcAddr() {
      return this.srcAddr;
   }

   public InetSocketAddress getDestControlEndpointAddr() {
      return this.destControlEndpointAddr;
   }

   public IpMessage unicastSyncSend(IpMessage m) throws InterruptedException, IOException, KnxIpException {
      IpMessage out = null;
      synchronized (this.syncLock) {
         this.con = null;
         if (m instanceof IpTunnelingReq) {
            this.dataEndpointSend(m);
         } else {
            this.controlEndpointSend(m);
         }
         long dt = 0;
         while (out == null && dt < m.getSyncSendTimeout()) {
            long st = System.currentTimeMillis();
            this.syncLock.wait(m.getSyncSendTimeout() - dt);
            dt += System.currentTimeMillis() - st;
            if (this.con != null && !(con instanceof IpDiscoverAck)) out = this.con;
         }
      }
      return out;
   }

   public void controlEndpointSend(IpMessage m) throws IOException, KnxIpException {
      if (this.destControlEndpointAddr == null) throw new KnxIpException("No KNX control endpoint configured");
      this.send(this.socket, m, this.destControlEndpointAddr);
   }

   public void dataEndpointSend(IpMessage m) throws IOException, KnxIpException {
      if (this.destDataEndpointAddr == null) throw new KnxIpException("No KNX data endpoint configured");
      this.send(this.socket, m, this.destDataEndpointAddr);
   }

   void send(DatagramSocket s, IpMessage m, InetSocketAddress address) throws IOException {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      m.write(os);
      byte[] b = os.toByteArray();
      s.send(new DatagramPacket(b, b.length, address));
   }

   void setDiscoverer(IpDiscoverer discoverer) {
      this.discoverer = discoverer;
   }

   void setClient(IpClientImpl client) {
      this.client = client;
   }
   
   void setDestDataEndpointAddr(InetSocketAddress destDataEndpointAddr) {
      this.destDataEndpointAddr = destDataEndpointAddr;
   }

   private IpMessage create(InputStream is) throws IOException, KnxIpException {
      IpMessage out = null;

      // Check header is 0x06 0x10
      if ((is.read() != 0x06) || (is.read() != 0x10)) throw new KnxIpException("Invalid message header");

      // Extract Service Type Identifier
      int sti = (is.read() << 8) + is.read();

      // Extract message length
      int l = ((is.read() << 8) + is.read()) - 6;

      // Instantiate message
      switch (sti) {
      case IpConnectAck.STI:
         out = new IpConnectAck(is, l);
         break;
      case IpDisconnectAck.STI:
         out = new IpDisconnectAck(is, l);
         break;
      case IpDiscoverAck.STI:
         out = new IpDiscoverAck(is, l);
         break;
      case IpTunnelingAck.STI:
         out = new IpTunnelingAck(is, l);
         break;
      case IpTunnelingReq.STI:
         out = new IpTunnelingReq(is, l);
         break;
      default:
         throw new KnxIpException("Unexpected message service type");
      }
      return out;
   }

   private void receive(IpTunnelingReq req) throws KnxIpException, IOException {
      IpClientImpl client = this.client;
      IpTunnelingAck ack = null;
      if (client != null) {
         ack = client.receive(req);
      }
      if (ack != null) this.dataEndpointSend(ack);
   }
}
