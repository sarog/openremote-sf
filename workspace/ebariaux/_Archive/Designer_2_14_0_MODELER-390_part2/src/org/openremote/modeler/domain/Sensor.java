/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.modeler.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openremote.modeler.shared.dto.DTOReference;
import org.openremote.modeler.shared.dto.SensorDTO;
import org.openremote.modeler.shared.dto.SensorDetailsDTO;
import org.openremote.modeler.shared.dto.SensorWithInfoDTO;

import flexjson.JSON;


/**
 * The super class of <b>CustomSensor</b> and <b>RangeSensor</b>.
 * It define a sensorCommandRef and a sensor type.
 */
@Entity
@Table(name = "sensor")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@DiscriminatorValue("SIMPLE_SENSOR")
public class Sensor extends BusinessEntity {

   private static final long serialVersionUID = 7762063535155846996L;
   private String name;
   private SensorCommandRef sensorCommandRef;
   
   /** The type of the sensor, include switch, level, range, color and custom. */
   private SensorType type;
   private Account account;
   private Device device;
   
   public Sensor() {
   }
   public Sensor(SensorType type) {
      this.type = type;
   }
   @Column(nullable = false)
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @OneToOne(mappedBy = "sensor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   public SensorCommandRef getSensorCommandRef() {
      return sensorCommandRef;
   }
   public void setSensorCommandRef(SensorCommandRef sensorCommandRef) {
      this.sensorCommandRef = sensorCommandRef;
   }
   @ManyToOne(fetch = FetchType.LAZY)
   @JSON(include = false)
   public Account getAccount() {
      return account;
   }

   public void setAccount(Account account) {
      this.account = account;
   }

   public SensorType getType() {
      return type;
   }

   public void setType(SensorType type) {
      this.type = type;
   }

   @Transient
   public String getDisplayName() {
      return getName();
   }
   @ManyToOne(fetch = FetchType.LAZY)
   @JSON(include = false)
   public Device getDevice() {
      return device;
   }
   public void setDevice(Device device) {
      this.device = device;
   }
   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("<link type=\"sensor\" ref=\"" + getOid() + "\">\n");
      sb.append("</link>");
      return sb.toString();
   }
   
   @Override
   public int hashCode() {
      return (int) getOid();
   }
   
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      Sensor other = (Sensor) obj;
      if (this.getOid() != other.getOid()) return false;
      if (name == null) {
         if (other.name != null) return false;
      } else if (!name.equals(other.name)) return false;
      if (sensorCommandRef == null) {
         if (other.sensorCommandRef != null) return false;
      } else if (!sensorCommandRef.equals(other.sensorCommandRef)) return false;
      if (type == null) {
         if (other.type != null) return false;
      } else if (!type.equals(other.type)) return false;
      return true;
   }
   
   /**
    * Equals without compare oid is for rebuilding sensor from template.
    * 
    * @param other the other
    * 
    * @return true, if successful
    */
   public boolean equalsWithoutCompareOid(Sensor other) {
      if (name == null) {
         if (other.name != null) return false;
      } else if (!name.equals(other.name)) return false;
      if (type == null) {
         if (other.type != null) return false;
      } else if (!type.equals(other.type)) return false;
      if (device == null) {
         if (other.device != null) return false;
      } else if (!device.equals(other.device)) return false;
      if (sensorCommandRef == null) {
         if (other.sensorCommandRef != null) return false;
      } else if (other.sensorCommandRef != null && !sensorCommandRef.equalsWithoutCompareOid(other.sensorCommandRef)) return false;
      return true;
   }

   /**
    * Returns the sensor id offset by a predefined value.
    * This is used to partition the id space between commands and sensor,
    * and avoid having id collision.
    * This is a temporary solution, see MODELER-413 for more details. 
    * 
    * @return int sensor id offset by predefine value.
    */
   @Transient
   @JSON(include = false)
   public long getOffsetId() {
     return 100000 + getOid();
   }

   
  @Transient
  public SensorDTO getSensorDTO() {
    SensorDTO sensorDTO = new SensorDTO(getOid(), getDisplayName(), getType());
    DeviceCommand dc = getSensorCommandRef().getDeviceCommand();
    sensorDTO.setCommand(dc.getDeviceCommandDTO());
    return sensorDTO;
  }

  @Transient
  public SensorDetailsDTO getSensorDetailsDTO() {
    SensorDetailsDTO dto;

    dto = new SensorDetailsDTO(

    getOid(), getName(), getType(), getSensorCommandRef().getDisplayName(), null, null, null);
    if (getSensorCommandRef() != null) {
      dto.setCommand(new DTOReference(getSensorCommandRef().getDeviceCommand().getOid()));
    }
    return dto;
  }

  @Transient
  public SensorWithInfoDTO getSensorWithInfoDTO() {
    return new SensorWithInfoDTO(getOid(), getDisplayName(), getType(), getSensorCommandRef().getDisplayName(), null, null, null);
  }

}
