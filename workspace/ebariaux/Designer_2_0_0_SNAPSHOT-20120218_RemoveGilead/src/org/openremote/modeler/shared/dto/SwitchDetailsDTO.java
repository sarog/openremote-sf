package org.openremote.modeler.shared.dto;

public class SwitchDetailsDTO implements DTO {

  private static final long serialVersionUID = 1L;
  
  private Long oid;
  private String name;
  private Long sensorId;
  private Long onCommandId;
  private String onCommandDisplayName;
  private Long offCommandId;
  private String offCommandDisplayName;

  public SwitchDetailsDTO() {
    super();
  }

  public SwitchDetailsDTO(Long oid, String name, Long sensorId, Long onCommandId, String onCommandDisplayName, Long offCommandId, String offCommandDisplayName) {
    super();
    this.oid = oid;
    this.name = name;
    this.sensorId = sensorId;
    this.onCommandId = onCommandId;
    this.onCommandDisplayName = onCommandDisplayName;
    this.offCommandId = offCommandId;
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

  public Long getSensorId() {
    return sensorId;
  }

  public void setSensorId(Long sensorId) {
    this.sensorId = sensorId;
  }

  public Long getOnCommandId() {
    return onCommandId;
  }

  public void setOnCommandId(Long onCommandId) {
    this.onCommandId = onCommandId;
  }

  public String getOnCommandDisplayName() {
    return onCommandDisplayName;
  }

  public void setOnCommandDisplayName(String onCommandDisplayName) {
    this.onCommandDisplayName = onCommandDisplayName;
  }

  public Long getOffCommandId() {
    return offCommandId;
  }

  public void setOffCommandId(Long offCommandId) {
    this.offCommandId = offCommandId;
  }

  public String getOffCommandDisplayName() {
    return offCommandDisplayName;
  }

  public void setOffCommandDisplayName(String offCommandDisplayName) {
    this.offCommandDisplayName = offCommandDisplayName;
  }

}
