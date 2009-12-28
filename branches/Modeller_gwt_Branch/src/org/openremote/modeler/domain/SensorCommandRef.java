package org.openremote.modeler.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("SENSOR_CMD_REF")
public class SensorCommandRef extends CommandRefItem {

   private Sensor sensor;
   
   private String deviceName;
   
   @OneToOne
   @JoinColumn(name = "target_sensor_oid")
   public Sensor getSensor() {
      return sensor;
   }

   public void setSensor(Sensor sensor) {
      this.sensor = sensor;
   }

   @Transient
   public String getDeviceName() {
      return deviceName;
   }

   public void setDeviceName(String deviceName) {
      this.deviceName = deviceName;
   }
   
   @Override
   @Transient
   public String getDisplayName() {
      this.deviceName = (this.deviceName == null || "".equals(this.deviceName)) ? getDeviceCommand().getDevice().getName() : this.deviceName;
      return getDeviceCommand().getName() + " (" + this.deviceName + ")";
   }
}
