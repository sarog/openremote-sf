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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.openremote.devicediscovery.domain.DiscoveredDeviceDTO;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.rpc.DeviceDiscoveryRPCService;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.RangeSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorCommandRef;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.domain.User;
import org.openremote.modeler.exception.DeviceDiscoveryException;
import org.openremote.modeler.service.DeviceService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.shared.dto.DeviceDTO;
import org.openremote.rest.GenericResourceResultWithErrorMessage;
import org.restlet.data.ChallengeScheme;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * The Class is for managing linked controller
 */
public class DeviceDiscoveryService extends BaseGWTSpringController implements DeviceDiscoveryRPCService {

  private static final long serialVersionUID = -7208047346538344305L;
  private static Logger log = Logger.getLogger(DeviceDiscoveryService.class);
  
  private Configuration configuration;
  private UserService userService;
  protected DeviceService deviceService;

  public void setConfiguration(Configuration configuration)
  {
    this.configuration = configuration;
  }

  public void setUserService(UserService userService)
  {
    this.userService = userService;
  }
  
  public void setDeviceService(DeviceService deviceService)
  {
    this.deviceService = deviceService;
  }


  @SuppressWarnings("unchecked")
  @Override
  public ArrayList<DiscoveredDeviceDTO> loadDevices(boolean onlyNew) throws DeviceDiscoveryException
  {
    User currentUser = userService.getCurrentUser();
    String url = configuration.getDeviceDiscoveryServiceRESTRootUrl() + "discoveredDevices";
    if (onlyNew) {
      url += "?used=false";
    }
    ClientResource cr = new ClientResource(url);
    cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, currentUser.getUsername(), currentUser.getPassword());
    Representation r = cr.get();
    cr.release();
    String str;
    try
    {
      str = r.getText();
    } catch (IOException e) 
    {
      log.error("Error calling DeviceDiscovery rest service while loading discovered devices", e);
      throw new DeviceDiscoveryException(e.getMessage());
    }
    GenericResourceResultWithErrorMessage res =new JSONDeserializer<GenericResourceResultWithErrorMessage>().use(null, GenericResourceResultWithErrorMessage.class).use("result", ArrayList.class).use("result.values", DiscoveredDeviceDTO.class).deserialize(str); 
    ArrayList<DiscoveredDeviceDTO> result = (ArrayList<DiscoveredDeviceDTO>)res.getResult(); 
    if (res.getErrorMessage() != null) {
      log.error("Error calling DeviceDiscovery rest service while loading discovered devices " + res.getErrorMessage() );
      throw new DeviceDiscoveryException(res.getErrorMessage());
    } 
    return result;
  }

  @Override
  public void deleteDevices(ArrayList<DiscoveredDeviceDTO> devicesToDelete) throws DeviceDiscoveryException
  {
    User currentUser = userService.getCurrentUser();
    for (DiscoveredDeviceDTO device: devicesToDelete)
    {
      String url = configuration.getDeviceDiscoveryServiceRESTRootUrl() + "discoveredDevices/" + device.getOid();
      ClientResource cr = new ClientResource(url);
      cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, currentUser.getUsername(), currentUser.getPassword());
      Representation result = cr.delete();
      String str;
      try
      {
        str = result.getText();
      } catch (IOException e)
      {
        log.error("Error calling DeviceDiscovery rest service while deleting discovered devices", e);
        throw new DeviceDiscoveryException(e.getMessage());
      }
      GenericResourceResultWithErrorMessage res =new JSONDeserializer<GenericResourceResultWithErrorMessage>().use(null, GenericResourceResultWithErrorMessage.class).use("result", String.class).deserialize(str); 
      if (res.getErrorMessage() != null) {
        log.error("Error calling DeviceDiscovery rest service while deleting discovered devices " + res.getErrorMessage() );
        throw new DeviceDiscoveryException(res.getErrorMessage());
      } 
    }
  }

  @Override
  public ArrayList<DeviceDTO> createORDevices(ArrayList<DiscoveredDeviceDTO> itemsToCreate, boolean oneDevicePerProtocol) throws DeviceDiscoveryException
  {
    Account account = userService.getAccount();
    ArrayList<Device> deviceList = new ArrayList<Device>();
    HashMap<String, Device> protocolDevices = new HashMap<String, Device>();
    Device protocolDevice = null;
    for (DiscoveredDeviceDTO discoveredDeviceDTO : itemsToCreate)
    {
      protocolDevice = null;
      if (oneDevicePerProtocol) {
        if (protocolDevices.get(discoveredDeviceDTO.getProtocol()) == null) {
          protocolDevice = new Device();
          protocolDevice.setAccount(account);
          protocolDevice.setModel("multiple devices");
          protocolDevice.setName(discoveredDeviceDTO.getProtocol());
          protocolDevice.setVendor("N/A");
          protocolDevices.put(discoveredDeviceDTO.getProtocol(), protocolDevice);
        } else {
          protocolDevice = protocolDevices.get(discoveredDeviceDTO.getProtocol());
        }
      }
      Device device = createDevice(discoveredDeviceDTO, protocolDevice, account);
      deviceList.add(deviceService.saveDevice(device));
      markDeviceAsUsed(discoveredDeviceDTO);
    }
    
    ArrayList<DeviceDTO> dtos = new ArrayList<DeviceDTO>();
    if (oneDevicePerProtocol) {
      dtos.add(new DeviceDTO(protocolDevice.getOid(), protocolDevice.getDisplayName()));
    } else {
      for (Device dev : deviceList) {
        dtos.add(new DeviceDTO(dev.getOid(), dev.getDisplayName()));
      }
    }
    return dtos;
  }

  public void markDeviceAsUsed(DiscoveredDeviceDTO deviceToUpdate) throws DeviceDiscoveryException
  {
    User currentUser = userService.getCurrentUser();
    String url = configuration.getDeviceDiscoveryServiceRESTRootUrl() + "discoveredDevices/" + deviceToUpdate.getOid();
    ClientResource cr = new ClientResource(url);
    cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, currentUser.getUsername(), currentUser.getPassword());
    deviceToUpdate.setUsed(true);
    Representation rep = new JsonRepresentation(new JSONSerializer().exclude("*.class").deepSerialize(deviceToUpdate));
    Representation result = cr.put(rep);
    String str;
    try
    {
      str = result.getText();
    } catch (IOException e)
    {
      log.error("Error calling DeviceDiscovery rest service while marking discovered device as used", e);
      throw new DeviceDiscoveryException(e.getMessage());
    }
    GenericResourceResultWithErrorMessage res =new JSONDeserializer<GenericResourceResultWithErrorMessage>().use(null, GenericResourceResultWithErrorMessage.class).use("result", DiscoveredDeviceDTO.class).deserialize(str); 
    if (res.getErrorMessage() != null) {
      log.error("Error calling DeviceDiscovery rest service while marking discovered device as uses " + res.getErrorMessage() );
      throw new DeviceDiscoveryException(res.getErrorMessage());
    } 
  }

  private Device createDevice(DiscoveredDeviceDTO discoveredDeviceDTO, Device protocolDevice, Account account) throws DeviceDiscoveryException
  {
    String name = discoveredDeviceDTO.getName();
    Device device;
    if (protocolDevice != null) {
      device = protocolDevice;
    } else {
      device = new Device();
      device.setAccount(account);
      device.setModel(discoveredDeviceDTO.getModel());
      device.setName(discoveredDeviceDTO.getProtocol()+"-"+name);
      device.setVendor("N/A");  //TODO could be something like discoveredDeviceDTO.getVendor()
    }
    
    if (discoveredDeviceDTO.getProtocol().equalsIgnoreCase("zwave")) {
      addZwaveCommands(device, discoveredDeviceDTO);
      addZwaveSensors(device, discoveredDeviceDTO);
      addZwaveSwitches(device, name);
      addZwaveSliders(device, name);
    } else {
      throw new DeviceDiscoveryException("Only Z-Wave is supported for the Device Wizard in the moment");
    }
   
    return device;
  }
  
  private void addZwaveCommands(Device device, DiscoveredDeviceDTO discoveredDeviceDTO)
  {
    if (discoveredDeviceDTO.getType().equalsIgnoreCase("switch") || discoveredDeviceDTO.getType().equalsIgnoreCase("dimmer")) {
      DeviceCommand deviceCommand = new DeviceCommand();
      deviceCommand.setDevice(device);
      deviceCommand.setName(discoveredDeviceDTO.getName()+ "-On");
      HashMap<String, String> attrMap = new HashMap<String, String>();
      attrMap.put("nodeId", discoveredDeviceDTO.getDeviceAttrs().get(0).getValue());
      attrMap.put("command", "on");
      deviceCommand.createProtocolWithAttributes("Z-Wave", attrMap);
      device.getDeviceCommands().add(deviceCommand);
      
      deviceCommand = new DeviceCommand();
      deviceCommand.setDevice(device);
      deviceCommand.setName(discoveredDeviceDTO.getName()+ "-Off");
      attrMap = new HashMap<String, String>();
      attrMap.put("nodeId", discoveredDeviceDTO.getDeviceAttrs().get(0).getValue());
      attrMap.put("command", "off");
      deviceCommand.createProtocolWithAttributes("Z-Wave", attrMap);
      device.getDeviceCommands().add(deviceCommand);
      
      deviceCommand = new DeviceCommand();
      deviceCommand.setDevice(device);
      deviceCommand.setName(discoveredDeviceDTO.getName()+ "-Status");
      attrMap = new HashMap<String, String>();
      attrMap.put("nodeId", discoveredDeviceDTO.getDeviceAttrs().get(0).getValue());
      attrMap.put("command", "status");
      deviceCommand.createProtocolWithAttributes("Z-Wave", attrMap);
      device.getDeviceCommands().add(deviceCommand);
    }
    
    if (discoveredDeviceDTO.getType().equalsIgnoreCase("dimmer")) {
      DeviceCommand deviceCommand = new DeviceCommand();
      deviceCommand.setDevice(device);
      deviceCommand.setName(discoveredDeviceDTO.getName()+ "-Dim");
      HashMap<String, String> attrMap = new HashMap<String, String>();
      attrMap.put("nodeId", discoveredDeviceDTO.getDeviceAttrs().get(0).getValue());
      attrMap.put("command", "dim");
      deviceCommand.createProtocolWithAttributes("Z-Wave", attrMap);
      device.getDeviceCommands().add(deviceCommand);
    }
    
    if (discoveredDeviceDTO.getType().equalsIgnoreCase("BinarySensor") || discoveredDeviceDTO.getType().equalsIgnoreCase("Thermostat")) {
      DeviceCommand deviceCommand = new DeviceCommand();
      deviceCommand.setDevice(device);
      deviceCommand.setName(discoveredDeviceDTO.getName()+ "-Status");
      HashMap<String, String> attrMap = new HashMap<String, String>();
      attrMap.put("nodeId", discoveredDeviceDTO.getDeviceAttrs().get(0).getValue());
      attrMap.put("command", "status");
      deviceCommand.createProtocolWithAttributes("Z-Wave", attrMap);
      device.getDeviceCommands().add(deviceCommand);
    }

    if (discoveredDeviceDTO.getType().equalsIgnoreCase("Thermostat")) {
      DeviceCommand deviceCommand = new DeviceCommand();
      deviceCommand.setDevice(device);
      deviceCommand.setName(discoveredDeviceDTO.getName()+ "-Temp");
      HashMap<String, String> attrMap = new HashMap<String, String>();
      attrMap.put("nodeId", discoveredDeviceDTO.getDeviceAttrs().get(0).getValue());
      attrMap.put("command", "temp");
      deviceCommand.createProtocolWithAttributes("Z-Wave", attrMap);
      device.getDeviceCommands().add(deviceCommand);
    }
    
  }
  
  
  private void addZwaveSensors(Device device, DiscoveredDeviceDTO discoveredDeviceDTO)
  {
    String name = discoveredDeviceDTO.getName();
    List<DeviceCommand> commands = device.getDeviceCommands();
    for (DeviceCommand deviceCommand : commands) {
      if (deviceCommand.getName().equals(name+"-Status")) {
        if ((discoveredDeviceDTO.getType().equalsIgnoreCase("Switch")) || (discoveredDeviceDTO.getType().equalsIgnoreCase("BinarySensor"))) {
          Sensor sensor = createSensor(name+"-OnOffSensor",SensorType.SWITCH, deviceCommand, device, 0, 0);
          device.getSensors().add(sensor);
        }
        if (discoveredDeviceDTO.getType().equalsIgnoreCase("Dimmer")) {
          Sensor sensor = createSensor(name+"-OnOffSensor",SensorType.SWITCH, deviceCommand, device, 0, 0);
          device.getSensors().add(sensor);
          sensor = createSensor(name+"-DimSensor",SensorType.RANGE, deviceCommand, device, 0, 100);
          device.getSensors().add(sensor);
        }
        if (discoveredDeviceDTO.getType().equalsIgnoreCase("Thermostat")) {
          Sensor sensor = createSensor(name+"-BatterySensor",SensorType.CUSTOM, deviceCommand, device, 0, 0);
          device.getSensors().add(sensor);
          sensor = createSensor(name+"-TempSensor", SensorType.RANGE, deviceCommand, device, 15, 30);
          device.getSensors().add(sensor);
        }
      }
    }
  }

  private Sensor createSensor(String sensorName, SensorType type, DeviceCommand command, Device device, int min, int max) {
    Sensor sensor;
    if (type == SensorType.RANGE) {
      sensor = new RangeSensor();
      ((RangeSensor) sensor).setMin(min);
      ((RangeSensor) sensor).setMax(max);
    } else if (type == SensorType.CUSTOM) {
      sensor = new CustomSensor();
    } else {
      sensor = new Sensor();
    }
    sensor.setType(type);
    sensor.setName(sensorName);
  
    SensorCommandRef sensorCommandRef = new SensorCommandRef();
    sensorCommandRef.setDeviceCommand(command);
    sensorCommandRef.setSensor(sensor);
    sensor.setSensorCommandRef(sensorCommandRef);
    sensor.setDevice(device);
    sensor.setAccount(device.getAccount());
    return sensor;
  }
  
  private void addZwaveSwitches(Device device, String name)
  {
    DeviceCommand on = null;
    DeviceCommand off = null;
    Sensor switchSensor = null;
    for (DeviceCommand deviceCommand : device.getDeviceCommands()) {
      if (deviceCommand.getName().equals(name+"-On")) {
        on = deviceCommand;
      }
      else if (deviceCommand.getName().equals(name+"-Off")) {
        off = deviceCommand;
      }
    }
    for (Sensor sensor : device.getSensors()) {
      if (sensor.getName().equals(name+"-OnOffSensor")) {
        switchSensor = sensor;
      }
    }
    if ((on!=null) && (off!=null) && (switchSensor!=null)) {
      Switch powerSwitch = new Switch(on, off, switchSensor);
      powerSwitch.setAccount(device.getAccount());
      device.getSwitchs().add(powerSwitch);
    }
  }

  private void addZwaveSliders(Device device, String name)
  {
    DeviceCommand sliderCommand = null;
    Sensor sliderSensor = null;
    
    for (DeviceCommand deviceCommand : device.getDeviceCommands()) {
      if (deviceCommand.getName().equals(name+"-Dim") || deviceCommand.getName().equals(name+"-Temp")) {
        sliderCommand = deviceCommand;
      }
    }
    for (Sensor sensor : device.getSensors()) {
      if (sensor.getName().equals(name+"-DimSensor") || sensor.getName().equals(name+"-TempSensor")) {
        sliderSensor = sensor;
      }
    }
    
    if ((sliderCommand!=null) && (sliderSensor!=null) ) {
      Slider slider = new Slider(name + "-Slider", sliderCommand, sliderSensor);
      slider.setAccount(device.getAccount());
      device.getSliders().add(slider);
    }
  }



}
