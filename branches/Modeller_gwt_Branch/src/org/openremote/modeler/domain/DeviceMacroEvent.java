package org.openremote.modeler.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
@SuppressWarnings("serial")
@Entity
@Table(name = "device_macro_event")
public class DeviceMacroEvent extends UIBusinessEntity {
   
   private List<BuildingModelerEventRef> buildingModelerEventRefs;

   @OneToMany(mappedBy = "parentDeviceMacroEvent")
   public List<BuildingModelerEventRef> getBuildingModelerEventRefs() {
      return buildingModelerEventRefs;
   }

   public void setBuildingModelerEventRefs(List<BuildingModelerEventRef> buildingModelerEventRefs) {
      this.buildingModelerEventRefs = buildingModelerEventRefs;
   }
   
}
