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

import org.hibernate.Hibernate;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.DeviceMacroItemService;
import org.openremote.modeler.service.DeviceMacroService;
import org.openremote.modeler.service.UserService;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class DeviceMacroServiceImpl.
 */
public class DeviceMacroServiceImpl extends BaseAbstractService<DeviceMacro> implements DeviceMacroService {

   /** The device macro item service. */
   private DeviceMacroItemService deviceMacroItemService;
   
   /** The user service. */
   private UserService userService;


   /**
    * Sets the device macro item service.
    * 
    * @param deviceMacroItemService the new device macro item service
    */
   public void setDeviceMacroItemService(DeviceMacroItemService deviceMacroItemService) {
      this.deviceMacroItemService = deviceMacroItemService;
   }
   
   

   /**
    * Sets the user service.
    * 
    * @param userService the new user service
    */
   public void setUserService(UserService userService) {
      this.userService = userService;
   }



   /*
    * (non-Javadoc)
    * 
    * @see org.openremote.modeler.service.BaseAbstractService#loadAll()
    */
   public List<DeviceMacro> loadAll() {
      List<DeviceMacro> list = super.loadAll();
      for (DeviceMacro deviceMacro : list) {
         Hibernate.initialize(deviceMacro.getDeviceMacroItems());
      }
      return list;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.openremote.modeler.client.rpc.DeviceMacroRPCService#save(org.openremote.modeler.domain.DeviceMacro)
    */
   public DeviceMacro saveDeviceMacro(DeviceMacro deviceMacro) {
      deviceMacro.setAccount(userService.getAccount());
      genericDAO.save(deviceMacro);
      return deviceMacro;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.openremote.modeler.client.rpc.DeviceMacroRPCService#edit(org.openremote.modeler.domain.DeviceMacro)
    */
   public DeviceMacro updateDeviceMacro(DeviceMacro deviceMacro) {
      DeviceMacro old = genericDAO.loadById(DeviceMacro.class, deviceMacro.getOid());
      if (old.getAccount() == null) {
         old.setAccount(userService.getAccount());
      }
      genericDAO.deleteAll(old.getDeviceMacroItems());
      old.getDeviceMacroItems().clear();

      old.setName(deviceMacro.getName());
      for (DeviceMacroItem deviceMacroItem : deviceMacro.getDeviceMacroItems()) {
         deviceMacroItem.setOid(0);
         deviceMacroItem.setParentDeviceMacro(old);
      }
      old.getDeviceMacroItems().addAll(deviceMacro.getDeviceMacroItems());
      return old;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.openremote.modeler.client.rpc.DeviceMacroRPCService#deleteDeviceMacro(long)
    */
   public void deleteDeviceMacro(long id) {
      DeviceMacro deviceMacro = loadById(id);
      deviceMacroItemService.deleteByDeviceMacro(deviceMacro);
      genericDAO.delete(deviceMacro);
   }


}
