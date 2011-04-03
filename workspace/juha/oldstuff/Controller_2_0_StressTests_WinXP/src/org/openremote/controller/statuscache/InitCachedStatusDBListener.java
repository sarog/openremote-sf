/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.openremote.controller.component.Sensor;
import org.openremote.controller.exception.ControllerException;
import org.openremote.controller.service.PollingMachinesService;
import org.openremote.controller.spring.SpringContext;
import org.springframework.context.support.ApplicationObjectSupport;

/**
 * This class is mainly responsible for <b>Initializing</b> the cached statuses database where in the memory.
 * 
 * @author Handy.Wang 2009-10-21
 */
public class InitCachedStatusDBListener extends ApplicationObjectSupport implements ServletContextListener {

   private PollingMachinesService pollingMachinesService = (PollingMachinesService)SpringContext.getInstance().getBean("pollingMachinesService");
   
   private Logger logger = Logger.getLogger(this.getClass().getName());
   
   /* (non-Javadoc)
    * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
    */
   @Override
   public void contextInitialized(ServletContextEvent event) {
      List<Sensor> sensors = new ArrayList<Sensor>();
      try {
         pollingMachinesService.initStatusCacheWithControllerXML(null, sensors);
      } catch (ControllerException e) {
         logger.error("Failed to init statusCache with controller.xml ." + e.getMessage(), e);
      }
      try {
         pollingMachinesService.startPollingMachineMultiThread(sensors);
      } catch (ControllerException e) {
         logger.error("Failed to start polling multiThread ." + e.getMessage(), e);
      }
   }
   
   /* (non-Javadoc)
    * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
    */
   @Override
   public void contextDestroyed(ServletContextEvent event) {
   }
}
