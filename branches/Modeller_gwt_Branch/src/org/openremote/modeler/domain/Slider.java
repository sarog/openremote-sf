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

import flexjson.JSON;

/**
 * The Class Slider.
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "slider")
public class Slider extends BusinessEntity {

   private String name;
   private DeviceCommandRef setValueCmd;
   private SensorRef sensorRef;
   private Account account;
   
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @OneToOne(cascade = CascadeType.ALL)
   @JoinColumn(name = "set_value_cmd_oid")
   public DeviceCommandRef getSetValueCmd() {
      return setValueCmd;
   }

   public void setSetValueCmd(DeviceCommandRef setValueCmd) {
      this.setValueCmd = setValueCmd;
   }

   @ManyToOne
   @JSON(include = false)
   public Account getAccount() {
      return account;
   }

   public void setAccount(Account account) {
      this.account = account;
   }

   @OneToOne(cascade = CascadeType.ALL)
   @JoinColumn(name = "sensor_ref_oid")
   public SensorRef getSensorRef() {
      return sensorRef;
   }

   public void setSensorRef(SensorRef sensorRef) {
      this.sensorRef = sensorRef;
   }
}
