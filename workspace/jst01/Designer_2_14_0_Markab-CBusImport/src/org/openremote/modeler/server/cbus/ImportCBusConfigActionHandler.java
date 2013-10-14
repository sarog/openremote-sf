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
package org.openremote.modeler.server.cbus;

import java.util.ArrayList;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.RangeSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorCommandRef;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.DeviceService;
import org.openremote.modeler.service.SensorService;
import org.openremote.modeler.service.SliderService;
import org.openremote.modeler.service.SwitchService;
import org.openremote.modeler.shared.cbus.GroupAddressImportConfig;
import org.openremote.modeler.shared.cbus.ImportCBusConfigAction;
import org.openremote.modeler.shared.cbus.ImportCBusConfigResult;
import org.openremote.modeler.shared.cbus.ImportConfig;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates the commands, sensors, switches and sliders for a CBus system following the import
 *  
 * @author Jamie Turner
 */
public class ImportCBusConfigActionHandler implements ActionHandler<ImportCBusConfigAction, ImportCBusConfigResult>
{
    protected DeviceService deviceService;
    protected DeviceCommandService deviceCommandService;
    protected SensorService sensorService;
    protected SliderService sliderService;
    protected SwitchService switchService;

    public void setDeviceService(DeviceService deviceService) 
    {
      this.deviceService = deviceService;
    }

    public void setDeviceCommandService(DeviceCommandService deviceCommandService) 
    {
      this.deviceCommandService = deviceCommandService;
    }

    public void setSensorService(SensorService sensorService) 
    {
      this.sensorService = sensorService;
    }
    
    public void setSliderService(SliderService sliderService) 
    {
      this.sliderService = sliderService;
    }
    
    public void setSwitchService(SwitchService switchService) 
    {
      this.switchService = switchService;
    }
    
    @Override
    public ImportCBusConfigResult execute(ImportCBusConfigAction action, ExecutionContext context) throws DispatchException 
    {
      ImportCBusConfigResult result = new ImportCBusConfigResult();
      Device device = deviceService.loadById(action.getDevice().getOid());
      result.setDeviceCommands(createDeviceElements(device, action.getConfig()));
      
      
      // TODO: handle potential errors and return error message
      // TODO: return created elements / updated device ? See also with RequestProxy mechanism
      

      return result;
      
    }
    
    @Override
    public Class<ImportCBusConfigAction> getActionType() 
    {
      return ImportCBusConfigAction.class;
    }

    @Override
    public void rollback(ImportCBusConfigAction action, ImportCBusConfigResult result, ExecutionContext context) throws DispatchException 
    {
      // TODO Implementation only required for compound action
    }
    
    /**
     * Create all the commands, sensors, sliders and switches
     * @param device
     * @param config
     * @return
     */
    @Transactional
    private ArrayList<DeviceCommand> createDeviceElements(Device device, ImportConfig config) 
    {     
      ArrayList<DeviceCommand> deviceCommands = new ArrayList<DeviceCommand>();
      ArrayList<Sensor> sensors = new ArrayList<Sensor>();
      ArrayList<Slider> sliders = new ArrayList<Slider>();
      ArrayList<Switch> switches = new ArrayList<Switch>();
      
      for (GroupAddressImportConfig addressConfig : config.getAddresses())
      {
	  //create on/off commands if switch compatible
	  DeviceCommand onCommand = null;
	  DeviceCommand offCommand = null;

	  onCommand = addDeviceCommand(device, addressConfig.getGroupAddressName() + " ON",
		  addressConfig.getNetworkName(), addressConfig.getApplicationName(), addressConfig.getGroupAddressName(),
		  "ON", null);
	  deviceCommands.add(onCommand);

	  offCommand = addDeviceCommand(device, addressConfig.getGroupAddressName() + " OFF",
		  addressConfig.getNetworkName(), addressConfig.getApplicationName(), addressConfig.getGroupAddressName(),
		  "OFF", null);
	  deviceCommands.add(offCommand);


	  DeviceCommand levelCommand = addDeviceCommand(device, addressConfig.getGroupAddressName() + " STATUS",
		  addressConfig.getNetworkName(), addressConfig.getApplicationName(), addressConfig.getGroupAddressName(),
		  "STATUS", null);
	  deviceCommands.add(levelCommand);

	  if(addressConfig.isSwitchWanted())
	  {
	      Sensor sensor = createDeviceSensor(device, SensorType.SWITCH, levelCommand, addressConfig.getGroupAddressName() + " Switch Sensor");
	      sensors.add(sensor);

	      Switch s = createDeviceSwitch(device, onCommand, offCommand, sensor, addressConfig.getGroupAddressName() + " Switch");
	      switches.add(s);
	  }

	  //create sensors and sliders
	  if (addressConfig.isDimmable()) 
	  {
	      //create dimming command and slider
	      DeviceCommand dimCommand = addDeviceCommand(device, addressConfig.getGroupAddressName() + " DIM",
		      addressConfig.getNetworkName(), addressConfig.getApplicationName(), addressConfig.getGroupAddressName(),
		      "DIM", "${param}");
	      deviceCommands.add(dimCommand);

	      Sensor dimSensor = createDeviceSensor(device, SensorType.RANGE, levelCommand, addressConfig.getGroupAddressName() + " Dim Sensor");
	      sensors.add(dimSensor);

	      sliders.add(createDeviceSlider(device, dimCommand, dimSensor, addressConfig.getGroupAddressName() + " Slider"));        
	  } 
      }
            
      
      deviceCommandService.saveAll(deviceCommands);
      sensorService.saveAllSensors(sensors, device.getAccount());
      switchService.saveAllSwitches(switches, device.getAccount());
      sliderService.saveAllSliders(sliders, device.getAccount());
      
      // TODO: test just re-saving the device
      
      // TODO : Nothing returned for now, DisplatchHandler does not handle Hibernate objects over the wire
      return null;
    }
    
