package org.openremote.modeler.irfileparser;


import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;

//TODO not sure if this class will remain in this package. maybe move
// to the model package or a new one

public class DeviceInfo extends BaseModel implements IsSerializable {



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DeviceInfo() {
	}
	
	public DeviceInfo(BrandInfo brand, String modelName) {
		setBrandInfo(brand);
		setModelName(modelName);
	}

	public BrandInfo getBrandInfo() {
		return get("brandInfo");
	}

	public void setBrandInfo(BrandInfo brandInfo) {
		set("brandInfo",brandInfo);
	}

	public String getModelName() {
		return get("modelName");
	}

	public void setModelName(String modelName) {
		set("modelName",modelName);
	}
	
}
