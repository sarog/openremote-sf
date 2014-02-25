/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.modeler.client.rpc;

import java.util.ArrayList;

import org.openremote.devicediscovery.domain.DiscoveredDeviceDTO;
import org.openremote.modeler.exception.DeviceDiscoveryException;
import org.openremote.modeler.shared.dto.DeviceDTO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The Interface is for managing discovered devices
 */
@RemoteServiceRelativePath("deviceDiscovery.smvc")
public interface DeviceDiscoveryRPCService extends RemoteService {

   /**
    * Get the list of discovered devices. Either only new ones or all
    * 
    * @return the list of devices
    */
   ArrayList<DiscoveredDeviceDTO> loadDevices(boolean onlyNew) throws DeviceDiscoveryException;
   
   /**
    * Delete the given devices from the discovered device table
    * @param devicesToDelete
    * @throws DeviceDiscoveryException
    */
   void deleteDevices(ArrayList<DiscoveredDeviceDTO> devicesToDelete) throws DeviceDiscoveryException;

   /**
    * Create all needed OR objects (devices, commands, sensors, switches, sliders) for the given devices.
    * If oneDevicePerProtocol is true all objects are put in one OR device per protocol otherwise each device
    * and it's objects will be put into one OR device for each device.
    * 
    * @param itemsToCreate
    * @param oneDevicePerProtocol
    * @return
    */
   ArrayList<DeviceDTO> createORDevices(ArrayList<DiscoveredDeviceDTO> itemsToCreate, boolean oneDevicePerProtocol) throws DeviceDiscoveryException;
}
