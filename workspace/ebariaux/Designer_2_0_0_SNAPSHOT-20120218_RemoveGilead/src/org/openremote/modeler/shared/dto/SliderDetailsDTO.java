package org.openremote.modeler.shared.dto;

public class SliderDetailsDTO implements DTO {

  private static final long serialVersionUID = 1L;

  private Long oid;
  private String name;
  private Long sensorId;
  private String commandName;
  private Long commandId;

  public SliderDetailsDTO() {
    super();
  }

  public SliderDetailsDTO(Long oid, String name, Long sensorId, Long commandId, String commandName) {
    super();
    this.oid = oid;
    this.name = name;
    this.sensorId = sensorId;
    this.commandId = commandId;
    this.commandName = commandName;
  }

  public Long getOid() {
    return oid;
  }

  public void setOid(Long oid) {
    this.oid = oid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getSensorId() {
    return sensorId;
  }

  public void setSensorId(Long sensorId) {
    this.sensorId = sensorId;
  }

  public Long getCommandId() {
    return commandId;
  }

  public void setCommandId(Long commandId) {
    this.commandId = commandId;
  }
  
  public String getCommandName() {
    return commandName;
  }

  public void setCommandName(String commandName) {
    this.commandName = commandName;
  }

}