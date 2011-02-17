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

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.openremote.controller.net.IPAutoDiscoveryServer;
import org.openremote.controller.net.RoundRobinTCPServer;
import org.openremote.controller.net.RoundRobinUDPServer;
import org.openremote.controller.service.ServiceContext;
import org.openremote.controller.service.PollingMachinesService;
import org.openremote.controller.exception.ControllerException;
import org.openremote.controller.Constants;


/**
 * Controller application initialization point when it is hosted within a Java servlet container. <p>
 *
 * This'll serve us as our application initialization point as long as the controller is a
 * pure servlet web app. If earlier initialization points are required then they need to be
 * hooked into Tomcat's container specific service implementations or Tomcat needs to be
 * wrapped with a service interface and initialized and started programmatically
 * ("embedded tomcat"). <p>
 *
 * As this is part of the standard Java servlet functionality, it will also work with other
 * servlet-based containers (such as Jetty). For Android runtimes without fully-compliant servlet
 * engines, some alternative lifecycle mechanism needs to be used. <p>
 *
 * TODO : document the initialization sequence and functionality (logging, service context, etc). <p>
 *
 * This implementation is hooked to servlet lifecycle in the web.xml configuration file under
 * webapp's WEB-INF directory.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class ApplicationListener implements ServletContextListener
{

  /**
   * Defines the context parameter name that must be set in the web applications web.xml file
   * for the service context class initialization. This is a mandatory property that must be
   * present for the application to start.
   *
   * <pre>{@code
   *
   * <context-param>
   *   <param-name>ServiceContextImplementation</param-name>
   *     <param-value>foo.bar.ClassName</param-value>
   *  </context-param>
   *
   * }</pre>
   *
   * In the above, the class 'foo.bar.ClassName' must extend from the abstract base class
   * (@link org.openremote.controller.service.ServiceContext}. A default implementation for
   * Java SE runtime can be found in {@link org.openremote.controller.spring.SpringContext}.
   *
   * @see org.openremote.controller.service.ServiceContext
   * @see org.openremote.controller.spring.SpringContext
   */
  public final static String SERVICE_CONTEXT_IMPL_INIT_PARAM_NAME = "ServiceContextImplementation";



  // Implement ServletContextListener -------------------------------------------------------------


  /**
   * This is invoked by the servlet container after the application (web archive) is loaded but
   * before it starts servicing incoming HTTP requests.  <p>
   *
   * It is the earliest point where we can accomplish application initialization short of hooking
   * directly in the implementation details of particular servlet container implementations.
   *
   * @param event     servlet context event provided by the container with access to the web
   *                  application's environment
   */
  @Override public void contextInitialized(ServletContextEvent event)
  {
    try
    {

      // Initialize java.util.Logging redirector to Log4J implementation.
      //
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
      //
      //                                                                                      [JPL]

      initLog4jRedirect();


      // Initializes the service context for this runtime environment.
      //
      // Purpose of service context is to isolate the core of the implementation from compile-time
      // third-party library dependencies. These service implementations can be switched depending
      // on deployment environment and system resources available. By avoiding direct compile time
      // linking, it is easier to port the controller implementation to other environments. Service
      // dependencies are built at runtime through a service context implementation.
      //
      // The default service context in a servlet runtime is based on Spring library.

      initializeServiceContext(event.getServletContext());


      
      initializeStateCache();

      new Thread(new IPAutoDiscoveryServer()).start();

      Thread.sleep(10);

      new Thread(new RoundRobinUDPServer()).start();

      Thread.sleep(10);

      new Thread(new RoundRobinTCPServer()).start();

      Thread.sleep(10);
    }

    catch (Throwable t)
    {

      // In case any initialization fails, wrap a clear message to user who is deploying the
      // controller about the error. Propagating the exception up in the call stack, it is
      // ultimately up to the servlet container to handle and report the error to the user
      // in the most appropriate way.

      String msg =
          "\n\n=============================================================================\n\n" +

          " Application initialization failed: \n" +
          " " + t.getMessage()  +

          "\n\n=============================================================================\n\n";


      throw new Error(msg, t);
    }
  }


  /**
   * Empty implementation.
   *
   * @param event     servlet context event provided by the container with access to the web
   *                  application's environment
   */
  @Override public void contextDestroyed(ServletContextEvent event)
  {
    // empty
  }



  // Private Methods ------------------------------------------------------------------------------


  /**
   * Initializes a runtime service context for servlet container based deployments.  <p>
   *
   * The implementation can be configured in web application's web.xml file using the
   * context-param element. See {@link ApplicationListener#SERVICE_CONTEXT_IMPL_INIT_PARAM_NAME}
   * for the name of the parameter.
   *
   * @param ctx   web application context provided by the servlet container
   *
   * @throws ServletException  if the initialization fails
   */
  private void initializeServiceContext(ServletContext ctx) throws ServletException
  {

    String serviceContextImplementationClass =
        ctx.getInitParameter(SERVICE_CONTEXT_IMPL_INIT_PARAM_NAME);

    // Check that configuration is present...

    if (serviceContextImplementationClass == null || serviceContextImplementationClass.equals(""))
    {
      throw new ServletException(
          SERVICE_CONTEXT_IMPL_INIT_PARAM_NAME +
          " initialization parameter in web.xml is missing or empty.\n" +
          " Cannot instantiate controller's service context.\n\n" +

          " Please make sure the following entry is in WEB-INF/web.xml: \n\n" +

          "  <context-param>\n" +
          "    <param-name>ServiceContextImplementation</param-name>\n" +
          "    <param-value>foo.bar.ClassName \n" +
          "       [implements: org.openremote.controller.service.ServiceContext] \n" +
          "       [default: 'org.openremote.controller.service.spring.SpringContext']\n" +
          "    </param-value>\n" +
          "  </context-param>\n\n"
      );
    }

    try
    {
      // Execute ServiceContext constructor... It is assumed to automatically register itself as
      // the implementation in the ServiceContext.
      //
      // Not executing this in the privileged code segment... the class to load is parameterized
      // and could therefore be used to load any hostile class. Therefore if security manager is
      // present, it must be configured to grant privileges to particular service context class
      // implementation.

      serviceContextImplementationClass = serviceContextImplementationClass.trim();
      Class clazz = Thread.currentThread().getContextClassLoader()
          .loadClass(serviceContextImplementationClass);
      clazz.newInstance();
    }

    catch (SecurityException exception)
    {
      throw new ServletException(
          "Could not instantiate a service context implementation ('" +
          serviceContextImplementationClass + "') due to security restriction. " +
          "If security manager has been configured, it may deny access to class " +
          "instantion: " + exception.getMessage(), exception
      );
    }

    catch (ExceptionInInitializerError error)
    {
      Throwable cause = error.getCause();

      String causeMsg = "";

      if (cause != null)
        causeMsg = cause.getMessage();

      throw new ServletException(
          "Unable to instantiate service context implementation '" +
          serviceContextImplementationClass + "', error has occured in " +
          "static initializer block (" + cause.getClass().getSimpleName() +
          " : " + causeMsg + ")", error
      );
    }

    catch (IllegalAccessException exception)
    {
      throw new ServletException(
          "Cannot instantiate service context class '" + serviceContextImplementationClass +
          "', can't access a public constructor: " + exception.getMessage(), exception
      );
    }

    catch (InstantiationException exception)
    {
      throw new ServletException(
          "Cannot instantiate service context class '" + serviceContextImplementationClass +
          "': " + exception.getMessage(), exception
      );
    }

    catch (ClassNotFoundException exception)
    {
      throw new ServletException(
          "The configured service context class '" + serviceContextImplementationClass +
          "' was not found.", exception
      );
    }
  }


  /**
   * TODO
   *
   * @throws ControllerException
   */
  private void initializeStateCache() throws ControllerException
  {
    PollingMachinesService devicePollingService = ServiceContext.getDevicePollingService();

    devicePollingService.initStatusCacheWithControllerXML(null);
    devicePollingService.startPollingMachineMultiThread();
  }


  /**
   * Configure all logging categories under {@link Constants#CONTROLLER_ROOT_LOG_CATEGORY} to
   * redirect from java.util.logging to log4j logging.
   */
  private void initLog4jRedirect()
  {
    Logger controllerRootLogger = Logger.getLogger(Constants.CONTROLLER_ROOT_LOG_CATEGORY);

    controllerRootLogger.addHandler(new Log4jRedirect());
    controllerRootLogger.setLevel(Level.ALL);

    controllerRootLogger.info("Initialized JUL to LOG4J Redirector.");
  }

  
  // Nested Classes -------------------------------------------------------------------------------


  /**
   * Java util logging handler implementation to map JUL log records to log4j API and send
   * log messages to log4j. <p>
   *
   * TODO : not handling parameterized log messages currently
   */
  private static class Log4jRedirect extends Handler
  {

    /**
     * Translates and sends JUL log records to log4j logging.
     *
     * @param logRecord   java.util.logging log record
     */
    @Override public void publish(LogRecord logRecord)
    {

      // extract the level, message, log category and exception from log record...

      Level level = logRecord.getLevel();
      String msg  = logRecord.getMessage();
      String category = logRecord.getLoggerName();
      Throwable thrown = logRecord.getThrown();

      // translate to log4j log category (1 to 1 name mapping)...

      org.apache.log4j.Logger log4j = org.apache.log4j.Logger.getLogger(category);

      // mapping from JUL level to log4j levels...

      org.apache.log4j.Level log4jLevel = mapToLog4jLevel(level);

      // and log...

      if (thrown != null)
      {
        if (log4j.isEnabledFor(log4jLevel))
          log4j.log(log4jLevel, msg, thrown);
      }

      else
      {
        if (log4j.isEnabledFor(log4jLevel))
          log4j.log(log4jLevel, msg);
      }
    }

    /**
     * Does nothing -- Log4j API does not provide explicit flushing to appenders.
     */
    @Override public void flush()
    {
      // no-op
    }

    /**
     * Does nothing -- Log4j API does not provide an explicit close operation for appenders.
     */
    @Override public void close()
    {
      // no-op
    }



    private static boolean systemWarningOnTraceLevelMapping = false;
    private static boolean systemWarningOnUnmappedLevel = false;

    private org.apache.log4j.Level mapToLog4jLevel(Level level)
    {
      if (level == Level.OFF)
      {
        return org.apache.log4j.Level.OFF;
      }

      else if (level == Level.SEVERE)
      {
        return org.apache.log4j.Level.ERROR;
      }

      else if (level == Level.WARNING)
      {
        return org.apache.log4j.Level.WARN;
      }

      else if (level == Level.INFO)
      {
        return org.apache.log4j.Level.INFO;
      }

      else if (level == Level.FINE)
      {
        return org.apache.log4j.Level.DEBUG;
      }

      else if (level == Level.FINER || level == Level.FINEST)
      {
        // TODO :
        //   update to log4j 1.2.12 to get TRACE level out-of-the-box, not hacking it in as
        //   a custom level for now, we're not tracing anywhere yet
        //                                                                            [JPL]
        //
        //log4jLevel = org.apache.log4j.Level.TRACE;

        if (!systemWarningOnTraceLevelMapping)
        {
          System.err.println(
              "\n\n" +
              "-----------------------------------------------------------------------------" +
              "\n\n" +

              "  System is using TRACE level logging which has not yet been mapped. \n" +
              "  Defaulting to DEBUG level." +

              "\n\n" +
              "-----------------------------------------------------------------------------" +
              "\n\n"
          );

          systemWarningOnTraceLevelMapping = true;
        }

        return org.apache.log4j.Level.DEBUG;
      }

      else if (level == Level.ALL)
      {
        return org.apache.log4j.Level.ALL;
      }

      else
      {
        if (!systemWarningOnUnmappedLevel)
        {
          System.err.println(
              "\n\n" +
              "-----------------------------------------------------------------------------" +
              "\n\n" +

              "  System is using custom log level (" + level.getName() + ")which has no \n" +
              "  defined mapping. Defaulting to INFO level." +

              "\n\n" +
              "-----------------------------------------------------------------------------" +
              "\n\n"
          );

          systemWarningOnUnmappedLevel = true;
        }

        return org.apache.log4j.Level.INFO;
      }
    }
  }
}
