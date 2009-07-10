/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.modeler.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * The Class Protocol.
 * 
 * @author Dan 2009-7-6
 */
@SuppressWarnings("serial")
@Entity
public class Protocol extends BusinessEntity {
   
   /** The type. */
   private String type;
   
   /** The attributes. */
   private List<ProtocolAttr> attributes = new ArrayList<ProtocolAttr>();
   
   /** The device event. */
   private DeviceCommand deviceCommand;

   /**
    * Gets the attributes.
    * 
    * @return the attributes
    */
   @OneToMany(mappedBy = "protocol", cascade = CascadeType.ALL)
   public List<ProtocolAttr> getAttributes() {
      return attributes;
   }

   /**
    * Sets the attributes.
    * 
    * @param attributes
    *           the new attributes
    */
   public void setAttributes(List<ProtocolAttr> attributes) {
      this.attributes = attributes;
   }

   /**
    * Gets the type.
    * 
    * @return the type
    */
   @Column(nullable = false)
   public String getType() {
      return type;
   }

   /**
    * Sets the type.
    * 
    * @param type
    *           the new type
    */
   public void setType(String type) {
      this.type = type;
   }

   /**
    * Gets the device event.
    * 
    * @return the device event
    */
   @OneToOne(mappedBy="protocol")
   public DeviceCommand getDeviceCommand() {
      return deviceCommand;
   }

   /**
    * Sets the device event.
    * 
    * @param deviceCommand
    *           the new device event
    */
   public void setDeviceCommand(DeviceCommand deviceCommand) {
      this.deviceCommand = deviceCommand;
   }
   
}
