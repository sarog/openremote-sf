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
package org.openremote.modeler.server.omnilink;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceAttr;
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
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.shared.omnilink.CreateOmnilinkDeviceAction;
import org.openremote.modeler.shared.omnilink.CreateOmnilinkDeviceResult;
import org.openremote.modeler.shared.omnilink.OmniLinkCmd;
import org.springframework.transaction.annotation.Transactional;

import com.digitaldan.jomnilinkII.Connection;
import com.digitaldan.jomnilinkII.Message;
import com.digitaldan.jomnilinkII.OmniInvalidResponseException;
import com.digitaldan.jomnilinkII.OmniNotConnectedException;
import com.digitaldan.jomnilinkII.OmniUnknownMessageTypeException;
import com.digitaldan.jomnilinkII.MessageTypes.AudioSourceStatus;
import com.digitaldan.jomnilinkII.MessageTypes.ObjectProperties;
import com.digitaldan.jomnilinkII.MessageTypes.ReqSystemInformation;
import com.digitaldan.jomnilinkII.MessageTypes.SystemInformation;
import com.digitaldan.jomnilinkII.MessageTypes.properties.AreaProperties;
import com.digitaldan.jomnilinkII.MessageTypes.properties.AudioSourceProperties;
import com.digitaldan.jomnilinkII.MessageTypes.properties.AudioZoneProperties;
import com.digitaldan.jomnilinkII.MessageTypes.properties.AuxSensorProperties;
import com.digitaldan.jomnilinkII.MessageTypes.properties.ButtonProperties;
import com.digitaldan.jomnilinkII.MessageTypes.properties.CodeProperties;
import com.digitaldan.jomnilinkII.MessageTypes.properties.ThermostatProperties;
import com.digitaldan.jomnilinkII.MessageTypes.properties.UnitProperties;
import com.digitaldan.jomnilinkII.MessageTypes.properties.ZoneProperties;

public class CreateOmnilinkDevicesActionHandler implements ActionHandler<CreateOmnilinkDeviceAction, CreateOmnilinkDeviceResult> {

	static final String STR_ATTRIBUTE_NAME_OMNI_TYPE = "omnitype";
	static final String STR_ATTRIBUTE_NAME_OMNI_NUM = "omninum";

	private final static String STR_ATTRIBUTE_NAME_COMMAND = "command";
	private final static String STR_ATTRIBUTE_NAME_PARAMETER1 = "parameter1";
	private final static String STR_ATTRIBUTE_NAME_PARAMETER2 = "parameter2";

	protected DeviceService deviceService;
	protected DeviceCommandService deviceCommandService;
	protected SensorService sensorService;
	protected SliderService sliderService;
	protected SwitchService switchService;
	protected UserService userService;

	//	private HashMap<Integer, Device> units;
	//	private HashMap<Integer, Device> thermos;
	//	private HashMap<Integer, Device> zones;
	//	private HashMap<Integer, Device> areas;
	//	private HashMap<Integer, Device> codes;
	//	private HashMap<Integer, Device> auxs;
	//	private HashMap<Integer, Device> buttons;
	//	private HashMap<Integer, Device> audioZones;
	//	private HashMap<Integer, Device> audioSources;

	public void setDeviceService(DeviceService deviceService) {
		this.deviceService = deviceService;
	}

	public void setDeviceCommandService(DeviceCommandService deviceCommandService) {
		this.deviceCommandService = deviceCommandService;
	}

	public void setSensorService(SensorService sensorService) {
		this.sensorService = sensorService;
	}

	public void setSliderService(SliderService sliderService) {
		this.sliderService = sliderService;
	}

