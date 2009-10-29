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
import org.openremote.controller.exception.InvalidPollingURLException;
import org.openremote.controller.service.ControlStatusPollingService;
import org.openremote.controller.service.StatusCacheService;
import org.openremote.controller.status_cache.PollingData;
import org.openremote.controller.status_cache.PollingThread;
import org.openremote.controller.status_cache.TimeoutRecord;
import org.openremote.controller.status_cache.TimeoutTable;

/**
 * Implementation of controlStatusPollingService.
 * 
 * @author Handy.Wang 2009-10-21
 */
public class ControlStatusPollingServiceImpl implements ControlStatusPollingService {
   
   /** The max length of time of current servlet response. */
   private static final int MAX_TIME_OUT_SECONDS = 50;
   
   /** A second equals how much mili seconds. */
   private static final int MILLI_SECONDS_A_SECOND = 1000;
   
   /** Separator of control ids in the RESTful url. */
   private static final String CONTROL_ID_SEPARATOR = ",";

   /** This value will be responsed when current servlet couldn't get the changed statuses in the <b>MAX_TIME_OUT_SECONDS</b>. */
   public static final String SERVER_RESPONSE_TIME_OUT_STATUS_CODE = "TIMEOUT";
   
   /** header of xml-formatted polling result data. */
   private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<openremote xmlns=\"http://www.openremote.org\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"\">\n";
   
   /** status element name of xml-formatted polling result data. */
   private static final String XML_STATUS_RESULT_ELEMENT_NAME = "status";
   
   /** id element name of xml-formatted polling result data. */
   private static final String XML_STATUS_RESULT_ELEMENT_CONTROL_IDENTITY = "id";
   
   /** tail of xml-formatted polling result data. */
   private static final String XML_TAIL = "</openremote>";
   
   /**
    * TIME_OUT table instance.
    */
   private TimeoutTable timeoutTable;
   
   private StatusCacheService statusCacheService;
   
   private Logger logger = Logger.getLogger(this.getClass().getName());
   
   /**
    * get the changed statuses from cached DB
    */
   @Override
   public String getChangedStatuses(long startTime, String deviceID, String unParsedcontrolIDs) {
      String changedStatuses = "";
      String[] controlIDs = (unParsedcontrolIDs == null || "".equals(unParsedcontrolIDs)) ? new String[]{} : unParsedcontrolIDs.split(CONTROL_ID_SEPARATOR);
      PollingData pollingData = new PollingData(controlIDs);
      PollingThread pollingThread = new PollingThread(pollingData);
      pollingThread.start();
      
      while (true) {
         if ((System.currentTimeMillis() - startTime) / MILLI_SECONDS_A_SECOND >= MAX_TIME_OUT_SECONDS) {
            changedStatuses = Constants.SERVER_RESPONSE_TIME_OUT;
            logger.info("Observing change of component status was timeout.");
            saveTimeOutRecord(deviceID, controlIDs);
            logger.info("Return timeout result of observed.");
            pollingThread.setWaitingStatusChange(false);
            
            break;
         }
         if (pollingData.getChangedStatuses() == null) {
            continue;
         } else {
            break;
         }
      }
      if (!SERVER_RESPONSE_TIME_OUT_STATUS_CODE.equals(changedStatuses)) {
         logger.info("Got the change of component status.");
         changedStatuses = composePollingResult(pollingData);
         logger.info("Return xml-formatted result of observed.");
      }
      return changedStatuses;
   }

   /* (non-Javadoc)
    * @see org.openremote.controller.service.ControlStatusPollingService#querySkipState(java.lang.String)
    */
   @Override
   public String querySkipState(String deviceID, String unParsedcontrolIDs) {
      logger.info("Querying skipped state from TIME_OUT table...");
      String skipState = "";
      String[] controlIDs = (unParsedcontrolIDs == null || "".equals(unParsedcontrolIDs)) ? new String[]{} : unParsedcontrolIDs.split(CONTROL_ID_SEPARATOR);
      List<Integer> pollingControlIDs = new ArrayList<Integer>();
      try {
         for (String pollingControlID : controlIDs) {
            pollingControlIDs.add(Integer.parseInt(pollingControlID));
         }
      } catch (NumberFormatException e) {
         throw new InvalidPollingURLException("The component id can only be some numbers instead of characters", e);
      }
      
      TimeoutRecord timeoutRecord = timeoutTable.query(deviceID, pollingControlIDs);
      String tempInfo = "Found: [device => " + deviceID + ", controlIDs => " + unParsedcontrolIDs + "] in TIME_OUT_TABLE.";
      logger.info(timeoutRecord == null ? "Not " + tempInfo : tempInfo);
      // same device
      if (timeoutRecord != null) {
         logger.info("Have queried changed data from TIME_OUT table.");
         Set<Integer> statusChangedIDs = timeoutRecord.getStatusChangedIDs();
         if (statusChangedIDs != null && statusChangedIDs.size() != 0) {
            logger.info("The status of found timeout record had changed during current polling and last polling.");
            skipState = queryChangedStatusesFromCachedStatusTable(statusChangedIDs);
            timeoutTable.delete(timeoutRecord);
            return skipState;
         }else {
            logger.info("The status of found timeout record didn't change during current polling and last polling.");
            timeoutTable.delete(timeoutRecord);
         }
      } 
      
      /*//not same device, but same polling control ids. 
      //In this case, can't remote the timeoutRecord, because this timeout record is other device's.
      timeoutRecord = timeoutTable.query(pollingControlIDs);
      if (timeoutRecord != null) {
         List<String> statusChangedIDs = timeoutRecord.getStatusChangedIDs();
         if (statusChangedIDs != null && statusChangedIDs.size() != 0) {
            return queryChangedStatusesFromCachedStatusTable(statusChangedIDs);
         }
      }*/
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
   
   /**
    * Save skip-state in case of the thread which observe status change was time out.
    */
   private void saveTimeOutRecord(String deviceID, String[] pollingControlIDs) {
      timeoutTable.insert(new TimeoutRecord(deviceID, pollingControlIDs));
      logger.info("Recording the timeout record.");
   }

   /**
    * Inject the timeout table. 
    */
   public void setTimeoutTable(TimeoutTable timeoutTable) {
      this.timeoutTable = timeoutTable;
   }

   public void setStatusCacheService(StatusCacheService statusCacheService) {
      this.statusCacheService = statusCacheService;
   }
   
}
