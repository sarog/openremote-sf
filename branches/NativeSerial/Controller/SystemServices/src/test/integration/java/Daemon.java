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

import java.io.File;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Helper class to start the native daemon.
 *
 * @author <a href = "mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class Daemon
{

  public final static String VERSION = "1.0.0";

  /**
   * Attempts to start the native I/O Daemon for testing.
   *
   */
  public static void start()
  {
    File workdir = new File(System.getProperty("user.dir"));

    File outputdir = new File(workdir, "output");

    if (!outputdir.exists())
    {
      System.out.println("***********************************************************************");
      System.out.println("");
      System.out.println("  Directory " + outputdir + " does not exist.");
      System.out.println("");
      System.out.println("  Make sure you've built the native executable first before running ");
      System.out.println("  these tests.");
      System.out.println("");
      System.out.println("***********************************************************************");

      System.exit(-1);
    }

    String osname = System.getProperty("os.name");

    File iodaemon = new File("");

    if (osname.startsWith("Linux"))
    {
      throw new Error("TODO: IMPLEMENT LINUX IODAEMON PATH");   // TODO
    }
    else if (osname.startsWith("Windows"))
    {
      iodaemon = new File(outputdir, "cygwin");
      iodaemon = new File(iodaemon, "iodaemon-" + VERSION + ".exe");
    }
    else if (osname.startsWith("Mac"))
    {
      throw new Error("TODO: IMPLEMENT MACOS IODAEMON PATH");   // TODO
    }

    if (!iodaemon.exists())
    {
      System.out.println("***********************************************************************");
      System.out.println("");
      System.out.println("  " + iodaemon.getAbsolutePath() + " does not exist.");
      System.out.println("");
      System.out.println("  Make sure you've built the native executable first before running ");
      System.out.println("  these tests.");
      System.out.println("");
      System.out.println("***********************************************************************");

      System.exit(-1);
    }
    
    ProcessBuilder builder = new ProcessBuilder(iodaemon.getAbsolutePath(), "--port=19999");

    try
    {
      Process process = builder.start();

      readInputStream(process.getInputStream());
      readErrorStream(process.getErrorStream());
    }
    catch (Exception e)
    {
      System.out.println("***********************************************************************");
      System.out.println("");
      System.out.println("  Cannot start I/O daemon!");
      System.out.println("");
      System.out.println("  " + e.toString());
      System.out.println("");
      System.out.println("***********************************************************************");
    }
  }

  private static void readInputStream(final InputStream input)
  {
    Runnable runnable = new Runnable()
    {
      public void run()
      {
        BufferedReader in = new BufferedReader(new InputStreamReader(input));

        boolean running = true;

        while (running)
        {
          try
          {
            String readLine;

            while ((readLine = in.readLine()) != null)
            {
              System.out.println("[I/O DAEMON] " + readLine);
            }
          }
          catch (Throwable t)
          {
            System.out.println("[I/O DAEMON] " + t.toString());
          }
        }
      }
    };

    Thread thread = new Thread(runnable);
    thread.setDaemon(true);

    thread.start();
  }

  private static void readErrorStream(final InputStream error)
  {
    Runnable runnable = new Runnable()
    {
      public void run()
      {
        BufferedReader in = new BufferedReader(new InputStreamReader(error));

        boolean running = true;
        
        while (running)
        {
          try
          {
            String readLine;

            while ((readLine = in.readLine()) != null)
            {
              System.out.println("[I/O DAEMON] " + readLine);
            }
          }
          catch (Throwable t)
          {
            System.out.println("[I/O DAEMON] " + t.toString());
          }
        }
      }
    };

    Thread thread = new Thread(runnable);
    thread.setDaemon(true);

    thread.start();
  }

}
