/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2010, OpenRemote Inc.
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
import org.openremote.controller.utils.Strings;
import org.openremote.controller.protocol.knx.datatype.DataPointType;
import tuwien.auto.calimero.cemi.CEMILData;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.exception.KNXTimeoutException;
import tuwien.auto.calimero.knxnetip.Discoverer;
import tuwien.auto.calimero.knxnetip.KNXConnectionClosedException;
import tuwien.auto.calimero.knxnetip.KNXnetIPTunnel;
import tuwien.auto.calimero.knxnetip.servicetype.SearchResponse;
import tuwien.auto.calimero.knxnetip.util.HPAI;
import tuwien.auto.calimero.KNXListener;
import tuwien.auto.calimero.FrameEvent;
import tuwien.auto.calimero.CloseEvent;

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
import java.util.concurrent.ConcurrentHashMap;


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
  private final static Logger log = Logger.getLogger(KNXCommandBuilder.KNX_LOG_CATEGORY);


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


  /**
   * TODO
   */
  private KNXBusListener busListener = null;



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

      busListener = new KNXBusListener(connection);

      connection.addConnectionListener(busListener);

//      GroupAddress address = new GroupAddress((byte)0, (byte)0);
//      GroupValueRead cmd = GroupValueRead.createCommand("STATUS", KNXConnectionManager.this, address);
//
//      cmd.read(EnumSensorType.CUSTOM, new HashMap());

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


  // Inner Classes --------------------------------------------------------------------------------


  /**
   * TODO
   *
   */
  private class KNXBusListener implements KNXListener
  {

    private KNXnetIPTunnel connection = null;
    private Map<GroupAddress, ApplicationProtocolDataUnit.ResponseAPDU> internalState =
        new ConcurrentHashMap<GroupAddress, ApplicationProtocolDataUnit.ResponseAPDU>(1000);



    private KNXBusListener(KNXnetIPTunnel connection)
    {
      this.connection = connection;

    }

    public void frameReceived(FrameEvent event)
    {
      try
      {
        log.debug("RECEIVED: " + event.getFrame());

        byte[] frame = event.getFrame().toByteArray();

        // TODO : properly handle AdditionalInfo field when AddInfo is present (currently breaks this impl.)

        if (DataLink.isDataIndicateFrame(frame[KNXCommand.CEMI_MESSAGECODE_OFFSET]))
        {
          GroupAddress address = new GroupAddress(
              frame[KNXCommand.CEMI_DESTADDR_HIGH_OFFSET],
              frame[KNXCommand.CEMI_DESTADDR_LOW_OFFSET]
          );

          byte dataLen    = frame[KNXCommand.CEMI_DATALEN_OFFSET];
          byte apciHi     = frame[KNXCommand.CEMI_TPCI_APCI_OFFSET];
          byte apciLoData = frame[KNXCommand.CEMI_APCI_DATA_OFFSET];

          // sanity checks -- is a response?

          if (!ApplicationProtocolDataUnit.isGroupValueResponse(new byte[] { apciHi, apciLoData }))
          {
            log.debug("Ignoring frame: " + event.getFrame());

            // TODO : should handle write requests coming to gateway, e.g. motion sensors

            return;
          }
          
          ApplicationProtocolDataUnit.ResponseAPDU apdu = null;

          if (dataLen == 1)
          {
            apdu = ApplicationProtocolDataUnit.ResponseAPDU.create6BitResponse
            (
                new byte[] { apciHi, apciLoData }
            );
          }

          else if (dataLen == 2)
          {
            apdu = ApplicationProtocolDataUnit.ResponseAPDU.create8BitResponse
            (
                new byte[] { apciHi, apciLoData, frame[KNXCommand.CEMI_DATA1_OFFSET] }
            );
          }

          else if (dataLen == 3)
          {
            apdu = ApplicationProtocolDataUnit.ResponseAPDU.createTwoByteResponse
            (
                new byte[] {
                    apciHi, apciLoData,
                    KNXCommand.CEMI_DATA1_OFFSET,
                    KNXCommand.CEMI_DATA2_OFFSET
                }
            );
          }

          else if (dataLen == 4)
          {
            apdu = ApplicationProtocolDataUnit.ResponseAPDU.createThreeByteResponse
            (
                new byte[] {
                    apciHi, apciLoData,
                    KNXCommand.CEMI_DATA1_OFFSET,
                    KNXCommand.CEMI_DATA2_OFFSET,
                    KNXCommand.CEMI_DATA3_OFFSET
                }
            );
          }

          else if (dataLen == 5)
          {
            apdu = ApplicationProtocolDataUnit.ResponseAPDU.createFourByteResponse
            (
                new byte[] {
                    apciHi, apciLoData,
                    KNXCommand.CEMI_DATA1_OFFSET,
                    KNXCommand.CEMI_DATA2_OFFSET,
                    KNXCommand.CEMI_DATA3_OFFSET,
                    KNXCommand.CEMI_DATA4_OFFSET
                }
            );
          }
          
          else
          {
            byte[] data = new byte[dataLen];
            System.arraycopy(frame, KNXCommand.CEMI_DATA1_OFFSET, data, 0, data.length);

            apdu = ApplicationProtocolDataUnit.ResponseAPDU.createStringResponse(data);
          }

          log.debug("Adding to internal state " + event.getFrame());

          internalState.put(address, apdu);
        }

/*
        StringBuffer buffer = new StringBuffer(1024);

        String msgCode = Strings.byteToUnsignedHexString(frame[0]);
        String addInfo = Strings.byteToUnsignedHexString(frame[1]);
        String cntrol1 = Strings.byteToUnsignedHexString(frame[2]);
        String cntrol2 = Strings.byteToUnsignedHexString(frame[3]);

        String sourceAddr = IndividualAddress.formatToAreaLineDevice(new byte[] { frame[4], frame[5]});
        String destAddr = GroupAddress.formatToMainMiddleSub(new byte[] { frame[6], frame[7]});

        int dataLen = frame[8];
        String[] data = new String[dataLen + 1];

        for (int offset = 9; (offset < offset + dataLen) && offset < frame.length; ++offset)
        {
          data[offset - 9] = Strings.byteToUnsignedHexString(frame[offset]);
        }

        String sourceAddress =
              (sourceAddr.length() == 5) ? "      " + sourceAddr + "      "
            : (sourceAddr.length() == 6) ? "     " + sourceAddr + "      "
            : (sourceAddr.length() == 7) ? "     " + sourceAddr + "     "
            : (sourceAddr.length() == 8) ? "    " + sourceAddr + "     "
                                         : "    " + sourceAddr + "    ";

        String destAddress =
            (destAddr.length() == 5) ? "      " + destAddr + "      "
          : (destAddr.length() == 6) ? "     " + destAddr + "      "
          : (destAddr.length() == 7) ? "     " + destAddr + "     "
                                     : "    " + destAddr + "     ";

        buffer
            .append("[FRAME] ").append(DataLink.findServicePrimitiveByMessageCode(frame[0]))
            .append(" ").append(sourceAddr).append(" -> ").append(destAddr).append(" Data: ");

        for (String b : data)
        {
          buffer.append(b).append(" ");
        }

        buffer
            .append("\n\n")
            .append("+--------+--------+--------+--------+--------+--------+--------+--------+---...\n")
            .append("|msg.code|add.info|control1|control2| source address  |  dest. address  |\n")
            .append("+--------+--------+--------+--------+-----------------+--------+--------+---...\n")
            .append("|  ").append(msgCode).append("  ")
            .append("|  ").append(addInfo).append("  ")
            .append("|  ").append(cntrol1).append("  ")
            .append("|  ").append(cntrol2).append("  ")
            .append("|").append(sourceAddress)
            .append("|").append(destAddress).append("|\n")
            .append("+--------+--------+--------+--------+--------+--------+--------+--------+---...\n");

        System.out.println(buffer);
*/
      }
      catch (Throwable t)
      {
        t.printStackTrace();

        // TODO
      }
    }

    public void connectionClosed(CloseEvent event)
    {
      this.connection.removeConnectionListener(this);
    }
  }


  /**
   * TODO
   */
  private class CalimeroConnection implements KNXConnection
  {
    private KNXnetIPTunnel connection = null;
    //private ProcessCommunicator pc;

    private CalimeroConnection(KNXnetIPTunnel connection)
    {
      // TODO : implement reconnect policies
      
      this.connection = connection;

    }


    public ApplicationProtocolDataUnit read(GroupValueRead command)
    {
      this.sendInternal(command);

      ApplicationProtocolDataUnit.ResponseAPDU response = busListener.internalState.get(command.getAddress());

      if (response == null)
      {
        return null;
      }

      DataPointType dpt = command.getDataPointType();

      return response.resolve(dpt);
    }

    public void send(GroupValueWrite command)
    {
      this.sendInternal(command);
    }



    private void sendInternal(KNXCommand command)
    {
      CEMILData commonEMI = null;

      try
      {
        Byte[] cemiBytes = command.getCEMIFrame();
        byte[] cemiFrame = new byte[cemiBytes.length];

        for (int i = 0; i < cemiBytes.length; ++i)
        {
          cemiFrame[i] = cemiBytes[i];
        }

        commonEMI = new CEMILData(cemiFrame, 0);

        log.info("Sending : " + command);

        connection.send(commonEMI, KNXnetIPTunnel.WAIT_FOR_ACK);
      }
      catch (KNXFormatException exception)
      {
        log.error("Error in Common EMI frame: " + exception.getMessage(), exception);
      }
      
      catch (KNXTimeoutException exception)
      {
        log.warn(
            "Sending KNX command " + command + " timed out: " +
            exception.getMessage(), exception
        );
      }
      catch (KNXConnectionClosedException exception)
      {
        log.error(
            "Unable to send KNX command " + command + ". Connection closed.",
            exception
        );
      }

    }


///*
//        try {
//            sendReadStatusRequest(connection, null);
//            return read(groupAddress, dptTypeID);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "unknown";
//*/
//    }
//
//    /**
//     * Sends read request to Device.
//     *
//     * @param knxNetIPTunnel KNXnetIPTunnel
//     * @param l the ProcessListener
//     *
//     * @throws Exception the exception
//     */
//    private void sendReadStatusRequest(KNXnetIPTunnel knxNetIPTunnel, ProcessListener l) throws Exception {
//        // create the network link to the KNX network
//        KNXNetworkLink lnk = createLink(knxNetIPTunnel);
//
//        // create process communicator with the established link
//        pc = new ProcessCommunicatorImpl(lnk);
//        if (l != null) {
//            pc.addProcessListener(l);
//        }
//        pc.setResponseTimeout(10);
//    }
//
//    /**
//     * Creates the knxNetIPTunnel link.
//     *
//     * @param knxNetIPTunnel the knx net ip tunnel
//     *
//     * @return the kNX network link
//     *
//     * @throws Exception the exception
//     */
//    private KNXNetworkLink createLink(KNXnetIPTunnel knxNetIPTunnel) throws Exception {
//        // create local and remote socket address for network link
//        final InetSocketAddress local = new InetSocketAddress(InetAddress.getLocalHost(), CLIENT_CONNECTION_PORT);
//        final InetSocketAddress host = new InetSocketAddress(InetAddress.getByName("192.168.0.10"), 3671);//knxNetIPTunnel.getRemoteAddress();
//        final int mode = KNXNetworkLinkIP.TUNNEL;
//        this.connection.close();
//        return new KNXNetworkLinkIP(mode, local, host, CONNECTION_USE_NAT, TPSettings.TP1);
//    }
//
//    /**
//     * Read status of device with group address and DataPointType id.
//     *
//     * @param groupAddress the group address
//     * @param dptTypeID the dpt type id
//     *
//     * @return the string
//     *
//     * @throws KNXException the KNX exception
//     */
//    private String read(String groupAddress, String dptTypeID) throws KNXException {
//            // check if we are doing a read or write operation
//            final GroupAddress main = new GroupAddress(groupAddress);
//            // encapsulate information into a datapoint
//            // this is a convenient way to let the process communicator
//            // handle the DPT stuff, so an already formatted string will be
//            // returned
//            final Datapoint dp = new StateDP(main, "", 0, dptTypeID);
//            String rst = pc.read(dp);
//            System.out.println("read value: " + rst);
//            return rst;
//        }
  }



/*
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
*/



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
