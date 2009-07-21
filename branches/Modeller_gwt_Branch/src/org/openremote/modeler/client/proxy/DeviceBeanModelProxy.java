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

import java.util.List;

import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;

import com.extjs.gxt.ui.client.data.BeanModel;

public class DeviceBeanModelProxy {
   public static void loadDevice(BeanModel beanModel, final AsyncSuccessCallback<List<BeanModel>> callback){
      if(beanModel == null){
         AsyncServiceFactory.getDeviceServiceAsync().loadAll(new AsyncSuccessCallback<List<Device>>(){
            public void onSuccess(List<Device> result) {
               callback.onSuccess(Device.createModels(result));
            }
            
         });
      }else{
         Device device = (Device)beanModel.getBean();
         AsyncServiceFactory.getDeviceCommandServiceAsync().loadByDevice(device.getOid(), new AsyncSuccessCallback<List<DeviceCommand>>(){
            @Override
            public void onSuccess(List<DeviceCommand> result) {
               callback.onSuccess(DeviceCommand.createModels(result));
            }
            
         });
      }
   }
}
