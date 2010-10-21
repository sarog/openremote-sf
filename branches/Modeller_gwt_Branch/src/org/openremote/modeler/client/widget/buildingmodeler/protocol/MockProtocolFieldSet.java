package org.openremote.modeler.client.widget.buildingmodeler.protocol;

import java.util.List;

import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.ProtocolAttr;

import com.extjs.gxt.ui.client.widget.form.TextField;

public class MockProtocolFieldSet extends AbstractProtocolFieldSet {

   private TextField<String> colorField;
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
   }

   @Override
   public void initFiledValuesByProtocol(Protocol protocol) {
      super.initFiledValuesByProtocol(protocol);
      List<ProtocolAttr> protocolAttrs = protocol.getAttributes();
      for (ProtocolAttr protocolAttr : protocolAttrs) {
         if (colorField.getName().equals(protocolAttr.getName())) {
            colorField.setValue(protocolAttr.getValue());
         }
      }
      
   }

   
}
