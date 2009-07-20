/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * 
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
package org.openremote.modeler.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;
import org.openremote.modeler.domain.DeviceMacroRef;

/**
 * The Class DeviceMacroItemService.
 */
public class DeviceMacroItemService extends BaseAbstractService<DeviceMacroItem> {
   
   /**
    * Delete by device command.
    * 
    * @param deviceCommand the device command
    */
   public void deleteByDeviceCommand(DeviceCommand deviceCommand){
      DetachedCriteria criteria = DetachedCriteria.forClass(DeviceCommandRef.class);
      List<DeviceCommandRef> deviceCommandRefs = genericDAO.findByDetachedCriteria(criteria.add(Restrictions.eq("deviceCommand", deviceCommand)));
      genericDAO.deleteAll(deviceCommandRefs);
   }
   
   /**
    * Delete by device macro.
    * 
    * @param targetDeviceMacro the target device macro
    */
   public void deleteByDeviceMacro(DeviceMacro targetDeviceMacro){
      DetachedCriteria criteria = DetachedCriteria.forClass(DeviceMacroRef.class);
      List<DeviceMacroRef> deviceMacroRefs = genericDAO.findByDetachedCriteria(criteria.add(Restrictions.eq("targetDeviceMacro", targetDeviceMacro)));
      genericDAO.deleteAll(deviceMacroRefs);
   }
}
