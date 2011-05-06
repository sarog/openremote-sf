package org.openremote.controller.protocol.knx.ip.tunnel;

import org.openremote.controller.protocol.knx.ip.tunnel.message.IpMessage;

public interface IpProcessorListener {
   void notifyMessage(IpMessage message);
}
