package org.openremote.modeler.server.lutron;

import java.util.HashMap;
import java.util.Map;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.openremote.modeler.client.proxy.DeviceCommandBeanModelProxy;
import org.openremote.modeler.client.widget.buildingmodeler.DeviceCommandWindow;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.RangeSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorCommandRef;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.SliderCommandRef;
import org.openremote.modeler.domain.SliderSensorRef;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.SensorService;
import org.openremote.modeler.service.SliderService;
import org.openremote.modeler.shared.lutron.ImportConfig;
import org.openremote.modeler.shared.lutron.ImportLutronConfigAction;
import org.openremote.modeler.shared.lutron.ImportLutronConfigResult;
import org.openremote.modeler.shared.lutron.OutputImportConfig;
import org.openremote.modeler.shared.lutron.OutputType;
import org.springframework.transaction.annotation.Transactional;

public class ImportLutronConfigActionHandler implements ActionHandler<ImportLutronConfigAction, ImportLutronConfigResult> {

  protected DeviceCommandService deviceCommandService;
  protected SensorService sensorService;
  protected SliderService sliderService;

  public void setDeviceCommandService(DeviceCommandService deviceCommandService) {
    this.deviceCommandService = deviceCommandService;
  }

  public void setSensorService(SensorService sensorService) {
    this.sensorService = sensorService;
  }
  
  public void setSliderService(SliderService sliderService) {
    this.sliderService = sliderService;
  }

  @Override
  public ImportLutronConfigResult execute(ImportLutronConfigAction action, ExecutionContext context) throws DispatchException {
    System.out.println("In ImportLutronConfigActionHandler");

    createDeviceElements(action.getDevice(), action.getConfig());
    
    
    // TODO: handle potential errors and return error message
    // TODO: return created elements / updated device ? See also with RequestProxy mechanism
    

    return new ImportLutronConfigResult();
  }

  @Override
  public Class<ImportLutronConfigAction> getActionType() {
    return ImportLutronConfigAction.class;
  }

  @Override
  public void rollback(ImportLutronConfigAction action, ImportLutronConfigResult result, ExecutionContext context) throws DispatchException {
    // TODO Implementation only required for compound action
  }

