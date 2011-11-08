package org.openremote.controller.protocol.port.pad;

import java.io.IOException;

import org.junit.Test;
import org.openremote.controller.protocol.port.PortException;
import org.openremote.controller.protocol.port.pad.PadClient;
import org.openremote.controller.protocol.port.pad.PingMessage;

public class PadClientTest {
   @Test
   public void testClient() throws PortException, IOException {
      PadClient c = PadClient.instance();
      c.service(new PingMessage());
   }
}
