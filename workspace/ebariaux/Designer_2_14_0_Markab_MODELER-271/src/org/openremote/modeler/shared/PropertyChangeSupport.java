package org.openremote.modeler.shared;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PropertyChangeSupport {
  
  private Map<String, Set<PropertyChangeListener>> listeners = new HashMap<String, Set<PropertyChangeListener>>(); 
  
  private Object sourceBean;
  
  public PropertyChangeSupport(Object sourceBean) {
    super();
    this.sourceBean = sourceBean;
  }

  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    Set<PropertyChangeListener> listenerLists = listeners.get(propertyName);
    
    if (listenerLists == null) {
      listenerLists = new HashSet<PropertyChangeListener>();
      listeners.put(propertyName, listenerLists);
    }
    
    listenerLists.add(listener);
  }
  
  public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    Set<PropertyChangeListener> listenerLists = listeners.get(propertyName);

    if (listenerLists != null) {
      listenerLists.remove(listener);
      if (listenerLists.isEmpty()) {
        listeners.remove(propertyName);
      }
    }
  }
  
  public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    firePropertyChange(new PropertyChangeEvent(sourceBean, propertyName, oldValue, newValue));
    
  }
  
  public void firePropertyChange(PropertyChangeEvent evt) {
    Set<PropertyChangeListener> listenerLists = listeners.get(evt.getPropertyName());

    if (listenerLists != null) {
      for (PropertyChangeListener listener : listenerLists) {
        listener.propertyChange(evt);
      }
    }
  }

}
