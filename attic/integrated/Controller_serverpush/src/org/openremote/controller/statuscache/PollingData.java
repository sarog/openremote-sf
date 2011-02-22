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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * It store control ids and changed statuses which associate with stored control ids of a polling request.
 * 
 * @author Handy.Wang 2009-10-21
 */
public class PollingData {
   
   public String getDeviceId() {
      return deviceId;
   }

   public void setDeviceId(String deviceId) {
      this.deviceId = deviceId;
   }

   private String deviceId ;
   /** The control ids of a polling request. */
   private Set<Integer> controlIDs;

   /** The changed statuses a polling request care about. */
   private Map<Integer, String> changedStatuses;

   public PollingData() {
      super();
   }
   
   /**
    * Instantiates a new polling data with control ids array in the RESTful polling url.
    * 
    * @param controlIDs the control i ds
    */
   public PollingData(Integer[] controlIDs) {
      super();
      Set<Integer> ids = new HashSet<Integer>();
      for (Integer controlID : controlIDs) {
         ids.add(controlID);
      }
      this.controlIDs = ids;
   }
   
   public PollingData(String[] controlIDs) {
      super();
      Set<Integer> ids = new HashSet<Integer>();
      for (String controlID : controlIDs) {
         ids.add(Integer.parseInt(controlID));
      }
      this.controlIDs = ids;
   }
   
   /**
    * Instantiates a new polling data with control ids list in the RESTful polling url.
    * 
    * @param controlIDs the control i ds
    */
   public PollingData(Set<Integer> controlIDs) {
      this.controlIDs = controlIDs;
   }

   public Set<Integer> getControlIDs() {
      return controlIDs;
   }

   public void setControlIDs(Set<Integer> controlIDs) {
      this.controlIDs = controlIDs;
   }

   public Map<Integer, String> getChangedStatuses() {
      return changedStatuses;
   }

   public void setChangedStatuses(Map<Integer, String> changedStatuses) {
      this.changedStatuses = changedStatuses;
   }

}
