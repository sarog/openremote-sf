/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.ComboBoxExt;
import org.openremote.modeler.client.widget.component.ImageSelectAdapterField;
import org.openremote.modeler.client.widget.component.ScreenImage;
import org.openremote.modeler.client.widget.uidesigner.ImageAssetPicker;
import org.openremote.modeler.client.widget.uidesigner.ImageAssetPicker.ImageAssetPickerListener;
import org.openremote.modeler.client.widget.uidesigner.PropertyPanel;
import org.openremote.modeler.client.widget.uidesigner.SelectSensorWindow;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.UIImage;
import org.openremote.modeler.domain.component.UILabel;
import org.openremote.modeler.shared.dto.SensorWithInfoDTO;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
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

   private UIImage uiImage;
   
   private FieldSet statesPanel; 
   
   public ImagePropertyForm(ScreenImage screenImage, UIImage uiImage, WidgetSelectionUtil widgetSelectionUtil) {
      super(screenImage, widgetSelectionUtil);
      this.screenImage = screenImage;
      this.uiImage = uiImage;
      addFields();
      createSensorStates();
      super.addDeleteButton();
   }
   
   private void addFields() {
      this.setLabelWidth(70);
      
      final Button sensorSelectBtn = new Button("Select");
      if (uiImage.getSensorDTO() != null){
         sensorSelectBtn.setText(uiImage.getSensorDTO().getDisplayName());
      }
      sensorSelectBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            SelectSensorWindow selectSensorWindow = new SelectSensorWindow();
            selectSensorWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  BeanModel dataModel = be.<BeanModel> getData();
                  SensorWithInfoDTO sensorDTO = dataModel.getBean();
                  uiImage.setSensorDTOAndInitSensorLink(sensorDTO);
                  sensorSelectBtn.setText(sensorDTO.getDisplayName());
                  if (sensorDTO.getType() == SensorType.SWITCH || sensorDTO.getType()==SensorType.CUSTOM) {
                     statesPanel.show();
                     createSensorStates();
                  } else {
                     statesPanel.hide();
                  }
               }
            });
         }
      });
     
      ComboBox<ModelData> labelBox = createLabelSelector();
      
      add(createImageUploader());
      AdapterField sensorAdapter = new AdapterField(sensorSelectBtn);
      sensorAdapter.setFieldLabel("Sensor");
      add(sensorAdapter);
      
      add(labelBox);
      
      statesPanel = new FieldSet();
      FormLayout layout = new FormLayout();
      layout.setLabelWidth(65);
      layout.setDefaultWidth(150);
      statesPanel.setLayout(layout);
      statesPanel.setHeading("Sensor State");
      add(statesPanel);
      SensorWithInfoDTO sensor = uiImage.getSensorDTO();
      if (sensor == null) {
         statesPanel.hide();
      } else if (sensor.getType() != SensorType.SWITCH && sensor.getType() != SensorType.CUSTOM) {
         statesPanel.hide();
      }
   }

   @SuppressWarnings("unchecked")
   private ComboBox<ModelData> createLabelSelector() {
      ComboBox<ModelData> labelBox = new ComboBoxExt();
      labelBox.setFieldLabel("FallbackLabel");
      Collection<UILabel> labelsonScreen = (Collection<UILabel>) screenImage.getScreenCanvas().getScreen().getAllUIComponentByType(UILabel.class);
      ListStore<ModelData> labelStore = new ListStore<ModelData>();
      for (UILabel label : labelsonScreen) {
         if (!label.isRemoved()) {
            ComboBoxDataModel<UILabel> labelModel = new ComboBoxDataModel<UILabel>(label.getDisplayName(), label);
            labelStore.add(labelModel);
         }
      }
      //set the label for the image. 
      if (uiImage.getLabel() != null && !uiImage.getLabel().isRemoved()) {
         labelBox.setValue(new ComboBoxDataModel<UILabel>(uiImage.getLabel().getDisplayName(), uiImage.getLabel()));
      }
      labelBox.setStore(labelStore);
      labelBox.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
         @Override
         public void selectionChanged(SelectionChangedEvent<ModelData> se) {
            ComboBoxDataModel<ModelData> labelData = (ComboBoxDataModel<ModelData>)se.getSelectedItem();
            UILabel label = (UILabel)labelData.getData();
            uiImage.setLabel(label);
         }
         
      });
      return labelBox;
   }
   
   private ImageSelectAdapterField createImageUploader() {
     final ImageSelectAdapterField imageSrcField = new ImageSelectAdapterField("Image");
     imageSrcField.setText(uiImage.getImageSource().getImageFileName());
     imageSrcField.addSelectionListener(new SelectionListener<ButtonEvent>() {
        @Override
        public void componentSelected(ButtonEvent ce) {
          final ImageSource image = uiImage.getImageSource();
          
          ImageAssetPicker imageAssetPicker = new ImageAssetPicker((image != null)?image.getSrc():null);
          imageAssetPicker.show();
          imageAssetPicker.center();
          imageAssetPicker.setListener(new ImageAssetPickerListener() {
           @Override
           public void imagePicked(String imageURL) {
             uiImage.setImageSource(new ImageSource(imageURL));
             imageSrcField.setText(uiImage.getImageSource().getImageFileName());
           }             
          });
        }
     });
     imageSrcField.addDeleteListener(new SelectionListener<ButtonEvent>() {
       public void componentSelected(ButtonEvent ce) {
          if (!UIImage.DEFAULT_IMAGE_URL.equals(uiImage.getImageSource().getSrc())) {
            uiImage.setImageSource(new ImageSource(UIImage.DEFAULT_IMAGE_URL));
             imageSrcField.setText(uiImage.getImageSource().getImageFileName());
          }
       }
    });
     return imageSrcField;
   }
   
   private ImageSelectAdapterField createImageSelectFieldForState(final SensorLink sensorLink, final String stateName) {
     final ImageSelectAdapterField imageUploadField = new ImageSelectAdapterField(stateName);
     imageUploadField.addSelectionListener(new SelectionListener<ButtonEvent>() {
        @Override
        public void componentSelected(ButtonEvent ce) {
          ImageAssetPicker imageAssetPicker = new ImageAssetPicker((sensorLink != null)?sensorLink.getStateValueByStateName(stateName):null);
          imageAssetPicker.show();
          imageAssetPicker.center();
          imageAssetPicker.setListener(new ImageAssetPickerListener() {
           @Override
           public void imagePicked(String imageURL) {
             Map<String,String> sensorAttrMap = new HashMap<String,String>();
             sensorAttrMap.put("name", stateName);
             sensorAttrMap.put("value", imageURL.substring(imageURL.lastIndexOf("/") + 1));
             SensorLink sensorLink = uiImage.getSensorLink();
             sensorLink.addOrUpdateChildForSensorLinker("state", sensorAttrMap);
             imageUploadField.setText(sensorLink.getStateValueByStateName(stateName));
           }             
          });
        }
     });
     imageUploadField.addDeleteListener(new SelectionListener<ButtonEvent>() {
       public void componentSelected(ButtonEvent ce) {
         removeSensorImage(stateName);
       }
    });
     if (sensorLink != null) {
       imageUploadField.setText(sensorLink.getStateValueByStateName(stateName));
     }
     return imageUploadField;
   }
   
   private void createSensorStates() {
      statesPanel.removeAll();
      final SensorLink sensorLink = uiImage.getSensorLink();
      if (uiImage.getSensorDTO() != null && uiImage.getSensorDTO().getType() == SensorType.SWITCH) {
        statesPanel.add(createImageSelectFieldForState(sensorLink, "on"));
        statesPanel.add(createImageSelectFieldForState(sensorLink, "off"));
      } else if (uiImage.getSensorDTO() != null && uiImage.getSensorDTO().getType() == SensorType.CUSTOM) {
        SensorWithInfoDTO customSensor = uiImage.getSensorDTO();
        List<String> stateNames = customSensor.getStateNames();
        for (final String stateName: stateNames) {
          statesPanel.add(createImageSelectFieldForState(sensorLink, stateName));
        }
      }
      statesPanel.layout(true);
   }
   
   @Override
   protected void afterRender() {
      super.afterRender();
      ((PropertyPanel)this.getParent()).setHeading("Image properties");
   }
   
   private void removeSensorImage(String stateName) {
      String sensorValue = uiImage.getSensorLink().getStateValueByStateName(stateName);
      if (!"".equals(sensorValue)) {
         Map<String,String> sensorAttrMap = new HashMap<String, String>();
         sensorAttrMap.put("name", stateName);
         sensorAttrMap.put("value", sensorValue);
         uiImage.getSensorLink().removeChildForSensorLinker("state", sensorAttrMap);
         createSensorStates();
      }
   }
}