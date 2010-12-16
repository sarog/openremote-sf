/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.beehive.api.service.impl;

import org.openremote.beehive.api.dto.modeler.SwitchDTO;
import org.openremote.beehive.api.service.SwitchService;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.modeler.Switch;

public class SwitchServiceImpl extends BaseAbstractService<Switch> implements SwitchService {

   public SwitchDTO save(SwitchDTO switchDTO, long accountId) {
      Account account = genericDAO.loadById(Account.class, accountId);
      Switch switchToggle = switchDTO.toSwitch();
      switchToggle.setAccount(account);
      genericDAO.save(switchToggle);
      
      return switchToggle.toDTO();
   }

   public void deleteSwitchById(long id) {
      Switch switchToggle = genericDAO.loadById(Switch.class, id);
      genericDAO.delete(switchToggle);
   }

   public Switch updateSwitch(SwitchDTO switchDTO) {
      Switch old = genericDAO.loadById(Switch.class, switchDTO.getId());
      old.setName(switchDTO.getName());
      if (switchDTO.getSwitchCommandOffRef() != null && old.getSwitchCommandOffRef() != null
            && old.getSwitchCommandOffRef().getOid() != switchDTO.getSwitchCommandOffRef().getId()) {
         genericDAO.delete(old.getSwitchCommandOffRef());
         old.setSwitchCommandOffRef(switchDTO.getSwitchCommandOffRef().toSwitchCommandOffRef(old));
      }
      if (switchDTO.getSwitchCommandOnRef() != null && old.getSwitchCommandOnRef() != null
            && old.getSwitchCommandOnRef().getOid() != switchDTO.getSwitchCommandOnRef().getId()) {
         genericDAO.delete(old.getSwitchCommandOnRef());
         old.setSwitchCommandOnRef(switchDTO.getSwitchCommandOnRef().toSwitchCommandOnRef(old));
      }
      if (switchDTO.getSwitchSensorRef() != null && old.getSwitchSensorRef() != null
            && old.getSwitchSensorRef().getOid() != switchDTO.getSwitchSensorRef().getId()) {
         genericDAO.delete(old.getSwitchSensorRef());
         old.setSwitchSensorRef(switchDTO.getSwitchSensorRef().toSwitchSensorRef(old));
      }
      return old;
   }

}
