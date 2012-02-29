/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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

import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.shared.dto.SliderWithInfoDTO;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;

/**
 * The Class is for lazy loading slider from server.
 */
public class SliderBeanModelTable extends BeanModelTable {

  /*
   public SliderBeanModelTable() {
      super();
      // initialize the Database. 
      SliderBeanModelProxy.loadAll(null, new AsyncSuccessCallback<List<BeanModel>>(){

         public void onSuccess(List<BeanModel> result) {
           return;
         }
         
      });
   }
   */
   
   public List<BeanModel> loadAllAsDTOs() {
     BeanModelFactory beanModelFactory = BeanModelLookup.get().getFactory(SliderWithInfoDTO.class);

      List<BeanModel> beanModelList = new ArrayList<BeanModel>();
      for (Long key : map.keySet()) {
        Slider slider = (Slider)map.get(key).getBean(); 
        beanModelList.add(beanModelFactory.createModel(new SliderWithInfoDTO(slider.getOid(), slider.getDisplayName(),
                (slider.getSetValueCmd() != null)?slider.getSetValueCmd().getDisplayName():null,
                (slider.getSliderSensorRef() != null)?slider.getSliderSensorRef().getDisplayName():null,
                slider.getDevice().getDisplayName())));
      }
      return beanModelList;
   }

}
