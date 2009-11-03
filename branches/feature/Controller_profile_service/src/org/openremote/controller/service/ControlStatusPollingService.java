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
package org.openremote.controller.service;


/**
 * It's responsiable for polling changed statuses from cached DB.
 * 
 * @author Handy.Wang 2009-10-21
 */
public interface ControlStatusPollingService {
   
   /** The max length of time of current servlet response. */
    static final int MAX_TIME_OUT_SECONDS = 50;
   
   /** A second equals how much mili seconds. */
    static final int MILLI_SECONDS_A_SECOND = 1000;
   
   /** Separator of control ids in the RESTful url. */
   static final String CONTROL_ID_SEPARATOR = ",";

   /** This value will be responsed when current servlet couldn't get the changed statuses in the <b>MAX_TIME_OUT_SECONDS</b>. */
   static final String SERVER_RESPONSE_TIME_OUT_STATUS_CODE = "TIMEOUT";
   
   /** header of xml-formatted polling result data. */
    static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<openremote xmlns=\"http://www.openremote.org\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"\">\n";
   
   /** status element name of xml-formatted polling result data. */
    static final String XML_STATUS_RESULT_ELEMENT_NAME = "status";
   
   /** id element name of xml-formatted polling result data. */
    static final String XML_STATUS_RESULT_ELEMENT_CONTROL_IDENTITY = "id";
   
   /** tail of xml-formatted polling result data. */
    static final String XML_TAIL = "</openremote>";
   
   /**
    * get the changed statuses from cached DB. 
    */
   public String waitForChangedStatuses(long startTime, String deviceID, String unParsedcontrolIDs);

   /**
    * Query skip states from TIME_OUT table. 
    */
   public String querySkippedState(String deviceID, String unParsedcontrolIDs);
   
   public void saveOrUpdateSkippedStateRecord(String deviceId,String unParsedcontrolIDs);
   
}
