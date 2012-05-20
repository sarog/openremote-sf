package org.openremote.modeler.irfileparser;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * allows to share xcfFileParser.device necessary information with the client
 * side
 * 
 * @author wbalcaen
 * 
 */
public class DeviceInfo extends BaseModel implements IsSerializable {

   /**
	 * 
	 */
   private static final long serialVersionUID = 1L;

   public DeviceInfo() {
   }

   public DeviceInfo(BrandInfo brand, String modelName) {
      setBrandInfo(brand);
      setModelName(modelName);
   }

   /**
    * returns the brandInfo
    * 
    * @return BrandInfo
    */
   public BrandInfo getBrandInfo() {
      return get("brandInfo");
   }

   /**
    * sets the brandInfo
    * 
    * @param brandInfo
    */
   public void setBrandInfo(BrandInfo brandInfo) {
      set("brandInfo", brandInfo);
   }

   /**
    * returns the model name
    * 
    * @return String
    */
   public String getModelName() {
      return get("modelName");
   }

   /**
    * sets the model name 
    * 
    * @param modelName
    */
   public void setModelName(String modelName) {
      set("modelName", modelName);
   }

}
