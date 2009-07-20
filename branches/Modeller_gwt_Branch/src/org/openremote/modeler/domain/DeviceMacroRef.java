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

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * The Class Device Macro Reference.
 * 
 * @author Dan 2009-7-6
 */
@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("DEVICE_MACRO_REF")
public class DeviceMacroRef extends DeviceMacroItem {

   public DeviceMacroRef(){}
   
   public DeviceMacroRef(DeviceMacro targetDeviceMacro) {
      super();
      this.targetDeviceMacro = targetDeviceMacro;
   }

   /** The target device macro. */
   private DeviceMacro targetDeviceMacro;

   /**
    * Gets the target device macro.
    * 
    * @return the target device macro
    */
   @OneToOne
   @JoinColumn(name="target_device_macro_oid")
   public DeviceMacro getTargetDeviceMacro() {
      return targetDeviceMacro;
   }

   /**
    * Sets the target device macro.
    * 
    * @param targetDeviceMacro
    *           the new target device macro
    */
   public void setTargetDeviceMacro(DeviceMacro targetDeviceMacro) {
      this.targetDeviceMacro = targetDeviceMacro;
   }

}
