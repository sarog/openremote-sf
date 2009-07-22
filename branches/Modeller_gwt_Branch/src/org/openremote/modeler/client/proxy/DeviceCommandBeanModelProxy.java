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
package org.openremote.modeler.client.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.DeviceCommandWindow;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.ProtocolAttr;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;

/**
 * The Class DeviceCommandBeanModelProxy.
 */
public class DeviceCommandBeanModelProxy {
   
   /**
    * Save device command.
    * 
    * @param device the device
    * @param map the map
    * @param callback the callback
    */
   public static void saveDeviceCommand(Device device, Map<String, String> map, final AsyncSuccessCallback<BeanModel> callback){
      DeviceCommand deviceCommand = new DeviceCommand();
      deviceCommand.setName(map.get(DeviceCommandWindow.DEVICE_COMMAND_NAME));
      deviceCommand.setDevice(device);
      
      Protocol protocol = new Protocol();
      protocol.setType(map.get(DeviceCommandWindow.DEVICE_COMMAND_PROTOCOL));
      protocol.setDeviceCommand(deviceCommand);
      
      for (String key : map.keySet()) {
         if (DeviceCommandWindow.DEVICE_COMMAND_NAME.equals(key) || DeviceCommandWindow.DEVICE_COMMAND_PROTOCOL.equals(key)) {
            continue;
         }
         ProtocolAttr protocolAttr = new ProtocolAttr();
         protocolAttr.setName(key);
         protocolAttr.setValue((map.get(key)));
         protocolAttr.setProtocol(protocol);
         protocol.getAttributes().add(protocolAttr);
      }
      
      deviceCommand.setProtocol(protocol);
      device.getDeviceCommands().add(deviceCommand);
      
      AsyncServiceFactory.getDeviceCommandServiceAsync().save(deviceCommand, new AsyncSuccessCallback<DeviceCommand>() {
         public void onSuccess(DeviceCommand deviceCommand) {
            callback.onSuccess(deviceCommand.getBeanModel());
         }
      });
   }
   
   /**
    * Update device command.
    * 
    * @param deviceCommand the device command
    * @param map the map
    * @param callback the callback
    */
   public static void updateDeviceCommand(final DeviceCommand deviceCommand, Map<String, String> map,
         final AsyncSuccessCallback<BeanModel> callback) {
      deviceCommand.setName(map.get(DeviceCommandWindow.DEVICE_COMMAND_NAME));
      List<ProtocolAttr> attrs = deviceCommand.getProtocol().getAttributes();
      for (int i = 0; i < attrs.size(); i++) {
         deviceCommand.getProtocol().getAttributes().get(i).setValue(map.get(attrs.get(i).getName()));
      };
      AsyncServiceFactory.getDeviceCommandServiceAsync().update(deviceCommand, new AsyncSuccessCallback<Void>() {
         public void onSuccess(Void result) {
            callback.onSuccess(deviceCommand.getBeanModel());
         }
      });
   }
   
   public static void saveAllDeviceCommands(Device device, List<ModelData> datas, final AsyncSuccessCallback<List<BeanModel>> callback){
      List<DeviceCommand> deviceCommands = new ArrayList<DeviceCommand>();
      for (ModelData m : datas) {
         Protocol protocol = new Protocol();
         protocol.setType(Constants.INFRARED_TYPE);

         ProtocolAttr nameAttr = new ProtocolAttr();
         nameAttr.setName("name");
         nameAttr.setValue(m.get("remoteName").toString());
         nameAttr.setProtocol(protocol);
         protocol.getAttributes().add(nameAttr);

         ProtocolAttr commandAttr = new ProtocolAttr();
         commandAttr.setName("command");
         commandAttr.setValue(m.get("name").toString());
         commandAttr.setProtocol(protocol);
         protocol.getAttributes().add(commandAttr);

         DeviceCommand deviceCommand = new DeviceCommand();
         deviceCommand.setDevice(device);
         deviceCommand.setProtocol(protocol);
         deviceCommand.setName(m.get("name").toString());

         protocol.setDeviceCommand(deviceCommand);

         device.getDeviceCommands().add(deviceCommand);

         deviceCommands.add(deviceCommand);
      }
      AsyncServiceFactory.getDeviceCommandServiceAsync().saveAll(deviceCommands, new AsyncSuccessCallback<List<DeviceCommand>>() {
         public void onSuccess(List<DeviceCommand> deviceCommands) {
            callback.onSuccess(DeviceCommand.createModels(deviceCommands));
         }
      });
   }
}
