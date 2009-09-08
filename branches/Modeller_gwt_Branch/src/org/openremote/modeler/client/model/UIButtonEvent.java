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
package org.openremote.modeler.client.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The Class UIButtonEvent.
 * 
 * @author handy.wang
 */
public class UIButtonEvent {

   /** The type. */
   private String type;

   /** The id. */
   private long id;

   /** The attributes. */
   private Map<String, String> protocolAttrs = new HashMap<String, String>();

   /**
    * Gets the type.
    * 
    * @return the type
    */
   public String getType() {
      return type;
   }

   /**
    * Sets the type.
    * 
    * @param type the new type
    */
   public void setType(String type) {
      this.type = type;
   }

   /**
    * Gets the id.
    * 
    * @return the id
    */
   public long getId() {
      return id;
   }

   /**
    * Sets the id.
    * 
    * @param id the new id
    */
   public void setId(long id) {
      this.id = id;
   }

   /**
    * Gets the protocol attrs.
    * 
    * @return the protocol attrs
    */
   public Map<String, String> getProtocolAttrs() {
      return protocolAttrs;
   }

   /**
    * Sets the protocol attrs.
    * 
    * @param protocolAttrs the protocol attrs
    */
   public void setProtocolAttrs(Map<String, String> protocolAttrs) {
      this.protocolAttrs = protocolAttrs;
   }
}
