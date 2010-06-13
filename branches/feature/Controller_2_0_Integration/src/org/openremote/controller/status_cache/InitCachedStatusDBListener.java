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

   /* (non-Javadoc)
    * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
    */
   @Override
   public void contextInitialized(ServletContextEvent event) {
      try {
         /*ResourceBundle resourceBundle = ResourceBundle.getBundle("database-hsql");
         String dbDriver = resourceBundle.getString("hsql.dbDriver");
         String dbUrl = resourceBundle.getString("hsql.dbUrl");
         String dbUserName = resourceBundle.getString("hsql.dbUserName");
         String dbPassWord = resourceBundle.getString("hsql.dbPassWord");
         
         Class.forName(dbDriver);
         connection = DriverManager.getConnection(dbUrl, dbUserName, dbPassWord);
         Statement stmt = null;
         ResultSet rs = null;
         stmt = connection.createStatement();
         String create_controller_cached_controls_table_sql = resourceBundle.getString("hsql.create_controller_cached_controls_table_sql");
         String insert_data_sql = "insert into controller_cached_controls(id, control_id, current_status) values(1, 1, 'OFF'); insert into controller_cached_controls(id, control_id, current_status) values(2, 2, 'ON');";
         String query_data_sql = "select * from controller_cached_controls";
         stmt.executeUpdate(create_controller_cached_controls_table_sql);
         stmt.executeUpdate(insert_data_sql);
         rs = stmt.executeQuery(query_data_sql);
         while (rs.next()) {
            System.out.println(">>> " + rs.getString(1) + ", " + rs.getString(2) + ", " + rs.getString(3));
         }*/
         
         //The followings are simulated for status changed.
         System.out.println("Starting simulated change status tread at " + new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(new Date()));
         Thread thread = new Thread(){
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
         thread.start();
      } catch (Exception e) {
         e.printStackTrace();
      }
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

}
