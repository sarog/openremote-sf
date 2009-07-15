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
package org.openremote.modeler.service;

import java.util.List;

import org.hibernate.Hibernate;
import org.openremote.modeler.client.rpc.DeviceService;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;

/**
 * The Class DeviceServiceImpl.
 */
public class DeviceServiceImpl extends BaseAbstractService<Device> implements DeviceService {

   /* (non-Javadoc)
    * @see org.openremote.modeler.client.rpc.DeviceService#saveDevice(java.util.Map)
    */
   public Device saveDevice(Device device) {
      genericDAO.saveOrUpdate(device);
      Hibernate.initialize(device.getDeviceCommands());
      return device;
   }

   /* (non-Javadoc)
    * @see org.openremote.modeler.client.rpc.DeviceService#removeDevice(org.openremote.modeler.domain.Device)
    */
   public void removeDevice(Device device) {
      genericDAO.delete(device);
   }
   
   /* (non-Javadoc)
    * @see org.openremote.modeler.client.rpc.DeviceService#loadAll(org.openremote.modeler.domain.Account)
    */
   public List<Device> loadAll(Account account) {
      List<Device> devices = account.getDevices();
      if (devices != null) {
         for (Device device : devices) {
            Hibernate.initialize(device.getDeviceCommands());
            for (DeviceCommand deviceCommand : device.getDeviceCommands()) {
               Hibernate.initialize(deviceCommand.getProtocol().getAttributes());
            }
         }
      }
      return devices;
   }
   
  
}
