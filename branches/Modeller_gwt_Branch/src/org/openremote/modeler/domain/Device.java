package org.openremote.modeler.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "device")
public class Device extends UIBusinessEntity {
   
   private String vendor;
   
   private String model;
   
   private List<DeviceEvent> deviceEvents;

   public String getVendor() {
      return vendor;
   }

   public void setVendor(String vendor) {
      this.vendor = vendor;
   }

   public String getModel() {
      return model;
   }

   public void setModel(String model) {
      this.model = model;
   }
   @OneToMany(mappedBy = "device", cascade = CascadeType.REMOVE)
   public List<DeviceEvent> getDeviceEvents() {
      return deviceEvents;
   }

   public void setDeviceEvents(List<DeviceEvent> deviceEvents) {
      this.deviceEvents = deviceEvents;
   }
   
   
   

}
