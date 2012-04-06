package org.openremote.modeler.irfileparser;

import org.openremote.ir.domain.DeviceInfo;

import com.extjs.gxt.ui.client.data.BeanModelTag;

/**
 * allows to share xcfFileParser.codeset necessary information with the
 * client side
 * 
 * @author wbalcaen
 * 
 */
public class CodeSetInfo extends org.openremote.ir.domain.CodeSetInfo implements BeanModelTag {

   private static final long serialVersionUID = 1L;

   public CodeSetInfo() {
     super();
   }

  public CodeSetInfo(DeviceInfo device, String description, String category, int index) {
    super(device, description, category, index);
  }
   
}
