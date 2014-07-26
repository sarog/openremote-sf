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

/**
 * The Class defines a OWN who's command type with its properties. A command type has a name and some fields.
 *
 * @author Marco Miccini
 */
public class OpenWebNetCommandType implements Serializable
{
   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = 4568350085407535012L;

   /** The name. */
   private String name;

   /** The fields. */
   private List<OpenWebNetField> fields = new ArrayList<OpenWebNetField>();

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
    * Gets the fields.
    *
    * @return the fields
    */
   public List<OpenWebNetField> getFields()
   {
      return fields;
   }

   /**
    * Sets the fields.
    *
    * @param fields the new fields
    */
   public void setFields(List<OpenWebNetField> fields)
   {
      this.fields = fields;
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

      OpenWebNetCommandType that = (OpenWebNetCommandType) o;

      if (name != null ? !name.equals(that.name) : that.name != null)
         return false;
      if (fields == null && that.fields == null)
         return true;
      if (fields == null || that.fields == null)
         return false;
      if (fields.size() == that.fields.size())
      {
         for (int i = 0; i < fields.size(); i++)
            if (!fields.get(i).equals(that.fields.get(i)))
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
      return 31 * result + (fields != null ? fields.hashCode() : 0);
   }
}
