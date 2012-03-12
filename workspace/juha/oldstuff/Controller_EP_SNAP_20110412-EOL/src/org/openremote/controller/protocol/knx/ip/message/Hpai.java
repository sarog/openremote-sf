package org.openremote.controller.protocol.knx.ip.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Hpai {
  private static byte[]     HEADER = { 0x08, 0x01 };
  private InetSocketAddress address;

  public Hpai(InetSocketAddress address) {
    this.address = address;
  }

  public Hpai(InputStream is) throws IOException {
    // TODO check structure length & protocol type
    is.skip(2);

    // TODO check read return values
    byte[] a = new byte[4];
    is.read(a);
    int p = (is.read() << 8) + is.read();
    this.address = new InetSocketAddress(InetAddress.getByAddress(a), p);
  }

  public void write(OutputStream os) throws IOException {
    os.write(HEADER);
    // TODO check byte array contains exactly 4 bytes
    byte[] a = this.address.getAddress().getAddress();
    os.write(a);
    int p = this.address.getPort();
    os.write((p >> 8) & 0xFF);
    os.write(p & 0xFF);
  }

  static int getLength() {
    return 0x8;
  }

  public InetSocketAddress getAddress() {
    return this.address;
  }
}
