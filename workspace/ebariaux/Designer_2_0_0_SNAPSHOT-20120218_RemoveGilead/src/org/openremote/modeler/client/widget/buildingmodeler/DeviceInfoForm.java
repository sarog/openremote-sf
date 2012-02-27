/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2011, OpenRemote Inc.
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

import java.util.Map;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.proxy.DeviceBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.CommonForm;
import org.openremote.modeler.selenium.DebugId;
import org.openremote.modeler.shared.dto.DeviceDetailsDTO;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.TextField;

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
      final DeviceDetailsDTO device = (DeviceDetailsDTO) deviceBeanModel.getBean(); 

      addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {            
            AsyncSuccessCallback<Void> callback = new AsyncSuccessCallback<Void>() {
               @Override
               public void onSuccess(Void result) {
                  wrapper.fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(deviceBeanModel));
               }
            };
            
            // TODO EBR : to review, seems this is only used for edit
            if (device.getName() == null) {
              // TODO: double check
//               DeviceBeanModelProxy.saveDevice(getFieldMap(), callback);
            } else {
              updateDeviceWithFieldValues(device, getFieldMap());
              DeviceBeanModelProxy.updateDeviceWithDTO(device, callback);
            }
         }

      });
      createFields();
   }

  private void updateDeviceWithFieldValues(DeviceDetailsDTO device, Map<String, String> map) {
    device.setName(map.get(DEVICE_NAME));
    device.setVendor(map.get(DEVICE_VENDOR));
    device.setModel(map.get(DEVICE_MODEL));
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

      // TODO EBR : seems this will never be null, double check
      
      if (deviceBeanModel != null) {
         DeviceDetailsDTO device = deviceBeanModel.getBean();
         nameField.setValue(device.getName());
         vendorField.setValue(device.getVendor());
         modelField.setValue(device.getModel());
      }

      add(nameField);
      add(vendorField);
      add(modelField);
   }
   
}
