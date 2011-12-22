package org.openremote.modeler.client.widget.buildingmodeler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.FormSubmitListener;
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
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.RangeSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorCommandRef;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.SliderCommandRef;
import org.openremote.modeler.domain.SliderSensorRef;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Label;

public class ImportWizardWindow extends FormWindow {

  private static final String PARSING_ERROR_MESSAGE = "Error parsing file";

  private Device device;
  private Label errorLabel;
  private Button submitButton;

  public ImportWizardWindow(Device device) {
    super();
    this.device = device;

    this.setHeading("Import...");

    form.setAction(GWT.getModuleBaseURL() + "fileUploadController.htm?method=importLutron");
    form.setEncoding(Encoding.MULTIPART);
    form.setMethod(Method.POST);

    createUI();
    show();
  }

  private void createUI() {
    final String NoScene = null;
    final String NoLevel = null;
    final String NoKey = null;

    setWidth(380);
    setAutoHeight(true);
    setLayout(new FlowLayout());

    form.setWidth(370);

    errorLabel = new Label();
    errorLabel.setVisible(false);
    errorLabel.setStyleName("importErrorMessage");
    form.add(errorLabel);

    final FileUploadField fileUploadField = new FileUploadField();
    fileUploadField.setName("file");
    fileUploadField.setAllowBlank(false);
    fileUploadField.setFieldLabel("File");
    fileUploadField.setStyleAttribute("overflow", "hidden");
    form.add(fileUploadField);

    submitButton = new Button("Submit");
    submitButton.addSelectionListener(new FormSubmitListener(form, submitButton));
    form.addButton(submitButton);

    Button cancelBtn = new Button("cancel");
    cancelBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(ButtonEvent ce) {
        ImportWizardWindow.this.hide();
      }
    });
    form.addButton(cancelBtn);
    form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
      public void handleEvent(FormEvent be) {
        errorLabel.setVisible(false);
        mask("Importing, please wait.");

      }
    });

    form.addListener(Events.Submit, new Listener<FormEvent>() {

      private void reportError(String errorMessage) {
        unmask();
        form.clear();
        form.clearState();
        submitButton.enable();
        errorLabel.setText(errorMessage);
        errorLabel.setVisible(true);
      }

      @Override
      public void handleEvent(FormEvent be) {
        LutronImportResultOverlay importResult = LutronImportResultOverlay.fromJSONString(be.getResultHtml());
        if (importResult.getErrorMessage() != null) {
          reportError(importResult.getErrorMessage());
          return;
        }
        
        ProjectOverlay projectOverlay = importResult.getProject();
        if (projectOverlay.getAreas() == null) {
          reportError("File does not contain any information");
          return;
        }
        
        final List<BeanModel> allModels = new ArrayList<BeanModel>();
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
                    fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(allModels));
                  }
                });
              }
            });
          }
        });
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
                  Info.display("INFO", "Output " + output.getName() + " - type is " + output.getType());
                  if ("Dimmer".equals(output.getType()) || "QEDShade".equals(output.getType())) {
    //              if (Output.OutputType.Dimmer.equals(output.getType()) || Output.OutputType.QEDShade.equals(output.getType())) {
                    addDeviceCommand(device, output, "RAISE", NoScene, NoLevel, NoKey, "_Raise");
                    addDeviceCommand(device, output, "LOWER", NoScene, NoLevel, NoKey, "_Lower");
                    addDeviceCommand(device, output, "STOP", NoScene, NoLevel, NoKey, "_Stop");
                    addDeviceCommand(device, output, "FADE", NoScene, NoLevel, NoKey, "_Fade");
                    addDeviceCommand(device, output, "STATUS_DIMMER", NoScene, NoLevel, NoKey, "_LevelRead");
    //              } else if (Output.OutputType.GrafikEyeMainUnit.equals(output.getType())) {
                  } else if ("GrafikEyeMainUnit".equals(output.getType())) {
                    addDeviceCommand(device, output, "SCENE", "0", NoLevel, NoKey, "_SceneOff");
                    addDeviceCommand(device, output, "STATUS_SCENE", "0", NoLevel, NoKey, "_OffRead");
                    for (int sceneNumber = 1; sceneNumber <= 8; sceneNumber++) {
                      addDeviceCommand(device, output, "SCENE", Integer.toString(sceneNumber), NoLevel, NoKey, "_Scene" + Integer.toString(sceneNumber));
                      addDeviceCommand(device, output, "STATUS_SCENE", Integer.toString(sceneNumber), NoLevel, NoKey, "_Scene" + Integer.toString(sceneNumber) + "Read");
                    }
    
                    addDeviceCommand(device, output, "STATUS_SCENE", NoScene, NoLevel, NoKey, "_SceneRead");
                  } else if ("Fan".equals(output.getType())) {
    //              } else if (Output.OutputType.Fan.equals(output.getType())) {
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

    });

    form.layout();

    add(form);
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
