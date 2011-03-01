package org.openremote.controller.protocol.knx;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.openremote.controller.protocol.knx.datatype.DataPointType;
import org.openremote.controller.protocol.knx.ip.tunnel.DiscoveryListener;
import org.openremote.controller.protocol.knx.ip.tunnel.IpClient;
import org.openremote.controller.protocol.knx.ip.tunnel.IpDiscoverer;
import org.openremote.controller.protocol.knx.ip.tunnel.IpMessageListener;
import org.openremote.controller.protocol.knx.ip.tunnel.KnxIpException;

public class KNXIpConnectionManager implements DiscoveryListener {
   // Class Members --------------------------------------------------------------------------------
   /**
    * GroupValueRead timeout TODO check value
    */
   private static int READ_TIMEOUT = 3000;

   /**
    * A system property name ("knx.bind.address") that can be set to a specific IPv4 address on the system to force KNX
    * related IP communication to a specific network interface.
    * <p>
    * 
    * The value of the property should be a valid IPv4 address. IPv6 is not yet supported by KNX.
    * <p>
    * 
    * NOTE: use of "any" local interface address (0.0.0.0) is not supported at the moment.
    */
   public final static String KNX_LOCAL_BIND_ADDRESS = "knx.bind.address";

   /**
    * KNX logger. Uses a common category for all KNX related logging.
    */
   private final static Logger log = Logger.getLogger(KNXCommandBuilder.KNX_LOG_CATEGORY);

   private static int CONNECT_TIMEOUT = 10000;

   // Instance Fields ------------------------------------------------------------------------------

   private KNXConnectionImpl connection;
   private Map<IpDiscoverer, InetSocketAddress> discoverers;

   // Constructors ---------------------------------------------------------------------------------

   public KNXIpConnectionManager() {
      this.connection = null;
      this.discoverers = new HashMap<IpDiscoverer, InetSocketAddress>();
   }

   public KNXIpConnectionManager(InetAddress srcAddr, InetSocketAddress destControlEndpointAddr) throws KnxIpException,
         IOException, InterruptedException {
      this();
      this.connection = new KNXConnectionImpl(new IpClient(srcAddr, destControlEndpointAddr));
   }

   // Implements DiscoveryListener -----------------------------------------------------------------

   @Override
   public void notifyDiscovery(IpDiscoverer discoverer, InetSocketAddress destControlEndpointAddr) {
      synchronized (this.discoverers) {
         this.discoverers.put(discoverer, destControlEndpointAddr);
         this.discoverers.notify();

         // The first interface found we be used for the connection
         if (this.connection == null) {
            this.connection = new KNXConnectionImpl(new IpClient(discoverer.getSrcAddr(), destControlEndpointAddr));

            // TODO asynchronously stop discovery process on other NICs
         }
      }
   }

   // Protected Instance Methods -------------------------------------------------------------------

   /**
    * Start the KNX connection manager service.
    * 
    * This method will trigger the KNX IP gateway discovery protocol over multicast in the local subnet. By default, the
    * discovery process is asynchronous so the start method will return quickly without waiting for the responses to
    * arrive.
    * 
    * @throws ConnectionException
    *            if there was an I/O or configuration error
    */
   protected void start() throws ConnectionException {
      if (this.connection == null) {
         // Start KNX multicast discovery...
         Set<InetAddress> nics = resolveLocalAddresses();

         for (InetAddress inet : nics) {
            IpDiscoverer discoverer = new IpDiscoverer(inet, this);

            try {
               discoverer.start();
               this.discoverers.put(discoverer, null);
            } catch (Exception e) {
               log.info("Failed to get network interface for address '" + inet + "'. Skipping...");
            }
         }
      }
   }

   protected void stop() throws InterruptedException {
      this.stopDiscovery();
      if (this.connection != null) {
         this.connection.stop();
         this.connection = null;
      }
   }

   /**
    * TODO
    * 
    * @return
    * 
    * @throws ConnectionException
    */
   protected KNXConnection getConnection() throws ConnectionException {
      if (this.connection != null) return this.connection;

      // Wait for a connection
      synchronized (this.discoverers) {
         try {
            this.discoverers.wait(CONNECT_TIMEOUT);
            if (this.connection != null) return this.connection;
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
         }
      }

      throw new ConnectionException("KNX IP Gateway not found.");
   }

