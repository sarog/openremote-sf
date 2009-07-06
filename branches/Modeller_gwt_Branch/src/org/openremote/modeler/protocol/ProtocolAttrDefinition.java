package org.openremote.modeler.protocol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProtocolAttrDefinition implements Serializable{
   /**
    * 
    */
   private static final long serialVersionUID = 2728595716149551560L;
   private String name;
   private String label;
   private List<ProtocolValidator> validators = new ArrayList<ProtocolValidator>();
   
   public String getName() {
      return name;
   }
   public void setName(String name) {
      this.name = name;
   }
   public String getLabel() {
      return label;
   }
   public void setLabel(String label) {
      this.label = label;
   }
   public List<ProtocolValidator> getValidators() {
      return validators;
   }
   public void setValidators(List<ProtocolValidator> validators) {
      this.validators = validators;
   }
   
   
}
