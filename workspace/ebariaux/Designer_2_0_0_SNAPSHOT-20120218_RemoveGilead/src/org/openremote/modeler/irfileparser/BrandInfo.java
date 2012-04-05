package org.openremote.modeler.irfileparser;

import com.extjs.gxt.ui.client.data.BeanModelTag;


/**
 * allows to share xcfFileParser.brand necessary information with the client side
 * 
 * @author wbalcaen
 *
 */
public class BrandInfo extends org.openremote.ir.domain.BrandInfo implements BeanModelTag {

   private static final long serialVersionUID = 1L;

  public BrandInfo() {
    super();
  }

  public BrandInfo(String brandName) {
    super(brandName);
  }
   
}
