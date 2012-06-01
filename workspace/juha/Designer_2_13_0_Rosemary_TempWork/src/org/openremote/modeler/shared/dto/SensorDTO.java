package org.openremote.modeler.shared.dto;

import org.openremote.modeler.domain.SensorType;

public class SensorDTO implements DTO {

  private static final long serialVersionUID = 1L;
  
  private String displayName;
  private long oid;
  private SensorType type;
  private DeviceCommandDTO command;

  public SensorDTO() {
    super();
  }
  
  public SensorDTO(long oid, String displayName, SensorType type) {
    super();
    this.oid = oid;
    this.displayName = displayName;
    this.type = type;
  }

  public String getDisplayName() {
    return displayName;
  }
  
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
  
  public long getOid() {
    return oid;
  }
  
  public void setOid(long oid) {
    this.oid = oid;
  }
  
  public SensorType getType() {
    return type;
  }

  public void setType(SensorType type) {
    this.type = type;
  }

  public DeviceCommandDTO getCommand() {
    return command;
  }

  public void setCommand(DeviceCommandDTO command) {
    this.command = command;
  }

}