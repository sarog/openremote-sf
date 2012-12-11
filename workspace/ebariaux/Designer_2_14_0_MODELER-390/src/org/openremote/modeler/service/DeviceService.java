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
package org.openremote.modeler.service;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.shared.dto.DeviceCommandDetailsDTO;
import org.openremote.modeler.shared.dto.DeviceDTO;
import org.openremote.modeler.shared.dto.DeviceDetailsDTO;
import org.openremote.modeler.shared.dto.DeviceDetailsWithChildrenDTO;
import org.openremote.modeler.shared.dto.DeviceWithChildrenDTO;
import org.openremote.modeler.shared.dto.SensorDetailsDTO;
import org.openremote.modeler.shared.dto.SliderDetailsDTO;
import org.openremote.modeler.shared.dto.SwitchDetailsDTO;

/**
 * The Interface DeviceService.
 */
public interface DeviceService {
   
  public static String ORIGINAL_OID_KEY = "OriginalOid";
  
   /**
    * Save device.
    * 
    * @param device the device
    * 
    * @return the device
    */
   Device saveDevice(Device device);
   
   /**
    * Update device.
    * 
    * @param device the device
    */
   void updateDevice(Device device);
   
   /**
    * Delete device.
    * 
    * @param id the id
    */
   void deleteDevice(long id);
   
   /**
    * Load by id.
    * 
    * @param id the id
    * 
    * @return the device
    */
   Device loadById(long id);
   

   /**
    * Load all.
    * 
    * @return the list< device>
    */
   List<Device> loadAll();
   
   /**
    * Load all.
    * 
    * @param account the account
    * 
    * @return the list< device>
    */
   List<Device> loadAll(Account account);
   
   List<Device> loadSameDevices(Device device);
   
   ArrayList<DeviceDetailsDTO> loadAllDeviceDetailsDTOs(Account account);

   /**
    * Loads a device by id and creates a DTO with all information on the device and its related objects (commands, sensors, switches and sliders).
    * 
    * @param oid id of the device to load
    * @return a DTO with information about the device and its related objects
    */
   DeviceWithChildrenDTO loadDeviceWithChildrenDTOById(long oid);

   /**
    * Loads a device by id and creates a DTO with all information on the device and its associated commands.
    * 
    * @param oid id of the device to load
    * @return a DTO with information about the device and its commands
    */
   DeviceWithChildrenDTO loadDeviceWithCommandChildrenDTOById(long oid);

   /**
    * Loads all devices for a given account and creates DTOs with all information on the devices and their related objects (commands, sensors, switches and sliders).
    * 
    * @param account the account owning the devices to load
    * @return a list of DTOs with information about the devices and their commands
    */
   ArrayList<DeviceDetailsWithChildrenDTO> loadAllDeviceDetailsWithChildrenDTOs(Account account);

   Device saveNewDeviceWithChildren(Account account, DeviceDetailsDTO device, ArrayList<DeviceCommandDetailsDTO> commands, ArrayList<SensorDetailsDTO> sensors, ArrayList<SwitchDetailsDTO> switches, ArrayList<SliderDetailsDTO> sliders);

}
