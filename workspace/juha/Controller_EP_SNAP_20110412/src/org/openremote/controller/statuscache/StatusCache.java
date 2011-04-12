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

import org.openremote.controller.exception.NoSuchComponentException;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.protocol.Event;
import org.openremote.controller.Constants;

/**
 * TODO
 *
 * @author Javen Zhang
 */
public class StatusCache
{

  // Class Members --------------------------------------------------------------------------------

  private final static Logger logger = Logger.getLogger(Constants.RUNTIME_STATECACHE_LOG_CATEGORY);


  // Private Instance Fields ----------------------------------------------------------------------

  private ChangedStatusTable changedStatusTable;

  private Map<Integer, String> sensorStatus = null;

  private EventProcessorChain eventProcessorChain;


  // Constructors ---------------------------------------------------------------------------------

  public StatusCache()
  {
    sensorStatus = new HashMap<Integer, String>();
  }

//  public StatusCache(ChangedStatusTable changedStatusTable)
//  {
//    super();
//    this.changedStatusTable = changedStatusTable;
//  }



  // Public Instance Methods ----------------------------------------------------------------------


   /**
    * This method is used to let the cache to store the status for all the device.
    * @param componentID
    * @param status
    */
   public synchronized void saveOrUpdateStatus(Integer componentID, String status) {
      String oldStatus = sensorStatus.get(componentID);
      if (status == null || "".equals(status)) {
         logger.info("Status is null or \"\" while calling saveOrUpdateStatus in statusCache.");
         return;
      }


     // TODO : fix the logic below, it makes little sense  [JPL]

      boolean needNotify = false;
      sensorStatus.put(componentID, status);
      if (oldStatus== null || "".equals(oldStatus) || !oldStatus.equals(status)) {
         needNotify = true;
      }
      
      if (needNotify) {
         updateChangedStatusTable(componentID);
      }
   }

  /**
   * TODO :
   *   - using method level synchronization to ensure we're at same concurrency control mechanism
   *     with the collection we are tweaking compared to the original method above
   *
   */
  public synchronized void update(Event event)
  {
    eventProcessorChain.push(event);
     

    String oldStatus = sensorStatus.get(event.getSourceID());
    sensorStatus.put(event.getSourceID(), event.serialize());

    if (oldStatus == null || oldStatus.equals("") || !oldStatus.equals(event.getValue()))
    {
      changedStatusTable.updateStatusChangedIDs(event.getSourceID());
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
      Map<Integer, String> statuses = new HashMap<Integer, String>();
      for (Integer sensorId : sensorIDs) {
         if (this.sensorStatus.get(sensorId) == null || "".equals(this.sensorStatus.get(sensorId))) {
            throw new NoSuchComponentException("No such component in status cache : " + sensorId);
         } else {
            statuses.put(sensorId, this.sensorStatus.get(sensorId));
         }
      }
      return statuses;
   }
   
   public String queryStatusBySensorlId(Integer sensorId) {
      String result = this.sensorStatus.get(sensorId);
      if (result == null) {
         throw new NoSuchComponentException("no such a component whose id is :"+sensorId);
      }
      return result;
   }

   public String queryStatus(Integer sensorId)
   {
     return this.sensorStatus.get(sensorId);
   }
  
   public void clear() {
      this.sensorStatus.clear();
   }

   public ChangedStatusTable getChangedStatusTable() {
      return changedStatusTable;
   }

   public void setChangedStatusTable(ChangedStatusTable changedStatusTable) {
      this.changedStatusTable = changedStatusTable;
   }


  // Service Dependencies -------------------------------------------------------------------------

  public void setEventProcessorChain(EventProcessorChain processorChain)
  {
    this.eventProcessorChain = processorChain;
  }



  // Private Instance Methods ---------------------------------------------------------------------

   private void updateChangedStatusTable(Integer controlId) {
      changedStatusTable.updateStatusChangedIDs(controlId);
   }
}
