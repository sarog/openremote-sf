package org.openremote.controller.service.impl;

import java.util.Map;
import java.util.Set;

import org.openremote.controller.service.ControlStatusPollingService;
import org.openremote.controller.status_cache.DBManager;
import org.openremote.controller.status_cache.PollingData;

public class ControlStatusPollingServiceImpl implements ControlStatusPollingService {
   
   private DBManager dbManager;
   
   /** The Constant xmlHeader of composed xml-formatted status results. */
   private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<openremote xmlns=\"http://www.openremote.org\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"\">\n";
   
   /** The Constant XML_STATUS_RESULT_ELEMENT_NAME composed xml-formatted status results. */
   private static final String XML_STATUS_RESULT_ELEMENT_NAME = "status";
   
   /** The Constant XML_STATUS_RESULT_ELEMENT_CONTROL_IDENTITY composed xml-formatted status results. */
   private static final String XML_STATUS_RESULT_ELEMENT_CONTROL_IDENTITY = "id";
   
   /** The Constant XML_TAIL of composed xml-formatted status results. */
   private static final String XML_TAIL = "</openremote>";
   
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

   public void setHsqlConnectionManager(DBManager hsqlConnectionManager) {
      this.dbManager = hsqlConnectionManager;
   }

}
