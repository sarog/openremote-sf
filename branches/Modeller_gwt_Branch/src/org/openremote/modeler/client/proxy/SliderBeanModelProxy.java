package org.openremote.modeler.client.proxy;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.model.TreeFolderBean;
import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.domain.Slider;

import com.extjs.gxt.ui.client.data.BeanModel;

public class SliderBeanModelProxy {
   public static void loadAll(BeanModel  sliderBean ,final AsyncSuccessCallback<List<BeanModel>> callback){
      if (sliderBean == null || sliderBean.getBean() instanceof TreeFolderBean) {
         AsyncServiceFactory.getSliderRPCServiceAsync().loadAll(new AsyncSuccessCallback<List<Slider>>() {
            @Override
            public void onSuccess(List<Slider> result) {
               List<BeanModel> sliderModels = Slider.createModels(result);
               BeanModelDataBase.sliderTable.insertAll(sliderModels);
               callback.onSuccess(sliderModels);
            }
         });
      } else {
         Slider slider = sliderBean.getBean();
         List<BeanModel> sliderBeanModels = new ArrayList<BeanModel>();
         sliderBeanModels.add(slider.getSetValueCmd().getBeanModel());
         callback.onSuccess(sliderBeanModels);
      }
   }
   
   public static void delete(final BeanModel beanModel,final AsyncSuccessCallback<Void> callback) {
      if (beanModel != null && beanModel.getBean() instanceof Slider) {
         AsyncServiceFactory.getSliderRPCServiceAsync().delete((Slider)(beanModel.getBean()),new AsyncSuccessCallback<Void>(){
            @Override
            public void onSuccess(Void result) {
               BeanModelDataBase.sliderTable.delete(beanModel);
               callback.onSuccess(result);
            }
         });
      }
   }
   
   public static void save(final BeanModel beanModel){
      if (beanModel != null && beanModel.getBean() instanceof Slider) {
         AsyncServiceFactory.getSliderRPCServiceAsync().save((Slider)(beanModel.getBean()), new AsyncSuccessCallback<Void>(){

            @Override
            public void onSuccess(Void result) {
               BeanModelDataBase.sliderTable.insert(beanModel);
            }
            
         });
      }
   }
   
   public static void update(final BeanModel beanModel){
      if (beanModel != null && beanModel.getBean() instanceof Slider) {
         AsyncServiceFactory.getSliderRPCServiceAsync().update((Slider)(beanModel.getBean()), new AsyncSuccessCallback<Void>(){

            @Override
            public void onSuccess(Void result) {
               BeanModelDataBase.sliderTable.update(beanModel);
            }
            
         });
      }
   }
}
