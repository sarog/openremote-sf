/*
 * OpenRemote, the Home of the Digital Home.
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

import org.openremote.modeler.client.rpc.SwitchRPCService;
import org.openremote.modeler.dao.GenericDAO;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.exception.PersistenceException;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.SensorService;
import org.openremote.modeler.service.SwitchService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.SwitchDTO;
import org.openremote.modeler.shared.dto.SwitchDetailsDTO;
import org.openremote.modeler.shared.dto.SwitchWithInfoDTO;
import org.openremote.modeler.utils.dtoconverter.SwitchDTOConverter;

/**
 * TODO :
 *
 *   The server side implementation of the RPC service <code>SwitchRPCService</code>.
 *
 *   Tasks to do :
 *
 *     - MODELER-313 -- return null reference in DTO if no sensor associated with switch
 *     - DTO transformation should logically go into the domain object implementation
 *     - This class should not access DB directly but to delegate to a REST API on Beehive
 *       that is responsible for persistent switch operations.
 *     - review the database load semantics, especially with regards to dependent objects
 *       in Switch, requires unit testing
 *
 *
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 * @author <a href = "mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class SwitchController extends BaseGWTSpringController implements SwitchRPCService {

   private SwitchService switchService;
   
   private UserService userService;
   
   @Override
   public void delete(long id) {
      switchService.delete(id);
   }

   public void setSwitchService(SwitchService switchService) {
      this.switchService = switchService;
   }

  public void setUserService(UserService userService) {
      this.userService = userService;
   }

  /**
   * Loads persistent switch state including dependent object states (optional associated
   * switch sensor, and mandatory switch 'on' and 'off' commands from the database and transforms
   * them into data transfer object graph that can be serialized to the client. In effect, this
   * method disconnects the persistent switch entities from client side processing.
   *
   * @param   id    the persistent switch entity identifier (primary key)
   *
   * @return  a data transfer object that contains the switch state including associated sensor
   *          and command state in a serializable object graph
   */
  @Override public SwitchDetailsDTO loadSwitchDetails(long id) {
    return switchService.loadSwitchDetailsDTO(id);
  }

  @Override
   public ArrayList<SwitchWithInfoDTO> loadAllSwitchWithInfosDTO() {
	   return new ArrayList<SwitchWithInfoDTO>(switchService.loadAllSwitchWithInfosDTO());
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
    switchDTO.setOnCommand(new DeviceCommandDTO(dc.getOid(), dc.getDisplayName(), dc.getFullyQualifiedName(), dc.getProtocol().getType()));
    dc = aSwitch.getSwitchCommandOffRef().getDeviceCommand();
    switchDTO.setOffCommand(new DeviceCommandDTO(dc.getOid(), dc.getDisplayName(), dc.getFullyQualifiedName(), dc.getProtocol().getType()));
    return switchDTO;
  }

  @Override
   public void updateSwitchWithDTO(SwitchDetailsDTO switchDTO) {
	  switchService.updateSwitchWithDTO(switchDTO);
   }

   @Override
   public void saveNewSwitch(SwitchDetailsDTO switchDTO, long deviceId) {
	   switchService.saveNewSwitch(switchDTO, deviceId);
   }
  
}
