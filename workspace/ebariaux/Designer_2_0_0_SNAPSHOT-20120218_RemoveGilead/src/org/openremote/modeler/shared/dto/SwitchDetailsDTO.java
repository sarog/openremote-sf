package org.openremote.modeler.shared.dto;

public class SwitchDetailsDTO implements DTO {

  private static final long serialVersionUID = 1L;
  
  private long oid;
  private String name;
  private long sensorId;
  private long onCommandId;
  private String onCommandDisplayName;
  private long offCommandId;
  private String offCommandDisplayName;

  public SwitchDetailsDTO() {
    super();
  }

  public SwitchDetailsDTO(long oid, String name, long sensorId, long onCommandId, String onCommandDisplayName, long offCommandId, String offCommandDisplayName) {
    super();
    this.oid = oid;
    this.name = name;
    this.sensorId = sensorId;
    this.onCommandId = onCommandId;
    this.onCommandDisplayName = onCommandDisplayName;
    this.offCommandId = offCommandId;
    this.offCommandDisplayName = offCommandDisplayName;
  }

  public long getOid() {
    return oid;
  }

  public void setOid(long oid) {
    this.oid = oid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getSensorId() {
    return sensorId;
  }

  public void setSensorId(long sensorId) {
    this.sensorId = sensorId;
  }

  public long getOnCommandId() {
    return onCommandId;
  }

  public void setOnCommandId(long onCommandId) {
    this.onCommandId = onCommandId;
  }

  public String getOnCommandDisplayName() {
    return onCommandDisplayName;
  }

  public void setOnCommandDisplayName(String onCommandDisplayName) {
    this.onCommandDisplayName = onCommandDisplayName;
  }

  public long getOffCommandId() {
    return offCommandId;
  }

  public void setOffCommandId(long offCommandId) {
    this.offCommandId = offCommandId;
  }

  public String getOffCommandDisplayName() {
    return offCommandDisplayName;
  }

  public void setOffCommandDisplayName(String offCommandDisplayName) {
    this.offCommandDisplayName = offCommandDisplayName;
  }

}
