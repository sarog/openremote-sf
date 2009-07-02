package org.openremote.modeler.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
@SuppressWarnings("serial")
@Entity
@Table(name = "protocol")
public class Protocol extends BusinessEntity {
   
   private String type;
   
   private List<Attribute> attributes;
   
   private DeviceEvent deviceEvent;

   @OneToMany(mappedBy = "protocol", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
   public List<Attribute> getAttributes() {
      return attributes;
   }

   public void setAttributes(List<Attribute> attributes) {
      this.attributes = attributes;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   @OneToOne(mappedBy="protocol",fetch = FetchType.LAZY)
   public DeviceEvent getDeviceEvent() {
      return deviceEvent;
   }

   public void setDeviceEvent(DeviceEvent deviceEvent) {
      this.deviceEvent = deviceEvent;
   }
   
}
