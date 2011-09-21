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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.model.StringComboBoxData;
import org.openremote.modeler.client.proxy.DeviceBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.ComboBoxExt;
import org.openremote.modeler.client.widget.CommonForm;
import org.openremote.modeler.client.widget.buildingmodeler.protocol.AbstractProtocolFieldSet;
import org.openremote.modeler.client.widget.buildingmodeler.protocol.ProtocolManager;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.ProtocolAttr;
import org.openremote.modeler.protocol.ProtocolAttrDefinition;
import org.openremote.modeler.protocol.ProtocolDefinition;
import org.openremote.modeler.protocol.ProtocolValidator;
import org.openremote.modeler.selenium.DebugId;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

/**
 * Device Info Form, contains basic info of a device, such as name, vendor, model.
 * 
 * @author Dan 2009-8-21
 */
public class DeviceInfoForm extends CommonForm {
   
   /** The Constant DEVICE_NAME. */
   public static final String DEVICE_NAME = "name";
   
   /** The Constant DEVICE_VENDOR. */
   public static final String DEVICE_VENDOR = "vendor";
   
   /** The Constant DEVICE_MODEL. */
   public static final String DEVICE_MODEL = "model";
   
   
   /** The device bean model. */
   protected BeanModel deviceBeanModel = null;
   
   /** The wrapper. */
   protected Component wrapper;
   
   private Protocol protocol = null;
   
