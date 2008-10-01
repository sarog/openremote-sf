/*
* OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.output;

import org.jboss.logging.Logger;
import org.jboss.beans.metadata.api.annotations.Inject;
import org.jboss.beans.metadata.api.annotations.FromContext;
import org.jboss.kernel.spi.dependency.KernelControllerContext;
import org.openremote.controller.core.Bootstrap;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.net.URL;
import java.util.Enumeration;


/**
 *
 * @author <a href = "mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class SerialTransmitter
{


  // Constants ------------------------------------------------------------------------------------

  public final static String LOG_CATEGORY = "SERIAL TRANSMITTER";


  // Class Members --------------------------------------------------------------------------------


  private final static Logger log = Logger.getLogger(Bootstrap.ROOT_LOG_CATEGORY + "." + LOG_CATEGORY);

  static
  {
    try
    {
      String os = getOperatingSystem();

      if (os.toLowerCase().startsWith("windows"))
      {
        loadDLL();
      }

      else
      {
        log.error(
            "Your operating system is not currently recognized (your system reports your operating " +
            "system as '" + os + "'). The native libraries required for serial port communication " +
            "have not been loaded and this service is unlikely to operate correctly."
        );

        // TODO : service status
      }
    }
    catch (Throwable t)
    {
      log.error("Error initializing service: " + t, t);

      // TODO : service status
    }
  }


  private static void loadDLL()
  {

  }
  
  private static String getOperatingSystem()
  {
    return AccessController.doPrivileged(
        new PrivilegedAction<String>()
        {
          public String run()
          {
            return System.getProperty("os.name");
          }
        }
    );
  }


  // Instance Fields ------------------------------------------------------------------------------

  private KernelControllerContext serviceContext;


  // MC Component Methods -------------------------------------------------------------------------

  @Inject(fromContext = FromContext.CONTEXT)
  public void setServiceContext(KernelControllerContext ctx)
  {
    this.serviceContext = ctx;
  }

  public void start()
  {
    try
    {
      Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources("rxtxSerial.dll");

      while (resources.hasMoreElements() )
      {
        log.info(resources.nextElement());
      }
    }
    catch (Throwable t)
    {
      log.error(t, t);

    }
  }


}
