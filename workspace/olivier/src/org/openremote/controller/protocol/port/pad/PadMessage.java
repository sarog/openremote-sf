package org.openremote.controller.protocol.port.pad;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.openremote.controller.protocol.port.PortException;

public abstract class PadMessage {
   private byte VERSION = 'a';

   public void write(OutputStream os) throws IOException {
      this.writeHeader(os);
      this.writeBody(os);
   }

   protected void writeHeader(OutputStream os) throws IOException {
      os.write(VERSION);
      os.write(this.getCode());
   }

   protected abstract byte getCode();

   protected abstract void writeBody(OutputStream os) throws IOException;

   protected abstract void readBody(InputStream is) throws IOException;

   public static void writeString(OutputStream os, String s) throws IOException {
      PadMessage.writeUint16(os, s.length());
      os.write(s.getBytes());
   }

   public static void writeUint16(OutputStream os, int i) throws IOException {
      os.write(String.format("%1$04X", i & 0xFFFF).getBytes());
   }

   public static void writeUint32(OutputStream os, int i) throws IOException {
      os.write(String.format("%1$04X", (i >> 16) & 0xFFFF).getBytes());
      os.write(String.format("%1$04X", i & 0xFFFF).getBytes());
   }

   public static int readUint32(InputStream is) throws IOException {
      byte buf[] = new byte[8];
      is.read(buf);
      return (int) Long.parseLong(new String(buf), 16);
   }

   public static void writeOctetString(OutputStream os, byte[] s) throws IOException {
      PadMessage.writeUint16(os, s.length * 2);
      for (int i = 0; i < s.length; ++i) {
         os.write(String.format("%1$02X", s[i] & 0xFF).getBytes());
      }
   }

   public static PadMessage read(InputStream is) throws IOException, PortException {
      int v = is.read();
      if (v != 'a') throw new PortException(PortException.INVALID_MESSAGE);
      int c = is.read();
      PadMessage m = PadMessageFactory.create((byte) c);
      m.readBody(is);
      return m;
   }
}
