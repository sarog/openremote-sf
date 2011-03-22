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
package org.openremote.controller.gateway;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.openremote.controller.exception.ControllerException;
import org.openremote.controller.service.GatewayManagerService;
import org.openremote.controller.spring.SpringContext;
import org.springframework.context.support.ApplicationObjectSupport;

/**
 * This class is mainly responsible for <b>Initializing</b> the cached statuses database where in the memory.
 * 
 * @author Rich Turner 2011-02-15
 */
public class InitConnectionManager extends ApplicationObjectSupport implements ServletContextListener {

   private GatewayManagerService gatewayManagerService = (GatewayManagerService)SpringContext.getInstance().getBean("gatewayManagerService");
   
   private Logger logger = Logger.getLogger(this.getClass().getName());
   
   /**
    * {@inheritDoc}
    */
   @Override
   public void contextInitialized(ServletContextEvent event) {
      try {
         gatewayManagerService.initGatewaysWithControllerXML(null);
      } catch (ControllerException e) {
         logger.error("Failed to init gateway manager with controller.xml ." + e.getMessage(), e);
      }
      try {
         gatewayManagerService.startGateways();
      } catch (ControllerException e) {
         logger.error("Failed to start gateways ." + e.getMessage(), e);
      }
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public void contextDestroyed(ServletContextEvent event) {
      gatewayManagerService.stopGateways();
   }
}
