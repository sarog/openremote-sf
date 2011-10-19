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
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.protocol.ReadCommand;
import org.openremote.controller.protocol.EventProducer;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.Event;
import org.openremote.controller.service.ServiceContext;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.Constants;
import org.openremote.controller.OpenRemoteRuntime;
import org.openremote.controller.statuscache.StatusCache;
import org.openremote.controller.model.event.Switch;
import org.openremote.controller.model.event.Range;
import org.openremote.controller.model.event.CustomState;
import org.openremote.controller.model.event.Level;
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
 * Sensors are registered with
 * {@link org.openremote.controller.statuscache.StatusCache device state cache}. Sensors create
 * {@link org.openremote.controller.protocol.Event events} which represent the data from event
 * producers and are passed by cache to
 * {@link org.openremote.controller.statuscache.EventProcessor event processors}. <p>
 *
 * Therefore the object hierarchy for sensors is as follows: <p>
 *
 * <pre>{@code Cache (one) <--> (many) Sensor (one) <--> (one) Event Producer}</pre>
 *
 * Event producers are created by third party integrators where as cache and sensors are part of
 * the controller framework. <p>
 *
 * @see org.openremote.controller.statuscache.StatusCache
 * @see org.openremote.controller.statuscache.EventProcessor
 * @see org.openremote.controller.protocol.EventListener
 * @see org.openremote.controller.protocol.Event
 *
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public abstract class Sensor
{

  /*
   * Tasks TODO :
   *
   *   - to maintain immutability to extensions, this implementation should be based on a
   *     sensor interface where the methods included in this implementation that no longer
   *     fulfill the immutability requirement are not included.
   *
   *     getName, getSensorID, getProperties, update are relevant to extensions and should
   *     be in the Sensor interface, other methods should be considered internal
   *
   *     See ORCJAVA-123 - http://jira.openremote.org/browse/ORCJAVA-123
   *
   *   - Minor improvement: event producers should expose some type of ID for the sensors.
   *     This is mostly useful for reporting (the sensor is associated with which event producer)
   *
   */


  // Constants ------------------------------------------------------------------------------------

  /**
   * TODO:
   *
   *  Represents an uninitialized or unknown sensor status value.
   *
   *   -- the type String should go away once events are completely defined as type-safe
   */
  public final static String UNKNOWN_STATUS = StatusCommand.UNKNOWN_STATUS;


  /**
   * Key name used in sensor properties to store range minimum value.
   *
   * @deprecated This is provided for backwards compatibility to a deprecated
   *             {@link org.openremote.controller.command.StatusCommand} interface.
   *             New implementations should use
   *             {@link org.openremote.controller.protocol.EventListener} interface instead and
   *             query the minimum and maximum values through the interface defined by
   *             {@link org.openremote.controller.component.RangeSensor}.
   */
  @Deprecated public final static String RANGE_MIN_STATE = "_range_min_state_";

  /**
   * Key name used in sensor properties to store range maximum value.
   *
   * @deprecated This is provided for backwards compatibility to a deprecated
   *             {@link org.openremote.controller.command.StatusCommand} interface.
   *             New implementations should use
   *             {@link org.openremote.controller.protocol.EventListener} interface instead and
   *             query the minimum and maximum values through the interface defined by
   *             {@link org.openremote.controller.component.RangeSensor}.
   */
  @Deprecated public final static String RANGE_MAX_STATE = "_range_max_state_";




  // Class Members --------------------------------------------------------------------------------

  /**
   * Common log category for runtime operations on sensors.
   */
  protected final static Logger log = Logger.getLogger(Constants.RUNTIME_SENSORS_LOG_CATEGORY);



  // Instance Fields ------------------------------------------------------------------------------


  /**
   * Human readable sensor name. Used with event processors, logging, etc.
   */
  private String sensorName;


  /**
   * Sensor's unique ID. Must be unique per controller deployment.
   */
  private int sensorID;


  /**
   * Reference to the state cache that receives and processes the events generated from this sensor.
   */
  private StatusCache deviceStateCache;


  /**
   * An event producer is a protocol handler that can be customized to return values to a sensor.
   * Therefore the sensor implementation remains type (as in Java class type) and protocol
   * independent and delegates these protocol specific tasks to event producer implementations. <p>
   *
   * Two sub-categories of event producers exist today: read commands (a.k.a status command) and
   * event listeners. These are dealt differently in that read commands are actively polled by
   * the sensor while event listeners produce events to the controller at their own schedule.
   */
  private EventProducer eventProducer;


  /**
   * This is a polling thread implementation for sensors that use
   * {@link org.openremote.controller.protocol.ReadCommand} instead of
   * {@link org.openremote.controller.protocol.EventListener}.
   */
  private DeviceReader deviceReader;

  
  /**
   * Sensor properties. These properties can be used by the protocol implementors to direct
   * their implementation on read commands and event listeners according to sensor configuration.
   *
   * TODO : add sensor <property> elements to XML schema
   */
  private Map<String, String> sensorProperties;

  /**
   * The datatype of sensor. The sensor should return values on its {@link #read} operation
   * only according to its datatype.
   *
   * @deprecated This enum is only provided for backwards compatibility with the already
   *             deprecated {@link org.openremote.controller.command.StatusCommand} interface.
   *             New code should not make use of this type.
   */
  @Deprecated private EnumSensorType sensorType;



  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new sensor with a given name, ID, sensor datatype (deprecated legacy),
   * an event producing protocol handler, reference to the managing device state cache,
   * and a set of sensor properties.
   *
   * @param name
   *          Human readable name of the sensor. Used with event processors, logging, etc.
   *
   * @param sensorID
   *          A unique sensor ID. Must be unique per controller deployment.
   *
   * @param cache
   *          reference to a device state cache this sensor registers itself with and pushes
   *          value updates to
   *
   * @param eventProducer
   *          protocol handler implementation
   *
   * @param sensorProperties
   *          Additional sensor properties. These properties can be used by the protocol
   *          implementors to direct their implementation according to sensor configuration.
   *
   * @param sensorType
   *          datatype for this sensor
   *
   */
  protected Sensor(String name, int sensorID, StatusCache cache, EventProducer eventProducer,
                   Map<String, String> sensorProperties, EnumSensorType sensorType)
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
    this.deviceStateCache = cache;
    this.eventProducer = eventProducer;
    this.sensorProperties = sensorProperties;
    this.sensorType = sensorType;
  }



  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * Returns the human readable name of this sensor.
   *
   * @return  sensor's name as defined in tooling and controller's XML definition
   */
  public String getName()
  {
    return sensorName;
  }

  /**
   * Returns this sensor's ID. The sensor ID is unique within a controller deployment.
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
   *              using 'instanceof' apply. The EnumSensorType however is exposed as part of
   *              the now deprecated {@link org.openremote.controller.command.StatusCommand}
   *              interface and therefore this method still remains to maintain that backwards
   *              compatibility. <b>New code should not make use of this method.</b>
   *
   * @return  this sensor's data type
   */
  @Deprecated public EnumSensorType getSensorType()
  {
    return sensorType;  // TODO : should be able to remove now
  }


  /**
   * Returns sensor's properties. Properties are simply string based name-value mappings.
   * Concrete sensor implementations may specify which particular properties they expose. <p>
   *
   * The returned map does not reference this sensor instance and can be modified freely.
   *
   * @return  sensor properties or an empty collection
   */
  public Map<String, String> getProperties()
  {
    HashMap<String, String> props = new HashMap<String, String>(5);
    props.putAll(sensorProperties);

    return props;
  }




  /**
   * Call path for event listeners. Allow direct update of the sensor's value in the controller's
   * global state cache. <p>
   *
   * Before updating the state cache, the value is first validated by concrete sensor
   * implementation's {@link Sensor#processEvent(String)} method.
   *
   * @param state   the new value for this sensor
   */
  public void update(String state)
  {
    // TODO :
    //   - signature update to event type (ORCJAVA-97 - http://jira.openremote.org/browse/ORCJAVA-97)

    // TODO :
    //   - Add thread dispatcher. Sensor should release event producing thread before reaching
    //     the event processor chain. This enables long running event processors without
    //     impacting sensor polling loop or listening thread. It can also be used to protect the
    //     system from an over-eager sensor (if buffer overflow yields dropped events).
    //
    //     Implementation for thread dispatch should be in this sensor so buffer overflow behavior 
    //     (blocking / dropping) can vary per configured sensor if required.
    //     (ORCJAVA-99 -- http://jira.openremote.org/browse/ORCJAVA-99)


    // Allow for sensor type specific processing of the value returned by event producer.
    // This can be used for mappings, validating value ranges, etc. before the value is
    // pushed into device state cache and event processor chain.

    Event evt = processEvent(state);

    log.trace("Processed {0}, received {1}", state, evt.getValue());

    deviceStateCache.update(evt);
        
    // TODO :
    //   - the check on enumSensorType is not object oriented. Events should be generated by
    //     the concrete Sensor subclasses (Sensor.getEvent()). This way the impl. is poly-
    //     morphic and adding new sensor types will not require modification of this parent
    //     (ORCJAVA-98 -- http://jira.openremote.org/browse/ORCJAVA-98)


//    if (getSensorType() == EnumSensorType.SWITCH)
//    {
//      Switch.State switchState;
//
//      if (value.equalsIgnoreCase("on"))
//      {
//        switchState = Switch.State.ON;
//      }
//
//      else if (value.equalsIgnoreCase("off"))
//      {
//        switchState = Switch.State.OFF;
//      }
//
//      else
//      {
//        log.warn(
//            "Sensor ''{0}'' (ID = {1}) is SWITCH type but produced a value that is not " +
//            " on/off : ''{2}''", getName(), getSensorID(), value
//        );
//
//        return;
//      }
//
//      Event evt = new Switch(getSensorID(), getName(), switchState);
//
//      deviceStateCache.update(evt);
//    }
//
//    else if (getSensorType() == EnumSensorType.RANGE)
//    {
//      try
//      {
//        Event evt = new Range(getSensorID(), getName(), Integer.parseInt(value));
//
//        deviceStateCache.update(evt);
//      }
//
//      catch (NumberFormatException exception)
//      {
//        log.warn(
//            "Sensor ''{0}'' (ID = {1}) is RANGE type but produced a value that is not " +
//            " an integer : ''{2}''", getName(), getSensorID(), value
//        );
//      }
//    }
//
//    else if (getSensorType() == EnumSensorType.CUSTOM)
//    {
//      Event evt = new CustomState(getSensorID(), getName(), value);
//
//      deviceStateCache.update(evt);
//    }
//
//    else if (getSensorType() == EnumSensorType.LEVEL)
//    {
//      try
//      {
//        Event evt = new Level(getSensorID(), getName(), Integer.parseInt(value));
//
//        deviceStateCache.update(evt);
//      }
//
//      catch (NumberFormatException exception)
//      {
//        log.warn(
//            "Sensor ''{0}'' (ID = {1}) is LEVEL type but produced a value that is not " +
//            " an integer : ''{2}''", getName(), getSensorID(), value
//        );
//      }
//    }
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
    return eventProducer instanceof ReadCommand || eventProducer instanceof StatusCommand;
  }



  /**
   * TODO 
   */
  public void start()
  {

    if (isEventListener())
    {
      EventListener listener = (EventListener)eventProducer;

      try
      {
        listener.setSensor(this);
      }

      catch (Throwable t)
      {
        log.error(
            "There was an implementation error in the event listener associated with " +
            " sensor ''{0}''. The listener implementation may not have started correctly : {1}",
            t, getName(), t.getMessage()
        );
      }
    }

    else
    {
//      Runnable r = new Runnable()
//      {
//        @Override public void run()
//        {
//          log.info("Started sensor (ID = {0}, type = {1}).", getSensorID(), getSensorType());
//
//          while (pollingThreadRunning)
//          {
//            update(read());
//
//            try
//            {
//              Thread.sleep(POLLING_INTERVAL);
//            }
//            catch (InterruptedException e)
//            {
//              pollingThreadRunning = false;
//
//              log.info("Shutting down sensor (ID = {0}, type = {1}).", getSensorID(), getSensorType());
//
//              // Allow the container to handle thread cleanup if it wants to...
//
//              Thread.currentThread().interrupt();
//            }
//          }
//        }
//      };

      deviceReader = new DeviceReader();
      deviceReader.start();
    }
  }


  /**
   * TODO
   */
  public void stop()
  {
    if (isEventListener())
    {
      EventListener listener = (EventListener)eventProducer;

      try
      {
        listener.stop(this);
      }

      catch (Throwable t)
      {
        log.error(
            "There was an implementation error in the event listener associated with " +
            "sensor ''{0}''. The event listener may not have stopped properly : {1}",
            t, getName(), t.getMessage()
        );
      }
    }

    else
    {
      if (deviceReader != null)
      {
        deviceReader.stop();
      }
    }
  }


  public boolean isRunning() // TODO : remove
  {
    if (deviceReader == null)
      return false;
    else
      return deviceReader.pollingThreadRunning;
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



  // Protected Methods ----------------------------------------------------------------------------

  /**
   *  TODO
   *
   * Callback to subclasses to apply their event producer validations and other processing
   * if necessary. This method is called both when a polling sensor (read command) value is
   * fetched or when an event listener adds a new sensor value to state cache.
   *
   * @see Sensor#read
   * @see Sensor#update
   *
   * @param value   value returned by the event producer
   *
   * @return validated and processed value of the event producer
   */
  protected abstract Event processEvent(String value);


  /**
   * Returns the current state of this sensor.  <p>
   *
   * If the sensor is bound to a read command implementation, the read command is invoked --
   * this may yield an active request using the connecting transport to device unless the read
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
   * way (other than handling implementation errors that yield runtime exceptions) -- instead it
   * calls {@link Sensor#processEvent} which the concrete subclasses should override and implement
   * to validate the inputs from read command and to ensure the sensor returns values that
   * adhere to its datatype.
   *
   * @see Sensor#processEvent(String)
   * @see org.openremote.controller.model.sensor.StateSensor#read
   * @see org.openremote.controller.model.sensor.SwitchSensor#read
   * @see org.openremote.controller.protocol.EventListener
   * @see org.openremote.controller.protocol.ReadCommand
   *
   * @return sensor's value, according to its datatype and provided by protocol handlers (read
   *         command or event listener) or {@link #UNKNOWN_STATUS} if value cannot be found.
   */
  protected String read() // TODO : API visibility
  {
    // If this sensor abstracts an event listener, the read() will not invoke the protocol
    // handler associated with this sensor directly -- instead we try to fetch the latest
    // value produced by an event listener from the controller's global device state cache.

    if (isEventListener())
    {
      return deviceStateCache.queryStatusBySensorId(sensorID);
    }

    // If we are dealing with regular read commands, execute it to explicitly fetch the
    // device state...

    if (isPolling())
    {
      try
      {
        // Support legacy API...

        String returnValue;

        if (eventProducer instanceof StatusCommand)
        {
          StatusCommand statusCommand = (StatusCommand)eventProducer;

          returnValue = statusCommand.read(sensorType, sensorProperties);
        }

        else
        {
          ReadCommand command = (ReadCommand)eventProducer;

          returnValue = command.read(this);
        }

        //return processEvent(returnValue);
        return returnValue;
      }

      catch (Throwable t)
      {
        log.error(
            "Implementation error in protocol handler {0} : {1}",
            t, eventProducer, t.getMessage()                      // TODO : event producers should provide toString() impl.
        );

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


  // Inner Classes -------------------------------------------------------------------------------



  public class UnknownEvent extends Event
  {

    public UnknownEvent()
    {
      super(getSensorID(), getName());
    }

    @Override public String getValue()
    {
      return serialize();
    }

    @Override public String serialize()
    {
      return UNKNOWN_STATUS;
    }
  }


  /**
   * TODO
   */
  private class DeviceReader implements Runnable
  {

    /**
     * Indicates the device polling thread's run state. Notice that for immediate stop, setting
     * this to false is not sufficient, but the thread also must be interrupted. See
     * {@link DeviceReader#stop}.
     */
    private volatile boolean pollingThreadRunning = true;

    /**
     * The actual thread reference being used by the owning sensor instance when
     * {@link org.openremote.controller.protocol.ReadCommand} is used as event producer.
     */
    private Thread pollingThread;

    /**
     * Starts the device polling thread.
     */
    public void start()
    {
      pollingThreadRunning = true;
      
      pollingThread = OpenRemoteRuntime.createThread(
          "Polling Sensor Thread ID = " + getSensorID() + ", Name ='" + getName() + "'", this
      );

      pollingThread.start();
    }

    /**
     * Stops the device polling thread. Notice that stopping the thread will cause it to exit.
     * The same thread cannot be resumed but a new thread will be created via
     * {@link DeviceReader#start()}.
     */
    public void stop()
    {
      pollingThreadRunning = false;

      try
      {
        // ----- BEGIN PRIVILEGED CODE BLOCK ------------------------------------------------------

        AccessController.doPrivilegedWithCombiner(new PrivilegedAction<Void>()
        {
          @Override public Void run()
          {
            pollingThread.interrupt();

            return null;
          }
        });

        // ----- END PRIVILEGED CODE BLOCK ------------------------------------------------------
      }

      catch (SecurityException e)
      {
        log.warn(
            "Could not interrupt device polling thread ''{0}'' due to security constraints: {1}\n" +
            "the thread will exit in {2} milliseconds...",
            pollingThread.getName(), e.getMessage(), ReadCommand.POLLING_INTERVAL
        );
      }
    }


    // Implements Runnable ------------------------------------------------------------------------

    /**
     * Once every given interval defined in {@link ReadCommand#POLLING_INTERVAL}, invokes a read()
     * request on the sensor and the underlying event producer. Depending on event producer
     * implementation this may create a concrete request on the device to read current state, or
     * it may return a cached value from memory.
     */
    @Override public void run()
    {
      log.info("Started sensor (ID = {0}, type = {1}).", getSensorID(), getSensorType());

      while (pollingThreadRunning)
      {
        Sensor.this.update(read());

        try
        {
          Thread.sleep(ReadCommand.POLLING_INTERVAL);
        }
        catch (InterruptedException e)
        {
          pollingThreadRunning = false;

          log.info("Shutting down sensor (ID = {0}, type = {1}).", getSensorID(), getSensorType());

          Thread.currentThread().interrupt();
        }
      }
    }
  }



}
