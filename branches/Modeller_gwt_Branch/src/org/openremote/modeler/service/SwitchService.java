package org.openremote.modeler.service;

import java.util.List;

import org.openremote.modeler.domain.Switch;

public interface SwitchService {
   
   public List<Switch> loadAll();
   
   public void delete(Switch switchToggle);
   
   public void save(Switch switchToggle);
   
   public void update(Switch switchToggle);
   
}
