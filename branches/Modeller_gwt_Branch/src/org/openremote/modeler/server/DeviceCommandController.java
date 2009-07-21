/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
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

package org.openremote.modeler.server;

import java.util.List;

import org.openremote.modeler.client.rpc.DeviceCommandRPCService;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.service.DeviceCommandService;

public class DeviceCommandController extends BaseGWTSpringControllerWithHibernateSupport implements
        DeviceCommandRPCService {

   /**
    * 
    */
   private static final long serialVersionUID = -8417889117208060088L;
   private DeviceCommandService deviceCommandService;

    public void setDeviceCommandService(DeviceCommandService deviceCommandRPCService) {
      this.deviceCommandService = deviceCommandRPCService;
   }

   public List<DeviceCommand> saveAll(List<DeviceCommand> deviceCommands) {
      return deviceCommandService.saveAll(deviceCommands);
   }

   public DeviceCommand save(DeviceCommand deviceCommand) {
      return deviceCommandService.save(deviceCommand);
   }

   public void deleteCommand(long id) {
      deviceCommandService.deleteCommand(id);
   }

   public void update(DeviceCommand deviceCommand) {
      deviceCommandService.update(deviceCommand);
   }

   public DeviceCommand loadById(long id){
      return deviceCommandService.loadById(id);
   }

   @Override
   public List<DeviceCommand> loadByDevice(long id) {
      return deviceCommandService.loadByDevice(id);
   }

}
