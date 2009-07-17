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

import java.util.List;

import org.openremote.modeler.domain.Device;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;

/**
 * The Class DeviceWindow.
 */
public class DeviceWindow extends SubmitWindow {
   
   /** The form. */
   private FormPanel form = new FormPanel();
   
   /** The _device. */
   private Device _device = null;
   
   /**
    * Instantiates a new device window.
    */
   public DeviceWindow() {
      initial("New device");
      show();
   }
   
   /**
    * Instantiates a new device window.
    * 
    * @param device the device
    */
   public DeviceWindow(Device device){
      this._device = device;
      initial("Edit device");
      show();
   }
   
   /**
    * Initial.
    * 
    * @param heading the heading
    */
   private void initial(String heading){
      setWidth(360);
      setHeight(200);
      setHeading(heading);
      setLayout(new FillLayout());
      
      form.setFrame(true);
      form.setHeaderVisible(false);
      form.setWidth(350);

      form.setButtonAlign(HorizontalAlignment.CENTER);

      Button submitBtn = new Button("Submit");
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
            List<Field<?>> list = form.getFields();
            for (Field<?> f : list) {
               f.reset();
            }
         }

      });
      form.addButton(submitBtn);
      form.addButton(resetButton);
      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
            List<Field<?>> list = form.getFields();
            if(_device == null){
               _device = new Device();
            }
            _device.setName(list.get(0).getValue().toString());
            _device.setVendor(list.get(1).getValue().toString());
            _device.setModel(list.get(2).getValue().toString());
            AppEvent appEvent = new AppEvent(Events.Submit, _device);
            fireSubmitListener(appEvent);
         }

      });
      
      createFields();
      add(form);
   }
   
   /**
    * Creates the fields.
    */
   private void createFields(){
      TextField<String> nameField = new TextField<String>();
      nameField.setName("name");
      nameField.setFieldLabel("Name");
      nameField.setAllowBlank(false);
      
      TextField<String> vendorField = new TextField<String>();
      vendorField.setName("vendor");
      vendorField.setFieldLabel("Vendor");
      vendorField.setAllowBlank(false);
      
      TextField<String> modelField = new TextField<String>();
      modelField.setName("model");
      modelField.setFieldLabel("Model");
      modelField.setAllowBlank(false);
      
      if(_device != null){
         nameField.setValue(_device.getName());
         vendorField.setValue(_device.getVendor());
         modelField.setValue(_device.getModel());
      }
      
      form.add(nameField);
      form.add(vendorField);
      form.add(modelField);
   }
}
