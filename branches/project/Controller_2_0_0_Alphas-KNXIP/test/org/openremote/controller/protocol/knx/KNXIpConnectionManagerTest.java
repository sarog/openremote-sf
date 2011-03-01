package org.openremote.controller.protocol.knx;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.command.CommandParameter;
import org.openremote.controller.exception.ConversionException;
import org.openremote.controller.protocol.knx.datatype.DataPointType;
import org.openremote.controller.protocol.knx.ip.tunnel.KnxIpException;

public class KNXIpConnectionManagerTest {
   DummyServer server;

   @Before
   public void setUp() throws IOException, InterruptedException {
      this.server = new DummyServer();
      synchronized (this.server) {
         this.server.start();
         this.server.wait();
      }
   }

   @Test
   public void testDiscover() throws ConnectionException, ConversionException, IOException, InterruptedException {
      KNXIpConnectionManager mgr = new KNXIpConnectionManager();
      DummyDiscoverServer discoverServer = new DummyDiscoverServer();
      synchronized (discoverServer) {
         discoverServer.start();
         discoverServer.wait();
      }
      mgr.start();
      KNXConnection c = mgr.getConnection();
      c.send(GroupValueWrite.createCommand("on", DataPointType.BINARY_VALUE, mgr, new GroupAddress((byte) 0x80,
            (byte) 0x00), new CommandParameter("0")));
      c.send(GroupValueWrite.createCommand("off", DataPointType.BINARY_VALUE, mgr, new GroupAddress((byte) 0x80,
            (byte) 0x00), new CommandParameter("0")));
      Assert.assertNull(this.server.getError());
      discoverServer.interrupt();
   }

   @Test
   public void testConnect() throws ConnectionException, ConversionException, KnxIpException, IOException,
         InterruptedException {
      KNXIpConnectionManager mgr = new KNXIpConnectionManager(new InetSocketAddress("127.0.0.1", 0),
            new InetSocketAddress("127.0.0.1", DummyServer.PORT));
      mgr.start();
      KNXConnection c = mgr.getConnection();
      c.send(GroupValueWrite.createCommand("on", DataPointType.BINARY_VALUE, mgr, new GroupAddress((byte) 0x80,
            (byte) 0x00), new CommandParameter("0")));
      c.send(GroupValueWrite.createCommand("off", DataPointType.BINARY_VALUE, mgr, new GroupAddress((byte) 0x80,
            (byte) 0x00), new CommandParameter("0")));
      mgr.stop();
      Assert.assertNull(this.server.getError());
   }
}
