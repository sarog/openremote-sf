package org.openremote.modeler.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("DEVICE_MACRO_EVENT_REF")
public class DeviceMacroEventRef extends BuildingModelerEventRef {
   
   private DeviceMacroEvent targetDeviceMacroEvent;

   @OneToOne
   @JoinColumn(name="target_device_macro_event_oid")
   public DeviceMacroEvent getTargetDeviceMacroEvent() {
      return targetDeviceMacroEvent;
   }

   public void setTargetDeviceMacroEvent(DeviceMacroEvent targetDeviceMacroEvent) {
      this.targetDeviceMacroEvent = targetDeviceMacroEvent;
   }

}
