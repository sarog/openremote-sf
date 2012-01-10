package org.openremote.modeler.client.widget.buildingmodeler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.customware.gwt.dispatch.client.DispatchAsync;

import org.openremote.modeler.client.ModelerGinjector;
import org.openremote.modeler.client.lutron.importmodel.AreaOverlay;
import org.openremote.modeler.client.lutron.importmodel.ArrayOverlay;
import org.openremote.modeler.client.lutron.importmodel.LutronImportResultOverlay;
import org.openremote.modeler.client.lutron.importmodel.OutputOverlay;
import org.openremote.modeler.client.lutron.importmodel.ProjectOverlay;
import org.openremote.modeler.client.lutron.importmodel.RoomOverlay;
import org.openremote.modeler.client.proxy.DeviceCommandBeanModelProxy;
import org.openremote.modeler.client.proxy.SensorBeanModelProxy;
import org.openremote.modeler.client.proxy.SliderBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.RangeSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorCommandRef;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.SliderCommandRef;
import org.openremote.modeler.domain.SliderSensorRef;
import org.openremote.modeler.shared.lutron.ImportConfig;
import org.openremote.modeler.shared.lutron.ImportLutronConfigAction;
import org.openremote.modeler.shared.lutron.ImportLutronConfigResult;
import org.openremote.modeler.shared.lutron.OutputImportConfig;
import org.openremote.modeler.shared.lutron.OutputType;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class LutronImportWizard extends DialogBox {

  private static LutronImportWizardUiBinder uiBinder = GWT.create(LutronImportWizardUiBinder.class);

  interface LutronImportWizardUiBinder extends UiBinder<Widget, LutronImportWizard> {
  }
  
  
  private Device device;
  
  final String NoScene = null;
  final String NoLevel = null;
  final String NoKey = null;
  
  @UiFactory
  DialogBox itself() {
    return this;
  }

  public LutronImportWizard(Device device) {    
    this.device = device;

    uiBinder.createAndBindUi(this);
    mainLayout.setSize("50em", "20em");
    center();
    
    errorMessageLabel.setVisible(false);

    uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
    uploadForm.setMethod(FormPanel.METHOD_POST);
    uploadForm.setAction(GWT.getModuleBaseURL() + "fileUploadController.htm?method=importLutron");

    uploadForm.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
    
      @Override
      public void onSubmitComplete(SubmitCompleteEvent event) {
        LutronImportResultOverlay importResult = LutronImportResultOverlay.fromJSONString(event.getResults());
        if (importResult.getErrorMessage() != null) {
          reportError(importResult.getErrorMessage());
          return;
        }
        
        ProjectOverlay projectOverlay = importResult.getProject();
        if (projectOverlay.getAreas() == null) {
          reportError("File does not contain any information");
          return;
        }

        ModelerGinjector injector = GWT.create(ModelerGinjector.class);
        DispatchAsync dispatcher = injector.getDispatchAsync();
        
        ImportConfig importConfig = new ImportConfig();
        
        ArrayOverlay<AreaOverlay> areas = projectOverlay.getAreas();
        for (int i = 0; i < areas.length(); i++) {
          AreaOverlay areaOverlay = areas.get(i);
          if (areaOverlay.getRooms() != null) {
            for (int j = 0; j < areaOverlay.getRooms().length(); j++) {
              RoomOverlay roomOverlay = areaOverlay.getRooms().get(j);
              if (roomOverlay.getOutputs() != null) {
                for (int k = 0; k < roomOverlay.getOutputs().length(); k++) {
                  OutputOverlay outputOverlay = roomOverlay.getOutputs().get(k);
                  importConfig.addOutputConfig(new OutputImportConfig(outputOverlay.getName(), OutputType.valueOf(outputOverlay.getType()), outputOverlay.getAddress(), roomOverlay.getName(), areaOverlay.getName()));
                }
              }
            }
          }
        }
        ImportLutronConfigAction action = new ImportLutronConfigAction(importConfig);
        dispatcher.execute(action, new AsyncCallback<ImportLutronConfigResult>() {

          @Override
          public void onFailure(Throwable caught) {
            Info.display("ERROR", "Call failed " + caught.getLocalizedMessage());
            reportError(caught.getMessage());
          }

          @Override
          public void onSuccess(ImportLutronConfigResult result) {
             Info.display("INFO", "Got result");
          }
          
        });
        
        if (1 == 1) return;

        final List<BeanModel> allModels = new ArrayList<BeanModel>();
        
        // TODO: have a method that saves everything in 1 go, maybe everything is created on the server side, just pass the info required to do it
        
        DeviceCommandBeanModelProxy.saveDeviceCommandList(createDeviceCommands(projectOverlay.getAreas()), new AsyncSuccessCallback<List<BeanModel>>() {
          @Override
          public void onSuccess(final List<BeanModel> deviceCommandModels) {
            Info.display("INFO", "Commands saved");
            allModels.addAll(deviceCommandModels);
            SensorBeanModelProxy.saveSensorList(createSensors(deviceCommandModels), new AsyncSuccessCallback<List<BeanModel>>() {
              @Override
              public void onSuccess(List<BeanModel> sensorModels) {
                Info.display("INFO", "Sensor saved");
                allModels.addAll(sensorModels);
                SliderBeanModelProxy.saveSliderList(createSliders(deviceCommandModels, sensorModels), new AsyncSuccessCallback<List<BeanModel>>() {
                  public void onSuccess(List<BeanModel> sliderModels) {
                    Info.display("INFO", "Slider saved");
                    allModels.addAll(sliderModels);
                    
                    hide();
//                    fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(allModels));
                  }
                  
                  @Override
                  public void onFailure(Throwable caught) {
                    Info.display("ERROR", "Error saving sliders");
                    // TODO: better handling of this
//                    fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(allModels));
                    
                    hide();
                  }
                });
              }
            });
          }
        });
        
//        hide();
       }
    });
  }
  
  private void reportError(String errorMessage) {
    uploadForm.reset();
    errorMessageLabel.setText(errorMessage);
    errorMessageLabel.setVisible(true);
  }

  @UiField
  DockLayoutPanel mainLayout;

  @UiField
  Label errorMessageLabel;
  
  @UiField
  Button submitButton;
  
  @UiField
  Button cancelButton;
  
  @UiField
  FormPanel uploadForm;
  

  @UiField
  FileUpload uploadField;
  
  @UiHandler("cancelButton")
  void handleClick(ClickEvent e) {
    hide();
  }

  
  
  
  
  
  private List<DeviceCommand> createDeviceCommands(final ArrayOverlay<AreaOverlay> areas) {
    for (int i = 0; i < areas.length(); i++) {
      AreaOverlay area = areas.get(i);
      if (area.getRooms() != null) {
        for (int j = 0; j < area.getRooms().length(); j++) {
          RoomOverlay room = area.getRooms().get(j);
          if (room.getOutputs() != null) {
            for (int k = 0; k < room.getOutputs().length(); k++) {
              OutputOverlay output = room.getOutputs().get(k);
              if (OutputType.Dimmer.toString().equals(output.getType()) || OutputType.QEDShade.toString().equals(output.getType())) {
                addDeviceCommand(device, output, "RAISE", NoScene, NoLevel, NoKey, "_Raise");
                addDeviceCommand(device, output, "LOWER", NoScene, NoLevel, NoKey, "_Lower");
                addDeviceCommand(device, output, "STOP", NoScene, NoLevel, NoKey, "_Stop");
                addDeviceCommand(device, output, "FADE", NoScene, NoLevel, NoKey, "_Fade");
                addDeviceCommand(device, output, "STATUS_DIMMER", NoScene, NoLevel, NoKey, "_LevelRead");
              } else if (OutputType.GrafikEyeMainUnit.toString().equals(output.getType())) {
                addDeviceCommand(device, output, "SCENE", "0", NoLevel, NoKey, "_SceneOff");
                addDeviceCommand(device, output, "STATUS_SCENE", "0", NoLevel, NoKey, "_OffRead");
                for (int sceneNumber = 1; sceneNumber <= 8; sceneNumber++) {
                  addDeviceCommand(device, output, "SCENE", Integer.toString(sceneNumber), NoLevel, NoKey, "_Scene" + Integer.toString(sceneNumber));
                  addDeviceCommand(device, output, "STATUS_SCENE", Integer.toString(sceneNumber), NoLevel, NoKey, "_Scene" + Integer.toString(sceneNumber) + "Read");
                }
                addDeviceCommand(device, output, "STATUS_SCENE", NoScene, NoLevel, NoKey, "_SceneRead");
              } else if (OutputType.Fan.toString().equals(output.getType())) {
                addDeviceCommand(device, output, "FADE", NoScene, "0", NoKey, "_Off");
                addDeviceCommand(device, output, "FADE", NoScene, "25", NoKey, "_Low");
                addDeviceCommand(device, output, "FADE", NoScene, "50", NoKey, "_Medium");
                addDeviceCommand(device, output, "FADE", NoScene, "75", NoKey, "_MediumHigh");
                addDeviceCommand(device, output, "FADE", NoScene, "100", NoKey, "_Full");
              }    
              // TODO: handle other output types
            }
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
      }
    }
    Info.display("INFO", "Before returning commands");
    return device.getDeviceCommands();
  }

 private List<Sensor> createSensors(final List<BeanModel> deviceCommands) {
    List<Sensor> result = new ArrayList<Sensor>();
    
    for (BeanModel commandBeanModel : deviceCommands) {
      DeviceCommand deviceCommand = (DeviceCommand)commandBeanModel.getBean();
      if ("STATUS_SCENE".equals(deviceCommand.getProtocol().getAttributeValue("command"))) {
        if (deviceCommand.getProtocol().getAttributeValue("scene") != null) {
          result.add(createDeviceSensor(device, SensorType.SWITCH, deviceCommand, removeEnd(deviceCommand.getName(), 4) + "Selected"));
        } else {
          Sensor sensor = createDeviceSensor(device, SensorType.RANGE, deviceCommand, removeEnd(deviceCommand.getName(), 4) + "SelectedScene");
          ((RangeSensor) sensor).setMin(0);
          ((RangeSensor) sensor).setMax(8);
          result.add(sensor);
        }
      } else if ("STATUS_DIMMER".equals(deviceCommand.getProtocol().getAttributeValue("command"))) {
        result.add(createDeviceSensor(device, SensorType.LEVEL, deviceCommand, removeEnd(deviceCommand.getName(), 4)));
      }
    }
    return result;
  }

  private List<Slider> createSliders(final List<BeanModel> commands, final List<BeanModel>sensors) {
    List<Slider> result = new ArrayList<Slider>();
    for (BeanModel sensorBeanModel : sensors) {
      Sensor sensor = (Sensor)sensorBeanModel.getBean();
      if (sensor.getType() == SensorType.LEVEL) {
        String outputName = removeEnd(sensor.getName(), 6);
        DeviceCommand sliderCommand = null;
        for (BeanModel commandBeanModel : commands) {
          if ((((DeviceCommand)commandBeanModel.getBean()).getName().equals(outputName + "_Fade"))) {
            sliderCommand = (DeviceCommand)commandBeanModel.getBean();
            break;
          }
        }
        if (sliderCommand != null) {
          result.add(createDeviceSlider(device, sliderCommand, sensor, outputName + "_Slider"));
        }
      }
    }
    return result;
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

  private DeviceCommand addDeviceCommand(Device aDevice, OutputOverlay output, String command, String scene, String level, String key, String nameSuffix) {
    return addDeviceCommand(aDevice, output.getName() + nameSuffix, output.getAddress(), command, scene, level, key);
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

  private String removeEnd(String receiver, int length) {
    if (receiver == null) {
      return null;
    }
    int targetLength = receiver.length() - length;
    if (targetLength <= 0) {
      return "";
    }
    return receiver.substring(0, targetLength);
  }
  
}
