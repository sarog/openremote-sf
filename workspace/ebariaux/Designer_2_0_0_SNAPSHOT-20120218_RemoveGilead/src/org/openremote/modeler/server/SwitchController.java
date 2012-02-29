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

import java.util.List;

import org.openremote.modeler.client.rpc.SwitchRPCService;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.SensorService;
import org.openremote.modeler.service.SwitchService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.shared.dto.SwitchDetailsDTO;

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

   @Override
   public List<Switch> loadAll() {
      return switchService.loadAll();
   }

   @Override
   public Switch save(Switch switchToggle) {
      switchToggle.setAccount(userService.getAccount());
      return switchService.save(switchToggle);
   }

   
   @Override
   public Switch update(Switch switchToggle) {
      switchToggle.setAccount(userService.getAccount());
      return switchService.update(switchToggle);
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
     return new SwitchDetailsDTO(sw.getOid(), sw.getName(), sw.getSwitchSensorRef().getSensor().getOid(),
             sw.getSwitchCommandOnRef().getDeviceCommand().getOid(), sw.getSwitchCommandOnRef().getDeviceCommand().getDisplayName(),
             sw.getSwitchCommandOffRef().getDeviceCommand().getOid(), sw.getSwitchCommandOffRef().getDeviceCommand().getDisplayName());
   }

   @Override
   public void updateSwitchWithDTO(SwitchDetailsDTO switchDTO) {
     Switch sw = switchService.loadById(switchDTO.getOid());
     sw.setName(switchDTO.getName());
     
     if (sw.getSwitchSensorRef().getSensor().getOid() != switchDTO.getSensorId()) {
       Sensor sensor = sensorService.loadById(switchDTO.getSensorId());
       sw.getSwitchSensorRef().setSensor(sensor);
     }
     
     if (sw.getSwitchCommandOnRef().getDeviceCommand().getOid() != switchDTO.getOnCommandId()) {
       DeviceCommand dc = deviceCommandService.loadById(switchDTO.getOnCommandId());
       sw.getSwitchCommandOnRef().setDeviceCommand(dc);
     }
     
     if (sw.getSwitchCommandOffRef().getDeviceCommand().getOid() != switchDTO.getOffCommandId()) {
       DeviceCommand dc = deviceCommandService.loadById(switchDTO.getOffCommandId());
       sw.getSwitchCommandOffRef().setDeviceCommand(dc);
     }

     switchService.update(sw);
   }

}
