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
package org.openremote.modeler.client.widget.buildingmodeler;

import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.FormResetListener;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.model.ComboBoxDataModel;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.proxy.SliderBeanModelProxy;
import org.openremote.modeler.client.utils.DeviceCommandSelectWindow;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorRef;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.UICommand;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
/**
 * 
 * @author Javen
 *
 */
public class SliderWindow extends FormWindow {
   public static String SLIDER_NAME_FIELD_NAME = "name";
   public static String SLIDER_SENSOR_FIELD_NAME = "sensor";
   public static String SLIDER_SETVALUE_COMMMAND_FIELD_NAME="setValue";
   
   private Slider slider = null;
   
   private TextField<String> nameField = new TextField<String>();
   private ComboBox<ModelData> sensorField = new ComboBox<ModelData>();
   private Button setValueBtn = new Button("select");
   
   private boolean edit = false;
   
   public SliderWindow(Slider slider){
      super();
      if (null != slider){
         this.slider = slider;
         edit = true;
      } else {
         this.slider = new Slider();
         edit = false;
      }
      this.setHeading(edit?"Edit Slider":"New Slider");
      this.setSize(320, 240);
      
      createField();
      
   }
   
   private void createField(){
      setWidth(380);
      setAutoHeight(true);
      setLayout(new FlowLayout());
      
      form.setWidth(370);
      
      nameField.setFieldLabel(SLIDER_NAME_FIELD_NAME);
      nameField.setName(SLIDER_NAME_FIELD_NAME);
      
      sensorField.setFieldLabel(SLIDER_SENSOR_FIELD_NAME);
      sensorField.setName(SLIDER_SENSOR_FIELD_NAME);
      
      ListStore<ModelData> sensorStore = new ListStore<ModelData>();
      List<BeanModel> sensors = BeanModelDataBase.sensorTable.loadAll();
      for(BeanModel sensorBean : sensors){
         Sensor sensor = sensorBean.getBean();
         SensorRef sensorRef = new SensorRef(sensor);
         ComboBoxDataModel<SensorRef> sensorRefSelector = new ComboBoxDataModel<SensorRef>(sensorRef.getSensor().getName(),sensorRef);
         sensorStore.add(sensorRefSelector);
      }
      sensorField.setStore(sensorStore);
      
      
      AdapterField switchOnAdapter = new AdapterField(setValueBtn);
      switchOnAdapter.setFieldLabel(SLIDER_SETVALUE_COMMMAND_FIELD_NAME);
      
      Button submitBtn = new Button("Submit");
      Button resetButton = new Button("Reset");
//      resetButton.addSelectionListener(new FormCaccleListener(form));
      
      
      form.add(nameField);
      form.add(sensorField);
      form.add(switchOnAdapter);
      
      form.addButton(submitBtn);
      form.addButton(resetButton);
      
      submitBtn.addSelectionListener(new FormSubmitListener(form));
      resetButton.addSelectionListener(new FormResetListener(form));
      
      setValueBtn.addSelectionListener(new CommandSelectListener());
      
      form.addListener(Events.BeforeSubmit, new SwitchSubmitListener());
      add(form);
   }
   
   
   class SwitchSubmitListener implements Listener<FormEvent>{

      @Override
      public void handleEvent(FormEvent be) {
         List<Field<?>> fields = form.getFields();
         for (Field<?> field : fields) {
            if (SLIDER_NAME_FIELD_NAME.equals(field.getName())) {
               slider.setName(field.getValue().toString());
            } else if (SLIDER_SENSOR_FIELD_NAME.equals(field.getName())) {
               slider.setSensorRef((SensorRef) field.getValue());
            }
         }
         if(!edit){
            SliderBeanModelProxy.save(slider.getBeanModel());
         } else {
            SliderBeanModelProxy.update(slider.getBeanModel());
         }
         fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(slider.getBeanModel()));
      }
   }
   
   
   class CommandSelectListener extends SelectionListener<ButtonEvent> {
      @Override
      public void componentSelected(ButtonEvent ce) {
         final DeviceCommandSelectWindow selectCommandWindow = new DeviceCommandSelectWindow();
         final Button command = ce.getButton();
         selectCommandWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
               BeanModel dataModel = be.<BeanModel> getData();
               UICommand uiCommand = null;
               if (dataModel.getBean() instanceof DeviceCommand) {
                  uiCommand = new DeviceCommandRef((DeviceCommand) dataModel.getBean());
               } else if (dataModel.getBean() instanceof DeviceCommandRef) {
                  uiCommand = (DeviceCommandRef) dataModel.getBean();
               } else {
                  MessageBox.alert("error", "A switch can only have command instead of macor", null);
                  return;
               }
               command.setText(uiCommand.getDisplayName());
               System.out.println(command.getTitle());
              slider.setSetValueCmd((DeviceCommandRef) uiCommand);
            }
         });
      }
   }
}
