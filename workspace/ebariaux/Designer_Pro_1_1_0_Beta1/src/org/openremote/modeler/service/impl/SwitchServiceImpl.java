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
package org.openremote.modeler.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.openremote.modeler.dao.GenericDAO;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.exception.PersistenceException;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.SensorService;
import org.openremote.modeler.service.SwitchService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.shared.dto.SwitchDetailsDTO;
import org.openremote.modeler.shared.dto.SwitchWithInfoDTO;
import org.openremote.modeler.utils.dtoconverter.SwitchDTOConverter;
import org.springframework.transaction.annotation.Transactional;

public class SwitchServiceImpl extends BaseAbstractService<Switch> implements SwitchService {
	
   private UserService userService = null;
   private SensorService sensorService;
   private DeviceCommandService deviceCommandService;

  public void setSensorService(SensorService sensorService) {
	this.sensorService = sensorService;
  }

  public void setDeviceCommandService(DeviceCommandService deviceCommandService) {
	this.deviceCommandService = deviceCommandService;
  }

  @Override
   public Switch loadById(long id) {
     return genericDAO.getById(Switch.class, id);
   }
   
   @Override
   @Transactional
   public void delete(long id) {
      Switch switchToggle = genericDAO.loadById(Switch.class, id);
      genericDAO.delete(switchToggle);
   }

   @Override
   @Transactional
   public List<Switch> loadAll() {
      List<Switch> result = userService.getAccount().getSwitches();
      if (result == null || result.size() == 0) {
         return new ArrayList<Switch> ();
      }
      Hibernate.initialize(result);
      return result;
   }


   @Override
   @Transactional
   public Switch save(Switch switchToggle) {
      genericDAO.save(switchToggle);
      if (switchToggle.getSwitchSensorRef() != null) {
         Hibernate.initialize(switchToggle.getSwitchSensorRef().getSensor());
      }
      return switchToggle;
   }

   @Override
   @Transactional
   public Switch update(Switch switchToggle) {
      Switch old = genericDAO.loadById(Switch.class, switchToggle.getOid());
      old.setName(switchToggle.getName());

      /*
       * Relationship to commands and sensor must be explicitly managed.
       * It is mandatory to manually delete old reference entities before linking to new one
       * or a combinatory explosion occurs when fetching back the switch.
       */
      if (switchToggle.getSwitchCommandOffRef() != null
            && old.getSwitchCommandOffRef().getOid() != switchToggle.getSwitchCommandOffRef().getOid()) {
         genericDAO.delete(old.getSwitchCommandOffRef());
         old.setSwitchCommandOffRef(switchToggle.getSwitchCommandOffRef());
         switchToggle.getSwitchCommandOffRef().setOffSwitch(old);
      }
      if (switchToggle.getSwitchCommandOnRef() != null
            && old.getSwitchCommandOnRef().getOid() != switchToggle.getSwitchCommandOnRef().getOid()) {
         genericDAO.delete(old.getSwitchCommandOnRef());
         old.setSwitchCommandOnRef(switchToggle.getSwitchCommandOnRef());
         switchToggle.getSwitchCommandOnRef().setOnSwitch(old);
      }
      if (old.getSwitchSensorRef() != null
            && old.getSwitchSensorRef().getOid() != switchToggle.getSwitchSensorRef().getOid()) {
         genericDAO.delete(old.getSwitchSensorRef());
         old.setSwitchSensorRef(switchToggle.getSwitchSensorRef());
         switchToggle.getSwitchSensorRef().setSwitchToggle(old);
      }
      return old;
   }
   
   public UserService getUserService() {
      return userService;
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
   @Override public SwitchDetailsDTO loadSwitchDetailsDTO(long id)
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

   public List<Switch> loadSameSwitchs(Switch swh) {
      List<Switch> result = null;
      DetachedCriteria critera = DetachedCriteria.forClass(Switch.class);
      critera.add(Restrictions.eq("device.oid", swh.getDevice().getOid()));
      critera.add(Restrictions.eq("name", swh.getName()));
      result = genericDAO.findByDetachedCriteria(critera);
      if (result != null) {
         for(Iterator<Switch> iterator = result.iterator();iterator.hasNext();) {
            Switch tmp = iterator.next();
            if (! tmp.equalsWithoutCompareOid(swh)) {
               iterator.remove();
            }
         }
      }
      return result;
   }
   
   @Override
   public List<SwitchWithInfoDTO> loadAllSwitchWithInfosDTO() {
     ArrayList<SwitchWithInfoDTO> dtos = new ArrayList<SwitchWithInfoDTO>();
     List<Switch> switches = loadAll();
     for (Switch sw : switches) {
       dtos.add(sw.getSwitchWithInfoDTO());
     }
     return dtos;    
   }

   @Override
   @Transactional
   public void updateSwitchWithDTO(SwitchDetailsDTO switchDTO) {
     Switch sw = loadById(switchDTO.getOid());
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

     update(sw);
   }

   @Override
   @Transactional
   public void saveNewSwitch(SwitchDetailsDTO switchDTO, long deviceId) {
     Sensor sensor = sensorService.loadById(switchDTO.getSensor().getId());
     DeviceCommand onCommand = deviceCommandService.loadById(switchDTO.getOnCommand().getId());
     DeviceCommand offCommand = deviceCommandService.loadById(switchDTO.getOffCommand().getId());
     
     Switch sw = new Switch(onCommand, offCommand, sensor);
     sw.setName(switchDTO.getName());
     sw.setAccount(userService.getAccount());
     
     save(sw);
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
       return loadById(id);
     }

     catch (GenericDAO.DatabaseError e)
     {
       throw new PersistenceException("Unable to load switch ID {0} : {1}", id, e.getMessage());
     }
   }

}
