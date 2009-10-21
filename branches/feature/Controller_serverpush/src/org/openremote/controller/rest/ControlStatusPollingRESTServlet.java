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
 * It will response corresponding result with the RESTful URL. 
 * 
 * @author Handy.Wang 2009-10-19
 */
@SuppressWarnings("serial")
public class ControlStatusPollingRESTServlet extends HttpServlet {

   /** 
    * This service is responsible for listen statuses change 
    * and return the changed statuses(xml-formatted). 
    */
   private ControlStatusPollingService controlStatusPollingService = (ControlStatusPollingService) SpringContext.getInstance().getBean("controlStatusPollingService");;

   /** The max length of time of current servlet response. */
   private static final int MAX_TIME_OUT_SECONDS = 50;
   
   private static final int MILLI_SECONDS_A_SECOND = 1000;
   
   /** Separator of control ids in the RESTful URL. */
   private static final String CONTROL_ID_SEPARATOR = ",";

   /** 
    * This value will be responsed when current servlet couldn't get 
    * the changed statuses in the <b>MAX_TIME_OUT_SECONDS</b>  
    */
   private static final String CONNECTION_TIME_OUT_MSG = "TIME_OUT";

   /**
    * @see HttpServlet#HttpServlet()
    */
   public ControlStatusPollingRESTServlet() {
      super();
   }

   /**
    * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
    */
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      doPost(request, response);
   }

   /**
    * It's responsible for polling the changed statuses or TIME_OUT if time out.
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
