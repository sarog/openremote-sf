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

import org.openremote.modeler.client.rpc.SensorRPCService;
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.RangeSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorCommandRef;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.State;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.DeviceService;
import org.openremote.modeler.service.SensorService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.shared.dto.DTOReference;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.SensorDTO;
import org.openremote.modeler.shared.dto.SensorDetailsDTO;
import org.openremote.modeler.shared.dto.SensorWithInfoDTO;

/**
 * The server side implementation of the RPC service <code>SensorRPCService</code>.
 */
public class SensorController extends BaseGWTSpringController implements SensorRPCService {

   private static final long serialVersionUID = 7122839354773238989L;

   private SensorService sensorService;
   private DeviceService deviceService;
   private DeviceCommandService deviceCommandService;
   
   private UserService userService;
   
   public Boolean deleteSensor(long id) {
      return sensorService.deleteSensor(id);
   }

   public void setSensorService(SensorService sensorService) {
      this.sensorService = sensorService;
   }
   
   public void setUserService(UserService userService) {
      this.userService = userService;
   }
   
   public void setDeviceService(DeviceService deviceService) {
    this.deviceService = deviceService;
  }

  public void setDeviceCommandService(DeviceCommandService deviceCommandService) {
    this.deviceCommandService = deviceCommandService;
  }

   @Override   
   public ArrayList<SensorDTO> loadSensorDTOsByDeviceId(long id) {
     ArrayList<SensorDTO> dtos = new ArrayList<SensorDTO>();
     for (Sensor s : sensorService.loadByDeviceId(id)) {
       dtos.add(new SensorDTO(s.getOid(), s.getDisplayName(), s.getType()));
     }
     return dtos;
   }
   
   @Override
   public SensorDetailsDTO loadSensorDetails(long id) {
     Sensor sensor = sensorService.loadById(id);
     return (sensor != null)?sensor.getSensorDetailsDTO():null;
  }
   
   @Override
  public ArrayList<SensorWithInfoDTO> loadAllSensorWithInfosDTO() {
     ArrayList<SensorWithInfoDTO> dtos = new ArrayList<SensorWithInfoDTO>();
     for (Sensor sensor : sensorService.loadAll(userService.getAccount())) {
       dtos.add(sensor.getSensorWithInfoDTO());
     }
     return dtos;    
  }

   @Override
  public void updateSensorWithDTO(SensorDetailsDTO sensor) {
     sensorService.updateSensorWithDTO(sensor);
  }

  public void saveNewSensor(SensorDetailsDTO sensorDTO, long deviceId) {
    Sensor sensor = null;
    if (sensorDTO.getType() == SensorType.RANGE) {
      sensor = new RangeSensor(sensorDTO.getMinValue(), sensorDTO.getMaxValue());
   } else if (sensorDTO.getType() == SensorType.CUSTOM) {
     CustomSensor customSensor = new CustomSensor();
     for (Map.Entry<String,String> e : sensorDTO.getStates().entrySet()) {
       customSensor.addState(new State(e.getKey(), e.getValue()));
     }
     sensor = customSensor;

   } else {
     sensor = new Sensor(sensorDTO.getType());
   }
    
    Device device = deviceService.loadById(deviceId);
    sensor.setDevice(device);
    sensor.setName(sensorDTO.getName());
    sensor.setAccount(userService.getAccount());

    DeviceCommand deviceCommand = deviceCommandService.loadById(sensorDTO.getCommand().getId());
    SensorCommandRef commandRef = new SensorCommandRef();
    commandRef.setSensor(sensor);
    commandRef.setDeviceCommand(deviceCommand);
    sensor.setSensorCommandRef(commandRef);
    
    sensorService.saveSensor(sensor);
  }

}
