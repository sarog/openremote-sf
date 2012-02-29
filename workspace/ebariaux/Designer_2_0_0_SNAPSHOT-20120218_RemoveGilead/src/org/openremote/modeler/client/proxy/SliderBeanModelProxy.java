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
package org.openremote.modeler.client.proxy;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.model.TreeFolderBean;
import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.shared.dto.DTOHelper;
import org.openremote.modeler.shared.dto.SliderDTO;
import org.openremote.modeler.shared.dto.SliderDetailsDTO;

import com.extjs.gxt.ui.client.data.BeanModel;

/**
 * The proxy is for managing slider.
 */
public class SliderBeanModelProxy {
  
   private SliderBeanModelProxy() {
   }
   
   public static void delete(final BeanModel beanModel, final AsyncSuccessCallback<Void> callback) {
      if (beanModel != null && beanModel.getBean() instanceof SliderDTO) {
         AsyncServiceFactory.getSliderRPCServiceAsync().delete(((SliderDTO) (beanModel.getBean())).getOid(),
               new AsyncSuccessCallback<Void>() {
                  @Override
                  public void onSuccess(Void result) {
                     BeanModelDataBase.sliderTable.delete(beanModel);
                     callback.onSuccess(result);
                  }
               });
      }
   }
   
   public static void save(final BeanModel beanModel,final AsyncSuccessCallback<Slider>callback){
      if (beanModel != null && beanModel.getBean() instanceof Slider) {
         AsyncServiceFactory.getSliderRPCServiceAsync().save((Slider) (beanModel.getBean()),
               new AsyncSuccessCallback<Slider>() {

                  @Override
                  public void onSuccess(Slider result) {
                     BeanModelDataBase.sliderTable.insert(result.getBeanModel());
                     callback.onSuccess(result);
                  }

               });
      }
   }
   
   public static void saveSliderList(List<Slider> sliderList, final AsyncSuccessCallback<List<BeanModel>> asyncSuccessCallback) {
     AsyncServiceFactory.getSliderRPCServiceAsync().saveAll(sliderList, new AsyncSuccessCallback<List<Slider>>() {
         public void onSuccess(List<Slider> sliderList) {
            List<BeanModel> sliderModels = Slider.createModels(sliderList);
            BeanModelDataBase.sliderTable.insertAll(sliderModels);
            asyncSuccessCallback.onSuccess(sliderModels);
         }
      });
 }
   
   
   
   
   public static void loadSliderDetails(final BeanModel beanModel, final AsyncSuccessCallback<BeanModel> asyncSuccessCallback) {
     AsyncServiceFactory.getSliderRPCServiceAsync().loadSliderDetails(((SliderDTO)beanModel.getBean()).getOid(), new AsyncSuccessCallback<SliderDetailsDTO>() {
      public void onSuccess(SliderDetailsDTO result) {
        asyncSuccessCallback.onSuccess(DTOHelper.getBeanModel(result));
      }
    });
   }
   
   public static void updateSliderWithDTO(final SliderDetailsDTO sensor, AsyncSuccessCallback<Void> callback) {
     AsyncServiceFactory.getSliderRPCServiceAsync().updateSliderWithDTO(sensor, callback);
   }

   public static void saveNewSlider(final SliderDetailsDTO sensor, final long deviceId, AsyncSuccessCallback<Void> callback) {
     AsyncServiceFactory.getSliderRPCServiceAsync().saveNewSlider(sensor, deviceId, callback);
   }

}
