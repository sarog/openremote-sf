package org.openremote.controller.protocol.port.pad;

import java.io.IOException;
import java.util.Map;

import org.openremote.controller.protocol.port.Message;
import org.openremote.controller.protocol.port.Port;
import org.openremote.controller.protocol.port.PortException;

public class AbstractPort implements Port, PortListener {
   public final static String PORT_ID = "portId";
   public final static String PORT_TYPE = "portType";
   public final static String PORT_SPEED = "speed";
   public final static String PORT_NB_BITS = "nbBits";
   public final static String PORT_PARITY = "parity";
   private Object notifyLock;
   private PadClient padClient;
   private NotifyMessage notification;
   private String portId;
   private String portType;

   public AbstractPort() {
      this.notifyLock = new Object();
      this.padClient = PadClient.instance();
      this.portId = null;
      this.portType = null;
   }

   @Override
   public void configure(Map<String, Object> configuration) throws IOException, PortException {
      Object o1 = configuration.get(PORT_ID);
      Object o2 = configuration.get(PORT_TYPE);
      if (!(o1 instanceof String) || !(o2 instanceof String)) {
         throw new PortException(PortException.INVALID_CONFIGURATION);
      }
      if (this.portId != null) this.padClient.unregisterPortListener(this.portId);
      this.portId = (String) o1;
      this.portType = (String) o2;
      if (this.portId == null || this.portId.equals("") || this.portType == null || this.portType.equals("")) {
         throw new PortException(PortException.INVALID_CONFIGURATION);
      }
      this.padClient.registerPortListener(this.portId, this);
      try {
         this.padClient.service(new CreatePortMessage(this.portId, this.portType));
      } catch (PortException x) {
         if (!(x.getCode() == PortException.SERVICE_FAILED && x.getRootCode() == -5)) throw x;
      }

      // Configure port
      ConfigureMessage m = new ConfigureMessage(portId);
      for (String key : configuration.keySet()) {
         m.addConfig(key, configuration.get(key).toString());
      }
      this.padClient.service(m);
   }

   @Override
   public void start() throws IOException, PortException {
      this.padClient.service(new LockMessage(this.portId, "t"));
   }

   @Override
   public void stop() throws IOException, PortException {
      this.padClient.service(new UnlockMessage(this.portId, "t"));
   }

   @Override
   public void send(Message message) throws IOException, PortException {
      this.padClient.service(new NotifyMessage(this.portId, message.getContent()));
   }

   @Override
   public Message receive() throws IOException {
      synchronized (this.notifyLock) {
         try {
            this.notification = null;
            this.notifyLock.wait();
            return this.notification.getMessage();
         } catch (InterruptedException e) {
            // Nothing more to do
         }
      }
      return null;
   }

   @Override
   public void notifyMessage(NotifyMessage notification) {
      synchronized (this.notifyLock) {
         this.notification = notification;
         this.notifyLock.notify();
      }
   }
}
