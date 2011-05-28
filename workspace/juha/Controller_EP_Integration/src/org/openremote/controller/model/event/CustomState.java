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
 * Custom state event is a very basic event type that simply holds any arbitrary string
 * as its value. No semantics or rules are applied to the value string.  <p>
 *
 * Custom state events are associated with 'custom' sensors in controller definition.
 *
 * @see org.openremote.controller.model.sensor.StateSensor
 * @see Level
 * @see Range
 * @see Switch
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class CustomState extends Event<String>
{


  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Holds the event value.
   */
  private String value;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new custom state event with a given source sensor's ID, source sensors name
   * and event value.
   *
   * @param sourceSensorID    the integer ID of the sensor that originated this event
   * @param sourceSensorName  the human-readable name of the sensor that originated this event
   * @param eventValue        an arbitrary string value of this event
   */
  public CustomState(int sourceSensorID, String sourceSensorName, String eventValue)
  {
    super(sourceSensorID, sourceSensorName);

    this.value = eventValue;
  }


  // Implements Event -----------------------------------------------------------------------------

  @Override public String getValue()
  {
    return value;
  }

  @Override public String serialize()
  {
    return value;
  }
}

