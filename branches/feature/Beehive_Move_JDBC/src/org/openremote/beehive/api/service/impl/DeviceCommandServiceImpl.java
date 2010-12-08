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

import org.hibernate.Hibernate;
import org.openremote.beehive.api.dto.modeler.DeviceCommandDTO;
import org.openremote.beehive.api.service.DeviceCommandService;
import org.openremote.beehive.domain.modeler.DeviceCommand;
import org.openremote.beehive.domain.modeler.Protocol;

public class DeviceCommandServiceImpl extends BaseAbstractService<DeviceCommand> implements DeviceCommandService {

   public DeviceCommandDTO loadDeviceCommandById(long id) {
      DeviceCommand deviceCommand = super.loadById(id);
      Hibernate.initialize(deviceCommand.getProtocol().getAttributes());
      return deviceCommand.toDTO();
   }

   public DeviceCommandDTO save(DeviceCommandDTO deviceCommandDTO) {
      
      DeviceCommand deviceCommand = deviceCommandDTO.toDeviceCommand();
      genericDAO.save(deviceCommand);
      Hibernate.initialize(deviceCommand.getProtocol().getAttributes());
      return deviceCommand.toDTO();
   }

   public Boolean deleteCommandById(long id) {
      DeviceCommand deviceCommand = loadById(id);
//      DetachedCriteria criteria = DetachedCriteria.forClass(CommandRefItem.class);
//      List<CommandRefItem> commandRefItems = genericDAO.findByDetachedCriteria(criteria.add(Restrictions.eq("deviceCommand", deviceCommand)));
//      if (commandRefItems.size() > 0) {
//         return false;
//      } else {
//         deviceMacroItemService.deleteByDeviceCommand(deviceCommand);
         genericDAO.delete(deviceCommand);
//      }
      return true;
   }

   public DeviceCommand update(DeviceCommandDTO deviceCommandDTO) {
      DeviceCommand old = loadById(deviceCommandDTO.getId());
      genericDAO.delete(old.getProtocol());
      old.setName(deviceCommandDTO.getName());
      Protocol newProtocol = deviceCommandDTO.getProtocol().toProtocol();
      newProtocol.setDeviceCommand(old);
      old.setProtocol(newProtocol);
      
      return old;
   }

}
