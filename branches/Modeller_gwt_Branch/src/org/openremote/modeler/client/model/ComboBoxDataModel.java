/**
 * 
 */
package org.openremote.modeler.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * @author Tomsky
 *
 */
public class ComboBoxDataModel<T> extends BaseModelData implements Serializable{
   
   private static final String LABEL = "label";
   private static final String DATA = "data";
   /**
    * 
    */
   private static final long serialVersionUID = 4414421641647033397L;
   
   public ComboBoxDataModel(String label, T t) {
      set(LABEL, label);
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

   @SuppressWarnings("unchecked")
   public T getData() {
      return (T) get(DATA);
   }
}
