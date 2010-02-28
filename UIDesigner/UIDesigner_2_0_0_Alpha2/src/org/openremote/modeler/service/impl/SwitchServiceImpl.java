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
      return switchToggle;
   }

   @Override
   public Switch update(Switch switchToggle) {
      Switch old = genericDAO.loadById(Switch.class, switchToggle.getOid());
      genericDAO.delete(old.getSwitchCommandOffRef());
      genericDAO.delete(old.getSwitchCommandOnRef());
      genericDAO.delete(old.getSwitchSensorRef());
      old.setName(switchToggle.getName());
      switchToggle.getSwitchCommandOffRef().setOffSwitch(old);
      switchToggle.getSwitchCommandOnRef().setOnSwitch(old);
      switchToggle.getSwitchSensorRef().setSwitchToggle(old);
      old.setSwitchCommandOffRef(switchToggle.getSwitchCommandOffRef());
      old.setSwitchCommandOnRef(switchToggle.getSwitchCommandOnRef());
      old.setSwitchSensorRef(switchToggle.getSwitchSensorRef());
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
