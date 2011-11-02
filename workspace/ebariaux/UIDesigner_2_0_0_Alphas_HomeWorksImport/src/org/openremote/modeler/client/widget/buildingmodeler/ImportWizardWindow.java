package org.openremote.modeler.client.widget.buildingmodeler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.lutron.importmodel.Area;
import org.openremote.modeler.client.lutron.importmodel.ControlStation;
import org.openremote.modeler.client.lutron.importmodel.Output;
import org.openremote.modeler.client.lutron.importmodel.Room;
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

        JSONObject jsonProject = JSONParser.parse(be.getResultHtml()).isObject();

        if (jsonProject.get("ERROR") != null) {
          reportError(jsonProject.get("ERROR").isString().stringValue());
          return;
        }
        List<Area> areas = new ArrayList<Area>();

        try {
          if (jsonProject.get("areas") == null) {
            reportError("File does not contain any information");
            return;
          }

          // Start by re-building the Lutron import object model form JSON
          // TODO: this should be handled by a deserializer and not done manually
          JSONArray jsonAreas = jsonProject.get("areas").isArray();
          for (int i = 0; i < jsonAreas.size(); i++) {
            JSONObject jsonArea = jsonAreas.get(i).isObject();
            Area area = new Area(jsonArea.get("name").isString().stringValue());
            areas.add(area);
            if (jsonArea.get("rooms") != null) {
              JSONArray jsonRooms = jsonArea.get("rooms").isArray();
              for (int j = 0; j < jsonRooms.size(); j++) {
                JSONObject jsonRoom = jsonRooms.get(i).isObject();
                Room room = new Room(jsonRoom.get("name").isString().stringValue());
                area.addRoom(room);
                if (jsonRoom.get("outputs") != null) {
                  JSONArray jsonOutputs = jsonRoom.get("outputs").isArray();
                  for (int k = 0; k < jsonOutputs.size(); k++) {
                    JSONObject jsonOutput = jsonOutputs.get(k).isObject();
                    Output output = new Output(jsonOutput.get("name").isString().stringValue(), Output.OutputType.valueOf(jsonOutput.get("type").isString().stringValue()), jsonOutput.get("address").isString().stringValue());
                    room.addOutput(output);
                  }
                }
                if (jsonRoom.get("inputs") != null) {
                  JSONArray jsonInputs = jsonRoom.get("inputs").isArray();
                  for (int k = 0; k < jsonInputs.size(); k++) {
                    JSONObject jsonControlStation = jsonInputs.get(k).isObject();
                    ControlStation controlStation = new ControlStation(jsonControlStation.get("name").isString().stringValue());
                    room.addInput(controlStation);
                    JSONArray jsonDevices = jsonControlStation.get("devices").isArray();
                    for (int l = 0; l < jsonDevices.size(); l++) {
                      JSONObject jsonDevice = jsonDevices.get(l).isObject();
                      org.openremote.modeler.client.lutron.importmodel.Device aDevice = new org.openremote.modeler.client.lutron.importmodel.Device(
                              org.openremote.modeler.client.lutron.importmodel.Device.DeviceType.valueOf(jsonDevice.get("type").isString().stringValue()),
                              jsonDevice.get("address").isString().stringValue(),
                              jsonDevice.get("webEnabled").isBoolean().booleanValue(), jsonDevice.get("webKeypadName").isString().stringValue());
                      controlStation.addDevice(aDevice);
                      JSONArray jsonButtons = jsonDevice.get("buttons").isArray();
                      for (int m = 0; m < jsonButtons.size(); m++) {
                        JSONObject jsonButton = jsonButtons.get(m).isObject();
                        org.openremote.modeler.client.lutron.importmodel.Button button = new org.openremote.modeler.client.lutron.importmodel.Button(jsonButton.get("name").isString().stringValue(), (int) jsonButton.get("number").isNumber().doubleValue());
                        aDevice.addButton(button);
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
        
        final List<BeanModel> allModels = new ArrayList<BeanModel>();
        DeviceCommandBeanModelProxy.saveDeviceCommandList(createDeviceCommands(areas), new AsyncSuccessCallback<List<BeanModel>>() {
          @Override
          public void onSuccess(final List<BeanModel> deviceCommandModels) {
            allModels.addAll(deviceCommandModels);
            SensorBeanModelProxy.saveSensorList(createSensors(deviceCommandModels), new AsyncSuccessCallback<List<BeanModel>>() {
              @Override
              public void onSuccess(List<BeanModel> sensorModels) {
                allModels.addAll(sensorModels);
                SliderBeanModelProxy.saveSliderList(createSliders(deviceCommandModels, sensorModels), new AsyncSuccessCallback<List<BeanModel>>() {
                  public void onSuccess(List<BeanModel> sliderModels) {
                    allModels.addAll(sliderModels);
                    fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(allModels));
                  }
                });
              }
            });
          }
        });
      }

      private List<DeviceCommand> createDeviceCommands(final List<Area> areas) {
        for (Area area : areas) {
          for (Room room : area.getRooms()) {
            for (Output output : room.getOutputs()) {
              if (Output.OutputType.Dimmer.equals(output.getType()) || Output.OutputType.QEDShade.equals(output.getType())) {
                addDeviceCommand(device, output, "RAISE", NoScene, NoLevel, NoKey, "_Raise");
                addDeviceCommand(device, output, "LOWER", NoScene, NoLevel, NoKey, "_Lower");
                addDeviceCommand(device, output, "STOP", NoScene, NoLevel, NoKey, "_Stop");
                addDeviceCommand(device, output, "FADE", NoScene, NoLevel, NoKey, "_Fade");
                addDeviceCommand(device, output, "STATUS_DIMMER", NoScene, NoLevel, NoKey, "_LevelRead");
//                addDeviceSlider(device, output, sliderCommand, sensor);
              } else if (Output.OutputType.GrafikEyeMainUnit.equals(output.getType())) {
                addDeviceCommand(device, output, "SCENE", "0", NoLevel, NoKey, "_SceneOff");
                addDeviceCommand(device, output, "STATUS_SCENE", "0", NoLevel, NoKey, "_OffRead");
                for (int sceneNumber = 1; sceneNumber <= 8; sceneNumber++) {
                  addDeviceCommand(device, output, "SCENE", Integer.toString(sceneNumber), NoLevel, NoKey, "_Scene" + Integer.toString(sceneNumber));
                  addDeviceCommand(device, output, "STATUS_SCENE", Integer.toString(sceneNumber), NoLevel, NoKey, "_Scene" + Integer.toString(sceneNumber) + "Read");
                }

                addDeviceCommand(device, output, "STATUS_SCENE", NoScene, NoLevel, NoKey, "_SceneRead");
              } else if (Output.OutputType.Fan.equals(output.getType())) {
                addDeviceCommand(device, output, "FADE", NoScene, "0", NoKey, "_Off");
                addDeviceCommand(device, output, "FADE", NoScene, "25", NoKey, "_Low");
                addDeviceCommand(device, output, "FADE", NoScene, "50", NoKey, "_Medium");
                addDeviceCommand(device, output, "FADE", NoScene, "75", NoKey, "_MediumHigh");
                addDeviceCommand(device, output, "FADE", NoScene, "100", NoKey, "_Full");
              }

              // TODO: handle other output types
            }
            for (ControlStation controlStation : room.getInputs()) {
              for (org.openremote.modeler.client.lutron.importmodel.Device roomDevice : controlStation.getDevices()) {
                for (org.openremote.modeler.client.lutron.importmodel.Button button : roomDevice.getButtons()) {
                  if (org.openremote.modeler.client.lutron.importmodel.Device.DeviceType.Keypad.equals(roomDevice.getType())) {
                    /*
                    addDeviceCommand(device, controlStation.getName() + "_" + button.getName() + "_Press", roomDevice.getAddress(), "PRESS", NoScene, NoLevel, Integer.toString(button.getNumber()));
                    addDeviceCommand(device, controlStation.getName() + "_" + button.getName() + "_Release", roomDevice.getAddress(), "RELEASE", NoScene, NoLevel, Integer.toString(button.getNumber()));
                    addDeviceCommand(device, controlStation.getName() + "_" + button.getName() + "_Hold", roomDevice.getAddress(), "HOLD", NoScene, NoLevel, Integer.toString(button.getNumber()));
                    */
                    // TODO: if defined as web keypad, generate UI
                  }
                }
              }
              // TODO: handle other input types
            }
          }
        }

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

  private static Sensor createDeviceSensor(Device aDevice, SensorType sensorType, DeviceCommand readCommand, String name) {
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
    sensorCommandRef.setSensor(sensor); // TODO: try not setting this and not re-setting it in backend -> hibernate exception still ? -> No exception on create but relationship not correctly set and impossible to delete later
//    sensorCommandRef.setDeviceName(aDevice.getName());
    sensor.setSensorCommandRef(sensorCommandRef);
    sensor.setDevice(aDevice);
//    aDevice.getSensors().add(sensor);
    return sensor;
  }

  private static Slider createDeviceSlider(Device aDevice, DeviceCommand sliderCommand, Sensor readSensor, String name) {
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
//    aDevice.getSliders().add(slider);
    return slider;
  }

  private static DeviceCommand addDeviceCommand(Device aDevice, Output output, String command, String scene, String level, String key, String nameSuffix) {
    return addDeviceCommand(aDevice, output.getName() + nameSuffix, output.getAddress(), command, scene, level, key);
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
  
  private static String removeEnd(String receiver, int length) {
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
