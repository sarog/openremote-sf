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

/**
 * Polling Thread is responsiable for listenning the statuses change
 * 
 * @author Handy.Wang 2009-10-21
 */
public class PollingThread extends Thread {
   
   private PollingData pollingData;
   
   public PollingThread() {
      super();
   }
   
   public PollingThread(PollingData pollingData) {
      this.pollingData = pollingData;
   }
   
   /* (non-Javadoc)
    * @see java.lang.Thread#run()
    */
   @Override
   public void run() {
      Map<String, String> changedStatuses = new HashMap<String, String>();
      boolean isTimeOut = false;
      for (String controID : pollingData.getControlIDs()) {
         if ("4".equals(controID)) {
            isTimeOut = true;
            break;
         }
      }
      while(isTimeOut) {
      }
      synchronized(pollingData) {
         for (String controID : pollingData.getControlIDs()) {
            changedStatuses.put(controID, "ON");
         }
         pollingData.setChangedStatuses(changedStatuses);
      }
   }

}
