package org.openremote.controller.protocol.knx.ip.tunnel.message;

import java.io.IOException;
import java.io.OutputStream;

public class IpConnectReq extends IpMessage {
   public static final int STI = 0x205;
   // TODO check if 3rd byte should not be changed
   private static byte[] CRI = { 0x04, 0x04, 0x02, 0x00 };
   private Hpai controlEndpoint, dataEndpoint;

   public IpConnectReq(Hpai controlEndpoint, Hpai dataEndpoint) {
      super(STI, 0x14);
      this.controlEndpoint = controlEndpoint;
      this.dataEndpoint = dataEndpoint;
   }

   @Override
   public Primitive getPrimitive() {
      return Primitive.REQ;
   }

   @Override
   public int getSyncSendTimeout() {
      return 10000;
   }

   @Override
   public void write(OutputStream os) throws IOException {
      super.write(os);
      this.controlEndpoint.write(os);
      this.dataEndpoint.write(os);
      os.write(CRI);
   }
}
