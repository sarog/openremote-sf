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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Use wait, notify, synchronize mechanism to do polling.<br /><br /> 
 * This table is used to record the skipped changed statuses and waited changed statuses .<br />
 * <b>Use Case1:</b>&nbsp;During the process of iPhone refreshing the polling connection or
 * dealing with the response of changed state, a later (very soon, before iPhone reestablishes the next polling,
 * although it's kind of probability stuff) change won't be detected by iPhone , in other word, this polling request has left
 * the changed status. the result is when iPhone comes back to continue polling, it
 * knows nothing about what has happened just now, and iPhone will keep the old view and waiting for the next change
 * which is not synchronous any more.<br />
 * 
 * <b>Use Case2:</b>If no statuses changed, polling request will <b>WAIT</b><br /> the Corresponded ChangedStatusRecord until<br />
 * the waited polling control ids' statuses changed. So, the polling request will be notified and get the change statuses.<br />
 * 
 * @author Handy.Wang 2009-10-23
 */
public class ChangedStatusTable {
   
   private List<ChangedStatusRecord> recordList;
   
//   private Logger logger = Logger.getLogger(this.getClass().getName());

   public ChangedStatusTable() {
      super();
      recordList = new ArrayList<ChangedStatusRecord>();
   }

   /**
    * Insert a changed status record.
    */
   public synchronized void insert(ChangedStatusRecord record) {
      if (this.query(record.getDeviceID(), record.getPollingControlIDs()) == null) { 
         recordList.add(record);
      }
   }
   
   /**
    * Query changed status record by deviceID and pollingControlIDs(pollingControlIDs is not order-insensitive).
    */
   public synchronized ChangedStatusRecord query(String deviceID, List<Integer> pollingControlIDs) {
      if (recordList.size() == 0 || pollingControlIDs == null || pollingControlIDs.size() == 0) {
         return null;
      }
      ChangedStatusRecord record = new ChangedStatusRecord(deviceID, pollingControlIDs);
      
      for (ChangedStatusRecord tempRecord : recordList) {
         if (tempRecord.equals(record)) {
            return tempRecord;
         }
      }
      return null;
   }
   
   /**
    * Query all changed status records whose pollingControlID column contains statusChangeControlID.
    */
   public synchronized List<ChangedStatusRecord> query(Integer statusChangedControlID) {
      List<ChangedStatusRecord> statusChangedRecord = new ArrayList<ChangedStatusRecord>();
      if(recordList==null||recordList.size()==0){
         return null;
      }
      for (ChangedStatusRecord record : recordList) {
         if (record.getPollingControlIDs().contains(statusChangedControlID)) {
            statusChangedRecord.add(record);
         }
      }
      return statusChangedRecord;
   }
   
   /**
    * Update status_changed_id column.
    */
   public void updateStatusChangedIDs(Integer statusChangedControlID) {
      for(ChangedStatusRecord record : recordList){
         synchronized (record) {
            if (record.getPollingControlIDs() != null && record.getPollingControlIDs().size() != 0) {
               for (Integer tmpControlId : record.getPollingControlIDs()) {
                  if (statusChangedControlID.equals(tmpControlId)) {
                     record.getStatusChangedIDs().add(statusChangedControlID);
                     record.notifyAll();
                     break;
                  }
               }
            }
         }
      }
   }
   
   /**
    * Reset changed status of panel in {@link ChangedStatusTable}. 
    */
   public synchronized void resetChangedStatusIDs(String deviceID, List<Integer> pollingControlIDs) {
      ChangedStatusRecord skippedStatusRecord = this.query(deviceID, pollingControlIDs);
      skippedStatusRecord.setStatusChangedIDs(new HashSet<Integer>());
   }

}
