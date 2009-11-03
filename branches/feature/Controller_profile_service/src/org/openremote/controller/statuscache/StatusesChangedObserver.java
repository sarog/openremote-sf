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

import java.util.Set;

/**
 * This Observer class is responsible for 
 *   observing the change of statuses which associate with control ids.
 * 
 * @author Handy.Wang 2009-10-21
 */
public class StatusesChangedObserver {

   /** The control ids which a polling request care about. */
   private Set<Integer> pollingControlIDs;
   
   /**
    * The subject which current observer instance observed.
    * It contains many registered observers and new status of status change.
    * It also provide a method named statusChanged for Cached status DataBase
    *   in order to let subject know that the status was changed and nofity the
    *   corresponding observers.
    */
   private ObservedStatusesSubject observedStatusesSubject;
   
   /**
    * When current observer instance is notified that status was changed,
    * This variable will be set value(Including properties: control id and new status of changed device).
    */
   private StatusChangedData statusChangeData;
   
   public StatusesChangedObserver() {
      super();
   }
   
   public StatusesChangedObserver(ObservedStatusesSubject observedStatusesSubject, Set<Integer> pollingControlIDs) {
      super();
      this.observedStatusesSubject = observedStatusesSubject;
      this.observedStatusesSubject.registerObserver(this);
      this.pollingControlIDs = pollingControlIDs;
   }
   
   /**
    * This method will be called by ObservedStatusSubject for notifying observer certain status was changed.
    *   and let observer know that current observer instance geted the changed status data. 
    */
   public void update(StatusChangedData statusChangeData) {
      this.statusChangeData = statusChangeData;
   }

   public Set<Integer> getPollingControlIDs() {
      return pollingControlIDs;
   }

   public void setPollingControlIDs(Set<Integer> pollingControlIDs) {
      this.pollingControlIDs = pollingControlIDs;
   }

   public ObservedStatusesSubject getObservedStatusesSubject() {
      return observedStatusesSubject;
   }

   public void setObservedStatusesSubject(ObservedStatusesSubject observedStatusesSubject) {
      this.observedStatusesSubject = observedStatusesSubject;
   }

   public StatusChangedData getStatusChangeData() {
      return statusChangeData;
   }

}
