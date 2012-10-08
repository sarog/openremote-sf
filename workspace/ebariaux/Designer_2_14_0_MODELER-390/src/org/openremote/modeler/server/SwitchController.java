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



  /**
   * Loads persistent switch state including dependent object states (optional associated
   * switch sensor, and mandatory switch 'on' and 'off' commands from the database and transforms
   * them into data transfer object graph that can be serialized to the client. In effect, this
   * method disconnects the persistent switch entities from client side processing.
   *
   * TODO :
   *    - the persistent entity transformation to data transfer objects logically belongs into
   *      the domain classes -- this avoids some unneeded data shuffling that helps maintain the
   *      domain object API independent of serialization requirements and restricts the domain
   *      object use to a smaller set of classes which helps with later refactoring
   *
   * @param   id    the persistent switch entity identifier (primary key)
   *
   * @return  a data transfer object that contains the switch state including associated sensor
   *          and command state in a serializable object graph
   */
  @Override public SwitchDetailsDTO loadSwitchDetails(long id)
  {
    Switch sw;

    try
    {
      // database load, see Switch class annotations for database access patterns

      sw = loadSwitch(id);
      
      return SwitchDTOConverter.createSwitchDetailsDTO(sw);
    }

    catch (PersistenceException e)
    {
      // TODO : 
      //    the requested switch ID was not found or could not be read from the database
      //    for some reason -- rethrowing as runtime error for now until can review proper
      //    error handling mechanism / implementation
      //                                                                    [JPL]

      throw new Error("Switch ID " + id + " could not be loaded : " + e.getMessage());
    }
  }


   
   @Override
   public ArrayList<SwitchWithInfoDTO> loadAllSwitchWithInfosDTO() {
     ArrayList<SwitchWithInfoDTO> dtos = new ArrayList<SwitchWithInfoDTO>();
     for (Switch sw : switchService.loadAll()) {
       dtos.add(sw.getSwitchWithInfoDTO());
     }
     return dtos;    
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



  // Private Instance Methods ---------------------------------------------------------------------

  /**
   * Loads a switch definition from a database, including dependent objects such
   * as an associated sensor and associated 'on' and 'off' commands.
   *
   * @see org.openremote.modeler.domain.Switch
   *
   * @param   id    switch identifier (primary key)
   *
   * @return  persistent switch entity
   *
   * @throws  PersistenceException    if the database load operation fails
   */
  private Switch loadSwitch(long id) throws PersistenceException
  {
    try
    {
      return switchService.loadById(id);
    }

    catch (GenericDAO.DatabaseError e)
    {
      throw new PersistenceException("Unable to load switch ID {0} : {1}", id, e.getMessage());
    }
  }
  
}
