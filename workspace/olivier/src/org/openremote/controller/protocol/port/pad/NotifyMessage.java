package org.openremote.controller.protocol.port.pad;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.openremote.controller.protocol.port.Message;

public class NotifyMessage extends PadMessage {
   public static final byte CODE = 'N';
   private String portId;
   private byte[] content;

   public NotifyMessage(String portId, byte[] content) {
      this.portId = portId;
      this.content = content;
   }

   @Override
   protected byte getCode() {
      return CODE;
   }

   @Override
   protected void writeBody(OutputStream os) throws IOException {
      PadMessage.writeString(os, this.portId);
      PadMessage.writeOctetString(os, this.content);
   }

   @Override
   protected void readBody(InputStream is) {
      // TODO
   }

   public Message getMessage() {
      return null;
   }

   public String getPortId() {
      return this.portId;
   }
}
