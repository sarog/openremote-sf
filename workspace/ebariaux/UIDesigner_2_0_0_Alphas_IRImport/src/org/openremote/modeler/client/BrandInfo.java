package org.openremote.modeler.client;


import com.google.gwt.user.client.rpc.IsSerializable;

//TODO not sure if this class will remain in this package. maybe move
// to the model package or a new one

public class BrandInfo implements IsSerializable {

	private String brandName;

	public BrandInfo() {
	}
	
	public BrandInfo(String brandName) {
		this.brandName = brandName; 
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

}
