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

package org.openremote.modeler.client.proxy;

import org.openremote.modeler.client.utils.BeanModelTable;
import org.openremote.modeler.domain.DeviceMacroItem;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.domain.DeviceCommandRef;
import com.extjs.gxt.ui.client.data.BeanModel;

/**
 * @author allen.wei
 */
public class BeanModelDataBase {

   public static final BeanModelTable deviceMap = new BeanModelTable();
   public static final BeanModelTable deviceCommandMap = new BeanModelTable();
   public static final BeanModelTable deviceMacroMap = new BeanModelTable();
   public static final BeanModelTable deviceMacroItemMap = new BeanModelTable();


   public static BeanModel getDeviceMacroItemBeanModel(DeviceMacroItem deviceMacroItem) {
      if (deviceMacroItem instanceof DeviceMacroRef) {
         DeviceMacroRef deviceMacroRef = (DeviceMacroRef) deviceMacroItem;
         return deviceMacroMap.get(deviceMacroRef.getTargetDeviceMacro().getOid());
      }
      if (deviceMacroItem instanceof DeviceCommandRef) {
         DeviceCommandRef deviceCommandRef = (DeviceCommandRef) deviceMacroItem;
         return deviceCommandMap.get(deviceCommandRef.getDeviceCommand().getOid());
      }
      throw new IllegalArgumentException("Illegal DeviceMacroItem type which passed in");
   }
   


}
