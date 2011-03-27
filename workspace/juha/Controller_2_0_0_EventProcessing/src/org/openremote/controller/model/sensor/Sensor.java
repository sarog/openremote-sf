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

import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.protocol.ReadCommand;
import org.openremote.controller.protocol.EventProducer;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.service.ServiceContext;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.Constants;
import org.openremote.controller.component.EnumSensorType;

/**
 * Sensors abstract incoming events from devices, either through polling or listening to
 * devices that actively broadcast their state changes. Sensors operate on protocol handlers
 * to execute read requests on devices to fetch the current device state.  <p>
 *
 * Each polling sensor (for passive devices) has a thread associated with it. Sensors bound to
 * event listeners do not create threads of their own but the event listener implementations
 * themselves are usually multi-threaded. <p>
 *
 * Each sensor can have list of properties which it makes available to implementations of
 * read commands and event listeners. These properties may be used by protocol implementers to
 * direct their event producer output values to suit the sensor's configuration. <p>
 *
 * A sensor class is designed as immutable. This immutability should be maintained by the sensor's
 * subclasses in order to make it safe to pass the sensor instances to protocol handlers and
 * possibly to other plugins and/or components.
 *
 *
 * TODO : per sensor polling frequency
 *
 * TODO : cache polling reads
 *
 * TODO : push to state cache by listeners
 *
 * TODO : keep track of event producer ID  associated with this sensor
 *
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public abstract class Sensor
{

  // Constants ------------------------------------------------------------------------------------

  public final static String UNKNOWN_STATUS = StatusCommand.UNKNOWN_STATUS;

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




  // Class Members --------------------------------------------------------------------------------

  /**
   * Common log category for runtime operations on sensors.
   */
  protected final static Logger log = Logger.getLogger(Constants.RUNTIME_SENSORS_LOG_CATEGORY);



  // Instance Fields ------------------------------------------------------------------------------


  private String sensorName;


  /**
   * Sensors unique ID. Must be unique per controller deployment (unique within controller.xml file)
   */
  private int sensorID;

  /**
   * The datatype of sensor. The sensor should return values on its {@link #read} operation
   * only according to its datatype.
   *
   * NOTE: this is going away with the legacy StatusCommand interface
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
   * Sensor properties. These properties can be used by the protocol implementors to direct
   * their implementation on read commands and event listeners according to sensor configuration.
   */
  private Map<String, String> sensorProperties;



  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new sensor with a given ID, sensor datatype, an event producing protocol handler,
   * and a set of sensor properties
   *
   * @param name              human-readable name of the sensor
   * @param sensorID          an unique sensor ID
   * @param sensorType        datatype for this sensor
   * @param eventProducer     event producing protocol handler implementation
   * @param sensorProperties  additional sensor properties
   */
  protected Sensor(String name, int sensorID, EnumSensorType sensorType, EventProducer eventProducer,
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

    this.sensorName = name;
    this.sensorID = sensorID;
    this.sensorType = sensorType;
    this.eventProducer = eventProducer;
    this.sensorProperties = sensorProperties;
  }



  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * TODO
   */
  public String getName()
  {
    return sensorName;
  }

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
   * @deprecated  Sensor types are available via proper object model where each sensor is a
   *              subclass of this abstract sensor class -- therefore normal type checks
   *              using 'instanceof' apply. The EnumSensorType however is exposed via a public
   *              API used by protocol implementers and therefore must still remain -- use of
   *              it (and use of this method by extension) is heavily discouraged though.
   *
   * @return  this sensor's data type
   */
  @Deprecated public EnumSensorType getSensorType()
  {
    return sensorType;
  }


  public Map<String, String> getProperties()
  {
    HashMap<String, String> props = new HashMap<String, String>(5);
    props.putAll(sensorProperties);

    return props;
  }


  /**
   * Returns the current state of this sensor.  <p>
   *
   * If the sensor is bound to a read command implementation, the read command is invoked --
   * this may yield an active state request on the connecting transport unless the read
   * command implementation caches certain values and returns them from memory. <p>
   *
   * In case of an event listener, this method does not invoke anything on the listener itself
   * but returns the last stored state from the controller's device state cache associated with
   * this sensor's ID. An event listener implementation is responsible of actively updating and
   * inserting the device state values into the controller's cache. <p>
   *
   * In case of errors, {@link org.openremote.controller.model.sensor.Sensor#UNKNOWN_STATUS} is
   * returned.  <p>
   *
   * This default implementation does not validate the input from protocol read commands in any
   * way (other than handling implementation errors that yield runtime exceptions) -- it is the
   * responsiblity of the concrete subclasses to validate the inputs from read commands to ensure
   * the sensor returns values that adhere to its datatype.
   *
   * @see org.openremote.controller.model.sensor.StateSensor#read
   * @see org.openremote.controller.model.sensor.SwitchSensor#read
   * @see org.openremote.controller.protocol.EventListener
   * @see org.openremote.controller.protocol.ReadCommand
   *
   * @return sensor's value, according to its datatype and provided by protocol handlers (read
   *         command or event listener)
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
      try
      {
        // Support legacy API...

        if (eventProducer instanceof StatusCommand)
        {
          StatusCommand statusCommand = (StatusCommand)eventProducer;

          return statusCommand.read(sensorType, sensorProperties);
        }

        else
        {
          ReadCommand command = (ReadCommand)eventProducer;

          return command.read(this);
        }
      }
      catch (Throwable t)
      {
        log.error("Implementation error in protocol handler " + eventProducer);

        return Sensor.UNKNOWN_STATUS;
      }
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
   * Indicates if this sensor is bound to an event listener.
   *
   * @return  true if this sensor is bound to an event listener implementation, false otherwise
   */
  public boolean isEventListener()
  {
    return eventProducer instanceof EventListener;
  }


  /**
   * Indicates whether this sensor is bound to read commands that has a polling sensor associated
   * to it, or receives state updates via other means.
   *
   * @return    true if a polling thread is associated with this thread, false otherwise
   */
  public boolean isPolling()
  {
    return eventProducer instanceof ReadCommand;
  }



  /**
   * TODO :
   *
   *   - Starts the sensor. This currently only applies to event listeners (which are initialized
   *     with a reference to this sensor). It should also apply to polling sensors by creating
   *     the polling thread (which ought to be managed by the sensor implementation itself).
   *     Further, the sensor should register itself with the deployer once it starts.
   *
   *     This all needs to wait until the thread polling thread mess is sorted out and a proper
   *     deployer is created.
   *                                                                                    [JPL]
   */
  public void start()
  {
    if (isEventListener())
    {
      EventListener listener = (EventListener)eventProducer;

      listener.setSensorID(sensorID);
    }
  }



  // Object Overrides -----------------------------------------------------------------------------

  /**
   * String represenation of this sensor, including sensor type, its unique identifier and
   * the event producer the sensor is bound to.
   *
   * @return  string representation of this sensor
   */
  @Override public String toString()
  {
    return sensorType + " Sensor (ID = " + sensorID + ") bound to event producer " + eventProducer;
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
