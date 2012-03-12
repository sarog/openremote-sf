package org.openremote.controller.protocol.knx.ip.message;

import java.io.IOException;
import java.io.OutputStream;

public class IpDiscoverReq extends IpMessage {
  // TODO check value
  public static final int SEARCH_TIMEOUT = 10000;
  public static final int STI            = 0x201;
  private Hpai            discoveryEndpoint;

  public IpDiscoverReq(Hpai hpai) {
    super(STI, Hpai.getLength());
    this.discoveryEndpoint = hpai;
  }

  @Override
  public Primitive getPrimitive() {
    return Primitive.REQ;
  }

  @Override
  public void write(OutputStream os) throws IOException {
    super.write(os);
    this.discoveryEndpoint.write(os);
  }

  @Override
  public int getSyncSendTimeout() {
    return SEARCH_TIMEOUT;
  }
}
