/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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

import org.openremote.modeler.client.rpc.SwitchRPCService;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.SensorService;
import org.openremote.modeler.service.SwitchService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.shared.dto.DTOReference;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.SwitchDTO;
import org.openremote.modeler.shared.dto.SwitchDetailsDTO;
import org.openremote.modeler.shared.dto.SwitchWithInfoDTO;

/**
 * The server side implementation of the RPC service <code>SwitchRPCService</code>.
 */
@SuppressWarnings("serial")
public class SwitchController extends BaseGWTSpringController implements SwitchRPCService {

   private SwitchService switchService;
   private SensorService sensorService;
   private DeviceCommandService deviceCommandService;
   
   private UserService userService;
   
   @Override
   public void delete(long id) {
      switchService.delete(id);
   }

   public void setSwitchService(SwitchService switchService) {
      this.switchService = switchService;
   }

   public void setSensorService(SensorService sensorService) {
    this.sensorService = sensorService;
  }

  public void setDeviceCommandService(DeviceCommandService deviceCommandService) {
    this.deviceCommandService = deviceCommandService;
  }

  public void setUserService(UserService userService) {
      this.userService = userService;
   }
   
   @Override
   public SwitchDetailsDTO loadSwitchDetails(long id) {
     Switch sw = switchService.loadById(id);
     return new SwitchDetailsDTO(sw.getOid(), sw.getName(), new DTOReference(sw.getSwitchSensorRef().getSensor().getOid()),
             new DTOReference(sw.getSwitchCommandOnRef().getDeviceCommand().getOid()), sw.getSwitchCommandOnRef().getDeviceCommand().getDisplayName(),
             new DTOReference(sw.getSwitchCommandOffRef().getDeviceCommand().getOid()), sw.getSwitchCommandOffRef().getDeviceCommand().getDisplayName());
   }
   
   @Override
   public ArrayList<SwitchWithInfoDTO> loadAllSwitchWithInfosDTO() {
     ArrayList<SwitchWithInfoDTO> dtos = new ArrayList<SwitchWithInfoDTO>();
     for (Switch sw : switchService.loadAll()) {
       dtos.add(createSwitchWithInfoDTO(sw));
     }
     return dtos;    
   }

  public static SwitchWithInfoDTO createSwitchWithInfoDTO(Switch aSwitch) {
    return new SwitchWithInfoDTO(aSwitch.getOid(), aSwitch.getDisplayName(),
                  (aSwitch.getSwitchCommandOnRef() != null)?aSwitch.getSwitchCommandOnRef().getDisplayName():null,
                  (aSwitch.getSwitchCommandOffRef() != null)?aSwitch.getSwitchCommandOffRef().getDisplayName():null,
                  (aSwitch.getSwitchSensorRef() != null)?aSwitch.getSwitchSensorRef().getDisplayName():null,
                  aSwitch.getDevice().getDisplayName());
  }
  
  public static SwitchDTO createSwitchDTO(Switch aSwitch) {
    SwitchDTO switchDTO = new SwitchDTO(aSwitch.getOid(), aSwitch.getDisplayName());
    DeviceCommand dc = aSwitch.getSwitchCommandOnRef().getDeviceCommand();
    switchDTO.setOnCommand(new DeviceCommandDTO(dc.getOid(), dc.getDisplayName(), dc.getProtocol().getType()));
    dc = aSwitch.getSwitchCommandOffRef().getDeviceCommand();
    switchDTO.setOffCommand(new DeviceCommandDTO(dc.getOid(), dc.getDisplayName(), dc.getProtocol().getType()));
    return switchDTO;
  }

  @Override
   public void updateSwitchWithDTO(SwitchDetailsDTO switchDTO) {
     Switch sw = switchService.loadById(switchDTO.getOid());
     sw.setName(switchDTO.getName());
     
     if (sw.getSwitchSensorRef().getSensor().getOid() != switchDTO.getSensor().getId()) {
       Sensor sensor = sensorService.loadById(switchDTO.getSensor().getId());
       sw.getSwitchSensorRef().setSensor(sensor);
     }
     
     if (sw.getSwitchCommandOnRef().getDeviceCommand().getOid() != switchDTO.getOnCommand().getId()) {
       DeviceCommand dc = deviceCommandService.loadById(switchDTO.getOnCommand().getId());
       sw.getSwitchCommandOnRef().setDeviceCommand(dc);
     }
     
     if (sw.getSwitchCommandOffRef().getDeviceCommand().getOid() != switchDTO.getOffCommand().getId()) {
       DeviceCommand dc = deviceCommandService.loadById(switchDTO.getOffCommand().getId());
       sw.getSwitchCommandOffRef().setDeviceCommand(dc);
     }

     switchService.update(sw);
   }

   @Override
   public void saveNewSwitch(SwitchDetailsDTO switchDTO, long deviceId) {
     Sensor sensor = sensorService.loadById(switchDTO.getSensor().getId());
     DeviceCommand onCommand = deviceCommandService.loadById(switchDTO.getOnCommand().getId());
     DeviceCommand offCommand = deviceCommandService.loadById(switchDTO.getOffCommand().getId());
     
     Switch sw = new Switch(onCommand, offCommand, sensor);
     sw.setName(switchDTO.getName());
     sw.setAccount(userService.getAccount());
     
     switchService.save(sw);
   }

}
