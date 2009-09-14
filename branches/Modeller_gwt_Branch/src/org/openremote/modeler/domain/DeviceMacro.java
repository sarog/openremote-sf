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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import flexjson.JSON;


/**
 * The Class Device Macro. It's a macro of {@link DeviceCommand}.
 * 
 * @author Dan 2009-7-6
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "device_macro")
public class DeviceMacro extends BusinessEntity {
   
   /** The device macro items. */
   private List<DeviceMacroItem> deviceMacroItems = new ArrayList<DeviceMacroItem>();
   
   /** The name. */
   private String name;
   
   /** The account. */
   private Account account;
   

   /**
    * Gets the name.
    * 
    * @return the name
    */
   @Column(nullable = false)
   public String getName() {
      return name;
   }

   /**
    * Sets the name.
    * 
    * @param name the new name
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * Gets the device macro items. 
    * Here the order of macro items is very important.
    * 
    * @return the device macro items
    */
   @OneToMany(mappedBy = "parentDeviceMacro", cascade = CascadeType.ALL)
   @OrderBy(value = "oid")// NOTE: *OrderBy* never be removed!
   public List<DeviceMacroItem> getDeviceMacroItems() {
      return deviceMacroItems;
   }

   /**
    * Sets the device macro items.
    * 
    * @param deviceMacroItems the new device macro items
    */
   public void setDeviceMacroItems(List<DeviceMacroItem> deviceMacroItems) {
      this.deviceMacroItems = deviceMacroItems;
   }

   /**
    * Gets the account.
    * 
    * @return the account
    */
   @ManyToOne
   @JSON(include = false)
   public Account getAccount() {
      return account;
   }

   /**
    * Sets the account.
    * 
    * @param account the new account
    */
   public void setAccount(Account account) {
      this.account = account;
   }

   @Override
   @Transient
   public String getDisplayName() {
      return getName();
   }
   
}
