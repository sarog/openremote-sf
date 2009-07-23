/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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

/**
 * The Class DeviceMacroServiceImpl.
 */
public class DeviceMacroServiceImpl extends BaseAbstractService<DeviceMacro> implements DeviceMacroService {

   private DeviceMacroItemService deviceMacroItemService;
   private UserService userService;


   public void setDeviceMacroItemService(DeviceMacroItemService deviceMacroItemService) {
      this.deviceMacroItemService = deviceMacroItemService;
   }
   
   

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
