package org.openremote.modeler.shared.dto;

import java.util.ArrayList;

public class MacroDTO implements DTO {

  private static final long serialVersionUID = 1L;
  
  private String displayName;
  private long oid;
  private ArrayList<MacroItemDTO> items;

  public MacroDTO() {
    super();
  }
  
  public MacroDTO(long oid, String displayName) {
    super();
    this.oid = oid;
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
  
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
  
  public long getOid() {
    return oid;
  }
  
  public void setOid(long oid) {
    this.oid = oid;
  }
  
  public ArrayList<MacroItemDTO> getItems() {
    return items;
  }

  public void setItems(ArrayList<MacroItemDTO> items) {
    this.items = items;
  }

}