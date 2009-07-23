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

import com.extjs.gxt.ui.client.data.BeanModel;
import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class DeviceMacroBeanModelProxy {

   public static void loadDeviceMaro(BeanModel deviceMacroBeanModel, final AsyncSuccessCallback<List<BeanModel>> callback) {
      if (deviceMacroBeanModel == null) {
         AsyncServiceFactory.getDeviceMacroServiceAsync().loadAll(new AsyncSuccessCallback<List<DeviceMacro>>() {

            public void onSuccess(List<DeviceMacro> result) {
               List<BeanModel> beanModels = DeviceMacro.createModels(result);
               BeanModelDataBase.deviceMacroMap.insertAll(beanModels);
               callback.onSuccess(beanModels);
            }
         });
      } else {
         AsyncServiceFactory.getDeviceMacroServiceAsync().loadDeviceMacroItems((DeviceMacro) deviceMacroBeanModel.getBean(), new AsyncSuccessCallback<List<DeviceMacroItem>>() {
            public void onSuccess(List<DeviceMacroItem> result) {
               List<BeanModel> beanModels = DeviceMacroItem.createModels(result);
               BeanModelDataBase.deviceMacroItemMap.insertAll(beanModels);
               callback.onSuccess(beanModels);
            }
         });
      }

   }

   public static void saveDeviceMacro(String deviceMacroName, List<BeanModel> items, final AsyncSuccessCallback<DeviceMacro> callback) {
      DeviceMacro deviceMacro = new DeviceMacro();
      deviceMacro.setName(deviceMacroName);
      deviceMacro.getDeviceMacroItems().addAll(convertMacroItemRef(items,deviceMacro));

      AsyncServiceFactory.getDeviceMacroServiceAsync().saveDeviceMacro(deviceMacro, new AsyncSuccessCallback<DeviceMacro>() {
         @Override
         public void onSuccess(DeviceMacro result) {
            BeanModelDataBase.deviceMacroMap.insert(result.getBeanModel());
            BeanModelDataBase.deviceMacroItemMap.insertAll(DeviceMacroItem.createModels(result.getDeviceMacroItems()));
            callback.onSuccess(result);
         }
      });
   }

   public static List<DeviceMacroItem> convertMacroItemRef(List<BeanModel> beanModels,DeviceMacro deviceMacro) {
      List<DeviceMacroItem> deviceMacroItems = new ArrayList<DeviceMacroItem>();
      for (BeanModel beanModel : beanModels) {
         if (beanModel.getBean() instanceof DeviceCommand) {
            DeviceCommand deviceCommand = (DeviceCommand) beanModel.getBean();
            DeviceCommandRef deviceCommandRef = new DeviceCommandRef(deviceCommand);
            deviceCommandRef.setParentDeviceMacro(deviceMacro);
            deviceMacroItems.add(deviceCommandRef);
         } else if (beanModel.getBean() instanceof DeviceMacro) {
            DeviceMacro macro = (DeviceMacro) beanModel.getBean();
            DeviceMacroRef deviceMacroRef = new DeviceMacroRef(macro);
            deviceMacroRef.setParentDeviceMacro(deviceMacro);
            deviceMacroItems.add(deviceMacroRef);
         }
      }
      return deviceMacroItems;
   }

   public static void updateDeviceMacro(final DeviceMacro deviceMacro, List<BeanModel> items, final AsyncSuccessCallback<DeviceMacro> callback) {
      Iterator<DeviceMacroItem> macroItemIterator = deviceMacro.getDeviceMacroItems().iterator();
      while(macroItemIterator.hasNext()) {
         DeviceMacroItem deviceMacroItem = macroItemIterator.next();
         BeanModelDataBase.deviceMacroItemMap.delete(deviceMacroItem.getOid());
         macroItemIterator.remove();
      }
      deviceMacro.getDeviceMacroItems().addAll(convertMacroItemRef(items,deviceMacro));
      AsyncServiceFactory.getDeviceMacroServiceAsync().updateDeviceMacro(deviceMacro, new AsyncSuccessCallback<DeviceMacro>() {
         @Override
         public void onSuccess(DeviceMacro result) {
            BeanModelDataBase.deviceMacroMap.update(deviceMacro.getBeanModel());
            for (DeviceMacroItem deviceMacroItem : result.getDeviceMacroItems()) {
               BeanModelDataBase.deviceMacroItemMap.insert(deviceMacroItem.getBeanModel());
            }
            callback.onSuccess(result);
         }
      });
   }


   public static void deleteDeviceMacro(final BeanModel deviceMacroBeanModel, final AsyncSuccessCallback<Void> callback) {
      DeviceMacro deviceMacro = deviceMacroBeanModel.getBean();
      AsyncServiceFactory.getDeviceMacroServiceAsync().deleteDeviceMacro(deviceMacro.getOid(), new AsyncSuccessCallback<Void>() {
         @Override
         public void onSuccess(Void result) {
            BeanModelDataBase.deviceMacroMap.delete(deviceMacroBeanModel);
            callback.onSuccess(result);
         }
      });
   }
}
