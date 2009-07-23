/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.modeler.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The Class DeviceMacro.
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
    * @param name
    *           the new name
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * Gets the device macro items.
    * 
    * @return the device macro items
    */
   @OneToMany(mappedBy = "parentDeviceMacro", cascade = CascadeType.ALL)
   public List<DeviceMacroItem> getDeviceMacroItems() {
      return deviceMacroItems;
   }

   /**
    * Sets the device macro items.
    * 
    * @param deviceMacroItems
    *           the new device macro items
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
   public Account getAccount() {
      return account;
   }

   /**
    * Sets the account.
    * 
    * @param account
    *           the new account
    */
   public void setAccount(Account account) {
      this.account = account;
   }
   
}
