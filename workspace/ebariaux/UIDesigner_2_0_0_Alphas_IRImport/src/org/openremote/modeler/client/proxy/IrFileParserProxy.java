package org.openremote.modeler.client.proxy;

import java.util.List;

import org.openremote.modeler.client.BrandInfo;
import org.openremote.modeler.client.DeviceInfo;
import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class IrFileParserProxy {

	
	private IrFileParserProxy(){}
		
		
		public static void loadBrands( final AsyncCallback<List<BrandInfo>> callback) {			

				AsyncServiceFactory.getiRFileParserRPCServiceAsync().getBrands(new AsyncSuccessCallback<List<BrandInfo>>() {
					@Override
					public void onSuccess(List<BrandInfo> result) {
						callback.onSuccess(result);
					}
				});
		}
	
		public static void loadModels( BrandInfo brand, final AsyncCallback<List<DeviceInfo>> callback) {			

			AsyncServiceFactory.getiRFileParserRPCServiceAsync().getDevices(brand, new AsyncSuccessCallback<List<DeviceInfo>>() {

				@Override
				public void onSuccess(List<DeviceInfo> result) {
					callback.onSuccess(result);
					
				}
			});
	}
	
}
