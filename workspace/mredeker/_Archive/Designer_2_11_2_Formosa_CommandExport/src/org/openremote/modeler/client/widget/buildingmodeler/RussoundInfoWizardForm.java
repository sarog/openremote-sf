/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.modeler.client.widget.buildingmodeler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.proxy.DeviceBeanModelProxy;
import org.openremote.modeler.client.proxy.DeviceCommandBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.CommonForm;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.RangeSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorCommandRef;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.SliderCommandRef;
import org.openremote.modeler.domain.SliderSensorRef;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.domain.SwitchCommandOffRef;
import org.openremote.modeler.domain.SwitchCommandOnRef;
import org.openremote.modeler.domain.SwitchSensorRef;
import org.openremote.modeler.selenium.DebugId;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.RowData;

/**
 * Wizard form for {@link DeviceInfoForm}, this is a part of {@link DeviceWizardWindow}.
 * 
 * @author Dan 2009-8-21
 */
public class RussoundInfoWizardForm extends CommonForm {

  /** The Constant DEVICE_NAME. */
  public static final String DEVICE_NAME = "name";
  
  /** The Constant DEVICE_VENDOR. */
  public static final String DEVICE_VENDOR = "vendor";
  
  /** The Constant DEVICE_MODEL. */
  public static final String DEVICE_MODEL = "model";
  
  /** The Constant CONTROLLERS. */
  public static final String CONTROLLERS = "controllers";
  
  /** The wrapper. */
  final protected Component wrapper;
  
  TextField<String> nameField;
  SimpleComboBox<String> modelField;
  TextField<String> controllerCountField;
  
