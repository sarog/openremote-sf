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

  public LutronImportWizard(final Device device) {    
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
        action.setDevice(device); // TODO : double check this is the device we want or how to access the member's variable
        
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
        
        /*

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
        */
        
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
  
}
