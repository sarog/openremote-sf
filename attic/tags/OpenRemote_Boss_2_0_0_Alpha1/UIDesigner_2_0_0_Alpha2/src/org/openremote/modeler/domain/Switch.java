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
   private SwitchCommandOnRef switchCommandOnRef;
   private SwitchCommandOffRef switchCommandOffRef;
   private SwitchSensorRef switchSensorRef;
   private Account account;
   private Device device;
   
   public String getName() {
      return name;
   }

   
   public void setName(String name) {
      this.name = name;
   }

   @OneToOne(mappedBy = "offSwitch", cascade = CascadeType.ALL)
   public SwitchCommandOffRef getSwitchCommandOffRef() {
      return switchCommandOffRef;
   }

   public void setSwitchCommandOffRef(SwitchCommandOffRef switchCommandOffRef) {
      this.switchCommandOffRef = switchCommandOffRef;
   }


   @OneToOne(mappedBy = "onSwitch", cascade = CascadeType.ALL)
   public SwitchCommandOnRef getSwitchCommandOnRef() {
      return switchCommandOnRef;
   }

   public void setSwitchCommandOnRef(SwitchCommandOnRef switchCommandOnRef) {
      this.switchCommandOnRef = switchCommandOnRef;
   }

   @OneToOne(mappedBy = "switchToggle", cascade = CascadeType.ALL)
   public SwitchSensorRef getSwitchSensorRef() {
      return switchSensorRef;
   }


   public void setSwitchSensorRef(SwitchSensorRef switchSensorRef) {
      this.switchSensorRef = switchSensorRef;
   }


   @ManyToOne
   @JSON(include = false)
   public Account getAccount() {
      return account;
   }

   public void setAccount(Account account) {
      this.account = account;
   }
   
   @ManyToOne
   @JSON(include=false)
   public Device getDevice() {
      return device;
   }


   public void setDevice(Device device) {
      this.device = device;
   }


   @Transient
   public String getDisplayName() {
      return getName();
   }
}
