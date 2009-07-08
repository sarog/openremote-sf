package org.openremote.modeler.client.widget;

import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;

public class DeviceForm extends Window {
   
   private FormPanel formPanel;
   
   public DeviceForm() {
      initial();
      createFields();
      add(formPanel);
   }
   
   private void initial(){
      this.setWidth(360);
      this.setHeight(200);
      this.setHeading("New Device");
      formPanel = new FormPanel();

      formPanel.setFrame(true);
      formPanel.setHeaderVisible(false);
      formPanel.setWidth(350);


      setButtonAlign(HorizontalAlignment.CENTER);

      Button submitBtn = new Button("Submit");
      Button resetButton = new Button("Reset");

      submitBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            if (formPanel.isValid()) {
               formPanel.submit();
            }
         }

      });

      resetButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            List<Field> list = formPanel.getFields();
            for (Field f : list) {
               f.reset();
            }
         }

      });
      formPanel.addButton(submitBtn);
      formPanel.addButton(resetButton);
   }
   
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
      
      TextField<String> typeField = new TextField<String>();
      typeField.setName("type");
      typeField.setFieldLabel("Type");
      
      formPanel.add(nameField);
      formPanel.add(vendorField);
      formPanel.add(modelField);
      formPanel.add(typeField);
   }
}
