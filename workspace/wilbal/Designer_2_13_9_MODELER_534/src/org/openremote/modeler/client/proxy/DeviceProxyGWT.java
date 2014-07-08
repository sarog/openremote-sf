/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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
import org.openremote.modeler.shared.dto.DTO;
import org.openremote.modeler.shared.dto.DTOHelper;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
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
public class DeviceProxyGWT {
   
   /**
    * Not be instantiated.
    */
   private DeviceProxyGWT() {
   }

   /**
    * Load device.
    * 
    * @param beanModel the bean model
    * @param callback the callback
    */
   public static void loadDevice( final AsyncSuccessCallback<ArrayList<DeviceDTO>> callback) {
         AsyncServiceFactory.getDeviceServiceAsync().loadAllDTOs(new AsyncSuccessCallback<ArrayList<DeviceDTO>>() {
            public void onSuccess(ArrayList<DeviceDTO> result) {
              
              /*
               List<BeanModel> beanModels = Device.createModels(result);
               BeanModelDataBase.deviceTable.insertAll(beanModels);
               */
              
               callback.onSuccess(result);
            }
            
         });
      } 
   public static void loadDevice( DeviceDTO dto,final AsyncSuccessCallback<ArrayList<DeviceCommandDTO>> callback) {
         AsyncServiceFactory.getDeviceServiceAsync().loadDeviceWithChildrenDTOById(((DeviceDTO)dto).getOid(), new AsyncSuccessCallback<DeviceWithChildrenDTO>() {

            @Override
            public void onSuccess(DeviceWithChildrenDTO result) {
               callback.onSuccess(result.getDeviceCommands());
            }
            
         });
      }
   public static void loadDevice( SensorDTO dto,final AsyncSuccessCallback<DeviceCommandDTO> callback) {
         callback.onSuccess(dto.getCommand());
      } 
   public static void loadDevice( SliderDTO dto,final AsyncSuccessCallback<DeviceCommandDTO> callback) {
         callback.onSuccess(dto.getCommand());
      } 
   
   public static void loadDevice( SwitchDTO dto,final AsyncSuccessCallback<List<DeviceCommandDTO>> callback) {

         List<DeviceCommandDTO> switchCommands = new ArrayList<DeviceCommandDTO>();
         switchCommands.add(dto.getOnCommand());
         switchCommands.add(dto.getOffCommand());
         callback.onSuccess(switchCommands);
   }
   
   public static void loadDeviceAndCommand(final Object o,final AsyncSuccessCallback<List<DeviceDTO>> callback) {

       AsyncServiceFactory.getDeviceServiceAsync().loadAllDTOs(new AsyncSuccessCallback<ArrayList<DeviceDTO>>() {
         public void onSuccess(ArrayList<DeviceDTO> result) {
            callback.onSuccess(result);
         }        
       });
     
   }

   public static void saveNewDevice(final DeviceDetailsDTO device, final AsyncSuccessCallback<DeviceDTO> callback) {
     AsyncServiceFactory.getDeviceServiceAsync().saveNewDevice(device, callback);
   }
   
   public static void saveNewDeviceWithChildren(final DeviceDetailsDTO device, final ArrayList<DeviceCommandDetailsDTO> commands,
           final ArrayList<SensorDetailsDTO> sensors, final ArrayList<SwitchDetailsDTO> switches, final ArrayList<SliderDetailsDTO> sliders, final AsyncSuccessCallback<DeviceDTO> callback) {
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
            AsyncServiceFactory.getDeviceServiceAsync().deleteDevice(device.getOid(), callback);
   }

   public static void loadDeviceDetails(DeviceDTO device, final AsyncSuccessCallback<BeanModel> callback) {
       AsyncServiceFactory.getDeviceServiceAsync().loadDeviceDetailsDTO(device.getOid(), new AsyncSuccessCallback<DeviceDetailsDTO>() {
         public void onSuccess(DeviceDetailsDTO result) {
           callback.onSuccess(DTOHelper.getBeanModel(result));
         }
       });
   }
   
}
