package org.openremote.modeler.service.impl;

import java.util.List;

import org.hibernate.Hibernate;
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
   public void save(Switch switchToggle) {
     genericDAO.save(switchToggle);
   }

   @Override
   public void update(Switch switchToggle) {
      genericDAO.saveOrUpdate(switchToggle);
   }
   
   

}
