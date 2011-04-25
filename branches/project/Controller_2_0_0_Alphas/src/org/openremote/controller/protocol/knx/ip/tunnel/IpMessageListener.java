package org.openremote.controller.protocol.knx.ip.tunnel;

public interface IpMessageListener {

   void receive(byte[] cemiFrame);
}
