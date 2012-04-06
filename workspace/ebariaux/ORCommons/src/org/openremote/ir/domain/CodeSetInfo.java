/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
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
