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
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.SwitchService;
import org.openremote.modeler.service.UserService;

public class SwitchServiceImpl extends BaseAbstractService<Switch> implements SwitchService {
   private UserService userService = null;

   @Override
   public void delete(long id) {
      Switch switchToggle = genericDAO.loadById(Switch.class, id);
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
      if (switchToggle.getSwitchSensorRef() != null) {
         Hibernate.initialize(switchToggle.getSwitchSensorRef().getSensor());
      }
      return switchToggle;
   }

   @Override
   public Switch update(Switch switchToggle) {
      Switch old = genericDAO.loadById(Switch.class, switchToggle.getOid());
      old.setName(switchToggle.getName());
      if (switchToggle.getSwitchCommandOffRef() != null
            && old.getSwitchCommandOffRef().getOid() != switchToggle.getSwitchCommandOffRef().getOid()) {
         genericDAO.delete(old.getSwitchCommandOffRef());
         old.setSwitchCommandOffRef(switchToggle.getSwitchCommandOffRef());
         switchToggle.getSwitchCommandOffRef().setOffSwitch(old);
      }
      if (switchToggle.getSwitchCommandOnRef() != null
            && old.getSwitchCommandOnRef().getOid() != switchToggle.getSwitchCommandOnRef().getOid()) {
         genericDAO.delete(old.getSwitchCommandOnRef());
         old.setSwitchCommandOnRef(switchToggle.getSwitchCommandOnRef());
         switchToggle.getSwitchCommandOnRef().setOnSwitch(old);
      }
      if (old.getSwitchSensorRef() != null
            && old.getSwitchSensorRef().getOid() != switchToggle.getSwitchSensorRef().getOid()) {
         genericDAO.delete(old.getSwitchSensorRef());
         old.setSwitchSensorRef(switchToggle.getSwitchSensorRef());
         switchToggle.getSwitchSensorRef().setSwitchToggle(old);
      }
      return old;
   }
   
    @Override
   public List<Switch> loadAll(Account account) {
      List<Switch> switchs = account.getSwitches();
      return switchs;
   }

   public UserService getUserService() {
      return userService;
   }

   public void setUserService(UserService userService) {
      this.userService = userService;
   }
   
    
}
