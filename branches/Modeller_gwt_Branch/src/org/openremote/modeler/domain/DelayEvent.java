package org.openremote.modeler.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "delay_event")
public class DelayEvent extends BusinessEntity {
   
   private int millisecond;
   private DeviceEvent deviceEvent;

   @ManyToOne
   @JoinColumn(nullable = false, name = "device_event_oid")
   public DeviceEvent getDeviceEvent() {
      return deviceEvent;
   }

   public void setDeviceEvent(DeviceEvent deviceEvent) {
      this.deviceEvent = deviceEvent;
   }

   public int getMillisecond() {
      return millisecond;
   }

   public void setMillisecond(int millisecond) {
      this.millisecond = millisecond;
   }

}
