package org.openremote.modeler.irfileparser;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * contains informations for led command information in irTrans commands
 * 
 * @author wbalcaen
 * 
 */
public class IRLed extends BaseModelData {

   private static final long serialVersionUID = 1L;

   public IRLed() {

   }

   public IRLed(String value, String code) {
      setValue(value);
      setCode(code);
   }

   public String getCode() {
      return get("code");
   }

   public void setCode(String code) {
      set("code", code);

   }

   protected String getValue() {
      return get("value");
   }

   protected void setValue(String value) {
      set("value", value);
   }
}
