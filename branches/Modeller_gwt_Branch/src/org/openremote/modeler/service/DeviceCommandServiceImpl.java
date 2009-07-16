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
import org.openremote.modeler.client.rpc.DeviceCommandService;
import org.openremote.modeler.domain.DeviceCommand;

/**
 * The implementation for DeviceCommandService interface.
 * 
 * @author Allen
 */
public class DeviceCommandServiceImpl extends BaseAbstractService<DeviceCommand> implements DeviceCommandService {

   /* (non-Javadoc)
    * @see org.openremote.modeler.client.rpc.DeviceCommandService#saveAll(java.util.List)
    */
   public List<DeviceCommand> saveAll(List<DeviceCommand> deviceCommands) {
      for (DeviceCommand command : deviceCommands) {
         genericDAO.save(command);
      }
      return deviceCommands;
   }

   public DeviceCommand save(DeviceCommand deviceCommand) {
      genericDAO.save(deviceCommand);
      Hibernate.initialize(deviceCommand.getProtocol().getAttributes());
      return deviceCommand;
   }

   public void deleteCommand(long id) {
      genericDAO.delete(loadById(id));
   }

   public void update(DeviceCommand deviceCommand) {
      genericDAO.saveOrUpdate(deviceCommand);
      
   }

   public DeviceCommand loadById(long id) {
      DeviceCommand deviceCommand = super.loadById(id);
      Hibernate.initialize(deviceCommand.getProtocol().getAttributes());
      return deviceCommand;
   }
   
}
