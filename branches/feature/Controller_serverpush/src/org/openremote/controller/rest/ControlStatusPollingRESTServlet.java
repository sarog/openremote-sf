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
package org.openremote.controller.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openremote.controller.service.ControlStatusPollingService;
import org.openremote.controller.spring.SpringContext;
import org.openremote.controller.status_cache.PollingData;
import org.openremote.controller.status_cache.PollingThread;

/**
 * Status Polling RESTful servlet of control.
 * It's responsiable for response corresponding result with the RESTful url.
 * 
 * @author Handy.Wang 2009-10-19
 */
@SuppressWarnings("serial")
public class ControlStatusPollingRESTServlet extends HttpServlet {

   /** This service is responsible for observe statuses change and return the changed statuses(xml-formatted). */
   private ControlStatusPollingService controlStatusPollingService = (ControlStatusPollingService) SpringContext.getInstance().getBean("controlStatusPollingService");;

   /** The max length of time of current servlet response. */
   private static final int MAX_TIME_OUT_SECONDS = 50;
   
   /** A second equals how much mili seconds. */
   private static final int MILLI_SECONDS_A_SECOND = 1000;
   
   /** Separator of control ids in the RESTful url. */
   private static final String CONTROL_ID_SEPARATOR = ",";

   /** This value will be responsed when current servlet couldn't get the changed statuses in the <b>MAX_TIME_OUT_SECONDS</b>. */
   private static final String CONNECTION_TIME_OUT_MSG = "TIME_OUT";

   /**
    * The Constructor.
    */
   public ControlStatusPollingRESTServlet() {
      super();
   }

   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      doPost(request, response);
   }

   /**
    * It's responsible for polling the <b>changed statuses</b> or <b>TIME_OUT</b> if time out.
    */
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      long startTime = System.currentTimeMillis();
      System.out.println("Started polling at " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

      String changedStatuses = "";
      String url = request.getRequestURL().toString();
      String regexp = "rest\\/polling\\/(.*)";
      Pattern pattern = Pattern.compile(regexp);
      Matcher matcher = pattern.matcher(url);
      String unParsedcontrolIDs = null;

      if (matcher.find()) {
         unParsedcontrolIDs = matcher.group(1);
         PrintWriter printWriter = response.getWriter();
         
         String[] controlIDs = (unParsedcontrolIDs == null || "".equals(unParsedcontrolIDs)) ? new String[]{} : unParsedcontrolIDs.split(CONTROL_ID_SEPARATOR);
         PollingData pollingData = new PollingData(controlIDs);
         PollingThread pollingThread = new PollingThread(pollingData);
         pollingThread.start();
         
         while (true) {
            if ((System.currentTimeMillis() - startTime) / MILLI_SECONDS_A_SECOND >= MAX_TIME_OUT_SECONDS) {
               changedStatuses = CONNECTION_TIME_OUT_MSG;
               pollingThread.interrupt();
               break;
            }
            if (pollingData.getChangedStatuses() == null) {
               continue;
            } else {
               break;
            }
         }
         if (!CONNECTION_TIME_OUT_MSG.equals(changedStatuses)) {
            changedStatuses = controlStatusPollingService.parsePollingResult(pollingData);
         }
         System.out.println("Finished polling at " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
         printWriter.write(changedStatuses);
      }
   }
   
}
