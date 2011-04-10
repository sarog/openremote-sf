package org.openremote.controller.protocol.knx.ip.message;

import java.io.IOException;
import java.io.OutputStream;

public class IpDisconnectReq extends IpMessage {
  public static final int STI = 0x209;
  private int             ccid;
  private Hpai            hpai;

  public IpDisconnectReq(int ccid, Hpai serverHpai) {
    super(STI, 10);
    this.ccid = ccid;
    this.hpai = serverHpai;
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
    os.write(this.ccid & 0xFF);
    os.write(0);
    this.hpai.write(os);
  }
}
