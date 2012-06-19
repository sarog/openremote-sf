package org.openremote.modeler.irfileparser;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * allows to share xcfFileParser.brand necessary information with the client side
 * 
 * @author wbalcaen
 *
 */
public class BrandInfo extends BaseModel implements IsSerializable {

   private static final long serialVersionUID = 1L;

   public BrandInfo() {
   }

   public BrandInfo(String brandName) {
      setBrandName(brandName);
   }

   /** 
    * returns the brandName
    * 
    * @return String
    */
   public String getBrandName() {
      return get("brandName");
   }

   /** 
    * sets the brandName
    * 
    * @param brandName
    */
   public void setBrandName(String brandName) {
      set("brandName", brandName);
   }

}
