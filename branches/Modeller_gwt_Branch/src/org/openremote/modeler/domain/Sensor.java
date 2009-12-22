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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import flexjson.JSON;


@SuppressWarnings("serial")
@Entity
@Table(name = "sensor")
public class Sensor extends BusinessEntity {

   private String name;
   private DeviceCommandRef deviceCommandRef;
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

   @OneToOne
   @JoinColumn(name = "status_cmd_oid")
   public DeviceCommandRef getDeviceCommandRef() {
      return deviceCommandRef;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setDeviceCommandRef(DeviceCommandRef deviceCommandRef) {
      this.deviceCommandRef = deviceCommandRef;
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

}
