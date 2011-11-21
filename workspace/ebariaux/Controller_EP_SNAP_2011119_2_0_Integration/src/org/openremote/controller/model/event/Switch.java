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
package org.openremote.controller.model.event;

import org.openremote.controller.protocol.Event;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class Switch extends Event<String>
{
  public enum State { ON, OFF }

  private String value;
  private State eventState;

  public Switch(int sourceID, String sourceName, String value, State originalState)
  {
    super(sourceID, sourceName);

    this.eventState = originalState;
    this.value = value;
  }


  public Switch.State getOriginalState()
  {
    return eventState;
  }

  @Override public String getValue()
  {
    return value;
  }

  @Override public void setValue(String value)
  {
    this.value = value;
  }

  @Override public String serialize()
  {
    //return eventState.toString().toLowerCase();
    return getValue();
  }

  @Override public boolean isEqual(Object o)
  {
    if (o == null)
    {
      return false;
    }

    if (o == this)
    {
      return true;
    }

    if (o.getClass() != this.getClass())
    {
      return false;
    }

    Switch s = (Switch)o;

    return s.getSourceID().equals(this.getSourceID())
        && s.getSource().equals(this.getSource())
        && s.getValue().equals(this.getValue());
  }

}

