package org.openremote.controller.protocol.knx.ip.tunnel;

import java.io.IOException;
import java.net.InetSocketAddress;

public interface IpClient {

   abstract void send(byte[] message) throws KnxIpException, InterruptedException, IOException;

   abstract void register(IpMessageListener l);

   abstract void unregister();

   abstract void connect() throws KnxIpException, InterruptedException, IOException;

   void disconnect() throws KnxIpException, InterruptedException, IOException;

   abstract InetSocketAddress getSrcAddr();
}