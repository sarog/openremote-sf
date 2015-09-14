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

import org.openremote.modeler.client.rpc.SensorRPCService;
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.RangeSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.State;
import org.openremote.modeler.service.SensorService;
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
   
   public Boolean deleteSensor(long id) {
      return sensorService.deleteSensor(id);
   }

   public void setSensorService(SensorService sensorService) {
      this.sensorService = sensorService;
   }

   @Override   
   public ArrayList<SensorDTO> loadSensorDTOsByDeviceId(long id) {
      return new ArrayList<SensorDTO>(sensorService.loadSensorDTOsByDeviceId(id));
   }
   
   @Override
   public SensorDetailsDTO loadSensorDetails(long id) {
	   return sensorService.loadSensorDetailsDTO(id);
  }
   
   @Override
  public ArrayList<SensorWithInfoDTO> loadAllSensorWithInfosDTO() {
	   return new ArrayList<SensorWithInfoDTO>(sensorService.loadAllSensorWithInfosDTO());
  }

  public static SensorWithInfoDTO createSensorWithInfoDTO(Sensor sensor) {
    if (sensor.getType() == SensorType.RANGE) {
       return new SensorWithInfoDTO(sensor.getOid(), sensor.getDisplayName(),
               sensor.getType(), sensor.getSensorCommandRef().getDisplayName(),
               Integer.toString(((RangeSensor)sensor).getMin()),
               Integer.toString(((RangeSensor)sensor).getMax()), null);
    } else if (sensor.getType() == SensorType.CUSTOM) {
       CustomSensor customSensor = (CustomSensor)sensor;
       ArrayList<String> states = new ArrayList<String>();
       for (State state : customSensor.getStates()) {
         states.add(state.getName());
       }
       return new SensorWithInfoDTO(sensor.getOid(), sensor.getDisplayName(),
               sensor.getType(), sensor.getSensorCommandRef().getDisplayName(), null, null, states);
    } else {
      return new SensorWithInfoDTO(sensor.getOid(), sensor.getDisplayName(),
              sensor.getType(), sensor.getSensorCommandRef().getDisplayName(), null, null, null);
    }
  }
   
  public static SensorDTO createSensorDTO(Sensor sensor) {
    SensorDTO sensorDTO = new SensorDTO(sensor.getOid(), sensor.getDisplayName(), sensor.getType());
    DeviceCommand dc = sensor.getSensorCommandRef().getDeviceCommand();
    sensorDTO.setCommand(new DeviceCommandDTO(dc.getOid(), dc.getDisplayName(), dc.getFullyQualifiedName(), dc.getProtocol().getType()));
    return sensorDTO;
  }
  
   @Override
  public void updateSensorWithDTO(SensorDetailsDTO sensor) {
     sensorService.updateSensorWithDTO(sensor);
  }

  public void saveNewSensor(SensorDetailsDTO sensorDTO, long deviceId) {
    sensorService.saveNewSensor(sensorDTO, deviceId);
  }

}
