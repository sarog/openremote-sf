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
package org.openremote.modeler.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.RangeSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorCommandRef;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.State;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.exception.PersistenceException;
import org.openremote.modeler.logging.AdministratorAlert;
import org.openremote.modeler.logging.LogFacade;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.DeviceMacroItemService;
import org.openremote.modeler.service.DeviceService;
import org.openremote.modeler.shared.dto.DeviceCommandDetailsDTO;
import org.openremote.modeler.shared.dto.DeviceDTO;
import org.openremote.modeler.shared.dto.DeviceDetailsDTO;
import org.openremote.modeler.shared.dto.DeviceDetailsWithChildrenDTO;
import org.openremote.modeler.shared.dto.DeviceWithChildrenDTO;
import org.openremote.modeler.shared.dto.SensorDetailsDTO;
import org.openremote.modeler.shared.dto.SliderDetailsDTO;
import org.openremote.modeler.shared.dto.SwitchDetailsDTO;
import org.openremote.modeler.utils.dtoconverter.DeviceDTOConverter;
import org.springframework.transaction.annotation.Transactional;


/**
 * TODO
 */
public class DeviceServiceImpl extends BaseAbstractService<Device> implements DeviceService {

  private static LogFacade persistenceLog = LogFacade.getInstance(LogFacade.Category.PERSISTENCE);

  private static AdministratorAlert admin =
      AdministratorAlert.getInstance(AdministratorAlert.Type.DATABASE);

   private DeviceMacroItemService deviceMacroItemService;

   /**
    * Sets the device macro item service.
    * 
    * @param deviceMacroItemService the new device macro item service
    */
   public void setDeviceMacroItemService(DeviceMacroItemService deviceMacroItemService) {
      this.deviceMacroItemService = deviceMacroItemService;
   }


   /**
    * {@inheritDoc}
    */
   @Transactional public Device saveDevice(Device device) {
      genericDAO.save(device);
      /*
      Hibernate.initialize(device.getSensors());
      Hibernate.initialize(device.getSwitchs());
      List<DeviceCommand> deviceCommands = device.getDeviceCommands();
      for(DeviceCommand cmd : deviceCommands ) {
         Hibernate.initialize(cmd.getProtocol().getAttributes());
      }
      Hibernate.initialize(device.getSliders());
      Hibernate.initialize(device.getDeviceAttrs());
      */
      return device;
   }

  @Transactional public void deleteDevice(long id) 
  {
    try
    {
      Device device;

      try
      {
        device = loadById(id);
      }

      catch (ObjectNotFoundException e)
      {
        persistenceLog.warn(
            "Attempted to delete non-existent device with id '{0}' -- Delete Ignored.", id
        );

        return;
      }

      for (DeviceCommand deviceCommand : device.getDeviceCommands())
      {
        deviceMacroItemService.deleteByDeviceCommand(deviceCommand);
      }

      genericDAO.delete(device);
    }

    catch (Throwable t)
    {
      persistenceLog.error(
          "Delete device operation (ID: {0}) failed : {1}", t, id, t.getMessage()
      );
    }
  }

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
  @Transactional public List<Device> loadAll(Account account) {
     DetachedCriteria criteria = DetachedCriteria.forClass(Device.class);
     criteria.add(Restrictions.eq("account", account));
      return genericDAO.getHibernateTemplate().findByCriteria(criteria, 0, 1);
   }

   /**
    * {@inheritDoc}
    */
   @Transactional public void updateDevice(Device device) {
      genericDAO.saveOrUpdate(device);
   }

   /**
    * {@inheritDoc}
    */
   @Transactional public Device loadById(long id) {
      Device device = super.loadById(id);
      if (device.getAccount() != null) {
         Hibernate.initialize(device.getAccount().getConfigs());
      }
      Hibernate.initialize(device.getDeviceCommands());
      Hibernate.initialize(device.getSensors());
      for (Sensor sensor : device.getSensors()) {
         if (SensorType.CUSTOM == sensor.getType()) {
            Hibernate.initialize(((CustomSensor)sensor).getStates());
         }
      }
      Hibernate.initialize(device.getSliders());


      try
      {
        Hibernate.initialize(device.getSwitchs());
      }

      catch (ObjectNotFoundException e)
      {
        Account acct = device.getAccount();

        String errorMessage = MessageFormat.format(
            "DATA INTEGRITY ERROR: Device ''{0}'' (ID : {1}) for " +
            "account (users : ''{2}'', ID: {3}) references a non-existent switch component: {4}",
            device.getName(), id, acct.getUsers(), acct.getOid(), e.getMessage()
        );

        admin.alert(errorMessage);

        PersistenceException.throwAsGWTClientException(
            "There was an error loading your device ''{0}''. Please contact an administrator " +
            "to solve this issue. Error message : {1}",
            device.getName(), errorMessage
        );
      }

      return device;
   }

   public List<Device> loadSameDevices(Device device) {
      DetachedCriteria critera = DetachedCriteria.forClass(Device.class);
      critera.add(Restrictions.eq("name", device.getName()));
      critera.add(Restrictions.eq("model", device.getModel()));
      critera.add(Restrictions.eq("vendor", device.getVendor()));
      critera.add(Restrictions.eq("account.oid", device.getAccount().getOid()));
      return genericDAO.findPagedDateByDetachedCriteria(critera, 1, 0);
   }

