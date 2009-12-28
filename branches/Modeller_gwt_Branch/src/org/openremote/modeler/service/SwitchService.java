package org.openremote.modeler.service;

import java.util.List;

import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.Switch;

public interface SwitchService {
   
   public List<Switch> loadAll(Account account);
   public List<Switch> loadAll();
   
   public void delete(Switch switchToggle);
   
   public Switch save(Switch switchToggle);
   
   public Switch update(Switch switchToggle);
   
}
