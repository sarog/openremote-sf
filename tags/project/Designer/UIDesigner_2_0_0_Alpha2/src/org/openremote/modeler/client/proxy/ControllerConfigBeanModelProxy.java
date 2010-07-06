package org.openremote.modeler.client.proxy;



import java.util.Set;

import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.domain.Config;
import org.openremote.modeler.domain.ConfigCategory;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class ControllerConfigBeanModelProxy {
   
   public static void getConfigs(final ConfigCategory category,final AsyncCallback<Set<Config>> callback){
      AsyncServiceFactory.getControllerConfigPRCServiceAsync().getConfigsByCategoryForCurrentAccount(category.getName(), new AsyncSuccessCallback<Set<Config>>(){

         @Override
         public void onSuccess(Set<Config> result) {
            callback.onSuccess(result);
         }
         
      });
   }
   
   public static void saveAllConfigs(final Set<Config> configs,final AsyncCallback<Set<Config>>callback){
      AsyncServiceFactory.getControllerConfigPRCServiceAsync().saveAll(configs, new AsyncSuccessCallback<Set<Config>>(){

         @Override
         public void onSuccess(Set<Config> result) {
            callback.onSuccess(result);
         }

         @Override
         public void onFailure(Throwable caught) {
            // TODO Auto-generated method stub
            caught.printStackTrace();
            super.onFailure(caught);
         }
         
         
      });
   }
}
