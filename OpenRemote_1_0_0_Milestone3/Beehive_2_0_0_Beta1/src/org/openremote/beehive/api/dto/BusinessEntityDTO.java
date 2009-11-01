/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008, OpenRemote Inc.
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
package org.openremote.beehive.api.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

/**
 * Business entity class for all DTO entities with the common property oid.
 * 
 * @author allen 2009-2-17
 * 
 */
public abstract class BusinessEntityDTO implements Serializable {

   private static final long serialVersionUID = -3871334485197341321L;
   private long oid;

   @XmlElement(name = "id")
   public long getOid() {
      return oid;
   }

   public void setOid(long oid) {
      this.oid = oid;
   }
}