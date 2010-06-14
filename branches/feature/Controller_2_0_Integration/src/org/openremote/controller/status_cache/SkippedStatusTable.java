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
 * We use subject-listener pattern to do polling. During the process of iPhone is refreshing the polling connection or
 * dealing with the response of changed state, a later (very soon, before iPhone reestablishes the next polling,
 * although it's kind of probability stuff) change won't be detected by iPhone , in other word, this listener has left
 * the subject and subject won't notify it at that time. the result is when iPhone comes back to continue polling, it
 * knows nothing about what has happened just now, and iPhone will keep the old view and waiting for the next change
 * which is not synchronous any more.
 * 
 * This table is used to record the skipped status .
 * @author Handy.Wang 2009-10-23
 */
public class SkippedStatusTable {
   
   private List<SkippedStatusRecord> recordList;
   
   private Logger logger = Logger.getLogger(this.getClass().getName());

   public SkippedStatusTable() {
      super();
      recordList = new ArrayList<SkippedStatusRecord>();
   }

   /**
    * Insert a timeout record into TIME_OUT table.
    */
   public synchronized void insert(SkippedStatusRecord record) {
      recordList.add(record);
   }
   
   /**
    * Delete a record from TIME_OUT table.
    */
   public synchronized void delete(SkippedStatusRecord record) {
      for (Iterator<SkippedStatusRecord> iterator = recordList.iterator(); iterator.hasNext(); ) {
         if (record.equals(iterator.next())) {
            iterator.remove();
         }
      }
      logger.info("Delete the found timeout record.");
   }
   
   /**
    * Query timeout record by deviceID and pollingControlIDs(order-insensitive).
    */
   public synchronized SkippedStatusRecord query(String deviceID, List<Integer> pollingControlIDs) {
      if (recordList.size() == 0 || pollingControlIDs == null || pollingControlIDs.size() == 0) {
         return null;
      }
      SkippedStatusRecord record = new SkippedStatusRecord(deviceID, pollingControlIDs);
      
      for (SkippedStatusRecord tempRecord : recordList) {
         if (tempRecord.equals(record)) {
            return tempRecord;
         }
      }
      return null;
   }
   
   /**
    * Query timeout record by pollingControlIDs(order-insensitive). 
    */
   public synchronized SkippedStatusRecord query(List<Integer> pollingControlIDs) {
      if (pollingControlIDs == null || pollingControlIDs.size() == 0) {
         return null;
      }
      
      Collections.sort(pollingControlIDs, new PollingControlIDListComparator());
      for (SkippedStatusRecord tempRecord : recordList) {
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
   public synchronized List<SkippedStatusRecord> query(Integer statusChangedControlID) {
      List<SkippedStatusRecord> statusChangedRecord = new ArrayList<SkippedStatusRecord>();
      if(recordList==null||recordList.size()==0){
         return null;
      }
      for (SkippedStatusRecord record : recordList) {
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
      for(SkippedStatusRecord record : recordList){
         if(record.getPollingControlIDs()!=null && record.getPollingControlIDs().size()!=0){
         for (Integer tmpControlId : record.getPollingControlIDs()) {
               if (statusChangedControlID.equals(tmpControlId)) {
                  record.getStatusChangedIDs().add(statusChangedControlID);
                  break;
               }
            }
         }
      }
   }
   
   public synchronized SkippedStatusRecord queryRecordByDeviceId(String deviceId) {
      if (recordList != null & recordList.size() != 0) {
         for (SkippedStatusRecord record : recordList) {
            if (record.getDeviceID().equals(deviceId)) {
               return record;
            }
         }
      }
      return null;
   }
}
