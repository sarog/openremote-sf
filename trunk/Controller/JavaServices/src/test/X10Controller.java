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
package test;

import x10.Controller;
import x10.UnitListener;
import x10.Command;
import x10.net.ControllerServer;

/**
 * Mock X10 controller.
 *
 * @author <a href="mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class X10Controller
{

  public static void main(String... args)
  {
    ControllerServer controllerServer = new ControllerServer(new MockController(), 9999);
    controllerServer.run();
  }

  private static class MockController implements Controller
  {

    public void addUnitListener(UnitListener unitListener)
    {
      //To change body of implemented methods use File | Settings | File Templates.
    }

    public void removeUnitListener(UnitListener unitListener)
    {
      //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addCommand(Command command)
    {
      System.out.println("COMMAND ADDED: " + command);
    }
  }
}
