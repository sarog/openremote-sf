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

import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.Device;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

// TODO: Auto-generated Javadoc
/**
 * The Interface DeviceService.
 */
@RemoteServiceRelativePath("device.smvc")
public interface DeviceService extends RemoteService {
   
   /**
    * Save device.
    * 
    * @param device the device
    * 
    * @return the device
    */
   public Device saveDevice(Device device);
   
   /**
    * Update device.
    * 
    * @param device the device
    */
   public void updateDevice(Device device);
   
   /**
    * Removes the device.
    * 
    * @param device the device
    */
   public void removeDevice(Device device);
   
   /**
    * Load by id.
    * 
    * @param id the id
    * 
    * @return the device
    */
   public Device loadById(long id);
   

   /**
    * Load all.
    * 
    * @return the list< device>
    */
   public List<Device> loadAll();
   
   /**
    * Load all.
    * 
    * @param account the account
    * 
    * @return the list< device>
    */
   public List<Device> loadAll(Account account);
}
