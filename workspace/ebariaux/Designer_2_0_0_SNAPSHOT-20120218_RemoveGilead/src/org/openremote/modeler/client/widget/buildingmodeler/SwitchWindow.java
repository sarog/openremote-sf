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
import org.openremote.modeler.client.proxy.SwitchBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.utils.DeviceCommandSelectWindow;
import org.openremote.modeler.client.widget.ComboBoxExt;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.SensorDTO;
import org.openremote.modeler.shared.dto.SwitchDetailsDTO;

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
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.event.shared.EventBus;

/**
 * The window to creates or updates a switch in a device.
 */
public class SwitchWindow extends FormWindow {
   public static final String SWITCH_NAME_FIELD_NAME = "name";
   public static final String SWITCH_SENSOR_FIELD_NAME = "sensor";
   public static final String SWITCH_ON_COMMAND_FIELD_NAME="command(on)";
   public static final String SWITCH_OFF_COMMAND_FIELD_NAME="command(off)";
   
   private EventBus eventBus;
   private long deviceId;
   private SwitchDetailsDTO switchDTO;

   private TextField<String> nameField = new TextField<String>();
   protected ComboBox<ModelData> sensorField = new ComboBoxExt();
   protected Button switchOnBtn = new Button("select");
   protected Button switchOffBtn = new Button("select");
   
   private boolean edit = false;
   
   /**
    * Instantiates a window to edit the switch.
    * 
    * @param switchToggle the switch toggle
    */
   public SwitchWindow(BeanModel switchModel, long deviceId, EventBus eventBus) {
     super();
     this.deviceId = deviceId;
     this.eventBus = eventBus;
     this.setHeading("Edit Switch");
     edit = true;
     this.setSize(320, 240);

     SwitchBeanModelProxy.loadSwitchDetails(switchModel, new AsyncSuccessCallback<BeanModel>() {
       public void onSuccess(BeanModel result) {
         SwitchWindow.this.switchDTO = result.getBean();
         createField();
         setHeight(300); // Somehow setting the height here is required for the autoheight calculation to work when layout is called 
         layout();
       }
     });
   }
   
   /**
    * Instantiates a window to create or edit a switch.
    */
   public SwitchWindow(long deviceId, EventBus eventBus) {
     super();
     this.deviceId = deviceId;
     this.eventBus = eventBus;
     switchDTO = new SwitchDetailsDTO();
     edit = false;
     this.setHeading("New Switch");
     this.setSize(320, 240);
     createField();
     setHeight(300); // Somehow setting the height here is required for the autoheight calculation to work when layout is called 
     layout();
   }

   /**
    * Creates the switch's fields, which includes name, sensor, on command and off command.
    */
   private void createField() {
      setWidth(380);
      setAutoHeight(true);
      setLayout(new FlowLayout());
      
      form.setWidth(370);
      
      nameField.setFieldLabel(SWITCH_NAME_FIELD_NAME);
      nameField.setName(SWITCH_NAME_FIELD_NAME);
      nameField.setAllowBlank(false);
      
      sensorField.setFieldLabel(SWITCH_SENSOR_FIELD_NAME);
      sensorField.setName(SWITCH_SENSOR_FIELD_NAME);
      
      
      final ListStore<ModelData> sensorStore = new ListStore<ModelData>();
      SensorBeanModelProxy.loadSensorDTOsByDeviceId(deviceId, new AsyncSuccessCallback<ArrayList<SensorDTO>>() {
        @Override
        public void onSuccess(ArrayList<SensorDTO> result) {
          for (SensorDTO s : result) {
            ComboBoxDataModel<SensorDTO> dm = new ComboBoxDataModel<SensorDTO>(s.getDisplayName(), s);
            sensorStore.add(dm);
            if (edit && s.getOid() == switchDTO.getSensorId()) {
              sensorField.setValue(dm);
            }
          }
        }
      });
      sensorField.setStore(sensorStore);
      sensorField.addSelectionChangedListener(new SensorSelectChangeListener());
      
      if (edit) {
         nameField.setValue(switchDTO.getName());
         switchOnBtn.setText(switchDTO.getOnCommandDisplayName());
         switchOffBtn.setText(switchDTO.getOffCommandDisplayName());
      }
      
      AdapterField switchOnAdapter = new AdapterField(switchOnBtn);
      switchOnAdapter.setFieldLabel(SWITCH_ON_COMMAND_FIELD_NAME);
      AdapterField switchOffAdapter = new AdapterField(switchOffBtn);
      switchOffAdapter.setFieldLabel(SWITCH_OFF_COMMAND_FIELD_NAME);
      
      Button submitBtn = new Button("Submit");
      Button resetButton = new Button("Reset");
      
      form.add(nameField);
      form.add(sensorField);
      form.add(switchOnAdapter);
      form.add(switchOffAdapter);
      
      form.addButton(submitBtn);
      form.addButton(resetButton);
      
      submitBtn.addSelectionListener(new FormSubmitListener(form, submitBtn));
      resetButton.addSelectionListener(new FormResetListener(form));
      
      switchOnBtn.addSelectionListener(new CommandSelectListener(true));
      switchOffBtn.addSelectionListener(new CommandSelectListener(false));
      
      form.addListener(Events.BeforeSubmit, new SwitchSubmitListener());
      add(form);
   }
   
