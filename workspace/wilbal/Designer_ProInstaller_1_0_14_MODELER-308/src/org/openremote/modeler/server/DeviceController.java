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
package org.openremote.modeler.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.rpc.DeviceRPCService;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.RangeSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorCommandRef;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.State;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.service.DeviceService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.DeviceCommandDetailsDTO;
import org.openremote.modeler.shared.dto.DeviceDTO;
import org.openremote.modeler.shared.dto.DeviceDetailsDTO;
import org.openremote.modeler.shared.dto.DeviceWithChildrenDTO;
import org.openremote.modeler.shared.dto.SensorDTO;
import org.openremote.modeler.shared.dto.SensorDetailsDTO;
import org.openremote.modeler.shared.dto.SliderDTO;
import org.openremote.modeler.shared.dto.SliderDetailsDTO;
import org.openremote.modeler.shared.dto.SwitchDTO;
import org.openremote.modeler.shared.dto.SwitchDetailsDTO;

/**
 * The server side implementation of the RPC service <code>DeviceRPCService</code>.
 */
public class DeviceController extends BaseGWTSpringController implements DeviceRPCService {

   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = -6698924847005128888L;
   
   /** The device service. */
   private DeviceService deviceService;
   
   /** The user service. */
   private UserService userService;

    /**
     * Sets the device service .
     * 
     * @param deviceService the device service
     */
   public void setDeviceService(DeviceService deviceService) {
      this.deviceService = deviceService;
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.openremote.modeler.client.rpc.DeviceRPCService#deleteDevice
    */
   public void deleteDevice(long id) {
      deviceService.deleteDevice(id);
   }
   /**
    * Sets the user service.
    * 
    * @param userService the new user service
    */
   public void setUserService(UserService userService) {
      this.userService = userService;
   }

   public ArrayList<DeviceDTO> loadAllDTOs() {
     return new ArrayList<DeviceDTO>(deviceService.loadAllDTOs(userService.getAccount()));
  }

   @Override
   public DeviceWithChildrenDTO loadDeviceWithChildrenDTOById(long oid) {
     return deviceService.loadDeviceWithChildrenDTOById(oid);
   }
   
   @Override
   public DeviceWithChildrenDTO loadDeviceWithCommandChildrenDTOById(long oid) {
     return deviceService.loadDeviceWithChildrenDTOById(oid);
   }

  protected ArrayList<DeviceCommandDTO> createDeviceCommandDTOs(List<DeviceCommand> deviceCommands) {
    ArrayList<DeviceCommandDTO> dcDTOs = new ArrayList<DeviceCommandDTO>();
     for (DeviceCommand dc : deviceCommands) {
       dcDTOs.add(new DeviceCommandDTO(dc.getOid(), dc.getDisplayName(), dc.getFullyQualifiedName(), dc.getProtocol().getType()));
     }
    return dcDTOs;
  }
   
   @Override
   public DeviceDetailsDTO loadDeviceDetailsDTO(long oid) {
	   return deviceService.loadDeviceDetailsDTOById(oid);
   }
   
   @Override
   public DeviceDTO saveNewDevice(DeviceDetailsDTO device) {
      return deviceService.saveNewDevice(device);
   }
   
   @Override
   public DeviceDTO saveNewDeviceWithChildren(DeviceDetailsDTO device, ArrayList<DeviceCommandDetailsDTO> commands, ArrayList<SensorDetailsDTO> sensors,
                                         ArrayList<SwitchDetailsDTO> switches, ArrayList<SliderDetailsDTO> sliders) {
	 return deviceService.saveNewDeviceWithChildren(device, commands, sensors, switches, sliders);
   }

   public void updateDeviceWithDTO(DeviceDetailsDTO device) {
	 deviceService.updateDeviceWithDTO(device);
   }
}
