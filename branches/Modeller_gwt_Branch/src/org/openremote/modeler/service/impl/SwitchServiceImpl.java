/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.modeler.service.impl;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.domain.SwitchSensorRef;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.SwitchService;

public class SwitchServiceImpl extends BaseAbstractService<Switch> implements SwitchService {

   @Override
   public void delete(long id) {
      Switch switchToggle = super.loadById(id);
      DetachedCriteria criteria = DetachedCriteria.forClass(SwitchSensorRef.class);
      List<SwitchSensorRef> switchSensorRefs = genericDAO.findByDetachedCriteria(criteria.add(Restrictions.eq("switchToggle", switchToggle)));
      genericDAO.deleteAll(switchSensorRefs);
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
