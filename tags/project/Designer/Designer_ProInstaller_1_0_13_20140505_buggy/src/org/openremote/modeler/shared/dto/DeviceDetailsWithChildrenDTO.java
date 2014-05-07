package org.openremote.modeler.shared.dto;

import java.util.ArrayList;

public class DeviceDetailsWithChildrenDTO extends DeviceDetailsDTO {

  private static final long serialVersionUID = 1L;
  
  private ArrayList<DeviceCommandDetailsDTO> deviceCommands;
  private ArrayList<SensorDetailsDTO> sensors;
  private ArrayList<SwitchDetailsDTO> switches;
  private ArrayList<SliderDetailsDTO> sliders;
  
  public DeviceDetailsWithChildrenDTO() {
    super();
  }
  
  public DeviceDetailsWithChildrenDTO(Long oid, String name, String vendor, String model) {
    super(oid, name, vendor, model);
  }

  public ArrayList<DeviceCommandDetailsDTO> getDeviceCommands() {
    return deviceCommands;
  }

  public void setDeviceCommands(ArrayList<DeviceCommandDetailsDTO> deviceCommands) {
    this.deviceCommands = deviceCommands;
  }

  public ArrayList<SensorDetailsDTO> getSensors() {
    return sensors;
  }

  public void setSensors(ArrayList<SensorDetailsDTO> sensors) {
    this.sensors = sensors;
  }

  public ArrayList<SwitchDetailsDTO> getSwitches() {
    return switches;
  }

  public void setSwitches(ArrayList<SwitchDetailsDTO> switches) {
    this.switches = switches;
  }

  public ArrayList<SliderDetailsDTO> getSliders() {
    return sliders;
  }

  public void setSliders(ArrayList<SliderDetailsDTO> sliders) {
    this.sliders = sliders;
  }

}
