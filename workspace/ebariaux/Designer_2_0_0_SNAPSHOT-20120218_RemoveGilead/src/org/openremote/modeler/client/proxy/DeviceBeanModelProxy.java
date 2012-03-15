/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2011, OpenRemote Inc.
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

import org.openremote.modeler.client.model.TreeFolderBean;
import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.shared.dto.DTOHelper;
import org.openremote.modeler.shared.dto.DeviceCommandDetailsDTO;
import org.openremote.modeler.shared.dto.DeviceDTO;
import org.openremote.modeler.shared.dto.DeviceDetailsDTO;
import org.openremote.modeler.shared.dto.DeviceWithChildrenDTO;
import org.openremote.modeler.shared.dto.SensorDTO;
import org.openremote.modeler.shared.dto.SensorDetailsDTO;
import org.openremote.modeler.shared.dto.SliderDTO;
import org.openremote.modeler.shared.dto.SliderDetailsDTO;
import org.openremote.modeler.shared.dto.SwitchDTO;
import org.openremote.modeler.shared.dto.SwitchDetailsDTO;

import com.extjs.gxt.ui.client.data.BeanModel;


/**
 * The proxy is for managing device and deviceCommand.
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
         AsyncServiceFactory.getDeviceServiceAsync().loadAllDTOs(new AsyncSuccessCallback<ArrayList<DeviceDTO>>() {
            public void onSuccess(ArrayList<DeviceDTO> result) {
              
              /*
               List<BeanModel> beanModels = Device.createModels(result);
               BeanModelDataBase.deviceTable.insertAll(beanModels);
               */
              
              List<BeanModel> beanModels = DTOHelper.createModels(result);
               callback.onSuccess(beanModels);
            }
            
         });
      } else if(beanModel.getBean() instanceof DeviceDTO){
         final List<BeanModel> beanModels = new ArrayList<BeanModel>();
         DeviceDTO device = (DeviceDTO) beanModel.getBean();
         AsyncServiceFactory.getDeviceServiceAsync().loadDeviceWithChildrenDTOById(device.getOid(), new AsyncSuccessCallback<DeviceWithChildrenDTO>() {

            @Override
            public void onSuccess(DeviceWithChildrenDTO result) {
               List<BeanModel> cmdBeanModels = DTOHelper.createModels(result.getDeviceCommands());
               List<BeanModel> sensorBeanModels = DTOHelper.createModels(result.getSensors());
               List<BeanModel> sliderBeanModels = DTOHelper.createModels(result.getSliders());
               List<BeanModel> switchBeanModels = DTOHelper.createModels(result.getSwitches());
               
               /*
               BeanModelDataBase.deviceCommandTable.insertAll(cmdBeanModels);
               BeanModelDataBase.sensorTable.insertAll(sensorBeanModels);
               BeanModelDataBase.sliderTable.insertAll(sliderBeanModels);
               BeanModelDataBase.switchTable.insertAll(switchBeanModels);
               */
               beanModels.addAll(cmdBeanModels);
               beanModels.addAll(sensorBeanModels);
               beanModels.addAll(sliderBeanModels);
               beanModels.addAll(switchBeanModels);
               callback.onSuccess(beanModels);
            }
            
         });
      }else if(beanModel.getBean() instanceof SensorDTO) {
         SensorDTO sensor = beanModel.getBean();
         List<BeanModel> sensorBenModels = new ArrayList<BeanModel>();
         sensorBenModels.add(DTOHelper.getBeanModel(sensor.getCommand()));
         callback.onSuccess(sensorBenModels);
      } else if(beanModel.getBean() instanceof SliderDTO) {
         SliderDTO slider = beanModel.getBean();
         List<BeanModel> sliderModels = new ArrayList<BeanModel>();
         sliderModels.add(DTOHelper.getBeanModel(slider.getCommand()));
         callback.onSuccess(sliderModels);
      } else if(beanModel.getBean() instanceof SwitchDTO) {
         SwitchDTO swh = beanModel.getBean();
         List<BeanModel> switchBeanModels = new ArrayList<BeanModel>();
         switchBeanModels.add(DTOHelper.getBeanModel(swh.getOnCommand()));
         switchBeanModels.add(DTOHelper.getBeanModel(swh.getOffCommand()));
         callback.onSuccess(switchBeanModels);
      }
   }
   
   public static void loadDeviceAndCommand(BeanModel beanModel, final AsyncSuccessCallback<List<BeanModel>> callback) {

     if (beanModel == null || beanModel.getBean() instanceof TreeFolderBean) {
       AsyncServiceFactory.getDeviceServiceAsync().loadAllDTOs(new AsyncSuccessCallback<ArrayList<DeviceDTO>>() {
         public void onSuccess(ArrayList<DeviceDTO> result) {
           List<BeanModel> beanModels = DTOHelper.createModels(result);
            callback.onSuccess(beanModels);
         }        
       });
     } else if(beanModel.getBean() instanceof DeviceDTO) {
       DeviceDTO device = (DeviceDTO) beanModel.getBean();
       AsyncServiceFactory.getDeviceServiceAsync().loadDeviceWithChildrenDTOById(device.getOid(), new AsyncSuccessCallback<DeviceWithChildrenDTO>() { // TODO : have method to only return commands as children

          @Override
          public void onSuccess(DeviceWithChildrenDTO result) {
             callback.onSuccess(DTOHelper.createModels(result.getDeviceCommands()));
          }
       });
      }
   }

   public static void saveNewDevice(final DeviceDetailsDTO device, final AsyncSuccessCallback<Void> callback) {
     AsyncServiceFactory.getDeviceServiceAsync().saveNewDevice(device, callback);
   }
   
   public static void saveNewDeviceWithChildren(final DeviceDetailsDTO device, final ArrayList<DeviceCommandDetailsDTO> commands,
           final ArrayList<SensorDetailsDTO> sensors, final ArrayList<SwitchDetailsDTO> switches, final ArrayList<SliderDetailsDTO> sliders, final AsyncSuccessCallback<Void> callback) {
     AsyncServiceFactory.getDeviceServiceAsync().saveNewDeviceWithChildren(device, commands, sensors, switches, sliders, callback);
   }
   
   public static void updateDeviceWithDTO(final DeviceDetailsDTO device, final AsyncSuccessCallback<Void> callback) {
     AsyncServiceFactory.getDeviceServiceAsync().updateDeviceWithDTO(device, callback);
   }
   
   /**
    * Delete device.
    * 
    * @param deviceModel the device model
    * @param callback the callback
    */
   public static void deleteDevice(BeanModel deviceModel, final AsyncSuccessCallback<Void> callback) {
      final DeviceDTO device = deviceModel.getBean();
      /*AsyncServiceFactory.getDeviceCommandServiceAsync().loadByDevice(device.getOid(), new AsyncSuccessCallback<List<DeviceCommand>>() {
         @Override
         public void onSuccess(List<DeviceCommand> result) {
            List<BeanModel> beanModels = DeviceCommand.createModels(result);
            BeanModelDataBase.deviceCommandTable.insertAll(beanModels);
            for (BeanModel beanModel : beanModels) {
               BeanModelDataBase.deviceCommandTable.delete(beanModel.<DeviceCommand> getBean().getOid());
            }*/
            AsyncServiceFactory.getDeviceServiceAsync().deleteDevice(device.getOid(), new AsyncSuccessCallback<Void>() {
               public void onSuccess(Void result) {
                 
                 /* TODO
                  //1, remove switches and sliders.
                  removeAllSwitchsForDevice(device);
                  removeAllSlidersForDevice(device);
                  //2, remove sensors. 
                  removeAllSensorsForDevice(device);
                  //3, remove device commands. 
                  removeAllDeviceCommandsForDevice(device);
                  //4, remove device
                  BeanModelDataBase.deviceTable.delete(device.getOid());
                  
                  */
                  callback.onSuccess(result);
               }
            });
      /*   }
      });*/
   }
   
   private static void removeAllSwitchsForDevice(Device device) {
      List<BeanModel> switchBeans = BeanModelDataBase.switchTable.loadAll();
      if (switchBeans !=null && switchBeans.size() >0){
         for(BeanModel swhBean : switchBeans) {
            Switch swh = swhBean.getBean();
            if(swh.getDevice().equals(device)) {
               BeanModelDataBase.switchTable.delete(swhBean);
            }
         }
      }
   }
   
   private static void removeAllSlidersForDevice(Device device) {
      List<BeanModel> sliderBeans = BeanModelDataBase.sliderTable.loadAll();
      if (sliderBeans !=null && sliderBeans.size() >0){
         for(BeanModel sldBean : sliderBeans) {
            Slider sld = sldBean.getBean();
            if(sld.getDevice().equals(device)) {
               BeanModelDataBase.sliderTable.delete(sldBean);
            }
         }
      }
   }
   
   private static void removeAllSensorsForDevice(Device device) {
      List<BeanModel> sensorBeans = BeanModelDataBase.sensorTable.loadAll();
      if (sensorBeans !=null && sensorBeans.size() >0){
         for(BeanModel sensorBean : sensorBeans) {
            Sensor sld = sensorBean.getBean();
            if(sld.getDevice().equals(device)) {
               BeanModelDataBase.sensorTable.delete(sensorBean);
            }
         }
      }
   }
   
   private static void removeAllDeviceCommandsForDevice(Device device) {
      List<BeanModel> dvcCommandBeans = BeanModelDataBase.deviceCommandTable.loadAll();
      if (dvcCommandBeans !=null && dvcCommandBeans.size() >0){
         for(BeanModel dvcBean : dvcCommandBeans) {
            DeviceCommand dvc = dvcBean.getBean();
            if(dvc.getDevice().equals(device)) {
               BeanModelDataBase.deviceCommandTable.delete(dvcBean);
            }
         }
      }
   }
   
   public static void loadDeviceDetails(DeviceDTO device, final AsyncSuccessCallback<BeanModel> callback) {
       AsyncServiceFactory.getDeviceServiceAsync().loadDeviceDetailsDTO(device.getOid(), new AsyncSuccessCallback<DeviceDetailsDTO>() {
         public void onSuccess(DeviceDetailsDTO result) {
           callback.onSuccess(DTOHelper.getBeanModel(result));
         }
       });
   }
   
}
