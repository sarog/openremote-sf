package org.openremote.modeler.shared.dto;

public interface UICommandDTO {
  
  Long getOid();
  String getDisplayName();
  
  int equalityHashCode();
  boolean equalityEquals(Object obj);

}
