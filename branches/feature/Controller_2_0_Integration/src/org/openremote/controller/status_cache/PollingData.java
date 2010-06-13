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
import java.util.List;
import java.util.Map;

/**
 * It store control ids and changed statuses which associate with stored control ids of a polling request.
 * 
 * @author Handy.Wang 2009-10-21
 */
public class PollingData {
   
   /** The control ids of a polling request. */
   private List<String> controlIDs;

   /** The changed statuses a polling request care about. */
   private Map<String, String> changedStatuses;

   public PollingData() {
      super();
   }
   
   /**
    * Instantiates a new polling data with control ids in the RESTful polling url.
    * 
    * @param controlIDs the control i ds
    */
   public PollingData(String[] controlIDs) {
      super();
      List<String> ids = new ArrayList<String>();
      for (String controlID : controlIDs) {
         ids.add(controlID);
      }
      this.controlIDs = ids;
   }

   public Map<String, String> getChangedStatuses() {
      return changedStatuses;
   }

   public void setChangedStatuses(Map<String, String> changedStatuses) {
      this.changedStatuses = changedStatuses;
   }

   public List<String> getControlIDs() {
      return controlIDs;
   }

   public void setControlIDs(List<String> controlIDs) {
      this.controlIDs = controlIDs;
   }
   
}
