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

import org.openremote.modeler.client.model.TreeFolderBean;
import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.State;

import com.extjs.gxt.ui.client.data.BeanModel;

public class SensorBeanModelProxy {

   private SensorBeanModelProxy() {
   }
   
   public static void loadSensor(BeanModel beanModel, final AsyncSuccessCallback<List<BeanModel>> callback) {
      if (beanModel == null || beanModel.getBean() instanceof TreeFolderBean) {
         AsyncServiceFactory.getSensorRPCServiceAsync().loadAll(new AsyncSuccessCallback<List<Sensor>>() {
            public void onSuccess(List<Sensor> result) {
               List<BeanModel> beanModels = Sensor.createModels(result);
               BeanModelDataBase.sensorTable.insertAll(beanModels);
               callback.onSuccess(beanModels);
            }
         });
      } else {
         Sensor sensor = (Sensor) beanModel.getBean();
         List<BeanModel> beanModels = new ArrayList<BeanModel>();
         if (sensor.getDeviceCommandRef() != null) {
            beanModels.add(sensor.getDeviceCommandRef().getBeanModel());
         }
         if (sensor instanceof CustomSensor) {
            beanModels.addAll(State.createModels(((CustomSensor)sensor).getStates()));
         }
         callback.onSuccess(beanModels);
      }
   }
   
   public static void saveSensor(Sensor sensor, final AsyncSuccessCallback<Sensor> callback) {
      AsyncServiceFactory.getSensorRPCServiceAsync().saveSensor(sensor, new AsyncSuccessCallback<Sensor>() {
         public void onSuccess(Sensor result) {
            BeanModelDataBase.sensorTable.insert(result.getBeanModel());
            callback.onSuccess(result);
         }
      });
   }
}
