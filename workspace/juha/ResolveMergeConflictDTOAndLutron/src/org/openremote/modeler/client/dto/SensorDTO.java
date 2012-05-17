package org.openremote.modeler.client.dto;

import java.io.Serializable;

import org.openremote.modeler.domain.SensorType;

import com.extjs.gxt.ui.client.data.BeanModelTag;

@SuppressWarnings("serial")
public class SensorDTO implements Serializable, BeanModelTag {

  private String displayName;
  private SensorType type;
  private String commandName;
  private String minValue;
  private String maxValue;
  private String statesInfo;
  private Long oid;
  
  public SensorDTO() {
    super();
  }

  public SensorDTO(Long oid, String displayName, SensorType type, String commandName, String minValue, String maxValue, String statesInfo) {
    super();
    this.oid = oid;
    this.displayName = displayName;
    this.type = type;
    this.commandName = commandName;
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.statesInfo = statesInfo;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public SensorType getType() {
    return type;
  }

  public void setType(SensorType type) {
    this.type = type;
  }

  public String getCommandName() {
    return commandName;
  }

  public void setCommandName(String commandName) {
    this.commandName = commandName;
  }

  public String getMinValue() {
    return minValue;
  }

  public void setMinValue(String minValue) {
    this.minValue = minValue;
  }

  public String getMaxValue() {
    return maxValue;
  }

  public void setMaxValue(String maxValue) {
    this.maxValue = maxValue;
  }

  public String getStatesInfo() {
    return statesInfo;
  }

  public void setStatesInfo(String statesInfo) {
    this.statesInfo = statesInfo;
  }

  public Long getOid() {
    return oid;
  }

  public void setOid(Long oid) {
    this.oid = oid;
  }
  
}
