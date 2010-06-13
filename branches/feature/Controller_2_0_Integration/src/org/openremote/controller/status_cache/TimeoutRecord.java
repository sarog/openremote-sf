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
import java.util.List;

/**
 * A timeout record.
 * 
 * @author Handy.Wang 2009-10-23
 */
public class TimeoutRecord {
   
   private String deviceID;

   private List<String> pollingControlIDs;

   private List<String> statusChangedIDs;

   public TimeoutRecord() {
      super();
      deviceID = "";
      pollingControlIDs = new ArrayList<String>();
      statusChangedIDs = new ArrayList<String>();
   }

   public TimeoutRecord(String deviceID, List<String> pollingControlIDs) {
      super();
      this.deviceID = deviceID;
      this.pollingControlIDs = pollingControlIDs;
      this.statusChangedIDs = new ArrayList<String>();
   }
   
   public TimeoutRecord(String deviceID, String[] pollingControlIDs) {
      super();
      this.deviceID = deviceID;
      this.pollingControlIDs = new ArrayList<String>();
      this.setPollingControlIDs(pollingControlIDs);
      this.statusChangedIDs = new ArrayList<String>();
   }

   public String getDeviceID() {
      return deviceID;
   }

   public void setDeviceID(String deviceID) {
      this.deviceID = deviceID;
   }

   public List<String> getPollingControlIDs() {
      return pollingControlIDs;
   }

   public void setPollingControlIDs(List<String> pollingControlIDs) {
      this.pollingControlIDs = pollingControlIDs;
   }

   public List<String> getStatusChangedIDs() {
      return statusChangedIDs;
   }

   public void setStatusChangedIDs(List<String> statusChangedIDs) {
      this.statusChangedIDs = statusChangedIDs;
   }
   
   /**
    * Add pollingControl id into polling control id list. 
    */
   public void addPollingControlID(String pollingControlID) {
      this.pollingControlIDs.add(pollingControlID);
   }
   
   /**
    * Add statusChanged id into status changed id list. 
    */
   public void addStatusChangedID(String statusChangedID) {
      this.statusChangedIDs.add(statusChangedID);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null || !(obj instanceof TimeoutRecord)) {
         return false;
      }
      TimeoutRecord timeoutRecord = (TimeoutRecord)obj;
      if ("".equals(timeoutRecord.getDeviceID()) || !timeoutRecord.getDeviceID().equals(this.deviceID)) {
         return false;
      }
      if (timeoutRecord.getPollingControlIDs().size() == 0 || timeoutRecord.getPollingControlIDs().size() != this.pollingControlIDs.size()) {
         return false;
      }
      
      Collections.sort(this.getPollingControlIDs(), new PollingControlIDListComparator());      
      Collections.sort(timeoutRecord.getPollingControlIDs(), new PollingControlIDListComparator());
      for (int i = 0; i < timeoutRecord.getPollingControlIDs().size(); i++) {
         if (!this.getPollingControlIDs().get(i).equals(timeoutRecord.getPollingControlIDs().get(i))) {
            return false;
         }
      }
      return true;
   }

   /**
    * Overload the method setPollingControlIDs with the parameter type "String[]". 
    */
   public void setPollingControlIDs(String[] pollingControlIDs) {
      for (String pollingControlID : pollingControlIDs) {
         this.pollingControlIDs.add(pollingControlID);
      }
   }
   
   
   
}
