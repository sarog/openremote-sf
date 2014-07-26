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
 * The Class defines a OWN who with its properties. A who has a value and some command types.
 *
 * @author Marco Miccini
 */
public class OpenWebNetWho implements Serializable
{
   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = 2872137856668108708L;

   /** The value. */
   private String value;

   /** The command types. */
   private List<OpenWebNetCommandType> commandTypes = new ArrayList<OpenWebNetCommandType>();

   /**
    * Gets the value.
    *
    * @return the value
    */
   public String getValue()
   {
      return value;
   }

   /**
    * Sets the value.
    *
    * @param value the new value
    */
   public void setValue(String value)
   {
      this.value = value;
   }

   /**
    * Gets the command types.
    *
    * @return the command types
    */
   public List<OpenWebNetCommandType> getCommandTypes()
   {
      return commandTypes;
   }

   /**
    * Sets the command types.
    *
    * @param commandTypes the new command types
    */
   public void setCommandTypes(List<OpenWebNetCommandType> commandTypes)
   {
      this.commandTypes = commandTypes;
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

      OpenWebNetWho that = (OpenWebNetWho) o;

      if (value != null ? !value.equals(that.value) : that.value != null)
         return false;
      if (commandTypes == null && that.commandTypes == null)
         return true;
      if (commandTypes == null || that.commandTypes == null)
         return false;
      if (commandTypes.size() == that.commandTypes.size())
      {
         for (int i = 0; i < commandTypes.size(); i++)
            if (!commandTypes.get(i).equals(that.commandTypes.get(i)))
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
      int result = (value != null) ? value.hashCode() : 0;
      return 31 * result + (commandTypes != null ? commandTypes.hashCode() : 0);
   }
}
