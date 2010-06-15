/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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

/**
 * In the experiment the other day, we found KNX has to read the status one by one, which is very slow if we want to
 * query 5 devices at one time. Considering quick response, we also add cache interface (dummy thread implementation for
 * now) for Controller status querying. 
 * 
 * 
 * In the future, there may and will be another asynchronous polling between Controller and
 * devices, because if user turns on the lamp switch on the wall, not from iPhone, Controller won't know this changed
 * state, so Controller has to do polling for devices more frequently. Since Controller hardware is more powerful than
 * iPhone hardware, we assume Controller can ensure it's synchronous with devices by all means.
 * 
 * @author Javen Zhang
 * 
 */
public class StatusCache {

   private ChangedStatusTable changedStatusTable;

   private Map<Integer, String> controlStatus = null;

   public StatusCache() {
      controlStatus = new HashMap<Integer, String>();
   }

   public StatusCache(ChangedStatusTable changedStatusTable) {
      super();
      this.changedStatusTable = changedStatusTable;
   }
   
   /**
    * This method is used to let the cache to store the status for all the device.
    * @param componentID
    * @param status
    */
   public synchronized void saveOrUpdateStatus(Integer componentID, String status) {
      String oldStatus = controlStatus.get(componentID);
      if (status == null || "".equals(status)) {
         throw new NullPointerException("The current status was null.");
      }
      
      boolean needNotify = false;
      controlStatus.put(componentID, status);
      if (oldStatus== null || "".equals(oldStatus) || !oldStatus.equals(status)) {
         needNotify = true;
      }
      
      if (needNotify) {
         updateChangedStatusTable(componentID);
      }
   }
   
   /**
    * This method is used to query the status whose component id in componentIDs. 
    * @param componentIDs
    * @return null if componentIDS is null.
    * @throws NoSuchComponentException when the component id is not cached. 
    */
   public Map<Integer, String> queryStatuses(Set<Integer> componentIDs) {
      if (componentIDs == null || componentIDs.size() == 0) {
         return null;
      }
      Map<Integer, String> statuses = new HashMap<Integer, String>();
      for (Integer controlId : componentIDs) {
         if (this.controlStatus.get(controlId) == null || "".equals(this.controlStatus.get(controlId))) {
            throw new NoSuchComponentException("No such component in status cache : " + controlId);
         } else {
            statuses.put(controlId, this.controlStatus.get(controlId));
         }
      }
      return statuses;
   }
   
   public String queryStatusByComponentlId(Integer componentId) {
      String result = this.controlStatus.get(componentId);
      if (result == null) {
         throw new NoSuchComponentException("no such a component whose id is :"+componentId);
      }
      return result;
   }

   public ChangedStatusTable getChangedStatusTable() {
      return changedStatusTable;
   }

   public void setChangedStatusTable(ChangedStatusTable changedStatusTable) {
      this.changedStatusTable = changedStatusTable;
   }

   private void updateChangedStatusTable(Integer controlId) {
      changedStatusTable.updateStatusChangedIDs(controlId);
   }
}
