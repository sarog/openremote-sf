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
package org.openremote.controller.model.sensor;

import org.openremote.controller.protocol.EventProducer;
import org.openremote.controller.component.EnumSensorType;


/**
 * A boolean switch sensor type. This is a specific case of a state sensor with two 'on' or 'off'
 * state strings. A switch sensor can be configured to map the default return values to other
 * values ("open/close", "running/stoppped", etc.) or to translate return values to local
 * languages.  <p>
 *
 * @see StateSensor
 * @see org.openremote.controller.protocol.ReadCommand
 * @see org.openremote.controller.protocol.EventListener
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class SwitchSensor extends StateSensor
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * Set up the distinct on/off states for the superclass constructor
   *
   * @return  distinct states for the state sensor
   */
  private static DistinctStates createSwitchStates()
  {
    DistinctStates states = new DistinctStates();
    states.addState("on");
    states.addState("off");

    return states;
  }


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new switch sensor with given sensor ID and event producer. The sensor
   * implementation will return 'on' or 'off' string values.
   *
   * @param name        human-readable name of this sensor
   * @param sensorID    controller unique identifier
   * @param producer    the protocol handler that backs this sensor either with a read command
   *                    or event listener implementation
   */
  public SwitchSensor(String name, int sensorID, EventProducer producer)
  {
    this(name, sensorID, producer, createSwitchStates());
  }


  /**
   * Constructs a new switch sensor with given sensor ID, an event producer, and on/off state
   * mapping. The distinct states should contain mapping <b>only</b> for 'on' and 'off' states
   * in case of a switch.  <p>
   *
   * The sensor implementation will return 'on or 'off' string values.
   *
   * @param name        human-readable name of this sensor
   * @param sensorID    controller unique identifier
   * @param producer    the protocol handler that backs this sensor either with a read command
   *                    or event listener implementation
   * @param states      state string mappings for the default 'on' and 'off' values
   */
  public SwitchSensor(String name, int sensorID, EventProducer producer, DistinctStates states)
  {
    super(name, sensorID, EnumSensorType.SWITCH, producer, states, false);
  }



  // Object Overrides -----------------------------------------------------------------------------

  /**
   * String representation of a range sensor. Returns sensor's name, ID, and range minimum and
   * maximum values.
   *
   * @return  this sensor as a string
   */
  @Override public String toString()
  {
    return
        "Switch Sensor (Name = '" + getName() + "', ID = '" + getSensorID() + "')";
  }

}