   /**
    * Instantiates a new device info form.
    * 
    * @param wrapper the wrapper
    * @param deviceBeanModel the device bean model
    */
   public DeviceInfoForm(final Component wrapper, final BeanModel deviceBeanModel) {
      super();
      this.deviceBeanModel = deviceBeanModel;
      this.wrapper = wrapper;
      addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
            
            AsyncSuccessCallback<BeanModel> callback = new AsyncSuccessCallback<BeanModel>() {
               @Override
               public void onSuccess(BeanModel deviceModel) {
                  wrapper.fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(deviceModel));
               }
            };
            if (((Device) deviceBeanModel.getBean()).getName() == null) {
               DeviceBeanModelProxy.saveDevice(getDeviceFieldMap(), callback);
            } else {
               DeviceBeanModelProxy.updateDevice(deviceBeanModel, getDeviceFieldMap(), callback);
            }
         }

      });
      createFields();
   }

	private Map<String, String> getDeviceFieldMap() {
		List<Field<?>> list = getFields();
		Map<String, String> attrMap = new HashMap<String, String>();
		for (Field<?> f : list) {
            if (Device.PROTOCOL_TYPE.equals(f.getName())) {
               Field<BaseModelData> p = (Field<BaseModelData>) f;
               attrMap.put(Device.PROTOCOL_TYPE, p.getValue().get(StringComboBoxData.getDisplayProperty())
                     .toString());
            } else {
               if (f.getValue() != null && !"".equals(f.getValue().toString())) {
                  attrMap.put(f.getName(), f.getValue().toString());
               }
            }
         }
		return attrMap;
	}


   /**
    * Creates the fields.
    */
   private void createFields() {
      TextField<String> nameField = new TextField<String>();
      nameField.setName(DEVICE_NAME);
      nameField.ensureDebugId(DebugId.DEVICE_NAME_FIELD);
      nameField.setFieldLabel("Name");
      nameField.setAllowBlank(false);
      
      TextField<String> vendorField = new TextField<String>();
      vendorField.setName(DEVICE_VENDOR);
      vendorField.setFieldLabel("Vendor");
      vendorField.ensureDebugId(DebugId.DEVICE_VENDOR_FIELD);
      vendorField.setAllowBlank(false);
      
      TextField<String> modelField = new TextField<String>();
      modelField.setName(DEVICE_MODEL);
      modelField.setFieldLabel("Model");
      modelField.ensureDebugId(DebugId.DEVICE_MODEL_FIELD);
      modelField.setAllowBlank(false);
      
      
      if (deviceBeanModel != null) {
         Device device = deviceBeanModel.getBean();
         nameField.setValue(device.getName());
         vendorField.setValue(device.getVendor());
         modelField.setValue(device.getModel());
         
         protocol = device.attrs2Protocol();
      }
      
      add(nameField);
      add(vendorField);
      add(modelField);
      addProtocolFields();
   }
   
	private void addProtocolFields() {
		List<String> protocolNames = ProtocolManager.getInstance()
				.getProtocolNames();
		ComboBoxExt protocolComb = new ComboBoxExt();
		protocolComb.setFieldLabel("Protocol");
		protocolComb.setName(Device.PROTOCOL_TYPE);

		for (String protocolName : protocolNames) {
			if (!protocolName.equalsIgnoreCase(Protocol.INFRARED_TYPE)) {
				StringComboBoxData data = new StringComboBoxData(protocolName,
						protocolName);
				protocolComb.getStore().add(data);
			}
		}

		protocolComb.setDisplayField(StringComboBoxData.getDisplayProperty());
		protocolComb.setEmptyText("Please Select Protocol...");
		protocolComb.setValueField(StringComboBoxData.getDisplayProperty());
		
		add(protocolComb);
		
		protocolComb.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
	         public void selectionChanged(SelectionChangedEvent<ModelData> se) {
	            if (getItems().size() > 4) {
	                  getItem(4).removeFromParent();
	            }
	            addAttrs((StringComboBoxData) se.getSelectedItem());
	         }
	      });
		
		if (protocol != null) {
			String protocolName = protocol.getType();
	         if (protocolNames.contains(protocolName)) {
	            StringComboBoxData data = new StringComboBoxData(protocolName, protocolName);
	            protocolComb.setValue(data);
	         }
		}
		
		layout();
	}
	
	private void addAttrs(StringComboBoxData data) {
	      String protocolName = data.getLabel();
	      ProtocolDefinition xmProtocol = ProtocolManager.getInstance().getXmlProtocol(protocolName);
	      if (xmProtocol != null) {
	         FieldSet attrSet = new FieldSet();
	         FormLayout layout = new FormLayout();
	         layout.setLabelWidth(80);
	         attrSet.setLayout(layout);
	         attrSet.setHeading(protocolName + " attributes");

	         for (ProtocolAttrDefinition attrDefinition : xmProtocol.getAttrs()) {
	            List<String> options = attrDefinition.getOptions();
	            String value = "";
	            if (attrDefinition.getValue() != null) {
	               value = attrDefinition.getValue();
	            }
	            if (protocol != null) {
	               for (ProtocolAttr attr : protocol.getAttributes()) {
	                  if (attrDefinition.getName().equals(attr.getName())) {
	                     value = attr.getValue();
	                  }
	               }
	            }

	            if (attrDefinition.isCheckBox()) {
	               CheckBox checkBox = new CheckBox() {
	                  protected void afterRender() {
	                     super.afterRender();
	                     // make the checkBox be left alignment
	                     this.setInputStyleAttribute("left", "0px");
	                  }
	               };
	               checkBox.setFieldLabel(attrDefinition.getLabel());
	               checkBox.setName(attrDefinition.getName());
	               if (!"".equals(value)) {
	                  checkBox.setValue(Boolean.valueOf(value));
	               }
	               attrSet.add(checkBox);
	            } else if (options.size() > 0) {
	               ComboBoxExt comboAttrField = new ComboBoxExt();
	               comboAttrField.setName(attrDefinition.getName());
	               comboAttrField.setFieldLabel(attrDefinition.getLabel());
	               ComboBoxExt.ComboBoxMessages comboBoxMessages = comboAttrField.getMessages();
	               for (String option : options) {
	                  if (!"".equals(option)) {
	                     StringComboBoxData comboData = new StringComboBoxData(option, option);
	                     comboAttrField.getStore().add(comboData);
	                     if (value.equals(option)) {
	                        comboAttrField.setValue(comboData);
	                     }
	                  }
	               }
	               setComboBoxValidators(comboAttrField, comboBoxMessages, attrDefinition.getValidators());
	               attrSet.add(comboAttrField);
	            } else {
	               TextField<String> attrField = new TextField<String>();
	               attrField.setName(attrDefinition.getName());
	               TextField<String>.TextFieldMessages messages = attrField.getMessages();
	               attrField.setFieldLabel(attrDefinition.getLabel());
	               if (!"".equals(value)) {
	                  attrField.setValue(value);
	               }
	               setValidators(attrField, messages, attrDefinition.getValidators());
	               attrSet.add(attrField);
	            }
	         }
	         add(attrSet);
	      } else {
	         AbstractProtocolFieldSet protocolSet = ProtocolManager.getInstance().getUIProtocol(protocolName);
	         if (protocolSet != null) {
	            List<ProtocolAttr> protocolAttrs = protocol == null ? null : protocol.getAttributes();
	            protocolSet.initFiledValuesByProtocol(protocolAttrs);
	            add(protocolSet);
	         }
	      }
	      layout();
	      if (isRendered()) {
	         ((Window)getParent()).layout();
	         ((Window)getParent()).center();
	      }
	   }

	   /**
	    * Sets the validators.
	    * 
	    * @param attrField the attr file
	    * @param messages the messages
	    * @param protocolValidators the protocol validators
	    */
	   private void setValidators(TextField<String> attrField, TextField<String>.TextFieldMessages messages,
	         List<ProtocolValidator> protocolValidators) {
	      for (ProtocolValidator protocolValidator : protocolValidators) {
	         if (protocolValidator.getType() == ProtocolValidator.ALLOW_BLANK_TYPE) {
	            if (Boolean.valueOf(protocolValidator.getValue())) {
	               attrField.setAllowBlank(true);
	            } else {
	               attrField.setAllowBlank(false);
	               messages.setBlankText(protocolValidator.getMessage());
	            }
	         } else if (protocolValidator.getType() == ProtocolValidator.MAX_LENGTH_TYPE) {
	            attrField.setMaxLength(Integer.valueOf(protocolValidator.getValue()));
	            messages.setMaxLengthText(protocolValidator.getMessage());
	         } else if (protocolValidator.getType() == ProtocolValidator.MIN_LENGTH_TYPE) {
	            attrField.setMinLength(Integer.valueOf(protocolValidator.getValue()));
	            messages.setMinLengthText(protocolValidator.getMessage());
	         } else if (protocolValidator.getType() == ProtocolValidator.REGEX_TYPE) {
	            attrField.setRegex(protocolValidator.getValue());
	            messages.setRegexText(protocolValidator.getMessage());
	         }
	      }
	   }
	   
	   private void setComboBoxValidators(ComboBoxExt comboField, ComboBoxExt.ComboBoxMessages messages,
	         List<ProtocolValidator> protocolValidators) {
	      for (ProtocolValidator protocolValidator : protocolValidators) {
	         if (protocolValidator.getType() == ProtocolValidator.ALLOW_BLANK_TYPE) {
	            if (Boolean.valueOf(protocolValidator.getValue())) {
	               comboField.setAllowBlank(true);
	            } else {
	               comboField.setAllowBlank(false);
	               messages.setBlankText(protocolValidator.getMessage());
	            }
	         } else if (protocolValidator.getType() == ProtocolValidator.MAX_LENGTH_TYPE) {
	            comboField.setMaxLength(Integer.valueOf(protocolValidator.getValue()));
	            messages.setMaxLengthText(protocolValidator.getMessage());
	         } else if (protocolValidator.getType() == ProtocolValidator.MIN_LENGTH_TYPE) {
	            comboField.setMinLength(Integer.valueOf(protocolValidator.getValue()));
	            messages.setMinLengthText(protocolValidator.getMessage());
	         } else if (protocolValidator.getType() == ProtocolValidator.REGEX_TYPE) {
	            comboField.setRegex(protocolValidator.getValue());
	            messages.setRegexText(protocolValidator.getMessage());
	         }
	      }
	   }
}
