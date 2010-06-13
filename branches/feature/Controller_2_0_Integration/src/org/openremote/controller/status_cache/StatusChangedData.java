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

/**
 * This class mainly store the data which status changed.<br /><br />
 * 
 * It is mainly used to transfer new status data between <b>ObservedStatusesSubject</b>
 *   and <b>StatusChangeObserver</b>.
 * 
 * @author Handy.Wang 2009-10-21
 */
public class StatusChangedData {

   /** The control id of status changed . */
   private String statusChangedControlID;

   /** The current status of status changed. */
   private String currentStatusAfterChanged;
   
   public StatusChangedData() {
      super();
   }
   
   /**
    * Initialized by control id and current status of status changed. 
    */
   public StatusChangedData(String statusChangedControlID, String currentStatusAfterChanged) {
      super();
      this.currentStatusAfterChanged = currentStatusAfterChanged;
      this.statusChangedControlID = statusChangedControlID;
   }

   public String getStatusChangedControlID() {
      return statusChangedControlID;
   }

   public void setStatusChangedControlID(String statusChangedControlID) {
      this.statusChangedControlID = statusChangedControlID;
   }

   public String getCurrentStatusAfterChanged() {
      return currentStatusAfterChanged;
   }

   public void setCurrentStatusAfterChanged(String currentStatusAfterChanged) {
      this.currentStatusAfterChanged = currentStatusAfterChanged;
   }
   
}
