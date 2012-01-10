package org.openremote.modeler.shared.lutron;

import java.io.Serializable;

@SuppressWarnings("serial")
public class OutputImportConfig implements Serializable {

  private String outputName;
  private OutputType type;
  private String address;
  private String roomName;
  private String areaName;
  
  public OutputImportConfig() {
  }

  public OutputImportConfig(String outputName, OutputType type, String address) {
    this();
    this.outputName = outputName;
    this.type = type;
    this.address = address;
  }

  public OutputImportConfig(String outputName, OutputType type, String address, String roomName, String areaName) {
    this(outputName, type, address);
    this.roomName = roomName;
    this.areaName = areaName;
  }

  public String getOutputName() {
    return outputName;
  }

  public void setOutputName(String outputName) {
    this.outputName = outputName;
  }

  public OutputType getType() {
    return type;
  }

  public void setType(OutputType type) {
    this.type = type;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getRoomName() {
    return roomName;
  }

  public void setRoomName(String roomName) {
    this.roomName = roomName;
  }

  public String getAreaName() {
    return areaName;
  }

  public void setAreaName(String areaName) {
    this.areaName = areaName;
  }

}
