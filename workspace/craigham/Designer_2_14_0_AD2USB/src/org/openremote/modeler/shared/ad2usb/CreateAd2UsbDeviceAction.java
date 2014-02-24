package org.openremote.modeler.shared.ad2usb;

import net.customware.gwt.dispatch.shared.Action;

public class CreateAd2UsbDeviceAction implements
		Action<CreateAd2UsbDeviceResult> {

	private String mDeviceName;
	private String mModel;

	public CreateAd2UsbDeviceAction() {
		super();
	}

	public CreateAd2UsbDeviceAction(String deviceName, String model) {
		super();
		this.mDeviceName = deviceName;
		this.mModel = model;
	}

	public String getDeviceName() {
		return mDeviceName;
	}

	public String getModel() {
		return mModel;
	}

}
