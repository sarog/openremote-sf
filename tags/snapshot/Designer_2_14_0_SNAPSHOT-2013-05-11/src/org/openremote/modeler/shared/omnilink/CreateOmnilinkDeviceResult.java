package org.openremote.modeler.shared.omnilink;

import java.util.ArrayList;

import net.customware.gwt.dispatch.shared.Result;

import org.openremote.modeler.shared.dto.DeviceDTO;

public class CreateOmnilinkDeviceResult implements Result {

  private ArrayList<DeviceDTO> devices;
  private String errorMessage;
  
  public CreateOmnilinkDeviceResult() {
    super();
  }

  public ArrayList<DeviceDTO> getDevices() {
    return devices;
  }

  public void setDevices(ArrayList<DeviceDTO> devices) {
    this.devices = devices;
  }

	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
  
}
