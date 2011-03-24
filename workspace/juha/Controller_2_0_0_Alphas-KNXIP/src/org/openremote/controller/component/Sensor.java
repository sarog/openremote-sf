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
package org.openremote.controller.component;

import java.util.Map;
import java.util.HashMap;

import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.protocol.EventProducer;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.service.ServiceContext;

/**
 * TODO : Sensor will listen status change by using {@link StatusCommand}.
 *
 * TODO : is immutable
 *
 * TODO : per sensor polling frequency
 *
 * TODO : cache polling reads
 *
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public abstract class Sensor
{

  // Constants ------------------------------------------------------------------------------------

  // TODO :
  //   both constants below are a result of a rather poorly designed API -- they may well be
  //   candidates for removal later.
  //                                                                                  [JPL]

  /**
   * key name used in state map to store range min state.
   */
  public final static String RANGE_MIN_STATE = "_range_min_state_";

  /**
   * key name used in state map to store range max state.
   */
  public final static String RANGE_MAX_STATE = "_range_max_state_";


  
  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Sensors unique ID. Must be unique per controller deployment (unique within controller.xml file)
   */
  private int sensorID;

  /**
   * The datatype of sensor. The sensor should return values on its {@link #read} operation
   * only according to its datatype.
   */
  private EnumSensorType sensorType;

  /**
   * An event producer is a protocol handler that can be customized to return values to a sensor.
   * Therefore the sensor implementation remains type (as in Java class type) and protocol
   * independent and delegates these protocol specific tasks to event producer implementations. <p>
   *
   * Two sub-categories of event producers exist today: read commands (a.k.a status command) and
   * event listeners. These are dealt differently in that read commands are actively polled by
   * the controller (through Sensor's API ) while event listeners produce events to the
   * controller at their own schedule.
   */
  private EventProducer eventProducer;

  /**
   * Sensor properties. These properties are used to handle the implementation of different types
   * of sensors (see {@link EnumSensorType}) to direct the implementation towards a specific
   * behavior.
   *
   * TODO: key is state name, value is actual state value.
   */
  private Map<String, String> sensorProperties;



  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new sensor with a given ID, sensor datatype, an event producing protocol handler,
   * and a set of sensor properties
   *
   * @param sensorID
   * @param sensorType
   * @param eventProducer
   * @param sensorProperties
   */
  public Sensor(int sensorID, EnumSensorType sensorType, EventProducer eventProducer,
               Map<String, String> sensorProperties)
  {
    if (sensorType == null || eventProducer == null)
    {
      throw new IllegalArgumentException(
          "Sensor implementation does not allow null datatype or null event producer " +
          "(ID = " + sensorID + ")."
      );
    }

    if (sensorProperties == null)
    {
      sensorProperties = new HashMap<String, String>(0);
    }

    this.sensorID = sensorID;
    this.sensorType = sensorType;
    this.eventProducer = eventProducer;
    this.sensorProperties = sensorProperties;
  }



  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * Returns this sensors ID. The sensor ID is unique in a controller deployment.
   *
   * @return  sensor ID
   */
  public int getSensorID()
  {
    return sensorID;
  }

  /**
   * Returns this sensor's datatype. Datatype dictates the types of values a sensor may
   * return.
   *
   * @return  this sensor's data type
   */
  public EnumSensorType getSensorType()
  {
    return sensorType;
  }


  /**
   * State map contains all supported states.
   * key is state name, value is actual state value.
   * e.g. for a switch may be:
   * <ul>
   * <li>on:light1_on</li>
   * <li>off:light1_off</li>
   * </ul>
   *
   * This map is used to find state name according to returned state value.
   *
   * @return TODO
   */
  public Map<String, String> getStateMap()
  {
    // TODO : this is only used in tests
    return sensorProperties;
  }



  /**
   * TODO : Read status using status command.
   *
   * @return TODO
   */
  public String read()
  {
    // If this sensor abstracts an event listener, the read() will not invoke the protocol
    // handler associated with this sensor directly -- instead we try to fetch the latest
    // value produced by an event listener from the controller's global device state cache.

    if (isEventListener())
    {
      String status = ServiceContext.getDeviceStateCache().queryStatus(sensorID);

      return (status == null) ? StatusCommand.UNKNOWN_STATUS : status;
    }

    // If we are dealing with regular read commands, execute it to explicitly fetch the
    // device state...

    if (isPolling())
    {
      StatusCommand statusCommand = (StatusCommand)eventProducer;

      return statusCommand.read(sensorType, sensorProperties);
    }

    else
    {
      throw new IllegalArgumentException(
          "Sensor has been initialized with an event producer that is neither a read command " +
          "or event listener. The implementation must be updated to accommodate this new type."
      );

    }
  }




  /**
   * TODO
   *
   * @return
   */
  public boolean isEventListener()
  {
    return eventProducer instanceof EventListener;
  }


  /**
   * TODO
   *
   * @return
   */
  public boolean isPolling()
  {
    return eventProducer instanceof StatusCommand;
  }



  /**
   * TODO
   *
   */
  public void initListener()
  {
    if (isEventListener())
    {
      EventListener listener = (EventListener)eventProducer;

      listener.setSensorID(sensorID);
    }
  }



  // Object Overrides -----------------------------------------------------------------------------

  /**
   * String represenation of this sensor, with sensor ID, sensor's type identifier and
   *
   * TODO
   *
   * @return
   */
  @Override public String toString()
  {
    return sensorID + " " + sensorType + " " + eventProducer;
  }

  /**
   * Returns a sensor ID as this sensor's hashcode.
   *
   * @return  sensor ID based hash
   */
  @Override public int hashCode()
  {
    return sensorID;
  }

  /**
   * Test sensor object equality based on unique identifier (as returned by {@link #getSensorID}. <p>
   *
   * Subclasses are considered equal, despite what their data values are, as long as the sensor
   * ID is equal.
   *
   * @param   o   object to compare to
   *
   * @return  true if equals, false otherwise
   */
  @Override public boolean equals(Object o)
  {
    if (o == null)
      return false;

    if (!(o instanceof Sensor))
      return false;

    Sensor sensor = (Sensor)o;

    return sensor.getSensorID() == this.getSensorID();
  }

}