   /**
    * {@inheritDoc}
    */
   @Override   
   public ArrayList<DeviceDetailsDTO> loadAllDeviceDetailsDTOs(Account account) {
     List<Device> devices = loadAll(account);
     ArrayList<DeviceDetailsDTO> dtos = new ArrayList<DeviceDetailsDTO>();
     for (Device device : devices) {
       dtos.add(device.getDeviceDetailsDTO());
     }
     return dtos;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override   
   public ArrayList<DeviceDetailsWithChildrenDTO> loadAllDeviceDetailsWithChildrenDTOs(Account account) {
     List<Device> devices = loadAll(account);
     ArrayList<DeviceDetailsWithChildrenDTO> dtos = new ArrayList<DeviceDetailsWithChildrenDTO>();
     for (Device d : devices) {
       dtos.add(DeviceDTOConverter.createDeviceDetailsWithChildrenDTO(d));
     }
     return dtos;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public DeviceWithChildrenDTO loadDeviceWithChildrenDTOById(long oid) {
     Device device = loadById(oid);
     return (device != null)?device.getDeviceWithChildrenDTO():null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public DeviceWithChildrenDTO loadDeviceWithCommandChildrenDTOById(long oid) {
     Device device = loadById(oid);
     return (device != null)?device.getDeviceWithCommandChildrenDTO():null;
   }
 
   /**
    * {@inheritDoc}
    */
   @Override
  public Device saveNewDeviceWithChildren(Account account, DeviceDetailsDTO device, ArrayList<DeviceCommandDetailsDTO> commands, ArrayList<SensorDetailsDTO> sensors, ArrayList<SwitchDetailsDTO> switches, ArrayList<SliderDetailsDTO> sliders) {
    Device deviceBean = new Device(device.getName(), device.getVendor(), device.getModel());
    deviceBean.setAccount(account);
    account.getDevices().add(deviceBean);
    deviceBean.storeTransient(ORIGINAL_OID_KEY, device.getOid());

    Map<DeviceCommandDetailsDTO, DeviceCommand> commandBeans = new HashMap<DeviceCommandDetailsDTO, DeviceCommand>();
    if (commands != null) {
      for (DeviceCommandDetailsDTO command : commands) {
        DeviceCommand dc = new DeviceCommand();
        dc.setDevice(deviceBean);
  
        dc.setName(command.getName());
        Protocol protocol = new Protocol();
        protocol.setDeviceCommand(dc);
        dc.setProtocol(protocol);
        protocol.setType(command.getProtocolType());
        dc.storeTransient(ORIGINAL_OID_KEY, command.getOid());
        for (Map.Entry<String, String> e : command.getProtocolAttributes().entrySet()) {
          protocol.addProtocolAttribute(e.getKey(), e.getValue());
        }
        commandBeans.put(command, dc);
      }
    }
    deviceBean.setDeviceCommands(new ArrayList<DeviceCommand>(commandBeans.values()));

    Map<SensorDetailsDTO, Sensor> sensorBeans = new HashMap<SensorDetailsDTO, Sensor>();
    if (sensors != null) {
      for (SensorDetailsDTO sensorDTO : sensors) {
        Sensor sensor = null;
        if (sensorDTO.getType() == SensorType.RANGE) {
          sensor = new RangeSensor(sensorDTO.getMinValue(), sensorDTO.getMaxValue());
        } else if (sensorDTO.getType() == SensorType.CUSTOM) {
          CustomSensor customSensor = new CustomSensor();
          for (Map.Entry<String, String> e : sensorDTO.getStates().entrySet()) {
            customSensor.addState(new State(e.getKey(), e.getValue()));
          }
          sensor = customSensor;
        } else {
          sensor = new Sensor(sensorDTO.getType());
        }
  
        sensor.setDevice(deviceBean);
        sensor.setName(sensorDTO.getName());
        sensor.setAccount(account);
        account.getSensors().add(sensor);
        sensor.storeTransient(ORIGINAL_OID_KEY, sensorDTO.getOid());
  
        DeviceCommand deviceCommand = commandBeans.get(sensorDTO.getCommand().getDto());
        SensorCommandRef commandRef = new SensorCommandRef();
        commandRef.setSensor(sensor);
        commandRef.setDeviceCommand(deviceCommand);
        sensor.setSensorCommandRef(commandRef);
        sensorBeans.put(sensorDTO, sensor);
      }
    }
    deviceBean.setSensors(new ArrayList<Sensor>(sensorBeans.values()));

    List<Switch> switchBeans = new ArrayList<Switch>();
    if (switches != null) {
      for (SwitchDetailsDTO switchDTO : switches) {
        Sensor sensor = sensorBeans.get(switchDTO.getSensor().getDto());
        DeviceCommand onCommand = commandBeans.get(switchDTO.getOnCommand().getDto());
        DeviceCommand offCommand = commandBeans.get(switchDTO.getOffCommand().getDto());
  
        Switch sw = new Switch(onCommand, offCommand, sensor);
        sw.setName(switchDTO.getName());
        sw.setAccount(account);
        account.getSwitches().add(sw);
        sensor.storeTransient(ORIGINAL_OID_KEY, switchDTO.getOid());
        switchBeans.add(sw);
      }
    }
    deviceBean.setSwitchs(switchBeans);

    List<Slider> sliderBeans = new ArrayList<Slider>();
    if (sliders != null) {
      for (SliderDetailsDTO sliderDTO : sliders) {
        Sensor sensor = sensorBeans.get(sliderDTO.getSensor().getDto());
        DeviceCommand command = commandBeans.get(sliderDTO.getCommand().getDto());
  
        Slider slider = new Slider(sliderDTO.getName(), command, sensor);
        slider.setAccount(account);
        account.getSliders().add(slider);
        sensor.storeTransient(ORIGINAL_OID_KEY, sliderDTO.getOid());
        sliderBeans.add(slider);
      }
    }
    deviceBean.setSliders(sliderBeans);

    saveDevice(deviceBean);
    
    return deviceBean;
  }   

}