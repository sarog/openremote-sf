package org.openremote.modeler.client.widget.buildingmodeler;

import java.util.HashMap;
import java.util.Map;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.proxy.DeviceCommandBeanModelProxy;
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

public class ImportWizardWindow extends FormWindow {

  private Device device;

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

    final FileUploadField fileUploadField = new FileUploadField();
    fileUploadField.setName("file");
    fileUploadField.setAllowBlank(false);
    fileUploadField.setFieldLabel("File");
    fileUploadField.setStyleAttribute("overflow", "hidden");
    form.add(fileUploadField);

    /*
    fileUploadField.addListener(Events.OnChange, new Listener<FieldEvent>() {
      public void handleEvent(FieldEvent be) {
         if (!form.isValid()) {
            return;
         }
         form.setAction(GWT.getModuleBaseURL() + "fileUploadController.htm?method=importLutron");
         form.setEncoding(Encoding.MULTIPART);
         form.setMethod(Method.POST);
         Info.display("Info", "File form submitted, action " + form.getAction());

         form.submit();
         mask("Importing, please wait.");
      }
   });
   */

    
    
    Button submitBtn = new Button("Submit");
    submitBtn.addSelectionListener(new FormSubmitListener(form, submitBtn));
    form.addButton(submitBtn);
    
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
//              fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(null));
       mask("Importing, please wait.");

     }
   });

   form.addListener(Events.Submit, new Listener<FormEvent>() {
     @Override
     public void handleEvent(FormEvent be) {
       final String NoScene = null;
       final String NoLevel = null;
       
       JSONObject jsonProject = JSONParser.parse(be.getResultHtml()).isObject();
 
       Device aDevice = new Device();
       aDevice.setName(jsonProject.get("name").isString().stringValue());
       
       JSONArray jsonAreas = jsonProject.get("areas").isArray();
       for (int i = 0; i < jsonAreas.size(); i++) {
         JSONArray jsonRooms = jsonAreas.get(i).isObject().get("rooms").isArray();
         for (int j = 0; j < jsonRooms.size(); j++) {
           JSONArray jsonOutputs = jsonRooms.get(i).isObject().get("outputs").isArray();
           for (int k = 0; k < jsonOutputs.size(); k++) {
             JSONObject jsonOutput = jsonOutputs.get(k).isObject();
             String outputType = jsonOutput.get("type").isString().stringValue();
             
             
             // TODO: handle other output types and handle inputs
             
             
             if ("Dimmer".equals(outputType) || "QEDShade".equals(outputType)) {
               addDeviceCommand(aDevice, jsonOutput, "RAISE", NoScene, NoLevel, "_Raise");
               addDeviceCommand(aDevice, jsonOutput, "LOWER", NoScene, NoLevel, "_Lower");
               addDeviceCommand(aDevice, jsonOutput, "STOP", NoScene, NoLevel, "_Stop");
               DeviceCommand sliderCommand = addDeviceCommand(aDevice, jsonOutput, "FADE", NoScene, NoLevel, "_Fade");

               DeviceCommand levelReadCommand = addDeviceCommand(aDevice, jsonOutput, "STATUS_DIMMER", NoScene, NoLevel, "_LevelRead");
               Sensor sensor = addDeviceSensor(aDevice, jsonOutput, SensorType.LEVEL, levelReadCommand, "Level");

               Slider slider = new Slider();
               slider.setName(jsonOutput.get("name").isString().stringValue() + "_Slider");
               SliderCommandRef sliderCommandRef = new SliderCommandRef();
               sliderCommandRef.setDeviceCommand(sliderCommand);
               sliderCommandRef.setSlider(slider);
               sliderCommandRef.setDeviceName(aDevice.getName());
               slider.setSetValueCmd(sliderCommandRef);
               SliderSensorRef sliderSensorRef = new SliderSensorRef();
               sliderSensorRef.setSensor(sensor);
               sliderSensorRef.setSlider(slider);
               slider.setSliderSensorRef(sliderSensorRef);
               aDevice.getSliders().add(slider);
             } else if ("GrafikEyeMainUnit".equals(outputType)) {
               addDeviceCommand(aDevice, jsonOutput, "SCENE", "0", NoLevel, "SceneOff");
               DeviceCommand dc = addDeviceCommand(aDevice, jsonOutput, "SCENE_STATUS", "0", NoLevel, "OffRead");
               for (int sceneNumber = 1; sceneNumber <= 8; sceneNumber++) {
                 addDeviceCommand(aDevice, jsonOutput, "SCENE", Integer.toString(sceneNumber), NoLevel, "Scene" + Integer.toString(sceneNumber));
                 dc = addDeviceCommand(aDevice, jsonOutput, "SCENE_STATUS", Integer.toString(sceneNumber), NoLevel, "Scene" + Integer.toString(sceneNumber) + "Read");
                 addDeviceSensor(aDevice, jsonOutput, SensorType.SWITCH, dc, "Scene" + Integer.toString(sceneNumber) + "Selected");
               }
               
               dc = addDeviceCommand(aDevice, jsonOutput, "SCENE_STATUS", NoScene, NoLevel, "SceneRead");
               Sensor sensor = addDeviceSensor(aDevice, jsonOutput, SensorType.RANGE, dc, "SelectedScene");
               ((RangeSensor)sensor).setMin(0);
               ((RangeSensor)sensor).setMax(8);
             } else if ("Fan".equals(outputType)) {
               addDeviceCommand(aDevice, jsonOutput, "FADE", NoScene, "0", "Off");
               addDeviceCommand(aDevice, jsonOutput, "FADE", NoScene, "25", "Low");
               addDeviceCommand(aDevice, jsonOutput, "FADE", NoScene, "50", "Medium");
               addDeviceCommand(aDevice, jsonOutput, "FADE", NoScene, "75", "MediumHigh");
               addDeviceCommand(aDevice, jsonOutput, "FADE", NoScene, "100", "Full");
             }
           }
         }
       }
       fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(aDevice));
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
     sensor.setName(jsonOutput.get("name").isString().stringValue() + "_" + nameSuffix);
     sensor.setType(sensorType);
     SensorCommandRef sensorCommandRef = new SensorCommandRef();
     sensorCommandRef.setDeviceCommand(readCommand);
     sensorCommandRef.setSensor(sensor);
     sensorCommandRef.setDeviceName(aDevice.getName());
     sensor.setSensorCommandRef(sensorCommandRef);
     aDevice.getSensors().add(sensor);
    return sensor;
  }

  private static DeviceCommand addDeviceCommand(Device aDevice, JSONObject output, String command, String scene, String level, String nameSuffix) {
    DeviceCommand dc = new DeviceCommand();
    Map<String, String> attrMap = new HashMap<String, String>();
    attrMap.put(DeviceCommandWindow.DEVICE_COMMAND_PROTOCOL, "Lutron HomeWorks"); // Display name of protocol needs to be used
    attrMap.put("address", output.get("address").isString().stringValue());
    attrMap.put("command", command);
    if (scene != null) {
      attrMap.put("scene", scene);
    }
    if (level != null) {
      attrMap.put("level", level);
    }
    dc.setProtocol(DeviceCommandBeanModelProxy.careateProtocol(attrMap, dc));
    dc.setName(output.get("name").isString().stringValue() + nameSuffix);
    aDevice.getDeviceCommands().add(dc);
    return dc;
  }

}