    /**
     * Create a command
     * @param aDevice
     * @param name
     * @param network
     * @param application
     * @param address
     * @param command
     * @param parameters
     * @return
     */
    private DeviceCommand addDeviceCommand(Device aDevice, String name, String network, String application, String address, String command, String parameters)
    {
	DeviceCommand dc = new DeviceCommand();

	Protocol protocol = dc.createProtocol("Clipsal CBus");
	protocol.addProtocolAttribute("command", command);
	protocol.addProtocolAttribute("network", network);
	protocol.addProtocolAttribute("application", application);
	protocol.addProtocolAttribute("group", address);
	if(parameters == null)
	    parameters = "";
	protocol.addProtocolAttribute("command_value", parameters);
	    
	dc.setName(name);
	dc.setDevice(aDevice);
	aDevice.getDeviceCommands().add(dc);
	return dc;
    }
    
    /**
     * Create a sensor
     * @param aDevice
     * @param sensorType
     * @param readCommand
     * @param name
     * @return
     */
    private Sensor createDeviceSensor(Device aDevice, SensorType sensorType, DeviceCommand readCommand, String name) 
    {
	Sensor sensor = null;
	if (SensorType.RANGE == sensorType) 
	{
	    sensor = new RangeSensor();
	    ((RangeSensor) sensor).setMin(0);
	    ((RangeSensor) sensor).setMax(100);
	} 
	else 
	{
	    sensor = new Sensor();
	}
	sensor.setName(name);
	sensor.setType(sensorType);
	
	SensorCommandRef sensorCommandRef = new SensorCommandRef();
	sensorCommandRef.setDeviceCommand(readCommand);
	sensorCommandRef.setSensor(sensor);
	sensor.setSensorCommandRef(sensorCommandRef);
	sensor.setDevice(aDevice);
	return sensor;
    }

    /**
     * Create a switch
     * @param aDevice
     * @param onCommand
     * @param offCommand
     * @param readSensor
     * @param name
     * @return
     */
    private Switch createDeviceSwitch(Device aDevice, DeviceCommand onCommand, DeviceCommand offCommand, Sensor readSensor, String name) 
    {
	Switch s = new Switch(onCommand, offCommand, readSensor);
	s.setName(name);
	s.setDevice(aDevice);
	return s;
    }
    /**
     * Create a slider
     * 
     * @param aDevice
     * @param sliderCommand
     * @param readSensor
     * @param name
     * @return
     */
    private Slider createDeviceSlider(Device aDevice, DeviceCommand sliderCommand, Sensor readSensor, String name) 
    {
	Slider slider = new Slider(name, sliderCommand, readSensor);
	slider.setDevice(aDevice);
	return slider;
    }
}
