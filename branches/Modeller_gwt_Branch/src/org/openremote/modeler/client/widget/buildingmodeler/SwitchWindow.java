package org.openremote.modeler.client.widget.buildingmodeler;

import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.FormResetListener;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.model.ComboBoxDataModel;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.proxy.SwitchBeanModelProxy;
import org.openremote.modeler.client.utils.DeviceCommandSelectWindow;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorRef;
import org.openremote.modeler.domain.Switch;
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

public class SwitchWindow extends FormWindow {
   public static String SWITCH_NAME_FIELD_NAME = "name";
   public static String SWITCH_SENSOR_FIELD_NAME = "sensor";
   public static String SWITCH_ON_COMMAND_FIELD_NAME="command(on)";
   public static String SWITCH_OFF_COMMAND_FIELD_NAME="command(off)";
   
   private Switch switchToggle = null;
   
   private TextField<String> nameField = new TextField<String>();
   private ComboBox<ModelData> sensorField = new ComboBox<ModelData>();
   private Button switchOnBtn = new Button("select");
   private Button switchOffBtn = new Button("select");
   
   private boolean edit = false;
   
   public SwitchWindow(Switch slider){
      super();
      if (null != slider){
         this.switchToggle = slider;
         edit = true;
      } else {
         this.switchToggle = new Switch();
         edit = false;
      }
      this.setHeading(edit?"Edit Switch":"New Switch");
      this.setSize(320, 240);
      
      createField();
   }
   
   private void createField(){
      setWidth(380);
      setAutoHeight(true);
      setLayout(new FlowLayout());
      
      form.setWidth(370);
      
      nameField.setFieldLabel(SWITCH_NAME_FIELD_NAME);
      nameField.setName(SWITCH_NAME_FIELD_NAME);
      
      sensorField.setFieldLabel(SWITCH_SENSOR_FIELD_NAME);
      sensorField.setName(SWITCH_SENSOR_FIELD_NAME);
      
      ListStore<ModelData> sensorStore = new ListStore<ModelData>();
      List<BeanModel> sensors = BeanModelDataBase.sensorTable.loadAll();
      for(BeanModel sensorBean : sensors){
         Sensor sensor = sensorBean.getBean();
         SensorRef sensorRef = new SensorRef(sensor);
         ComboBoxDataModel<SensorRef> sensorRefSelector = new ComboBoxDataModel<SensorRef>(sensorRef.getSensor().getName(),sensorRef);
         sensorStore.add(sensorRefSelector);
      }
      sensorField.setStore(sensorStore);
      
      
      AdapterField switchOnAdapter = new AdapterField(switchOnBtn);
      switchOnAdapter.setFieldLabel(SWITCH_ON_COMMAND_FIELD_NAME);
      AdapterField switchOffAdapter = new AdapterField(switchOffBtn);
      switchOffAdapter.setFieldLabel(SWITCH_OFF_COMMAND_FIELD_NAME);
      
      Button submitBtn = new Button("Submit");
      Button resetButton = new Button("Reset");
//      resetButton.addSelectionListener(new FormCaccleListener(form));
      
      
      form.add(nameField);
      form.add(sensorField);
      form.add(switchOnAdapter);
      form.add(switchOffAdapter);
      
      form.add(submitBtn);
      form.add(resetButton);
      
      submitBtn.addSelectionListener(new FormSubmitListener(form));
      resetButton.addSelectionListener(new FormResetListener(form));
      
      switchOnBtn.addSelectionListener(new CommandSelectListener(true));
      switchOffBtn.addSelectionListener(new CommandSelectListener(false));
      
      form.addListener(Events.BeforeSubmit, new SwitchSubmitListener());
//      addListener(Events.BeforeSubmit, new SubmitListener());
      add(form);
//      form.layout();
   }
   
   class SwitchSubmitListener implements Listener<FormEvent>{

      @Override
      public void handleEvent(FormEvent be) {
         List<Field<?>> fields = form.getFields();
         for (Field<?> field : fields) {
            if (SWITCH_NAME_FIELD_NAME.equals(field.getName())) {
               switchToggle.setName(field.getValue().toString());
            } else if (SWITCH_SENSOR_FIELD_NAME.equals(field.getName())) {
               switchToggle.setSensorRef((SensorRef) field.getValue());
            }
         }
         if(!edit){
            SwitchBeanModelProxy.save(switchToggle.getBeanModel());
         } else {
            SwitchBeanModelProxy.update(switchToggle.getBeanModel());
         }
         fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(switchToggle.getBeanModel()));
      }
   }
   
   
   class CommandSelectListener extends SelectionListener<ButtonEvent> {
      private boolean forSwitchOn = true;
      public CommandSelectListener(boolean forSwitchOn){
         this.forSwitchOn = forSwitchOn;
      }
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
               if (forSwitchOn) {
                  switchToggle.setOnDeviceCommandRef((DeviceCommandRef)uiCommand);
               } else  {
                  switchToggle.setOffDeviceCommandRef((DeviceCommandRef)uiCommand);
               }
            }
         });
      }
   }
}
