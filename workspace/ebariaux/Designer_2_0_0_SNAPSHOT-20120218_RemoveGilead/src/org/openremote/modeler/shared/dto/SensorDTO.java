/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.modeler.shared.dto;

import org.openremote.modeler.domain.SensorType;

public class SensorDTO implements DTO {

  private static final long serialVersionUID = 1L;

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
