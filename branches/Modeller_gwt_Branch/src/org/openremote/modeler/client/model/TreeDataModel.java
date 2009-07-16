package org.openremote.modeler.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class TreeDataModel extends BaseTreeModel implements Serializable {

   private static final String DATA = "data";
   private static final String LABEL = "label";
   /**
    * 
    */
   private static final long serialVersionUID = -3212925111586567858L;

   public TreeDataModel(Object o,String label) {
      set(LABEL,label);
      set(DATA, o);
   }

   public String getLabel() {
      return (String) get(LABEL);
   }
   
   public static String getDisplayProperty() {
      return LABEL;
   }
   
   public static String getDataProperty() {
      return DATA;
   }

   public String toString() {
      return getLabel();
   }

   @SuppressWarnings("unchecked")
   public <X> X getData() {
      return (X)get(DATA);
   }
   
  
}