   /**
    * The listener to submit the window, save the switch data into device and server.
    */
   class SwitchSubmitListener implements Listener<FormEvent> {

      @Override
      public void handleEvent(FormEvent be) {
        
        // TODO EBR : review this validation, this does prevent re-submitting the form
        // there must be a specific way to handle validation, not doing it in submit        
        if (switchDTO.getOnCommandId() == null || switchDTO.getOffCommandId() == null) {
          MessageBox.alert("Switch", "A switch must have on and off commands defined to toggle its state", null);
          return;
        }
        if (switchDTO.getSensorId() == null) {
          MessageBox.alert("Switch", "A switch must have a sensor defined to define its state", null);
          return;
        }
         
         List<Field<?>> fields = form.getFields();
         for (Field<?> field : fields) {
            if (SWITCH_NAME_FIELD_NAME.equals(field.getName())) {
               switchDTO.setName(field.getValue().toString());
               break;
            }
         }
         if (!edit) {
            SwitchBeanModelProxy.saveNewSwitch(switchDTO, deviceId, new AsyncSuccessCallback<Void>() {
               @Override
               public void onSuccess(Void result) {
                 eventBus.fireEvent(new DeviceUpdatedEvent(null)); // TODO : pass correct data
                 hide();
               };

            });
         } else {
            SwitchBeanModelProxy.updateSwitchWithDTO(switchDTO, new AsyncSuccessCallback<Void>() {
               @Override
               public void onSuccess(Void result) {
                 eventBus.fireEvent(new DeviceUpdatedEvent(null)); // TODO : pass correct data
                 hide();
              };
            });
         }
      }
   }
   
   /**
    * The listener to select on or off command for the switch.
    * 
    */
   class CommandSelectListener extends SelectionListener<ButtonEvent> {
      private boolean forSwitchOn = true;
      
      /**
       * Instantiates a new command select listener by the boolean.
       * If true, select a on command, else select a off command.
       * 
       * @param forSwitchOn the for switch on
       */
      public CommandSelectListener(boolean forSwitchOn) {
         this.forSwitchOn = forSwitchOn;
      }
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
               if (forSwitchOn) {
                 switchDTO.setOnCommandId(dc.getOid());
                 switchDTO.setOnCommandDisplayName(dc.getDisplayName());
               } else {
                 switchDTO.setOffCommandId(dc.getOid());
                 switchDTO.setOffCommandDisplayName(dc.getDisplayName());
               }
            }
         });
      }
   }
   
   /**
    * The listener to select a sensor for the switch.
    */
   class SensorSelectChangeListener extends SelectionChangedListener<ModelData> {

      @SuppressWarnings("unchecked")
      @Override
      public void selectionChanged(SelectionChangedEvent<ModelData> se) {
        ComboBoxDataModel<SensorDTO> sensorItem = (ComboBoxDataModel<SensorDTO>) se.getSelectedItem();
        switchDTO.setSensorId(sensorItem.getData().getOid());
      }
   }
}
