package org.openremote.controller.protocol.knx.ip.message;

import java.io.IOException;
import java.io.OutputStream;

public abstract class IpMessage {
  public static final int     OK     = 0;
  private static final byte[] HEADER = { 0x06, 0x10 };
  private int                 sti;
  private int                 variableLength;

  public static enum Primitive {
    REQ, RESP
  };

  public abstract Primitive getPrimitive();

  public IpMessage(int sti, int variableLength) {
    this.sti = sti;
    this.variableLength = variableLength;
  }

  public void write(OutputStream os) throws IOException {
    os.write(HEADER);
    int d = this.getServiceTypeIdentifier();
    os.write((d >> 8) & 0xFF);
    os.write(d & 0xFF);
    d = this.getVariableLength() + 6;
    os.write((d >> 8) & 0xFF);
    os.write(d & 0xFF);
  }

  public int getSyncSendTimeout() {
    return 0;
  }

  public int getServiceTypeIdentifier() {
    return this.sti;
  }

  public int getVariableLength() {
    return this.variableLength;
  }
}
