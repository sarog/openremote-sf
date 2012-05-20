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
package org.openremote.modeler.client.utils;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.proxy.SensorBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.RangeSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.State;
import org.openremote.modeler.shared.dto.SensorDTO;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;

/**
 * The Class is for lazy loading sensor from server.
 */
public class SensorBeanModelTable extends BeanModelTable {

   public SensorBeanModelTable() {
      super();
      /*
       * initialize the Database.  
       */
      SensorBeanModelProxy.loadSensor(null, new AsyncSuccessCallback<List<BeanModel>>(){

         public void onSuccess(List<BeanModel> result) {
           return;
         }
         
      });
   }
   
   public List<BeanModel> loadAllAsDTOs() {
     BeanModelFactory beanModelFactory = BeanModelLookup.get().getFactory(SensorDTO.class);

      List<BeanModel> beanModelList = new ArrayList<BeanModel>();
      for (Long key : map.keySet()) {
        Sensor sensor = (Sensor)map.get(key).getBean();
        
        
        if (sensor.getType() == SensorType.RANGE) {
          beanModelList.add(beanModelFactory.createModel(new SensorDTO(sensor.getOid(), sensor.getDisplayName(),
                  sensor.getType(), sensor.getSensorCommandRef().getDisplayName(),
                  Integer.toString(((RangeSensor)sensor).getMin()),
                  Integer.toString(((RangeSensor)sensor).getMax()), null)));
       } else if (sensor.getType() == SensorType.CUSTOM) {
          CustomSensor customSensor = (CustomSensor)sensor;
          String states = "";
          for (State state : customSensor.getStates()) {
             states = states + state.getName() + ". ";
          }
          beanModelList.add(beanModelFactory.createModel(new SensorDTO(sensor.getOid(), sensor.getDisplayName(),
                  sensor.getType(), sensor.getSensorCommandRef().getDisplayName(), null, null, states)));
       } else {
         beanModelList.add(beanModelFactory.createModel(new SensorDTO(sensor.getOid(), sensor.getDisplayName(),
                 sensor.getType(), sensor.getSensorCommandRef().getDisplayName(), null, null, null)));
       }
      }
      return beanModelList;
   }

}
