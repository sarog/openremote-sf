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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.MessageFormat;

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
import org.openremote.modeler.server.SensorController;
import org.openremote.modeler.server.SliderController;
import org.openremote.modeler.server.SwitchController;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.DeviceMacroItemService;
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
import org.openremote.modeler.logging.LogFacade;
import org.openremote.modeler.logging.AdministratorAlert;
import org.openremote.modeler.exception.PersistenceException;
import org.springframework.transaction.annotation.Transactional;


/**
 * TODO
 */
public class DeviceServiceImpl extends BaseAbstractService<Device> implements DeviceService {

  private static LogFacade persistenceLog = LogFacade.getInstance(LogFacade.Category.PERSISTENCE);

  private static AdministratorAlert admin =
      AdministratorAlert.getInstance(AdministratorAlert.Type.DATABASE);

   private DeviceMacroItemService deviceMacroItemService;
   
   private UserService userService;

   /**
    * Sets the device macro item service.
    * 
    * @param deviceMacroItemService the new device macro item service
    */
   public void setDeviceMacroItemService(DeviceMacroItemService deviceMacroItemService) {
      this.deviceMacroItemService = deviceMacroItemService;
   }

  public void setUserService(UserService userService) {
	this.userService = userService;
  }
  
   /**
    * {@inheritDoc}
    */
   @Override
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

  @Override
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
   @Override
   public List<DeviceDTO> loadAllDTOs(Account account) {
      List<Device> devices = account.getDevices();
      ArrayList<DeviceDTO> dtos = new ArrayList<DeviceDTO>();
      for (Device d : devices) {
        dtos.add(d.getDeviceDTO());
      }
      return dtos;
   }

