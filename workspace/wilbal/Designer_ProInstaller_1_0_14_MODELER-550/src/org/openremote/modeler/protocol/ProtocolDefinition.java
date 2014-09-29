/* OpenRemote, the Home of the Digital Home.
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

package org.openremote.modeler.protocol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class defines a protocol with its properties. A protocol has display name, tag name, allowed account
 * and some attributes.
 * 
 * The protocol xml segment structure is similar following:</br>
 * 
 * &lt;protocol displayName="Infrared" tagName="ir" allowedAccountIds="account1,account2"&gt;</br>
      &lt;attr name="name" label="Name"&gt;</br>
         &lt;validations&gt;</br>
            &lt;allowBlank&gt;false&lt;/allowBlank&gt;</br>
         &lt;/validations&gt;</br>
      &lt;/attr>
      &lt;attr name="command" label="IR Command"&gt;</br>
         &lt;validations&gt;</br>
            &lt;allowBlank&gt;false&lt;/allowBlank&gt;</br>
            &lt;maxLength&gt;10&lt;/maxLength&gt;</br>
            &lt;regex message="Command is necessary"&gt;\w+&lt;/regex&gt;</br>
         &lt;/validations&gt;</br>
      &lt;/attr&gt;</br>
   &lt;/protocol&gt;</br>
 * 
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
public class ProtocolDefinition implements Serializable {
   
   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = -726881807822688804L;
   
   /** The name. */
   private String displayName;
   
   /** The tag name. */
   private String tagName;
   
   /** The allowed account Ids */
   private List<String> allowedAccountIds;
   
   /** The attrs. */
   private List<ProtocolAttrDefinition> attrs = new ArrayList<ProtocolAttrDefinition>();

   /**
    * Gets the display name.
    * 
    * @return the display name
    */
   public String getDisplayName() {
      return displayName;
   }

   /**
    * Sets the display name.
    * 
    * @param displayName the new display name
    */
   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }

   /**
    * Gets the tag name.
    * 
    * @return the tag name
    */
   public String getTagName() {
      return tagName;
   }

   /**
    * Sets the tag name.
    * 
    * @param tagName the new tag name
    */
   public void setTagName(String tagName) {
      this.tagName = tagName;
   }

   public List<String> getAllowedAccountIds() {
      return allowedAccountIds;
   }

   public void setAllowedAccountIds(List<String> allowedAccountIds) {
      this.allowedAccountIds = allowedAccountIds;
   }

   /**
    * Gets the attrs.
    * 
    * @return the attrs
    */
   public List<ProtocolAttrDefinition> getAttrs() {
      return attrs;
   }

   /**
    * Sets the attrs.
    * 
    * @param attrs the new attrs
    */
   public void setAttrs(List<ProtocolAttrDefinition> attrs) {
      this.attrs = attrs;
   }

   
   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ProtocolDefinition other = (ProtocolDefinition) obj;
      if (allowedAccountIds == null) {
         if (other.allowedAccountIds != null)
            return false;
      } else if (!allowedAccountIds.equals(other.allowedAccountIds))
         return false;
      if (attrs == null) {
         if (other.attrs != null)
            return false;
      } else if (!attrs.equals(other.attrs))
         return false;
      if (displayName == null) {
         if (other.displayName != null)
            return false;
      } else if (!displayName.equals(other.displayName))
         return false;
      if (tagName == null) {
         if (other.tagName != null)
            return false;
      } else if (!tagName.equals(other.tagName))
         return false;
      return true;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result
            + ((allowedAccountIds == null) ? 0 : allowedAccountIds.hashCode());
      result = prime * result + ((attrs == null) ? 0 : attrs.hashCode());
      result = prime * result
            + ((displayName == null) ? 0 : displayName.hashCode());
      result = prime * result + ((tagName == null) ? 0 : tagName.hashCode());
      return result;
   }
}
