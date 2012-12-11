package org.openremote.modeler.shared.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
  
  /**
   * SensorDetailsDTOs, SwitchDetailsDTOs and SliderDetailsDTOs use DTOReference objects
   * to reference each other and the DeviceCommandDetailsDTOs.
   * 
   * Those DTOReference can either use an id or point to the DTO instance itself.
   * This method will walk the hierarchy, ensuring that all references using ids
   * are replaced with a reference to the DTO instance.
   * 
   * This is useful when this object comes from DB, where ids are used for relationship.
   */
  public void replaceIdWithDTOInReferences() {
    Map<Long, DeviceCommandDetailsDTO> commandsPerId = new HashMap<Long, DeviceCommandDetailsDTO>();
    if (getDeviceCommands() != null) {
      for (DeviceCommandDetailsDTO dc : getDeviceCommands()) {
        commandsPerId.put(dc.getOid(), dc);
      }
    }
    
    Map<Long, SensorDetailsDTO> sensorsPerId = new HashMap<Long, SensorDetailsDTO>();
    if (getSensors() != null) {
      for (SensorDetailsDTO s : getSensors()) {
        DTOReference ref = s.getCommand();
        if (ref != null) {
          ref.resolveIdToDTO(commandsPerId);
        }
        sensorsPerId.put(s.getOid(), s);
      }
    }
    
    if (getSwitches() != null) {
      for (SwitchDetailsDTO s : getSwitches()) {
        DTOReference ref = s.getSensor();
        if (ref != null) {
          ref.resolveIdToDTO(sensorsPerId);
        }
        ref = s.getOnCommand();
        if (ref != null) {
          ref.resolveIdToDTO(commandsPerId);
        }
        ref = s.getOffCommand();
        if (ref != null) {
          ref.resolveIdToDTO(commandsPerId);
        }
      }
    }
    
    if (getSliders() != null) {
      for (SliderDetailsDTO s : getSliders()) {
        DTOReference ref = s.getCommand();
        if (ref != null) {
          ref.resolveIdToDTO(commandsPerId);
        }
        ref = s.getSensor();
        if (ref != null) {
          ref.resolveIdToDTO(sensorsPerId);
        }
      }
    }
  }

}
