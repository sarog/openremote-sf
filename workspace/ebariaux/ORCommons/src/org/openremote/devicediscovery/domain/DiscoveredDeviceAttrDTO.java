/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.devicediscovery.domain;

import java.io.Serializable;



/**
 * The Attribute of Device.
 * 
 * @author Marcus
 */
public class DiscoveredDeviceAttrDTO implements Serializable {

    /**
    * 
    */
   private static final long serialVersionUID = -8739483812946911347L;
  

   /** The oid. */
   private Long oid;
   
   /** The name. */
   private String name;
   
   /** The value. */
   private String value;
   
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
    * @param name the new name
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
    * @param value the new value
    */
   public void setValue(String value) {
      this.value = value;
   }

  public Long getOid()
  {
    return oid;
  }

  public void setOid(Long oid)
  {
    this.oid = oid;
  }

   

}
