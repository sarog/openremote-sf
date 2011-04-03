/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.controller.protocol.knx;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;

import tuwien.auto.calimero.knxnetip.Discoverer;
import tuwien.auto.calimero.knxnetip.KNXnetIPTunnel;
import tuwien.auto.calimero.knxnetip.servicetype.SearchResponse;
import tuwien.auto.calimero.knxnetip.util.HPAI;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.IndividualAddress;
import tuwien.auto.calimero.cemi.CEMILData;
import org.apache.log4j.Logger;

/**
 * The goal of the KNXConnectionManager is to:
 *
 * 1) Isolate the OpenRemote event system from the underlying KNX IP wire protocol library types.
 * 2) Provide a uniform connection management interface for the underlying KNX IP connection
 * 3) Create a higher level functional API on top of the KNX IP wire protocol details
 *
 *
 * <h3>Type Isolation</h3>
 *
 * By isolating the rest of the OpenRemote system from the underlying KNX IP wire library API we
 * enable more transparent upgrades of the wire protocol implementations. Therefore issues with
 * backwards incompatible API development or replacing the KNX IP wire library should not impact
 * the rest of the system.  The KNXConnectionManager implementation should work to prevent type
 * leakage from the underlying wire library implementation to the rest of the system.
 *
 *
 * <h3>Connection Management</h3>
 *
 * Connection management functionality is implemented to deal with the scarce number of KNX
 * connections that are available to common KNX IP gateways. KNX specification requires each
 * IP connection to the gateway to be mapped to a KNX individual address (a.k.a physical address)
 * which is commissioned with ETS [1]. In practice many KNX IP gateways only support commissioning
 * of one or few such individual addresses. This means that the available connections must be
 * shared with all external devices wishing to access the KNX bus. Therefore the connection
 * management functionality should support creation of various policies to implement connection
 * sharing.
 *
 * TODO : connection policies, aggressive, yielding, managed
 *
 *
 * <h3>Functional API</h3>
 *
 * TODO  : see KNXConnection interface
 *
 * [1] TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
class KNXConnectionManager
{

  /*
   * Implementation Notes:
   *
   *  - The current wire protocol library API is Calimero 2.0 Alpha 4. This connection manager
   *    implementation should attempt to prevent any type leakage from Calimero to the rest of
   *    the system to make it easier to upgrade (Calimero breaks API compatibility between 1.x
   *    and 2.x versions and current 2.x implementation is still considered alpha) or switch
   *    the implementation outright if necessary.
   *
   *  - The so-called "connection management" in this first iteration is a simplistic single
   *    permanent connection to IP gateway which is shared by all connection manager clients.
   *    For all KNX IP gateways that only support single individual address comissioned to
   *    incoming connection this means that the ORB hosting this connection manager becomes the
   *    single owner of the IP gateway. Less aggressive connection policies are on the to-do list.
   *
   *  - The purpose of the Connection API is to provide a higher level abstraction that would not
   *    expose the client to the details of KNX protocol frames, including message codes,
   *    cEMI frames, etc. (but there should be a way to get to those lower level details as the
   *    API will only support limited use cases initially)
   *
   *  - The class is designed as effectively immutable, and should therefore be thread-safe.
   */


  // Constants ------------------------------------------------------------------------------------

  private final static int CLIENT_DISCOVERY_LISTENER_PORT = 0;  // zero = any free port TODO : this setting needs to be externalized
  private final static int CLIENT_CONNECTION_PORT = 0;          // zero = any free port TODO : this setting needs to be externalized
  private final static boolean DISCOVERY_USE_NAT = false;       // TODO : this setting needs to be externalized
  private final static boolean CONNECTION_USE_NAT = true;       // TODO : this setting needs to be externalized
  private final static int DISCOVERY_TIMEOUT = 10;              // value in seconds  TODO : this setting needs to be externalized
  private final static boolean BLOCKING_DISCOVERY = false;



  // Class Members --------------------------------------------------------------------------------

  /**
   * KNX logger. Uses a common category for all KNX related logging.
   */
  private final static Logger log = Logger.getLogger(KNXEventBuilder.KNX_LOG_CATEGORY);


  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Calimero KNX IP gateway discovery API. This is initialized in the start() method.
   */
  private Discoverer discovery = null;

  /**
   * Host Protocol Address Information (HPAI) structure for the KNX IP gateway ("server IP").
   * This will be initialized by a successful gateway discovery, or could be written to
   * directly if necessary to bypass the multicast discovery protocol.
   *
   * If a gateway is not found (either through discovery or via direct IP configuration), the
   * endpoint reference should be null.
   *
   * In general avoid accessing the field directly and use it via published methods to
   * ensure proper synchronization.
   *
   * TODO:
   *   there's currently no way to reset the gateway HPAI which could be useful in some cases
   *   (power and/or hardware failures, maintenance). Enabling the re-trigger of discovery would
   *   allow ORB to reset KNX services without restart. This also relates to connection
   *   reconnect policies.
   *
   * TODO:
   *   multiple available gateways not addressed in this implementation yet
   */
  private HPAI gatewayEndPoint = null;

  /**
   * The KNX IP tunnel connection. Initialized when a connection is first requested via
   * getConnection() call. Should never be accessed directly but via getConnection() to
   * ensure proper synchronization semantics.
   *
   * The current implementaiton is "aggressive" in attempting to maintain one persistent
   * connection to the KNX IP gateway. Therefore this reference should be non-null after the
   * first connection has been established (assuming it completes without exceptions). However,
   * the internal state of the connection can become CLOSED at anytime. This is not currently
   * dealt with since the implementation still lacks reconnect policies.
   */
  private KNXnetIPTunnel connection = null;


  // Protected Instance Methods -------------------------------------------------------------------

  /**
   * Start the KNX connection manager service.
   *
   * This method will trigger the KNX IP gateway discovery protocol over multicast in the local
   * subnet. By default, the discovery process is asynchronous so the start method will return
   * quickly without waiting for the responses to arrive.
   *
   * @throws ConnectionException TODO
   */
  protected void start() throws ConnectionException
  {
    // add shutdown hook in an attempt to avoid leaving open connections behind
    // (the KNX IP gateways are very fussy about closing connections...)

    log.debug("Adding shutdown hook to manage unclosed KNX connections in case of controller exit.");

    addShutdownHook();

    // Start KNX multicast discovery...
    
    try
    {
      // TODO :
      //   the localhost resolution is less than ideal -- especially for multi-nic setups should
      //   have a way to specify bind address

      InetAddress localhost = null;

      try
      {
        localhost = InetAddress.getLocalHost();
      }
      catch (Throwable t)
      {
        throw new ConnectionException("Cannot access localhost: " + t.getMessage(), t);
      }

      log.info(
          "Starting KNX gateway discovery on " + localhost +
          " (Using NAT = " + DISCOVERY_USE_NAT +
          ", Client Listener Port = " + CLIENT_DISCOVERY_LISTENER_PORT +
          ", Discovery Timeout = " + DISCOVERY_TIMEOUT + " second(s), " +
          "Blocking Mode = " + BLOCKING_DISCOVERY + ")."
      );

      discovery = new Discoverer(CLIENT_DISCOVERY_LISTENER_PORT, DISCOVERY_USE_NAT);
      discovery.startSearch(DISCOVERY_TIMEOUT, BLOCKING_DISCOVERY);
    }
    catch (KNXException exception)
    {
      // Avoid propagating Calimero exception type outside this implementation...

      String message = exception.getMessage();
      StackTraceElement[] trace = exception.getStackTrace();

      ConnectionException ce = new ConnectionException("KNX discovery failed: " + message);
      ce.setStackTrace(trace);

      throw ce;
    }
  }


  /**
   * TODO
   *
   * @return
   *
   * @throws ConnectionException
   */
  protected synchronized KNXConnection getConnection() throws ConnectionException
  {
    if (connection != null)
      return new CalimeroConnection(connection);

    if (gatewayEndPoint != null)
    {
      buildConnection();

      return new CalimeroConnection(connection);
    }

    gatewayEndPoint = waitForDiscovery();

    if (gatewayEndPoint != null)
    {
      buildConnection();

      return new CalimeroConnection(connection);
    }
    else
    {
      throw new ConnectionException("KNX IP Gateway not found.");
    }
  }


  // Private Instance Methods ---------------------------------------------------------------------

  /**
   * TODO
   */
  private void addShutdownHook()
  {
    try
    {
      final Thread shutdown = new Thread(new Shutdown());

      AccessController.doPrivileged(new PrivilegedAction<Void>()
      {
        public Void run()
        {
          Runtime.getRuntime().addShutdownHook(shutdown);

          return null;
        }
      });
    }
    catch (SecurityException exception)
    {
      log.warn(
          "Cannot register shutdown hook. Most likely due to lack of security permissions " +
          "in the JVM security manager. KNX connection manager service will operate normally " +
          "but may be unable to clean up all the connection resources in case of an unexpected " +
          "shutdown (security exception: " + exception.getMessage() + ")",
          exception
      );
    }
    catch (IllegalStateException exception)
    {
      log.error(
          "Unable to register shutdown hook due to illegal state exception (" +
          exception.getMessage() + "). This may be due to the JVM already starting the " +
          "shutdown process.",
          exception
      );
    }
  }


  /**
   * TODO
   *
   * @throws ConnectionException
   */
  private synchronized void buildConnection() throws ConnectionException
  {
    try
    {
      InetAddress serverIP = gatewayEndPoint.getAddress();
      int serverPort = gatewayEndPoint.getPort();
      InetAddress clientIP = InetAddress.getLocalHost();    // TODO: not the best method for multi-NIC machines

      log.info("Creating connection from " + clientIP + " to " + serverIP + ":" + serverPort);

      connection = new KNXnetIPTunnel(
          KNXnetIPTunnel.LINK_LAYER,
          new InetSocketAddress(clientIP, CLIENT_CONNECTION_PORT),
          new InetSocketAddress(serverIP, serverPort),
          CONNECTION_USE_NAT
      );

      log.info(
          "Connection '" + connection.getName() + "' created " +
          "(Using NAT = " + CONNECTION_USE_NAT + ")."
      );
    }
    catch (UnknownHostException uhe)
    {
      throw new ConnectionException("Could not access localhost: " + uhe.getMessage(), uhe);
    }
    catch (KNXException knx)
    {
      // TODO : could handle specific timeout and remote exceptions for retry or failover

      String message = knx.getMessage();
      StackTraceElement[] stack = knx.getStackTrace();

      ConnectionException ce = new ConnectionException(
          "Unable to connect to KNX IP gateway: " + message
      );

      ce.setStackTrace(stack);

      throw ce;
    }
  }

  /**
   * TODO
   * 
   * @return
   *
   * @throws ConnectionException
   */
  private synchronized HPAI waitForDiscovery() throws ConnectionException
  {
    SearchResponse[] responses = discovery.getSearchResponses();

    if (responses.length > 0)
    {
      // just randomly pick first one...

      log.info("Discovered gateway: " + responses.toString());

      return responses[0].getControlEndpoint();
    }

    if (!discovery.isSearching())
    {
/*
      log.info("Discovery has ended");    // TODO

      try
      {
        HPAI hpai = new HPAI(InetAddress.getByAddress(new byte[] { (byte)192, (byte)168, 1, 33 }), 3671);

        return hpai;
      }
      catch (Throwable t){}
*/
      throw new ConnectionException("KNX IP Gateway was not found.");
    }

    try
    {
      log.info("Waiting on discovery...");    // TODO

      for (int i = 0; i <= DISCOVERY_TIMEOUT; ++i)
      {
        Thread.sleep(1000);

        if (discovery.getSearchResponses().length > 0)
          i = DISCOVERY_TIMEOUT + 1;

        log.info(i);    // TODO
      }
    }
    catch (InterruptedException e)
    {
      Thread.currentThread().interrupt();

      throw new ConnectionException("Thread was interrupted while waiting for discovery results.", e);
    }

    responses = discovery.getSearchResponses();

    if (responses.length > 0)
    {
      log.info("Discovered gateway: " + responses.toString());

      return responses[0].getControlEndpoint();
    }

    // still nothing..
    discovery.stopSearch();

    // TODO : check direct IP config (once it is implemented)

/*
    try
    {
      HPAI hpai = new HPAI(InetAddress.getByAddress(new byte[] { (byte)192, (byte)168, 1, 33 }), 3671);

      return hpai;
    }
    catch (Throwable t){}

    log.info("Couldn't find anything!");    // TODO
*/
    throw new ConnectionException("KNX IP Gateway not found.");
  }


  // Nested Classes and Interfaces ----------------------------------------------------------------


  /**
   * TODO
   */
  private class CalimeroConnection implements KNXConnection
  {
    private KNXnetIPTunnel connection = null;

    private CalimeroConnection(KNXnetIPTunnel connection)
    {
      // TODO : implement reconnect policies
      
      this.connection = connection;
    }

    public void send(String groupAddress, KNXCommand command)
    {
//      GroupAddress group = new GroupAddress(0, 0, 4);
//      IndividualAddress src = new IndividualAddress(0);

      byte commandPayload = 0;
      
      switch (command)
      {
        case SWITCH_ON:

          commandPayload = (byte)0x81;

          break;

        case SWITCH_OFF:

          commandPayload = (byte)0x80;

          break;

        default:

          log.error("");    // TODO
      }

      //CEMILData cEMI = new CEMILData(0x11, src, group, new byte[] { (byte)SWITCH_OFF }, Priority.NORMAL);

      CEMILData cEMI = null;

      try
      {
        cEMI = new CEMILData(
            new byte[]
                {
                    0x11, 0x00, (byte)0x8C, (byte)0xE0, 0x00, 0x00, 0x00, 0x04, 0x01, 0x00, commandPayload
                },
            0
        );
      }
      catch (Throwable t)
      {
        log.error(t);
      }
      
      try
      {
        log.info("sending...");

        //byte[] payload = cEMI.getPayload();
        byte[] payload = cEMI.toByteArray();
        for (int i = 0; i < payload.length; ++i)
        {
          System.out.print(Integer.toHexString(((int)payload[i]) & 0xFF));
          System.out.flush();
        }
        System.out.println();
        
        connection.send(cEMI, KNXnetIPTunnel.NONBLOCKING);

        log.info("sent!");
      }
      catch (Throwable t)
      {
        log.error(t);
      }
    }
  }


  // Inner Classes --------------------------------------------------------------------------------

  /**
   * Implements shutdown hook for the KNX connection manager. Main reason is that the IP KNX
   * gateways can get really fussy over unclosed connections. So trying to make sure everything
   * gets closed properly.
   */
  private class Shutdown implements Runnable
  {
    public void run()
    {
      try
      {
        if (connection != null)
        {
          String connectionName      = connection.getName();
          InetSocketAddress address  = connection.getRemoteAddress();

          // format the message...

          StringBuilder builder = new StringBuilder(256)
            .append("Closing connection (name = ")
            .append(connectionName)
            .append(", address = ")
            .append(address)
            .append(")");

          log.info(builder.toString());

          connection.close();

          log.info("Connection closed.");
        }
      }
      catch (Throwable t)
      {
        log.error("Closing connection failed: " + t);
      }
    }
  }
}