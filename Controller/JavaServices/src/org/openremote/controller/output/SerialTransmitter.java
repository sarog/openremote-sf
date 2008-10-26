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
import org.jboss.kernel.spi.dependency.KernelControllerContext;
import org.jboss.beans.metadata.api.annotations.Inject;
import org.jboss.beans.metadata.api.annotations.FromContext;
import org.jboss.beans.metadata.api.annotations.Start;
import org.openremote.controller.core.Bootstrap;

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


  // Instance Fields ------------------------------------------------------------------------------

  private KernelControllerContext serviceContext;

  private int ioDaemonPort = 9999;

  // MC Component Methods -------------------------------------------------------------------------

  @Inject(fromContext = FromContext.CONTEXT)
  public void setServiceContext(KernelControllerContext ctx)
  {
    this.serviceContext = ctx;
  }

  @Start
  public void start()
  {
    log.info("Starting Serial Transmitter...");
    
    // TODO: inject the name

    try
    {
      serviceContext.getKernel().getBus().invoke(
          "Output/IOProxy",
          "sendBytes",
          new Object[] { IOModule.PING, new byte[] {} },
          new String[] { IOModule.class.getName(), new byte[] {}.getClass().getName() }
      );
    }
    catch (Throwable t)
    {
      throw new Error(t);
    }
  }
}
