package org.openremote.modeler.irfileparser;


import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;

//TODO not sure if this class will remain in this package. maybe move
// to the model package or a new one

public class BrandInfo extends BaseModel implements IsSerializable {



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BrandInfo() {
	}
	
	public BrandInfo(String brandName) {
		setBrandName(brandName); 
	}

	public String getBrandName() {
		return get("brandName");
	}

	public void setBrandName(String brandName) {
		set("brandName",brandName);
	}

}
