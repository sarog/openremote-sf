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

import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.openremote.controller.spring.SpringContext;
import org.springframework.context.support.ApplicationObjectSupport;

/**
 * This class is mainly responsible for <b>Initializing</b> the cached statuses database where in the memory.
 * 
 * @author Handy.Wang 2009-10-21
 */
public class InitCachedStatusDBListener extends ApplicationObjectSupport implements ServletContextListener {

   /** The logger. */
   private Logger logger = Logger.getLogger(InitCachedStatusDBListener.class.getName());
   
   /** The connection. */
   public static Connection connection;
   
   /**
    * TIME_OUT table instance.
    */
   private TimeoutTable timeoutTable = (TimeoutTable) SpringContext.getInstance().getBean("timeoutTable");;

   /* (non-Javadoc)
    * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
    */
   @Override
   public void contextInitialized(ServletContextEvent event) {
      try {
         //The folling s are simulated for status changed in TIME_OUT table.
         System.out.println("TIME_OUT table : Starting simulated change status tread at " + new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(new Date()));
         simulateSkipStateTrackTestCase2();
         simulateSkipStateTrackTestCase3();
         
         //The followings are simulated for status changed in ObservedStatusesSubject.
         System.out.println("ObservedStatusesSubject : Starting simulated change status tread at " + new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(new Date()));
         Thread nofityObservedStatusSubjectThread = new Thread(){
            @Override
            public void run() {
               int i = 0;
               for(; ; i++) {
                  try {
                     ObservedStatusesSubject observedStatusesSubject = (ObservedStatusesSubject) SpringContext.getInstance().getBean("observedStatusesSubject");
                     if (i % 2 == 0) {
                        observedStatusesSubject.statusChanged(new StatusChangedData("1", "OFF"));
                     } else {
                        observedStatusesSubject.statusChanged(new StatusChangedData("1", "ON"));
                     }
                     sleep(10000);
                  } catch (InterruptedException e) {
                     e.printStackTrace();
                  }
               }
            }
         };
         nofityObservedStatusSubjectThread.start();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   
   /**
    * Simulate Test Case 2 in the SkipStateTrackTest.java <br />
    * 
    * DESC: First polling request was time out, second polling request can find previous time out record and
    *   get the changed status during two polling requests.
    */
   private void simulateSkipStateTrackTestCase2() {
      Thread simulateThread = new Thread() {
         @Override
         public void run() {
            while (true) {
               List<TimeoutRecord> timeoutRecords = timeoutTable.query("2");
               for (TimeoutRecord timeoutRecord : timeoutRecords) {
                  timeoutRecord.addStatusChangedID("2");
               }
               if (timeoutRecords.size() > 0) {
                  break;
               }
            }
         }
      };
      simulateThread.start();
   }
   
   /**
    * Simulate Test Case 3 in the SkipStateTrackTest.java <br />
    * 
    * DESC: First polling request was time out, second polling request can find previous time out record but
    *   the status previous polling request care about didn't change. So, second polling should remove
    *   previous time out record and observe status. At last getting the changed status
    */
   private void simulateSkipStateTrackTestCase3() {
      Thread simulateThread = new Thread() {
         @Override
         public void run() {
            while (true) {
               List<TimeoutRecord> timeoutRecords = timeoutTable.query("3");
               if (timeoutRecords != null && timeoutRecords.size() != 0) {
                  int i = 0;
                  for(; ; i++) {
                     try {
                        ObservedStatusesSubject observedStatusesSubject = (ObservedStatusesSubject) SpringContext.getInstance().getBean("observedStatusesSubject");
                        if (i % 2 == 0) {
                           observedStatusesSubject.statusChanged(new StatusChangedData("3", "OFF"));
                        } else {
                           observedStatusesSubject.statusChanged(new StatusChangedData("3", "ON"));
                        }
                        sleep(10000);
                     } catch (InterruptedException e) {
                        e.printStackTrace();
                     }
                  }
               }
            }
         }
      };
      simulateThread.start();
   }

   /* (non-Javadoc)
    * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
    */
   @Override
   public void contextDestroyed(ServletContextEvent event) {

      ServletContext context = event.getServletContext();
      try {
         Statement stmt = connection.createStatement();
         stmt.executeUpdate("SHUTDOWN;");
         stmt.close();
      } catch (Exception e) {
         System.out.println("Shutdown HSQL database error : " + e);
         logger.error("Shutdown HSQL database error", e);
         context.log(e.getMessage());
      } finally {
         try {
            connection.close();
            context.log("Closing HSQL database...");
         } catch (Exception e) {
         }
      }
   }

   public void setTimeoutTable(TimeoutTable timeoutTable) {
      this.timeoutTable = timeoutTable;
   }

}
