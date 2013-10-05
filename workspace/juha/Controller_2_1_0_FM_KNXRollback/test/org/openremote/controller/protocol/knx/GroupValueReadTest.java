/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
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
package org.openremote.controller.protocol.knx;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;
import org.openremote.controller.component.EnumSensorType;
import org.openremote.controller.protocol.knx.datatype.DataPointType;
import org.openremote.controller.protocol.knx.dib.DeviceInformation;
import org.openremote.controller.protocol.knx.dib.SupportedServiceFamily;
import org.openremote.controller.protocol.knx.ip.message.Hpai;
import org.openremote.controller.protocol.knx.ip.message.IpConnectReq;
import org.openremote.controller.protocol.knx.ip.message.IpConnectResp;
import org.openremote.controller.protocol.knx.ip.message.IpConnectionStateReq;
import org.openremote.controller.protocol.knx.ip.message.IpDisconnectReq;
import org.openremote.controller.protocol.knx.ip.message.IpDisconnectResp;
import org.openremote.controller.protocol.knx.ip.message.IpDiscoverReq;
import org.openremote.controller.protocol.knx.ip.message.IpDiscoverResp;
import org.openremote.controller.protocol.knx.ip.message.IpMessage;
import org.openremote.controller.protocol.knx.ip.message.IpTunnelingAck;
import org.openremote.controller.protocol.knx.ip.message.IpTunnelingReq;
import org.openremote.controller.protocol.port.Message;
import org.openremote.controller.protocol.port.Port;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.utils.Strings;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class GroupValueReadTest
{

  @Test public void test() throws Exception
  {
    KNXIpConnectionManager connectionManager = new KNXIpConnectionManager();

    connectionManager.setPhysicalBusClazz(VirtualKNXGateway.class.getName());

    connectionManager.start();
    connectionManager.getConnection();

    GroupValueRead readCmd = GroupValueRead.createCommand("STATUS",
        connectionManager,
        new GroupAddress("0/0/1"),
        DataPointType.STRING_ASCII
    );

    String value = readCmd.read(EnumSensorType.CUSTOM, null);

    Assert.assertTrue("got null value", value != null);

    Assert.assertTrue("got '" + value + "'", value.equals("foo"));

    connectionManager.stop();
  }


  // Nested Classes -------------------------------------------------------------------------------

  /**
   * This is an in-memory KNX gateway. At the moment it is used as a testing tool. But
   * the implementation could be extended for KNX software multiplexing implementation
   * or in-memory KNX gateway emulator.   <p>
   *
   * The way this implementation works is it implements the abstract "port" interface
   * (used to be known as "physical bus"). This allows the implementation to be plugged
   * into the KNX IP connection manager implementation (the "client-side" of KNX
   * communication). When an invocation to send a KNX UDP frame is made, instead of
   * delegating the frame bytes to a physical UDP network interface, we interpret the
   * bytes and create an in-memory CEMI frame response. So as far as the KNX IP connection
   * manager implementation is concerned, we look like a "real" gateway even though the
   * CEMI frames never reach any physical interface.
   */
  public static class VirtualKNXGateway implements Port
  {

    // Class Members ------------------------------------------------------------------------------

    /**
     * Logging to KNX log category.
     */
    private final static Logger log = Logger.getLogger(KNXCommandBuilder.KNX_LOG_CATEGORY + ".gateway");



    // Instance Fields ----------------------------------------------------------------------------

    /**
     * Queue response frames based on processed send requests here. The receive side will
     * poll and return 'responses' back to listeners.
     */
    private BlockingQueue<IpMessage> responseQueue = new LinkedBlockingQueue<IpMessage>(5000);

    /**
     * Manages connection channels -- channel allocation, ids and channel sequence counters.
     */
    private Channel.Factory channelFactory = Channel.Factory.newInstance();

    /**
     * The virtual 'devices' this gateway responds to.
     */
    private Map<GroupAddress, Device> devices = new HashMap<GroupAddress, Device>();

    /**
     * Flag to indicate the gateway is running.
     */
    private volatile boolean listening = true;


    // Constructors -------------------------------------------------------------------------------

    /**
     * Constructs a new virtual gateway with a set of predefined 'devices' that can be
     * communicated with.
     */
    public VirtualKNXGateway()
    {
      try
      {
        devices.put(new GroupAddress("0/0/1"), new ASCIIStringDevice("foo"));
      }

      catch (Throwable t)
      {
        throw new Error("Failed to initialize Virtual KNX Gateway: " + t.getMessage());
      }
    }



    // Port Overrides -----------------------------------------------------------------------------

    @Override public void configure(Map<String, Object> properties)
    {
      // no-op, we don't have any special configuration needs from the KNX IP connection
      // manager side.
    }

    @Override public void start()
    {
      log.info("Gateway started by [T: {0}]", Thread.currentThread().getName());
    }

    @Override public void stop()
    {
      // set the receiver side response queue polling loop to end -- we are not interrupting
      // the blocking poll directly but it should poll the listening status on a regular
      // basis...

      log.info("[T: {0}] Stopping the gateway....", Thread.currentThread().getName());

      try
      {
        // Give the response queue poller a small breather before we kill shut it down...

        Thread.sleep(200);
      }

      catch (InterruptedException e)
      {
        Thread.currentThread().interrupt();
      }

      listening = false;
    }


    /**
     * This send method is invoked by the KNX connection manager when it expects a KNX frame
     * (encapsulated in the Message instance) to be sent over a physical connection. In here
     * we intercept the frames and handle them 'locally' by generating appropriate responses.
     * No physical connection to a KNX gateway is ever made.
     *
     * @param msg   the KNX frame bytes
     */
    @Override public void send(Message msg) throws IOException
    {
      byte[] content = msg.getContent();

      // Check that we get a valid KNXNet/IP 1.0 frame before continuing...

      if (!IpMessage.isValidFrame(content))
      {
        throw new IOException(IpMessage.getFrameError(content));
      }

      // Are we dealing with a search (gateway discovery) request from the client... ?

      if (IpDiscoverReq.isSearchRequest(content))
      {
        IpDiscoverReq knxDiscoveryRequest = new IpDiscoverReq(content);

        responseQueue.add(createKNXGatewaySearchResponse(knxDiscoveryRequest));
      }

      // Are we receiving a KNX connect request...?

      else if (IpConnectReq.isConnectRequest(content))
      {
        IpConnectReq knxConnectRequest = new IpConnectReq(content);

        responseQueue.add(createKNXConnectionResponse(knxConnectRequest));
      }

      // Are we receiving heart beat (connection state) request from client... ?

      else if (IpConnectionStateReq.isConnectionStateRequest(content))
      {
        IpConnectionStateReq knxConnectionStateRequest = new IpConnectionStateReq(content);

        responseQueue.add(createKNXConnectionStateResponse(knxConnectionStateRequest));
      }

      // Did we receive a CEMI tunneling request...?

      else if (IpTunnelingReq.isTunnelingRequest(content))
      {
        IpTunnelingReq knxTunnelingRequest = new IpTunnelingReq(content);

        CommonEMI requestCEMI = new CommonEMI(knxTunnelingRequest.getcEmiFrame());

        GroupAddress deviceGroupAddress = requestCEMI.getDestinationAddress();
        GroupAddress sourceGroupAddress = requestCEMI.getSourceAddress();

        Device device = devices.get(deviceGroupAddress);

        if (device == null)
        {
          // Don't ACK if we don't have a device for the given destination address

          return;
        }

        IpTunnelingAck ack = knxTunnelingRequest.createAckResponse();

        log.info("[T: {0}] Queuing acknowledgment frame {1}", Thread.currentThread().getName(), ack);

        responseQueue.add(ack);

        CommonEMI responseCEMI = new CommonEMI(
            DataLink.MessageCode.DATA_INDICATE_BYTE,
            deviceGroupAddress,
            device.getResponseAPDU()
        );

        IpTunnelingReq responseTunnelingRequest = knxTunnelingRequest.createDataResponse(
            channelFactory.getChannel(knxTunnelingRequest.getChannelId()),
            responseCEMI
        );

        log.info("[T: {0}] Queuing CEMI response frame {0}", Thread.currentThread().getName(), responseTunnelingRequest);

        responseQueue.add(responseTunnelingRequest);
      }

      else if (IpDisconnectReq.isDisconnectRequest(content))
      {
        // TODO :
        //   doing direct byte manipulation here since the disconnect requests have not
        //   been refactored for a more useful API yet.
        //                                                                    [JPL]

        int channelID = content[6] & 0xFF;

        Channel channel  = channelFactory.getChannel(channelID);

        if (channel == null)
        {
          // We don't have the channel they're wanting to close...

          log.info("Request to close channel {0} which did not exist. Ignoring request...");

          return;
        }

        log.info("Disconnect request for channel {0}.", channel.getIdentifier());

        channel.close();

        byte[] disconnectResponse = new byte[] { (byte)channelID, 0x00 /* status ok */ };

        responseQueue.add(new IpDisconnectResp(new ByteArrayInputStream(disconnectResponse), 2));

        log.info("Closing channel {0}", channelID);
      }

      else if (IpTunnelingAck.isTunnelingAcknowledgement(content))
      {
          // TODO :
          //   Not tracking at the moment whether our data indicates are ack'ed
          //   and thus the frame re-send is not implemented
      }

      else
      {
        StringBuilder builder = new StringBuilder();
        builder.append("what's this : ");

        for (byte b : content)
        {
          builder.append(Strings.byteToUnsignedHexString(b));
          builder.append(" ");
        }

        System.out.println(builder);
      }

    }


    /**
     * The receive method is invoked by the KNX bus listener to receive incoming data indicate
     * messages, acknowledgements and so on.  <p>
     *
     * In our case we just use it to feed our response packets directly without having a
     * physical connection to the network. The send-side processing can insert messages
     * into the response queue which we will return back to the KNX bus listener as "received"
     * frames when requested.
     *
     * @return  KNX frame message
     *
     */
    @Override public Message receive() throws IOException
    {
      IpMessage response;

      while (listening)
      {
        try
        {
          response = responseQueue.poll(100, TimeUnit.MILLISECONDS);

          if (!listening)
          {
            break;
          }

          if (response != null)
          {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            BufferedOutputStream bout = new BufferedOutputStream(bytes);

            response.write(bout);
            bout.flush();
            bout.close();

            log.info("Received: {0}", Strings.byteArrayToUnsignedHexString(bytes.toByteArray()));

            return new Message(bytes.toByteArray());
          }
        }

        catch (IOException e)
        {
           log.error("I/O error when writing response: {0}", e, e.getMessage());
        }

        catch (InterruptedException e)
        {
          Thread.currentThread().interrupt();

          listening = false;

          log.info("[T: {0}] Response loop was interrupted. Exiting....", Thread.currentThread().getName());
        }
      }

      // TODO:
      //   There should be a better way to communicate a scheduled close back to the caller.
      //   The exception below is chosen because it creates the least noise for now.
      //                                                                                [JPL]

      throw new IOException("Receiver has been closed.");
    }



    // Private Instance Methods -------------------------------------------------------------------

    /**
     * Helper method to process connection state requests ("heartbeat") and create a response
     * to it.
     *
     * @param knxConnectionStateRequest  the incoming KNX connection state request
     *
     * @return  KNX connection state response with ok or error status. Error status will be
     *          created if a connection state request is sent with an unrecognized channel id.
     */
    private IpMessage createKNXConnectionStateResponse(IpConnectionStateReq knxConnectionStateRequest)
    {
      log.info("Received connection state request: {0}", knxConnectionStateRequest);

      int channelID = knxConnectionStateRequest.getChannelId();

      if (channelFactory.getChannel(channelID) != null)
      {
        return knxConnectionStateRequest.createNoErrorResponse();
      }

      else
      {
        return knxConnectionStateRequest.createConnectionIDErrorResponse();
      }
    }

    /**
     * Helper method to process KNX connection requests and creating connection responses.
     *
     * @param knxConnectionRequest  the incoming KNX connection request
     *
     * @return    A KNX connection response with a newly allocated channel identifier. Since
     *            the virtual gateway doesn't have a real IP address it responds to, we return
     *            a HPAI with "any" IP address and a zero port. The individual address we use
     *            for the gateway is the same as the channel ID.
     */
    private IpMessage createKNXConnectionResponse(IpConnectReq knxConnectionRequest)
    {
      log.info("KNX Connection request : {0}", knxConnectionRequest);

      try
      {
        Channel channel = channelFactory.newChannel();

        return new IpConnectResp(

            channel, IpConnectResp.Status.NO_ERROR,

            // The actual physical IP has no meaning for us, the packets will never reach
            // a NIC even on the client side. So just returning 'any' address with random
            // (zero) port assignment...

            new Hpai(new InetSocketAddress("0.0.0.0", 0)),

            // Arbitrarily using the same individual address and channel ID -- not that it
            // should make a whole lot of difference...

            new IndividualAddress(channel.getIdentifier())
        );
      }

      catch (ConnectionException e)
      {
        // Individual address used in case of connection errors.
        //
        //  TODO :
        //     Should double check if there are individual address values that are
        //     reserved for special purposes.

        final IndividualAddress ERROR_ADDRESS = new IndividualAddress(0xFFFF);

        return new IpConnectResp(

            Channel.ERROR_CHANNEL,

            IpConnectResp.Status.NO_MORE_CONNECTIONS_ERROR,

            // This is the server's data endpoint HPAI. Since we can't allocate a channel ID,
            // any address with port zero seems like a good value for the error case...

            new Hpai(new InetSocketAddress("0.0.0.0", 0)),

            ERROR_ADDRESS
        );
      }

    }


    /**
     * Helper method to process KNX discovery requests and creating discovery responses.
     *
     * @param knxDiscoveryRequest   the incoming KNX discovery request
     *
     * @return    KNX discovery response advertizing this gateway
     */
    private IpMessage createKNXGatewaySearchResponse(IpDiscoverReq knxDiscoveryRequest)
    {
      log.info("Responding to search request: {0}", knxDiscoveryRequest);

      final String LOCALHOST = "127.0.0.1";

      try
      {
        InetSocketAddress address = new InetSocketAddress("1.2.3.4", 1234);

        Map<ServiceTypeIdentifier.Family, Integer> services =
            new HashMap<ServiceTypeIdentifier.Family, Integer>();
        services.put(ServiceTypeIdentifier.Family.CORE, 10);

        Hpai serverControlHPAI = new Hpai(address);

        DeviceInformation deviceInfo = new DeviceInformation(
            "Test Gateway", DeviceInformation.KNXMedium.TP0, new IndividualAddress(0x101),
            NetworkInterface.getByInetAddress(InetAddress.getByName(LOCALHOST)),
            1, 1, 1
        );

        SupportedServiceFamily supportedServices = new SupportedServiceFamily(services);

        return new IpDiscoverResp(serverControlHPAI, deviceInfo, supportedServices);
      }

      catch (UnknownHostException e)
      {
        throw new Error("Cannot resolve host " + LOCALHOST);
      }

      catch (SocketException e)
      {
        throw new Error("Cannot resolve host " + LOCALHOST);
      }
    }


    // Inner Classes ------------------------------------------------------------------------------

    abstract class Device
    {
      abstract ApplicationProtocolDataUnit getResponseAPDU();
    }

    /**
     * Represents KNX ASCII string (DPT 16.000) device that returns a fixed string value.
     */
    public class ASCIIStringDevice extends Device
    {
      private String responseValue;

      ASCIIStringDevice(String responseValue)
      {
        this.responseValue = responseValue;
      }
      @Override ApplicationProtocolDataUnit getResponseAPDU()
      {
        return ApplicationProtocolDataUnit.createKNXASCIIStringResponse(responseValue);
      }
    }
  }



}

