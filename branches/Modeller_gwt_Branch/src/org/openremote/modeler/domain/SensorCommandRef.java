package org.openremote.modeler.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("SENSOR_CMD_REF")
public class SensorCommandRef extends CommandRefItem {

   private Sensor sensor;
   
   @OneToOne
   @JoinColumn(name = "sensor_oid")
   public Sensor getSensor() {
      return sensor;
   }

   public void setSensor(Sensor sensor) {
      this.sensor = sensor;
   }

}
