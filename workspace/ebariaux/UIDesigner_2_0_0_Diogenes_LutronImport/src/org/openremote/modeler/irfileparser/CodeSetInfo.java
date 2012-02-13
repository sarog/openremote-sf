package org.openremote.modeler.irfileparser;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.tinsys.ir.database.IRCommand;

/**
 * allows to share xcfFileParser.codeset necessary information with the
 * client side
 * 
 * @author wbalcaen
 * 
 */
public class CodeSetInfo extends BaseModel implements IsSerializable {

   private static final long serialVersionUID = 1L;

   public CodeSetInfo() {
   }

   public CodeSetInfo(DeviceInfo device, String description, String category,
         int index) {
      setDeviceInfo(device);
      setDescription(description);
      setCategory(category);
      setIndex(index);
   }

   /** 
    * Sets the index that will allow to retrieve the codeset in xcfFileParser bean
    * 
    * @param index
    */
   public void setIndex(Integer index) {
      set("index", index);

   }

   /** 
    * returns the index that will allow to retrieve the codeset in xcfFileParser bean
    * 
    * @return Integer
    */
   public Integer getIndex() {
      return get("index");
   }

   /**
    * sets the category
    * 
    * @param category
    */
   public void setCategory(String category) {
      set("category", category);

   }

   /**
    * returns the category
    * 
    * @return String
    */
   public String getCategory() {
      return get("category");
   }

   /**
    * sets the description 
    * 
    * @param description
    */
   public void setDescription(String description) {
      set("description", description);

   }

   /**
    * returns the description 
    * 
    * @return String
    */
   public String getDescription() {
      return get("description");
   }

   /**
    * sets the device
    * 
    * @param device
    */
   public void setDeviceInfo(DeviceInfo device) {
      set("deviceInfo", device);

   }

   /**
    * 
    * return the device
    * 
    * @return DeviceInfo
    */
   public DeviceInfo getDeviceInfo() {
      return get("deviceInfo");
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
    * returns the IrCommandInfo
    * 
    * @return List<IRCommandInfo>
    */
   public List<IRCommandInfo> getIRCommandInfo() {
      return get("iRCommandInfos");
   }

   /**
    * 
    * sets the IRCommandInfos
    * 
    * @param iRCommandInfos
    */
   public void setIRCommandInfo(List<IRCommandInfo> iRCommandInfos) {
      set("iRCommandInfos", iRCommandInfos);
   }
}
