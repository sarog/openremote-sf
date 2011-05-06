package org.openremote.controller.protocol.knx.ip.tunnel.message;

import java.io.IOException;
import java.io.InputStream;

public class IpDiscoverAck extends IpMessage {
  public static final int STI = 0x202;
  private Hpai            controlEndpoint;

  public IpDiscoverAck(InputStream is, int vl) throws IOException {
    super(STI, vl);
    this.controlEndpoint = new Hpai(is);

    // TODO Read device hardware DIB
    int l = is.read();
    is.skip(l - 1);

    // TODO Read supported service families DIB
    l = is.read();
    is.skip(l - 1);
  }

  @Override
  public Primitive getPrimitive() {
    return Primitive.ACK;
  }

  public Hpai getControlEndpoint() {
    return this.controlEndpoint;
  }
}
