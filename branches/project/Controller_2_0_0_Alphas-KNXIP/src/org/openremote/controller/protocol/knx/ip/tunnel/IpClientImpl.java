package org.openremote.controller.protocol.knx.ip.tunnel;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.openremote.controller.protocol.knx.ip.tunnel.message.Hpai;
import org.openremote.controller.protocol.knx.ip.tunnel.message.IpConnectReq;
import org.openremote.controller.protocol.knx.ip.tunnel.message.IpConnectAck;
import org.openremote.controller.protocol.knx.ip.tunnel.message.IpDisconnectReq;
import org.openremote.controller.protocol.knx.ip.tunnel.message.IpDisconnectAck;
import org.openremote.controller.protocol.knx.ip.tunnel.message.IpMessage;
import org.openremote.controller.protocol.knx.ip.tunnel.message.IpTunnelingReq;
import org.openremote.controller.protocol.knx.ip.tunnel.message.IpTunnelingAck;

public class IpClientImpl implements IpClient {
   private Integer channelId;
   private int seqCounter;
   private IpMessageListener messageListener;
   private IpProcessor processor;

   IpClientImpl(IpProcessor processor) {
      this.processor = processor;
      this.processor.setClient(this);
   }

   public IpClientImpl(InetSocketAddress srcAddr, InetSocketAddress destAddr) throws KnxIpException, IOException,
         InterruptedException {
      this.processor = new IpProcessor(srcAddr, destAddr);
      this.processor.start();
      this.processor.setClient(this);
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * org.openremote.controller.protocol.knx.ip.tunnel.IpClient#register(org.openremote.controller.protocol.knx.ip.tunnel
    * .IpMessageListener)
    */
   @Override
   public void register(IpMessageListener l) {
      this.messageListener = l;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.openremote.controller.protocol.knx.ip.tunnel.IpClient#unregister()
    */
   @Override
   public void unregister() {
      this.messageListener = null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.openremote.controller.protocol.knx.ip.tunnel.IpClient#send(byte[])
    */
   @Override
   public void send(byte[] message) throws KnxIpException, InterruptedException, IOException {
      if (this.channelId == null) this.connect();

      IpMessage resp = this.processor.unicastSyncSend(new IpTunnelingReq(this.channelId, this.seqCounter, message));

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
                  throw new KnxIpException("Response wrong sequence counter value, expected " + this.seqCounter
                        + ", got " + cr.getSeqCounter());
                  // TODO reset?
               }
            } else {
               throw new KnxIpException("Response wrong channel id");
               // TODO reset?
            }
         }
      }

      this.seqCounter++;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.openremote.controller.protocol.knx.ip.tunnel.IpClient#connect()
    */
   @Override
   public void connect() throws KnxIpException, InterruptedException, IOException {
      Hpai ep = new Hpai(this.processor.getSrcAddr());
      IpMessage resp = this.processor.unicastSyncSend(new IpConnectReq(ep, ep));

      // Check response
      if (resp instanceof IpConnectAck) {
         IpConnectAck cr = (IpConnectAck) resp;
         int st = cr.getStatus();
         if (st == IpConnectAck.OK) {
            // Extract communication channel id
            this.channelId = cr.getChannelId();

            // set IpProcessor destDataEndpointAddr with server HPAI value
            this.processor.setDestDataEndpointAddr(cr.getDataEndpoint().getAddress());
         } else {
            throw new KnxIpException("Connect request response error : " + st);
         }
      } else {
         throw new KnxIpException("Unexpected response for connect request");
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.openremote.controller.protocol.knx.ip.tunnel.IpClient#disconnect()
    */
   @Override
   public void disconnect() throws KnxIpException, InterruptedException, IOException {
      IpMessage resp = this.processor.unicastSyncSend(new IpDisconnectReq(this.channelId, new Hpai(this.processor
            .getSrcAddr())));

      // Check response
      if (resp instanceof IpDisconnectAck) {
         IpDisconnectAck cr = (IpDisconnectAck) resp;
         if (this.channelId == cr.getChannelId()) {
            int st = cr.getStatus();
            if (st != IpDisconnectAck.OK) {
               throw new KnxIpException("Response error : " + st);
            } else {
               this.processor.stop();
            }
         } else {
            throw new KnxIpException("Wrong channel id");
         }
      } else {
         throw new KnxIpException("Unexpected response type");
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.openremote.controller.protocol.knx.ip.tunnel.IpClient#getSrcAddr()
    */
   @Override
   public InetSocketAddress getSrcAddr() {
      return this.processor.getSrcAddr();
   }

   IpTunnelingAck receive(IpTunnelingReq req) {
      if (req.getChannelId() == this.channelId) {
         int seqCounter = req.getSeqCounter();
         // TODO check seq counter
         IpMessageListener l = this.messageListener;
         if (l != null) {
            l.receive(req.getcEmiFrame());
         }
         return new IpTunnelingAck(this.channelId, seqCounter, IpTunnelingAck.OK);
      } else {
         return null;
      }
   }
}
