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
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.openremote.controller.net.IPAutoDiscoveryServer;
import org.openremote.controller.net.RoundRobinTCPServer;
import org.openremote.controller.net.RoundRobinUDPServer;
import org.openremote.controller.service.ServiceContext;
import org.openremote.controller.service.PollingMachinesService;
import org.openremote.controller.exception.ControllerException;


/**
 * This'll serve us as our application initialization point as long as the controller is a
 * pure servlet web app. If earlier initialization points are required then they need to be
 * hooked into Tomcat's container specific service implementations or Tomcat needs to be
 * wrapped with a service interface and initialized and started programmatically
 * ("embedded tomcat"). For Android runtimes, some alternative lifecycle mechanisms needs to
 * be looked at. <p>
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

  @Override public void contextInitialized(ServletContextEvent event)
  {
    try
    {
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
      System.err.println(
          "\n\n=============================================================================\n\n" +

          " Application initialization failed: \n" +
          " " + t.getMessage()  +

          "\n\n=============================================================================\n\n" +

          "STACK TRACE:\n\n"
      );

      t.printStackTrace(System.err);
    }
  }


  @Override public void contextDestroyed(ServletContextEvent event)
  {
    // empty
  }


  // Private Methods ------------------------------------------------------------------------------


  private void initializeServiceContext(ServletContext ctx)
      throws ServletException, ClassNotFoundException, InstantiationException,
             IllegalAccessException, ExceptionInInitializerError, SecurityException
  {
    String serviceContextImplementationClass = ctx.getInitParameter(SERVICE_CONTEXT_IMPL_INIT_PARAM_NAME);

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

    // Execute ServiceContext constructor... It is assumed to automatically register itself as
    // the implementation in the ServiceContext.
    //
    // Not executing this in the privileged code segment... the class to load is parameterized
    // and could therefore be used to load any hostile class. Therefore if security manager is
    // present, it must be configured to grant privileges to particular service context class
    // implementation.

    serviceContextImplementationClass = serviceContextImplementationClass.trim();
    Class clazz = Thread.currentThread().getContextClassLoader().loadClass(serviceContextImplementationClass);
    clazz.newInstance();
  }


  private void initializeStateCache() throws ControllerException
  {
    PollingMachinesService devicePollingService = ServiceContext.getDevicePollingService();

    devicePollingService.initStatusCacheWithControllerXML(null);
    devicePollingService.startPollingMachineMultiThread();
  }

}
