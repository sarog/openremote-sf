package org.openremote.modeler.shared.dto;

public interface UICommandDTO {
  
  Long getOid();
  String getDisplayName();
  
  /**
   * Provides a hash value based on equality test. The default hashCode() method is based on identity.
   * See MODELER-500 issue for more background information.
   * 
   * @return a hash code value for this object.
   */
  int equalityHashCode();

  /**
   * Performs a "real" equality test, the default equal() method is based on identity.
   * See MODELER-500 issue for more background information.
   * 
   * @return true if this object is the same as the obj argument; false otherwise.
   */
  boolean equalityEquals(Object obj);

}
