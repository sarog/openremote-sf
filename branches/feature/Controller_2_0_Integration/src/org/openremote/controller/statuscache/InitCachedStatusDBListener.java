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
package org.openremote.controller.statuscache;

import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openremote.controller.Configuration;
import org.openremote.controller.service.StatusCacheService;
import org.openremote.controller.spring.SpringContext;
import org.openremote.controller.utils.ConfigFactory;
import org.openremote.controller.utils.NetworkUtil;
import org.springframework.context.support.ApplicationObjectSupport;

/**
 * This class is mainly responsible for <b>Initializing</b> the cached statuses database where in the memory.
 * 
 * @author Handy.Wang 2009-10-21
 */
public class InitCachedStatusDBListener extends ApplicationObjectSupport implements ServletContextListener {

   /** The logger. */
//   private Logger logger = Logger.getLogger(InitCachedStatusDBListener.class.getName());
   
   /** The connection. */
   public static Connection connection;
   
   /**
    * TIME_OUT table instance.
    */
   private SkippedStatusTable skippedStatusTable = (SkippedStatusTable) SpringContext.getInstance().getBean("skippedStatusTable");
   private StatusCacheService statusCacheService = (StatusCacheService) SpringContext.getInstance().getBean("statusCacheService");

   private static Configuration configuration = ConfigFactory.getConfig();
   private static  String ServerIP = NetworkUtil.getLocalhostIP();
   
   private static  String webAppName = "controller";
   private static  String resourceBasePath = "http://"+ServerIP+":"+configuration.getWebappPort()+"/"+webAppName+"/resources/";
   
   /* (non-Javadoc)
    * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
    */
   @Override
   public void contextInitialized(ServletContextEvent event) {
      try {
         InitStatusCache();
         simulateStatusCacheControlID1();
         simulateStatusCacheControlID2();
         simulateStatusCacheControlID3();
         simulateStatusCacheControlID5();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   
   /**
    * Init 4 components initial status.
    */
   private void InitStatusCache() {
      statusCacheService.saveOrUpdateStatus(1, "OFF");
      statusCacheService.saveOrUpdateStatus(2, "OFF");
      statusCacheService.saveOrUpdateStatus(3, "OFF");
      statusCacheService.saveOrUpdateStatus(4, "OFF");
      statusCacheService.saveOrUpdateStatus(5, "url");
   }
   
   /**
    * Simulate Case1 of StatusCahe.<br />
    * 
    * The device of control id is 1 will switch ON/OFF every 10 seconds.
    */
   private void simulateStatusCacheControlID1() {
      Thread thread = new Thread() {
         @Override
         public void run() {
            int i = 0;
            for(; ; i++) {
               if (i % 2 == 0) {
                  statusCacheService.saveOrUpdateStatus(1, "ON");
               } else {
                  statusCacheService.saveOrUpdateStatus(1, "OFF");
               }
               try {
                  sleep(10000);
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
            }
         }
      };
      thread.start();
   }
   
   /**
    * Simulate Case 2 of StatusCache.<br />
    * 
    * DESC: First polling request was time out, second polling request can find previous time out record and <br />
    *   get the changed status during two polling requests.<br /> 
    *   So, the client will get timeout response in odd times and changed statuses in even times.
    */
   private void simulateStatusCacheControlID2() {
      Thread simulateThread = new Thread() {
         @Override
         public void run() {
            int i = 0;
            for (;; i++) {
               List<SkippedStatusRecord> skippedStatusRecord = skippedStatusTable.query(2);
               if (skippedStatusRecord != null && skippedStatusRecord.size() != 0) {
                  if (i % 2 == 0) {
                     statusCacheService.saveOrUpdateStatus(2, "ON");
                  } else {
                     statusCacheService.saveOrUpdateStatus(2, "OFF");
                  }
                  try {
                     sleep(10000);
                  } catch (InterruptedException e) {
                     e.printStackTrace();
                  }
               }
            }
         }
      };
      simulateThread.start();
   }
   
   private void simulateStatusCacheControlID3() {
      Thread simulateThread = new Thread() {
         @Override
         public void run() {
            int i = 0;
            for (;; i++) {
               try {
                  Thread.sleep(80000);
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
               if (i % 2 == 0) {
                  statusCacheService.saveOrUpdateStatus(3, "ON");
               } else {
                  statusCacheService.saveOrUpdateStatus(3, "OFF");
               }
               try {
                  sleep(10000);
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
            }
         }
      };
      simulateThread.start();
   }
   
   private void simulateStatusCacheControlID5() {
      Thread simulateThread = new Thread() {
         String[] imageNames = new String[] {"1.png","2.png","3.png","4.png","5.png"};
         @Override
         public void run() {
            int index = 0;
            while(true){
               String image = imageNames[index];
               statusCacheService.saveOrUpdateStatus(5, resourceBasePath+image);
               index = index<imageNames.length-1?++index:0;
               try {
                  sleep(1000);
               } catch (InterruptedException e) {
                  e.printStackTrace();
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
   }


}
