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

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


/**
 * The Class Attribute.
 * 
 * @author Dan 2009-7-6
 */
@SuppressWarnings("serial")
@Entity
public class Attribute extends BusinessEntity {

   /** The name. */
   private String name;
   
   /** The value. */
   private String value;
   
   /** The protocol. */
   private Protocol protocol;
   
   /**
    * Gets the name.
    * 
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * Sets the name.
    * 
    * @param name
    *           the new name
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * Gets the value.
    * 
    * @return the value
    */
   public String getValue() {
      return value;
   }

   /**
    * Sets the value.
    * 
    * @param value
    *           the new value
    */
   public void setValue(String value) {
      this.value = value;
   }
   
   /**
    * Gets the protocol.
    * 
    * @return the protocol
    */
   @ManyToOne
   @JoinColumn(nullable = false)
   public Protocol getProtocol() {
      return protocol;
   }

   /**
    * Sets the protocol.
    * 
    * @param protocol
    *           the new protocol
    */
   public void setProtocol(Protocol protocol) {
      this.protocol = protocol;
   }
   

}
