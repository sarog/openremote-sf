/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.openremote.irbuilder.listener;

import org.openremote.irbuilder.exception.FileOperationException;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import java.util.Properties;
import java.io.*;

/**
 * Write website absolute path to directoryConfig.properties
 *  
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
public class ApplicationListener implements ServletContextListener {
   public void contextInitialized(ServletContextEvent servletContextEvent) {
      Properties properties = new Properties();

      properties.setProperty("web.dir",servletContextEvent.getServletContext().getRealPath("/"));
      properties.setProperty("tmp.dir",servletContextEvent.getServletContext().getRealPath("/")+"tmp/");

      File file = new File(getClass().getResource("/directoryConfig.properties").getFile());
      FileOutputStream outputStream = null;
      try {
         outputStream = new FileOutputStream(file);
         properties.store(outputStream,"init file");
      } catch (IOException e) {
         e.printStackTrace();
         throw new FileOperationException("write directoryConfig.properties fail.",e);
      } finally {
         try {
            outputStream.close();
         } catch (IOException e) {
            e.printStackTrace();
            throw new FileOperationException("close OutputStream of  directoryConfig.properties fail.",e);
         }
      }


   }

   public void contextDestroyed(ServletContextEvent servletContextEvent) {
      //do nothing
   }
}
