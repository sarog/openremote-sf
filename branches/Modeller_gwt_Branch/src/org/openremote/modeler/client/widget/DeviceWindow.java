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

/**
 * The Class DeviceForm.
 */
public class DeviceWindow extends SubmitWindow {
   
   /** The window. */
   private FormPanel form;

   /**
    * Instantiates a new create device window.
    */
   public DeviceWindow() {
      initial("New device");
      createFields();
      add(form);
      show();
   }
   
   /**
    * Instantiates a new edit device window.
    * 
    * @param device the device
    */
   public DeviceWindow(Device device){
      initial("Edit device");
      createFields(device);
      add(form);
      show();
   }
   /**
    * Initial.
    */
   private void initial(String heading){
      form = new FormPanel();
      setWidth(360);
      setHeight(200);
      setHeading(heading);

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
            Map<String, String> attrMap = new HashMap<String, String>();
            for (Field<?> f : list) {
               attrMap.put(f.getName(), f.getValue().toString());
            }
            AppEvent appEvent = new AppEvent(Events.Submit, attrMap);
            fireSubmitListener(appEvent);
         }

      });
   }
   
   /**
    * Creates the fields.
    */
   private void createFields(){
      TextField<String> nameField = new TextField<String>();
      nameField.setName("name");
      nameField.setFieldLabel("Name");
      
      TextField<String> vendorField = new TextField<String>();
      vendorField.setName("vendor");
      vendorField.setFieldLabel("Vendor");
      
      TextField<String> modelField = new TextField<String>();
      modelField.setName("model");
      modelField.setFieldLabel("Model");
      
      form.add(nameField);
      form.add(vendorField);
      form.add(modelField);
   }
   
   private void createFields(Device device){
      TextField<String> nameField = new TextField<String>();
      nameField.setName("name");
      nameField.setFieldLabel("Name");
      nameField.setValue(device.getName());
      
      TextField<String> vendorField = new TextField<String>();
      vendorField.setName("vendor");
      vendorField.setFieldLabel("Vendor");
      vendorField.setValue(device.getVendor());
      
      TextField<String> modelField = new TextField<String>();
      modelField.setName("model");
      modelField.setFieldLabel("Model");
      modelField.setValue(device.getModel());
      
      form.add(nameField);
      form.add(vendorField);
      form.add(modelField);
   }
}