  @Transactional
  private void createDeviceElements(Device device, ImportConfig config) {
    final String NoScene = null;
    final String NoLevel = null;
    final String NoKey = null;

    for (OutputImportConfig outputConfig : config.getOutputs()) {
      if (OutputType.Dimmer == outputConfig.getType() || OutputType.QEDShade == outputConfig.getType()) {
        deviceCommandService.save(addDeviceCommand(device, outputConfig.getOutputName() + "_Raise", outputConfig.getAddress(), "RAISE", NoScene, NoLevel, NoKey));
        deviceCommandService.save(addDeviceCommand(device, outputConfig.getOutputName() + "_Lower", outputConfig.getAddress(), "LOWER", NoScene, NoLevel, NoKey));
        deviceCommandService.save(addDeviceCommand(device, outputConfig.getOutputName() + "_Stop", outputConfig.getAddress(), "STOP", NoScene, NoLevel, NoKey));
        DeviceCommand deviceCommand = addDeviceCommand(device, outputConfig.getOutputName() + "_LevelRead", outputConfig.getAddress(), "STATUS_DIMMER", NoScene, NoLevel, NoKey);
        deviceCommandService.save(deviceCommand);        
        Sensor sensor = createDeviceSensor(device, SensorType.LEVEL, deviceCommand, outputConfig.getOutputName() + "_Level");
        sensorService.saveSensor(sensor);
        deviceCommand = addDeviceCommand(device, outputConfig.getOutputName() + "_Fade", outputConfig.getAddress(), "FADE", NoScene, NoLevel, NoKey);
        deviceCommandService.save(deviceCommand);
        sliderService.save(createDeviceSlider(device, deviceCommand, sensor, outputConfig.getOutputName() + "_Slider"));        
      } else if (OutputType.GrafikEyeMainUnit == outputConfig.getType()) {
        deviceCommandService.save(addDeviceCommand(device, outputConfig.getOutputName() + "_SceneOff", outputConfig.getAddress(), "SCENE", "0", NoLevel, NoKey));
        DeviceCommand deviceCommand = addDeviceCommand(device, outputConfig.getOutputName() + "_OffRead", outputConfig.getAddress(), "STATUS_SCENE", "0", NoLevel, NoKey);
        deviceCommandService.save(deviceCommand);
        sensorService.saveSensor(createDeviceSensor(device, SensorType.SWITCH, deviceCommand, outputConfig.getOutputName() + "_OffSelected"));
        for (int sceneNumber = 1; sceneNumber <= 8; sceneNumber++) {
          deviceCommandService.save(addDeviceCommand(device, outputConfig.getOutputName() + "_Scene" + Integer.toString(sceneNumber), outputConfig.getAddress(), "SCENE", Integer.toString(sceneNumber), NoLevel, NoKey));
          deviceCommand = addDeviceCommand(device, outputConfig.getOutputName() + "_Scene" + Integer.toString(sceneNumber) + "Read", outputConfig.getAddress(), "STATUS_SCENE", Integer.toString(sceneNumber), NoLevel, NoKey);
          deviceCommandService.save(deviceCommand);
          sensorService.saveSensor(createDeviceSensor(device, SensorType.SWITCH, deviceCommand, outputConfig.getOutputName() + "_Scene" + Integer.toString(sceneNumber) + "Selected"));
        }
        deviceCommand = addDeviceCommand(device, outputConfig.getOutputName() + "_SceneRead", outputConfig.getAddress(), "STATUS_SCENE", NoScene, NoLevel, NoKey);
        deviceCommandService.save(deviceCommand);
        Sensor sensor = createDeviceSensor(device, SensorType.RANGE, deviceCommand, outputConfig.getOutputName() + "_SelectedScene");
        ((RangeSensor) sensor).setMin(0);
        ((RangeSensor) sensor).setMax(8);
        sensorService.saveSensor(sensor);
      } else if (OutputType.Fan == outputConfig.getType()) {
        deviceCommandService.save(addDeviceCommand(device, outputConfig.getOutputName() + "_Off", outputConfig.getAddress(), "FADE", NoScene, "0", NoKey));
        deviceCommandService.save(addDeviceCommand(device, outputConfig.getOutputName() + "_Low", outputConfig.getAddress(), "FADE", NoScene, "25", NoKey));
        deviceCommandService.save(addDeviceCommand(device, outputConfig.getOutputName() + "_Medium", outputConfig.getAddress(), "FADE", NoScene, "50", NoKey));
        deviceCommandService.save(addDeviceCommand(device, outputConfig.getOutputName() + "_MediumHigh", outputConfig.getAddress(), "FADE", NoScene, "75", NoKey));
        deviceCommandService.save(addDeviceCommand(device, outputConfig.getOutputName() + "_Full", outputConfig.getAddress(), "FADE", NoScene, "100", NoKey));
      }
      // TODO: handle other output types
    }

    /*
    for (ControlStation controlStation : room.getInputs()) {
      for (org.openremote.modeler.server.lutron.importmodel.Device roomDevice : controlStation.getDevices()) {
        for (org.openremote.modeler.server.lutron.importmodel.Button button : roomDevice.getButtons()) {
          if (org.openremote.modeler.server.lutron.importmodel.Device.DeviceType.Keypad.equals(roomDevice.getType())) {
            /*
            addDeviceCommand(device, controlStation.getName() + "_" + button.getName() + "_Press", roomDevice.getAddress(), "PRESS", NoScene, NoLevel, Integer.toString(button.getNumber()));
            addDeviceCommand(device, controlStation.getName() + "_" + button.getName() + "_Release", roomDevice.getAddress(), "RELEASE", NoScene, NoLevel, Integer.toString(button.getNumber()));
            addDeviceCommand(device, controlStation.getName() + "_" + button.getName() + "_Hold", roomDevice.getAddress(), "HOLD", NoScene, NoLevel, Integer.toString(button.getNumber()));
            *//*
            // TODO: if defined as web keypad, generate UI
          }
        }
      }
      // TODO: handle other input types
    }*/

  }

  private DeviceCommand addDeviceCommand(Device aDevice, String name, String address, String command, String scene, String level, String key) {
    DeviceCommand dc = new DeviceCommand();
    Map<String, String> attrMap = new HashMap<String, String>();
    attrMap.put(DeviceCommandWindow.DEVICE_COMMAND_PROTOCOL, "Lutron HomeWorks"); // Display name of protocol needs to be used
    attrMap.put("address", address);
    attrMap.put("command", command);
    if (scene != null) {
      attrMap.put("scene", scene);
    }
    if (level != null) {
      attrMap.put("level", level);
    }
    if (key != null) {
      attrMap.put("key", key);
    }
    dc.setProtocol(DeviceCommandBeanModelProxy.careateProtocol(attrMap, dc));
    dc.setName(name);
    dc.setDevice(aDevice);
    aDevice.getDeviceCommands().add(dc);
    return dc;
  }

  private Sensor createDeviceSensor(Device aDevice, SensorType sensorType, DeviceCommand readCommand, String name) {
    Sensor sensor = null;
    if (SensorType.RANGE == sensorType) {
      sensor = new RangeSensor();
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

  private Slider createDeviceSlider(Device aDevice, DeviceCommand sliderCommand, Sensor readSensor, String name) {
    Slider slider = new Slider();
    slider.setName(name);
    SliderCommandRef sliderCommandRef = new SliderCommandRef();
    sliderCommandRef.setDeviceCommand(sliderCommand);
    sliderCommandRef.setSlider(slider);
    sliderCommandRef.setDeviceName(aDevice.getName());
    slider.setSetValueCmd(sliderCommandRef);
    SliderSensorRef sliderSensorRef = new SliderSensorRef();
    sliderSensorRef.setSensor(readSensor);
    sliderSensorRef.setSlider(slider);
    slider.setSliderSensorRef(sliderSensorRef);
    slider.setDevice(aDevice);
    return slider;
  }

}
