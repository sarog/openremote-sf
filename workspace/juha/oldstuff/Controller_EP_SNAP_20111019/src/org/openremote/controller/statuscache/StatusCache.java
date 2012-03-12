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
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.openremote.controller.exception.NoSuchComponentException;
import org.openremote.controller.exception.ConfigurationException;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.protocol.Event;
import org.openremote.controller.Constants;
import org.openremote.controller.model.sensor.Sensor;

/**
 * TODO : related tasks ORCJAVA-101, ORCJAVA-102, ORCJAVA-112
 *
 * @author @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Javen Zhang
 */
public class StatusCache
{

  // Class Members --------------------------------------------------------------------------------

  private final static Logger log = Logger.getLogger(Constants.RUNTIME_STATECACHE_LOG_CATEGORY);


  // Private Instance Fields ----------------------------------------------------------------------

  private ChangedStatusTable changedStatusTable;

  private Map<Integer, String> sensorStatus = null;

  private EventProcessorChain eventProcessorChain;

  private Map<Integer, Sensor> sensors = new ConcurrentHashMap<Integer, Sensor>();

  /**
   * TODO
   */
  private boolean isShutdownInProcess = false;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * TODO
   */
  public StatusCache()
  {
    this(new ChangedStatusTable(), new EventProcessorChain());
  }

  /**
   * TODO
   * 
   * @param cst
   */
  public StatusCache(ChangedStatusTable cst, EventProcessorChain epc)
  {
    sensorStatus = new HashMap<Integer, String>();

    this.changedStatusTable = cst;
    this.eventProcessorChain = epc;
  }




  // Public Instance Methods ----------------------------------------------------------------------


  /**
   * TODO
   *
   * @param sensor
   */
  public void registerSensor(Sensor sensor)
  {
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

    sensor.update(Sensor.UNKNOWN_STATUS);

    initLog.debug("Initialized sensor ''{0}'' to ''{1}''", sensor.toString(), Sensor.UNKNOWN_STATUS);

    initLog.info("Registered sensor : {0}", sensor.toString());
  }


  /**
   * TODO
   *
   * note that iterator allows modifications
   *
   * @return
   */
  public Iterator<Sensor> listSensors()
  {
    return sensors.values().iterator();
  }


  /**
   * TODO
   *
   * @param id
   * @return
   */
  public Sensor getSensor(int id)
  {
    return sensors.get(id);
  }


  public void shutdown()
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



  
  /**
   * TODO :
   *   - using method level synchronization to ensure we're at same concurrency control mechanism
   *     with the collection we are tweaking compared to the original method above
   *
   *     See ORCJAVA-102 -- http://jira.openremote.org/browse/ORCJAVA-102
   */
  public synchronized void update(Event event)
  {
    if (isShutdownInProcess)
    {
      log.debug(
          "Device state cache is shutting down. Ignoring update from ''{0}'' (ID = ''{1}'').",
          event.getSource(), event.getSourceID()
      );

      return;
    }


    eventProcessorChain.push(event);

    String eventValue = event.serialize();
    
    String oldStatus = sensorStatus.get(event.getSourceID());
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
    * This method is used to query the status whose component id in componentIDs. 
    * @param sensorIDs
    * @return null if componentIDS is null.
    * @throws NoSuchComponentException when the component id is not cached. 
    */
   public Map<Integer, String> queryStatuses(Set<Integer> sensorIDs) {
      if (sensorIDs == null || sensorIDs.size() == 0) {
         return null;
      }

      log.trace("Query status for sensor IDs : {0}", sensorIDs);

      Map<Integer, String> statuses = new HashMap<Integer, String>();
      for (Integer sensorId : sensorIDs) {
         if (this.sensorStatus.get(sensorId) == null || "".equals(this.sensorStatus.get(sensorId))) {
            throw new NoSuchComponentException("No such component in status cache : " + sensorId);
         } else {
            statuses.put(sensorId, this.sensorStatus.get(sensorId));
         }
      }

      log.trace("Returning sensor status map (ID, Value) : {0}", statuses);

      return statuses;
   }
   
  public String queryStatusBySensorId(Integer sensorID) // TODO : should rename
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

//  @Deprecated public String queryStatus(Integer sensorId)
//  {
//   return this.sensorStatus.get(sensorId);
//  }


  // Private Instance Methods ---------------------------------------------------------------------


  private void clearChangedStatuses()
  {
    Iterator<Sensor> iterator = listSensors();

    while (iterator.hasNext())
    {
      Sensor sensor = iterator.next();

      // Just wake up all the records, acturelly, the status didn't change.
      changedStatusTable.updateStatusChangedIDs(sensor.getSensorID());
    }

    changedStatusTable.clearAllRecords();
  }



  /**
   * TODO
   */
  private void stopSensors()
  {
    Iterator<Sensor> allSensors = listSensors();

    while (allSensors.hasNext())
    {
      Sensor sensor = allSensors.next();

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
