package org.openremote.modeler.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class DeviceMacroItemModel extends BaseModelData {

   private static final String DATA_FIELD = "data";
   private static final String LABEL_FIELD = "label";

   public DeviceMacroItemModel(String label, Object deviceMacroItem) {
      set(LABEL_FIELD, label);
      set(DATA_FIELD, deviceMacroItem);
   }

   public String getLabel() {
      return get(LABEL_FIELD).toString();
   }

   public Object getData() {
      return  get(DATA_FIELD);
   }
   
   public static String getDisplayProperty() {
      return LABEL_FIELD;
   }
   
   public static String getDataProperty() {
      return DATA_FIELD;
   }
}
