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

import org.openremote.modeler.domain.DeviceCommand;

import com.google.gwt.user.client.rpc.AsyncCallback;

// TODO: Auto-generated Javadoc
/**
 * The Interface DeviceCommandRPCServiceAsync.
 */
public interface DeviceCommandRPCServiceAsync {
   
   /**
    * Save all.
    * 
    * @param deviceCommands the device commands
    * @param callback the callback
    */
   public void saveAll(List<DeviceCommand> deviceCommands,AsyncCallback<List<DeviceCommand>> callback);
   
   /**
    * Save.
    * 
    * @param deviceCommand the device command
    * @param callback the callback
    */
   public void save(DeviceCommand deviceCommand,AsyncCallback<DeviceCommand> callback);
   
   /**
    * Update.
    * 
    * @param deviceCommand the device command
    * @param callback the callback
    */
   public void update(DeviceCommand deviceCommand,AsyncCallback<Void> callback);
   
   /**
    * Load by id.
    * 
    * @param id the id
    * @param callback the callback
    */
   public void loadById(long id,AsyncCallback<DeviceCommand> callback);
   
   /**
    * Delete command.
    * 
    * @param id the id
    * @param callback the callback
    */
   public void deleteCommand(long id,AsyncCallback<Void> callback);

   /**
    * Load by device.
    * 
    * @param id the id
    * @param asyncSuccessCallback the async success callback
    */
   public void loadByDevice(long id, AsyncCallback<List<DeviceCommand>> asyncCallback);
   
   
}
