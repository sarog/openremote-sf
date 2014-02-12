package org.openremote.modeler.shared.isy;

import java.util.ArrayList;

import org.openremote.modeler.shared.dto.DeviceDTO;

import net.customware.gwt.dispatch.shared.Result;

public class CreateISYDeviceResult implements Result {
	 private ArrayList<DeviceDTO> devices;
	  private String errorMessage;
	  
	  public CreateISYDeviceResult() {
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
