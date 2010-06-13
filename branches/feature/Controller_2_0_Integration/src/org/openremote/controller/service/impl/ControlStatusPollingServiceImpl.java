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

import java.util.Map;
import java.util.Set;

import org.openremote.controller.service.ControlStatusPollingService;
import org.openremote.controller.status_cache.DBManager;
import org.openremote.controller.status_cache.PollingData;

/**
 * Implementation of controlStatusPollingService.
 * 
 * @author Handy.Wang 2009-10-21
 */
public class ControlStatusPollingServiceImpl implements ControlStatusPollingService {
   
   /** header of xml-formatted polling result data. */
   private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<openremote xmlns=\"http://www.openremote.org\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"\">\n";
   
   /** status element name of xml-formatted polling result data. */
   private static final String XML_STATUS_RESULT_ELEMENT_NAME = "status";
   
   /** id element name of xml-formatted polling result data. */
   private static final String XML_STATUS_RESULT_ELEMENT_CONTROL_IDENTITY = "id";
   
   /** tail of xml-formatted polling result data. */
   private static final String XML_TAIL = "</openremote>";
   
   /**
    * get the changed statuses from cached DB
    */
   @Override
   public String getChangedStatuses(String unParsedcontrolIDs) {
//      String[] parsedControlIDs = unParsedcontrolIDs.split(CONTROL_ID_SEPARATOR);
      
      if (unParsedcontrolIDs.contains("4")) {
         return "";
      } 
      
      StringBuffer rst = new StringBuffer();
      rst.append("<openremote xsi:schemaLocation=\"\">\n");
      rst.append("<status id=\"1\">ON</status>\n");
      rst.append("<status id=\"2\">Middle</status>\n");
      rst.append("<status id=\"3\">0.8</status>\n");
      rst.append("</openremote>");
      return rst.toString();
   }
   
   /**
    * conpose the changed statuses into xml-formatted data.
    */
   @Override
   public String parsePollingResult(PollingData pollingResult) {
      StringBuffer sb = new StringBuffer();
      sb.append(XML_HEADER);
      
      Map<String, String> changedStatuses = pollingResult.getChangedStatuses();
      Set<String> controlIDs = changedStatuses.keySet();
      for (String controlID : controlIDs) {
          sb.append("<" + XML_STATUS_RESULT_ELEMENT_NAME + " " + XML_STATUS_RESULT_ELEMENT_CONTROL_IDENTITY + "=\"" + controlID + "\">");
          sb.append(changedStatuses.get(controlID));
          sb.append("</" + XML_STATUS_RESULT_ELEMENT_NAME + ">\n");
          sb.append("\n");
      }
      sb.append(XML_TAIL);
      return sb.toString();
   }
   
}
