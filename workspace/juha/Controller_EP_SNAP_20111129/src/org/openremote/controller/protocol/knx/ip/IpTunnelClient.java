/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.controller.protocol.knx.ip;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;

import org.openremote.controller.protocol.knx.ip.IpTunnelClientListener.Status;
import org.openremote.controller.protocol.knx.ip.KnxIpException.Code;
import org.openremote.controller.protocol.knx.ip.message.Hpai;
import org.openremote.controller.protocol.knx.ip.message.IpConnectReq;
import org.openremote.controller.protocol.knx.ip.message.IpConnectResp;
import org.openremote.controller.protocol.knx.ip.message.IpConnectionStateReq;
import org.openremote.controller.protocol.knx.ip.message.IpConnectionStateResp;
import org.openremote.controller.protocol.knx.ip.message.IpDisconnectReq;
import org.openremote.controller.protocol.knx.ip.message.IpDisconnectResp;
import org.openremote.controller.protocol.knx.ip.message.IpMessage;
import org.openremote.controller.protocol.knx.ip.message.IpTunnelingAck;
import org.openremote.controller.protocol.knx.ip.message.IpTunnelingReq;
import org.openremote.controller.utils.Logger;


/**
 * TODO
 *
 * @author Olivier Gandit
 */
public class IpTunnelClient implements IpProcessorListener
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * KNX logger. Uses a common category for all KNX related logging.
   */
  private final static Logger log = Logger.getLogger(IpProcessor.KNXIP_LOG_CATEGORY);


  // Instance Fields ------------------------------------------------------------------------------

  private int channelId;
  private int seqCounter;
  private IpTunnelClientListener messageListener;
  private IpProcessor processor;
  private InetSocketAddress destControlEndpointAddr;
  private InetSocketAddress destDataEndpointAddr;
  private Timer heartBeat;
  private Thread shutdownHook;
  private InetAddress srcAddr;


  // Constructors ---------------------------------------------------------------------------------

  public IpTunnelClient(InetAddress srcAddr, InetSocketAddress destControlEndpointAddr,
                        String physicalBusClazz)
  {
    this.srcAddr = srcAddr;
    this.destControlEndpointAddr = destControlEndpointAddr;
    this.processor = new IpProcessor(this, physicalBusClazz);
    this.destDataEndpointAddr = null;
    this.shutdownHook = new ShutdownHook();
    this.heartBeat = new Timer("KNX IP heartbeat");
  }


  // Public Instance Methods ----------------------------------------------------------------------

  public void register(IpTunnelClientListener l)
  {
    this.messageListener = l;
  }

  public void unregister()
  {
    this.messageListener = null;
  }

  public synchronized void service(byte[] message)
      throws KnxIpException, InterruptedException, IOException
  {
    IpMessage resp = this.processor.service(
        new IpTunnelingReq(this.channelId, this.seqCounter, message), this.destDataEndpointAddr
    );


    // Check response

    if (resp == null)
    {
      throw new KnxIpException(Code.NO_RESPONSE_FROM_INTERFACE, "Service failed, no ACK");
    }

    else
    {
      // Handle tunnel ACK, ignore other responses

      if (resp instanceof IpTunnelingAck)
      {
        IpTunnelingAck cr = (IpTunnelingAck) resp;

        if (cr.getChannelId() == this.channelId)
        {
          if (cr.getSeqCounter() == this.seqCounter)
          {
            int st = cr.getStatus();

            if (st != IpTunnelingAck.OK)
            {
              throw new KnxIpException(Code.RESPONSE_ERROR, "Service failed : " + st);
            }
          }

          else
          {
            throw new KnxIpException(
                Code.WRONG_SEQUENCE_COUNTER_VALUE, "Service failed, expected " + this.seqCounter +
                ", got " + cr.getSeqCounter()
            );
          }
        }

        else
        {
          throw new KnxIpException(Code.WRONG_CHANNEL_ID, "Service failed");
        }
      }
    }

    this.seqCounter = (this.seqCounter + 1 & 0xFF);
  }

  public synchronized void connect() throws KnxIpException, InterruptedException, IOException
  {
    if (this.isConnected())
    {
      throw new KnxIpException(Code.ALREADY_CONNECTED, "Connect failed");
    }

    this.processor.start("runtime", this.srcAddr, null);

    Hpai ep = new Hpai(this.processor.getSrcSocketAddr());

    IpMessage resp = this.processor.service(new IpConnectReq(ep, ep), this.destControlEndpointAddr);


    // Check response

    if (resp instanceof IpConnectResp)
    {
      IpConnectResp cr = (IpConnectResp) resp;

      IpConnectResp.Status st = cr.getStatus();

      if (st == IpConnectResp.Status.NO_ERROR)
      {
        // Extract communication channel id

        this.channelId = cr.getChannelId();

        // set destDataEndpointAddr with response HPAI value

        this.destDataEndpointAddr = cr.getDataEndpoint().getAddress();
            
        // Schedule heartbeat every 60s

        this.heartBeat.schedule(new HeartBeatTask(), 0, 60000);

        // Register shutdown hook

        Runtime.getRuntime().addShutdownHook(IpTunnelClient.this.shutdownHook);
            
        // Notify that we are connected

        this.messageListener.notifyInterfaceStatus(Status.connected);

        log.info("Connected to KNX-IP interface " + this.destControlEndpointAddr);
      }

      else
      {
        this.processor.stop();

        throw new KnxIpException(Code.RESPONSE_ERROR, "Connect failed : " + st);
      }
    }

    else
    {
      this.processor.stop();

      throw new KnxIpException(Code.WRONG_RESPONSE_TYPE, "Connect failed");
    }
  }

  public synchronized void disconnect() throws KnxIpException, InterruptedException, IOException
  {
    try
    {
      if (!this.isConnected())
      {
        throw new KnxIpException(Code.NOT_CONNECTED, "Disconnect failed");
      }

      IpDisconnectReq disconnectRequest = new IpDisconnectReq(
          this.channelId, new Hpai(this.processor.getSrcSocketAddr())
      );

      IpMessage resp = this.processor.service(disconnectRequest, this.destDataEndpointAddr);


      // Check response

      if (resp instanceof IpDisconnectResp)
      {
        IpDisconnectResp cr = (IpDisconnectResp) resp;

        if (this.channelId == cr.getChannelId())
        {
          this.terminateConnection();
  

          // Check status anyway

          int st = cr.getStatus();

          if (st != IpDisconnectResp.OK)
          {
            throw new KnxIpException(Code.RESPONSE_ERROR, "Disconnect failed : " + st);
          }
               
          log.info("Disconnected gracefully from " + this.destControlEndpointAddr);
        }

        else
        {
          throw new KnxIpException(Code.WRONG_CHANNEL_ID, "Disconnect failed");
        }
      }

      else
      {
        throw new KnxIpException(Code.WRONG_RESPONSE_TYPE, "Disconnect failed");
      }
    }

    finally
    {
      this.terminateConnection();
    }
  }
   

  public void terminateConnection() throws InterruptedException
  {

    try
    {
      // Unregister shutdown hook  -- TODO: should be executed in a privileged code block

      Runtime.getRuntime().removeShutdownHook(IpTunnelClient.this.shutdownHook);
    }

    catch (IllegalStateException e)
    {
      // This may occur if the JVM has already initiated the shutdown sequence,
      // writing a debug statement but that's all.

      log.debug(
            "Was unable to remove shutdown hook. This may be due to a JVM shutdown" +
            "sequence already started: {0}", e, e.getMessage()
      );
    }

    // Stop heartbeat

    this.heartBeat.cancel();

    // Stop IP processor

    this.processor.stop();

    // Set server date endpoint address to null to force reconnection before sending new values

    this.destDataEndpointAddr = null;

    // notify connection off

    this.messageListener.notifyInterfaceStatus(Status.disconnected);
  }
   

  public boolean isConnected()
  {
    return this.destDataEndpointAddr != null;
  }



  // Implements IpProcessorListener ---------------------------------------------------------------
  
  @Override public void notifyMessage(IpMessage message)
  {
    if (message instanceof IpTunnelingReq)
    {
      IpTunnelingReq req = (IpTunnelingReq) message;

      if (req.getChannelId() == this.channelId)
      {
        int seqCounter = req.getSeqCounter();

        // TODO check seq counter

        // Send ACK
        try
        {
          IpTunnelingAck ack = new IpTunnelingAck(this.channelId, seqCounter, IpTunnelingAck.OK);

          this.processor.send(ack, this.destDataEndpointAddr);
        }

        catch (IOException e)
        {
          // ACK not sent, ignore
        }

        // Notify listener

        IpTunnelClientListener l = this.messageListener;

        if (l != null)
        {
          l.receive(req.getcEmiFrame());
        }
      }

      else
      {
        // TODO send NACK?
      }
    }
  }


  // Inner Classes --------------------------------------------------------------------------------

  private class HeartBeatTask extends TimerTask
  {

    @Override public void run()
    {
      int nbErrs = 0;

      while (nbErrs < 3)
      {
        try
        {
          this.monitor();

          return;

        }

        catch (KnxIpException e)
        {
          log.warn("KNX IP heartbeat request failed", e);
        }

        catch (InterruptedException e)
        {
          Thread.currentThread().interrupt();
        }

        catch (IOException e)
        {
          log.warn("KNX IP heartbeat request failed", e);
        }

        nbErrs++;
      }

      try
      {
        IpTunnelClient.this.disconnect();
      }

      catch (KnxIpException e)
      {
        log.warn("KNX IP heartbeat disconnect request failed", e);
      }

      catch (InterruptedException e)
      {
        Thread.currentThread().interrupt();
      }

      catch (IOException e)
      {
        log.warn("KNX IP heartbeat disconnect request failed", e);
      }
    }


    private void monitor() throws KnxIpException, InterruptedException, IOException
    {
      Hpai ep = new Hpai(IpTunnelClient.this.processor.getSrcSocketAddr());

      IpConnectionStateReq stateRequest = new IpConnectionStateReq(IpTunnelClient.this.channelId, ep);

      IpMessage resp = IpTunnelClient.this.processor.service(
          stateRequest, IpTunnelClient.this.destControlEndpointAddr
      );


      // Check response

      if (resp instanceof IpConnectionStateResp)
      {
        IpConnectionStateResp cr = (IpConnectionStateResp) resp;

        int cId = cr.getChannelId();

        if (cId == IpTunnelClient.this.channelId)
        {
          int st = cr.getStatus();

          if (st != IpConnectResp.OK)
          {
            throw new KnxIpException(Code.RESPONSE_ERROR, "Monitor failed : " + st);
          }
        }

        else
        {
          throw new KnxIpException(Code.WRONG_CHANNEL_ID, "Monitor failed : " + cId);
        }
      }

      else
      {
        throw new KnxIpException(Code.WRONG_RESPONSE_TYPE, "Monitor failed");
      }
    }
  }


  private class ShutdownHook extends Thread
  {
    @Override public void run()
    {
      try
      {
        IpTunnelClient.this.disconnect();
      }

      catch (KnxIpException e)
      {
        // Ignore
      }

      catch (InterruptedException e)
      {
        Thread.currentThread().interrupt();
      }

      catch (IOException e)
      {
        // Ignore
      }
    }
  }
}
