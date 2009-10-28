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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * TIME_OUT table for record skip-state.
 * 
 * @author Handy.Wang 2009-10-23
 */
public class TimeoutTable {
   
   private List<TimeoutRecord> recordList;
   
   private Logger logger = Logger.getLogger(this.getClass().getName());

   public TimeoutTable() {
      super();
      recordList = new ArrayList<TimeoutRecord>();
   }

   /**
    * Insert a timeout record into TIME_OUT table.
    */
   public synchronized void insert(TimeoutRecord record) {
      recordList.add(record);
   }
   
   /**
    * Delete a record from TIME_OUT table.
    */
   public synchronized void delete(TimeoutRecord record) {
      for (Iterator<TimeoutRecord> iterator = recordList.iterator(); iterator.hasNext(); ) {
         if (record.equals(iterator.next())) {
            iterator.remove();
         }
      }
      logger.info("Delete the found timeout record.");
   }
   
   /**
    * Query timeout record by deviceID and pollingControlIDs(order-insensitive).
    */
   public synchronized TimeoutRecord query(String deviceID, List<Integer> pollingControlIDs) {
      if (pollingControlIDs == null || pollingControlIDs.size() == 0) {
         return null;
      }
      TimeoutRecord record = new TimeoutRecord(deviceID, pollingControlIDs);
      for (TimeoutRecord tempRecord : recordList) {
         if (tempRecord.equals(record)) {
            return tempRecord;
         }
      }
      return null;
   }
   
   /**
    * Query timeout record by pollingControlIDs(order-insensitive). 
    */
   public synchronized TimeoutRecord query(List<Integer> pollingControlIDs) {
      if (pollingControlIDs == null || pollingControlIDs.size() == 0) {
         return null;
      }
      
      Collections.sort(pollingControlIDs, new PollingControlIDListComparator());
      for (TimeoutRecord tempRecord : recordList) {
         List<Integer> tempPollingControlIDs = tempRecord.getPollingControlIDs();
         if (tempPollingControlIDs.size() != pollingControlIDs.size()) {
            return null;
         }
         Collections.sort(tempPollingControlIDs, new PollingControlIDListComparator());
         for (int i = 0; i < tempPollingControlIDs.size(); i++) {
            if (!tempPollingControlIDs.get(i).equals(pollingControlIDs.get(i))) {
               return null;
            }
         }
         return tempRecord;
      }
      return null;
   }
   
   /**
    * Query all timeout records whose pollingControlID column contains statusChangeControlID.
    */
   public synchronized List<TimeoutRecord> query(Integer statusChangedControlID) {
      List<TimeoutRecord> statusChangedRecord = new ArrayList<TimeoutRecord>();
      for (TimeoutRecord record : recordList) {
         if (record.getPollingControlIDs().contains(statusChangedControlID)) {
            statusChangedRecord.add(record);
         }
      }
      return statusChangedRecord;
   }
   
   /**
    * Update status_changed_id column.
    */
   public synchronized void updateStatusChangedIDs(Integer statusChangedControlID) {
      for(TimeoutRecord record : recordList){
         for(Integer tmpControlId : record.getPollingControlIDs()){
            if(statusChangedControlID.equals(tmpControlId)){
               record.getStatusChangedIDs().add(statusChangedControlID);
               break;
            }
         }
      }
   }
   
}
