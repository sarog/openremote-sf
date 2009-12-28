package org.openremote.modeler.service.impl;

import java.util.List;

import org.hibernate.Hibernate;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.SwitchService;

@SuppressWarnings("unchecked")
public class SwitchServiceImpl extends BaseAbstractService implements SwitchService {


   @Override
   public void delete(Switch switchToggle) {
      genericDAO.delete(switchToggle);
   }

   @Override
   public List<Switch> loadAll() {
      List<Switch> switchs = genericDAO.loadAll(Switch.class);
      Hibernate.initialize(switchs);
      return switchs;
   }


   @Override
   public Switch save(Switch switchToggle) {
      genericDAO.save(switchToggle);
      return switchToggle;
   }

   @Override
   public Switch update(Switch switchToggle) {
      Switch oldSwitch = genericDAO.loadById(Switch.class, switchToggle.getOid());
      oldSwitch.setName(switchToggle.getName());
//      oldSwitch.setSwitchCommandOffRef(switchToggle.getSwitchCommandOffRef());
//      oldSwitch.setSwitchCommandOnRef(switchToggle.getSwitchCommandOnRef());
//      oldSwitch.setSwitchSensorRef(switchToggle.getSwitchSensorRef());
      
      return oldSwitch;
   }
   
    @Override
   public List<Switch> loadAll(Account account) {
      List<Switch> switchs = account.getSwitches();
      return switchs;
   }


}
