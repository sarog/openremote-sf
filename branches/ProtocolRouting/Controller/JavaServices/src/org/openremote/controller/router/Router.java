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
package org.openremote.controller.router;

import org.jboss.beans.metadata.api.annotations.FromContext;
import org.jboss.beans.metadata.api.annotations.Inject;
import org.jboss.kernel.spi.dependency.KernelControllerContext;
import org.jboss.logging.Logger;
import org.openremote.controller.core.Bootstrap;

/**
 * TODO: Routing of control protocol messages in the controller.
 * 
 * @author <a href="mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class Router
{

  // Constants ------------------------------------------------------------------------------------

  public final static String LOG_CATEGORY = "CONTROL PROTOCOL ROUTER";


  // Class Members --------------------------------------------------------------------------------

  private static Logger log = Logger.getLogger(Bootstrap.ROOT_LOG_CATEGORY + "." + LOG_CATEGORY);


  // Instance Fields ------------------------------------------------------------------------------

  private KernelControllerContext serviceContext = null;


  // MC Component Methods -------------------------------------------------------------------------

  @Inject(fromContext = FromContext.CONTEXT)
  public void setServiceContext(KernelControllerContext ctx)
  {
    this.serviceContext = ctx;
  }

  public void start()
  {
    log.info("Control Protocol Router initialized.");
  }


  // Public Instance Methods ----------------------------------------------------------------------

  public void route(String msg)
  {
    Message message = new Message(msg);

    if (message.isDeviceRegistrationMessage())
    {
      registerDevice(message);
    }

    // TODO : AddressTable component name should be injected

    try
    {
      String descriptorString =  (String)serviceContext.getKernel().getBus().invoke(
          "ControlProtocol/AddressTable",
          "lookup",
          new Object[] { message.getAddress() },
          new String[] { String.class.getName() }
      );

      // TODO : debug level logging

      log.info("Message address '" + message.getAddress() + "' translated to '" + descriptorString + "'.");

      Message deviceDescriptor = new Message(descriptorString);

      String invoker = deviceDescriptor.getComponentName();

      serviceContext.getKernel().getBus().invoke(
          invoker,
          "sendCommand",
          new Object[] { msg },
          new String[] { String.class.getName() }
      );
    }
    catch (Throwable t)
    {
      log.error("Error invoking AddressTable.lookup(): " + t);
    }
  }


  // Private Instance Methods ---------------------------------------------------------------------

  private void registerDevice(Message msg)
  {
    // TODO : AddressTable component name should be injected

    try
    {
      serviceContext.getKernel().getBus().invoke(
          "ControlProtocol/AddressTable",
          "addDevice",
          new Object[] { msg },
          new String[] { Message.class.getName() }
      );
    }
    catch (Throwable t)
    {
      log.error("Error invoking AddressTable.addDevice(): " + t);
    }
  }
}

