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
package org.openremote.modeler.client.rpc;

import java.util.List;

import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

// TODO: Auto-generated Javadoc
/**
 * The Interface DeviceCommandRPCService.
 */
@RemoteServiceRelativePath("deviceCommand.smvc")
public interface DeviceCommandRPCService extends RemoteService{
   
   /**
    * Save all.
    * 
    * @param deviceCommands the device commands
    * 
    * @return the list< device command>
    */
   List<DeviceCommand> saveAll(List<DeviceCommand> deviceCommands);
   
   /**
    * Save.
    * 
    * @param deviceCommand the device command
    * 
    * @return the device command
    */
   DeviceCommand save(DeviceCommand deviceCommand);
   
   /**
    * Update.
    * 
    * @param deviceCommand the device command
    */
   void update(DeviceCommand deviceCommand);
   
   /**
    * Load by id.
    * 
    * @param id the id
    * 
    * @return the device command
    */
   DeviceCommand loadById(long id);
   
   /**
    * Delete command.
    * 
    * @param id the id
    */
   void deleteCommand(long id);
   
   /**
    * Load by device.
    * 
    * @param device the device
    * 
    * @return the list< device command>
    */
   List<DeviceCommand> loadByDevice(long id);
}
