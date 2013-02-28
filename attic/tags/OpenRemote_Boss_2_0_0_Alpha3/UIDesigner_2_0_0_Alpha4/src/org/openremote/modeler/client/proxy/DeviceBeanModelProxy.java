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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.model.TreeFolderBean;
import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.buildingmodeler.DeviceInfoForm;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.Switch;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;


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
      if (beanModel == null || beanModel.getBean() instanceof TreeFolderBean) {
         AsyncServiceFactory.getDeviceServiceAsync().loadAll(new AsyncSuccessCallback<List<Device>>() {
            public void onSuccess(List<Device> result) {
               List<BeanModel> beanModels = Device.createModels(result);
               BeanModelDataBase.deviceTable.insertAll(beanModels);
               callback.onSuccess(beanModels);
            }
            
         });
      } else if(beanModel.getBean() instanceof Device){
         final List<BeanModel> beanModels = new ArrayList<BeanModel>();
         Device device = (Device) beanModel.getBean();
         AsyncServiceFactory.getDeviceServiceAsync().loadById(device.getOid(), new AsyncSuccessCallback<Device>(){

            @Override
            public void onSuccess(Device result) {
               List<BeanModel> cmdBeanModels = DeviceCommand.createModels(result.getDeviceCommands());
               List<BeanModel> sensorBeanModels = Sensor.createModels(result.getSensors());
               List<BeanModel> sliderBeanModels = Slider.createModels(result.getSliders());
               List<BeanModel> switchBeanModels = Switch.createModels(result.getSwitchs());
               
               BeanModelDataBase.deviceCommandTable.insertAll(cmdBeanModels);
               BeanModelDataBase.sensorTable.insertAll(sensorBeanModels);
               BeanModelDataBase.sliderTable.insertAll(sliderBeanModels);
               BeanModelDataBase.switchTable.insertAll(switchBeanModels);
               
               beanModels.addAll(cmdBeanModels);
               beanModels.addAll(sensorBeanModels);
               beanModels.addAll(sliderBeanModels);
               beanModels.addAll(switchBeanModels);
               callback.onSuccess(beanModels);
            }
            
         });
      }else if(beanModel.getBean() instanceof Sensor){
         Sensor sensor = beanModel.getBean();
         List<BeanModel> sensorBenModels = new ArrayList<BeanModel>();
         sensorBenModels.add(sensor.getSensorCommandRef().getBeanModel());
         callback.onSuccess(sensorBenModels);
      } else if(beanModel.getBean() instanceof Slider){
         Slider slider = beanModel.getBean();
         List<BeanModel> sliderModels = new ArrayList<BeanModel>();
         sliderModels.add(slider.getSetValueCmd().getBeanModel());
         callback.onSuccess(sliderModels);
      } else if(beanModel.getBean() instanceof Switch){
         Switch swh = beanModel.getBean();
         List<BeanModel> switchBeanModels = new ArrayList<BeanModel>();
         switchBeanModels.add(swh.getSwitchCommandOnRef().getBeanModel());
         switchBeanModels.add(swh.getSwitchCommandOffRef().getBeanModel());
         callback.onSuccess(switchBeanModels);
      }
   }
   
   public static void loadDeviceAndCommand(BeanModel beanModel, final AsyncSuccessCallback<List<BeanModel>> callback) {
      if (beanModel == null || beanModel.getBean() instanceof TreeFolderBean) {
         AsyncServiceFactory.getDeviceServiceAsync().loadAll(new AsyncSuccessCallback<List<Device>>() {
            public void onSuccess(List<Device> result) {
               List<BeanModel> beanModels = Device.createModels(result);
               BeanModelDataBase.deviceTable.insertAll(beanModels);
               callback.onSuccess(beanModels);
            }
            
         });
      } else if(beanModel.getBean() instanceof Device){
         final List<BeanModel> beanModels = new ArrayList<BeanModel>();
         Device device = (Device) beanModel.getBean();
         AsyncServiceFactory.getDeviceServiceAsync().loadById(device.getOid(), new AsyncSuccessCallback<Device>(){

            @Override
            public void onSuccess(Device result) {
               List<BeanModel> commandBeans = DeviceCommand.createModels(result.getDeviceCommands());
               beanModels.addAll(commandBeans);
               BeanModelDataBase.deviceCommandTable.insertAll(commandBeans);
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
    * Save device with commands.
    * 
    * @param device
    *           the device
    * @param datas
    *           the datas
    * @param callback
    *           the callback
    */
   public static void saveDeviceWithCommands(final Device device, List<ModelData> datas, final AsyncSuccessCallback<BeanModel> callback) {
      device.setDeviceCommands(DeviceCommandBeanModelProxy.convert2DeviceCommand(device, datas));
      AsyncServiceFactory.getDeviceServiceAsync().saveDevice(device, new AsyncSuccessCallback<Device>() {
         public void onSuccess(Device result) {
            BeanModel deviceModel = result.getBeanModel();
            BeanModelDataBase.deviceTable.insert(deviceModel);
            List<BeanModel> deviceCommandModels = DeviceCommand.createModels(device.getDeviceCommands());
            BeanModelDataBase.deviceCommandTable.insertAll(deviceCommandModels);
            callback.onSuccess(deviceModel);
         }
      });
   }
   
   /**
    * Save device.
    * 
    * @param device
    *           the device
    * @param callback
    *           the callback
    */
   public static void saveDevice(Device device, final AsyncSuccessCallback<BeanModel> callback) {
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
      device.setName(map.get(DeviceInfoForm.DEVICE_NAME));
      device.setVendor(map.get(DeviceInfoForm.DEVICE_VENDOR));
      device.setModel(map.get(DeviceInfoForm.DEVICE_MODEL));
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
