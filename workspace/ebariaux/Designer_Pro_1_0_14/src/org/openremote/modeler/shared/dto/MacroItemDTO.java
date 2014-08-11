package org.openremote.modeler.shared.dto;

public class MacroItemDTO implements DTO {

  private static final long serialVersionUID = 1L;

  private String displayName;
  private MacroItemType type;
  
  public MacroItemDTO() {
    super();
  }
  
  public MacroItemDTO(String displayName, MacroItemType type) {
    super();
    this.displayName = displayName;
    this.type = type;
  }

  public String getDisplayName() {
    return displayName;
  }
  
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
  
  public MacroItemType getType() {
    return type;
  }
  
  public void setType(MacroItemType type) {
    this.type = type;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
    MacroItemDTO other = (MacroItemDTO) obj;
    if (displayName == null) {
      if (other.displayName != null)
        return false;
    } else if (!displayName.equals(other.displayName))
      return false;
    if (type != other.type)
      return false;
    return true;
  }
  
}