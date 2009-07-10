package org.openremote.modeler.client.model;

import java.io.Serializable;

import org.openremote.modeler.domain.BusinessEntity;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class TreeDataModel<T extends BusinessEntity> extends BaseTreeModel implements Serializable {

   private static final String DATA = "data";
   private static final String LABEL = "label";
   /**
    * 
    */
   private static final long serialVersionUID = -3212925111586567858L;

   public TreeDataModel(T t,String label) {
      set(LABEL,label);
      set(DATA, t);
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

   public T getData() {
      return (T) get(DATA);
   }
}
