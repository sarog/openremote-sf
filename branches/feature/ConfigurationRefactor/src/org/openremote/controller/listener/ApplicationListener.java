/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.openremote.controller.net.IPAutoDiscoveryServer;
import org.openremote.controller.net.RoundRobinTCPServer;
import org.openremote.controller.net.RoundRobinUDPServer;
import org.springframework.context.ApplicationEvent;


/**
 * TODO
 *
 */
public class ApplicationListener implements ServletContextListener
{
   
  private static Logger logger = Logger.getLogger(ApplicationListener.class);

  /**
   * {@inheritDoc}
   */
  public void contextDestroyed(ServletContextEvent event)
  {

  }

  /**
   * {@inheritDoc}
   */
  public void contextInitialized(ServletContextEvent event)
  {
    new Thread(new IPAutoDiscoveryServer()).start();
    nap(10);
    new Thread(new RoundRobinUDPServer()).start();
    nap(10);
    new Thread(new RoundRobinTCPServer()).start();
    nap(10);
  }

  private void nap(long time)
  {
    try
    {
       Thread.sleep(time);
    }

    catch (InterruptedException e)
    {
       logger.error("InterruptedException", e);
    }
  }

}