   /**
    * Attempts to resolve local IPv4 addresses to use for KNX discovery and client-side HPAI for a KNX connection.
    * <p>
    * 
    * Note that operating system and hardware configurations and behavior varies wildly so the implementation provided
    * here is not guaranteed to be fool-proof. Additional feedback and improvements are welcome.
    * <p>
    * 
    * In case the resolution in this method is not working, or an explicit client-side IP address is wanted for other
    * reasons, this implementation can be overriden by setting a system wide {@link #KNX_LOCAL_BIND_ADDRESS} property.
    * The <code>KNX_LOCAL_BIND_ADDRESS</code> property should have as its value a valid IPv4 address in the machine
    * hosting this KNX connection manager.
    * <p>
    * 
    * In addition, this method can be overridden by subclasses for alternative address resolution implementations.
    * 
    * @throws ConnectionException
    *            if there's an I/O error querying the network interfaces on this machine or if
    *            {@link #KNX_LOCAL_BIND_ADDRESS} was configured but could not be resolved to a valid address and
    *            connected to
    * 
    * @return Set of IPv4 addresses on the local machine that could be used for KNX discovery and as client-side HPAI
    *         end-points.
    */
   protected Set<InetAddress> resolveLocalAddresses() throws ConnectionException {
      /*
       * NOTE: InetAddress.getLocalHost() can return a host name that does not resolve to an IP address that has been
       * configured in the host systm lookup service -- this seems to be the case with the default Voyage (Debian)
       * Linux, for example. Therefore in the implementation we actually iterate through all the available network
       * interfaces trying to find valid candidate NICs and IP addresses
       * 
       * NOTE: Logging in this method is on low threshold as its useful for debugging installation and startup issues --
       * the current implementation assumes we execute this only once so verbosity should not be an issue. Logging is
       * directed to a specific KNX logging category
       */

      // First check if an explicit IP address binding has been configured...

      if (hasExplicitAddressBinding()) {
         InetAddress explicitBinding = getConfiguredLocalAddress();

         if (explicitBinding != null) {
            Set<InetAddress> set = new HashSet<InetAddress>();
            set.add(explicitBinding);
            return set;
         }

         else {
            throw new ConnectionException("Property '" + KNX_LOCAL_BIND_ADDRESS
                  + "' was configured but could not be resolved "
                  + "to a valid IPv4 address or could not be connected to. Check the KNX logs for "
                  + "additional details.");
         }
      }

      // Will iterate through all network interfaces in this machine...

      log.info("KNX Connection manager resolving local host IP addresses...");

      Enumeration<NetworkInterface> nics = null;

      try {
         nics = NetworkInterface.getNetworkInterfaces();
      } catch (SocketException e) {
         // if an I/O exception occurs -- no additional detail under what circumstances this
         // might occur is provided

         throw new ConnectionException("Cannot query network interfaces (" + e.getMessage() + "). "
               + "KNX discovery failed.", e);
      }

      // Collect candidate IPv4 addresses from all network interfaces here...

      Set<InetAddress> candidateAddresses = new HashSet<InetAddress>(5);

      // Iterate through the nics...

      while (nics.hasMoreElements()) {
         NetworkInterface nic = nics.nextElement();

         // candidate nics are not loopbacks, disabled or point-to-point (such as modem PPP) ifaces...

         if (!isCandidate(nic)) continue;

         log.info("Found candidate NIC: " + nic);

         // Iterate through each address assigned to the candidate nic...

         List<InterfaceAddress> ipAddresses = nic.getInterfaceAddresses();

         for (InterfaceAddress address : ipAddresses) {
            InetAddress ipAddress = address.getAddress();

            // KNX doesn't support IPv6 so we will simply drop any IPv6 addresses....

            if (ipAddress instanceof Inet6Address) {
               log.info("Skipped IPv6 address (not supported by KNX) " + ipAddress);

               continue;
            }

            // Add to candidate IPv4 address set (duplicates are ignored)...

            if (candidateAddresses.add(ipAddress)) log.info("Added candidate IP address to set - " + ipAddress);
         }
      }

      return candidateAddresses;
   }

