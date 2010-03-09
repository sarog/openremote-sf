/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.modeler.client.widget.propertyform;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.model.ComboBoxDataModel;
import org.openremote.modeler.client.utils.SensorLink;
import org.openremote.modeler.client.widget.ImageUploadField;
import org.openremote.modeler.client.widget.SimpleComboBox;
import org.openremote.modeler.client.widget.component.ScreenImage;
import org.openremote.modeler.client.widget.uidesigner.SelectSensorWindow;
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.State;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.UIImage;
import org.openremote.modeler.domain.component.UILabel;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

/**
 * A panel for display screen Image properties.
 */
public class ImagePropertyForm extends PropertyForm {
   private ScreenImage screenImage = null;
   private transient Operation operation=Operation.UPLOAD_IMAGE;
   private FieldSet statesPanel; 
   
   private State customSensorState = null;
   
   public ImagePropertyForm(ScreenImage screenImage) {
      super();
      this.screenImage = screenImage;
      addFields(screenImage);
      addListenersToForm();
      createSensorStates();
   }
   private void addFields(final ScreenImage screenImage) {
      final UIImage uiImage = screenImage.getUiImage();
      
      final Button sensorSelectBtn = new Button("Select");
      if(screenImage.getUiImage().getSensor()!=null){
         sensorSelectBtn.setText(screenImage.getUiImage().getSensor().getDisplayName());
      }
      sensorSelectBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            SelectSensorWindow selectSensorWindow = new SelectSensorWindow();
            selectSensorWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  BeanModel dataModel = be.<BeanModel> getData();
                  Sensor sensor = dataModel.getBean();
                  uiImage.setSensor(sensor);
                  sensorSelectBtn.setText(sensor.getDisplayName());

                  createSensorStates();
               }
            });
         }
      });
      
     
      /*final Button selectLabel = new Button("Select");
      selectLabel.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            SelectLabelWindow selectSensorWindow = new SelectLabelWindow(screenImage.getScreenCanvas());
            selectSensorWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  BeanModel dataModel = be.<BeanModel> getData();
                  UILabel label = dataModel.getBean();
                  uiImage.setLabel(label);
                  selectLabel.setText(label.getDisplayName());
               }
            });
         }
      });*/
      
      ComboBox<ModelData> labelBox = createLabelSelector();
      
      add(createImageUploader());
      AdapterField sensorAdapter = new AdapterField(sensorSelectBtn);
      sensorAdapter.setFieldLabel("Sensor");
      add(sensorAdapter);
      
      add(labelBox);
