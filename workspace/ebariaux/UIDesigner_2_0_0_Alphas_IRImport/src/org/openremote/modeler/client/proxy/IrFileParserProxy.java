package org.openremote.modeler.client.proxy;

import java.util.List;

import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.irfileparser.BrandInfo;
import org.openremote.modeler.irfileparser.CodeSetInfo;
import org.openremote.modeler.irfileparser.DeviceInfo;
import org.openremote.modeler.irfileparser.GlobalCache;
import org.openremote.modeler.irfileparser.IRCommandInfo;
import org.openremote.modeler.irfileparser.IRTrans;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class IrFileParserProxy {


	private IrFileParserProxy() {
	}

	public static void loadBrands(final AsyncCallback<List<BrandInfo>> callback) {

		AsyncServiceFactory.getiRFileParserRPCServiceAsync().getBrands(
				new AsyncSuccessCallback<List<BrandInfo>>() {
					@Override
					public void onSuccess(List<BrandInfo> result) {
						callback.onSuccess(result);
					}
				});
	}

	public static void loadModels(BrandInfo brand,
			final AsyncCallback<List<DeviceInfo>> callback) {

		AsyncServiceFactory.getiRFileParserRPCServiceAsync().getDevices(brand,
				new AsyncSuccessCallback<List<DeviceInfo>>() {

					@Override
					public void onSuccess(List<DeviceInfo> result) {
						callback.onSuccess(result);

					}
				});

	}

	public static void loadCodeSets(DeviceInfo device,
			final AsyncCallback<List<CodeSetInfo>> callback) {
		AsyncServiceFactory.getiRFileParserRPCServiceAsync().getCodeSets(
				device, new AsyncSuccessCallback<List<CodeSetInfo>>() {

					@Override
					public void onSuccess(List<CodeSetInfo> result) {
						callback.onSuccess(result);
					}
				});
	}
	
	public static void loadIRCommands(CodeSetInfo codeSet, final AsyncCallback<List<IRCommandInfo>> callback){
		
		AsyncServiceFactory.getiRFileParserRPCServiceAsync().getIRCommands(codeSet, new AsyncSuccessCallback<List<IRCommandInfo>>() {
			
			
			

			@Override
			public void onSuccess(List<IRCommandInfo> result) {
				callback.onSuccess(result);				
			}
		});
	}

	public static void saveCommands(
			Device device, final List<IRCommandInfo> selectedFunctions,
			GlobalCache globalCache, IRTrans irTrans, final AsyncSuccessCallback<List<BeanModel>> callback) {
		AsyncServiceFactory.getiRFileParserRPCServiceAsync().saveCommands(device,globalCache,irTrans,selectedFunctions, new AsyncSuccessCallback<List<DeviceCommand>>() {

			@Override
			public void onSuccess(List<DeviceCommand> deviceCommands) {
	            List<BeanModel> deviceCommandModels = DeviceCommand.createModels(deviceCommands);
	            BeanModelDataBase.deviceCommandTable.insertAll(deviceCommandModels);
	            callback.onSuccess(deviceCommandModels);
			}
			/*@Override
			public void onFailure(Throwable caught) {
				
				super.onFailure(caught);
				Window.alert("erreur..."+caught.getMessage()+caught.getCause());
				System.out.println("erreur..."+caught.getMessage()+caught.getCause());
			}*/
		});
		
	}
	
	
}
