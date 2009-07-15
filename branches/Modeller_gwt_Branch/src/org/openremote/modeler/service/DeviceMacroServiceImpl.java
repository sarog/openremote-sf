/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */
package org.openremote.modeler.service;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Hibernate;
import org.openremote.modeler.client.rpc.DeviceMacroService;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;

// TODO: Auto-generated Javadoc
/**
 * The Class DeviceMacroServiceImpl.
 */
public class DeviceMacroServiceImpl extends BaseAbstractService<DeviceMacro> implements DeviceMacroService {

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
    * @see org.openremote.modeler.client.rpc.DeviceMacroService#save(org.openremote.modeler.domain.DeviceMacro)
    */
   public DeviceMacro saveDeviceMacro(DeviceMacro deviceMacro) {
      genericDAO.save(deviceMacro);
      return deviceMacro;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.openremote.modeler.client.rpc.DeviceMacroService#edit(org.openremote.modeler.domain.DeviceMacro)
    */
   public DeviceMacro updateDeviceMacro(DeviceMacro deviceMacro) {
      DeviceMacro old = genericDAO.loadById(DeviceMacro.class, deviceMacro.getOid());
      genericDAO.deleteAll(old.getDeviceMacroItems());
      old.getDeviceMacroItems().clear();

      old.setName(deviceMacro.getName());
      for (DeviceMacroItem deviceMacroItem : deviceMacro.getDeviceMacroItems()) {
         deviceMacroItem.setOid(0);
      }
      old.getDeviceMacroItems().addAll(deviceMacro.getDeviceMacroItems());
      return old;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.openremote.modeler.client.rpc.DeviceMacroService#deleteDeviceMacro(long)
    */
   public void deleteDeviceMacro(long id) {
      DeviceMacro deviceMacro = loadById(id);
      genericDAO.delete(deviceMacro);
   }

}
