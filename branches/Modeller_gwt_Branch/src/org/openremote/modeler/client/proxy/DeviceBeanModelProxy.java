package org.openremote.modeler.client.proxy;

import java.util.List;

import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.rpc.DeviceService;
import org.openremote.modeler.client.rpc.DeviceServiceAsync;
import org.openremote.modeler.domain.Device;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.google.gwt.core.client.GWT;

public class DeviceBeanModelProxy {
   public static final DeviceServiceAsync deviceServiceAsync = GWT.create(DeviceService.class);
   
   public static void loadAll(final AsyncSuccessCallback<List<BeanModel>> callback){
      deviceServiceAsync.loadAll(new AsyncSuccessCallback<List<Device>>(){
         public void onSuccess(List<Device> result) {
            BeanModelFactory beanModelFactory = BeanModelLookup.get().getFactory(Device.class);
            callback.onSuccess(beanModelFactory.createModel(result));
         }
         
      });
   }
}
