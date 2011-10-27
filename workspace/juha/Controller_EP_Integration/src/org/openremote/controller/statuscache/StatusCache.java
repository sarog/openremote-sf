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
package org.openremote.controller.statuscache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.openremote.controller.Constants;
import org.openremote.controller.exception.NoSuchComponentException;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.Event;
import org.openremote.controller.utils.Logger;

/**
 * TODO
 *
 * @author @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Javen Zhang
 */
public class StatusCache
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * Common status cache logging category on operations that occur during runtime (not part
   * of lifecycle start/stop operations).
   */
  private final static Logger log = Logger.getLogger(Constants.RUNTIME_STATECACHE_LOG_CATEGORY);


  // Private Instance Fields ----------------------------------------------------------------------

  /**
   * TODO : ChangedStatusTable implementation requires a thorough review
   */
  private ChangedStatusTable changedStatusTable;

  /**
   * Maintains a map of sensor ids to their values in cache.
   */
  private Map<Integer, String> sensorStatus = new ConcurrentHashMap<Integer, String>();

  /**
   * A chain of event processors that incoming events (values) are forced through before
   * their values are stored in the cache. <p>
   *
   * Event processors may modify the existing values, discard events entirely or spawn
   * multiple other events that are included in the state cache.
   */
  private EventProcessorChain eventProcessorChain;

  /**
   * Map of sensor IDs to actual sensor instances.
   */
  private Map<Integer, Sensor> sensors = new ConcurrentHashMap<Integer, Sensor>();

  /**
   * Used to indicate if the state cache is in the middle of a shut down process -- this
   * flag can be used by methods to fail-fast in such cases.
   */
  private volatile Boolean isShutdownInProcess = false;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * TODO : need to thoroughly review ChangedStatusTable use
   */
  public StatusCache()
  {
    this(new ChangedStatusTable(), new EventProcessorChain());
  }

  /**
   * TODO : need to thoroughly review ChangedStatusTable use
   */
  public StatusCache(ChangedStatusTable cst, EventProcessorChain epc)
  {
    this.changedStatusTable = cst;
    this.eventProcessorChain = epc;
  }




  // Public Instance Methods ----------------------------------------------------------------------


  /**
   * Register a sensor with this cache instance. The registered sensor will participate in
   * cache's lifecycle.
   *
   * @param sensor    sensor to register
   */
  public void registerSensor(Sensor sensor)
  {
    if (isShutdownInProcess)
      return;

    Sensor previous = sensors.put(sensor.getSensorID(), sensor);

    // Use a specific log category just to log the creation of sensor objects
    // in this method (happens at startup or soft restart)...

    Logger initLog = Logger.getLogger(Constants.SENSOR_INIT_LOG_CATEGORY);

    if (previous != null)
    {
      initLog.error(
          "Duplicate registration of sensor ID {0}. Sensor ''{1}'' has replaced ''{2}''.",
          sensor.getSensorID(), sensor.getName(), previous.getName()
      );
    }

    // TODO :
    //   Use of Sensor.UNKNOWN_STATUS should go away once we store and return Events rather than
    //   serializing to untyped strings, see ORCJAVA-203

    sensorStatus.put(sensor.getSensorID(), Sensor.UNKNOWN_STATUS);

    initLog.debug("Initialized sensor ''{0}'' to ''{1}''", sensor.toString(), Sensor.UNKNOWN_STATUS);

    initLog.info("Registered sensor : {0}", sensor.toString());
  }



  /**
   * Returns a sensor instance associated with the given ID.
   *
   * @param id    sensor ID
   *
   * @return      sensor instance
   */
  public Sensor getSensor(int id)
  {
    // TODO :
    //   Should eventually return a sensor interface, see ORCJAVA-123

    // TODO :
    //   This method is currently only consumed by Deployer implementation. The deployer in turn
    //   is only using it to host a temporary method that may still move due to refactoring.
    //   At that point the whole delegation through Deployer may not be very meaningful.
    
    return sensors.get(id);
  }


  /**
   * Performs a state cache cleanup at shut down. This method is synchronized, preventing
   * concurrent thread access to shutdown steps. <p>
   *
   * Part of the shutdown of state cache:
   * <ul>
   * <li>registered sensors are stopped</li>
   * <li>the in-memory states are cleared</li>
   * <li>registered sensors are unregistered</li>
   * </ul>
   *
   * This allows more orderly cleanup of the resources associated with this state cache --
   * namely the sensor threads are allowed to cleanup and stop properly. <p>
   *
   * Once the shutdown is completed, this cache instance can be discarded. There's no corresponding
   * start operation to allow reuse of this object.
   */
  public synchronized void shutdown()
  {
    {
      try
      {
        isShutdownInProcess = true;

        stopSensors();

        clearChangedStatuses();

        sensorStatus.clear();

        sensors.clear();
      }

      finally
      {
        isShutdownInProcess = false;
      }
    }
  }


  
  /**
   * Updates an incoming event value into cache. <p>
   *
   * <b>TODO:</b>
   *
   * This method is currently synchronized to restrict concurrency -- events are processed
   * and updated one-by-one. The implications of concurrent event processing through the processors
   * and concurrent updates must be evaluated. See ORCJAVA-205.
   *
   * @param event   the event to process -- the actual value stored in this cache will depend
   *                on the modifications made by event processors associated with this cache
   */
  public synchronized void update(Event event)
  {

    // fail fast on incoming sensor updates, if we want to shut things down already...

    if (isShutdownInProcess)
    {
      log.debug(
          "Device state cache is shutting down. Ignoring update from ''{0}'' (ID = ''{1}'').",
          event.getSource(), event.getSourceID()
      );

      return;
    }


    // push incoming event through processing chain -- keep the last returned instance including
    // modifications if any...

    event = eventProcessorChain.push(event);


    // TODO :
    //   Serializing to strings early -- should go away once we store Events, see ORCJAVA-203

    String oldStatus = sensorStatus.get(event.getSourceID());

    String eventValue = event.serialize();

    // Store...

    sensorStatus.put(event.getSourceID(), eventValue);

    log.trace(
        "Updated cache with Sensor ID = {0} (''{1}'') with value ''{2}''.",
        event.getSourceID(), event.getSource(), eventValue
    );

    if (oldStatus == null || oldStatus.equals("") || !oldStatus.equals(eventValue))
    {
      changedStatusTable.updateStatusChangedIDs(event.getSourceID());

      log.trace(
          "Marked Sensor ID = {0} (''{1}'') changed.", event.getSourceID(), event.getSource()
      );
    }
  }


  /**
   * TODO :
   *   Not sure we need this, could just loop on caller side to do multiple queryStatus().
   *   May be more consistent, especially because of the unoptimal exception handling here, etc.
   *
   *   See ORCJAVA-206.
   *
   * @throws NoSuchComponentException when the component id is not cached.
   */
  public Map<Integer, String> queryStatus(Set<Integer> sensorIDs)
  {
    if (sensorIDs == null || sensorIDs.size() == 0)
    {
       return null;     // TODO : return an empty collection instead
    }

    log.trace("Query status for sensor IDs : {0}", sensorIDs);

    Map<Integer, String> statuses = new HashMap<Integer, String>();

    for (Integer sensorId : sensorIDs) 
    {
       if (this.sensorStatus.get(sensorId) == null || "".equals(this.sensorStatus.get(sensorId)))
       {
         // TODO : do not throw an exception, instead return 'unknown value'
         
         throw new NoSuchComponentException("No such component in status cache : " + sensorId);
       }

       else
       {
          statuses.put(sensorId, this.sensorStatus.get(sensorId));
       }
    }

    log.trace("Returning sensor status map (ID, Value) : {0}", statuses);

    return statuses;
  }


  /**
   * Returns the current in-memory state of the given sensor ID.
   *
   * TODO : ORCJAVA-203 -- migrate to Event API
   *
   * @param sensorID    requested sensor ID
   *
   * @return  current cache-stored value for the given sensor ID
   */
  public String queryStatus(Integer sensorID)
  {
    String result = this.sensorStatus.get(sensorID);

    if (result == null)
    {
      log.error(
          "Requested sensor id ''{0}'' was not found. Defaulting to ''{1}''.",
          sensorID, Sensor.UNKNOWN_STATUS);

      return Sensor.UNKNOWN_STATUS;
    }

    return result;
  }



  // Private Instance Methods ---------------------------------------------------------------------


  private void clearChangedStatuses()
  {
    for (Sensor sensor : sensors.values())
    {
      // Just wake up all the records, acturelly, the status didn't change.
      changedStatusTable.updateStatusChangedIDs(sensor.getSensorID());
    }

    changedStatusTable.clearAllRecords();
  }


  private void stopSensors()
  {
    for (Sensor sensor : sensors.values())
    {
      log.info(
          "Stopping sensor ''{0}'' (ID = ''{1}'')...",
          sensor.getName(), sensor.getSensorID()
      );

      try
      {
        sensor.stop();
      }

      catch (Throwable t)
      {
        log.error(
            "Failed to stop sensor ''{0}'' (ID = ''{1}'') : {2}",
            t, sensor.getName(), sensor.getSensorID(), t.getMessage()
        );
      }
    }
  }

}
