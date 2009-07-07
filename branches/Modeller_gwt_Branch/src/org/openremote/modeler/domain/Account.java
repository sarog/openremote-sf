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

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * The Class Account.
 * 
 * @author Dan 2009-7-7
 */
@SuppressWarnings("serial")
@Entity
public class Account extends BusinessEntity {

   /** The user. */
   private User user;
   
   /** The devices. */
   private List<Device> devices;
   
   /** The device macros. */
   private List<DeviceMacro> deviceMacros;

   /**
    * Gets the user.
    * 
    * @return the user
    */
   @OneToOne
   public User getUser() {
      return user;
   }

   /**
    * Sets the user.
    * 
    * @param user
    *           the new user
    */
   public void setUser(User user) {
      this.user = user;
   }

   /**
    * Gets the devices.
    * 
    * @return the devices
    */
   @OneToMany(mappedBy="account")
   public List<Device> getDevices() {
      return devices;
   }

   /**
    * Sets the devices.
    * 
    * @param devices
    *           the new devices
    */
   public void setDevices(List<Device> devices) {
      this.devices = devices;
   }

   /**
    * Gets the device macros.
    * 
    * @return the device macros
    */
   @OneToMany(mappedBy="account")
   public List<DeviceMacro> getDeviceMacros() {
      return deviceMacros;
   }

   /**
    * Sets the device macros.
    * 
    * @param deviceMacros
    *           the new device macros
    */
   public void setDeviceMacros(List<DeviceMacro> deviceMacros) {
      this.deviceMacros = deviceMacros;
   }
   
   
   
}
