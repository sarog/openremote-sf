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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * The Class Device Command Reference.
 * 
 * @author Dan 2009-7-6
 */
@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("DEVICE_CMD_REF")
public class DeviceCommandRef extends DeviceMacroItem {
   
   /** The device command. */
   private DeviceCommand deviceCommand;

   /**
    * Gets the device command.
    * 
    * @return the device command
    */
   @OneToOne
   @JoinColumn(name="target_device_cmd_oid")
   public DeviceCommand getDeviceCommand() {
      return deviceCommand;
   }

   /**
    * Sets the device command.
    * 
    * @param deviceCommand
    *           the new device command
    */
   public void setDeviceCommand(DeviceCommand deviceCommand) {
      this.deviceCommand = deviceCommand;
   }
   
}
