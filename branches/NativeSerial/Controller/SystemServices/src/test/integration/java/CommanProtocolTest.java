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


import java.net.Socket;
import java.io.IOException;
import java.io.BufferedOutputStream;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;

/**
 * Integration tests to verify the correctness of the native I/O daemon control protocol.
 *
 * @author <a href = "mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class CommanProtocolTest {

  @BeforeClass(alwaysRun = true)
  public void init()
  {
    System.out.println("**** Starting daemon....");
    
    Daemon.start();

    try
    {
      Daemon.ping();
    }
    catch (Throwable t)
    {
      System.out.println("***********************************************************************");
      System.out.println("");
      System.out.println("  Cannot ping I/O daemon -- did it start or is it just very broken?");
      System.out.println("");
      System.out.println("  " + t.toString());
      System.out.println("");
      System.out.println("***********************************************************************");

      System.exit(-1);
    }
  }

  @Test public void unknownControlCommand() throws IOException
  {
    Daemon.sendBytes("DCONTROL0x00000007GARBAGE".getBytes());
  }


  @AfterClass(alwaysRun = true)
  public void destroy()
  {
    try
    {
      System.out.println("**** Stopping Daemon...\n");

      Daemon.stop();
    }
    catch (Throwable t)
    {
      System.out.println("**** Stopping failed - trying to kill...\n");

      Daemon.kill();
    }
  }
}