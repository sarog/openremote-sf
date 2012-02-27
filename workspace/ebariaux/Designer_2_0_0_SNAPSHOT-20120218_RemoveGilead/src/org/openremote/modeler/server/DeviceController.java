/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2011, OpenRemote Inc.
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
import java.util.List;

import org.openremote.modeler.client.rpc.DeviceRPCService;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.service.DeviceService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.DeviceDTO;
import org.openremote.modeler.shared.dto.DeviceDetailsDTO;
import org.openremote.modeler.shared.dto.DeviceWithChildrenDTO;
import org.openremote.modeler.shared.dto.SensorDTO;
import org.openremote.modeler.shared.dto.SliderDTO;
import org.openremote.modeler.shared.dto.SwitchDTO;

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
    * @see org.openremote.modeler.client.rpc.DeviceRPCService#saveDevice(java.util.Map)
    */
   public Device saveDevice(Device device) {
      device.setAccount(userService.getAccount());
      return deviceService.saveDevice(device);
   }
   

   public ArrayList<Device> saveDevices(ArrayList<Device> devices)
   {
     ArrayList<Device> result = new ArrayList<Device>();
     for (Device device : devices)
     {
       result.add(saveDevice(device));
     }
     return result;
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.openremote.modeler.client.rpc.DeviceRPCService#removeDevice(org.openremote.modeler.domain.Device)
    */
   public void deleteDevice(long id) {
      deviceService.deleteDevice(id);
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.openremote.modeler.client.rpc.DeviceRPCService#loadById(long)
    */
   public Device loadById(long id) {
      return deviceService.loadById(id);
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.openremote.modeler.client.rpc.DeviceRPCService#loadAll()
    */
   public List<Device> loadAll() {
      return deviceService.loadAll(userService.getAccount());
   }

   /**
    * Sets the user service.
    * 
    * @param userService the new user service
    */
   public void setUserService(UserService userService) {
      this.userService = userService;
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.openremote.modeler.client.rpc.DeviceRPCService#loadAll(org.openremote.modeler.domain.Account)
    */
   public List<Device> loadAll(Account account) {
      return null;
   }

   public Account getAccount() {
      return userService.getAccount();
   }
   
   
   public ArrayList<DeviceDTO> loadAllDTOs() {
     List<Device> devices = deviceService.loadAll(userService.getAccount());
     ArrayList<DeviceDTO> dtos = new ArrayList<DeviceDTO>();
     for (Device d : devices) {
       dtos.add(new DeviceDTO(d.getOid(), d.getDisplayName()));
     }
     return dtos;
  }

   public DeviceWithChildrenDTO loadDeviceWithChildrenDTOById(long oid) {
     Device device = deviceService.loadById(oid);
     DeviceWithChildrenDTO deviceDTO = new DeviceWithChildrenDTO(device.getOid(), device.getDisplayName());
     ArrayList<DeviceCommandDTO> dcDTOs = new ArrayList<DeviceCommandDTO>();
     for (DeviceCommand dc : device.getDeviceCommands()) {
       dcDTOs.add(new DeviceCommandDTO(dc.getOid(), dc.getDisplayName(), dc.getProtocol().getType()));
     }
     deviceDTO.setDeviceCommands(dcDTOs);
     ArrayList<SensorDTO> sensorDTOs = new ArrayList<SensorDTO>();
     for (Sensor sensor : device.getSensors()) {
       SensorDTO sensorDTO = new SensorDTO(sensor.getOid(), sensor.getDisplayName());
       DeviceCommand dc = sensor.getSensorCommandRef().getDeviceCommand();
       sensorDTO.setCommand(new DeviceCommandDTO(dc.getOid(), dc.getDisplayName(), dc.getProtocol().getType()));
       sensorDTOs.add(sensorDTO);
     }
     deviceDTO.setSensors(sensorDTOs);
     ArrayList<SwitchDTO> switchDTOs = new ArrayList<SwitchDTO>();
     for (Switch s : device.getSwitchs()) {
       SwitchDTO switchDTO = new SwitchDTO(s.getOid(), s.getDisplayName());
       DeviceCommand dc = s.getSwitchCommandOnRef().getDeviceCommand();
       switchDTO.setOnCommand(new DeviceCommandDTO(dc.getOid(), dc.getDisplayName(), dc.getProtocol().getType()));
       dc = s.getSwitchCommandOffRef().getDeviceCommand();
       switchDTO.setOffCommand(new DeviceCommandDTO(dc.getOid(), dc.getDisplayName(), dc.getProtocol().getType()));
       switchDTOs.add(switchDTO);
     }
     deviceDTO.setSwitches(switchDTOs);
     ArrayList<SliderDTO> sliderDTOs = new ArrayList<SliderDTO>();
     for (Slider s : device.getSliders()) {
       SliderDTO sliderDTO = new SliderDTO(s.getOid(), s.getDisplayName());
       DeviceCommand dc = s.getSetValueCmd().getDeviceCommand();
       sliderDTO.setCommand(new DeviceCommandDTO(dc.getOid(), dc.getDisplayName(), dc.getProtocol().getType()));
       sliderDTOs.add(sliderDTO);
     }
     deviceDTO.setSliders(sliderDTOs);
     return deviceDTO;
   }

   public DeviceDetailsDTO loadDeviceDetailsDTO(long oid) {
     Device device = deviceService.loadById(oid);
     return new DeviceDetailsDTO(device.getOid(), device.getName(), device.getVendor(), device.getModel());
   }

   public void updateDeviceWithDTO(DeviceDetailsDTO device) {
     Device deviceBean = deviceService.loadById(device.getOid());
     deviceBean.setName(device.getName());
     deviceBean.setVendor(device.getVendor());
     deviceBean.setModel(device.getModel());
     deviceService.updateDevice(deviceBean);
   }

}
