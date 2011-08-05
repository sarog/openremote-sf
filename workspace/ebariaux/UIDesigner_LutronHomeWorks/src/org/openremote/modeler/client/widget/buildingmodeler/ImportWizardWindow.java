package org.openremote.modeler.client.widget.buildingmodeler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.proxy.DeviceCommandBeanModelProxy;
import org.openremote.modeler.client.proxy.SensorBeanModelProxy;
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
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
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
        final String NoScene = null;
        final String NoLevel = null;
        final String NoKey = null;

        JSONObject jsonProject = JSONParser.parse(be.getResultHtml()).isObject();

        if (jsonProject.get("ERROR") != null) {
          reportError(jsonProject.get("ERROR").isString().stringValue());
          return;
        }

        try {
          if (jsonProject.get("areas") == null) {
            reportError("File does not contain any information");
            return;
          }
          JSONArray jsonAreas = jsonProject.get("areas").isArray();
          for (int i = 0; i < jsonAreas.size(); i++) {
            JSONObject jsonArea = jsonAreas.get(i).isObject();
            if (jsonArea.get("rooms") != null) {
              JSONArray jsonRooms = jsonArea.get("rooms").isArray();
              for (int j = 0; j < jsonRooms.size(); j++) {
                JSONObject jsonRoom = jsonRooms.get(i).isObject();
                if (jsonRoom.get("outputs") != null) {
                  JSONArray jsonOutputs = jsonRoom.get("outputs").isArray();
                  for (int k = 0; k < jsonOutputs.size(); k++) {
                    JSONObject jsonOutput = jsonOutputs.get(k).isObject();
                    String outputType = jsonOutput.get("type").isString().stringValue();
                    if ("Dimmer".equals(outputType) || "QEDShade".equals(outputType)) {
                      addDeviceCommand(device, jsonOutput, "RAISE", NoScene, NoLevel, NoKey, "_Raise");
                      addDeviceCommand(device, jsonOutput, "LOWER", NoScene, NoLevel, NoKey, "_Lower");
                      addDeviceCommand(device, jsonOutput, "STOP", NoScene, NoLevel, NoKey, "_Stop");
                      DeviceCommand sliderCommand = addDeviceCommand(device, jsonOutput, "FADE", NoScene, NoLevel, NoKey, "_Fade");

                      DeviceCommand levelReadCommand = addDeviceCommand(device, jsonOutput, "STATUS_DIMMER", NoScene, NoLevel, NoKey, "_LevelRead");
                      Sensor sensor = addDeviceSensor(device, jsonOutput, SensorType.LEVEL, levelReadCommand, "_Level");

                      Slider slider = new Slider();
                      slider.setName(jsonOutput.get("name").isString().stringValue() + "_Slider");
                      SliderCommandRef sliderCommandRef = new SliderCommandRef();
                      sliderCommandRef.setDeviceCommand(sliderCommand);
                      sliderCommandRef.setSlider(slider);
                      sliderCommandRef.setDeviceName(device.getName());
                      slider.setSetValueCmd(sliderCommandRef);
                      SliderSensorRef sliderSensorRef = new SliderSensorRef();
                      sliderSensorRef.setSensor(sensor);
                      sliderSensorRef.setSlider(slider);
                      slider.setSliderSensorRef(sliderSensorRef);
                      slider.setDevice(device);
                      device.getSliders().add(slider);
                    } else if ("GrafikEyeMainUnit".equals(outputType)) {
                      addDeviceCommand(device, jsonOutput, "SCENE", "0", NoLevel, NoKey, "_SceneOff");
                      DeviceCommand dc = addDeviceCommand(device, jsonOutput, "SCENE_STATUS", "0", NoLevel, NoKey, "_OffRead");
                      for (int sceneNumber = 1; sceneNumber <= 8; sceneNumber++) {
                        addDeviceCommand(device, jsonOutput, "SCENE", Integer.toString(sceneNumber), NoLevel, NoKey, "_Scene" + Integer.toString(sceneNumber));
                        dc = addDeviceCommand(device, jsonOutput, "SCENE_STATUS", Integer.toString(sceneNumber), NoLevel, NoKey, "_Scene" + Integer.toString(sceneNumber) + "Read");
                        addDeviceSensor(device, jsonOutput, SensorType.SWITCH, dc, "_Scene" + Integer.toString(sceneNumber) + "Selected");
                      }

                      dc = addDeviceCommand(device, jsonOutput, "SCENE_STATUS", NoScene, NoLevel, NoKey, "_SceneRead");
                      Sensor sensor = addDeviceSensor(device, jsonOutput, SensorType.RANGE, dc, "_SelectedScene");
                      ((RangeSensor) sensor).setMin(0);
                      ((RangeSensor) sensor).setMax(8);
                    } else if ("Fan".equals(outputType)) {
                      addDeviceCommand(device, jsonOutput, "FADE", NoScene, "0", NoKey, "_Off");
                      addDeviceCommand(device, jsonOutput, "FADE", NoScene, "25", NoKey, "_Low");
                      addDeviceCommand(device, jsonOutput, "FADE", NoScene, "50", NoKey, "_Medium");
                      addDeviceCommand(device, jsonOutput, "FADE", NoScene, "75", NoKey, "_MediumHigh");
                      addDeviceCommand(device, jsonOutput, "FADE", NoScene, "100", NoKey, "_Full");
                    }

                    // TODO: handle other output types
                  }
                }
                if (jsonRoom.get("inputs") != null) {
                  JSONArray jsonInputs = jsonRoom.get("inputs").isArray();
                  for (int k = 0; k < jsonInputs.size(); k++) {
                    JSONObject jsonControlStation = jsonInputs.get(k).isObject();
                    String controlStationName = jsonControlStation.get("name").isString().stringValue();
                    JSONArray jsonDevices = jsonControlStation.get("devices").isArray();
                    for (int l = 0; l < jsonDevices.size(); l++) {
                      JSONObject jsonDevice = jsonDevices.get(l).isObject();
                      String deviceType = jsonDevice.get("type").isString().stringValue();
                      String address = jsonDevice.get("address").isString().stringValue();
                      JSONArray jsonButtons = jsonDevice.get("buttons").isArray();
                      for (int m = 0; m < jsonButtons.size(); m++) {
                        JSONObject jsonButton = jsonButtons.get(m).isObject();
                        if ("Keypad".equals(deviceType)) {
                          int buttonNumber = (int) jsonButton.get("number").isNumber().doubleValue();
                          String buttonName = jsonButton.get("name").isString().stringValue();
                          addDeviceCommand(device, controlStationName + "_" + buttonName + "_Press", address, "PRESS", NoScene, NoLevel, Integer.toString(buttonNumber));
                          addDeviceCommand(device, controlStationName + "_" + buttonName + "_Release", address, "RELEASE", NoScene, NoLevel, Integer.toString(buttonNumber));
                          addDeviceCommand(device, controlStationName + "_" + buttonName + "_Hold", address, "HOLD", NoScene, NoLevel, Integer.toString(buttonNumber));

                          // TODO: if defined as web keypad, generate UI
                        }

                        // TODO: handle other input types
                      }
                    }
                  }
                }
              }
            }
          }
        } catch (Exception e) {
          reportError(PARSING_ERROR_MESSAGE);
          return;
        }

        fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(device));
      }
    });

    form.layout();

    add(form);
  }

  private static Sensor addDeviceSensor(Device aDevice, JSONObject jsonOutput, SensorType sensorType, DeviceCommand readCommand, String nameSuffix) {
    Sensor sensor = null;
    if (SensorType.RANGE == sensorType) {
      sensor = new RangeSensor();
    } else {
      sensor = new Sensor();
    }
    sensor.setName(jsonOutput.get("name").isString().stringValue() + nameSuffix);
    sensor.setType(sensorType);
    SensorCommandRef sensorCommandRef = new SensorCommandRef();
    sensorCommandRef.setDeviceCommand(readCommand);
    sensorCommandRef.setSensor(sensor);
    sensorCommandRef.setDeviceName(aDevice.getName());
    sensor.setSensorCommandRef(sensorCommandRef);
    sensor.setDevice(aDevice);
    aDevice.getSensors().add(sensor);
    return sensor;
  }

  private static DeviceCommand addDeviceCommand(Device aDevice, JSONObject output, String command, String scene, String level, String key, String nameSuffix) {
    return addDeviceCommand(aDevice, output.get("name").isString().stringValue() + nameSuffix, output.get("address").isString().stringValue(), command, scene, level, key);
  }

  private static DeviceCommand addDeviceCommand(Device aDevice, String name, String address, String command, String scene, String level, String key) {
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
}
