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
package org.openremote.controller.bootstrap.servlet;

import java.util.logging.Logger;
import java.util.logging.LogManager;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;

import org.openremote.controller.bootstrap.Startup;
import org.openremote.controller.Constants;

/**
 * Part of a Controller's web application bootstrap for a servlet container.  <p>
 *
 * This is a servlet application context listener implementation which sets up java.util.logging
 * redirect to log4j implementation. It is separated out from the rest of the web app bootstrap
 * so that it can initiated early and therefore logging is redirected early, before other
 * components are initiated and start logging.   <p>
 *
 * This implementation is hooked to servlet lifecycle in the web.xml configuration file under
 * webapp's WEB-INF directory. It delegates to a generic
 * {@link org.openremote.controller.bootstrap.Startup} class which can be shared across all
 * different bootstrap implementations.
 *
 * @see org.openremote.controller.bootstrap.Startup
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class LogInitialization implements ServletContextListener
{

  /**
   * Initialize java.util.Logging redirector to Log4J implementation. Delegates to
   * {@link org.openremote.controller.bootstrap.Startup#redirectJULtoLog4j()} implementation
   * which is generic without relying on any specific bootstrap or container sevices.
   *
   * @see org.openremote.controller.bootstrap.Startup#redirectJULtoLog4j()
   * 
   * @param event   servlet context event provided by the container with access to the web
   *                application's environment
   */
  @Override public void contextInitialized(ServletContextEvent event)
  {
    // Strictly speaking Log4j is probably not necessary (JUL does the job just fine) but
    // currently a lot of the implementation and most likely many of the third party libraries
    // default to Log4j as the logging framework. Therefore default to redirect all the JUL
    // logging to Log4j for now.
    //
    // In general, org.openremote implementations should have compile-time dependency either
    // to java.util.logging API or org.openremote.util.Logger API (where latter is the preferred
    // option). This minimizes the third party API dependencies which helps in portability,
    // especially where it is prudent to try to minimize the runtime size (therefore using
    // JUL logging in class libraries instead of external Log4j implementation).
    //
    // For a standard servlet implementation (which this initialization is part of), the system
    // resource requirements are such that external Log4j library is probably manageable.
    // Eventually this dependency might be removed altogether, making this redirect unnecessary.

    Startup.redirectJULtoLog4j();


    // Programmatically disable log recording on parent log categories and their handlers. This
    // currently cannot be done through the Apache Tomcat's conf/logging.properties file (due
    // to how it is implemented) so doing it here -- since this can be somewhat specific to
    // Tomcat, currently not setting this in the generic Startup.redirectJULtoLog4j implementation.

    Startup.doNotDelegateControllerLogsToParentHandlers();
  }

  /**
   * Empty implementation
   *
   * @param event   servlet context event provided by the container with access to the web
   *                application's environment
   */
  @Override public void contextDestroyed(ServletContextEvent event)
  {
    // does nothing
  }

}

