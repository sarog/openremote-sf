/**
 * 
 */
package org.openremote.modeler.client.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.model.ComboBoxDataModel;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.ProtocolAttr;
import org.openremote.modeler.protocol.ProtocolAttrDefinition;
import org.openremote.modeler.protocol.ProtocolDefinition;
import org.openremote.modeler.protocol.ProtocolValidator;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

/**
 * @author Tomsky
 *
 */
public class DeviceCommandWindow extends SubmitWindow {
   private FormPanel commandForm = new FormPanel();
   private ComboBox<ModelData> protocol = new ComboBox<ModelData>();
   
   public DeviceCommandWindow(Map<String, ProtocolDefinition> protocols) {
      setHeading("New command");
      initial();
      createFields(protocols);
      add(commandForm);
   }
   public DeviceCommandWindow(DeviceCommand deviceCommand) {
      setHeading("Edit command");
      initial();
      createFields(deviceCommand);
      add(commandForm);
   }
   private void initial(){
      setWidth(360);
      setAutoHeight(true);
      
      commandForm.setFrame(true);
      commandForm.setHeaderVisible(false);
      commandForm.setWidth(350);
      
      commandForm.setButtonAlign(HorizontalAlignment.CENTER);

      Button submitBtn = new Button("Submit");
      Button resetButton = new Button("Reset");

      submitBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            if (commandForm.isValid()) {
               commandForm.submit();
            }
         }

      });

      resetButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            List<Field<?>> list = commandForm.getFields();
            for (Field<?> f : list) {
               f.reset();
            }
         }

      });
      commandForm.addButton(submitBtn);
      commandForm.addButton(resetButton);
      commandForm.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         @SuppressWarnings("unchecked")
         public void handleEvent(FormEvent be) {
            List<Field<?>> list = commandForm.getFields();
            Map<String, String> attrMap = new HashMap<String, String>();
            for (Field<?> f : list) {
               if("protocol".equals(f.getName())){
                  Field<BaseModelData> p = (Field<BaseModelData>) f;
                  attrMap.put("protocol", p.getValue().get(ComboBoxDataModel.getDisplayProperty()).toString());
               }else{
                  attrMap.put(f.getName(), f.getValue().toString());
               }
            }
            AppEvent appEvent = new AppEvent(Events.Submit, attrMap);
            fireSubmitListener(appEvent);
         }

      });
      
   }
   
   private void createFields(Map<String, ProtocolDefinition> protocols){
      TextField<String> nameField = new TextField<String>();
      nameField.setName("name");
      nameField.setFieldLabel("Name");
      
      ListStore<ModelData> store = new ListStore<ModelData>();
      protocol.setStore(store);
      protocol.setFieldLabel("Protocol");
      protocol.setName("protocol");
      
      for (String key : protocols.keySet()) {
         ComboBoxDataModel<ProtocolDefinition> data = new ComboBoxDataModel<ProtocolDefinition>(key,protocols.get(key));
         store.add(data);
      }
      
      protocol.setDisplayField(ComboBoxDataModel.getDisplayProperty());
      protocol.setEmptyText("Please Select Protocol...");
      protocol.setValueField(ComboBoxDataModel.getDisplayProperty());
      
      commandForm.add(nameField);
      commandForm.add(protocol);
      
      protocol.addSelectionChangedListener(new SelectionChangedListener<ModelData>(){
         @SuppressWarnings("unchecked")
         public void selectionChanged(SelectionChangedEvent<ModelData> se) {
            if(commandForm.getItems().size() > 2){
               commandForm.getItem(2).removeFromParent();
            }
            addFields((ComboBoxDataModel<ProtocolDefinition>)se.getSelectedItem());
         }
      });
      
      commandForm.layout();
   }
   
   private void createFields(DeviceCommand deviceCommand){
      TextField<String> nameField = new TextField<String>();
      nameField.setName("name");
      nameField.setFieldLabel("Name");
      nameField.setValue(deviceCommand.getName());
      
      TextField<String> protocolField = new TextField<String>();
      protocolField.setName("proto");
      protocolField.setFieldLabel("Protocol");
      protocolField.setValue(deviceCommand.getProtocol().getType());
      protocolField.disable();
      
      FieldSet attrSet = new FieldSet();
      FormLayout layout = new FormLayout();  
      layout.setLabelWidth(80);  
      attrSet.setLayout(layout);
      attrSet.setHeading(protocolField.getValue()+" attributes");
      
      for (ProtocolAttr attr : deviceCommand.getProtocol().getAttributes()) {
         TextField<String> attrField = new TextField<String>();
         attrField.setName(attr.getName());
         attrField.setFieldLabel(attr.getName());
         attrField.setValue(attr.getValue());
         
         attrSet.add(attrField);
      }
      deviceCommand.getProtocol().getAttributes();
      commandForm.add(nameField);
      commandForm.add(protocolField);
      commandForm.add(attrSet);
      
      commandForm.layout();
   }
   private void addFields(ComboBoxDataModel<ProtocolDefinition> data){
      FieldSet attrSet = new FieldSet();
      FormLayout layout = new FormLayout();  
      layout.setLabelWidth(80);  
      attrSet.setLayout(layout);
      attrSet.setHeading(data.getLabel()+" attributes");
      
      for (ProtocolAttrDefinition attrDefinition : data.getData().getAttrs()) {
         TextField<String> attrField = new TextField<String>();
         attrField.setName(attrDefinition.getName());
         TextField<String>.TextFieldMessages messages = attrField.getMessages();
         attrField.setFieldLabel(attrDefinition.getLabel());

         setValidators(attrField, messages, attrDefinition.getValidators());

         attrSet.add(attrField);
      }
      commandForm.add(attrSet);
      commandForm.layout();
   }
   
   private void setValidators(TextField<String> attrFile, TextField<String>.TextFieldMessages messages,
         List<ProtocolValidator> protocolValidators) {
      for (ProtocolValidator protocolValidator : protocolValidators) {
         if (protocolValidator.getType() == ProtocolValidator.ALLOW_BLANK_TYPE) {
            if (Boolean.valueOf(protocolValidator.getValue())) {
               attrFile.setAllowBlank(true);
            } else {
               attrFile.setAllowBlank(false);
            }
            messages.setBlankText(protocolValidator.getMessage());
         } else if (protocolValidator.getType() == ProtocolValidator.MAX_LENGTH_TYPE) {
            attrFile.setMaxLength(Integer.valueOf(protocolValidator.getValue()));
            messages.setMaxLengthText(protocolValidator.getMessage());
         } else if (protocolValidator.getType() == ProtocolValidator.MIN_LENGTH_TYPE) {
            attrFile.setMinLength(Integer.valueOf(protocolValidator.getValue()));
            messages.setMinLengthText(protocolValidator.getMessage());
         } else if (protocolValidator.getType() == ProtocolValidator.REGEX_TYPE) {
            attrFile.setRegex(protocolValidator.getValue());
            messages.setRegexText(protocolValidator.getMessage());
         }
      }
}
}
