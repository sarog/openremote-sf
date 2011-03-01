package org.openremote.controller.protocol.knx.ip.tunnel;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.openremote.controller.protocol.knx.ip.tunnel.message.Hpai;
import org.openremote.controller.protocol.knx.ip.tunnel.message.IpConnectResp;
import org.openremote.controller.protocol.knx.ip.tunnel.message.IpConnectReq;
import org.openremote.controller.protocol.knx.ip.tunnel.message.IpDisconnectResp;
import org.openremote.controller.protocol.knx.ip.tunnel.message.IpDisconnectReq;
import org.openremote.controller.protocol.knx.ip.tunnel.message.IpMessage;
import org.openremote.controller.protocol.knx.ip.tunnel.message.IpTunnelingAck;
import org.openremote.controller.protocol.knx.ip.tunnel.message.IpTunnelingReq;

public class IpClient implements IpProcessorListener {
   private int channelId;
   private int seqCounter;
   private IpMessageListener messageListener;
   private IpProcessor processor;
   private InetSocketAddress destControlEndpointAddr;
   private InetSocketAddress destDataEndpointAddr;

   public IpClient(InetAddress srcAddr, InetSocketAddress destControlEndpointAddr) {
      this.destControlEndpointAddr = destControlEndpointAddr;
      this.processor = new IpProcessor(srcAddr, this);
      this.destDataEndpointAddr = null;
   }

   public void register(IpMessageListener l) {
      this.messageListener = l;
   }

   public void unregister() {
      this.messageListener = null;
   }

   public synchronized void send(byte[] message) throws KnxIpException, InterruptedException, IOException {
      if (this.destDataEndpointAddr == null) this.connect();

      IpMessage resp = this.processor.unicastSyncSend(new IpTunnelingReq(this.channelId, this.seqCounter, message),
            this.destDataEndpointAddr);

      // Check response
      if (resp == null) {
         throw new KnxIpException("No response");
      } else {
         if (resp instanceof IpTunnelingAck) {
            IpTunnelingAck cr = (IpTunnelingAck) resp;
            if (cr.getChannelId() == this.channelId) {
               if (cr.getSeqCounter() == this.seqCounter) {
                  int st = cr.getStatus();
                  if (st != IpTunnelingAck.OK) {
                     throw new KnxIpException("Response error : " + st);
                  }
               } else {
                  this.disconnect();
                  throw new KnxIpException("Tunnel failed, response wrong sequence counter value, expected "
                        + this.seqCounter + ", got " + cr.getSeqCounter());
               }
            } else {
               this.disconnect();
               throw new KnxIpException("Tunnel failed, response wrong channel id");
            }
         }
      }

      this.seqCounter++;
   }

   public synchronized void connect() throws KnxIpException, InterruptedException, IOException {
      if (this.destDataEndpointAddr != null) throw new KnxIpException("Already connected");
      this.processor.start();
      Hpai ep = new Hpai(this.processor.getSrcAddr());
      IpMessage resp = this.processor.unicastSyncSend(new IpConnectReq(ep, ep), this.destControlEndpointAddr);

      // Check response
      if (resp instanceof IpConnectResp) {
         IpConnectResp cr = (IpConnectResp) resp;
         int st = cr.getStatus();
         if (st == IpConnectResp.OK) {
            // Extract communication channel id
            this.channelId = cr.getChannelId();

            // set destDataEndpointAddr with response HPAI value
            this.destDataEndpointAddr = cr.getDataEndpoint().getAddress();
         } else {
            throw new KnxIpException("Connect failed, response error : " + st);
         }
      } else {
         throw new KnxIpException("Connect failed, unexpected response");
      }
   }

   public synchronized void disconnect() throws KnxIpException, InterruptedException, IOException {
      if (this.destDataEndpointAddr == null) throw new KnxIpException("Not connected");
      IpMessage resp = this.processor.unicastSyncSend(
            new IpDisconnectReq(this.channelId, new Hpai(this.processor.getSrcAddr())), this.destDataEndpointAddr);

      // Check response
      if (resp instanceof IpDisconnectResp) {
         IpDisconnectResp cr = (IpDisconnectResp) resp;
         if (this.channelId == cr.getChannelId()) {
            int st = cr.getStatus();
            if (st != IpDisconnectResp.OK) {
               throw new KnxIpException("Response error : " + st);
            } else {
               this.processor.stop();

               // Set server date endpoint address to null to force reconnection before sending new values
               this.destDataEndpointAddr = null;
            }
         } else {
            throw new KnxIpException("Disconnect failed, response wrong channel id");
         }
      } else {
         throw new KnxIpException("Disconnect failed, unexpected response");
      }
   }

   @Override
   public void notifyMessage(IpMessage message) {
      if (message instanceof IpTunnelingReq) {
         IpTunnelingReq req = (IpTunnelingReq) message;
         if (req.getChannelId() == this.channelId) {
            int seqCounter = req.getSeqCounter();
            // TODO check seq counter
            IpMessageListener l = this.messageListener;
            if (l != null) {
               l.receive(req.getcEmiFrame());
            }
            try {
               this.processor.send(new IpTunnelingAck(this.channelId, seqCounter, IpTunnelingAck.OK),
                     this.destDataEndpointAddr);
            } catch (IOException e) {
               // ACK not sent, ignore
            }
         }
      }
   }
}
