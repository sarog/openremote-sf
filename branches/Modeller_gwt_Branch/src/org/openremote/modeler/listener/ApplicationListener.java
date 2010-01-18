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
package org.openremote.modeler.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openremote.modeler.configuration.PathConfig;
import org.springframework.context.ApplicationEvent;


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
 * @author Tomsky 2010-1-18
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
      // set web root, eg: "E:\apache-tomcat-5.5.28\webapps\modeler\".
      PathConfig.WEBROOTPATH = event.getServletContext().getRealPath("/");
   }
}
