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

public class DeviceCommandDTO implements DTO, UICommandDTO {

  private static final long serialVersionUID = 1L;
  
  private String displayName;
  private String fullyQualifiedName;
  private String protocolType;
  private Long oid;

  public DeviceCommandDTO() {
    super();
  }
  
  public DeviceCommandDTO(Long oid, String displayName, String fullyQualifiedName, String protocolType) {
    super();
    this.oid = oid;
    this.displayName = displayName;
    this.fullyQualifiedName = fullyQualifiedName;
    this.protocolType = protocolType;
  }

  public String getDisplayName() {
    return displayName;
  }
  
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
  
  public Long getOid() {
    return oid;
  }
  
  public void setOid(Long oid) {
    this.oid = oid;
  }

  public String getProtocolType() {
    return protocolType;
  }

  public void setProtocolType(String protocolType) {
    this.protocolType = protocolType;
  }

  /**
   * Full name of command, including the device it belongs to.
   * 
   * @return
   */
  public String getFullyQualifiedName() {
	return fullyQualifiedName;
  }

  public void setFullyQualifiedName(String fullyQualifiedName) {
	this.fullyQualifiedName = fullyQualifiedName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
    result = prime * result + ((fullyQualifiedName == null) ? 0 : fullyQualifiedName.hashCode());
    result = prime * result + ((oid == null) ? 0 : oid.hashCode());
    result = prime * result + ((protocolType == null) ? 0 : protocolType.hashCode());
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
    DeviceCommandDTO other = (DeviceCommandDTO) obj;
    if (displayName == null) {
      if (other.displayName != null)
        return false;
    } else if (!displayName.equals(other.displayName))
      return false;
    if (fullyQualifiedName == null) {
      if (other.fullyQualifiedName != null)
        return false;
    } else if (!fullyQualifiedName.equals(other.fullyQualifiedName))
      return false;
    if (oid == null) {
      if (other.oid != null)
        return false;
    } else if (!oid.equals(other.oid))
      return false;
    if (protocolType == null) {
      if (other.protocolType != null)
        return false;
    } else if (!protocolType.equals(other.protocolType))
      return false;
    return true;
  }
  
}
