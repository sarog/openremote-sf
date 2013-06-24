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

public class SwitchWithInfoDTO implements DTO {

  private static final long serialVersionUID = 1L;

  private String displayName;
  private String onCommandName;
  private String offCommandName;
  private String sensorName;
  private String deviceName;
  private Long oid;
  
  public SwitchWithInfoDTO() {
    super();
  }

  public SwitchWithInfoDTO(Long oid, String displayName, String onCommandName, String offCommandName, String sensorName, String deviceName) {
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((deviceName == null) ? 0 : deviceName.hashCode());
    result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
    result = prime * result + ((offCommandName == null) ? 0 : offCommandName.hashCode());
    result = prime * result + ((oid == null) ? 0 : oid.hashCode());
    result = prime * result + ((onCommandName == null) ? 0 : onCommandName.hashCode());
    result = prime * result + ((sensorName == null) ? 0 : sensorName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SwitchWithInfoDTO other = (SwitchWithInfoDTO) obj;
    if (deviceName == null) {
      if (other.deviceName != null)
        return false;
    } else if (!deviceName.equals(other.deviceName))
      return false;
    if (displayName == null) {
      if (other.displayName != null)
        return false;
    } else if (!displayName.equals(other.displayName))
      return false;
    if (offCommandName == null) {
      if (other.offCommandName != null)
        return false;
    } else if (!offCommandName.equals(other.offCommandName))
      return false;
    if (oid == null) {
      if (other.oid != null)
        return false;
    } else if (!oid.equals(other.oid))
      return false;
    if (onCommandName == null) {
      if (other.onCommandName != null)
        return false;
    } else if (!onCommandName.equals(other.onCommandName))
      return false;
    if (sensorName == null) {
      if (other.sensorName != null)
        return false;
    } else if (!sensorName.equals(other.sensorName))
      return false;
    return true;
  }
  
}