   /**
    * Instantiates a new device info wizard form.
    * 
    * @param wrapper
    *           the wrapper
    * @param deviceBeanModel
    *           the device bean model
    */
   public RussoundInfoWizardForm(Component parent) {
     super();
     this.wrapper = parent;
     createFields();
     addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
        public void handleEvent(FormEvent be) {
          ArrayList<Device> deviceList = createRussoundDevices();
          if ((null==deviceList) || deviceList.isEmpty()) {
            return;
          }
          DeviceBeanModelProxy.saveDevicesWithContents(deviceList, new AsyncSuccessCallback<ArrayList<BeanModel>>() {
            @Override
            public void onSuccess(ArrayList<BeanModel> deviceModels) {
              wrapper.fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(deviceModels));
            }
          });
        }
     });
   }
   


  protected ArrayList<Device> createRussoundDevices()
  {
    String deviceName = nameField.getValue();
    String vendor = "Russound";
    String model = modelField.getSimpleValue();
    int zonesPerController = -1;
    if (model.equals("CA-S44")) {
      zonesPerController = 4;
    } else if (model.equals("MCA-C5")) {
      zonesPerController = 8;
    } else {
      zonesPerController = 6;
    }
    int controllerCount = Integer.parseInt(controllerCountField.getValue()); 
    ArrayList<Device> deviceList = new ArrayList<Device>();
    for (int controller = 1; controller <= controllerCount; controller++)
    {
      for (int zone = 1; zone <= zonesPerController; zone++)
      {
        int zoneNumber = (controller-1) * zonesPerController + zone;
        String name = deviceName + " - Zone"+zoneNumber;
        Device device = new Device();
        device.setModel(model);
        device.setName(name);
        device.setVendor(vendor);
        
        addRussoundCommands(device, zone, controller, zoneNumber, zonesPerController);
        addRussoundSensors(device);
        addRussoundSwitches(device);
        addRussoundSliders(device);
        
        deviceList.add(device);
      }
    }
    
    return deviceList;
  }


  private void addRussoundCommands(Device device, int zone, int controller, int zoneNumber, int zonesPerController)
  {
    HashMap<String, String> cmdNames = new HashMap<String, String>();
    cmdNames.put("All Zones (OFF)", "ALL_OFF");
    cmdNames.put("All Zones (ON)", "ALL_ON");
    cmdNames.put("Power (ON)", "POWER_ON");
    cmdNames.put("Power (OFF)", "POWER_OFF");
    cmdNames.put("Power (STATUS)", "GET_POWER_STATUS");
    cmdNames.put("Volume (UP)", "VOL_UP");
    cmdNames.put("Volume (DOWN)", "VOL_DOWN");
    cmdNames.put("Volume (GET)", "GET_VOLUME");
    cmdNames.put("Volume (SET)", "SET_VOLUME");
    cmdNames.put("TurnOnVolume (SET)", "SET_TURNON_VOLUME");
    cmdNames.put("TurnOnVolume (GET)", "GET_TURNON_VOLUME");
    cmdNames.put("Loudness (ON)", "SET_LOUDNESS_ON");
    cmdNames.put("Loudness (OFF)", "SET_LOUDNESS_OFF");
    cmdNames.put("Loudness (STATUS)", "GET_LOUDNESS_MODE");
    cmdNames.put("Bass Level (SET)", "SET_BASS_LEVEL");
    cmdNames.put("Bass Level (GET)", "GET_BASS_LEVEL");
    cmdNames.put("Treble Level (SET)", "SET_TREBLE_LEVEL");
    cmdNames.put("Treble Level (GET)", "GET_TREBLE_LEVEL");
    cmdNames.put("Balance Level (SET)", "SET_BALANCE_LEVEL");
    cmdNames.put("Balance Level (GET)", "GET_BALANCE_LEVEL");
    cmdNames.put("Source (SET)", "SET_SOURCE");
    cmdNames.put("Source (GET)", "GET_SOURCE");
    
    for (Entry<String, String> cmd : cmdNames.entrySet())
    {
      DeviceCommand deviceCommand = new DeviceCommand();
      deviceCommand.setName("Zone"+zoneNumber+ " " + cmd.getKey());
      deviceCommand.setDevice(device);
      HashMap<String, String> attrMap = new HashMap<String, String>();
      attrMap.put("controller", ""+controller);
      attrMap.put("zone", ""+zone);
      attrMap.put("command", cmd.getValue());
      attrMap.put("protocol", "Russound RNET Protocol");
      deviceCommand.setProtocol(DeviceCommandBeanModelProxy.careateProtocol(attrMap, deviceCommand));
      device.getDeviceCommands().add(deviceCommand);
    }
  }


  private void addRussoundSensors(Device device)
  {
    List<DeviceCommand> commands = device.getDeviceCommands();
    for (DeviceCommand deviceCommand : commands)
    {
      if (deviceCommand.getName().indexOf("STATUS") != -1) {
        Sensor sensor = createSensor(SensorType.SWITCH, deviceCommand, device, 0, 0);
        device.getSensors().add(sensor);
      } else if (deviceCommand.getName().indexOf("GET") != -1) {
        if (deviceCommand.getName().indexOf("Volume") != -1) {
          Sensor sensor = createSensor(SensorType.RANGE, deviceCommand, device, 0, 100);
          device.getSensors().add(sensor);
        } else {
          Sensor sensor = createSensor(SensorType.RANGE, deviceCommand, device, -10, 10);
          device.getSensors().add(sensor);
        }
      }
    }
  }

  private Sensor createSensor(SensorType type, DeviceCommand command, Device device, int min, int max) {
    Sensor sensor;
    if (type == SensorType.RANGE) {
        sensor = new RangeSensor();
       ((RangeSensor) sensor).setMin(min);
       ((RangeSensor) sensor).setMax(max);
    } else {
        sensor = new Sensor();
    }
    sensor.setType(type);
    sensor.setName(command.getName().substring(0,command.getName().indexOf('(')-1));
  
    SensorCommandRef sensorCommandRef = new SensorCommandRef();
    sensorCommandRef.setDeviceCommand(command);
    sensorCommandRef.setSensor(sensor);
    sensor.setSensorCommandRef(sensorCommandRef);
    sensor.setDevice(device);
    return sensor;
  }
  
  private void addRussoundSliders(Device device)
  {
    HashMap<String, DeviceCommand> sliderCommands = new HashMap<String, DeviceCommand>();
    HashMap<DeviceCommand, Sensor> sliderCommandsAndSensors = new HashMap<DeviceCommand, Sensor>();
    for (DeviceCommand deviceCommand : device.getDeviceCommands())
    {
      if (deviceCommand.getName().indexOf("(SET)") != -1) {
        sliderCommands.put(deviceCommand.getName().replace(" (SET)", ""), deviceCommand);
      }
    }
    for (Sensor sensor : device.getSensors())
    {
      if (sliderCommands.get(sensor.getName()) != null) {
        sliderCommandsAndSensors.put(sliderCommands.get(sensor.getName()), sensor);
      }
    }
    for (Entry<DeviceCommand, Sensor> entry : sliderCommandsAndSensors.entrySet())
    {
      Slider slider = createSlider(entry.getKey(), entry.getValue());
      device.getSliders().add(slider);
    }
  }



  private void addRussoundSwitches(Device device)
  {
    DeviceCommand powerOn = null;
    DeviceCommand powerOff = null;
    DeviceCommand loudnessOn = null;
    DeviceCommand loudnessOff = null;
    Sensor powerSensor = null;
    Sensor loudnessSensor = null;
    for (DeviceCommand deviceCommand : device.getDeviceCommands())
    {
      if (deviceCommand.getName().indexOf("Power (ON)") != -1) {
        powerOn = deviceCommand;
      }
      else if (deviceCommand.getName().indexOf("Power (OFF)") != -1) {
        powerOff = deviceCommand;
      }
      else if (deviceCommand.getName().indexOf("Loudness (ON)") != -1) {
        loudnessOn = deviceCommand;
      }
      else if (deviceCommand.getName().indexOf("Loudness (OFF)") != -1) {
        loudnessOff = deviceCommand;
      }
    }
    for (Sensor sensor : device.getSensors())
    {
      if (sensor.getName().indexOf("Power") != -1) {
        powerSensor = sensor;
      }
      else if (sensor.getName().indexOf("Loudness") != -1) {
        loudnessSensor = sensor;
      }      
    }
    Switch powerSwitch = createSwitch(powerOn, powerOff, powerSensor);
    device.getSwitchs().add(powerSwitch);
    Switch loudnessSwitch = createSwitch(loudnessOn, loudnessOff, loudnessSensor);
    device.getSwitchs().add(loudnessSwitch);
  }

  
  private Slider createSlider(DeviceCommand setCmd, Sensor sensor) {
    Slider newSlider = new Slider();
    newSlider.setDevice(sensor.getDevice());
    SliderCommandRef setValueCmdRef = new SliderCommandRef();
    setValueCmdRef.setDeviceCommand(setCmd);
    setValueCmdRef.setDeviceName(setCmd.getDevice().getName());
    setValueCmdRef.setSlider(newSlider);
    SliderSensorRef sensorRef = new SliderSensorRef(newSlider);
    sensorRef.setSensor(sensor);
    newSlider.setSetValueCmd(setValueCmdRef);
    newSlider.setSliderSensorRef(sensorRef);
    newSlider.setName(sensor.getName()+" Slider");
    return newSlider;
  }

  private Switch createSwitch(DeviceCommand onCmd, DeviceCommand offCmd, Sensor sensor) {
    final Switch newSwitch = new Switch();
    newSwitch.setDevice(sensor.getDevice());
    SwitchCommandOnRef onRef = new SwitchCommandOnRef();
    onRef.setDeviceCommand(onCmd);
    onRef.setDeviceName(onCmd.getDevice().getName());
    onRef.setOnSwitch(newSwitch);
    SwitchCommandOffRef offRef = new SwitchCommandOffRef();
    offRef.setDeviceCommand(offCmd);
    offRef.setDeviceName(offCmd.getDevice().getName());
    offRef.setOffSwitch(newSwitch);
    SwitchSensorRef sensorRef = new SwitchSensorRef(newSwitch);
    sensorRef.setSensor(sensor);
    newSwitch.setSwitchCommandOnRef(onRef);
    newSwitch.setSwitchCommandOffRef(offRef);
    newSwitch.setSwitchSensorRef(sensorRef);
    newSwitch.setName(sensor.getName()+ " Switch");
    return newSwitch;
  }

  /**
    * Creates the fields.
    */
   private void createFields() {
      nameField = new TextField<String>();
      nameField.setName(DEVICE_NAME);
      nameField.ensureDebugId(DebugId.DEVICE_NAME_FIELD);
      nameField.setFieldLabel("Name");
      nameField.setAllowBlank(false);
    
      modelField = new SimpleComboBox<String>();
      modelField.setFieldLabel("Model");  
      modelField.setName(DEVICE_MODEL);
      modelField.ensureDebugId(DebugId.DEVICE_MODEL_FIELD);
      modelField.setAllowBlank(false);
      modelField.setEditable(false);
      modelField.add(Arrays.asList(new String[]{"CA-S44", "CA-A66", "CA-M66", "CA-V66", "MCA-C3", "MCA-C5"}));
      
      controllerCountField = new TextField<String>();
      controllerCountField.setName(CONTROLLERS);
      controllerCountField.setFieldLabel("No of controller");
      controllerCountField.setAllowBlank(false);
     
      add(nameField);
      add(modelField);
      add(controllerCountField);
   }
   
   

   /* (non-Javadoc)
    * @see org.openremote.modeler.client.widget.CommonForm#isNoButton()
    */
   @Override
   public boolean isNoButton() {
      return true;
   }

   /* (non-Javadoc)
    * @see com.extjs.gxt.ui.client.widget.Component#show()
    */
   @Override
   public void show() {
      super.show();
      ((Window) wrapper).setSize(360, 250);
   }
   
   
   

}
