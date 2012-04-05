package org.openremote.modeler.irfileparser;

import org.openremote.ir.domain.BrandInfo;

import com.extjs.gxt.ui.client.data.BeanModelTag;

/**
 * allows to share xcfFileParser.device necessary information with the client
 * side
 * 
 * @author wbalcaen
 * 
 */
public class DeviceInfo extends org.openremote.ir.domain.DeviceInfo implements BeanModelTag {

   private static final long serialVersionUID = 1L;

  public DeviceInfo() {
    super();
  }

  public DeviceInfo(BrandInfo brand, String modelName) {
    super(brand, modelName);
  }

}
