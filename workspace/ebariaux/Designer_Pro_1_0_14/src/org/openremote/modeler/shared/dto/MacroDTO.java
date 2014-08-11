package org.openremote.modeler.shared.dto;

import java.util.ArrayList;

public class MacroDTO implements DTO, UICommandDTO {

  private static final long serialVersionUID = 1L;
  
  private String displayName;
  private Long oid;
  private ArrayList<MacroItemDTO> items;

  public MacroDTO() {
    super();
  }
  
  public MacroDTO(Long oid, String displayName) {
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
  
  public Long getOid() {
    return oid;
  }
  
  public void setOid(Long oid) {
    this.oid = oid;
  }
  
  public ArrayList<MacroItemDTO> getItems() {
    return items;
  }

  public void setItems(ArrayList<MacroItemDTO> items) {
    this.items = items;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
    result = prime * result + ((items == null) ? 0 : items.hashCode());
    result = prime * result + ((oid == null) ? 0 : oid.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MacroDTO other = (MacroDTO) obj;
    if (displayName == null) {
      if (other.displayName != null)
        return false;
    } else if (!displayName.equals(other.displayName))
      return false;
    if (items == null) {
      if (other.items != null)
        return false;
    } else if (!items.equals(other.items))
      return false;
    if (oid == null) {
      if (other.oid != null)
        return false;
    } else if (!oid.equals(other.oid))
      return false;
    return true;
  }

}