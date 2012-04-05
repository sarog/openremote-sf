package org.openremote.ir.domain;

import java.io.Serializable;

/**
 * allows to share xcfFileParser.codeset necessary information with the
 * client side
 * 
 * @author wbalcaen
 * 
 */
public class CodeSetInfo implements Serializable {

  private static final long serialVersionUID = 1L;

  private Integer index;
  private String category;
  private String description;
  private DeviceInfo deviceInfo;

  public CodeSetInfo() {
  }

  public CodeSetInfo(DeviceInfo device, String description, String category, int index) {
    setDeviceInfo(device);
    setDescription(description);
    setCategory(category);
    setIndex(index);
  }

  public Integer getIndex() {
    return index;
  }

  public void setIndex(Integer index) {
    this.index = index;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public DeviceInfo getDeviceInfo() {
    return deviceInfo;
  }

  public void setDeviceInfo(DeviceInfo deviceInfo) {
    this.deviceInfo = deviceInfo;
  }

  /*
  public void setBrandInfo(BrandInfo brandInfo) {
    set("brandInfo", brandInfo);
  }

  public List<IRCommandInfo> getIRCommandInfo() {
    return get("iRCommandInfos");
  }

  public void setIRCommandInfo(List<IRCommandInfo> iRCommandInfos) {
    set("iRCommandInfos", iRCommandInfos);
  }
  
  */
}
