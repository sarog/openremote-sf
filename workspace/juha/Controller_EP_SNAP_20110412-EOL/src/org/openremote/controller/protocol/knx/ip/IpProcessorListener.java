package org.openremote.controller.protocol.knx.ip;

import org.openremote.controller.protocol.knx.ip.message.IpMessage;

public interface IpProcessorListener {
   void notifyMessage(IpMessage message);
}
