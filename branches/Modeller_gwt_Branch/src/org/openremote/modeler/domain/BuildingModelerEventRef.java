package org.openremote.modeler.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("BUILDING_MODELER_EVENT_REF")
public class BuildingModelerEventRef extends UIDesignerEventRef {

   private DeviceMacroEvent parentDeviceMacroEvent;

   @ManyToOne
   @JoinColumn(name = "parent_device_macro_event_oid")
   public DeviceMacroEvent getParentDeviceMacroEvent() {
      return parentDeviceMacroEvent;
   }

   public void setParentDeviceMacroEvent(DeviceMacroEvent parentDeviceMacroEvent) {
      this.parentDeviceMacroEvent = parentDeviceMacroEvent;
   }

}
