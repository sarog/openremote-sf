/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import flexjson.JSON;

/**
 * The Class Switch.
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "switch")
public class Switch extends BusinessEntity {

   private String name;
   private DeviceCommandRef onDeviceCommandRef;
   private DeviceCommandRef offDeviceCommandRef;
   private SensorRef sensorRef;
   private Account account;

   public String getName() {
      return name;
   }

   @OneToOne(cascade = CascadeType.ALL)
   @JoinColumn(name = "on_cmd_oid")
   public DeviceCommandRef getOnDeviceCommandRef() {
      return onDeviceCommandRef;
   }

   @OneToOne(cascade = CascadeType.ALL)
   @JoinColumn(name = "off_cmd_oid")
   public DeviceCommandRef getOffDeviceCommandRef() {
      return offDeviceCommandRef;
   }
   
   public void setName(String name) {
      this.name = name;
   }

   public void setOnDeviceCommandRef(DeviceCommandRef onDeviceCommandRef) {
      this.onDeviceCommandRef = onDeviceCommandRef;
   }

   public void setOffDeviceCommandRef(DeviceCommandRef offDeviceCommandRef) {
      this.offDeviceCommandRef = offDeviceCommandRef;
   }

   @OneToOne(cascade = CascadeType.ALL)
   @JoinColumn(name = "sensor_ref_oid")
   public SensorRef getSensorRef() {
      return sensorRef;
   }

   public void setSensorRef(SensorRef sensorRef) {
      this.sensorRef = sensorRef;
   }

   @ManyToOne
   @JSON(include = false)
   public Account getAccount() {
      return account;
   }

   public void setAccount(Account account) {
      this.account = account;
   }
   
   @Transient
   public String getDisplayName() {
      return getName();
   }

}
