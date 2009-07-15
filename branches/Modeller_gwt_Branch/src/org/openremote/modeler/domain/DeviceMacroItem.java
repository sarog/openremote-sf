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

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The Class Device Macro Item.
 * 
 * @author Dan 2009-7-6
 */
@SuppressWarnings("serial")
@Entity
@Table(name="device_macro_item")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type")
@DiscriminatorValue("DEVICE_MACRO_ITEM")
public class DeviceMacroItem extends BusinessEntity{

   /** The parent device macro. */
   private DeviceMacro parentDeviceMacro;

   /**
    * Gets the parent device macro.
    * 
    * @return the parent device macro
    */
   @ManyToOne
   @JoinColumn(name = "parent_device_macro_oid")
   public DeviceMacro getParentDeviceMacro() {
      return parentDeviceMacro;
   }

   /**
    * Sets the parent device macro.
    * 
    * @param parentDeviceMacro
    *           the new parent device macro
    */
   public void setParentDeviceMacro(DeviceMacro parentDeviceMacro) {
      this.parentDeviceMacro = parentDeviceMacro;
   }
   
   @Transient
   public String getLabel() {
      if (this instanceof DeviceMacroRef) {
         DeviceMacroRef deviceMacroRef = (DeviceMacroRef) this;
         return deviceMacroRef.getTargetDeviceMacro().getName();
      } else if (this instanceof DeviceCommandRef) {
         DeviceCommandRef commandRef = (DeviceCommandRef) this;
         return commandRef.getDeviceCommand().getName();
      } else {
         return "";
      }
   }

}
