package org.openremote.controller.protocol.knx.ip;

import java.net.InetSocketAddress;

public interface DiscoveryListener {
   /**
    * A new KNX IP interface was found
    * 
    * @param discoverer
    *           The <code>Discoverer</code> object that found the KNX IP interface.
    * @param destControlEndpointAddr
    *           KNX IP interface control endpoint socket address.
    */
   void notifyDiscovery(IpDiscoverer discoverer, InetSocketAddress destControlEndpointAddr);
}
