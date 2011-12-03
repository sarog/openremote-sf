package org.openremote.controller.protocol.knx.ip;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import org.openremote.controller.protocol.bus.DatagramSocketMessage;
import org.openremote.controller.protocol.bus.Message;
import org.openremote.controller.protocol.bus.PhysicalBus;
import org.openremote.controller.protocol.knx.ip.message.IpConnectReq;
import org.openremote.controller.protocol.knx.ip.message.IpConnectionStateReq;
import org.openremote.controller.protocol.knx.ip.message.IpDisconnectReq;
import org.openremote.controller.protocol.knx.ip.message.IpDiscoverReq;
import org.openremote.controller.protocol.knx.ip.message.IpTunnelingReq;

// TODO add a KNX response send mechanism
public class PhysicalBusMock implements PhysicalBus {
   private static final byte[] DISCOVER_RESP = new byte[] { 0x06, 0x10, 0x02, 0x02, 0x00, 0x0E, 0x08, 0x01, 127, 0, 0,
         1, 0x0E, 0x56, 0, 0 };
   private static final byte[] CONNECT_RESP = new byte[] { 0x06, 0x10, 0x02, 0x06, 0x00, 0x12, 0x15, 0x00, 0x08, 0x01,
         127, 0, 0, 1, 0x0E, 0x56, 0x04, 0x04, 0x11, 0x0A };
   private static final byte[] DISCONNECT_RESP = new byte[] { 0x06, 0x10, 0x02, 0x0A, 0x00, 0x08, 0x15, 0x00 };
   private static final byte[] CONNECTIONSTATE_RESP = new byte[] { 0x06, 0x10, 0x02, 0x08, 0x00, 0x08, 0x15, 0x00 };
   private static final byte[] TUNNELING_ACK = new byte[] { 0x06, 0x10, 0x04, 0x21, 0x00, 0x0A, 0x04, 0x15, 0x00, 0x00 };
   private byte[] resp;
   private InetSocketAddress srcAddr;

   @Override
   public void start(Object inChannel, Object outChannel) {
      this.srcAddr = (InetSocketAddress) ((DatagramSocket) inChannel).getLocalSocketAddress();
   }

   @Override
   public void stop() {
   }

   @Override
   public void send(Message message) throws IOException {
      byte[] req = message.getContent();
      int sti = (req[2] << 8) + req[3];
      switch (sti) {
      case IpDiscoverReq.STI:
         this.sendResponse(DISCOVER_RESP);
         break;
      case IpConnectReq.STI:
         this.sendResponse(CONNECT_RESP);
         break;
      case IpDisconnectReq.STI:
         this.sendResponse(DISCONNECT_RESP);
         break;
      case IpConnectionStateReq.STI:
         this.sendResponse(CONNECTIONSTATE_RESP);
         break;
      case IpTunnelingReq.STI:
         byte[] resp = TUNNELING_ACK;
         resp[8] = req[8];
         this.sendResponse(TUNNELING_ACK);
         break;
      }
   }

   private void sendResponse(byte[] message) {
      synchronized (this) {
         this.resp = message;
         this.notify();
      }
   }

   @Override
   public Message receive() throws IOException {
      synchronized (this) {
         try {
            this.wait();
            return new DatagramSocketMessage(this.srcAddr, this.resp);
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
         }
      }
      return null;
   }
}