   @Override
   public List<Device> loadAll(Account account) {
      return account.getDevices();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @Transactional public void updateDevice(Device device) {
      genericDAO.saveOrUpdate(device);
   }

   /**
    * {@inheritDoc}
    */
   @Override
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

   @Override
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
   public ArrayList<DeviceWithChildrenDTO> loadAllDeviceWithChildrenDTOs(Account account) {
     List<Device> devices = loadAll(account);
     ArrayList<DeviceWithChildrenDTO> dtos = new ArrayList<DeviceWithChildrenDTO>();
     for (Device d : devices) {
       dtos.add(createDeviceWithChildrenDTO(d));
     }
     return dtos;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override   
   public DeviceDetailsDTO loadDeviceDetailsDTOById(long id) {
     Device device = loadById(id);
     return device.getDeviceDetailsDTO();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public DeviceWithChildrenDTO loadDeviceWithChildrenDTOById(long oid) {
     Device device = loadById(oid);
     return createDeviceWithChildrenDTO(device);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public DeviceWithChildrenDTO loadDeviceWithCommandChildrenDTOById(long oid) {
     Device device = loadById(oid);
     DeviceWithChildrenDTO deviceDTO = new DeviceWithChildrenDTO(device.getOid(), device.getDisplayName());
     deviceDTO.setDeviceCommands(createDeviceCommandDTOs(device.getDeviceCommands()));
     return deviceDTO;
   }

   private DeviceWithChildrenDTO createDeviceWithChildrenDTO(Device device) {
     DeviceWithChildrenDTO deviceDTO = new DeviceWithChildrenDTO(device.getOid(), device.getDisplayName());
      deviceDTO.setDeviceCommands(createDeviceCommandDTOs(device.getDeviceCommands()));
      ArrayList<SensorDTO> sensorDTOs = new ArrayList<SensorDTO>();
      for (Sensor sensor : device.getSensors()) {
        sensorDTOs.add(sensor.getSensorDTO());
      }
      deviceDTO.setSensors(sensorDTOs);
      ArrayList<SwitchDTO> switchDTOs = new ArrayList<SwitchDTO>();
      for (Switch s : device.getSwitchs()) {
        switchDTOs.add(s.getSwitchDTO());
      }
      deviceDTO.setSwitches(switchDTOs);
      ArrayList<SliderDTO> sliderDTOs = new ArrayList<SliderDTO>();
      for (Slider s : device.getSliders()) {
        sliderDTOs.add(s.getSliderDTO());
      }
      deviceDTO.setSliders(sliderDTOs);
      return deviceDTO;
   }

   private ArrayList<DeviceCommandDTO> createDeviceCommandDTOs(List<DeviceCommand> deviceCommands) {
     ArrayList<DeviceCommandDTO> dcDTOs = new ArrayList<DeviceCommandDTO>();
      for (DeviceCommand dc : deviceCommands) {
        dcDTOs.add(dc.getDeviceCommandDTO());
      }
     return dcDTOs;
   }

   @Transactional
   @Override
   public DeviceDTO saveNewDevice(DeviceDetailsDTO deviceDTO) {
     Device device = new Device(deviceDTO.getName(), deviceDTO.getVendor(), deviceDTO.getModel());
     device.setAccount(userService.getAccount());
     saveDevice(device);
     return device.getDeviceDTO();
   }
   
   @Transactional
   @Override
   public Device saveNewDeviceWithChildren(DeviceDetailsDTO deviceDTO, List<DeviceCommandDetailsDTO> commands, List<SensorDetailsDTO> sensors,
           List<SwitchDetailsDTO> switches, List<SliderDetailsDTO> sliders) {
		Account account = userService.getAccount();

		Device device = new Device(deviceDTO.getName(), deviceDTO.getVendor(), deviceDTO.getModel());
		device.setAccount(account);
		device.storeTransient(ORIGINAL_OID_KEY, deviceDTO.getOid());

		Map<DeviceCommandDetailsDTO, DeviceCommand> commandBeans = new HashMap<DeviceCommandDetailsDTO, DeviceCommand>();
		if (commands != null) {
			for (DeviceCommandDetailsDTO command : commands) {
				DeviceCommand dc = new DeviceCommand();
				dc.setDevice(device);
	
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
		device.setDeviceCommands(new ArrayList<DeviceCommand>(commandBeans.values()));

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
	
				sensor.setDevice(device);
				sensor.setName(sensorDTO.getName());
				sensor.setAccount(account);
				sensor.storeTransient(ORIGINAL_OID_KEY, sensorDTO.getOid());
	
				DeviceCommand deviceCommand = commandBeans.get(sensorDTO.getCommand().getDto());
				SensorCommandRef commandRef = new SensorCommandRef();
				commandRef.setSensor(sensor);
				commandRef.setDeviceCommand(deviceCommand);
				sensor.setSensorCommandRef(commandRef);
				sensorBeans.put(sensorDTO, sensor);
			}
		}
		device.setSensors(new ArrayList<Sensor>(sensorBeans.values()));

		List<Switch> switchBeans = new ArrayList<Switch>();
		if (switches != null) {
			for (SwitchDetailsDTO switchDTO : switches) {
				Sensor sensor = sensorBeans.get(switchDTO.getSensor().getDto());
				DeviceCommand onCommand = commandBeans.get(switchDTO.getOnCommand().getDto());
				DeviceCommand offCommand = commandBeans.get(switchDTO.getOffCommand().getDto());
	
				Switch sw = new Switch(onCommand, offCommand, sensor);
				sw.setName(switchDTO.getName());
				sw.setAccount(account);
				sw.storeTransient(ORIGINAL_OID_KEY, switchDTO.getOid());
				switchBeans.add(sw);
			}
		}
		device.setSwitchs(switchBeans);

		List<Slider> sliderBeans = new ArrayList<Slider>();
		if (sliders != null) {
			for (SliderDetailsDTO sliderDTO : sliders) {
				Sensor sensor = sensorBeans.get(sliderDTO.getSensor().getDto());
				DeviceCommand command = null;
				if (sliderDTO.getCommand() != null) { // Passive sliders don't have a command
				    command = commandBeans.get(sliderDTO.getCommand().getDto());
				}
	
				Slider slider = new Slider(sliderDTO.getName(), command, sensor);
				slider.setAccount(account);
				slider.storeTransient(ORIGINAL_OID_KEY, sliderDTO.getOid());
				sliderBeans.add(slider);
			}
		}
		device.setSliders(sliderBeans);

		saveDevice(device);
		return device;
	}

   @Transactional
   @Override
   public void updateDeviceWithDTO(DeviceDetailsDTO deviceDTO) {
     Device device = loadById(deviceDTO.getOid());
     device.setName(deviceDTO.getName());
     device.setVendor(deviceDTO.getVendor());
     device.setModel(deviceDTO.getModel());
     updateDevice(device);
   }

}