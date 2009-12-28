package org.openremote.modeler.domain;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "sensor_ref_item")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@DiscriminatorValue("SENSOR_REF_ITEM")
public class SensorRefItem extends BusinessEntity {

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
