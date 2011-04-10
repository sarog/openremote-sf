package org.openremote.controller.protocol.knx.ip.message;

import java.io.IOException;
import java.io.OutputStream;

public class IpConnectionStateReq extends IpMessage {
   public static final int STI = 0x207;
   private int channelId;
   private Hpai controlEndpoint;

   public IpConnectionStateReq(int channelId, Hpai controlEndpoint) {
      super(STI, 0x0A);
      this.controlEndpoint = controlEndpoint;
      this.channelId = channelId;
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
      os.write(this.channelId);
      os.write(0);
      this.controlEndpoint.write(os);
   }
}
