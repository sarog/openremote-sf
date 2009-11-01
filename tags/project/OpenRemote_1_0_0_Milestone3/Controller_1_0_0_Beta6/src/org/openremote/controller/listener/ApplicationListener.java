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
package org.openremote.controller.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openremote.controller.net.IPAutoDiscoveryServer;


/**
 * The listener interface for receiving application events.
 * The class that is interested in processing a application
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addApplicationListener<code> method. When
 * the application event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see ApplicationEvent
 * @author Dan 2009-5-18
 */
public class ApplicationListener implements ServletContextListener {

   /**
    * {@inheritDoc}
    */
   public void contextDestroyed(ServletContextEvent event) {
      ;//do nothing
   }

   /**
    * {@inheritDoc}
    */
   public void contextInitialized(ServletContextEvent event) {
      new Thread(new IPAutoDiscoveryServer()).start(); 
   }

}
