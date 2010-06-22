/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
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

import org.apache.log4j.Logger;
import tuwien.auto.calimero.cemi.CEMILData;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.exception.KNXTimeoutException;
import tuwien.auto.calimero.knxnetip.Discoverer;
import tuwien.auto.calimero.knxnetip.KNXConnectionClosedException;
import tuwien.auto.calimero.knxnetip.KNXnetIPTunnel;
import tuwien.auto.calimero.knxnetip.servicetype.SearchResponse;
import tuwien.auto.calimero.knxnetip.util.HPAI;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

  /**
   * A system property name ("knx.bind.address") that can be set to a specific IPv4 address on the
   * system to force KNX related IP communication to a specific network interface. <p>
   *
   * The value of the property should be a valid IPv4 address. IPv6 is not yet supported by KNX. <p>
   *
   * NOTE: use of "any" local interface address (0.0.0.0) is not supported at the moment.
   */

  // IMPL NOTE: disallowing "0.0.0.0" address is mandated by the underlying Calimero impl.
  public final static String KNX_LOCAL_BIND_ADDRESS = "knx.bind.address";

  // TODO : all the settings below should be externalized
  private final static int CLIENT_DISCOVERY_LISTENER_PORT = 0;  // zero = any free port
  private final static int CLIENT_CONNECTION_PORT = 0;          // zero = any free port
  private final static boolean DISCOVERY_USE_NAT = false;
  private final static boolean CONNECTION_USE_NAT = true;
  private final static int DISCOVERY_TIMEOUT = 10;              // value in seconds
  private final static boolean BLOCKING_DISCOVERY = true;



  // Class Members --------------------------------------------------------------------------------

  /**
   * KNX logger. Uses a common category for all KNX related logging.
   */
  private final static Logger log = Logger.getLogger(KNXEventBuilder.KNX_LOG_CATEGORY);


  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Calimero KNX IP gateway discovery API.
   */
  private Map<InetAddress, Discoverer> discoveryMap = new HashMap<InetAddress, Discoverer>(3);


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
   * The current implementation is "aggressive" in attempting to maintain one persistent
   * connection to the KNX IP gateway. Therefore this reference should be non-null after the
   * first connection has been established (assuming it completes without exceptions). However,
   * the internal state of the connection can become CLOSED at anytime. This is not currently
   * dealt with since the implementation still lacks reconnect policies.
   */
  private KNXnetIPTunnel connection = null;

  /**
   * The chosen IPv4 address for KNX discovery and KNX connection client HPAI. <p>
   *
   * This address can be determined via {@link #resolveLocalAddresses()} method which returns a set
   * of IP candidate addresses on this hosting machine. Alternatively the
   * {@link #KNX_LOCAL_BIND_ADDRESS} system property can be used to set this value.  <p>
   *
   * The KNX discovery process can elect one of the candidate IP addresses as the client IP based
   * on the network interface receiving the KNX discovery response. The subsequent KNX connection
   * creation should then use this same client IP address as part of the client-side HPAI header.
   */
  private InetAddress clientIP = null;


  // Protected Instance Methods -------------------------------------------------------------------

  /**
   * Start the KNX connection manager service.
   *
   * This method will trigger the KNX IP gateway discovery protocol over multicast in the local
   * subnet. By default, the discovery process is asynchronous so the start method will return
   * quickly without waiting for the responses to arrive.
   *
   * @throws ConnectionException if there was an I/O or configuration error
   */
  protected void start() throws ConnectionException
  {
    // add shutdown hook in an attempt to avoid leaving open connections behind
    // (the KNX IP gateways are very fussy about closing connections...)

    log.debug("Adding shutdown hook to manage unclosed KNX connections in case of controller exit.");

    addShutdownHook();

    
    // TODO :
    //
    //   allow bypassing discovery by passing gateway ip in parameter


    // Start KNX multicast discovery...
    
    try
    {
      Set<InetAddress> nics = resolveLocalAddresses();

      for (InetAddress inet : nics)
      {
        Discoverer discoverer = new Discoverer(inet, CLIENT_DISCOVERY_LISTENER_PORT, DISCOVERY_USE_NAT);

        try
        {
          discoverer.startSearch(
              CLIENT_DISCOVERY_LISTENER_PORT,
              NetworkInterface.getByInetAddress(inet), 
              DISCOVERY_TIMEOUT,
              !BLOCKING_DISCOVERY
          );

          discoveryMap.put(inet, discoverer);
        }
        catch (SocketException e)
        {
          log.info("Failed to get network interface for address '" + inet + "'. Skipping...");
        }
      }

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
   * Attempts to resolve local IPv4 addresses to use for KNX discovery and client-side HPAI
   * for a KNX connection.  <p>
   *
   * Note that operating system and hardware configurations and behavior varies wildly so the
   * implementation provided here is not guaranteed to be fool-proof. Additional feedback and
   * improvements are welcome.  <p>
   *
   * In case the resolution in this method is not working, or an explicit client-side IP address
   * is wanted for other reasons, this implementation can be overriden by setting a system wide
   * {@link #KNX_LOCAL_BIND_ADDRESS} property. The <code>KNX_LOCAL_BIND_ADDRESS</code> property
   * should have as its value a valid IPv4 address in the machine hosting this KNX connection
   * manager.  <p>
   *
   * In addition, this method can be overridden by subclasses for alternative address resolution
   * implementations.
   *
   * @throws ConnectionException if there's an I/O error querying the network interfaces on this
   *                             machine or if {@link #KNX_LOCAL_BIND_ADDRESS} was configured but
   *                             could not be resolved to a valid address and connected to
   *
   * @return  Set of IPv4 addresses on the local machine that could be used for KNX discovery
   *          and as client-side HPAI end-points.
   */
  protected Set<InetAddress> resolveLocalAddresses() throws ConnectionException
  {
    /*
     * NOTE:
     *   InetAddress.getLocalHost() can return a host name that does not resolve to an IP
     *   address that has been configured in the host system lookup service -- this seems
     *   to be the case with the default Voyage (Debian) Linux, for example. Therefore in
     *   the implementation we actually iterate through all the available network interfaces
     *   trying to find valid candidate NICs and IP addresses
     *
     * NOTE:
     *   Logging in this method is on low threshold as its useful for debugging installation
     *   and startup issues -- the current implementation assumes we execute this only once
     *   so verbosity should not be an issue. Logging is directed to a specific KNX logging
     *   category
     */


    // First check if an explicit IP address binding has been configured...

    if (hasExplicitAddressBinding())
    {
      InetAddress explicitBinding = getConfiguredLocalAddress();

      if (explicitBinding != null)
      {
        Set<InetAddress> set = new HashSet<InetAddress>();
        set.add(explicitBinding);
        return set;
      }

      else
      {
        throw new ConnectionException(
            "Property '" + KNX_LOCAL_BIND_ADDRESS + "' was configured but could not be resolved " +
            "to a valid IPv4 address or could not be connected to. Check the KNX logs for " +
            "additional details.");
      }
    }


    // Will iterate through all network interfaces in this machine...

    log.info("KNX Connection manager resolving local host IP addresses...");

    Enumeration<NetworkInterface> nics = null;

    try
    {
      nics = NetworkInterface.getNetworkInterfaces();
    }
    catch (SocketException e)
    {
      // if an I/O exception occurs -- no additional detail under what circumstances this
      // might occur is provided

      throw new ConnectionException(
          "Cannot query network interfaces (" + e.getMessage() + "). " +
          "KNX discovery failed.", e);
    }


    // Collect candidate IPv4 addresses from all network interfaces here...

    Set<InetAddress> candidateAddresses = new HashSet<InetAddress>(5);


    // Iterate through the nics...

    while (nics.hasMoreElements())
    {
      NetworkInterface nic = nics.nextElement();

      // candidate nics are not loopbacks, disabled or point-to-point (such as modem PPP) ifaces...

      if (!isCandidate(nic))
        continue;

      log.info("Found candidate NIC: " + nic);


      // Iterate through each address assigned to the candidate nic...

      List<InterfaceAddress> ipAddresses = nic.getInterfaceAddresses();

      for (InterfaceAddress address : ipAddresses)
      {
        InetAddress ipAddress = address.getAddress();


        // KNX doesn't support IPv6 so we will simply drop any IPv6 addresses....

        if (ipAddress instanceof Inet6Address)
        {
          log.info("Skipped IPv6 address (not supported by KNX) " + ipAddress);

          continue;
        }

        // Add to candidate IPv4 address set (duplicates are ignored)...

        if (candidateAddresses.add(ipAddress))
          log.info("Added candidate IP address to set - " + ipAddress);
      }
    }

    return candidateAddresses;
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

    /*
     * TODO :
     *   as per the connection management discussion, the KNXConnection interface
     *   should implement a corresponding close() method (which it may or may not
     *   react to based on the connection management policy)
     *
     * TODO :
     *   document the thread synchronization wrt connection and gatewayEndpoint field access
     *
     * TODO :
     *   given the synchronization, connection pooling may become relevant
     */


    // if we have an existing connection, return it...
    //
    // TODO :
    //   - possible to check connection validity (maybe by having access to the underlying
    //     KNX connection hearbeat)?
    //   - failover if connection broken?

    if (connection != null)
      return new CalimeroConnection(connection);


    // if we don't have a connection but have found our KNX gateway end-point,
    // build a connection to it...
    //
    // TODO :
    //   - handle exception from buildConnection (possible to go back to gateway discovery?)

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
   * A quick helper method to detect if the KNX_LOCAL_BIND_ADDRESS system property has been set.
   *
   * @return true if KNX_LOCAL_BIND_ADDRESS has been set; false otherwise
   */
  private boolean hasExplicitAddressBinding()
  {
    return getKNXLocalBindAddressValue() != null;
  }

  /**
   * Utility method to retrieve the value of KNX_LOCAL_BIND_ADDRESS system property.
   *
   * Executed in a privileged code block, so as long as the calling code has sufficient permissions
   * we don't require any extra privileges.
   *
   * @return the value of KNX_LOCAL_BIND_ADDRESS if set, or null
   */
  private String getKNXLocalBindAddressValue()
  {

    // START PRIVILEGED CODE BLOCK ----------------------------------------------------------------
    return AccessController.doPrivileged(
        new PrivilegedAction<String>()
        {
          public String run()
          {
            try
            {
              return System.getProperty(KNX_LOCAL_BIND_ADDRESS);
            }
            catch (SecurityException exception)
            {
              log.error("Security manager has denied access to '" + KNX_LOCAL_BIND_ADDRESS +
                        "' propery (" + exception.getMessage() + ").", exception);

              // Can't get the property, so act as if it was not set...

              return null;
            }
          }
        }
    );
    // END PRIVILEGED CODE BLOCK ------------------------------------------------------------------
  }

  /**
   * Attempts to resolve a configured KNX_LOCAL_BIND_ADDRESS system property into a valid
   * InetAddress
   *
   * @return  an <code>InetAddress</code> resolved from {@link #KNX_LOCAL_BIND_ADDRESS} system
   *          property or <code>null</code> if the property was not set or could not be resolved
   *          or connected to.
   */
  private InetAddress getConfiguredLocalAddress()
  {
    final String localBindAddress = getKNXLocalBindAddressValue();

    if (localBindAddress == null)
      return null;

    else
    {
      // START PRIVILEGED CODEBLOCK ---------------------------------------------------------------
      return AccessController.doPrivileged(
          new PrivilegedAction<InetAddress>()
          {
            public InetAddress run()
            {
              try
              {
                return InetAddress.getByName(localBindAddress);
              }
              catch (UnknownHostException exception)
              {
                log.error("Could not resolve explicit KNX address binding '" + localBindAddress +
                          "': " + exception.getMessage(), exception);

                return null;
              }
              catch (SecurityException e)
              {
                log.error("Security manager denied access to address '" + localBindAddress + "': " +
                          e.getMessage(), e);

                return null;
              }
            }
          }
      );
      // END PRIVILEGED CODEBLOCK -----------------------------------------------------------------
    }
  }


  /**
   * Utility method attempting to validate if a given network interface is useful as a client side
   * KNX HPAI end-point. It currently skips NICs that have not been enabled, loopback interfaces
   * and point-to-point (e.g. PPP to modem) interfaces.
   *
   * NOTE :
   *   still looking for a fool-proof implementation that would work consistently across different
   *   OS'es and network interface setups. The current implementation is still likely to prove
   *   insufficient so feedback here is welcome.
   *
   * @param nic   the network interface to validate
   *
   * @return true if the network interface is considered as a candidate NIC to be used as the
   *         client side KNX discovery end-point and as KNX connection client-side HPAI; false
   *         otherwise
   */
  private boolean isCandidate(NetworkInterface nic)
  {
    try
    {
      if (!nic.isUp())
      {
        // not running, not useful

        log.info("Skipping disabled NIC: " + nic);

        return false;
      }
      if (nic.isLoopback())
      {
        log.info("Skipping loopback interface: " + nic);

        return false;
      }
      if (nic.isPointToPoint())
      {
        log.info("Skipping point-to-point interface: " + nic);

        return false;
      }
    }
    catch (SocketException exception)
    {
      // log warning and move on
      log.warn("Error retrieving NIC info: " + exception.getMessage(), exception);

      return false;
    }


    // TODO : need to test if virtual ifaces are always included or require getSubs() call

    if (nic.isVirtual())
    {
      log.info("Skipping virtual interface: " + nic);

      // TODO - there could be legit use cases to use virtual interface instead

      return false;
    }

    return true;
  }

  /**
   * Registers a shutdown hook in the JVM to attempt to close any open KNX connections when
   * JVM process is killed.
   *
   * Adding shutdown hook in a privileged code block -- as long as calling code has sufficient
   * security permissions, we don't require additional permissions for this operation.
   */
  private void addShutdownHook()
  {
    try
    {
      final Thread shutdown = new Thread(new Shutdown());


      // BEGIN PRIVILEGED CODE BLOCK --------------------------------------------------------------
      AccessController.doPrivileged(new PrivilegedAction<Void>()
      {
        public Void run()
        {
          Runtime.getRuntime().addShutdownHook(shutdown);

          return null;
        }
      });
      // END PRIVILEGED CODE BLOCK ----------------------------------------------------------------


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
   *
   * @throws ConnectionException
   */
  private synchronized void buildConnection() throws ConnectionException
  {
    try
    {
      InetAddress serverIP = gatewayEndPoint.getAddress();
      int serverPort = gatewayEndPoint.getPort();

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
   * NOTE: *must* set the clientIP on succesful discovery, other methods rely on it
   *
   * @return
   *
   * @throws ConnectionException
   */
  private synchronized HPAI waitForDiscovery() throws ConnectionException
  {
    // Make a copy to allow concurrent modification in the following loop...

    InetAddress[] nics = new InetAddress[discoveryMap.size()];
    discoveryMap.keySet().toArray(nics);

    // Check if any of the NICs has a discovery response...

    // TODO :
    //   We are returning the first valid response regardless that there may
    //   be several responding gateways and the user might want to choose
    //   a specific one

    for (InetAddress address : nics)
    {
      Discoverer discoverer = discoveryMap.get(address);
      SearchResponse[] responses = discoverer.getSearchResponses();

      // We have a valid response, store client NIC and return KNX gateway HPAI...

      if (responses.length > 0)
      {
        HPAI gateway = responses[0].getControlEndpoint();
        clientIP = address;

        log.info("Discovered gateway: " + gateway + " through client IP " + address);

        return gateway;
      }

      // No valid response and search has ended (timed out), remove the discovery
      // object from the map...

      if (!discoverer.isSearching())
      {
        try
        {
          discoveryMap.remove(address);

          log.info("KNX gateway discovery on client IP " + address + " ended with no results.");

          // TODO :
          //
          //  For discovery retry policies (gateways was restarted, etc.), removing discovery
          //  from the map means the valid NIC collection must be rebuilt. May want to deal with
          //  this differently once rediscovery policies are in place.
        }
        catch (ConcurrentModificationException exception)
        {
          // This is just in case -- shouldn't happen but shit happens and code changes so lets
          // just report it and not kill the whole thread...

          log.error("Developer Fail: Implementation error in collection handling!");
        }
      }
    }

    // If we exhausted all our options, give up and go home...

    if (discoveryMap.isEmpty())
    {
      throw new ConnectionException("KNX IP Gateway was not found.");
    }


    // There were still some who were searching, let's give them a chance...

    // TODO :
    //
    //  This part is now blocking until a timeout is reached. It is going to affect
    //  the startup time of the controller so should be executed in parallel threads
    //  for best performance...

    for (InetAddress address : discoveryMap.keySet())
    {
      Discoverer discoverer = discoveryMap.get(address);

      try
      {
        log.info("Waiting on discovery on " + address);

        for (int i = 0; i <= DISCOVERY_TIMEOUT; ++i)
        {
          Thread.sleep(1000);

          if (discoverer.getSearchResponses().length > 0)
          {
            HPAI gateway = discoverer.getSearchResponses()[0].getControlEndpoint();
            clientIP = address;

            // TODO :
            //
            //  Here too return the first valid gateway without validating it or letting
            //  user choose a specific one...

            log.info("Discovered gateway '" + gateway + "' through " + address);

            return gateway;
          }

          log.info("Waited " + i + " seconds (timeout at " + DISCOVERY_TIMEOUT + " seconds)...");
        }

        // still nothing...

        discoverer.stopSearch();
      }
      catch (InterruptedException e)
      {
        Thread.currentThread().interrupt();

        throw new ConnectionException("Thread was interrupted while waiting for discovery results.", e);
      }
    }

    // Finally give up completely....

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

      // KNX Addressing on the common EMI wireformat is a two byte field, consisting of address
      // high byte (a.k.a Octet 0) and low byte (a.k.a Octet 1) [KNX 1.1].
      //
      // [KNX 1.1] Volume 3: Systems Specifications, Part 3 Chapter 2: Data Link Layer General
      // defines Group Address bit structure (1.4 Definitions on page 6-7) as follows:
      //
      //           +-----------------------------------------------+
      // 16 bits   |                 GROUP ADDRESS                 |
      //           +-----------------------+-----------------------+
      //           | OCTET 0 (high byte)   |  OCTET 1 (low byte)   |
      //           +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
      //    bits   | 7| 6| 5| 4| 3| 2| 1| 0| 7| 6| 5| 4| 3| 2| 1| 0|
      //           +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
      //           |  |  Main Group (S13)  |   Sub Group (S13)     |
      //           +--+--------------------+-----------------------+
      //
      // KNX Group addresses do not need to be unique and a device may have more than one group
      // address. Group addresses are defined globally for the entire KNX network (however, in
      // the message frames it is possible to restrict the number of routers that can be crossed
      // to reach devices).  A group address zero is sent to every device (it is a broadcast).
      //
      // NOTE:
      //        Supplement 13 (S13) to KNX 1.1 specification shows additional structure with
      //        Main Group (what appears a 7 bit value) and Sub Group (a 8 bit value) but gives
      //        no further definition for these fields.
      //                                                                                  [JPL]
      //
      // NOTE:
      //        Regarding the group address segments, I haven't found the corresponding
      //        KNX specification to support the common convention of main/middle/sub levels
      //        (5/3/8 bits respectively) or main/sub levels (5/11 bits respectively).
      //
      //        This implementation will however treat '/' separated group address fields
      //        according to these conventions in an attempt to maintain interoperability.
      //        If there's a spec recommendation for this convention somewhere, let me know
      //        (haven't looked through KNX 2.0 to see if it clarifies group address structure).
      //
      //                                                                                  [JPL]
      //

      /* Group Address high byte in the cEMI frame (unstructured) */
      int addressHiByte = 0;

      /* Group Address low byte in the cEMI frame (unstructured) */
      int addressLoByte = 0;


      // Common External Message Interface Control Fields [KNX 1.1 Application Note 033]
      //
      // Common External Message Interface (EMI) defines two control fields in its frame format
      // (one byte each). The bit structure of each control field is defined in the KNX 1.1
      // Application Note 033: Common EMI Specification, section 2.4 Basic Message Structure:
      //
      //   Control Field 1
      //
      //    Bit  |
      //   ------+---------------------------------------------------------------
      //     7   | Frame Type  - 0x0 for extended frame
      //         |               0x1 for standard frame
      //   ------+---------------------------------------------------------------
      //     6   | Reserved
      //         |
      //   ------+---------------------------------------------------------------
      //     5   | Repeat Flag - 0x0 repeat frame on medium in case of an error
      //         |               0x1 do not repeat
      //   ------+---------------------------------------------------------------
      //     4   | System Broadcast - 0x0 system broadcast
      //         |                    0x1 broadcast
      //   ------+---------------------------------------------------------------
      //     3   | Priority    - 0x0 system
      //         |               0x1 normal
      //   ------+               0x2 urgent
      //     2   |               0x3 low
      //         |
      //   ------+---------------------------------------------------------------
      //     1   | Acknowledge Request - 0x0 no ACK requested
      //         | (L_Data.req)          0x1 ACK requested
      //   ------+---------------------------------------------------------------
      //     0   | Confirm      - 0x0 no error
      //         | (L_Data.con) - 0x1 error
      //   ------+---------------------------------------------------------------
      //

      /* A bit for standard common EMI frame type (not extended) in the first control field. */
      final int STANDARD_FRAME_TYPE = 0x01 << 7;

      /* Use frame repeat in the first control field. */
      final int REPEAT_FRAME = 0x00;

      /* Use system broadcast in the first control field. */
      final int SYSTEM_BROADCAST = 0x00;

      /* Bits for normal frame priority (%01) in the first control field of the common EMI frame. */
      final int NORMAL_PRIORITY = 0x01 << 2;

      /* Bit for requesting an ACK (L_Data.req only) for the frame in the first control field. */
      final int REQUEST_ACK = 0x01 << 1;


      //   Control Field 2
      //
      //    Bit  |
      //   ------+---------------------------------------------------------------
      //     7   | Destination Address Type - 0x0 individual address
      //         |                          - 0x1 group address
      //   ------+---------------------------------------------------------------
      //    6-4  | Hop Count (0-7)
      //   ------+---------------------------------------------------------------
      //    3-0  | Extended Frame Format - 0x0 for standard frame
      //   ------+---------------------------------------------------------------
      //

      /* Destination Address Type bit for group address in the second control field of the common
       * EMI frame - most significant bit of the byte. */
      final int GROUP_ADDRESS = 0x01 << 7;

      /* Hop count. Default to six. Bits 4 to 6 in the second control field of the cEMI frame. */
      final int HOP_COUNT =  0x06 << 4;

      /* Non-extended frame format in the second control field of the common EMI frame
       *(four zero bits) */
      final int NON_EXTENDED_FRAME_FORMAT = 0x0;


      // The KNX Common External Message Interface (a.k.a cEMI) frame has a variable length
      // and structure depending on the Common EMI frame message code (first byte) and additional
      // info length (second byte).
      //
      // In a very generic fashion, a Common EMI frame can be defined as follows
      // (KNX 1.1 Application Note 033 - Common EMI Specification, 2.4 Basic Message Structure,
      // page 8):
      //
      // +----+----+---- ... ----+-------- ... --------+
      // | MC | AI |  Add. Info  |   Service Info      |
      // +----+----+---- ... ----+-------- ... --------+
      //
      // MC = Message Code
      // AI = Additional Info Length (0x00 if no additional info is included)
      //
      // KNX communication stack defines a frame transfer service (known as L_Data Service) in the
      // data link layer (KNX 1.1 -- Volume 3 System Specification, Part 2 Communication,
      // Chapter 2 Data Link Layer General, section 2.1 L_Data Service, page 8).
      //
      // Link layer data services are available in "normal" mode (vs. bus monitor mode). A data
      // request (known as L_Data.req primitive) is used to transmit a frame. The corresponding
      // Common EMI frame for L_Data.req is defined as shown below (KNX 1.1 Application Note 033,
      // section 2.5.33 L_Data.req, page 13). Example assumes a standard (non-extended) frame with
      // no additional info fields set in the frame. The application protocol data unit (APDU) is
      // for a short data (<= 6 bits) group value write request (A_GroupValue_Write.req)
      //
      // +--------+--------+--------+--------+----------------+----------------+--------+----------------+
      // |  Msg   |Add.Info| Ctrl 1 | Ctrl 2 | Source Address | Dest. Address  |  Data  |      APDU      |
      // | Code   | Length |        |        |                |                | Length |                |
      // +--------+--------+--------+--------+----------------+----------------+--------+----------------+
      //   1 byte   1 byte   1 byte   1 byte      2 bytes          2 bytes       1 byte      2 bytes
      //
      //  Message Code    = 0x11 - a L_Data.req primitive
      //  Add.Info Length = 0x00 - no additional info
      //  Control Field 1 = see the bit structure above
      //  Control Field 2 = see the bit structure above
      //  Source Address  = 0x0000 - filled in by router/gateway with its source address which is
      //                    part of the KNX subnet
      //  Dest. Address   = KNX group or individual address (2 byte)
      //  Data Length     = Number of bytes of data in the APDU excluding the TPCI/APCI bits
      //  APDU            = Application Protocol Data Unit - the actual payload including transport
      //                    protocol control information (TPCI), application protocol control
      //                    information (APCI) and data passed as an argument from higher layers of
      //                    the KNX communication stack
      //

      final int LINK_LAYER_DATA_REQUEST   = 0x11;
      final int NO_ADDITIONAL_INFORMATION = 0x00;
      final int SOURCE_ADDRESS_HIBYTE     = 0x00;
      final int SOURCE_ADDRESS_LOBYTE     = 0x00;

      //
      // APDU...
      //
      final int APDU_DATA_LENGTH = 0x01;
      final int APCI_GROUPVALUE_WRITE_HIBYTE = 0x00;
      final int APCI_GROUPVALUE_WRITE_LOBYTE = 0x80;

      final int DATATYPE_BOOLEAN_BIT_ON   = 0x01;
      final int DATATYPE_BOOLEAN_BIT_OFF  = 0x00;


      /* Indicates in the code if the address submitted as method parameter is interpreted as
       * individual or group address. */
      //boolean isGroupAddressType = true;


      // We take a forward slash ('/') to mean group address semantics -- either as
      // three-level 5bit/3bit/8bit (main/middle/sub) hierarchy or 5bit/11bit (main/sub)
      // hierarchy...

      if (groupAddress.contains("/"))
      {
        String [] elements = groupAddress.split("/");

        // Interpret group address in 3 segments as 5/3/8 bit sequence...

        if (elements.length == 3)
        {

          try
          {
            int hibits = new Integer(elements[0]);
            int midbits = new Integer(elements[1]);
            int lowbits = new Integer(elements[2]);

            // Sanity checks on address field sizes -- 5 bits is at most 31 decimal...

            if (hibits < 0 || hibits > 31)
            {
              log.error(
                  "Group address value '" + hibits + "' in '" + groupAddress + "' is too large."
              );

              return;
            }

            // ...middle bits is max 7 decimal (%111)...

            if (midbits < 0 || midbits > 7)
            {
              log.error(
                  "Group address value '" + midbits + "' in '" + groupAddress + "' is too large."
              );

              return;
            }

            // ...low bits max 255 (8 bits)...

            if (lowbits < 0 || lowbits > 255)
            {
              log.error(
                  "Group address value '" + lowbits + "' in '" + groupAddress + "' is too large."
              );

              return;
            }

            // shift hibits by 3 to the left to make space for midbits in the first address byte..

            hibits = hibits << 3;

            // and merge with the middle bits...

            hibits = hibits | midbits;

            // store in two bytes for cEMI frame...

            addressHiByte = hibits;
            addressLoByte = lowbits;
          }
          catch (NumberFormatException exception)
          {
            log.error(
                "Cannot parse group address '" + groupAddress +
                "' (assuming 5/3/8 bit format): " + exception.getMessage(), exception
            );

            return;
          }
        }

        // Interpret group address as 5/11 bit segments...

        else if (elements.length == 2)
        {
          // TODO...

          log.error("Dual segment (main/sub) style group addresses not implemented yet.");
          return;
        }

        else
        {
          log.error("Unknown group address structure in '" + groupAddress + "'.");
          return;
        }
      }


      else
      {
        // TODO : if no address structure, could interpret as two byte value address...

        log.error("KNX Group Address must be in format main/middle/sub (" + groupAddress + ")");

        return;
      }


      int apduData = APCI_GROUPVALUE_WRITE_LOBYTE;
      
      switch (command)
      {
        case SWITCH_ON:

          apduData += DATATYPE_BOOLEAN_BIT_ON;

          break;

        case SWITCH_OFF:

          apduData += DATATYPE_BOOLEAN_BIT_OFF;

          break;

        default:

          log.error("Unknown KNX command type: '" + command + "'.");  

          return;
      }

      CEMILData commonEMI = null;

      try
      {
        byte[] commonEMIFrame = new byte[]
            {
                LINK_LAYER_DATA_REQUEST,        // Message Code
                NO_ADDITIONAL_INFORMATION,      // Additional Info Length
                (byte)(STANDARD_FRAME_TYPE +     // Control Field 1
                      REPEAT_FRAME +
                      SYSTEM_BROADCAST +
                      NORMAL_PRIORITY +
                      REQUEST_ACK),
                (byte)(GROUP_ADDRESS +           // Control Field 2
                      HOP_COUNT +
                      NON_EXTENDED_FRAME_FORMAT),
                SOURCE_ADDRESS_HIBYTE,          // Source Address
                SOURCE_ADDRESS_LOBYTE,
                (byte)addressHiByte,            // Destination Address
                (byte)addressLoByte,
                APDU_DATA_LENGTH,               // Data Length
                APCI_GROUPVALUE_WRITE_HIBYTE,   // TPCI/APCI
                (byte)apduData                  // APCI & Data
            };

        commonEMI = new CEMILData(commonEMIFrame, 0);

        log.info(printCommonEMIFrame(commonEMIFrame));
      }
      catch (KNXFormatException exception)
      {
        log.error("Error in Common EMI frame: " + exception.getMessage(), exception);

        return;
      }
      
      try
      {
        log.debug("sending...");

        connection.send(commonEMI, KNXnetIPTunnel.NONBLOCKING);

        log.info("sent!");
      }
      catch (KNXTimeoutException exception)
      {
        log.warn(
            "Sending KNX command to " + groupAddress + " timed out: " +
            exception.getMessage(), exception
        );
      }
      catch (KNXConnectionClosedException exception)
      {
        log.error(
            "Unable to send KNX command to " + groupAddress + ". Connection closed.",
            exception
        );
      }
    }
  }


  // Private Methods ------------------------------------------------------------------------------

  private String printCommonEMIFrame(byte[] frame)
  {
    StringBuilder str = new StringBuilder(1024);

    if (frame.length < 11)
      return "Unknown or unsupported Common EMI frame format";

    for (int i = 0; i < 11; ++i)
    {
      str.append(printUnsignedByteAsTwoDigitHex(frame[i])).append(' ');

      // TODO : decode the CEMI fields to human readable descriptions
    }

    return str.toString();
  }

  private String printUnsignedByteAsTwoDigitHex(byte value)
  {
    int hex = value & 0xFF;
        
    if (hex < 0x0F)
      return "0x0" + Integer.toHexString(hex).toUpperCase();
    else
      return "0x" + Integer.toHexString(hex).toUpperCase();
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
        log.debug("Executing JVM shutdown hook to close KNX connections...");

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
        log.error("Closing connection failed: " + t.getMessage(), t);
      }
    }
  }
}