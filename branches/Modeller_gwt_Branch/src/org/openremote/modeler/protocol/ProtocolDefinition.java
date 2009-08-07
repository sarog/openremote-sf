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

package org.openremote.modeler.protocol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class ProtocolDefinition.
 * 
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
public class ProtocolDefinition implements Serializable {
   
   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = -726881807822688804L;
   
   /** The name. */
   private String name;
   
   /** The attrs. */
   private List<ProtocolAttrDefinition> attrs = new ArrayList<ProtocolAttrDefinition>();

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
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }

      ProtocolDefinition that = (ProtocolDefinition) o;

      if (name != null ? !name.equals(that.name) : that.name != null) {
         return false;
      }
      if (attrs == null && that.attrs == null) {
         return true;
      }
      if (attrs == null || that.attrs == null) {
         return false;
      }
      if (attrs.size() == that.attrs.size()) {
         for (int i = 0; i < attrs.size(); i++) {
            if (!attrs.get(i).equals(that.attrs.get(i))) {
               return false;
            }
         }
      } else {
         return false;
      }
      return true;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      int result = name != null ? name.hashCode() : 0;
      result = 31 * result + (attrs != null ? attrs.hashCode() : 0);
      return result;
   }
}
