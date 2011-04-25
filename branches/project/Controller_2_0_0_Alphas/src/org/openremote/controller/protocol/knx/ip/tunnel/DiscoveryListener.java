package org.openremote.controller.protocol.knx.ip.tunnel;

public interface DiscoveryListener {
   /**
    * A new IP KNX interface was found
    * 
    * @param client
    *           the <code>IpClient</code> object to use to interact with the IP KNX interface.
    */
   void notifyDiscovery(IpDiscoverer discoverer, IpClient client);
}
