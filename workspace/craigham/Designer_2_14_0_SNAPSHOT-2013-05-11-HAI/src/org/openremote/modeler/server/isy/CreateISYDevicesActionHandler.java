package org.openremote.modeler.server.isy;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.RangeSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorCommandRef;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.service.DeviceService;
import org.openremote.modeler.service.SensorService;
import org.openremote.modeler.service.SliderService;
import org.openremote.modeler.service.SwitchService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.shared.dto.DeviceDTO;
import org.openremote.modeler.shared.isy.CreateISYDeviceAction;
import org.openremote.modeler.shared.isy.CreateISYDeviceResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CreateISYDevicesActionHandler implements
		ActionHandler<CreateISYDeviceAction, CreateISYDeviceResult> {
	public static final String VENDOR = "n/a";
	public static final String MODEL = "ISY-99";

	public final static String ISY99_XMLPROPERTY_ADDRESS = "address";
	public final static String ISY99_XMLPROPERTY_COMMAND = "command";
	public final static String ISY99_XMLPROPERTY_NAME = "name";

	// private final static String STR_ATTRIBUTE_NAME_COMMAND = "command";
	// private final static String STR_ATTRIBUTE_NAME_PARAMETER1 = "parameter1";
	// private final static String STR_ATTRIBUTE_NAME_PARAMETER2 = "parameter2";

	protected DeviceService deviceService;
	protected SensorService sensorService;
	protected SliderService sliderService;
	protected SwitchService switchService;
	protected UserService userService;

	@Override
	public CreateISYDeviceResult execute(CreateISYDeviceAction action,
			ExecutionContext result) throws DispatchException {

		CreateISYDeviceResult returnValue = new CreateISYDeviceResult();
		List<DeviceCommand> deviceCommands = new ArrayList<DeviceCommand>();
		List<Sensor> sensors = new ArrayList<Sensor>();
		List<Slider> sliders = new ArrayList<Slider>();
		List<Switch> switches = new ArrayList<Switch>();
		List<Device> devices = new ArrayList<Device>();

		List<ISYSeedinfo> deviceSeedList = getSeedInfo(action.getUrl(),
				action.getUserName(), action.getPassward());

		for (ISYSeedinfo seedinfo : deviceSeedList) {
			if ("1.45.65.0".equals(seedinfo.getType())
					|| "2.42.66.0".equals(seedinfo.getType())
					|| "1.32.65.0".equals(seedinfo.getType())) {
				String address = seedinfo.getAddress();
				String name = seedinfo.getName();

				Device device = new Device(name, VENDOR, MODEL);
				
				device.setAccount(userService.getAccount());

				devices.add(device);
				DeviceCommand cmdOn = addDeviceCommand(device, address, name
						+ " cmd-on", "DON");
				deviceCommands.add(cmdOn);

				DeviceCommand cmdOff = addDeviceCommand(device, address, name
						+ " cmd-off", "DOF");
				deviceCommands.add(cmdOff);

				DeviceCommand cmdFastOn = addDeviceCommand(device, address, name
						+ " cmd-fast on", "DFON");
				deviceCommands.add(cmdFastOn);

				DeviceCommand cmdFastOff = addDeviceCommand(device, address, name
						+ " cmd-off", "DFOF");
				deviceCommands.add(cmdFastOff);

				DeviceCommand cmdPower = addDeviceCommand(device, address, name
						+ " get power", "get-power");
				deviceCommands.add(cmdPower);

				Sensor powerSensor = createDeviceSensor(device,
						SensorType.SWITCH, cmdPower, name + " power sensor");
				sensors.add(powerSensor);

				Switch sw = createDeviceSwitch(cmdOn, cmdOff, powerSensor, name
						+ " Switch");
				switches.add(sw);

				boolean lightHasDimmer = !"2.42.66.0"
						.equals(seedinfo.getType());
				if (lightHasDimmer) {
					DeviceCommand cmdLevel = addDeviceCommand(device, address,
							name + " cmd level", "get-level");
					deviceCommands.add(cmdLevel);

					DeviceCommand cmdSetLevel = addDeviceCommand(device,
							address, name + " set level", "DON");
					deviceCommands.add(cmdSetLevel);

					Sensor levelSensor = createDeviceSensor(device,
							SensorType.RANGE, cmdLevel, name + " level sensor");
					sensors.add(levelSensor);
					Slider slider = createDeviceSlider(device, cmdSetLevel,
							levelSensor, name + " Slider");
					sliders.add(slider);
				}
			}
			// motion
			if ("16.1.65.0".equals(seedinfo.getType())) {
				String name = seedinfo.getName();
				Device device = new Device(name, VENDOR, MODEL);
				device.setAccount(userService.getAccount());
				devices.add(device);
				DeviceCommand cmdPower = addDeviceCommand(device,
						seedinfo.getAddress(), name + " get power", "get-power");
				deviceCommands.add(cmdPower);

				Sensor powerSensor = createDeviceSensor(device,
						SensorType.SWITCH, cmdPower, name + " power sensor");
				sensors.add(powerSensor);
			}
			// leak
			if ("16.8.65.0".equals(seedinfo.getType())) {
				String name = seedinfo.getName();
				Device device = new Device(name, VENDOR, MODEL);
				device.setAccount(userService.getAccount());
				devices.add(device);
				DeviceCommand cmdPower = addDeviceCommand(device,
						seedinfo.getAddress(), name + " get power", "get-power");
				deviceCommands.add(cmdPower);

				Sensor powerSensor = createDeviceSensor(device,
						SensorType.SWITCH, cmdPower, name + " power sensor");
				sensors.add(powerSensor);
			}
			// relay
			if ("2.9.66.0".equals(seedinfo.getType())) {
				String name = seedinfo.getName();
				Device device = new Device(name, VENDOR, MODEL);
				device.setAccount(userService.getAccount());
				devices.add(device);
				DeviceCommand cmdPower = addDeviceCommand(device,
						seedinfo.getAddress(), name + " get power", "get-power");
				deviceCommands.add(cmdPower);

				Sensor powerSensor = createDeviceSensor(device,
						SensorType.SWITCH, cmdPower, name + " power sensor");
				sensors.add(powerSensor);
			}
			// outdoor capable switch
			if ("2.56.66.0".equals(seedinfo.getType())) {
				String name = seedinfo.getName();
				Device device = new Device(name, VENDOR, MODEL);
				device.setAccount(userService.getAccount());
				devices.add(device);
				DeviceCommand cmdPower = addDeviceCommand(device,
						seedinfo.getAddress(), name + " get power", "get-power");
				deviceCommands.add(cmdPower);

				Sensor powerSensor = createDeviceSensor(device,
						SensorType.SWITCH, cmdPower, name + " power sensor");
				sensors.add(powerSensor);
			}
		}

		ArrayList<DeviceDTO> dtos = new ArrayList<DeviceDTO>();
		for (Device device : devices) {
			System.out.println("Saving Device " + device.getName());
			deviceService.saveDevice(device);
			dtos.add(new DeviceDTO(device.getOid(), device.getDisplayName()));
		}
		returnValue.setDevices(dtos);

		/*
		 * saving the devices above will save all the commands tied to them so
		 * don't do it again
		 */
		// deviceCommandService.saveAll(deviceCommands);

		sensorService.saveAllSensors(sensors, userService.getAccount());

		sliderService.saveAllSliders(sliders, userService.getAccount());

		// hmm, switchService does not have a save all.
		for (Switch sw : switches) {
			sw.setAccount(userService.getAccount());
			switchService.save(sw);
		}

		return returnValue;
	}

	private Switch createDeviceSwitch(DeviceCommand onCmd,
			DeviceCommand offCmd, Sensor sensor, String name) {
		Switch sw = new Switch(onCmd, offCmd, sensor);
		sw.setName(name);
		return sw;
	}

	private Slider createDeviceSlider(Device aDevice,
			DeviceCommand sliderCommand, Sensor readSensor, String name) {
		Slider slider = new Slider(name, sliderCommand, readSensor);
		slider.setDevice(aDevice);
		return slider;
	}

	private Sensor createDeviceSensor(Device aDevice, SensorType sensorType,
			DeviceCommand readCommand, String name) {
		Sensor sensor = null;
		if (SensorType.RANGE == sensorType) {
			RangeSensor rangeSensor = new RangeSensor();
			rangeSensor.setMin(0);
			rangeSensor.setMax(255);
			sensor = rangeSensor;

		} else if (SensorType.CUSTOM == sensorType) {
			sensor = new CustomSensor();
		} else {
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

	@Override
	public Class<CreateISYDeviceAction> getActionType() {
		return CreateISYDeviceAction.class;
	}

	@Override
	public void rollback(CreateISYDeviceAction arg0,
			CreateISYDeviceResult arg1, ExecutionContext arg2)
			throws DispatchException {
		// TODO Auto-generated method stub

	}

	private DeviceCommand addDeviceCommand(Device aDevice, String address,
			String name, String command) {
		DeviceCommand dc = new DeviceCommand();

		Protocol protocol = dc.createProtocol(MODEL);

		protocol.addProtocolAttribute(ISY99_XMLPROPERTY_ADDRESS, address + "");
		protocol.addProtocolAttribute(ISY99_XMLPROPERTY_COMMAND, command + "");
//		protocol.addProtocolAttribute(ISY99_XMLPROPERTY_NAME, name + "");

		// protocol.addProtocolAttribute(STR_ATTRIBUTE_NAME_PARAMETER2,
		// parameter2 + "");
		// protocol.addProtocolAttribute(STR_ATTRIBUTE_NAME_OMNI_TYPE,
		// omnilinkType + "");
		// protocol.addProtocolAttribute(STR_ATTRIBUTE_NAME_OMNI_NUM,
		// omnilinkNum + "");

		dc.setName(name);
		dc.setDevice(aDevice);
		aDevice.getDeviceCommands().add(dc);
		return dc;
	}

	public void setDeviceService(DeviceService deviceService) {
		this.deviceService = deviceService;
	}

	// public void setDeviceCommandService(DeviceCommandService
	// deviceCommandService) {
	// this.deviceCommandService = deviceCommandService;
	// }

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

	private List<ISYSeedinfo> getSeedInfo(String ipAddress, String userName,
			String password) {
		List<ISYSeedinfo> returnValue = new ArrayList<CreateISYDevicesActionHandler.ISYSeedinfo>();
		HttpClient httpclient = new HttpClient();
		httpclient.getState().setCredentials(
				new AuthScope(ipAddress, 80),
				new org.apache.commons.httpclient.UsernamePasswordCredentials(
						userName, password));
		GetMethod httpget = new GetMethod("http://" + ipAddress + "/rest/nodes");
		String xml = null;
		try {
			try {
				httpclient.executeMethod(httpget);
				// System.out.println(httpget.getResponseBodyAsString());

				XPathFactory xpathFactory = XPathFactory.newInstance();
				XPath xpath = xpathFactory.newXPath();

				InputSource source = new InputSource(new StringReader(
						httpget.getResponseBodyAsString()));
				DocumentBuilder builder = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder();
				Document doc = builder.parse(source);
				XPathExpression expr = xpath.compile("/nodes/node");
				Object result = expr.evaluate(doc, XPathConstants.NODESET);
				NodeList products = (NodeList) result;
				for (int i = 0; i < products.getLength(); i++) {
					Node n = products.item(i);
					if (n != null && n.getNodeType() == Node.ELEMENT_NODE) {

						String address = xpath.evaluate("address", n);
						String name = xpath.evaluate("name", n);
						String type = xpath.evaluate("type", n);

						returnValue.add(new ISYSeedinfo(address, name, type));
					}
				}

			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			httpget.releaseConnection();
		}
		return returnValue;
	}

	public static void getDeviceListFromISY() {

	}

	private class ISYSeedinfo {
		private String mAddress;
		private String mName;
		private String mType;

		public ISYSeedinfo(String address, String name, String type) {
			mAddress = address;
			mName = name;
			mType = type;
		}

		public String getAddress() {
			return mAddress;
		}

		public String getName() {
			return mName;
		}

		public String getType() {
			return mType;
		}

		public String toString() {
			return "'" + mAddress + "':";
		}
	}

}