   // Private Instance Methods ---------------------------------------------------------------------

   /**
    * A quick helper method to detect if the KNX_LOCAL_BIND_ADDRESS system property has been set.
    * 
    * @return true if KNX_LOCAL_BIND_ADDRESS has been set; false otherwise
    */
   private boolean hasExplicitAddressBinding() {
      return getKNXLocalBindAddressValue() != null;
   }

   /**
    * Utility method to retrieve the value of KNX_LOCAL_BIND_ADDRESS system property.
    * 
    * Executed in a privileged code block, so as long as the calling code has sufficient permissions we don't require
    * any extra privileges.
    * 
    * @return the value of KNX_LOCAL_BIND_ADDRESS if set, or null
    */
   private String getKNXLocalBindAddressValue() {

      // START PRIVILEGED CODE BLOCK ----------------------------------------------------------------
      return AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            try {
               return System.getProperty(KNX_LOCAL_BIND_ADDRESS);
            } catch (SecurityException exception) {
               log.error("Security manager has denied access to '" + KNX_LOCAL_BIND_ADDRESS + "' property ("
                     + exception.getMessage() + ").", exception);

               // Can't get the property, so act as if it was not set...

               return null;
            }
         }
      });
      // END PRIVILEGED CODE BLOCK ------------------------------------------------------------------
   }

   /**
    * Attempts to resolve a configured KNX_LOCAL_BIND_ADDRESS system property into a valid InetAddress
    * 
    * @return an <code>InetAddress</code> resolved from {@link #KNX_LOCAL_BIND_ADDRESS} system property or
    *         <code>null</code> if the property was not set or could not be resolved or connected to.
    */
   private InetAddress getConfiguredLocalAddress() {
      final String localBindAddress = getKNXLocalBindAddressValue();

      if (localBindAddress == null) return null;

      else {
         // START PRIVILEGED CODEBLOCK ---------------------------------------------------------------
         return AccessController.doPrivileged(new PrivilegedAction<InetAddress>() {
            public InetAddress run() {
               try {
                  return InetAddress.getByName(localBindAddress);
               } catch (UnknownHostException exception) {
                  log.error(
                        "Could not resolve explicit KNX address binding '" + localBindAddress + "': "
                              + exception.getMessage(), exception);

                  return null;
               } catch (SecurityException e) {
                  log.error("Security manager denied access to address '" + localBindAddress + "': " + e.getMessage(),
                        e);

                  return null;
               }
            }
         });
         // END PRIVILEGED CODEBLOCK -----------------------------------------------------------------
      }
   }

   /**
    * Utility method attempting to validate if a given network interface is useful as a client side KNX HPAI end-point.
    * It currently skips NICs that have not been enabled, loopback interfaces and point-to-point (e.g. PPP to modem)
    * interfaces.
    * 
    * NOTE : still looking for a fool-proof implementation that would work consistently across different OS'es and
    * network interface setups. The current implementation is still likely to prove insufficient so feedback here is
    * welcome.
    * 
    * @param nic
    *           the network interface to validate
    * 
    * @return true if the network interface is considered as a candidate NIC to be used as the client side KNX discovery
    *         end-point and as KNX connection client-side HPAI; false otherwise
    */
   private boolean isCandidate(NetworkInterface nic) {
      try {
         if (!nic.isUp()) {
            // not running, not useful

            log.info("Skipping disabled NIC: " + nic);

            return false;
         }
         if (nic.isLoopback()) {
            log.info("Skipping loopback interface: " + nic);

            return false;
         }
         if (nic.isPointToPoint()) {
            log.info("Skipping point-to-point interface: " + nic);

            return false;
         }
      } catch (SocketException exception) {
         // log warning and move on
         log.warn("Error retrieving NIC info: " + exception.getMessage(), exception);

         return false;
      }

      // TODO : need to test if virtual ifaces are always included or require getSubs() call

      if (nic.isVirtual()) {
         log.info("Skipping virtual interface: " + nic);

         // TODO - there could be legit use cases to use virtual interface instead

         return false;
      }

      return true;
   }

   private void stopDiscovery() throws InterruptedException {
      for (Iterator<IpDiscoverer> i = this.discoverers.keySet().iterator(); i.hasNext();) {
         ((IpDiscoverer) i.next()).stop();
         i.remove();
      }
   }

   // Inner Classes --------------------------------------------------------------------------------

   private class KNXConnectionImpl implements KNXConnection, IpMessageListener {

      private IpClient client;
      private Map<GroupAddress, ApplicationProtocolDataUnit.ResponseAPDU> internalState = new ConcurrentHashMap<GroupAddress, ApplicationProtocolDataUnit.ResponseAPDU>(
            1000);

      public KNXConnectionImpl(IpClient client) {
         this.client = client;
         this.client.register(this);
      }

      // Implements KNXConnection -----------------------------------------------------------------

      @Override
      public void send(GroupValueWrite command) {
         this.ipSend(command);
      }

      @Override
      public synchronized ApplicationProtocolDataUnit read(GroupValueRead command) {
         this.ipSend(command);
         try {
            this.wait(KNXIpConnectionManager.READ_TIMEOUT);
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
         }
         ApplicationProtocolDataUnit.ResponseAPDU response = this.internalState.get(command.getAddress());

         if (response == null) {
            return null;
         }

         DataPointType dpt = command.getDataPointType();

         return response.resolve(dpt);
      }

      // Implements IpMessageListener -----------------------------------------------------------------

      @Override
      public void receive(byte[] cemiFrame) {
         this.handleResponse(cemiFrame);
      }

      // Private Instance Methods ---------------------------------------------------------------------

      private void stop() {
         try {
            this.client.disconnect();
         } catch (KnxIpException e) {
            log.error(e.getMessage());
         } catch (InterruptedException e) {
            // Ignore
            Thread.currentThread().interrupt();
         } catch (IOException e) {
            log.error(e.getMessage());
         }
      }

      private void ipSend(KNXCommand command) {
         Byte[] f = command.getCEMIFrame();
         byte[] m = new byte[f.length];
         for (int i = 0; i < f.length; ++i) {
            m[i] = f[i];
         }
         try {
            this.client.send(m);
         } catch (KnxIpException e) {
            log.error(e.getMessage());
         } catch (InterruptedException e) {
            // Ignore
         } catch (IOException e) {
            log.error(e.getMessage());
         }
      }

      private void handleResponse(byte[] frame) {
         try {
            // TODO : properly handle AdditionalInfo field when AddInfo is present (currently breaks this impl.)

            if (DataLink.isDataIndicateFrame(frame[KNXCommand.CEMI_MESSAGECODE_OFFSET])) {
               GroupAddress address = new GroupAddress(frame[KNXCommand.CEMI_DESTADDR_HIGH_OFFSET],
                     frame[KNXCommand.CEMI_DESTADDR_LOW_OFFSET]);

               byte dataLen = frame[KNXCommand.CEMI_DATALEN_OFFSET];
               byte apciHi = frame[KNXCommand.CEMI_TPCI_APCI_OFFSET];
               byte apciLoData = frame[KNXCommand.CEMI_APCI_DATA_OFFSET];

               // sanity checks -- is a response?

               if (!ApplicationProtocolDataUnit.isGroupValueResponse(new byte[] { apciHi, apciLoData })) {
                  log.debug("Ignoring frame");

                  // TODO : should handle write requests coming to gateway, e.g. motion sensors

                  return;
               }

               ApplicationProtocolDataUnit.ResponseAPDU apdu = null;

               if (dataLen == 1) {
                  apdu = ApplicationProtocolDataUnit.ResponseAPDU.create6BitResponse(new byte[] { apciHi, apciLoData });
               }

               else if (dataLen == 2) {
                  apdu = ApplicationProtocolDataUnit.ResponseAPDU.create8BitResponse(new byte[] { apciHi, apciLoData,
                        frame[KNXCommand.CEMI_DATA1_OFFSET] });
               }

               else if (dataLen == 3) {
                  apdu = ApplicationProtocolDataUnit.ResponseAPDU.createTwoByteResponse(new byte[] { apciHi,
                        apciLoData, KNXCommand.CEMI_DATA1_OFFSET, KNXCommand.CEMI_DATA2_OFFSET });
               }

               else if (dataLen == 4) {
                  apdu = ApplicationProtocolDataUnit.ResponseAPDU.createThreeByteResponse(new byte[] { apciHi,
                        apciLoData, KNXCommand.CEMI_DATA1_OFFSET, KNXCommand.CEMI_DATA2_OFFSET,
                        KNXCommand.CEMI_DATA3_OFFSET });
               }

               else if (dataLen == 5) {
                  apdu = ApplicationProtocolDataUnit.ResponseAPDU.createFourByteResponse(new byte[] { apciHi,
                        apciLoData, KNXCommand.CEMI_DATA1_OFFSET, KNXCommand.CEMI_DATA2_OFFSET,
                        KNXCommand.CEMI_DATA3_OFFSET, KNXCommand.CEMI_DATA4_OFFSET });
               }

               else {
                  byte[] data = new byte[dataLen];
                  System.arraycopy(frame, KNXCommand.CEMI_DATA1_OFFSET, data, 0, data.length);

                  apdu = ApplicationProtocolDataUnit.ResponseAPDU.createStringResponse(data);
               }
               internalState.put(address, apdu);
            }

            /*
             * StringBuffer buffer = new StringBuffer(1024);
             * 
             * String msgCode = Strings.byteToUnsignedHexString(frame[0]); String addInfo =
             * Strings.byteToUnsignedHexString(frame[1]); String cntrol1 = Strings.byteToUnsignedHexString(frame[2]);
             * String cntrol2 = Strings.byteToUnsignedHexString(frame[3]);
             * 
             * String sourceAddr = IndividualAddress.formatToAreaLineDevice(new byte[] { frame[4], frame[5]}); String
             * destAddr = GroupAddress.formatToMainMiddleSub(new byte[] { frame[6], frame[7]});
             * 
             * int dataLen = frame[8]; String[] data = new String[dataLen + 1];
             * 
             * for (int offset = 9; (offset < offset + dataLen) && offset < frame.length; ++offset) { data[offset - 9] =
             * Strings.byteToUnsignedHexString(frame[offset]); }
             * 
             * String sourceAddress = (sourceAddr.length() == 5) ? "      " + sourceAddr + "      " :
             * (sourceAddr.length() == 6) ? "     " + sourceAddr + "      " : (sourceAddr.length() == 7) ? "     " +
             * sourceAddr + "     " : (sourceAddr.length() == 8) ? "    " + sourceAddr + "     " : "    " + sourceAddr +
             * "    ";
             * 
             * String destAddress = (destAddr.length() == 5) ? "      " + destAddr + "      " : (destAddr.length() == 6)
             * ? "     " + destAddr + "      " : (destAddr.length() == 7) ? "     " + destAddr + "     " : "    " +
             * destAddr + "     ";
             * 
             * buffer .append("[FRAME] ").append(DataLink.findServicePrimitiveByMessageCode(frame[0]))
             * .append(" ").append(sourceAddr).append(" -> ").append(destAddr).append(" Data: ");
             * 
             * for (String b : data) { buffer.append(b).append(" "); }
             * 
             * buffer .append("\n\n")
             * .append("+--------+--------+--------+--------+--------+--------+--------+--------+---...\n")
             * .append("|msg.code|add.info|control1|control2| source address  |  dest. address  |\n")
             * .append("+--------+--------+--------+--------+-----------------+--------+--------+---...\n")
             * .append("|  ").append(msgCode).append("  ") .append("|  ").append(addInfo).append("  ")
             * .append("|  ").append(cntrol1).append("  ") .append("|  ").append(cntrol2).append("  ")
             * .append("|").append(sourceAddress) .append("|").append(destAddress).append("|\n")
             * .append("+--------+--------+--------+--------+--------+--------+--------+--------+---...\n");
             * 
             * System.out.println(buffer);
             */
         } catch (Throwable t) {
            t.printStackTrace();

            // TODO
         }
      }
   }
}
