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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <a href="mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class Router
{

  private Kernel kernel = null;


  public void setKernel(Kernel kernel)
  {
    this.kernel = kernel;

    System.out.println("Router initialized.");
  }

  public void route(String msg)
  {
      Message message = new Message(msg);                                   // (2)

//      Address destinationAddress = AddressTable.lookup(message.getAddress());           // (3)

//      message.setAddress(destinationAddress);

      message.send();                                                                   // (5)
  }

  /*
  public Message translate(String id, Object msg)
  {
    try
    {
      String message = (String)kernel.getBus().invoke(
          id,
          "translateMessage",
          new Object[] { msg },
          new String[] { Object.class.getName() }
      );

      return new Message(message);
    }
    catch (Throwable t)
    {
      throw new Error(t);
    }
  }
  */

}

