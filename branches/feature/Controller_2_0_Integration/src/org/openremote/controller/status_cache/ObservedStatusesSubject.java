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
import java.util.Iterator;
import java.util.List;

/**
 * This class include all Observers and provides the changed status for corresponding Observers.
 * 
 * @author Handy.Wang 2009-10-21
 */
public class ObservedStatusesSubject {
   
   /** observer list of status change. */
   private List<StatusesChangedObserver> statusChangeObservers;
   
   /** The data after status change. */
   private StatusChangedData statusChangeData;
   
   public ObservedStatusesSubject() {
      statusChangeObservers = new ArrayList<StatusesChangedObserver>();
   }

   /**
    * Register the observer to this subject. 
    */
   public void registerObserver(StatusesChangedObserver statusChangeObserver) {
      statusChangeObservers.add(statusChangeObserver);
   }

   /**
    * Notify the proper observers which associate with the control id of changed status.
    * 
    * The notified observers will get the change status.
    */
   public void notifyObserver() {
      Iterator<StatusesChangedObserver> iterator = statusChangeObservers.iterator();
      while (iterator.hasNext()) {
         StatusesChangedObserver statusChangeObserver = iterator.next();
         if (containControlID(statusChangeObserver)) {
            statusChangeObserver.update(statusChangeData);
            iterator.remove();
         }
      }
   }
   
   /**
    * This method will called by status change event of Cached Status DataBase. 
    */
   public void statusChanged(StatusChangedData statusChangeData) {
      this.statusChangeData = statusChangeData;
      notifyObserver();
   }
   
   /**
    * Judge if the observer contain the changed control id. 
    */
   private boolean containControlID(StatusesChangedObserver statusChangeObserver) {
      for (String pollingControlID : statusChangeObserver.getPollingControlIDs()) {
         if (this.statusChangeData.getStatusChangedControlID().equals(pollingControlID)) {
            return true;
         }
      }
      return false;
   }
   
}
