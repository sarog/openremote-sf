/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.modeler.client.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.model.ComboBoxDataModel;
import org.openremote.modeler.client.utils.Protocols;
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
 * The Class DeviceCommandWindow.
 */
public class DeviceCommandWindow extends SubmitWindow {
   
   /** The command form. */
   private FormPanel commandForm = new FormPanel();
   
   /** The _device command. */
   private DeviceCommand _deviceCommand = null;
   
   /**
    * Instantiates a new device command window.
    */
   public DeviceCommandWindow() {
      setHeading("New command");
      initial();
      show();
   }
   
   /**
    * Instantiates a new device command window.
    * 
    * @param deviceCommand the device command
    */
   public DeviceCommandWindow(DeviceCommand deviceCommand) {
      this._deviceCommand = deviceCommand;
      setHeading("Edit command");
      initial();
      show();
   }
   
   /**
    * Initial.
    */
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
      createFields(Protocols.getInstance());
      add(commandForm);
   }
   
   /**
    * Creates the fields.
    * 
    * @param protocols the protocols
    */
   private void createFields(Map<String, ProtocolDefinition> protocols){
      TextField<String> nameField = new TextField<String>();
      nameField.setName("name");
      nameField.setFieldLabel("Name");
      nameField.setAllowBlank(false);
      
      ComboBox<ModelData> protocol = new ComboBox<ModelData>();
      ListStore<ModelData> store = new ListStore<ModelData>();
      protocol.setStore(store);
      protocol.setFieldLabel("Protocol");
      protocol.setName("protocol");
      protocol.setAllowBlank(false);
      
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
            addAttrs((ComboBoxDataModel<ProtocolDefinition>)se.getSelectedItem());
         }
      });
      
      if(_deviceCommand != null){
         String protocolName = _deviceCommand.getProtocol().getType();
         nameField.setValue(_deviceCommand.getName());
         ComboBoxDataModel<ProtocolDefinition> data = new ComboBoxDataModel<ProtocolDefinition>(protocolName,protocols.get(protocolName));
         protocol.setValue(data);
         protocol.disable();
      }
      commandForm.layout();
   }
   
   /**
    * Adds the attrs.
    * 
    * @param data the data
    */
   private void addAttrs(ComboBoxDataModel<ProtocolDefinition> data){
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
         if(_deviceCommand != null){
            for (ProtocolAttr attr : _deviceCommand.getProtocol().getAttributes()) {
               if(attrDefinition.getName().equals(attr.getName())){
                  attrField.setValue(attr.getValue());
               }
            }
         }
         setValidators(attrField, messages, attrDefinition.getValidators());

         attrSet.add(attrField);
      }
      commandForm.add(attrSet);
      commandForm.layout();
   }
   
   /**
    * Sets the validators.
    * 
    * @param attrFile the attr file
    * @param messages the messages
    * @param protocolValidators the protocol validators
    */
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
