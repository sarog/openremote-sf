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
package org.openremote.controller.protocol;

import java.util.Map;
import java.util.HashMap;

import org.openremote.controller.exception.ProtocolException;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class CommandProperties
{


  private Map<String, String> commandProperties = new HashMap<String, String>(20);


  public String getMandatoryProperty(String name) throws ProtocolException
  {
    if (name == null || name.equals(""))
    {
      throw new ProtocolException("");
    }

    String value = commandProperties.get(name.toLowerCase());

    if (value == null || value.equals(""))
    {
      throw new ProtocolException("");
    }

    return value;
  }


  public String getOptionalProperty(String name)
  {
    if (name == null || name.equals(""))
    {
      return null;
    }
    
    return commandProperties.get(name.toLowerCase());
  }



  protected void add(String name, String value)
  {
    commandProperties.put(name.toLowerCase(), value);
  }



}

