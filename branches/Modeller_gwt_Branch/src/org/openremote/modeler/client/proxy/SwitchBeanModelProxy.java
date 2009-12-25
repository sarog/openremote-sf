package org.openremote.modeler.client.proxy;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.model.TreeFolderBean;
import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.domain.Switch;

import com.extjs.gxt.ui.client.data.BeanModel;

public class SwitchBeanModelProxy {
   
   public static void loadAll(BeanModel switchBean,final AsyncSuccessCallback<List<BeanModel>> callback){
      if (switchBean == null || switchBean.getBean() instanceof TreeFolderBean) {
         AsyncServiceFactory.getSwitchRPCServiceAsync().loadAll(new AsyncSuccessCallback<List<Switch>>() {
            @Override
            public void onSuccess(List<Switch> result) {
               List<BeanModel> switchBeanModels = Switch.createModels(result);
               BeanModelDataBase.switchTable.insertAll(switchBeanModels);
               callback.onSuccess(switchBeanModels);
            }
         });
      } else {
         Switch switchToggle = switchBean.getBean();
         List<BeanModel> commandBeanModels = new ArrayList<BeanModel>();
         commandBeanModels.add(switchToggle.getOnDeviceCommandRef().getBeanModel());
         commandBeanModels.add(switchToggle.getOffDeviceCommandRef().getBeanModel());
         
         callback.onSuccess(commandBeanModels);
      }
   }
   
   public static void delete(final BeanModel beanModel,final AsyncSuccessCallback<Void> callback) {
      if (beanModel != null && beanModel.getBean() instanceof Switch) {
         AsyncServiceFactory.getSwitchRPCServiceAsync().delete((Switch)(beanModel.getBean()),new AsyncSuccessCallback<Void>(){
            @Override
            public void onSuccess(Void result) {
               BeanModelDataBase.switchTable.delete(beanModel);
               callback.onSuccess(result);
            }
         });
      }
   }
   
   public static void save(final BeanModel beanModel){
      if (beanModel != null && beanModel.getBean() instanceof Switch) {
         AsyncServiceFactory.getSwitchRPCServiceAsync().save((Switch)(beanModel.getBean()), new AsyncSuccessCallback<Void>(){

            @Override
            public void onSuccess(Void result) {
               BeanModelDataBase.switchTable.insert(beanModel);
            }
            
         });
      }
   }
   
   public static void update(final BeanModel beanModel){
      if (beanModel != null && beanModel.getBean() instanceof Switch) {
         AsyncServiceFactory.getSwitchRPCServiceAsync().update((Switch)(beanModel.getBean()), new AsyncSuccessCallback<Void>(){

            @Override
            public void onSuccess(Void result) {
               BeanModelDataBase.switchTable.update(beanModel);
            }
            
         });
      }
   }
}