//      AdapterField labelAdapter = new AdapterField(selectLabel);
//      labelAdapter.setFieldLabel("Label");
//      add(labelAdapter);
      
      statesPanel = new FieldSet();
      FormLayout layout = new FormLayout();
      layout.setLabelWidth(80);
      layout.setDefaultWidth(80);
      statesPanel.setLayout(layout);
      statesPanel.setHeading("Sensor State");
      add(statesPanel);
   }
   @SuppressWarnings("unchecked")
   private ComboBox<ModelData> createLabelSelector() {
      ComboBox<ModelData> labelBox = new SimpleComboBox();
      labelBox.setFieldLabel("Label");
      Collection<UILabel> labelsonScreen = (Collection<UILabel>) screenImage.getScreenCanvas().getScreen().getAllUIComponentByType(UILabel.class);
      ListStore<ModelData> labelStore = new ListStore<ModelData>();
      for (UILabel label : labelsonScreen) {
         if (!label.isRemoved()) {
            ComboBoxDataModel<UILabel> labelModel = new ComboBoxDataModel<UILabel>(label.getDisplayName(), label);
            labelStore.add(labelModel);
         }
      }
      //set the label for the image. 
      if(screenImage.getUiImage().getLabel()!=null && !screenImage.getUiImage().getLabel().isRemoved()){
         labelBox.setValue(new ComboBoxDataModel<UILabel>(screenImage.getUiImage().getLabel().getDisplayName(),screenImage.getUiImage().getLabel()));
      }
      labelBox.setStore(labelStore);
      labelBox.addSelectionChangedListener(new SelectionChangedListener<ModelData>(){

         @Override
         public void selectionChanged(SelectionChangedEvent<ModelData> se) {
            ComboBoxDataModel<ModelData> labelData = (ComboBoxDataModel<ModelData>) se.getSelectedItem();
            UILabel label = (UILabel) labelData.getData();
            screenImage.getUiImage().setLabel(label);
         }
         
      });
      return labelBox;
   }
   
   private ImageUploadField createImageUploader() {
      ImageUploadField imageSrc = new ImageUploadField(null) {
         @Override
         protected void onChange(ComponentEvent ce) {
            super.onChange(ce);
            if (!isValid()) {
               return;
            }
            operation = Operation.UPLOAD_IMAGE;
            this.setActionToForm(ImagePropertyForm.this);
            submit();
            screenImage.getScreenCanvas().mask("Uploading image...");
         }
      };
      imageSrc.setValue(screenImage.getUiImage().getImageSource().getImageFileName());
      imageSrc.setFieldLabel("Image");
      return imageSrc;
   }
   
   private void addListenersToForm() {
      addListener(Events.Submit, new Listener<FormEvent>() {
         @Override
         public void handleEvent(FormEvent be) {
            String imageURL = be.getResultHtml();
            SensorLink sensorLink = screenImage.getUiImage().getSensorLinker();
            Map<String,String> sensorAttrMap = new HashMap<String,String>();
            
            if (!"".equals(imageURL)) {
               if(operation == Operation.UPLOAD_IMAGE){
                  screenImage.setImageSource(new ImageSource(imageURL));
               }else {
                  switch (operation){
                  case UPLOAD_SWITCH_ON_IMAGE:
                     sensorAttrMap.put("name","on");
                     break;
                  case UPLOAD_SWITCH_OFF_IMAGE:
                     sensorAttrMap.put("name","off");
                     break;
                  default:
                     sensorAttrMap.put("name",customSensorState.getName());
                  }
                  sensorAttrMap.put("value", imageURL.substring(imageURL.lastIndexOf("/")+1));
                  sensorLink.addOrUpdateChildForSensorLinker("state", sensorAttrMap);
               }
            }
            screenImage.getScreenCanvas().unmask();
            BeanModel screenBeanModel = null;
            fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(screenBeanModel));
         }
      });
   }
   private void createSensorStates(){
      statesPanel.removeAll();
      SensorLink sensorLink = screenImage.getUiImage().getSensorLinker();
      if(screenImage.getUiImage().getSensor()!=null && screenImage.getUiImage().getSensor().getType()==SensorType.SWITCH){
         ImageUploadField onImageUpload = new ImageUploadField("switchOnImage") {
            @Override
            protected void onChange(ComponentEvent ce) {
               super.onChange(ce);
               if (!isValid()) {
                  return;
               }
               operation = Operation.UPLOAD_SWITCH_ON_IMAGE;
               setActionToForm(ImagePropertyForm.this);
               submit();
               screenImage.getScreenCanvas().mask("Uploading image...");
            }
         };
         
         onImageUpload.setFieldLabel("on:");
         
         ImageUploadField offImageUpload = new ImageUploadField("switchOffImage") {
            @Override
            protected void onChange(ComponentEvent ce) {
               super.onChange(ce);
               if (!isValid()) {
                  return;
               }
               operation = Operation.UPLOAD_SWITCH_OFF_IMAGE;
               this.setActionToForm(ImagePropertyForm.this);
               submit();
               screenImage.getScreenCanvas().mask("Uploading image...");
            }
         };
         offImageUpload.setFieldLabel("off:");
         if(sensorLink!=null){
            onImageUpload.setValue(sensorLink.getStateValueByStateName("on"));
            offImageUpload.setValue(sensorLink.getStateValueByStateName("off"));
         }
         statesPanel.add(onImageUpload);
         statesPanel.add(offImageUpload);
      }else if(screenImage.getUiImage().getSensor()!=null && screenImage.getUiImage().getSensor().getType() == SensorType.CUSTOM){
         CustomSensor customSensor = (CustomSensor) screenImage.getUiImage().getSensor();
         List<State> states = customSensor.getStates();
         for(final State state: states){
            ImageUploadField imageUploader = new ImageUploadField(state.getName()) {
               @Override
               protected void onChange(ComponentEvent ce) {
                  super.onChange(ce);
                  if (!isValid()) {
                     return;
                  }
                  operation = Operation.OTHER;
                  customSensorState = state;
                  setActionToForm(ImagePropertyForm.this);
                  submit();
                  screenImage.getScreenCanvas().mask("Uploading image...");
               }
            };
//            imageUploader.setName(state.getName());
            imageUploader.setFieldLabel(state.getName());
            if(sensorLink!=null){
               imageUploader.setValue(sensorLink.getStateValueByStateName(state.getName()));
            }
            statesPanel.add(imageUploader);
            
         }
      }
      statesPanel.layout(true);
   }
   
   static enum Operation{
      UPLOAD_IMAGE,UPLOAD_SWITCH_ON_IMAGE,UPLOAD_SWITCH_OFF_IMAGE,OTHER;
   }
   
   /*private void setImageUploadAction(String ImageFieldName){
      String action = GWT.getModuleBaseURL() + "fileUploadController.htm?method=uploadImage&uploadFieldName="
            + (ImageFieldName == null ? ImageUploadField.IMAGEUPLOADFIELD : ImageFieldName);
      setAction(action);
      setEncoding(Encoding.MULTIPART);
      setMethod(Method.POST);
   }*/
}
