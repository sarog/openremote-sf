package org.openremote.modeler.client.widget.buildingmodeler.protocol;

import java.util.List;

import org.openremote.modeler.domain.ProtocolAttr;

import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.TextField;

public class MockProtocolFieldSet extends AbstractProtocolFieldSet {

   private TextField<String> colorField;
   private CheckBox classical;
   @Override
   protected String getProtocolDisplayName() {
      return "Mock";
   }

   @Override
   protected String getProtocolType() {
      return "mock";
   }

   @Override
   protected void initFields() {
      colorField = new TextField<String>();
      colorField.setFieldLabel("Color");
      colorField.setName("color");
      add(colorField);
      
      classical = new CheckBox();  
      classical.setFieldLabel("Classical");
      classical.setName("classical");
      add(classical);
      
   }

   @Override
   public void initFiledValuesByProtocol(List<ProtocolAttr> protocolAttrs) {
      if (protocolAttrs == null) {
         colorField.clear();
         classical.clear();
      } else {
         for (ProtocolAttr protocolAttr : protocolAttrs) {
            if (colorField.getName().equals(protocolAttr.getName())) {
               colorField.setValue(protocolAttr.getValue());
            } else if (classical.getName().equals(protocolAttr.getName())) {
               classical.setValue(Boolean.valueOf(protocolAttr.getValue()));
            }
         }
      }
   }

   
}
