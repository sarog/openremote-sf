/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.modeler.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import flexjson.JSON;


@SuppressWarnings("serial")
@Entity
@Table(name = "sensor")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@DiscriminatorValue("SIMPLE_SENSOR")
public class Sensor extends BusinessEntity {

   private String name;
   private SensorCommandRef sensorCommandRef;
   private SensorType type;
   private Account account;
   
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

   @OneToOne(mappedBy = "sensor", cascade = CascadeType.ALL)
   public SensorCommandRef getSensorCommandRef() {
      return sensorCommandRef;
   }
   public void setSensorCommandRef(SensorCommandRef sensorCommandRef) {
      this.sensorCommandRef = sensorCommandRef;
   }
   @ManyToOne
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
}
