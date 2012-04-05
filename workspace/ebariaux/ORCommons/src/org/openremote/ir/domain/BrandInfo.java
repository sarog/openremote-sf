package org.openremote.ir.domain;

import java.io.Serializable;

/**
 * allows to share xcfFileParser.brand necessary information with the client side
 * 
 * @author wbalcaen
 *
 */
public class BrandInfo implements Serializable {

  private static final long serialVersionUID = 1L;

  private String brandName;

  public BrandInfo() {
  }

  public BrandInfo(String brandName) {
    setBrandName(brandName);
  }

  public String getBrandName() {
    return brandName;
  }

  public void setBrandName(String brandName) {
    this.brandName = brandName;
  }

}
