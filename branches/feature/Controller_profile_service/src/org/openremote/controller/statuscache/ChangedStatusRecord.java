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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A changed status record.<br />
 * This Record is used to record the skipped changed statuses and waited changed statuses .<br />
 * 
 * @author Handy.Wang 2009-10-23
 */
public class ChangedStatusRecord {
   
   /** A logical identity of panel */
   private String deviceID;

   /** The ids a polling request contains */
   private List<Integer> pollingControlIDs;

   /** The ids whose status had changed in the pollingControlIDs */
   private Set<Integer> statusChangedIDs;

   /** Default constructor */
   public ChangedStatusRecord() {
      super();
      deviceID = "";
      pollingControlIDs = new ArrayList<Integer>();
      statusChangedIDs = new HashSet<Integer>();
   }

   public ChangedStatusRecord(String deviceID, List<Integer> pollingControlIDs) {
      super();
      this.deviceID = deviceID;
      this.pollingControlIDs = pollingControlIDs;
      this.statusChangedIDs = new HashSet<Integer>();
   }

   public ChangedStatusRecord(String deviceID, Integer[] pollingControlIDs) {
      super();
      this.deviceID = deviceID;
      this.pollingControlIDs = new ArrayList<Integer>();
      this.setPollingControlIDs(pollingControlIDs);
      this.statusChangedIDs = new HashSet<Integer>();
   }

   public ChangedStatusRecord(String deviceID, String[] pollingControlIDs) {
      super();
      this.pollingControlIDs = new ArrayList<Integer>();
      for (String s : pollingControlIDs) {
         this.pollingControlIDs.add(Integer.parseInt(s));
      }
      this.deviceID = deviceID;
      this.statusChangedIDs = new HashSet<Integer>();
   }

   public ChangedStatusRecord(String deviceID, Collection<Integer> pollingControlIDs) {
      super();
      this.pollingControlIDs = new ArrayList<Integer>();
      for (Integer i : pollingControlIDs) {
         this.pollingControlIDs.add(i);
      }
      this.deviceID = deviceID;
      this.statusChangedIDs = new HashSet<Integer>();
   }

   public String getDeviceID() {
      return deviceID;
   }

   public void setDeviceID(String deviceID) {
      this.deviceID = deviceID;
   }

   /**
    * Add pollingControl id into polling control id list. 
    */
   public void addPollingControlID(Integer pollingControlID) {
      this.pollingControlIDs.add(pollingControlID);
   }
   
   /**
    * Add statusChanged id into status changed id list. 
    */
   public void addStatusChangedID(Integer statusChangedID) {
      this.statusChangedIDs.add(statusChangedID);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null || !(obj instanceof ChangedStatusRecord)) {
         return false;
      }
      ChangedStatusRecord timeoutRecord = (ChangedStatusRecord) obj;
      if ("".equals(timeoutRecord.getDeviceID()) || !timeoutRecord.getDeviceID().equals(this.deviceID)) {
         return false;
      }
      if (timeoutRecord.getPollingControlIDs().size() == 0
            || timeoutRecord.getPollingControlIDs().size() != this.pollingControlIDs.size()) {
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
   public void setPollingControlIDs(Integer[] pollingControlIDs) {
      for (Integer pollingControlID : pollingControlIDs) {
         this.pollingControlIDs.add(pollingControlID);
      }
   }

   public List<Integer> getPollingControlIDs() {
      return pollingControlIDs;
   }

   public void setPollingControlIDs(List<Integer> pollingControlIDs) {
      this.pollingControlIDs = pollingControlIDs;
   }

   public Set<Integer> getStatusChangedIDs() {
      return statusChangedIDs;
   }

   public void setStatusChangedIDs(Set<Integer> statusChangedIDs) {
      this.statusChangedIDs = statusChangedIDs;
   }

   @Override
   public String toString() {
      StringBuffer sb = new StringBuffer();

      sb.append("DEVICEID:" + deviceID);
      sb.append("\tcomponentID:" + this.pollingControlIDs.toString());
      sb.append("statusChangedComponentID:" + this.statusChangedIDs);
      return sb.toString();
   }

}
