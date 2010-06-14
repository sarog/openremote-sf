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
package org.openremote.controller.status_cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Javen Zhang
 *
 */
public class StatusCache {
   
   private TimeoutTable timeoutTable;

   private Map<Integer ,String> controlStatus = null;
   
   private ObservedStatusesSubject observedStatusesSubject = null;
   
   public StatusCache(){
      controlStatus = new HashMap<Integer,String>();
   }
   
   public StatusCache(TimeoutTable timeoutTable, ObservedStatusesSubject observers){
      this.timeoutTable = timeoutTable;
      this.observedStatusesSubject = observers;
   }
   
   public synchronized void saveOrUpdateStatus(Integer controlId, String status){
      String oldStatus = controlStatus.get(controlId);
      if (oldStatus == null || "".equals(oldStatus) || !oldStatus.equals(status)) {
         controlStatus.put(controlId, status);
         this.updateTimeoutTable(controlId);
         observedStatusesSubject.statusChanged(new StatusChangedData(controlId, status));
      }
   }
   
   public Map<Integer, String> queryStatuses(Set<Integer> controlIds){
      if(controlIds == null || controlIds.size() == 0){
         return null;
      }
      Map<Integer,String> statuses = new HashMap<Integer,String>();
      for (Integer controlId :controlIds){
         if (this.controlStatus.get(controlId) == null || "".equals(this.controlStatus.get(controlId))) {
            //TODO: throw NoSuchComponentException();
         } else {
            statuses.put(controlId, this.controlStatus.get(controlId));
         }
      }
      return statuses;
   }
   
   public String queryStatusByControlId(Integer controlId){
      return this.controlStatus.get(controlId);
   }
   

   public StatusCache(TimeoutTable timeoutTable) {
      super();
      this.timeoutTable = timeoutTable;
   }
   
   public TimeoutTable getTimeoutTable() {
      return timeoutTable;
   }


   public void setTimeoutTable(TimeoutTable timeoutTable) {
      this.timeoutTable = timeoutTable;
   }

   public void setObservedStatusesSubject(ObservedStatusesSubject observedStatusesSubject) {
      this.observedStatusesSubject = observedStatusesSubject;
   }

   private void updateTimeoutTable(Integer controlId){
      timeoutTable.updateStatusChangedIDs(controlId);
   }
}
