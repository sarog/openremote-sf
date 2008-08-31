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

import org.jboss.kernel.Kernel;
import org.jboss.kernel.spi.dependency.KernelControllerContext;
import org.jboss.beans.metadata.api.annotations.Inject;
import org.jboss.beans.metadata.api.annotations.FromContext;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <a href="mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class Router
{

  private KernelControllerContext serviceContext = null;


  @Inject(fromContext = FromContext.CONTEXT)
  public void setServiceContext(KernelControllerContext ctx)
  {
    this.serviceContext = ctx;
  }

  public void start()
  {
    System.out.println("Router initialized.");
  }


  public void route(String msg)
  {
    Message message = new Message(msg);

    if (message.isDeviceRegistrationMessage())
    {
      registerDevice(message);
    }

    /*
    Address destinationAddress = AddressTable.lookup(message.getAddress());

    message.setAddress(destinationAddress);
    */
    
    message.send();
  }


  // Private Instance Methods ---------------------------------------------------------------------

  private void registerDevice(Message msg)
  {
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
      System.out.println(t);
    }
  }
}

