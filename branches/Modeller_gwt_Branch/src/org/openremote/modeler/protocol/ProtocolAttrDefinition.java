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
package org.openremote.modeler.protocol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class ProtocolAttrDefinition.
 */
public class ProtocolAttrDefinition implements Serializable {

   /**
    * The Constant serialVersionUID.
    */
   private static final long serialVersionUID = 2728595716149551560L;

   /**
    * The name.
    */
   private String name;

   /**
    * The label.
    */
   private String label;

   /**
    * The validators.
    */
   private List<ProtocolValidator> validators = new ArrayList<ProtocolValidator>();

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
    * Gets the label.
    *
    * @return the label
    */
   public String getLabel() {
      return label;
   }

   /**
    * Sets the label.
    *
    * @param label the new label
    */
   public void setLabel(String label) {
      this.label = label;
   }

   /**
    * Gets the validators.
    *
    * @return the validators
    */
   public List<ProtocolValidator> getValidators() {
      return validators;
   }

   /**
    * Sets the validators.
    *
    * @param validators the new validators
    */
   public void setValidators(List<ProtocolValidator> validators) {
      this.validators = validators;
   }


   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ProtocolAttrDefinition that = (ProtocolAttrDefinition) o;

      if (label != null ? !label.equals(that.label) : that.label != null) return false;
      if (name != null ? !name.equals(that.name) : that.name != null) return false;
      if (validators == null && that.getValidators() == null) return true;
      if (validators == null || that.getValidators() == null) return false;

      if (validators.size() == that.getValidators().size()) {
         for (int i = 0; i < validators.size(); i++) {
            if (!validators.get(i).equals(that.getValidators().get(i))) {
               return false;
            }
         }
      } else {
         return false;
      }
      return true;
   }

   @Override
   public int hashCode() {
      int result = name != null ? name.hashCode() : 0;
      result = 31 * result + (label != null ? label.hashCode() : 0);
      result = 31 * result + (validators != null ? validators.hashCode() : 0);
      return result;
   }
}
