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

import org.apache.log4j.Logger;
import org.openremote.controller.service.ControlStatusPollingService;
import org.openremote.controller.spring.SpringContext;

/**
 * Status Polling RESTful servlet of control.
 * It's responsiable for response corresponding result with the RESTful url.
 * 
 * @author Handy.Wang 2009-10-19
 */
@SuppressWarnings("serial")
public class ControlStatusPollingRESTServlet extends HttpServlet {

   /** This service is responsible for observe statuses change and return the changed statuses(xml-formatted). */
   private ControlStatusPollingService controlStatusPollingService = (ControlStatusPollingService) SpringContext.getInstance().getBean("controlStatusPollingService");

   private Logger logger = Logger.getLogger(this.getClass().getName());
   
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
      logger.info("Started polling at " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

      String url = request.getRequestURL().toString();
      String regexp = "rest\\/polling\\/(.*?)\\/(.*)";
      Pattern pattern = Pattern.compile(regexp);
      Matcher matcher = pattern.matcher(url);
      String unParsedcontrolIDs = null;
      String deviceID = null;
      
      if (matcher.find()) {
         deviceID = matcher.group(1);
         unParsedcontrolIDs = matcher.group(2);
         PrintWriter printWriter = response.getWriter();         
         
         String skipState = controlStatusPollingService.querySkipState(deviceID, unParsedcontrolIDs);
         if (skipState != null && !"".equals(skipState)) {
            logger.info("Return the skip state which queried from StatusCache.");
            printWriter.write(skipState);
         } else {
            printWriter.write(controlStatusPollingService.getChangedStatuses(startTime, deviceID, unParsedcontrolIDs));
         }
         logger.info("Finished polling at " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
      }
   }
   
}
