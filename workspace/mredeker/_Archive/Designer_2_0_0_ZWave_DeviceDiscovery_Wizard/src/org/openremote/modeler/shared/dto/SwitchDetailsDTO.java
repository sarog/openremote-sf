package org.openremote.modeler.shared.dto;

public class SwitchDetailsDTO implements DTO {

  private static final long serialVersionUID = 1L;
  
  private Long oid;
  private String name;
  private DTOReference sensor;
  private DTOReference onCommand;
  private String onCommandDisplayName;
  private DTOReference offCommand;
  private String offCommandDisplayName;

  public SwitchDetailsDTO() {
    super();
  }

  public SwitchDetailsDTO(Long oid, String name, DTOReference sensor, DTOReference onCommand, String onCommandDisplayName, DTOReference offCommand, String offCommandDisplayName) {
    super();
    this.oid = oid;
    this.name = name;
    this.sensor = sensor;
    this.onCommand = onCommand;
    this.onCommandDisplayName = onCommandDisplayName;
    this.offCommand = offCommand;
    this.offCommandDisplayName = offCommandDisplayName;
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

  public DTOReference getSensor() {
    return sensor;
  }

  public void setSensor(DTOReference sensor) {
    this.sensor = sensor;
  }

  public DTOReference getOnCommand() {
    return onCommand;
  }

  public void setOnCommand(DTOReference onCommand) {
    this.onCommand = onCommand;
  }

  public DTOReference getOffCommand() {
    return offCommand;
  }

  public void setOffCommand(DTOReference offCommand) {
    this.offCommand = offCommand;
  }

  public String getOnCommandDisplayName() {
    return onCommandDisplayName;
  }

  public void setOnCommandDisplayName(String onCommandDisplayName) {
    this.onCommandDisplayName = onCommandDisplayName;
  }

  public String getOffCommandDisplayName() {
    return offCommandDisplayName;
  }

  public void setOffCommandDisplayName(String offCommandDisplayName) {
    this.offCommandDisplayName = offCommandDisplayName;
  }

}
