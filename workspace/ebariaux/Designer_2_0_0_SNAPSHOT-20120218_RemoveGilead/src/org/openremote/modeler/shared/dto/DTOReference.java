package org.openremote.modeler.shared.dto;

import java.io.Serializable;

/**
 * Used so that one DTO can reference another one (e.g. sensor referencing its command).
 * Required so that reference can both exist for objects already persisted (that have an id)
 * and newly created instances (that don't have an id).
 * 
 * @author Eric Bariaux (eric@openremote.org)
 */
public class DTOReference implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private DTO dto;
  private Long id;

  public DTOReference() {
    super();
  }

  public DTOReference(DTO dto) {
    super();
    this.dto = dto;
  }

  public DTOReference(Long id) {
    super();
    this.id = id;
  }

  public DTO getDto() {
    return dto;
  }

  public void setDto(DTO dto) {
    this.dto = dto;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
  
}
