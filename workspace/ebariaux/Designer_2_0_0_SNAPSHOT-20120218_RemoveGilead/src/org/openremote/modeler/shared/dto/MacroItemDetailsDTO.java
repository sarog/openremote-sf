package org.openremote.modeler.shared.dto;

public class MacroItemDetailsDTO implements DTO {

  private static final long serialVersionUID = 1L;

  private Long oid;
  private MacroItemType type;
  private Integer delay;
  private DTOReference dto;
  
  public MacroItemDetailsDTO() {
    super();
  }
  
  public MacroItemDetailsDTO(Long oid, Integer delay) {
    super();
    this.oid = oid;
    this.delay = delay;
    this.type = MacroItemType.Delay;
  }

  public MacroItemDetailsDTO(Long oid, MacroItemType type, DTOReference dto) {
    super();
    this.oid = oid;
    this.type = type;
    this.dto = dto;
  }

  public Long getOid() {
    return oid;
  }

  public void setOid(Long oid) {
    this.oid = oid;
  }

  public Integer getDelay() {
    return delay;
  }

  public void setDelay(Integer delay) {
    this.delay = delay;
  }

  public DTOReference getDto() {
    return dto;
  }

  public void setDto(DTOReference dto) {
    this.dto = dto;
  }

  public MacroItemType getType() {
    return type;
  }

  public void setType(MacroItemType type) {
    this.type = type;
  }

  
  // TODO: this will not work when editing existing macro, review
  public String getDisplayName() {
    switch(type) {
      case Delay:
        return "Delay " + delay + " ms";
      case Macro:
        if (dto == null) {
          return "";
        }
        if (dto.getDto() == null) {
          return "";
        }
        return "TODO";
      case Command:
        return "TODO";
    }
    return "";
  }
}
