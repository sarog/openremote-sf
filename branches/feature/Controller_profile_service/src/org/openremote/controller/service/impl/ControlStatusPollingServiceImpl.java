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
package org.openremote.controller.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openremote.controller.Constants;
import org.openremote.controller.exception.NoSuchComponentException;
import org.openremote.controller.service.ControlStatusPollingService;
import org.openremote.controller.service.StatusCacheService;
import org.openremote.controller.statuscache.PollingData;
import org.openremote.controller.statuscache.SkippedStatusRecord;
import org.openremote.controller.statuscache.SkippedStatusTable;

/**
 * Implementation of controlStatusPollingService.
 * 
 * @author Handy.Wang 2009-10-21
 */
public class ControlStatusPollingServiceImpl implements ControlStatusPollingService {
   
   private SkippedStatusTable skippedStatusTable;
   
   private StatusCacheService statusCacheService;
   
   public void setStatusCacheService(StatusCacheService statusCacheService) {
      this.statusCacheService = statusCacheService;
   }

   private Logger logger = Logger.getLogger(this.getClass().getName());
   
   /* (non-Javadoc)
    * @see org.openremote.controller.service.ControlStatusPollingService#querySkipState(java.lang.String)
    */
   @Override
   public String querySkippedState(String deviceID, String unParsedcontrolIDs) {
      logger.info("Querying skipped state from TIME_OUT table...");
      String skipState = "";
      String[] controlIDs = (unParsedcontrolIDs == null || "".equals(unParsedcontrolIDs)) ? new String[]{} : unParsedcontrolIDs.split(CONTROL_ID_SEPARATOR);
      
      List<Integer> pollingControlIDs = new ArrayList<Integer>();
      for (String pollingControlID : controlIDs) {
         try {
            pollingControlIDs.add(Integer.parseInt(pollingControlID));
         } catch (NumberFormatException e) {
            throw new NoSuchComponentException("The component id '" + pollingControlID + "' should be digit", e);
         }
      }

      SkippedStatusRecord skipStateRecord = skippedStatusTable.query(deviceID, pollingControlIDs);
      String tempInfo = "Found: [device => " + deviceID + ", controlIDs => " + unParsedcontrolIDs + "] in TIME_OUT_TABLE.";
      logger.info(skipStateRecord == null ? "Not " + tempInfo : tempInfo);
      
      if (skipStateRecord == null) {
         skipStateRecord = new SkippedStatusRecord(deviceID, pollingControlIDs);
         skippedStatusTable.insert(skipStateRecord);
      }
      synchronized (skipStateRecord) {
         boolean willTimeout = false;
         while (skipStateRecord.getStatusChangedIDs() == null || skipStateRecord.getStatusChangedIDs().size() == 0) {
            if (willTimeout) {
               return Constants.SERVER_RESPONSE_TIME_OUT;
            }
            try {
               logger.info(skipStateRecord + "Waiting...");
               skipStateRecord.wait(50000);
               willTimeout = true;
            } catch (InterruptedException e) {
               e.printStackTrace();
               return Constants.SERVER_RESPONSE_TIME_OUT;
            }
         }
         logger.info(skipStateRecord + "got the waited data");
         skipState = queryChangedStatusesFromCachedStatusTable(skipStateRecord.getStatusChangedIDs());
         skippedStatusTable.resetChangedStatusIDs(deviceID, pollingControlIDs);
      }
      
      return skipState;
   }
   
   private String queryChangedStatusesFromCachedStatusTable(Set<Integer> statusChangedIDs) {
      logger.info("Queriy changed data from StatusCache.");
      PollingData pollingData = new PollingData(statusChangedIDs);
      Map<Integer, String> changedStatuses = statusCacheService.queryStatuses(pollingData.getControlIDs());
      pollingData.setChangedStatuses(changedStatuses);
      logger.info("Have queried changed data from StatusCache.");
      return composePollingResult(pollingData);
   }
   
   /**
    * compose the changed statuses into xml-formatted data.
    */
   private String composePollingResult(PollingData pollingResult) {
      StringBuffer sb = new StringBuffer();
      sb.append(XML_HEADER);
      
      Map<Integer, String> changedStatuses = pollingResult.getChangedStatuses();
      if (changedStatuses == null) {
         return "";
      }
      Set<Integer> controlIDs = changedStatuses.keySet();
      for (Integer controlID : controlIDs) {
          sb.append("<" + XML_STATUS_RESULT_ELEMENT_NAME + " " + XML_STATUS_RESULT_ELEMENT_CONTROL_IDENTITY + "=\"" + controlID + "\">");
          sb.append(changedStatuses.get(controlID));
          sb.append("</" + XML_STATUS_RESULT_ELEMENT_NAME + ">\n");
          sb.append("\n");
      }
      sb.append(XML_TAIL);
      return sb.toString();
   }

   public void setSkippedStatusTable(SkippedStatusTable skippedStatusTable) {
      this.skippedStatusTable = skippedStatusTable;
   }
   
}
