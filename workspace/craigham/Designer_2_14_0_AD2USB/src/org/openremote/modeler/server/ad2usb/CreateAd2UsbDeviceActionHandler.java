package org.openremote.modeler.server.ad2usb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

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
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.DeviceService;
import org.openremote.modeler.service.SensorService;
import org.openremote.modeler.service.SliderService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.shared.ad2usb.CreateAd2UsbDeviceAction;
import org.openremote.modeler.shared.ad2usb.CreateAd2UsbDeviceResult;
import org.openremote.modeler.shared.dto.DeviceDTO;
import org.openremote.modeler.shared.russound.CreateRussoundDeviceAction;
import org.openremote.modeler.shared.russound.CreateRussoundDeviceResult;

public class CreateAd2UsbDeviceActionHandler implements
		ActionHandler<CreateAd2UsbDeviceAction, CreateAd2UsbDeviceResult> {

	protected UserService userService;
	protected DeviceService deviceService;
	protected DeviceCommandService deviceCommandService;
	protected SensorService sensorService;
	protected SliderService sliderService;

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setDeviceService(DeviceService deviceService) {
		this.deviceService = deviceService;
	}

	public void setDeviceCommandService(
			DeviceCommandService deviceCommandService) {
		this.deviceCommandService = deviceCommandService;
	}

	public void setSensorService(SensorService sensorService) {
		this.sensorService = sensorService;
	}

	public void setSliderService(SliderService sliderService) {
		this.sliderService = sliderService;
	}

	@Override
	public Class<CreateAd2UsbDeviceAction> getActionType() {
		return CreateAd2UsbDeviceAction.class;
	}

	private ArrayList<Device> createAd2UsbDevices(String deviceName,
			String model) {
		ArrayList<Device> deviceList = new ArrayList<Device>();

		String vendor = "Ademco";

		Account account = userService.getAccount();

		Device device = new Device();
		device.setAccount(account);
		device.setModel(model);
		device.setName(deviceName);
		device.setVendor(vendor);

		addAd2UsbCommands(device);

		deviceList.add(deviceService.saveDevice(device));
		return deviceList;
	}

	private void addAd2UsbCommands(Device device) {

		// add last message retrieval command
		DeviceCommand lastMessageCommand = new DeviceCommand();
		lastMessageCommand.setName("Get Last Message: ");
		lastMessageCommand.setDevice(device);
		HashMap<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("command", "last-message");
		lastMessageCommand.createProtocolWithAttributes("AD2USB Protocol",
				attrMap);
		device.getDeviceCommands().add(lastMessageCommand);

		// add keypress commands
		String[] keys = "1,2,3,4,5,6,7,8,9,0,#,*".split(",");
		for (String keyPress : keys) {
			DeviceCommand deviceCommand = new DeviceCommand();
			deviceCommand.setName("KeyPress: " + keyPress);
			deviceCommand.setDevice(device);
			attrMap = new HashMap<String, String>();
			attrMap.put("parameter1", keyPress);
			attrMap.put("command", "CMD_KEYPRESS");
			deviceCommand.createProtocolWithAttributes("AD2USB Protocol",
					attrMap);
			device.getDeviceCommands().add(deviceCommand);
		}

		Sensor sensor = new CustomSensor();
//		sensor.setType(SensorType.CUSTOM);
		sensor.setName(device.getName() + " - Last Message");

		SensorCommandRef sensorCommandRef = new SensorCommandRef();
		sensorCommandRef.setDeviceCommand(lastMessageCommand);
		sensorCommandRef.setSensor(sensor);
		sensor.setSensorCommandRef(sensorCommandRef);
		sensor.setDevice(device);
		sensor.setAccount(device.getAccount());
		device.getSensors().add(sensor);
	}

	@Override
	public CreateAd2UsbDeviceResult execute(CreateAd2UsbDeviceAction action,
			ExecutionContext context) throws DispatchException {
		CreateAd2UsbDeviceResult result = new CreateAd2UsbDeviceResult();
		ArrayList<DeviceDTO> dtos = new ArrayList<DeviceDTO>();
		for (Device dev : createAd2UsbDevices(action.getDeviceName(),
				action.getModel())) {
			dtos.add(new DeviceDTO(dev.getOid(), dev.getDisplayName()));
		}
		result.setDevices(dtos);
		return result;
	}

	@Override
	public void rollback(CreateAd2UsbDeviceAction arg0,
			CreateAd2UsbDeviceResult arg1, ExecutionContext arg2)
			throws DispatchException {
		// TODO Auto-generated method stub

	}

}