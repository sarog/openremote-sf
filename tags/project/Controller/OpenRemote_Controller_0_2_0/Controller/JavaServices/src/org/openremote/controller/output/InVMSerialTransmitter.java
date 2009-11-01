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
import org.jboss.beans.metadata.api.annotations.Start;
import org.jboss.beans.metadata.api.annotations.Stop;
import org.jboss.kernel.spi.dependency.KernelControllerContext;
import org.openremote.controller.core.Bootstrap;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.net.URL;
import java.util.Enumeration;
import java.io.IOException;
import java.io.BufferedOutputStream;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;
import gnu.io.PortInUseException;


/**
 *
 * @author <a href = "mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class InVMSerialTransmitter
{


  // Constants ------------------------------------------------------------------------------------

  public final static String LOG_CATEGORY = "SERIAL TRANSMITTER";


  // Class Members --------------------------------------------------------------------------------


  private final static Logger log = Logger.getLogger(Bootstrap.ROOT_LOG_CATEGORY + "." + LOG_CATEGORY);


  // Instance Fields ------------------------------------------------------------------------------

  private KernelControllerContext serviceContext;

  private String commPort = "/dev/ttyS0";

  private SerialPort serial;

  private BufferedOutputStream out;



  // MC Component Methods -------------------------------------------------------------------------

  @Inject(fromContext = FromContext.CONTEXT)
  public void setServiceContext(KernelControllerContext ctx)
  {
    this.serviceContext = ctx;
  }

  @Start public void start()
  {

    loadNativeSerialLib();

    try
    {
      CommPortIdentifier commPortId = CommPortIdentifier.getPortIdentifier(commPort);

      // TODO : This is blocking... best to do in another thread to keep deployments rolling...

      serial = (SerialPort)commPortId.open("OpenRemote Serial Transmitter", 2000);

      out = new BufferedOutputStream(serial.getOutputStream());
    }
    catch (NoSuchPortException e)
    {
      throw new Error(e);   // TODO
    }
    catch (PortInUseException e)
    {
      throw new Error(e);   // TODO
    }
    catch (IOException e)
    {
      throw new Error(e);   // TODO
    }
    finally
    {
    }
  }

  @Stop public void stop()
  {
    if (out != null)
    {
      try
      {
        out.close();
      }
      catch (IOException e)
      {
        log.debug("Error closing IO stream: " + e, e);
      }
    }

    if (serial != null)
      serial.close();    
  }


  // JavaBean Properties --------------------------------------------------------------------------

  public void setCommPort(String commPort)
  {
    this.commPort = commPort;
  }

  public String getCommPort()
  {
    return commPort;
  }
  

  // Private Instance Methods ---------------------------------------------------------------------

  private void loadNativeSerialLib()
  {
    String os = getOperatingSystem();

    if (os.toLowerCase().startsWith("windows"))
    {
      loadWindowsDLL();
    }
/*
    else if (os.toLowerCase().startsWith("linux"))
    {
      loadLinuxSO();
    }
*/
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

  
  private void loadWindowsDLL()
  {
    // TODO : this is debug level logging...

    log.info("Searching for rxtxSerial.dll");

    final URL url = Thread.currentThread().getContextClassLoader().getResource("rxtxSerial.dll");

    if (url == null)
      throw new Error("could not load rxtxSerial.dll");   // TODO

    AccessController.doPrivileged(
        new PrivilegedAction<Void>()
        {
          public Void run()
          {
            try
            {
              Runtime.getRuntime().load(url.getPath());

              return null;
            }
            catch (SecurityException e)
            {
              throw new Error(e);   // TODO
            }
            catch (UnsatisfiedLinkError e)
            {
              throw new Error("DLL not found: " + e);   // TODO
            }
          }
        }
    );

    log.info("Loaded " + url);      // TODO : debug log
  }


  private void loadLinuxSO()
  {

  }


  private String getOperatingSystem()
  {
    return AccessController.doPrivileged(
        new PrivilegedAction<String>()
        {
          public String run()
          {
            try
            {
              return System.getProperty("os.name");
            }
            catch (SecurityException e)
            {
              throw new Error(e);   // TODO
            }
          }
        }
    );
  }

}
