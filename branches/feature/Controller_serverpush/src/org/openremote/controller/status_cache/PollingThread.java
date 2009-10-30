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

import org.apache.log4j.Logger;
import org.openremote.controller.spring.SpringContext;

/**
 * Polling Thread is responsiable for observe the statuses which it care about.
 * 
 * @author Handy.Wang 2009-10-21
 */
public class PollingThread extends Thread {
   
   /**
    * A boolean flag which indicates whether current thread need to observe the status change persistently.<br /><br />
    * 
    * This property will be set to flase by servlet thread when the process of observe status change has time out.
    */
   private boolean isWaitingStatusChange = true;
   
   private SkippedStatusTable skippedStatusTable = (SkippedStatusTable) SpringContext.getInstance().getBean("skippedStatusTable"); 
   
   /** 
    * It store the controlIDs which a polling request associate with 
    *   and store the changed statuses.<br /><br />
    *   
    * This pollingData property mainly provide for a polling servlet thread
    *   whitch associate with current <b>PollingThread</b> instance.<br /><br />
    *   
    * This pollingData property is initialized by Constructor which has a parameter of PollingData type
    *   or setter method.
    */
   private PollingData pollingData;
   
   private Logger logger = Logger.getLogger(this.getClass().getName());
   
   public PollingThread() {
      super();
   }
   
   public PollingThread(PollingData pollingData) {
      this.pollingData = pollingData;
   }
   
   /**
    * It's the mainly implementation of current thread.<br />
    * It's mainly responsible for observe the status change and get the changed statuses.
    */
   @Override
   public void run() {
      ObservedStatusesSubject observedStatusesSubject = (ObservedStatusesSubject) SpringContext.getInstance().getBean("observedStatusesSubject");
      StatusesChangedObserver statusChangeObserver = new StatusesChangedObserver(observedStatusesSubject, pollingData.getControlIDs());
      logger.info("Observing change of component status ...");
      while(isWaitingStatusChange) {
         StatusChangedData statusChangeData = statusChangeObserver.getStatusChangeData();
         if (statusChangeData != null) {
            Set<Integer> controlIds = statusChangeObserver.getPollingControlIDs();
//            Iterator<Integer> idsIterator = controlIds.iterator();
            if (controlIds.size() > 1 ){
               skippedStatusTable.insert(new SkippedStatusRecord(pollingData.getDeviceId(),pollingData.getControlIDs()));
            }
            Map<Integer, String> changedStatuses = new HashMap<Integer, String>();
            changedStatuses.put(statusChangeData.getStatusChangedControlID(), statusChangeData.getCurrentStatusAfterChanged());
            pollingData.setChangedStatuses(changedStatuses);
            break;
         }
      }
   }

   public PollingData getPollingData() {
      return pollingData;
   }

   public void setPollingData(PollingData pollingData) {
      this.pollingData = pollingData;
   }

   public void setWaitingStatusChange(boolean isWaitingStatusChange) {
      this.isWaitingStatusChange = isWaitingStatusChange;
   }
   
}
