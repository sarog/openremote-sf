package org.openremote.modeler.shared.lutron;

import java.util.ArrayList;

import net.customware.gwt.dispatch.shared.Result;

import org.openremote.modeler.domain.DeviceCommand;

public class ImportLutronConfigResult implements Result {

  private String errorMessage;
  private ArrayList<DeviceCommand> deviceCommands;
  
  public ImportLutronConfigResult() {
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public ArrayList<DeviceCommand> getDeviceCommands() {
    return deviceCommands;
  }

  public void setDeviceCommands(ArrayList<DeviceCommand> deviceCommands) {
    this.deviceCommands = deviceCommands;
  }

}
