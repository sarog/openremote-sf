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
package org.openremote.controller.console;

import java.io.File;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jboss.kernel.Kernel;

/**
 * Sets up client command REST interface.   <p>
 *
 * The setup mainly involves looking up shared references the kernel has bound to JNDI and
 * making them available to servlet instance via servlet context.   <p>
 *
 * The variables bound to servlet context are:
 * <ul>
 *   <li>{@link #KERNEL_ATTRIBUTE KERNEL_ATTRIBUTE}</li>
 *   <li>{@link #SERVER_ROOT_ATTRIBUTE SERVER_ROOT_ATTRIBUTE}</li>
 *   <li>{@link #SERVER_DOWNLOADS_ATTRIBUTE SERVER_DOWNLOADS_ATTRIBUTE}</li>
 * </ul>
 *
 * Servlet context attributes SERVER_ROOT_ATTRIBUTE and SERVER_DOWNLOADS_ATTRIBUTE return type
 * java.io.File and reference the controller's root configuration directory and downloads
 * directory, respectively.  <p>
 *
 * Attribute KERNEL_ATTRIBUTE returns type org.jboss.kernel.Kernel which is a reference to
 * the underlying microkernel instance.
 *
 * @author <a href = "mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class HttpCommandStartup implements ServletContextListener
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * Servlet context attribute name for kernel reference. The returned attribute type is
   * org.jboss.kernel.Kernel.
   */
  public final static String KERNEL_ATTRIBUTE = "kernel";

  /**
   * Servlet context attribute name for controller's root configuration directory. The returned
   * attribute type is java.io.File.
   */
  public final static String SERVER_ROOT_ATTRIBUTE = "ServerRoot";

  /**
   * Servlet context attribute name for controller's downloads directory. The retruned attribute
   * type is java.io.File.
   */
  public final static String SERVER_DOWNLOADS_ATTRIBUTE = "Downloads";


  // Implements ServletContextListener ------------------------------------------------------------

  /**
   * Looks up a kernel reference and file locations from JNDI. The JNDI references
   * will be bound by controller's startup beans that must be deployed to the kernel
   * before this servlet. The variables for kernel, controller root configuration
   * directory and downloads directory will be bound to servlet's context. <p>
   *
   * @param event   see superclass
   */
  public void contextInitialized(ServletContextEvent event)
  {
    try
    {
      /*
       * TODO:
       *   - Should have common constants for variable lookup names
       *   - Logging
       */

      Context naming = new InitialContext();
      ServletContext ctx = event.getServletContext();

      Kernel kernel = (Kernel)naming.lookup("/kernel");
      ctx.setAttribute(KERNEL_ATTRIBUTE, kernel);

      File serverRootDirectory = (File)naming.lookup("/filesystem/root");
      File serverDownloadDirectory = (File)naming.lookup("/filesystem/downloads");

      ctx.setAttribute(SERVER_ROOT_ATTRIBUTE , serverRootDirectory);
      ctx.setAttribute(SERVER_DOWNLOADS_ATTRIBUTE, serverDownloadDirectory);

      try
      {
        // TODO : remove when JBAS-4310 is fixed (fixed with jboss-vfs Beta15)
        HttpCommand.workaround_JBAS_4310_for_MSWindows(kernel);
      }
      catch (Throwable t)
      {
        System.out.println(t);
      }

      System.out.println("Console command REST interface online.");
    }
    catch (NamingException e)
    {
      System.out.println(
          "Unable to retrieve reference from JNDI. Application initialization will be incomplete (" +
          e.toString() + ")."
      );
    }
  }

  public void contextDestroyed(ServletContextEvent event)
  {
    System.out.println("Client command REST interface is offline.");
  }

}

