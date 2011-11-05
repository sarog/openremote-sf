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

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public abstract class Event<T>
{

  /**
   * The ID of the originating sensor.
   */
  private int sourceSensorID;

  /**
   * The name of the originating sensor.
   */
  private String sourceSensorName;



  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new event with a given sensor ID and sensor name. Both sensor ID and name
   * are defined in the controller.xml model for sensors.
   *
   * @param sourceSensorID    integer ID of the sensor that originated this event
   * @param sourceSensorName  human-readable name of the sensor that originated this event
   */
  public Event(int sourceSensorID, String sourceSensorName)
  {
    this.sourceSensorID = sourceSensorID;

    this.sourceSensorName = sourceSensorName;
  }


  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * TODO
   *
   * @return
   */
  public int getSourceID()
  {
    return sourceSensorID;
  }

  public void setSourceID(int sensorID)
  {
    this.sourceSensorID = sensorID;
  }

  /**
   * TODO
   *
   * @return
   */
  public String getSource()
  {
    return sourceSensorName;
  }

  public void setSource(String name)
  {
    this.sourceSensorName = name;
  }
  


  /**
   * Returns the value of this event.
   *
   * @return  event value
   */
  public abstract T getValue();

  /**
   * TODO
   *
   * This is an intermediate migration API -- at the end of the event processing chain
   * events are stored into the device state cache which panels consume. The API there is
   * still string-based.
   *
   * This method implementation should return an appropriate string representation of the
   * event value.
   *
   * @return    string representation of event value
   */
  public abstract String serialize();
}

