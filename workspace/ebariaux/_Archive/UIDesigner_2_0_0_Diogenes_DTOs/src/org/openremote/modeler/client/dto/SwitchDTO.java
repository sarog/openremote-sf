package org.openremote.modeler.client.dto;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BeanModelTag;

@SuppressWarnings("serial")
public class SwitchDTO implements Serializable, BeanModelTag {

  private String displayName;
  private String onCommandName;
  private String offCommandName;
  private String sensorName;
  private String deviceName;
  private Long oid;
  
  public SwitchDTO() {
    super();
  }

  public SwitchDTO(Long oid, String displayName, String onCommandName, String offCommandName, String sensorName, String deviceName) {
    super();
    this.oid = oid;
    this.displayName = displayName;
    this.onCommandName = onCommandName;
    this.offCommandName = offCommandName;
    this.sensorName = sensorName;
    this.deviceName = deviceName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getOnCommandName() {
    return onCommandName;
  }

  public void setOnCommandName(String onCommandName) {
    this.onCommandName = onCommandName;
  }

  public String getOffCommandName() {
    return offCommandName;
  }

  public void setOffCommandName(String offCommandName) {
    this.offCommandName = offCommandName;
  }

  public String getSensorName() {
    return sensorName;
  }

  public void setSensorName(String sensorName) {
    this.sensorName = sensorName;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }

  public Long getOid() {
    return oid;
  }

  public void setOid(Long oid) {
    this.oid = oid;
  }
  
}
