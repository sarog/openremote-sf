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

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import org.openremote.controller.protocol.EventProducer;
import org.openremote.controller.component.EnumSensorType;

/**
 * A state sensor operates on a finite set of explicit state values that it returns. <p>
 *
 * Explicit state values returned from a read command or event listener implementation may
 * be mapped to other values to accommodate human-consumable values for the panel UI
 * for example, or as a translation mechanism for localized interfaces. <p>
 *
 * By default the explicit state strings this sensor expects the event producers to return are
 * available as sensor properties through the
 * {@link org.openremote.controller.protocol.ReadCommand} and
 * {@link org.openremote.controller.protocol.EventListener} APIs. See {@link DistinctStates} for
 * more details.
 *
 * @see org.openremote.controller.protocol.ReadCommand
 * @see org.openremote.controller.protocol.EventListener
 * @see DistinctStates
 * @see org.openremote.controller.model.sensor.SwitchSensor
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class StateSensor extends Sensor
{

  // Class Members --------------------------------------------------------------------------------


  /**
   * Adds distinct states as sensor properties.
   *
   * @param include   indicates whether the sensor's distinct states should be included as
   *                  properties or if an empty property set should be returned instead
   * @param states    the distinct states of this sensor
   *
   * @return    sensor's properties, including the distinct state names if included
   */
  private static Map<String, String> statesAsProperties(boolean include, DistinctStates states)
  {
    if (states == null || !include)
    {
      return new HashMap<String, String>(0);
    }
    
    HashMap<String, String> props = new HashMap<String, String>();
    int index = 1;

    for (String state : states.getAllStates())
    {
      props.put("state-" + index++, state);
    }

    return props;
  }


  // Instance Fields ------------------------------------------------------------------------------


  /**
   * Stores the state values and possible mappings for this sensor.
   */
  private DistinctStates states;



  // Constructors ---------------------------------------------------------------------------------


  /**
   * Constructs a new sensor with a given sensor ID, event producer and distinct state values
   * this sensor will return.  <p>
   *
   * The default implementation of a state sensor sends all its state values to event producer
   * implementations -- therefore protocol implementers can inspect what the expected return
   * values of a state sensor are and adjust their implementations accordingly. See
   * {@link org.openremote.controller.protocol.ReadCommand} and
   * {@link org.openremote.controller.protocol.EventListener} for details.
   *
   * @see DistinctStates
   * @see org.openremote.controller.protocol.ReadCommand
   * @see org.openremote.controller.protocol.EventListener
   *
   *
   * @param name        human-readable name of this sensor
   * @param sensorID    controller unique identifier
   * @param producer    the protocol handler that backs this sensor either with a read command
   *                    or event listener implementation
   * @param states      distinct state values and their mappings this sensor will return
   */
  public StateSensor(String name, int sensorID, EventProducer producer, DistinctStates states)
  {
    this(name, sensorID, EnumSensorType.CUSTOM, producer, states, true);
  }


  /**
   * Constructs a new sensor with a given sensor ID, event producer and distinct state values
   * this sensor will return.  <p>
   *
   * This constructor allows subclasses to determine whether the distinct states are passed
   * as sensor properties through the {@link org.openremote.controller.protocol.ReadCommand}
   * and {@link org.openremote.controller.protocol.EventListener} interface -- when the sensor's
   * type (such as {@link org.openremote.controller.model.sensor.SwitchSensor}) makes the available
   * states explicit, it may not be necessary to pass the additional property information to
   * event producer implementers.
   *
   * @param name        human-readable name of this sensor
   * @param sensorID    controller unique identifier
   * @param type        enumeration of sensor types -- this enum set is made available to
   *                    event producer implementers to discover the sensor's datatype they
   *                    need to adhere to. NOTE: this is legacy API and bound to evolve away
   *                    as the immutable Sensor class can be used instead
   * @param producer    the protocol handler that backs this sensor either with a read command
   *                    or event listener implementation
   * @param states      distinct state values and their mappings this sensor will return
   * @param includeStatesAsProperties indicates whether the sensor implementation should pass the
   *                    explicit state strings as sensor properties for event producer implementers
   *                    to inspect
   */
  protected StateSensor(String name, int sensorID, EnumSensorType type, EventProducer producer,
                        DistinctStates states, boolean includeStatesAsProperties)
  {
    super(name, sensorID, type, producer, statesAsProperties(includeStatesAsProperties, states));

    this.states = states;

  }


  // Sensor Overrides -----------------------------------------------------------------------------


  /**
   * Enforce read values for state sensor which 1) only allow event producers to return values this
   * sensor advertizes as producing (any other values are converted to
   * {@link org.openremote.controller.model.sensor.Sensor#UNKNOWN_STATUS} values) and
   * 2) map the return values from event producers to translated values if such
   * have been configured for this sensor.
   *
   * @return  the state strings of this sensor or their translated versions
   */
  @Override public String processEvent(String value)
  {
    if (!states.hasState(value))
    {
      log.debug(
          "Event producer bound to sensor (ID = {0}) returned a value that is not " +
          "consistent with sensor's datatype : {1}",
          super.getSensorID(), value
      );

      return Sensor.UNKNOWN_STATUS;
    }

    if (!states.hasMapping(value))
    {
      return value;
    }

    else
    {
      return states.getMapping(value);
    }

  }


  // Object Overrides -----------------------------------------------------------------------------

  /**
   * String representation of a state sensor. Returns sensor's name, ID, and state mappings.
   *
   * @return  this sensor as a string
   */
  @Override public String toString()
  {
    return
        "Sensor (Name = '" + getName() + "', ID = '" + getSensorID() +
        "', State Mappings: " + states.toString() + ")";
  }




  // Nested Classes -------------------------------------------------------------------------------

  /**
   * Helper class to store the distinct state values for a state sensor and possible value
   * mappings if configured. <p>
   *
   * Each explict state is available to event producer implementers through the sensor properties
   * passed via the {@link org.openremote.controller.protocol.ReadCommand} and
   * {@link org.openremote.controller.protocol.EventListener} APIs. The expected
   * state values can be found using a key 'state-1' for the first available state string to
   * 'state-n' to the last expected state value.
   */
  public static class DistinctStates
  {


    // Instance Fields ----------------------------------------------------------------------------

    /**
     * Store the states.
     */
    private Map<String, String> states = new HashMap<String, String>();



    // Public Instance Methods --------------------------------------------------------------------

    /**
     * Store an explicit state value without mapping.
     *
     * @param state   explicit state string the event producers are expected to return from
     *                their {@link org.openremote.controller.protocol.ReadCommand} or
     *                {@link org.openremote.controller.protocol.EventListener} implementations.
     */
    public void addState(String state)
    {
      states.put(state, null);
    }

    /**
     * Stores an explicit state value with mapping. When the event producer returns the state
     * value, it is automatically mapped to a new value to be consumed by UI widgets and other
     * users of the sensor.
     *
     * @param state     the state string returned by event producer implementations
     * @param mapping   the value the state string is translated to by the sensor
     */
    public void addStateMapping(String state, String mapping)
    {
      states.put(state, mapping);
    }

    /**
     * Indicates if the given state string is contained within this state collection
     *
     * @param value   the requested state string
     *
     * @return        true if the state string has been added, false otherwise
     */
    public boolean hasState(String value)
    {
      return states.containsKey(value);
    }


    // Object Overrides ---------------------------------------------------------------------------

    /**
     * Returns sensor's state mappings as a string. Implementation delegates to
     * {@link java.util.Map#toString()}.
     *
     * @return  all state mappings as a string
     */
    @Override public String toString()
    {
      return states.toString();
    }


    // Private Instance Methods -------------------------------------------------------------------


    /**
     * Indicates if the given state string has a mapping in this state collection
     *
     * @param state   the state string which mapping is requested
     *
     * @return        true if the state string is mapped to another value in this state
     *                collection, false otherwise
     */
    private boolean hasMapping(String state)
    {
      if (!hasState(state))
      {
        return false;
      }

      String mapping = states.get(state);

      return mapping != null;
    }

    /**
     * Returns the mapped value of a state string.
     *
     * @param state   the state string which mapping is requested
     *
     * @return        returns the translated value of a given event producer state string, or
     *                {@link org.openremote.controller.model.sensor.Sensor#UNKNOWN_STATUS} if
     *                no such mapping was found
     */
    private String getMapping(String state)
    {
      String mapping = states.get(state);

      if (mapping == null)
      {
        return Sensor.UNKNOWN_STATUS;
      }

      return mapping;
    }

    /**
     * Returns all the available state strings that has been added to this collection.
     *
     * @return    all values as a set of strings
     */
    private Set<String> getAllStates()
    {
      return states.keySet();
    }
  }
}

