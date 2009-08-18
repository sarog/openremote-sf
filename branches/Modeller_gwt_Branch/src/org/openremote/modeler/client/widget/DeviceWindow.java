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
package org.openremote.modeler.client.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.proxy.DeviceBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.selenium.DebugId;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;


/**
 * The Class DeviceWindow.
 */
public class DeviceWindow extends FormWindow {
   
   /** The DEVIC e_ name. */
   public static final String DEVICE_NAME = "name";
   
   /** The DEVIC e_ vendor. */
   public static final String DEVICE_VENDOR = "vendor";
   
   /** The DEVIC e_ model. */
   public static final String DEVICE_MODEL = "model";
   
   
   /** The device model. */
   private BeanModel deviceModel = null;
   
   /**
    * Instantiates a new device window.
    */
   public DeviceWindow() {
      super();
      initial("New device");
      this.ensureDebugId(DebugId.NEW_DEVICE_WINDOW);
      show();
   }
   
   /**
    * Instantiates a new device window.
    * 
    * @param deviceModel the device model
    */
   public DeviceWindow(BeanModel deviceModel) {
      super();
      this.deviceModel = deviceModel;
      initial("Edit device");
      show();
   }
   
   /**
    * Initial.
    * 
    * @param heading the heading
    */
   private void initial(String heading) {
      setSize(360, 200);
      setHeading(heading);

      Button submitBtn = new Button("Submit");
      submitBtn.ensureDebugId(DebugId.DEVICE_SUBMIT_BTN);
      Button resetButton = new Button("Reset");

      submitBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            if (form.isValid()) {
               form.submit();
            }
         }

      });

      resetButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            form.reset();
         }

      });
      form.addButton(submitBtn);
      form.addButton(resetButton);
      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
            List<Field<?>> list = form.getFields();
            Map<String, String> attrMap = new HashMap<String, String>();
            for (Field<?> field : list) {
               attrMap.put(field.getName(), field.getValue().toString());
            }
            
            AsyncSuccessCallback<BeanModel> callback = new AsyncSuccessCallback<BeanModel>() {
               @Override
               public void onSuccess(BeanModel deviceModel) {
                  fireEvent(SubmitEvent.Submit, new SubmitEvent(deviceModel));
               }
            };
            if (deviceModel == null) {
               DeviceBeanModelProxy.saveDevice(attrMap, callback);
            } else {
               DeviceBeanModelProxy.updateDevice(deviceModel, attrMap, callback);
            }
         }

      });
      
      createFields();
      add(form);
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
      
      if (deviceModel != null) {
         Device device = deviceModel.getBean();
         nameField.setValue(device.getName());
         vendorField.setValue(device.getVendor());
         modelField.setValue(device.getModel());
      }
      
      form.add(nameField);
      form.add(vendorField);
      form.add(modelField);
   }
}
