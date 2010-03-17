/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.modeler.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * The Class DeviceCommand.
 * 
 * @author Dan 2009-7-6
 */
@Entity
@Table(name = "device_command")
public class DeviceCommand extends BusinessEntity {
   
   private static final long serialVersionUID = -3654650649337382535L;

   /** The device. */
   private Device device;
   
   /** The protocol. */
   private Protocol protocol;
   
   /** The name. */
   private String name;
   
   /** The section id. */
   private String sectionId;

   /**
    * Gets the name.
    * 
    * @return the name
    */
   @Column(nullable = false)
   public String getName() {
      return name;
   }

   /**
    * Sets the name.
    * 
    * @param name the new name
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * Gets the device.
    * 
    * @return the device
    */
   @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.MERGE })
   public Device getDevice() {
      return device;
   }

   /**
    * Sets the device.
    * 
    * @param device the new device
    */
   public void setDevice(Device device) {
      this.device = device;
   }
   
   /**
    * Gets the protocol.
    * 
    * @return the protocol
    */
   @OneToOne(cascade = CascadeType.ALL ,fetch = FetchType.EAGER)
   @JoinColumn(nullable = false)
   public Protocol getProtocol() {
      return protocol;
   }

   /**
    * Sets the protocol.
    * 
    * @param protocol the new protocol
    */
   public void setProtocol(Protocol protocol) {
      this.protocol = protocol;
   }

   /* (non-Javadoc)
    * @see org.openremote.modeler.domain.BusinessEntity#getDisplayName()
    */
   @Override
   @Transient
   public String getDisplayName() {
      return getName();
   }


   /**
    * Sets the section id.
    * 
    * @param sectionId the new section id
    */
   public void setSectionId(String sectionId) {
      this.sectionId = sectionId;
   }

   /**
    * Gets the section id.
    * 
    * @return the section id
    */
   public String getSectionId() {
      return sectionId;
   }
   @Transient
   public DeviceCommandRef getDeviceCommandRef() {
      DeviceCommandRef cmdRef = new DeviceCommandRef(this);
      return cmdRef;
   }

   @Override
   public int hashCode() {
      /*final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      return result ^ 0xFFFF + (int) getOid();*/
      return (int) getOid();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      DeviceCommand other = (DeviceCommand) obj;
      if (name == null) {
         if (other.name != null) return false;
      } else if (!name.equals(other.name)) return false;
      if (this.device!=null && other.device != null) {
         if (!this.device.equals(other.device)){
            return false;
         }
      }
      return other.getOid() == getOid();
   }
   
   
}
