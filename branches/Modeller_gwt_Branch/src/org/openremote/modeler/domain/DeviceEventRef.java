package org.openremote.modeler.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("DEVICE_EVENT_REF")
public class DeviceEventRef extends BuildingModelerEventRef {
   
   private DeviceEvent deviceEvent;

   @OneToOne
   @JoinColumn(name="target_device_event_oid")
   public DeviceEvent getDeviceEvent() {
      return deviceEvent;
   }

   public void setDeviceEvent(DeviceEvent deviceEvent) {
      this.deviceEvent = deviceEvent;
   }
   
}
