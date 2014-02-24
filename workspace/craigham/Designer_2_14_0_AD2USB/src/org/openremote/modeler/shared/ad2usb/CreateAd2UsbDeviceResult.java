package org.openremote.modeler.shared.ad2usb;

import java.util.ArrayList;

import net.customware.gwt.dispatch.shared.Result;

import org.openremote.modeler.shared.dto.DeviceDTO;

public class CreateAd2UsbDeviceResult implements Result {

	private ArrayList<DeviceDTO> devices;

	public CreateAd2UsbDeviceResult() {
		super();
	}

	public ArrayList<DeviceDTO> getDevices() {
		return devices;
	}

	public void setDevices(ArrayList<DeviceDTO> devices) {
		this.devices = devices;
	}

	// TODO: should hold potential error message
}