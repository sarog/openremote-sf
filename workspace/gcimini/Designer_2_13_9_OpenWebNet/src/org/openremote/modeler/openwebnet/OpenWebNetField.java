/*
 * OpenRemote, the Home of the Digital Home.
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
package org.openremote.modeler.openwebnet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.protocol.ProtocolValidator;

/**
 * The Class defines a OWN field with its properties. A field has a name and some values.
 *
 * @author Marco Miccini
 */
public class OpenWebNetField implements Serializable
{
   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = -4955303408082150449L;

   /** The name. */
   private String name;

   /** The optional attribute. */
   private boolean optional;

   /** The values. */
   private List<ProtocolValidator> values = new ArrayList<ProtocolValidator>();

   /**
    * Gets the name.
    *
    * @return the name
    */
   public String getName()
   {
      return name;
   }

   /**
    * Sets the name.
    *
    * @param name the new name
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * Gets the optional attribute.
    *
    * @return the optional attribute
    */
   public boolean getOptional()
   {
      return optional;
   }

   /**
    * Sets the optional attribute.
    *
    * @param optional the new optional attribute
    */
   public void setOptional(String optional)
   {
      this.optional = new Boolean(optional);
   }

   /**
    * Gets the values.
    *
    * @return the values
    */
   public List<ProtocolValidator> getValues()
   {
      return values;
   }

   /**
    * Sets the values.
    *
    * @param values the new values
    */
   public void setValues(List<ProtocolValidator> values)
   {
      this.values = values;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object o)
   {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;

      OpenWebNetField that = (OpenWebNetField) o;

      if (name != null ? !name.equals(that.name) : that.name != null)
         return false;
      if (values == null && that.values == null)
         return true;
      if (values == null || that.values == null)
         return false;
      if (values.size() == that.values.size())
      {
         for (int i = 0; i < values.size(); i++)
            if (!values.get(i).equals(that.values.get(i)))
               return false;
      }
      else
         return false;
      return true;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      int result = (name != null) ? name.hashCode() : 0;
      return 31 * result + (values != null ? values.hashCode() : 0);
   }
}