	public void setSwitchService(SwitchService switchService) {
		this.switchService = switchService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Override
	public CreateOmnilinkDeviceResult execute(CreateOmnilinkDeviceAction action, ExecutionContext context) throws DispatchException {
		System.out.println("CreateOmnilinkDeviceResult");
		CreateOmnilinkDeviceResult result = new CreateOmnilinkDeviceResult();
		Exception ex = null;
		try {
			importAllDevices(action.getHost(), action.getPort(), action.getKey1() + ":" + action.getKey2()); 
		} catch (UnknownHostException e) {
			e.printStackTrace();
			ex = e;
		} catch (IOException e) {
			e.printStackTrace();
			ex = e;
		} catch (Exception e) {
			e.printStackTrace();
			ex = e;
		}
		if(ex != null)
			result.setErrorMessage(ex.getMessage());
		System.out.println("returning result	"); 
		return result;

	}

	@Override
	public Class<CreateOmnilinkDeviceAction> getActionType() {
		return CreateOmnilinkDeviceAction.class;
	}

	@Override
	public void rollback(CreateOmnilinkDeviceAction action, CreateOmnilinkDeviceResult result, ExecutionContext context) throws DispatchException {
		// TODO Implementation only required for compound action
	}

	//	private void populateCache(){
	//		System.out.println("populateCache");
	//		units = new HashMap<Integer, Device>();
	//
	//				List<Device> devices= deviceService.loadAll();
	//				for(Device device : devices){
	//					List<DeviceAttr> attrs = device.getDeviceAttrs();
	//					int type = -1;
	//					int num = -1;
	//					for(DeviceAttr attr : attrs){
	//						if(attr.getName().equals(STR_ATTRIBUTE_NAME_OMNI_TYPE)){
	//							try { 
	//								type = Integer.parseInt(attr.getValue()); 
	//							} catch (Exception ignored){
	//		
	//							}
	//						} else if(attr.getName().equals(STR_ATTRIBUTE_NAME_OMNI_NUM)){
	//							try { 
	//								num = Integer.parseInt(attr.getValue()); 
	//							} catch (Exception ignored){
	//		
	//							}
	//						}
	//					}
	//					if(type > -1 && num > -1){
	//						Integer i = new Integer(num);
	//						switch(type){
	//						case Message.OBJ_TYPE_UNIT:
	//							units.put(i, device);
	//							break;
	//						case Message.OBJ_TYPE_THERMO:
	//							thermos.put(i, device);
	//							break;
	//						case Message.OBJ_TYPE_ZONE:
	//							zones.put(i, device);
	//							break;
	//						case Message.OBJ_TYPE_AREA:
	//							areas.put(i, device);
	//							break;
	//						case Message.OBJ_TYPE_CODE:
	//							codes.put(i, device);
	//							break;
	//						case Message.OBJ_TYPE_AUX_SENSOR:
	//							auxs.put(i, device);
	//							break;
	//						case Message.OBJ_TYPE_BUTTON:
	//							units.put(i, device);
	//							break;
	//						case Message.OBJ_TYPE_AUDIO_ZONE:
	//							audioZones.put(i, device);
	//							break;
	//						case Message.OBJ_TYPE_AUDIO_SOURCE:
	//							audioSources.put(i, device);
	//							break;
	//						default:
	//							break;
	//						}
	//					}
	//				}
	//	}

	
	@Transactional
	private void importAllDevices(String host, int port, String key) throws UnknownHostException, IOException, Exception{
		
		ArrayList<DeviceCommand> deviceCommands = new ArrayList<DeviceCommand>();
		ArrayList<Sensor> sensors = new ArrayList<Sensor>();
		ArrayList<Slider> sliders = new ArrayList<Slider>();
		ArrayList<Switch> switches = new ArrayList<Switch>();
		ArrayList<Device> devices = new ArrayList<Device>();
		
		Connection c = new Connection(host, port, key);
		
		loadUnits(c,deviceCommands,sensors,sliders,switches,devices);
		loadThermos(c,deviceCommands,sensors,sliders,switches,devices);
		loadAuxs(c,deviceCommands,sensors,sliders,switches,devices);
		loadButtons(c,deviceCommands,sensors,sliders,switches,devices);
		loadAreas(c,deviceCommands,sensors,sliders,switches,devices);
		loadZones(c,deviceCommands,sensors,sliders,switches,devices);
		loadAudioZones(c,deviceCommands,sensors,sliders,switches,devices);
		loadAudioSources(c,deviceCommands,sensors,sliders,switches,devices);

		//no exceptions, go ahead and save all
		for(Device device : devices){
			System.out.println("Saving Device " + device.getName());
			deviceService.saveDevice(device);
		}
		/*
		 * saving the devices above will save all the commands tied to them
		 * so don't do it again
		 */
		//deviceCommandService.saveAll(deviceCommands);
		
		sensorService.saveAllSensors(sensors, userService.getAccount());
		
		sliderService.saveAllSliders(sliders, userService.getAccount());
		
		//hmm, switchService does not have a save all.
		for(Switch sw : switches){
			sw.setAccount(userService.getAccount());
			switchService.save(sw);
		}
	}

	private void loadUnits(Connection c,
			ArrayList<DeviceCommand> deviceCommands, ArrayList<Sensor> sensors,
			ArrayList<Slider> sliders, ArrayList<Switch> switches,
			ArrayList<Device> devices) throws IOException,
			OmniNotConnectedException, OmniInvalidResponseException,
			OmniUnknownMessageTypeException {
		int objnum = 0;
		Message m;
		int currentRoom = 0;
		String currentRoomName = null;
		while ((m = c.reqObjectProperties(Message.OBJ_TYPE_UNIT, objnum, 1, ObjectProperties.FILTER_1_NAMED,
				ObjectProperties.FILTER_2_AREA_ALL, ObjectProperties.FILTER_3_ANY_LOAD)).getMessageType() == Message.MESG_TYPE_OBJ_PROP) {
			UnitProperties o = ((UnitProperties) m);
			objnum = o.getNumber();
			String name = "(Unit) " + o.getName();
			boolean isUpbRoom = false;	
			if(o.getUnitType() == UnitProperties.UNIT_TYPE_HLC_ROOM || 
					o.getObjectType() == UnitProperties.UNIT_TYPE_VIZIARF_ROOM){
				currentRoom = objnum;
				currentRoomName = o.getName();
				if(o.getUnitType() == UnitProperties.UNIT_TYPE_HLC_ROOM)
					isUpbRoom = true;
			} else if(objnum < currentRoom + 8){
					name = "(Unit) "  + currentRoomName + ": " + o.getName();
			} else if(o.getUnitType() == UnitProperties.UNIT_TYPE_FLAG){
					name = "(Flag) " + o.getName();
			}
			
			Device device = new Device(name, "HAI", "Omnilink");
			device.setAccount(userService.getAccount());
			List<DeviceAttr> dattrs = new LinkedList<DeviceAttr>();
			DeviceAttr datt = new DeviceAttr();
			datt.setName(STR_ATTRIBUTE_NAME_OMNI_NUM);
			datt.setValue(objnum + "");
			dattrs.add(datt);

			datt = new DeviceAttr();
			datt.setName(STR_ATTRIBUTE_NAME_OMNI_TYPE);
			datt.setValue(Message.OBJ_TYPE_UNIT + "");
			dattrs.add(datt);
			device.setDeviceAttrs(dattrs);

			devices.add(device);

			DeviceCommand cmdOn = addDeviceCommand(device, name + " cmd on", 0, objnum, OmniLinkCmd.CMD_UNIT_ON);
			deviceCommands.add(cmdOn);

			DeviceCommand cmdOff = addDeviceCommand(device, name + " cmd off", 0, objnum, OmniLinkCmd.CMD_UNIT_OFF);
			deviceCommands.add(cmdOff);

			DeviceCommand cmdLevel = addDeviceCommand(device, name + " cmd level", 0, objnum, OmniLinkCmd.CMD_UNIT_PERCENT);
			deviceCommands.add(cmdLevel);

			DeviceCommand cmdSensorLevel = addDeviceCommand(device, name + " get level", 0, objnum, OmniLinkCmd.SENSOR_UNIT_LEVEL);
			deviceCommands.add(cmdSensorLevel);

			DeviceCommand cmdSensorPower = addDeviceCommand(device, name + " get power", 0, objnum, OmniLinkCmd.SENSOR_UNIT_POWER);
			deviceCommands.add(cmdSensorPower);

			if(isUpbRoom){
				int roomNum = (objnum + 7) / 8;
				//every room has 6 links, the 3rd is where link A starts,  
				//so in room 1 linkA=link3 linkB=link4 linkc=link6 linkd=link7
				int linkA = (roomNum * 6) -3;
				for(int i=0;i< 4; i++){
					int link = linkA +i;
					char linkChar = Character.toChars('A' + i)[0];
					DeviceCommand cmdScene = addDeviceCommand(device, name + " cmd Scene " + linkChar,link, objnum, OmniLinkCmd.CMD_UNIT_UPB_LINK_ON);
					deviceCommands.add(cmdScene);
				}

			}
			
			Sensor senLevel = createDeviceSensor(device, SensorType.LEVEL, cmdSensorLevel,name + " Sensor Level");
			sensors.add(senLevel);

			Sensor senSwitch = createDeviceSensor(device, SensorType.SWITCH, cmdSensorPower,name + " Sensor Switch");
			sensors.add(senSwitch);

			Switch sw = createDeviceSwitch(cmdOn, cmdOff, senSwitch, name + " Switch");
			switches.add(sw);

			Slider slider = createDeviceSlider(device, cmdLevel, senLevel, name + " Slider");
			sliders.add(slider);
		}
	}



	private void loadThermos(Connection c,
			ArrayList<DeviceCommand> deviceCommands, ArrayList<Sensor> sensors,
			ArrayList<Slider> sliders, ArrayList<Switch> switches,
			ArrayList<Device> devices) throws IOException,
			OmniNotConnectedException, OmniInvalidResponseException,
			OmniUnknownMessageTypeException {
		int objnum = 0;
		Message m;
		while ((m = c.reqObjectProperties(Message.OBJ_TYPE_THERMO, objnum, 1, ObjectProperties.FILTER_1_NAMED,
				ObjectProperties.FILTER_2_AREA_ALL, ObjectProperties.FILTER_3_ANY_LOAD)).getMessageType() == Message.MESG_TYPE_OBJ_PROP) {
			ThermostatProperties o = ((ThermostatProperties) m);
			objnum = o.getNumber();
			String name = "(Thermostat) " + o.getName();
			Device device = new Device(name, "HAI", "Omnilink");
			device.setAccount(userService.getAccount());

			List<DeviceAttr> dattrs = new LinkedList<DeviceAttr>();

			DeviceAttr datt = new DeviceAttr();
			datt.setName(STR_ATTRIBUTE_NAME_OMNI_NUM);
			datt.setValue(objnum + "");
			dattrs.add(datt);

			datt = new DeviceAttr();
			datt.setName(STR_ATTRIBUTE_NAME_OMNI_TYPE);
			datt.setValue(Message.OBJ_TYPE_THERMO + "");
			dattrs.add(datt);
			device.setDeviceAttrs(dattrs);

			devices.add(device);


			DeviceCommand cmdRaiseCoolPoint = addDeviceCommand(device, name + " CMD Set Raise Cool Point", 1, objnum, OmniLinkCmd.CMD_THERMO_RAISE_LOWER_COOL);
			deviceCommands.add(cmdRaiseCoolPoint);

			DeviceCommand cmdLowerCoolPoint = addDeviceCommand(device, name + " CMD Set Lower Cool Point", -11, objnum, OmniLinkCmd.CMD_THERMO_RAISE_LOWER_COOL);
			deviceCommands.add(cmdLowerCoolPoint);

			DeviceCommand cmdRaiseHeatPoint = addDeviceCommand(device, name + " CMD Set Raise Heat", 1, objnum, OmniLinkCmd.CMD_THERMO_RAISE_LOWER_HEAT);
			deviceCommands.add(cmdRaiseHeatPoint);

			DeviceCommand cmdLowerHeatPoint = addDeviceCommand(device, name + " CMD Set Lower Heat", -1, objnum, OmniLinkCmd.CMD_THERMO_RAISE_LOWER_HEAT);
			deviceCommands.add(cmdLowerHeatPoint);

			DeviceCommand cmdSetCoolPointC = addDeviceCommand(device, name + " CMD Set Cool Point C", 0, objnum, OmniLinkCmd.CMD_THERMO_SET_COOL_POINTC);
			deviceCommands.add(cmdLowerHeatPoint);

			DeviceCommand cmdSetCoolPointF = addDeviceCommand(device, name + " CMD Set Cool Point F", 0, objnum, OmniLinkCmd.CMD_THERMO_SET_COOL_POINTF);
			deviceCommands.add(cmdSetCoolPointF);

			DeviceCommand cmdSetFanMode = addDeviceCommand(device, name + " CMD Set Fan Mode (auto,on,cycle)", 0, objnum, OmniLinkCmd.CMD_THERMO_SET_FAN_MODE);
			deviceCommands.add(cmdSetFanMode);

			DeviceCommand cmdSetHeatPointC = addDeviceCommand(device, name + " CMD Set Heat Point C", 0, objnum, OmniLinkCmd.CMD_THERMO_SET_HEAT_POINTC);
			deviceCommands.add(cmdSetHeatPointC);

			DeviceCommand cmdSetHeatPointF = addDeviceCommand(device, name + " CMD Set Heat Point F", 0, objnum, OmniLinkCmd.CMD_THERMO_SET_HEAT_POINTF);
			deviceCommands.add(cmdSetHeatPointF);

			DeviceCommand cmdSetHoldMode = addDeviceCommand(device, name + " CMD Set Hold Mode (off,hold,vacation)", 0, objnum, OmniLinkCmd.CMD_THERMO_SET_HOLD_MODE);
			deviceCommands.add(cmdSetHoldMode);

			DeviceCommand cmdSetSystemMode = addDeviceCommand(device, name + " CMD Set System Mode (off,heat,cool,auto,emergency)", 0, objnum, OmniLinkCmd.CMD_THERMO_SET_SYSTEM_MODE);
			deviceCommands.add(cmdSetSystemMode);

			DeviceCommand cmdSensorCoolPointC = addDeviceCommand(device, name + " CMD Get Cool Point C", 0, objnum, OmniLinkCmd.SENSOR_THERMO_COOL_POINTC);
			deviceCommands.add(cmdSensorCoolPointC);

			DeviceCommand cmdSensorCoolPointF = addDeviceCommand(device, name + " CMD Get Cool Point F", 0, objnum, OmniLinkCmd.SENSOR_THERMO_COOL_POINTF);
			deviceCommands.add(cmdSensorCoolPointF);

			DeviceCommand cmdSensorFanMode = addDeviceCommand(device, name + " CMD Get Fan Mode", 0, objnum, OmniLinkCmd.SENSOR_THERMO_FAN_MODE);
			deviceCommands.add(cmdSensorFanMode);

			DeviceCommand cmdSensorHeatPointC = addDeviceCommand(device, name + " CMD Get Heat Point C", 0, objnum, OmniLinkCmd.SENSOR_THERMO_HEAT_POINTC);
			deviceCommands.add(cmdSensorHeatPointC);

			DeviceCommand cmdSensorHeatPointF = addDeviceCommand(device, name + " CMD Get Heat Point F", 0, objnum, OmniLinkCmd.SENSOR_THERMO_HEAT_POINTF);
			deviceCommands.add(cmdSensorHeatPointF);

			DeviceCommand cmdSensorHodeMold= addDeviceCommand(device, name + " CMD Get Hold Mode", 0, objnum, OmniLinkCmd.SENSOR_THERMO_HOLD_MODE);
			deviceCommands.add(cmdSensorHodeMold);

			DeviceCommand cmdSensorSystemMode= addDeviceCommand(device, name + " CMD Get System Mode", 0, objnum, OmniLinkCmd.SENSOR_THERMO_SYSTEM_MODE);
			deviceCommands.add(cmdSensorSystemMode);

			DeviceCommand cmdSensorTempC= addDeviceCommand(device, name + " CMD Get Temp C", 0, objnum, OmniLinkCmd.SENSOR_THERMO_TEMPC);
			deviceCommands.add(cmdSensorTempC);

			DeviceCommand cmdSensorTempF= addDeviceCommand(device, name + " CMD Get Temp F", 0, objnum, OmniLinkCmd.SENSOR_THERMO_TEMPF);
			deviceCommands.add(cmdSensorTempF);

			RangeSensor senRangeCoolPointC = (RangeSensor)createDeviceSensor(device, SensorType.RANGE, cmdSensorCoolPointC, name + " Sensor Range Cool Point C");
			senRangeCoolPointC.setMin(-40);
			senRangeCoolPointC.setMax(87);
			sensors.add(senRangeCoolPointC);

			RangeSensor senRangeCoolPointF = (RangeSensor)createDeviceSensor(device, SensorType.RANGE, cmdSensorCoolPointF, name + " Sensor Range Cool Point F");
			senRangeCoolPointF.setMin(-40);
			senRangeCoolPointF.setMax(189);
			sensors.add(senRangeCoolPointF);

			RangeSensor senRangeFanMode = (RangeSensor)createDeviceSensor(device, SensorType.RANGE, cmdSensorFanMode, name + " Sensor Range Fan Mode");
			senRangeFanMode.setMin(0);
			senRangeFanMode.setMax(2);
			sensors.add(senRangeFanMode);

			RangeSensor senRangeHeatPointC = (RangeSensor)createDeviceSensor(device, SensorType.RANGE, cmdSensorHeatPointC, name + " Sensor Range Heat Point C");
			senRangeHeatPointC.setMin(-40);
			senRangeHeatPointC.setMax(87);
			sensors.add(senRangeHeatPointC);

			RangeSensor senRangeHeatPointF = (RangeSensor)createDeviceSensor(device, SensorType.RANGE, cmdSensorHeatPointF, name + " Sensor Range Heat Point F");
			senRangeHeatPointF.setMin(-40);
			senRangeHeatPointF.setMax(189);
			sensors.add(senRangeHeatPointF);

			RangeSensor senRangeHoldMode = (RangeSensor)createDeviceSensor(device, SensorType.RANGE, cmdSensorHodeMold, name + " Sensor Range Hold Mode");
			senRangeHoldMode.setMin(0);
			senRangeHoldMode.setMax(2);
			sensors.add(senRangeHoldMode);

			RangeSensor senRangeSystemMode = (RangeSensor)createDeviceSensor(device, SensorType.RANGE, cmdSensorSystemMode, name + " Sensor Range System Mode");
			senRangeSystemMode.setMin(0);
			senRangeSystemMode.setMax(4);
			sensors.add(senRangeSystemMode);

			RangeSensor senRangeTempC = (RangeSensor)createDeviceSensor(device, SensorType.RANGE, cmdSensorTempC, name + " Sensor Range Temp C");
			senRangeTempC.setMin(-40);
			senRangeTempC.setMax(87);
			sensors.add(senRangeTempC);

			RangeSensor senRangeTempF = (RangeSensor)createDeviceSensor(device, SensorType.RANGE, cmdSensorTempF, name + " Sensor Range Temp F");
			senRangeTempF.setMin(-40);
			senRangeTempF.setMax(189);
			sensors.add(senRangeTempF);

			Slider sliderSetCoolPointF  = createDeviceSlider(device, cmdSetCoolPointF, senRangeCoolPointF, name + " Slider Set Cool Point F");
			sliders.add(sliderSetCoolPointF);

			Slider sliderSetCoolPointC  = createDeviceSlider(device, cmdSetCoolPointC, senRangeCoolPointC, name + " Slider Set Cool Point C");
			sliders.add(sliderSetCoolPointC);

			Slider sliderSetHeatPointF  = createDeviceSlider(device, cmdSetHeatPointF, senRangeHeatPointF, name + " Slider Set Heat Point F");
			sliders.add(sliderSetHeatPointF);

			Slider sliderSetHeatPointC  = createDeviceSlider(device, cmdSetHeatPointC, senRangeHeatPointC, name + " Slider Set Heat Point C");
			sliders.add(sliderSetHeatPointC);

			Slider sliderSetFanMode  = createDeviceSlider(device, cmdSetFanMode, senRangeFanMode, name + " Slider Set Fan Mode");
			sliders.add(sliderSetFanMode);

			Slider sliderHoldFanMode  = createDeviceSlider(device, cmdSetHoldMode, senRangeHoldMode, name + " Slider Set Hold Mode");
			sliders.add(sliderHoldFanMode);

			Slider sliderSystemMode  = createDeviceSlider(device, cmdSetSystemMode, senRangeSystemMode, name + " Slider Set System Mode");
			sliders.add(sliderSystemMode);

		}

	}

	private void loadZones(Connection c,
			ArrayList<DeviceCommand> deviceCommands, ArrayList<Sensor> sensors,
			ArrayList<Slider> sliders, ArrayList<Switch> switches,
			ArrayList<Device> devices) throws IOException,
			OmniNotConnectedException, OmniInvalidResponseException,
			OmniUnknownMessageTypeException {
		int objnum = 0;
		Message m;
		while ((m = c.reqObjectProperties(Message.OBJ_TYPE_ZONE, objnum, 1, ObjectProperties.FILTER_1_NAMED,
				ObjectProperties.FILTER_2_AREA_ALL, ObjectProperties.FILTER_3_ANY_LOAD)).getMessageType() == Message.MESG_TYPE_OBJ_PROP) {
			ZoneProperties o = ((ZoneProperties) m);
			objnum = o.getNumber();
			String name = "(Zone) " + o.getName();
			Device device = new Device(name, "HAI", "Omnilink");
			device.setAccount(userService.getAccount());

			List<DeviceAttr> dattrs = new LinkedList<DeviceAttr>();

			DeviceAttr datt = new DeviceAttr();
			datt.setName(STR_ATTRIBUTE_NAME_OMNI_NUM);
			datt.setValue(objnum + "");
			dattrs.add(datt);

			datt = new DeviceAttr();
			datt.setName(STR_ATTRIBUTE_NAME_OMNI_TYPE);
			datt.setValue(Message.OBJ_TYPE_ZONE + "");
			dattrs.add(datt);
			device.setDeviceAttrs(dattrs);

			devices.add(device);

			DeviceCommand cmdBypass = addDeviceCommand(device, name + " CMD Bypase Zone", 0, objnum, OmniLinkCmd.CMD_SECURITY_BYPASS_ZONE);
			deviceCommands.add(cmdBypass);

			DeviceCommand cmdRestore = addDeviceCommand(device, name + " CMD Restore Zone", 0, objnum, OmniLinkCmd.CMD_SECURITY_RESTORE_ZONE);
			deviceCommands.add(cmdRestore);

			DeviceCommand cmdSenZoneCur = addDeviceCommand(device, name + " CMD Get Zone Status Current", 0, objnum, OmniLinkCmd.SENSOR_ZONE_STATUS_CURRENT);
			deviceCommands.add(cmdSenZoneCur);
			
			DeviceCommand cmdSenZoneLatch = addDeviceCommand(device, name + " CMD Get Zone Status Latched", 0, objnum, OmniLinkCmd.SENSOR_ZONE_STATUS_LATCHED);
			deviceCommands.add(cmdSenZoneLatch);
			
			DeviceCommand cmdSenZoneArm = addDeviceCommand(device, name + " CMD Get Zone Status Arming", 0, objnum, OmniLinkCmd.SENSOR_ZONE_STATUS_ARMING);
			deviceCommands.add(cmdSenZoneArm);
			
			Sensor senZoneCur = createDeviceSensor(device, SensorType.CUSTOM, cmdSenZoneCur, name + " Sensor Current");
			sensors.add(senZoneCur);
			
			Sensor senZoneLatch = createDeviceSensor(device, SensorType.CUSTOM, cmdSenZoneLatch, name + " Sensor Latched");
			sensors.add(senZoneLatch);
			
			Sensor senZoneArm = createDeviceSensor(device, SensorType.CUSTOM, cmdSenZoneArm, name + " Sensor Arming");
			sensors.add(senZoneArm);

		}

	}
	
	  private void loadAreas(Connection c,
				ArrayList<DeviceCommand> deviceCommands, ArrayList<Sensor> sensors,
				ArrayList<Slider> sliders, ArrayList<Switch> switches,
				ArrayList<Device> devices) throws IOException, OmniNotConnectedException, OmniInvalidResponseException,
	  OmniUnknownMessageTypeException {
		  int objnum = 0;
		  Message m;
		  while ((m = c.reqObjectProperties(Message.OBJ_TYPE_AREA, objnum, 1, ObjectProperties.FILTER_1_NAMED_UNAMED,
				  ObjectProperties.FILTER_2_NONE, ObjectProperties.FILTER_3_NONE)).getMessageType() == Message.MESG_TYPE_OBJ_PROP) {
			  AreaProperties o = ((AreaProperties) m);
			  objnum = o.getNumber();
			  String name = "(Area) " + (o.getName().length() > 0 ? o.getName() : o.getNumber());
				Device device = new Device(name, "HAI", "Omnilink");
				device.setAccount(userService.getAccount());

				List<DeviceAttr> dattrs = new LinkedList<DeviceAttr>();

				DeviceAttr datt = new DeviceAttr();
				datt.setName(STR_ATTRIBUTE_NAME_OMNI_NUM);
				datt.setValue(objnum + "");
				dattrs.add(datt);

				datt = new DeviceAttr();
				datt.setName(STR_ATTRIBUTE_NAME_OMNI_TYPE);
				datt.setValue(Message.OBJ_TYPE_AREA + "");
				dattrs.add(datt);
				device.setDeviceAttrs(dattrs);
				devices.add(device);
				

//		         30            HAI Omni IIe
//		         16            HAI OmniPro II
//		         36            HAI Lumina
//		         37            HAI Lumina Pro
				SystemInformation si= c.reqSystemInformation();
				boolean omni = si.getModel() < 36;
				
				DeviceCommand cmdDisarmOmni = addDeviceCommand(device, name + " CMD Set Disarm", 0,objnum, OmniLinkCmd.CMD_SECURITY_OMNI_DISARM);
				deviceCommands.add(cmdDisarmOmni);
				
				if(omni){
					DeviceCommand cmdDayOmni = addDeviceCommand(device, name + " CMD Set Day Mode Omni", 0,objnum, OmniLinkCmd.CMD_SECURITY_OMNI_DAY_MODE);
					deviceCommands.add(cmdDayOmni);
					
					DeviceCommand cmdNightOmni = addDeviceCommand(device, name + " CMD Set Night Mode Omni", 0,objnum, OmniLinkCmd.CMD_SECURITY_OMNI_NIGHT_MODE);
					deviceCommands.add(cmdNightOmni);
					
					DeviceCommand cmdAwayOmni = addDeviceCommand(device, name + " CMD Set Day Away Omni", 0,objnum, OmniLinkCmd.CMD_SECURITY_OMNI_AWAY_MODE);
					deviceCommands.add(cmdAwayOmni);
					
					DeviceCommand cmdVacaOmni = addDeviceCommand(device, name + " CMD Set Day Vaccation Omni", 0,objnum, OmniLinkCmd.CMD_SECURITY_OMNI_VACATION_MODE);
					deviceCommands.add(cmdVacaOmni);
					
					DeviceCommand cmdDayInstOmni = addDeviceCommand(device, name + " CMD Set Day Instant Mode Omni", 0,objnum, OmniLinkCmd.CMD_SECURITY_OMNI_DAY_INSTANCE_MODE);
					deviceCommands.add(cmdDayInstOmni);
					
					DeviceCommand cmdNightDelayOmni = addDeviceCommand(device, name + " CMD Set Night Delay Mode Omni", 0,objnum, OmniLinkCmd.CMD_SECURITY_OMNI_NIGHT_DELAYED_MODE);
					deviceCommands.add(cmdNightDelayOmni);
				} else {
					DeviceCommand cmdAeayLumina = addDeviceCommand(device, name + " CMD Set Away Mode Lumina", 0,objnum, OmniLinkCmd.CMD_SECURITY_LUMINA_AWAY_MODE);
					deviceCommands.add(cmdAeayLumina);
					
					DeviceCommand cmdHomeLumina = addDeviceCommand(device, name + " CMD Set Home Mode Lumina", 0,objnum, OmniLinkCmd.CMD_SECURITY_LUMINA_HOME_MODE);
					deviceCommands.add(cmdHomeLumina);
					
					DeviceCommand cmdPartyLumina = addDeviceCommand(device, name + " CMD Set Party Mode Lumina", 0,objnum, OmniLinkCmd.CMD_SECURITY_LUMINA_PARTY_MODE);
					deviceCommands.add(cmdPartyLumina);
					
					DeviceCommand cmdSleepLumina = addDeviceCommand(device, name + " CMD Set Sleep Mode Lumina", 0,objnum, OmniLinkCmd.CMD_SECURITY_LUMINA_SLEEP_MODE);
					deviceCommands.add(cmdSleepLumina);
					
					DeviceCommand cmdSpecialLumina = addDeviceCommand(device, name + " CMD Set Special Mode Lumina", 0,objnum, OmniLinkCmd.CMD_SECURITY_LUMINA_SPECIAL_MODE);
					deviceCommands.add(cmdSpecialLumina);
					
					DeviceCommand cmdVacaLumina = addDeviceCommand(device, name + " CMD Set Vaccation Lumina", 0,objnum, OmniLinkCmd.CMD_SECURITY_LUMINA_VACATION_MODE);
					deviceCommands.add(cmdVacaLumina);
				}
				
				DeviceCommand sensMode = addDeviceCommand(device, name + " CMD Get Mode", 0,objnum, OmniLinkCmd.SENSOR_AREA_STATUS_MODE);
				deviceCommands.add(sensMode);
				
				DeviceCommand sensAlarm = addDeviceCommand(device, name + " CMD Get Alarm", 0,objnum, OmniLinkCmd.SENSOR_AREA_STATUS_ALARM);
				deviceCommands.add(sensMode);
				
				Sensor senAreaMode = createDeviceSensor(device, SensorType.CUSTOM, sensMode, name + " Mode");
				sensors.add(senAreaMode);
				
				Sensor senAreaAlarm = createDeviceSensor(device, SensorType.CUSTOM, sensAlarm, name + " Alarm");
				sensors.add(senAreaAlarm);
		  }
	
	  }
	
	private void loadAuxs(Connection c,
			ArrayList<DeviceCommand> deviceCommands, ArrayList<Sensor> sensors,
			ArrayList<Slider> sliders, ArrayList<Switch> switches,
			ArrayList<Device> devices) throws IOException,
			OmniNotConnectedException, OmniInvalidResponseException,
			OmniUnknownMessageTypeException {
		int objnum = 0;
		Message m;
		while ((m = c.reqObjectProperties(Message.OBJ_TYPE_AUX_SENSOR, objnum, 1, ObjectProperties.FILTER_1_NAMED,
				ObjectProperties.FILTER_2_AREA_ALL, ObjectProperties.FILTER_3_NONE)).getMessageType() == Message.MESG_TYPE_OBJ_PROP) {
			AuxSensorProperties o = ((AuxSensorProperties) m);
			objnum = o.getNumber();
			String name = "(Aux) " + o.getName();
			Device device = new Device(name, "HAI", "Omnilink");
			device.setAccount(userService.getAccount());

			List<DeviceAttr> dattrs = new LinkedList<DeviceAttr>();

			DeviceAttr datt = new DeviceAttr();
			datt.setName(STR_ATTRIBUTE_NAME_OMNI_NUM);
			datt.setValue(objnum + "");
			dattrs.add(datt);

			datt = new DeviceAttr();
			datt.setName(STR_ATTRIBUTE_NAME_OMNI_TYPE);
			datt.setValue(Message.OBJ_TYPE_AUX_SENSOR + "");
			dattrs.add(datt);
			device.setDeviceAttrs(dattrs);
			devices.add(device);

			DeviceCommand cmdGetCurrentC = addDeviceCommand(device, name + " CMD Get Current C", 0,objnum, OmniLinkCmd.SENSOR_AUX_CURRENTC);
			deviceCommands.add(cmdGetCurrentC);

			DeviceCommand cmdGetCurrentF = addDeviceCommand(device, name + " CMD Get Current F", 0,objnum, OmniLinkCmd.SENSOR_AUX_CURRENTF);
			deviceCommands.add(cmdGetCurrentF);

			DeviceCommand cmdGetHighC = addDeviceCommand(device, name + " CMD Get High C", 0,objnum, OmniLinkCmd.SENSOR_AUX_HIGHC);
			deviceCommands.add(cmdGetHighC);

			DeviceCommand cmdGetHighF = addDeviceCommand(device, name + " CMD Get High F", 0,objnum, OmniLinkCmd.SENSOR_AUX_HIGHF);
			deviceCommands.add(cmdGetHighF);

			DeviceCommand cmdGetLowC = addDeviceCommand(device, name + " CMD Get Low C", 0,objnum, OmniLinkCmd.SENSOR_AUX_LOWC);
			deviceCommands.add(cmdGetLowC);

			DeviceCommand cmdGetLowF = addDeviceCommand(device, name + " CMD Get Low F", 0,objnum, OmniLinkCmd.SENSOR_AUX_LOWF);
			deviceCommands.add(cmdGetLowF);

			DeviceCommand cmdStat = addDeviceCommand(device, name + " CMD Get Stat", 0,objnum, OmniLinkCmd.SENSOR_AUX_STATUS);
			deviceCommands.add(cmdStat);

			RangeSensor senRangeCurrentC = (RangeSensor)createDeviceSensor(device, SensorType.RANGE, cmdGetCurrentC, name + " Sensor Range Current C");
			senRangeCurrentC.setMin(-40);
			senRangeCurrentC.setMax(87);
			sensors.add(senRangeCurrentC);

			RangeSensor senRangeCurrentF = (RangeSensor)createDeviceSensor(device, SensorType.RANGE, cmdGetCurrentF, name + " Sensor Range Current F");
			senRangeCurrentF.setMin(-40);
			senRangeCurrentF.setMax(189);
			sensors.add(senRangeCurrentF);

			RangeSensor senRangeHighC = (RangeSensor)createDeviceSensor(device, SensorType.RANGE, cmdGetHighC, name + " Sensor Range High C");
			senRangeHighC.setMin(-40);
			senRangeHighC.setMax(87);
			sensors.add(senRangeHighC);

			RangeSensor senRangeHighF = (RangeSensor)createDeviceSensor(device, SensorType.RANGE, cmdGetHighF, name + " Sensor Range High F");
			senRangeHighF.setMin(-40);
			senRangeHighF.setMax(189);
			sensors.add(senRangeHighF);

			RangeSensor senRangeLowC = (RangeSensor)createDeviceSensor(device, SensorType.RANGE, cmdGetLowC, name + " Sensor Range Low C");
			senRangeLowC.setMin(-40);
			senRangeLowC.setMax(87);
			sensors.add(senRangeLowC);

			RangeSensor senRangeLowF = (RangeSensor)createDeviceSensor(device, SensorType.RANGE, cmdGetLowF, name + " Sensor Range Low F");
			senRangeLowF.setMin(-40);
			senRangeLowF.setMax(189);
			sensors.add(senRangeLowF);

		}

	}

	private void loadButtons(Connection c,
			ArrayList<DeviceCommand> deviceCommands, ArrayList<Sensor> sensors,
			ArrayList<Slider> sliders, ArrayList<Switch> switches,
			ArrayList<Device> devices) throws IOException,
			OmniNotConnectedException, OmniInvalidResponseException,
			OmniUnknownMessageTypeException {
		int objnum = 0;
		Message m;
		while ((m = c.reqObjectProperties(Message.OBJ_TYPE_BUTTON, objnum, 1, ObjectProperties.FILTER_1_NAMED,
				ObjectProperties.FILTER_2_AREA_ALL, ObjectProperties.FILTER_3_NONE)).getMessageType() == Message.MESG_TYPE_OBJ_PROP) {
			ButtonProperties o = ((ButtonProperties) m);
			objnum = o.getNumber();
			String name = "(Button) " + o.getName();
			Device device = new Device(name, "HAI", "Omnilink");
			device.setAccount(userService.getAccount());

			List<DeviceAttr> dattrs = new LinkedList<DeviceAttr>();

			DeviceAttr datt = new DeviceAttr();
			datt.setName(STR_ATTRIBUTE_NAME_OMNI_NUM);
			datt.setValue(objnum + "");
			dattrs.add(datt);

			datt = new DeviceAttr();
			datt.setName(STR_ATTRIBUTE_NAME_OMNI_TYPE);
			datt.setValue(Message.OBJ_TYPE_BUTTON + "");
			dattrs.add(datt);
			device.setDeviceAttrs(dattrs);

			devices.add(device);

			DeviceCommand cmdButton = addDeviceCommand(device, name + " CMD Button Push", 0,objnum, OmniLinkCmd.CMD_BUTTON);
			deviceCommands.add(cmdButton);
		}

	}
	//
	//  private void loadCodes() throws IOException, OmniNotConnectedException, OmniInvalidResponseException,
	//  OmniUnknownMessageTypeException {
	//	  int objnum = 0;
	//	  Message m;
	//	  while ((m = c.reqObjectProperties(Message.OBJ_TYPE_CODE, objnum, 1, ObjectProperties.FILTER_1_NAMED,
	//			  ObjectProperties.FILTER_2_NONE, ObjectProperties.FILTER_3_NONE)).getMessageType() == Message.MESG_TYPE_OBJ_PROP) {
	//		  log.info(m.toString());
	//		  CodeProperties o = ((CodeProperties) m);
	//		  objnum = o.getNumber();
	//		  codes.put(new Integer(o.getNumber()), o);
	//	  }
	//	  log.info(m.toString());
	//
	//  }
	//
	private void loadAudioZones(Connection c,
			ArrayList<DeviceCommand> deviceCommands, ArrayList<Sensor> sensors,
			ArrayList<Slider> sliders, ArrayList<Switch> switches,
			ArrayList<Device> devices) throws IOException,
			OmniNotConnectedException, OmniInvalidResponseException,
			OmniUnknownMessageTypeException {
		int objnum = 0;
		Message m;
		while ((m = c.reqObjectProperties(Message.OBJ_TYPE_AUDIO_ZONE, objnum, 1, ObjectProperties.FILTER_1_NAMED,
				ObjectProperties.FILTER_2_NONE, ObjectProperties.FILTER_3_NONE)).getMessageType() == Message.MESG_TYPE_OBJ_PROP) {
			AudioZoneProperties o = ((AudioZoneProperties) m);
			objnum = o.getNumber();
			String name = "(Audio Zone) " + o.getName();
			Device device = new Device(name, "HAI", "Omnilink");
			device.setAccount(userService.getAccount());
			List<DeviceAttr> dattrs = new LinkedList<DeviceAttr>();
			DeviceAttr datt = new DeviceAttr();
			datt.setName(STR_ATTRIBUTE_NAME_OMNI_NUM);
			datt.setValue(objnum + "");
			dattrs.add(datt);
			datt = new DeviceAttr();
			datt.setName(STR_ATTRIBUTE_NAME_OMNI_TYPE);
			datt.setValue(Message.OBJ_TYPE_AUDIO_ZONE + "");
			dattrs.add(datt);
			device.setDeviceAttrs(dattrs);

			devices.add(device);

			for(int i = 1; i<=46; i++){
				DeviceCommand cmdSelectKey = addDeviceCommand(device, name + " CMD Set Audio Zone Select Key " + i, i,objnum, OmniLinkCmd.CMD_AUDIO_ZONE_SELECT_KEY);
				deviceCommands.add(cmdSelectKey);
			}
			for(int i = 1; i<=8;i++){
				DeviceCommand cmdSetSrc = addDeviceCommand(device, name + " CMD Set Audio Zone Source " + i, i,objnum, OmniLinkCmd.CMD_AUDIO_ZONE_SET_SOURCE);
				deviceCommands.add(cmdSetSrc);
			}

			DeviceCommand cmdSetOn = addDeviceCommand(device, name + " CMD Set Audio Zone On", 1,objnum, OmniLinkCmd.CMD_AUDIO_ZONE_SET_ON);
			deviceCommands.add(cmdSetOn);

			DeviceCommand cmdSetOff = addDeviceCommand(device, name + " CMD Set Audio Zone Off", 0,objnum, OmniLinkCmd.CMD_AUDIO_ZONE_SET_ON);
			deviceCommands.add(cmdSetOff);

			DeviceCommand cmdSetMute = addDeviceCommand(device, name + " CMD Set Audio Zone Mute", 1,objnum, OmniLinkCmd.CMD_AUDIO_ZONE_SET_MUTE);
			deviceCommands.add(cmdSetMute);

			DeviceCommand cmdSetUnMute = addDeviceCommand(device, name + " CMD Set Audio Zone UnMute", 0,objnum, OmniLinkCmd.CMD_AUDIO_ZONE_SET_MUTE);
			deviceCommands.add(cmdSetMute);

			DeviceCommand cmdSetVolume = addDeviceCommand(device, name + " CMD Set Audio Zone Volume", 0,objnum, OmniLinkCmd.CMD_AUDIO_ZONE_SET_VOLUME);
			deviceCommands.add(cmdSetVolume);

			DeviceCommand cmdGetMute = addDeviceCommand(device, name + " CMD Get Audio Zone Mute", 0,objnum, OmniLinkCmd.SENSOR_AUDIOZONE_MUTE);
			deviceCommands.add(cmdGetMute);

			DeviceCommand cmdGetPower = addDeviceCommand(device, name + " CMD Get Audio Zone Power", 0,objnum, OmniLinkCmd.SENSOR_AUDIOZONE_POWER);
			deviceCommands.add(cmdGetPower);

			DeviceCommand cmdGetSec = addDeviceCommand(device, name + " CMD Get Audio Zone Source", 0,objnum, OmniLinkCmd.SENSOR_AUDIOZONE_SOURCE);
			deviceCommands.add(cmdGetSec);

			DeviceCommand cmdGetText = addDeviceCommand(device, name + " CMD Get Audio Zone Text", 0,objnum, OmniLinkCmd.SENSOR_AUDIOZONE_TEXT);
			deviceCommands.add(cmdGetText);

			DeviceCommand cmdGetVolume = addDeviceCommand(device, name + " CMD Get Audio Zone Volume", 0,objnum, OmniLinkCmd.SENSOR_AUDIOZONE_VOLUME);
			deviceCommands.add(cmdGetVolume);

			Sensor senVolume = createDeviceSensor(device, SensorType.LEVEL, cmdGetVolume, name + " Sensor Volume Level");
			sensors.add(senVolume);

			Sensor senText = createDeviceSensor(device, SensorType.CUSTOM, cmdGetText, name + " Sensor Text");
			sensors.add(senText);

			Sensor senPower = createDeviceSensor(device, SensorType.SWITCH, cmdGetPower, name + " Sensor Power");
			sensors.add(senPower);

			Sensor senMute = createDeviceSensor(device, SensorType.SWITCH, cmdGetMute, name + " Sensor Mute");
			sensors.add(senMute);

			Slider sliderSetVolume  = createDeviceSlider(device, cmdSetVolume, senVolume, name + " Slider Set Volume");
			sliders.add(sliderSetVolume);

			Switch swPower = createDeviceSwitch(cmdSetOn, cmdSetOff, senPower, name + " Power Switch");
			switches.add(swPower);

			Switch swMute = createDeviceSwitch(cmdSetMute, cmdSetUnMute, senMute, name + " Mute Switch");
			switches.add(swMute);

		}

	}

	private void loadAudioSources(Connection c,
			ArrayList<DeviceCommand> deviceCommands, ArrayList<Sensor> sensors,
			ArrayList<Slider> sliders, ArrayList<Switch> switches,
			ArrayList<Device> devices) throws IOException,
			OmniNotConnectedException, OmniInvalidResponseException,
			OmniUnknownMessageTypeException {
		int objnum = 0;
		Message m;
		while ((m = c.reqObjectProperties(Message.OBJ_TYPE_AUDIO_SOURCE, objnum, 1, ObjectProperties.FILTER_1_NAMED,
				ObjectProperties.FILTER_2_NONE, ObjectProperties.FILTER_3_NONE)).getMessageType() == Message.MESG_TYPE_OBJ_PROP) {
			AudioSourceProperties o = ((AudioSourceProperties) m);
			objnum = ((ObjectProperties) m).getNumber();

			String name = "(Audio Source) " + o.getName();
			Device device = new Device(name, "HAI", "Omnilink");
			device.setAccount(userService.getAccount());

			List<DeviceAttr> dattrs = new LinkedList<DeviceAttr>();

			DeviceAttr datt = new DeviceAttr();
			datt.setName(STR_ATTRIBUTE_NAME_OMNI_NUM);
			datt.setValue(objnum + "");
			dattrs.add(datt);

			datt = new DeviceAttr();
			datt.setName(STR_ATTRIBUTE_NAME_OMNI_TYPE);
			datt.setValue(Message.OBJ_TYPE_AUDIO_SOURCE + "");
			dattrs.add(datt);
			device.setDeviceAttrs(dattrs);

			devices.add(device);

			DeviceCommand cmdGetText = addDeviceCommand(device, name + " CMD Get Audio Source Text", 0,objnum, OmniLinkCmd.SENSOR_AUDIOSOURCE_TEXT);
			deviceCommands.add(cmdGetText);

			Sensor senText = createDeviceSensor(device, SensorType.CUSTOM, cmdGetText, name + " Sensor Text");
			sensors.add(senText);


		}

	}


	private DeviceCommand addDeviceCommand(Device aDevice, String name, int parameter1, int parameter2, OmniLinkCmd command) {
		DeviceCommand dc = new DeviceCommand();

		Protocol protocol = dc.createProtocol("Omnilink");
		protocol.addProtocolAttribute(STR_ATTRIBUTE_NAME_COMMAND, command + "");
		if(parameter1 > 0)
			protocol.addProtocolAttribute(STR_ATTRIBUTE_NAME_PARAMETER1, parameter1 + "");
		protocol.addProtocolAttribute(STR_ATTRIBUTE_NAME_PARAMETER2, parameter2 + "");
		//    protocol.addProtocolAttribute(STR_ATTRIBUTE_NAME_OMNI_TYPE, omnilinkType + ""); 
		//    protocol.addProtocolAttribute(STR_ATTRIBUTE_NAME_OMNI_NUM, omnilinkNum + ""); 

		dc.setName(name);
		dc.setDevice(aDevice);
		aDevice.getDeviceCommands().add(dc);
		return dc;
	}

	private Sensor createDeviceSensor(Device aDevice, SensorType sensorType, DeviceCommand readCommand, String name) {
		Sensor sensor = null;
		if (SensorType.RANGE == sensorType) {
			sensor = new RangeSensor();
		} else if (SensorType.CUSTOM == sensorType) { 
			sensor = new CustomSensor();
		}else {
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

	private Slider createDeviceSlider(Device aDevice, DeviceCommand sliderCommand, Sensor readSensor, String name) {
		Slider slider = new Slider(name, sliderCommand, readSensor);
		slider.setDevice(aDevice);
		return slider;
	}

	private Switch createDeviceSwitch(DeviceCommand onCmd, DeviceCommand offCmd, Sensor sensor, String name ){
		Switch sw = new Switch(onCmd, offCmd, sensor);
		sw.setName(name);
		return sw;
	}

}