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

/**
 * The Class Device.
 * 
 * @author Dan 2009-7-6
 */
@SuppressWarnings("serial")
@Entity
public class Device extends BusinessEntity {
   
   /** The name. */
   private String name;
   
   /** The vendor. */
   private String vendor;
   
   /** The model. */
   private String model;
   
   /** The device commands. */
   private List<DeviceCommand> deviceCommands = new ArrayList<DeviceCommand>();
   
   /** The device attrs. */
   private List<DeviceAttr> deviceAttrs;
   
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
    * Gets the vendor.
    * 
    * @return the vendor
    */
   public String getVendor() {
      return vendor;
   }

   /**
    * Sets the vendor.
    * 
    * @param vendor
    *           the new vendor
    */
   public void setVendor(String vendor) {
      this.vendor = vendor;
   }

   /**
    * Gets the model.
    * 
    * @return the model
    */
   public String getModel() {
      return model;
   }

   /**
    * Sets the model.
    * 
    * @param model
    *           the new model
    */
   public void setModel(String model) {
      this.model = model;
   }
   
   /**
    * Gets the device commands.
    * 
    * @return the device commands
    */
   @OneToMany(mappedBy = "device", cascade = CascadeType.REMOVE)
   public List<DeviceCommand> getDeviceCommands() {
      return deviceCommands;
   }

   /**
    * Sets the device commands.
    * 
    * @param deviceCommands
    *           the new device commands
    */
   public void setDeviceCommands(List<DeviceCommand> deviceCommands) {
      this.deviceCommands = deviceCommands;
   }

   /**
    * Gets the device attrs.
    * 
    * @return the device attrs
    */
   @OneToMany(mappedBy = "device", cascade = CascadeType.REMOVE)
   public List<DeviceAttr> getDeviceAttrs() {
      return deviceAttrs;
   }

   /**
    * Sets the device attrs.
    * 
    * @param deviceAttrs
    *           the new device attrs
    */
   public void setDeviceAttrs(List<DeviceAttr> deviceAttrs) {
      this.deviceAttrs = deviceAttrs;
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
