package org.openremote.modeler.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "device_event")
public class DeviceEvent extends UIBusinessEntity {
   
   private Device device;
   
   private Protocol protocol;

   @ManyToOne
   @JoinColumn(nullable = false, name = "device_oid")
   public Device getDevice() {
      return device;
   }

   public void setDevice(Device device) {
      this.device = device;
   }
   @OneToOne
   public Protocol getProtocol() {
      return protocol;
   }

   public void setProtocol(Protocol protocol) {
      this.protocol = protocol;
   }

   
}
