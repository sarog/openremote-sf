package org.openremote.modeler.irfileparser;


import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.tinsys.ir.database.IRCommand;

//TODO not sure if this class will remain in this package. maybe move
// to the model package or a new one

public class CodeSetInfo extends BaseModel implements IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CodeSetInfo() {
	}
	
	public CodeSetInfo(DeviceInfo device, String description, String category) {
		setDeviceInfo(device);
		setDescription(description);
		setCategory(category);
	}

	public void setCategory(String category) {
		set("category",category);
		
	}
	
	public String getCategory(){
		return get("category");
	}

	public void setDescription(String description) {
		set("description",description);
		
	}
	
	public String getDescription(){
		return get("description");
	}

	public void setDeviceInfo(DeviceInfo device) {
		set("deviceInfo",device);
		
	}

	public DeviceInfo getDeviceInfo() {
		return get("deviceInfo");
	}

	public void setBrandInfo(BrandInfo brandInfo) {
		set("brandInfo",brandInfo);
	}

	public List<IRCommandInfo> getIRCommandInfo(){
		return get("iRCommandInfos");
	}
	public void setIRCommandInfo(List<IRCommandInfo> iRCommandInfos){
		set("iRCommandInfos",iRCommandInfos);
	}
}
