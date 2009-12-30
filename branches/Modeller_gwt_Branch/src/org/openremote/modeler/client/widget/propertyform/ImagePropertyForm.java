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

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.model.ComboBoxDataModel;
import org.openremote.modeler.client.widget.ImageUploadField;
import org.openremote.modeler.client.widget.SimpleComboBox;
import org.openremote.modeler.client.widget.component.ScreenImage;
import org.openremote.modeler.client.widget.uidesigner.SelectSensorWindow;
import org.openremote.modeler.domain.Sensor;
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
import com.google.gwt.core.client.GWT;

/**
 * A panel for display screen label properties.
 */
public class ImagePropertyForm extends PropertyForm {
   private ScreenImage screenImage = null;
   public ImagePropertyForm(ScreenImage screenImage) {
      super();
      this.screenImage = screenImage;
      addFields(screenImage);
      addListenersToForm();
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
   }
   @SuppressWarnings("unchecked")
   private ComboBox<ModelData> createLabelSelector() {
      ComboBox<ModelData> labelBox = new SimpleComboBox();
      labelBox.setFieldLabel("Label");
      Collection<UILabel> labelsonScreen = (Collection<UILabel>) screenImage.getScreenCanvas().getScreen().getAllUIComponentByType(UILabel.class);
      ListStore<ModelData> labelStore = new ListStore<ModelData>();
      for(UILabel label : labelsonScreen){
         ComboBoxDataModel<UILabel> labelModel = new ComboBoxDataModel<UILabel>(label.getDisplayName(),label);
         labelStore.add(labelModel);
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
      ImageUploadField imageSrc = new ImageUploadField() {
         @Override
         protected void onChange(ComponentEvent ce) {
            super.onChange(ce);
            if (!isValid()) {
               return;
            }
            submit();
            screenImage.getScreenCanvas().mask("Uploading image...");
         }
      };
      imageSrc.setValue(screenImage.getUiImage().getSrc());
      imageSrc.setFieldLabel("src");
      return imageSrc;
   }
   
   private void addListenersToForm() {
      setAction(GWT.getModuleBaseURL() + "fileUploadController.htm?method=uploadImage&uploadFieldName="
            + ImageUploadField.IMAGEUPLOADFIELD);
      setEncoding(Encoding.MULTIPART);
      setMethod(Method.POST);

      addListener(Events.Submit, new Listener<FormEvent>() {
         @Override
         public void handleEvent(FormEvent be) {
            String backgroundImgURL = be.getResultHtml();
            if (!"".equals(backgroundImgURL)) {
               screenImage.setSrc(backgroundImgURL);
               screenImage.getScreenCanvas().unmask();
            }
            BeanModel screenBeanModel = null;
            fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(screenBeanModel));
         }
      });
   }
}
