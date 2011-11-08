package org.openremote.controller.protocol.port.pad;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openremote.controller.protocol.port.Message;
import org.openremote.controller.protocol.port.PortException;
import org.openremote.controller.protocol.port.pad.AbstractPort;

public class AbstractPortTest {
   @Test
   public void testPort() throws PortException, IOException {
      AbstractPort port = new AbstractPort();
      Map<String, Object> cfg = new HashMap<String, Object>();
      cfg.put(AbstractPort.PORT_ID, "/dev/ttyUSB0");
      cfg.put(AbstractPort.PORT_TYPE, "serial");
      port.configure(cfg);
      port.start();
      port.send(new Message(new byte[] { 0x10, 0x40, 0x40, 0x16 }));
      port.stop();
   }
}
