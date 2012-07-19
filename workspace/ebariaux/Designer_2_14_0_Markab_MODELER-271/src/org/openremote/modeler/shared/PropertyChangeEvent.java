package org.openremote.modeler.shared;

public class PropertyChangeEvent {

  private Object source;
  private String propertyName;
  private Object oldValue;
  private Object newValue;

  public PropertyChangeEvent(Object source, String propertyName, Object oldValue, Object newValue) {
    super();
    this.source = source;
    this.propertyName = propertyName;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }
  
  public Object getSource() {
    return source;
  }
  
  public String getPropertyName() {
    return propertyName;
  }
  
  public Object getOldValue() {
    return oldValue;
  }
  
  public Object getNewValue() {
    return newValue;
  }

}