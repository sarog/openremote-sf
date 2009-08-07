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

package org.openremote.modeler.client.proxy;

import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.DeviceWindow;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;

import com.extjs.gxt.ui.client.data.BeanModel;


/**
 * The Class DeviceBeanModelProxy.
 */
public class DeviceBeanModelProxy {
   
   /**
    * Not be instantiated.
    */
   private DeviceBeanModelProxy() {
   }

   /**
    * Load device.
    * 
    * @param beanModel the bean model
    * @param callback the callback
    */
   public static void loadDevice(BeanModel beanModel, final AsyncSuccessCallback<List<BeanModel>> callback) {
      if (beanModel == null) {
         AsyncServiceFactory.getDeviceServiceAsync().loadAll(new AsyncSuccessCallback<List<Device>>() {
            public void onSuccess(List<Device> result) {
               List<BeanModel> beanModels = Device.createModels(result);
               BeanModelDataBase.deviceTable.insertAll(beanModels);
               callback.onSuccess(beanModels);
            }
            
         });
      } else {
         Device device = (Device) beanModel.getBean();
         AsyncServiceFactory.getDeviceCommandServiceAsync().loadByDevice(device.getOid(), new AsyncSuccessCallback<List<DeviceCommand>>() {
            @Override
            public void onSuccess(List<DeviceCommand> result) {
               List<BeanModel> beanModels = DeviceCommand.createModels(result);
               BeanModelDataBase.deviceCommandTable.insertAll(beanModels);
               callback.onSuccess(beanModels);
            }
            
         });
      }
   }
   
   /**
    * Save device.
    * 
    * @param map the map
    * @param callback the callback
    */
   public static void saveDevice(Map<String, String> map, final AsyncSuccessCallback<BeanModel> callback) {
      Device device = new Device();
      setAttrsToDevice(map, device);
      AsyncServiceFactory.getDeviceServiceAsync().saveDevice(device, new AsyncSuccessCallback<Device>() {
         public void onSuccess(Device result) {
            BeanModel deviceModel = result.getBeanModel();
            BeanModelDataBase.deviceTable.insert(deviceModel);
            callback.onSuccess(deviceModel);
         }
      });
   }

   
   /**
    * Update device.
    * 
    * @param deviceModel the device model
    * @param map the map
    * @param callback the callback
    */
   public static void updateDevice(final BeanModel deviceModel, Map<String, String> map, final AsyncSuccessCallback<BeanModel> callback) {
      Device device = deviceModel.getBean();
      setAttrsToDevice(map, device);
      AsyncServiceFactory.getDeviceServiceAsync().updateDevice(device, new AsyncSuccessCallback<Void>() {
         public void onSuccess(Void result) {
            BeanModelDataBase.deviceTable.update(deviceModel);
            callback.onSuccess(deviceModel);
         }
      });
   }
   
   /**
    * Sets the attrs to device.
    * 
    * @param map the map
    * @param device the device
    */
   private static void setAttrsToDevice(Map<String, String> map, Device device) {
      device.setName(map.get(DeviceWindow.DEVICE_NAME));
      device.setVendor(map.get(DeviceWindow.DEVICE_VENDOR));
      device.setModel(map.get(DeviceWindow.DEVICE_MODEL));
   }
   
   /**
    * Delete device.
    * 
    * @param deviceModel the device model
    * @param callback the callback
    */
   public static void deleteDevice(BeanModel deviceModel, final AsyncSuccessCallback<Void> callback) {
      final Device device = deviceModel.getBean();
      AsyncServiceFactory.getDeviceCommandServiceAsync().loadByDevice(device.getOid(), new AsyncSuccessCallback<List<DeviceCommand>>() {
         @Override
         public void onSuccess(List<DeviceCommand> result) {
            List<BeanModel> beanModels = DeviceCommand.createModels(result);
            BeanModelDataBase.deviceCommandTable.insertAll(beanModels);
            for (BeanModel beanModel : beanModels) {
               BeanModelDataBase.deviceCommandTable.delete(beanModel.<DeviceCommand> getBean().getOid());
            }
            AsyncServiceFactory.getDeviceServiceAsync().deleteDevice(device.getOid(), new AsyncSuccessCallback<Void>() {
               public void onSuccess(Void result) {
                  BeanModelDataBase.deviceTable.delete(device.getOid());
                  callback.onSuccess(result);
               }
            });
         }
      });
   }
}
