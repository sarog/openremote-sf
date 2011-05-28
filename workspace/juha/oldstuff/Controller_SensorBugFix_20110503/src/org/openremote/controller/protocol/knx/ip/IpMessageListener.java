package org.openremote.controller.protocol.knx.ip;

public interface IpMessageListener {

   void receive(byte[] cemiFrame);
}
