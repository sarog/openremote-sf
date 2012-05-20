package org.openremote.modeler.shared.russound;

import java.util.ArrayList;

import net.customware.gwt.dispatch.shared.Result;

import org.openremote.modeler.domain.Device;

public class CreateRussoundDeviceResult implements Result {

  private ArrayList<Device> devices;

  public CreateRussoundDeviceResult() {
    super();
  }

  public ArrayList<Device> getDevices() {
    return devices;
  }

  public void setDevices(ArrayList<Device> devices) {
    this.devices = devices;
  }
  
  // TODO: should hold  potential error message
}
