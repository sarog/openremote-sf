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

import org.openremote.modeler.client.rpc.DeviceCommandRPCService;
import org.openremote.modeler.domain.DeviceCommand;

import java.util.List;

public class DeviceCommandController extends BaseGWTSpringControllerWithHibernateSupport implements
        DeviceCommandRPCService {

   /**
    * 
    */
   private static final long serialVersionUID = -8417889117208060088L;
   private DeviceCommandRPCService deviceCommandRPCService;

    public void setDeviceCommandService(DeviceCommandRPCService deviceCommandRPCService) {
      this.deviceCommandRPCService = deviceCommandRPCService;
   }

   public List<DeviceCommand> saveAll(List<DeviceCommand> deviceCommands) {
      return deviceCommandRPCService.saveAll(deviceCommands);
   }

   public DeviceCommand save(DeviceCommand deviceCommand) {
      return deviceCommandRPCService.save(deviceCommand);
   }

   public void deleteCommand(long id) {
      deviceCommandRPCService.deleteCommand(id);
   }

   public void update(DeviceCommand deviceCommand) {
      deviceCommandRPCService.update(deviceCommand);
   }

   public DeviceCommand loadById(long id){
      return deviceCommandRPCService.loadById(id);
   }

}
