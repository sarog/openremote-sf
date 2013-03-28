package org.openremote.modeler.shared.dto;

import java.util.ArrayList;
import java.util.Collection;

public class MacroDetailsDTO implements DTO {

  private static final long serialVersionUID = 1L;

  private Long oid;
  private String name;
  private ArrayList<MacroItemDetailsDTO> items;
  
  public MacroDetailsDTO() {
    super();
  }

  public MacroDetailsDTO(Long oid, String name, ArrayList<MacroItemDetailsDTO> items) {
    super();
    this.oid = oid;
    this.name = name;
    this.items = items;
  }

  public Long getOid() {
    return oid;
  }

  public void setOid(Long oid) {
    this.oid = oid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ArrayList<MacroItemDetailsDTO> getItems() {
    return items;
  }

  public void setItems(ArrayList<MacroItemDetailsDTO> items) {
    this.items = items;
  }
  
  /**
   * Checks if this macro has references another macro, whose id is not in the provided list.
   * If list is empty, this basically checks that a macro does reference another macro.
   * 
   * @param otherMacroIds List of ids of macros that this macro can "safely depend on"
   * @return boolean true if this macro references a macro that is not in the provided list
   */
  public boolean dependsOnMacroNotInList(Collection<Long> otherMacroIds) {
    for (MacroItemDetailsDTO item : items) {
      if (item.getType() == MacroItemType.Macro && !otherMacroIds.contains(item.getDto().getId())) {
        return true;
      }
    }
    return false;
  }
  
}
