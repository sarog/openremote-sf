/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.event.DeviceUpdatedEvent;
import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.FormResetListener;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.model.ComboBoxDataModel;
import org.openremote.modeler.client.proxy.SensorBeanModelProxy;
import org.openremote.modeler.client.proxy.SliderBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.utils.DeviceCommandSelectWindow;
import org.openremote.modeler.client.widget.ComboBoxExt;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.SensorDTO;
import org.openremote.modeler.shared.dto.SliderDetailsDTO;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.event.shared.EventBus;
/**
 * The window to creates or update a slider and save it into server.
 * 
 * @author Javen
 *
 */
public class SliderWindow extends FormWindow {
   public static final String SLIDER_NAME_FIELD_NAME = "name";
   public static final String SLIDER_SENSOR_FIELD_NAME = "sensor";
   public static final String SLIDER_SETVALUE_COMMMAND_FIELD_NAME="setValue";
   
   private EventBus eventBus;
   
   private long deviceId;
   private SliderDetailsDTO sliderDTO;
   
   private TextField<String> nameField = new TextField<String>();
   protected ComboBox<ModelData> sensorField = new ComboBoxExt();
   protected Button setValueBtn = new Button("select");
   
   private boolean edit = false;
   
   /**
    * Instantiates a window to edit a slider.
    */
   public SliderWindow(BeanModel sliderModel, long deviceId, EventBus eventBus) {
      super();
      this.eventBus = eventBus;
      this.deviceId = deviceId;
      this.setHeading("Edit Slider");
      edit = true;
      this.setSize(320, 240);

      SliderBeanModelProxy.loadSliderDetails(sliderModel, new AsyncSuccessCallback<BeanModel>() {
        public void onSuccess(BeanModel result) {
          SliderWindow.this.sliderDTO = result.getBean();
          createField();
          setHeight(300); // Somehow setting the height her is required for the autoheight calculation to work when layout is called 
          layout();
        }
      });
   }
   
   /**
    * Instantiates a window to create a new slider.
    */
   public SliderWindow(long deviceId, EventBus eventBus) {
      super();
      this.eventBus = eventBus;
      this.deviceId = deviceId;
      sliderDTO = new SliderDetailsDTO();
      this.setHeading("New Slider");
      edit = false;
      this.setSize(320, 240);      
      createField();
   }
   
   /**
    * Creates the slider property fields, includes name, sensor and setValue command.
    */
   private void createField() {
      setWidth(380);
      setAutoHeight(true);
      setLayout(new FlowLayout());
      
      form.setWidth(370);
      
      nameField.setFieldLabel(SLIDER_NAME_FIELD_NAME);
      nameField.setName(SLIDER_NAME_FIELD_NAME);
      nameField.setAllowBlank(false);
      
      sensorField.setFieldLabel(SLIDER_SENSOR_FIELD_NAME);
      sensorField.setName(SLIDER_SENSOR_FIELD_NAME);
      
      final ListStore<ModelData> sensorStore = new ListStore<ModelData>();
      SensorBeanModelProxy.loadSensorDTOsByDeviceId(deviceId, new AsyncSuccessCallback<ArrayList<SensorDTO>>() {
        @Override
        public void onSuccess(ArrayList<SensorDTO> result) {
          for (SensorDTO s : result) {
            ComboBoxDataModel<SensorDTO> dm = new ComboBoxDataModel<SensorDTO>(s.getDisplayName(), s);
            sensorStore.add(dm);
            if (edit && s.getOid() == sliderDTO.getSensorId()) {
              sensorField.setValue(dm);
            }
          }
        }
      });
      sensorField.setStore(sensorStore);
      sensorField.addSelectionChangedListener(new SensorSelectChangeListener());

      if (edit) {
         nameField.setValue(sliderDTO.getName());
         setValueBtn.setText(sliderDTO.getCommandName());
      }

      AdapterField switchOnAdapter = new AdapterField(setValueBtn);
      switchOnAdapter.setFieldLabel(SLIDER_SETVALUE_COMMMAND_FIELD_NAME);
      Button submitBtn = new Button("Submit");
      Button resetButton = new Button("Reset");
      
      submitBtn.addSelectionListener(new FormSubmitListener(form, submitBtn));
      resetButton.addSelectionListener(new FormResetListener(form));
      
      form.add(nameField);
      form.add(sensorField);
      form.add(switchOnAdapter);
      
      form.addButton(submitBtn);
      form.addButton(resetButton);
      
      setValueBtn.addSelectionListener(new CommandSelectListener());
      
      form.addListener(Events.BeforeSubmit, new SliderSubmitListener());      
      add(form);
   }
   
   
   /**
    * The listener to add the slider into current device and server.
    */
   class SliderSubmitListener implements Listener<FormEvent> {

      @Override
      public void handleEvent(FormEvent be) {
        
        // TODO EBR : review this validation, this does prevent re-submitting the form
        // there must be a specific way to handle validation, not doing it in submit        
        if (sliderDTO.getCommandId() == null) {
          MessageBox.alert("Slider", "A slider must have a command defined to set its value", null);
          return;
        }
        if (sliderDTO.getSensorId() == null) {
          MessageBox.alert("Slider", "A slider must have a sensor defined to read its value", null);
          return;
        }
        
         List<Field<?>> fields = form.getFields();
         for (Field<?> field : fields) {
            if (SLIDER_NAME_FIELD_NAME.equals(field.getName())) {
               sliderDTO.setName(field.getValue().toString());
               break;
            }
         }
         if (!edit) {
            SliderBeanModelProxy.saveNewSlider(sliderDTO, deviceId, new AsyncSuccessCallback<Void>(){
               @Override
               public void onSuccess(Void result) {
                 eventBus.fireEvent(new DeviceUpdatedEvent(null)); // TODO : pass correct data
                 hide();
               }
            });
         } else {
            SliderBeanModelProxy.updateSliderWithDTO(sliderDTO, new AsyncSuccessCallback<Void>() {
               @Override
               public void onSuccess(Void result) {
                 eventBus.fireEvent(new DeviceUpdatedEvent(null)); // TODO : pass correct data
                 hide();
               }
            });
         }
      }
   }
   
   
   /**
    * The listener to select a setValue command for the slider.
    */
   class CommandSelectListener extends SelectionListener<ButtonEvent> {
      @Override
      public void componentSelected(ButtonEvent ce) {
         final DeviceCommandSelectWindow selectCommandWindow = new DeviceCommandSelectWindow(deviceId);
         final Button command = ce.getButton();
         selectCommandWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
               BeanModel dataModel = be.<BeanModel> getData();
               DeviceCommandDTO dc = dataModel.getBean();
               command.setText(dc.getDisplayName());
               sliderDTO.setCommandId(dc.getOid());
               sliderDTO.setCommandName(dc.getDisplayName());
            }
         });
      }
   }
   
   /**
    * The listener to set the sensor for the slider when the sensor selection changed. 
    */
   class SensorSelectChangeListener extends SelectionChangedListener<ModelData> {

      @SuppressWarnings("unchecked")
      @Override
      public void selectionChanged(SelectionChangedEvent<ModelData> se) {
          ComboBoxDataModel<SensorDTO> sensorItem = (ComboBoxDataModel<SensorDTO>) se.getSelectedItem();
          sliderDTO.setSensorId(sensorItem.getData().getOid());
      }
   }
}
